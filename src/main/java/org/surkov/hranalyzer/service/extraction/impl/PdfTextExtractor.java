package org.surkov.hranalyzer.service.extraction.impl;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;
import org.surkov.hranalyzer.service.extraction.FileType;
import org.surkov.hranalyzer.service.extraction.TextExtractor;

import java.io.IOException;
import java.io.InputStream;

/**
 * Экстрактор текста из PDF файлов.
 */
@Component
public class PdfTextExtractor implements TextExtractor<String> {

    /**
     * Извлекает текст из PDF файла.
     *
     * @param inputStream поток ввода для PDF файла
     * @return строка с извлеченным текстом
     * @throws IOException если произошла ошибка при чтении файла
     */
    @Override
    public String extract(InputStream inputStream) throws IOException {
        try (PDDocument document = PDDocument.load(inputStream)) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }

    /**
     * Возвращает тип файла, который поддерживается этим экстрактором.
     *
     * @return тип файла PDF
     */
    @Override
    public FileType getSupportedFileType() {
        return FileType.PDF;
    }
}
