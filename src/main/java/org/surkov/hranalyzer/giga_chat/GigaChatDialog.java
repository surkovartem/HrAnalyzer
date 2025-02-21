package org.surkov.hranalyzer.giga_chat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.surkov.hranalyzer.giga_chat.config.GigaChatConfig;
import org.surkov.hranalyzer.giga_chat.dto.GigaChatMessage;
import org.surkov.hranalyzer.giga_chat.dto.GigaChatRequest;
import org.surkov.hranalyzer.giga_chat.exception.AuthenticationException;
import org.surkov.hranalyzer.giga_chat.exception.ApiRequestException;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.Base64;

/**
 * Компонент для взаимодействия с GigaChat API.
 * Отвечает за аутентификацию и выполнение запросов к API.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GigaChatDialog {

    private final GigaChatConfig config;
    private final OkHttpClient client;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final AtomicReference<String> accessToken = new AtomicReference<>("");
    private final AtomicLong tokenExpirationTime = new AtomicLong(0);

    /**
     * Инициализация компонента.
     * Проверяет конфигурацию и получает начальный токен доступа.
     *
     * @throws IllegalStateException если конфигурация некорректна
     */
    @PostConstruct
    public void init() {
        validateConfig();
        try {
            fetchAccessToken();
        } catch (Exception e) {
            log.error("Failed to initialize GigaChatDialog", e);
            throw new IllegalStateException("Failed to initialize GigaChatDialog", e);
        }
    }

    private void validateConfig() {
        if (!config.getAuthUrl().startsWith("https")) {
            throw new IllegalStateException("AUTH_URL must use HTTPS");
        }
    }

    /**
     * Получает токен доступа от GigaChat API.
     *
     * @throws AuthenticationException если не удалось получить токен
     */
    @Retryable(value = AuthenticationException.class, maxAttempts = 3, backoff = @Backoff(delay = 1000, multiplier = 2))
    public synchronized void fetchAccessToken() {
        try {
            String authKey = Base64.getEncoder().encodeToString(
                    (config.getClientId() + ":" + config.getClientSecret()).getBytes(StandardCharsets.UTF_8)
            );

            RequestBody formBody = new FormBody.Builder()
                    .add("scope", "GIGACHAT_API_PERS")
                    .build();

            Request request = new Request.Builder()
                    .url(config.getAuthUrl())
                    .post(formBody)
                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                    .addHeader("Accept", "application/json")
                    .addHeader("RqUID", UUID.randomUUID().toString())
                    .addHeader("Authorization", "Basic " + authKey)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    String responseBody = response.body() != null ? response.body().string() : "No response body";
                    log.error("Failed to retrieve access token. Response code: {}, body: {}", response.code(), maskSensitiveData(responseBody));
                    throw new AuthenticationException("Failed to retrieve access token. Response code: " + response.code());
                }

                String responseBody = response.body().string();
                JsonNode root = objectMapper.readTree(responseBody);
                if (!root.has("access_token") || !root.has("expires_at")) {
                    log.error("Invalid token response: missing access_token or expires_at");
                    throw new AuthenticationException("Invalid token response");
                }

                accessToken.set(root.get("access_token").asText());
                tokenExpirationTime.set(root.get("expires_at").asLong() * 1000L);
                log.info("Access token retrieved successfully, expires at: {}", tokenExpirationTime.get());
            }
        } catch (Exception e) {
            log.error("Error fetching access token", e);
            throw new AuthenticationException("Error fetching access token", e);
        }
    }

    /**
     * Выполняет запрос к GigaChat API.
     *
     * @param message сообщение для отправки
     * @return ответ от API
     * @throws ApiRequestException если запрос не удался
     */
    public String getResponse(String message) {
        refreshTokenIfNeeded();
        try {
            GigaChatRequest payload = new GigaChatRequest();
            payload.setModel("GigaChat");
            GigaChatMessage userMessage = new GigaChatMessage();
            userMessage.setRole("user");
            userMessage.setContent(message);
            payload.setMessages(Collections.singletonList(userMessage));
            payload.setStream(false);

            String jsonPayload = objectMapper.writeValueAsString(payload);
            RequestBody body = RequestBody.create(jsonPayload, MediaType.get("application/json; charset=utf-8"));

            Request request = new Request.Builder()
                    .url(config.getApiUrl())
                    .post(body)
                    .addHeader("Authorization", "Bearer " + accessToken.get())
                    .addHeader("Content-Type", "application/json")
                    .addHeader("RqUID", UUID.randomUUID().toString())
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    String responseBody = response.body() != null ? response.body().string() : "No response body";
                    log.error("GigaChat API request failed. Response code: {}, body: {}", response.code(), maskSensitiveData(responseBody));
                    throw new ApiRequestException("GigaChat API request failed. Response code: " + response.code());
                }
                return response.body().string();
            }
        } catch (Exception e) {
            log.error("Error processing GigaChat API request", e);
            throw new ApiRequestException("Error processing GigaChat API request", e);
        }
    }

    private void refreshTokenIfNeeded() {
        if (System.currentTimeMillis() >= tokenExpirationTime.get() - 60_000) {
            log.info("Access token expired or about to expire. Refreshing...");
            fetchAccessToken();
        }
    }

    private String maskSensitiveData(String data) {
        if (data == null || data.length() < 4) {
            return "****";
        }
        return data.substring(0, 4) + "****" + data.substring(data.length() - 4);
    }
}