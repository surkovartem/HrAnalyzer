package org.surkov.hranalyzer.exception;

/**
 * Исключение, выбрасываемое при попытке обработки неподдерживаемого типа файла.
 */
public class UnsupportedFileTypeException extends RuntimeException {

    /**
     * Конструктор для создания исключения с сообщением.
     *
     * @param message сообщение об ошибке
     */
    public UnsupportedFileTypeException(String message) {
        super(message);
    }
}