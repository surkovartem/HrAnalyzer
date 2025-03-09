package org.surkov.hranalyzer.giga_chat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Component;
import org.surkov.hranalyzer.giga_chat.exception.ApiRequestException;
import org.surkov.hranalyzer.giga_chat.exception.AuthenticationException;
import org.surkov.hranalyzer.giga_chat.utils.SecurityUtils;

import java.io.IOException;

/**
 * Обертка над HTTP-клиентом для выполнения запросов к API.
 * Предоставляет методы для выполнения запросов и обработки ответов, включая обработку ошибок.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class HttpClientWrapper {

    /**
     * HTTP-клиент для выполнения запросов к API.
     */
    private final OkHttpClient client;

    /**
     * Объект для сериализации и десериализации JSON.
     */
    private final ObjectMapper objectMapper;

    /**
     * Выполняет HTTP-запрос и возвращает тело ответа в виде JSON-объекта.
     *
     * @param request      HTTP-запрос, который необходимо выполнить.
     * @param errorMessage Сообщение об ошибке для логирования и исключения.
     * @return JSON-объект, представляющий тело ответа.
     * @throws ApiRequestException     запрос завершился с ошибкой или произошла ошибка ввода-вывода.
     * @throws AuthenticationException ошибка связана с аутентификацией (коды ответа 401 или 403).
     */
    public JsonNode executeRequest(final Request request, final String errorMessage) {
        try (Response response = client.newCall(request).execute()) {
            String responseBody = handleResponse(response, errorMessage);
            return objectMapper.readTree(responseBody);
        } catch (IOException e) {
            log.error("{} Ошибка при выполнении запроса", errorMessage, e);
            throw new ApiRequestException(errorMessage + ". Ошибка при выполнении запроса", e);
        }
    }

    /**
     * Выполняет HTTP-запрос и возвращает тело ответа в виде строки.
     *
     * @param request      HTTP-запрос, который необходимо выполнить.
     * @param errorMessage Сообщение об ошибке для логирования и исключения.
     * @return Тело ответа в виде строки.
     * @throws ApiRequestException запрос завершился с ошибкой или произошла ошибка ввода-вывода.
     */
    public String executeRequestForString(final Request request, final String errorMessage) {
        try (Response response = client.newCall(request).execute()) {
            return handleResponse(response, errorMessage);
        } catch (IOException e) {
            log.error("{} Ошибка при выполнении запроса", errorMessage, e);
            throw new ApiRequestException(errorMessage + ". Ошибка при выполнении запроса", e);
        }
    }

    /**
     * Обрабатывает HTTP-ответ, проверяет его успешность и возвращает тело ответа в виде строки.
     * Если ответ неуспешен или тело отсутствует, логирует ошибку и выбрасывает соответствующее исключение.
     *
     * @param response     HTTP-ответ, полученный от сервера.
     * @param errorMessage Сообщение об ошибке для логирования и исключения.
     * @return Тело ответа в виде строки.
     * @throws ApiRequestException     запрос завершился с ошибкой или произошла ошибка ввода-вывода.
     * @throws AuthenticationException ошибка связана с аутентификацией (коды ответа 401 или 403).
     * @throws IOException             ошибка при чтении тела ответа.
     */
    private String handleResponse(final Response response, final String errorMessage) throws IOException {
        if (!response.isSuccessful()) {
            String responseBody = response.body() != null ? response.body().string() : "Тело ответа отсутствует";
            log.error(
                    "{} Код ответа: {}, тело: {}",
                    errorMessage,
                    response.code(),
                    SecurityUtils.maskSensitiveData(responseBody)
            );
            if (response.code() == 401 || response.code() == 403) {
                throw new AuthenticationException(errorMessage + ". Код ответа: " + response.code());
            }
            throw new ApiRequestException(errorMessage + ". Код ответа: " + response.code());
        }

        String responseBody = response.body() != null ? response.body().string() : null;
        if (responseBody == null) {
            log.error("{} Тело ответа отсутствует", errorMessage);
            throw new ApiRequestException(errorMessage + ". Тело ответа отсутствует");
        }

        return responseBody;
    }
}
