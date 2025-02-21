package org.surkov.hranalyzer.service.extraction.impl;

import org.springframework.stereotype.Component;
import org.surkov.hranalyzer.service.extraction.FileType;
import org.surkov.hranalyzer.service.extraction.TextExtractor;

import java.io.InputStream;

/**
 * Экстрактор текста из TXT файлов.
 */
@Component
public class TxtTextExtractor implements TextExtractor<String> {

    /**
     * Извлекает текст из TXT файла.
     *
     * @param inputStream поток ввода для TXT файла
     * @return строка с извлеченным текстом
     */
    @Override
    public String extract(InputStream inputStream) {
        // Заглушка: пока не реализовано
        return "Обработка TXT пока не реализована.";
    }

    /**
     * Возвращает тип файла, который поддерживается этим экстрактором.
     *
     * @return тип файла TXT
     */
    @Override
    public FileType getSupportedFileType() {
        return FileType.TXT;
    }
}