package org.surkov.hranalyzer.service.extraction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.surkov.hranalyzer.exception.UnsupportedFileTypeException;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Сервис для извлечения текста из файла.
 * Отвечает за выбор правильного {@link TextExtractor} на основе типа файла.
 * Использует Dependency Injection для получения списка всех доступных экстракторов.
 *
 * <p><i>Примечание:</i> Чтобы добавить новый формат, достаточно создать новый класс,
 * реализующий {@link TextExtractor}, и Spring автоматически его подхватит.</p>
 */
@Service
public class ExtractionService {

    private final Map<FileType, TextExtractor<String>> extractors;

    /**
     * Конструктор, внедряющий зависимости экстракторов текста.
     *
     * @param extractorList Список всех доступных {@link TextExtractor} в приложении.
     *                      Spring автоматически внедряет сюда все бины, реализующие этот интерфейс.
     */
    @Autowired
    public ExtractionService(List<TextExtractor<String>> extractorList) {
        this.extractors = extractorList.stream()
                .collect(Collectors.toMap(TextExtractor::getSupportedFileType, Function.identity()));
    }

    /**
     * Извлекает текст из файла, представленного потоком ввода.
     *
     * @param inputStream   Поток ввода, содержащий данные файла.
     * @param fileExtension Расширение файла (например, ".pdf", ".docx").
     * @return Извлеченный текст из файла в виде строки.
     * @throws IOException                  Если произошла ошибка ввода-вывода при чтении данных из потока.
     * @throws UnsupportedFileTypeException Если не найден подходящий экстрактор для данного типа файла.
     */
    public String extractText(InputStream inputStream, String fileExtension) throws IOException {
        FileType fileType = FileType.fromExtension(fileExtension);

        TextExtractor<String> extractor = extractors.get(fileType);
        if (extractor == null) {
            throw new UnsupportedFileTypeException("No extractor found for file type: " + fileType);
        }
        return extractor.extract(inputStream);
    }
}