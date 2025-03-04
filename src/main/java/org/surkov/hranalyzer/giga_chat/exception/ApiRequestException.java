package org.surkov.hranalyzer.giga_chat.exception;

/**
 * Исключение, выбрасываемое при ошибке запроса к GigaChat API.
 */
public class ApiRequestException extends RuntimeException {

    /**
     * Конструктор с сообщением об ошибке.
     *
     * @param message Сообщение об ошибке.
     */
    public ApiRequestException(final String message) {
        super(message);
    }

    /**
     * Конструктор с сообщением об ошибке и причиной.
     *
     * @param message Сообщение об ошибке.
     * @param cause   Причина ошибки (исходное исключение).
     */
    public ApiRequestException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
