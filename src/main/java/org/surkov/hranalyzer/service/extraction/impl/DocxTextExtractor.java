package org.surkov.hranalyzer.service.extraction.impl;

import org.springframework.stereotype.Component;
import org.surkov.hranalyzer.service.extraction.FileType;
import org.surkov.hranalyzer.service.extraction.TextExtractor;

import java.io.InputStream;

/**
 * Экстрактор текста из DOCX файлов.
 */
@Component
public class DocxTextExtractor implements TextExtractor<String> {

    /**
     * Извлекает текст из DOCX файла.
     *
     * @param inputStream поток ввода для DOCX файла
     * @return строка с извлеченным текстом
     */
    @Override
    public String extract(final InputStream inputStream) {
        // Заглушка: пока не реализовано
        return "Обработка DOCX пока не реализована.";
    }

    /**
     * Возвращает тип файла, который поддерживается этим экстрактором.
     *
     * @return тип файла DOCX
     */
    @Override
    public FileType getSupportedFileType() {
        return FileType.DOCX;
    }
}
