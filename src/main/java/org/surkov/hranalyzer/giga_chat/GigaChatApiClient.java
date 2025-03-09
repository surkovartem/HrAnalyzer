package org.surkov.hranalyzer.giga_chat;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.springframework.stereotype.Component;
import org.surkov.hranalyzer.giga_chat.config.GigaChatConfig;
import org.surkov.hranalyzer.giga_chat.dto.GigaChatMessage;
import org.surkov.hranalyzer.giga_chat.dto.GigaChatRequest;
import org.surkov.hranalyzer.giga_chat.exception.ApiRequestException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Клиент для выполнения запросов к GigaChat API.
 * Отвечает за отправку сообщений в API и получение ответов.
 *
 * @author surkov
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GigaChatApiClient {

    /**
     * Конфигурация GigaChat API, содержащая URL и другие параметры.
     */
    private final GigaChatConfig config;

    /**
     * Менеджер токенов, предоставляющий токен доступа для аутентификации в API.
     */
    private final TokenManager tokenManager;

    /**
     * Обертка над HTTP-клиентом для выполнения запросов к API.
     */
    private final HttpClientWrapper httpClientWrapper;

    /**
     * Объект для сериализации и десериализации JSON.
     */
    private final ObjectMapper objectMapper;

    /**
     * Выполняет запрос к GigaChat API
     * для получения ответа на основе переданных данных.
     *
     * @param systemPrompt Системный промпт, задающий контекст для анализа.
     * @param text         Текст резюме для анализа.
     * @param model    Модель для анализа резюме.
     * @return Ответ от API в виде строки.
     * @throws ApiRequestException ошибка при выполнении запроса к API.
     */
    public String getResponse(
            final String systemPrompt,
            final String text,
            final String model
    ) {
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
            payload.setModel(model);
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
                    .addHeader(
                            "Authorization",
                            "Bearer " + tokenManager.getAccessToken()
                    )
                    .addHeader(
                            "Content-Type",
                            "application/json"
                    )
                    .addHeader(
                            "RqUID",
                            UUID.randomUUID().toString()
                    )
                    .build();

            return httpClientWrapper.executeRequestForString(
                    request,
                    "Ошибка запроса к GigaChat API"
            );
        } catch (Exception e) {
            log.error("Ошибка при обработке запроса к GigaChat API", e);
            throw new ApiRequestException(
                    "Ошибка при обработке запроса к GigaChat API", e
            );
        }
    }
}
