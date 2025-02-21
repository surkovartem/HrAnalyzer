package org.surkov.hranalyzer.service.extraction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.surkov.hranalyzer.exception.UnsupportedFileTypeException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Сервис для извлечения текста из файла.
 * Отвечает за выбор правильного TextExtractor на основе типа файла.
 * Использует Dependency Injection для получения списка всех доступных экстракторов.
 *
 * <blockquote><i>Примечание:</i> Чтобы добавить новый формат, достаточно создать новый класс, реализующий TextExtractor, и Spring автоматически его подхватит.</blockquote>
 */
@Service
public class ExtractionService {

    private final Map<FileType, TextExtractor<String>> extractors;

    /**
     * Конструктор для инициализации списка экстракторов текста.
     *
     * @param extractorList список всех доступных экстракторов текста
     */
    @Autowired
    public ExtractionService(List<TextExtractor<String>> extractorList) {
        this.extractors = extractorList.stream()
                .collect(Collectors.toMap(TextExtractor::getSupportedFileType, Function.identity()));
    }

    /**
     * Извлекает текст из файла по указанному пути.
     *
     * @param filePath путь к файлу
     * @return строка с извлеченным текстом
     * @throws IOException если произошла ошибка при чтении файла
     */
    public String extractText(String filePath) throws IOException {
        FileType fileType = FileType.fromFileName(filePath);

        TextExtractor<String> extractor = extractors.get(fileType);
        if (extractor == null) {
            throw new UnsupportedFileTypeException("No extractor found for file type: " + fileType);
        }
        try (InputStream is = Files.newInputStream(Paths.get(filePath))) {
            return extractor.extract(is);
        }
    }
}
