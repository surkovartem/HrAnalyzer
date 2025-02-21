package org.surkov.hranalyzer.giga_chat.exception;

/**
 * Исключение, выбрасываемое при ошибке аутентификации.
 */
public class AuthenticationException extends RuntimeException {
    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
