package org.surkov.hranalyzer.service.extraction.impl;

import org.springframework.stereotype.Component;
import org.surkov.hranalyzer.service.extraction.FileType;
import org.surkov.hranalyzer.service.extraction.TextExtractor;

import java.io.InputStream;

/**
 * Экстрактор текста из RTF файлов.
 */
@Component
public class RtfTextExtractor implements TextExtractor<String> {

    /**
     * Извлекает текст из RTF файла.
     *
     * @param inputStream поток ввода для RTF файла
     * @return строка с извлеченным текстом
     */
    @Override
    public String extract(InputStream inputStream) {
        // Заглушка: пока не реализовано
        return "Обработка RTF пока не реализована.";
    }

    /**
     * Возвращает тип файла, который поддерживается этим экстрактором.
     *
     * @return тип файла RTF
     */
    @Override
    public FileType getSupportedFileType() {
        return FileType.RTF;
    }
}