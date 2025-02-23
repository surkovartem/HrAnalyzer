package org.surkov.hranalyzer.service.analysis;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.surkov.hranalyzer.service.extraction.ExtractionService;
import org.surkov.hranalyzer.service.gpt.GptService;

import java.io.IOException;
import java.io.InputStream;

/**
 * Сервис для анализа резюме.
 */
@Service
@RequiredArgsConstructor
public class ResumeAnalysisService {

    private final ExtractionService extractionService;
    private final GptService gptService;

    /**
     * Анализирует резюме, полученное из потока ввода.
     *
     * @param inputStream   Поток ввода, содержащий данные резюме.
     * @param fileExtension Расширение файла резюме (например, ".pdf", ".docx").
     * @return Результат анализа резюме в виде строки.
     * @throws IOException Если произошла ошибка ввода-вывода при чтении данных из потока.
     */
    public String analyzeResume(
            InputStream inputStream,
            String fileExtension,
            String systemPrompt,
            String gigaModel
    ) throws IOException {
        String resumeText = extractionService.extractText(inputStream, fileExtension);
        return gptService.analyzeResume(systemPrompt, resumeText, gigaModel);
    }
}