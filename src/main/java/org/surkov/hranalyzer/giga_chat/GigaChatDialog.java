package org.surkov.hranalyzer.giga_chat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.surkov.hranalyzer.giga_chat.config.GigaChatConfig;
import org.surkov.hranalyzer.giga_chat.dto.GigaChatMessage;
import org.surkov.hranalyzer.giga_chat.dto.GigaChatRequest;
import org.surkov.hranalyzer.giga_chat.exception.ApiRequestException;
import org.surkov.hranalyzer.giga_chat.exception.AuthenticationException;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Компонент для взаимодействия с GigaChat API.
 * Отвечает за аутентификацию и выполнение запросов к API.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GigaChatDialog {

    /**
     * Конфигурация для подключения к GigaChat API.
     */
    private final GigaChatConfig config;

    /**
     * HTTP-клиент для выполнения запросов к API.
     */
    private final OkHttpClient client;

    /**
     * Объект для сериализации и десериализации JSON.
     */
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Токен доступа для аутентификации в API.
     * Хранится в атомарной ссылке для обеспечения потокобезопасности.
     */
    private final AtomicReference<String> accessToken = new AtomicReference<>("");

    /**
     * Время истечения срока действия токена доступа (в миллисекундах).
     * Хранится в атомарном объекте для обеспечения потокобезопасности.
     */
    private final AtomicLong tokenExpirationTime =
            new AtomicLong(0);

    /**
     * Запас времени (в миллисекундах) перед истечением
     * срока действия токена, чтобы обновить его заранее.
     */
    private static final int TOKEN_REFRESH_BUFFER_MS = 60_000;

    /**
     * Начальная задержка перед повторной
     * попыткой получения токена.
     */
    private static final int RETRY_INITIAL_DELAY_MS = 1000;

    /**
     * Множитель для экспоненциального увеличения
     * задержки между повторными попытками.
     */
    private static final int RETRY_DELAY_MULTIPLIER = 2;

    /**
     * Количество символов, остающихся видимыми
     * в начале и конце строки при маскировке.
     */
    private static final int MASK_VISIBLE_CHARS = 4;

    /**
     * Множитель для перевода секунд в миллисекунды.
     */
    private static final int SECONDS_TO_MILLISECONDS = 1000;

    /**
     * Инициализация компонента.
     * Проверяет конфигурацию и получает начальный токен доступа.
     *
     * @throws IllegalStateException если конфигурация некорректна или
     *                               не удалось получить токен доступа
     */
    @PostConstruct
    public void init() {
        validateConfig();
        try {
            fetchAccessToken();
        } catch (Exception e) {
            log.error("Не удалось инициализировать GigaChatDialog", e);
            throw new IllegalStateException("Не удалось инициализировать GigaChatDialog", e);
        }
    }

    /**
     * Проверяет корректность конфигурации.
     *
     * @throws IllegalStateException если URL аутентификации не использует HTTPS
     */
    private void validateConfig() {
        if (!config.getAuthUrl().startsWith("https")) {
            throw new IllegalStateException("AUTH_URL должен использовать HTTPS");
        }
    }

    /**
     * Получает токен доступа от GigaChat API.
     * Метод помечен как synchronized для предотвращения одновременного
     * обновления токена несколькими потоками.
     *
     * @throws AuthenticationException если не удалось получить токен
     */
    @Retryable(
            retryFor = AuthenticationException.class,
            backoff = @Backoff(
                    delay = RETRY_INITIAL_DELAY_MS,
                    multiplier = RETRY_DELAY_MULTIPLIER
            )
    )
    public synchronized void fetchAccessToken() {
        try {
            String credentials = config.getClientId() + ":" + config.getClientSecret();
            String authKey = Base64.getEncoder().encodeToString(
                    credentials.getBytes(StandardCharsets.UTF_8)
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
                    String responseBody = response.body() != null ? response.body().string() : "Тело ответа отсутствует";
                    log.error(
                            "Не удалось получить токен доступа. Код ответа: {}, тело: {}",
                            response.code(),
                            maskSensitiveData(responseBody)
                    );
                    throw new AuthenticationException("Не удалось получить токен доступа. Код ответа: " + response.code());
                }

                String responseBody = response.body() != null ? response.body().string() : null;
                if (responseBody == null) {
                    log.error("Тело ответа отсутствует при получении токена доступа");
                    throw new AuthenticationException("Тело ответа отсутствует при получении токена доступа");
                }
                JsonNode root = objectMapper.readTree(responseBody);

                if (!root.has("access_token") || !root.has("expires_at")) {
                    log.error("Некорректный ответ токена: отсутствует access_token или expires_at");
                    throw new AuthenticationException("Некорректный ответ токена");
                }

                accessToken.set(root.get("access_token").asText());
                // Время истечения токена (сек) из ответа API
                long expiresAtSec = root.get("expires_at").asLong();
                // Время истечения токена в мс
                long expiresAtMs = expiresAtSec * SECONDS_TO_MILLISECONDS;
                tokenExpirationTime.set(expiresAtMs);
                log.info(
                        "Токен доступа успешно получен, истекает: {}",
                        tokenExpirationTime.get()
                );
            }
        } catch (Exception e) {
            log.error("Ошибка при получении токена доступа", e);
            throw new AuthenticationException("Ошибка при получении токена доступа", e);
        }
    }

    /**
     * Выполняет запрос к GigaChat API.
     *
     * @param systemPrompt Системный промпт для анализа резюме.
     * @param text         Текст резюме.
     * @param gigaModel    Модель для анализа резюме.
     * @return Ответ от API в виде строки.
     * @throws ApiRequestException если запрос не удался
     */
    public String getResponse(
            final String systemPrompt,
            final String text,
            final String gigaModel
    ) {
        refreshTokenIfNeeded();
        try {
            GigaChatMessage systemMessage = new GigaChatMessage();
            systemMessage.setRole("system");
            systemMessage.setContent(systemPrompt);

            GigaChatMessage userMessage = new GigaChatMessage();
            userMessage.setRole("user");
            userMessage.setContent(text);

            List<GigaChatMessage> messages = new ArrayList<>();
            messages.add(systemMessage);
            messages.add(userMessage);

            GigaChatRequest payload = new GigaChatRequest();
            payload.setModel(gigaModel);
            payload.setMessages(messages);
            payload.setStream(false);

            String jsonPayload = objectMapper.writeValueAsString(payload);
            RequestBody body = RequestBody.create(
                    jsonPayload,
                    MediaType.get("application/json; charset=utf-8")
            );

            Request request = new Request.Builder()
                    .url(config.getApiUrl())
                    .post(body)
                    .addHeader("Authorization", "Bearer " + accessToken.get())
                    .addHeader("Content-Type", "application/json")
                    .addHeader("RqUID", UUID.randomUUID().toString())
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    String responseBody = response.body() != null
                            ? response.body().string()
                            : "Тело ответа отсутствует";
                    log.error(
                            "Ошибка запроса к GigaChat API. Код ответа: {}, тело: {}",
                            response.code(),
                            maskSensitiveData(responseBody)
                    );
                    throw new ApiRequestException(
                            "Ошибка запроса к GigaChat API. Код ответа: " + response.code()
                    );
                }

                String responseBody = response.body() != null ? response.body().string() : null;
                if (responseBody == null) {
                    log.error("Тело ответа отсутствует при выполнении запроса к GigaChat API");
                    throw new ApiRequestException(
                            "Тело ответа отсутствует при выполнении запроса к GigaChat API"
                    );
                }
                return responseBody;
            }
        } catch (Exception e) {
            log.error("Ошибка при обработке запроса к GigaChat API", e);
            throw new ApiRequestException("Ошибка при обработке запроса к GigaChat API", e);
        }
    }

    /**
     * Обновляет токен доступа, если он истек или скоро истечет.
     * Проверяет, не истек ли срок действия токена, с учетом запаса времени.
     */
    private void refreshTokenIfNeeded() {
        // Время, после которого токен считается устаревшим, с учетом запаса
        long tokenExpirationThreshold = tokenExpirationTime.get() - TOKEN_REFRESH_BUFFER_MS;
        if (System.currentTimeMillis() >= tokenExpirationThreshold) {
            log.info("Токен доступа истек или скоро истечет. Обновление...");
            fetchAccessToken();
        }
    }

    /**
     * Маскирует конфиденциальные данные в строке, оставляя видимыми только
     * первые и последние символы.
     *
     * @param data Строка с данными для маскировки.
     * @return Замаскированная строка или "****", если строка слишком короткая.
     */
    private String maskSensitiveData(final String data) {
        if (data == null || data.length() <= MASK_VISIBLE_CHARS * 2) {
            return "****";
        }

        String start = data.substring(0, MASK_VISIBLE_CHARS);
        String end = data.substring(data.length() - MASK_VISIBLE_CHARS);
        return start + "****" + end;
    }
}
