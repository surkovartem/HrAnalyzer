package org.surkov.hranalyzer.giga_chat.exception;

/**
 * Исключение, выбрасываемое при ошибке запроса к GigaChat API.
 */
public class ApiRequestException extends RuntimeException {
    public ApiRequestException(String message) {
        super(message);
    }

    public ApiRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}