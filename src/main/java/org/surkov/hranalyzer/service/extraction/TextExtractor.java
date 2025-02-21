package org.surkov.hranalyzer.service.extraction;

import java.io.IOException;
import java.io.InputStream;

/**
 * Интерфейс для экстракторов текста из файлов.
 *
 * @param <T> тип возвращаемого значения
 */
public interface TextExtractor<T> {

    /**
     * Извлекает текст из потока ввода.
     *
     * @param inputStream поток ввода для файла
     * @return объект типа T с извлеченным текстом
     * @throws IOException если произошла ошибка при чтении файла
     */
    T extract(InputStream inputStream) throws IOException;

    /**
     * Возвращает тип файла, который поддерживается этим экстрактором.
     *
     * @return тип файла
     */
    FileType getSupportedFileType();
}