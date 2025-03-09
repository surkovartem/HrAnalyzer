package org.surkov.hranalyzer.service.extraction.impl;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;
import org.surkov.hranalyzer.service.extraction.FileType;
import org.surkov.hranalyzer.service.extraction.TextExtractor;

import java.io.IOException;
import java.io.InputStream;

/**
 * Реализация {@link TextExtractor} для извлечения текста из PDF-файлов.
 * Использует библиотеку Apache PDFBox.
 */
@Component
public class PdfTextExtractor implements TextExtractor<String> {

    /**
     * Извлекает текст из PDF-файла.
     *
     * @param inputStream Поток ввода, содержащий данные PDF-файла.
     * @return Извлеченный текст.
     * @throws IOException ошибка при чтении или обработке PDF-файла.
     */
    @Override
    public String extract(final InputStream inputStream) throws IOException {
        try (PDDocument document = PDDocument.load(inputStream)) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }

    /**
     * Возвращает тип файла, поддерживаемый этим экстрактором (PDF).
     *
     * @return {@link FileType#PDF}.
     */
    @Override
    public FileType getSupportedFileType() {
        return FileType.PDF;
    }
}
