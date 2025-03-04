package org.surkov.hranalyzer.giga_chat.exception;

/**
 * Исключение, выбрасываемое при ошибке аутентификации.
 */
public class AuthenticationException extends RuntimeException {

    /**
     * Конструктор с сообщением об ошибке.
     *
     * @param message Сообщение об ошибке.
     */
    public AuthenticationException(final String message) {
        super(message);
    }

    /**
     * Конструктор с сообщением об ошибке и причиной.
     *
     * @param message Сообщение об ошибке.
     * @param cause   Причина ошибки (исходное исключение).
     */
    public AuthenticationException(
            final String message,
            final Throwable cause) {
        super(message, cause);
    }
}
