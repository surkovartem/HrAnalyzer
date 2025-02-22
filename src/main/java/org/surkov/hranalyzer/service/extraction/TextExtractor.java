package org.surkov.hranalyzer.service.extraction;

import java.io.IOException;
import java.io.InputStream;

/**
 * Интерфейс для экстракторов текста из файлов.
 *
 * @param <T> Тип возвращаемого значения (например, String, List<String> и т.д.).
 */
public interface TextExtractor<T> {
    /**
     * Извлекает содержимое из файла, представленного потоком ввода.
     *
     * @param inputStream Поток ввода, содержащий данные файла.
     * @return Извлеченное содержимое файла.
     * @throws IOException Если произошла ошибка ввода-вывода.
     */
    T extract(InputStream inputStream) throws IOException;

    /**
     * Возвращает тип файла, поддерживаемый этим экстрактором.
     *
     * @return Тип файла.
     */
    FileType getSupportedFileType();
}