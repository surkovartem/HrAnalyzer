package org.surkov.hranalyzer.exception;

/**
 * Исключение, выбрасываемое, когда встречается неподдерживаемый тип файла.
 */
public class UnsupportedFileTypeException extends RuntimeException {

    /**
     * Конструктор с сообщением.
     *
     * @param message Сообщение об ошибке.
     */
    public UnsupportedFileTypeException(final String message) {
        super(message);
    }
}
