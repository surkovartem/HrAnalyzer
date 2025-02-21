package org.surkov.hranalyzer.service.analysis;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.surkov.hranalyzer.service.extraction.ExtractionService;
import org.surkov.hranalyzer.service.gpt.GptService;

import java.io.IOException;

/**
 * Сервис для анализа резюме.
 */
@Service
@RequiredArgsConstructor
public class ResumeAnalysisService {

    private final ExtractionService extractionService;
    private final GptService gptService;

    /**
     * Анализирует резюме по указанному пути к файлу.
     *
     * @param filePath путь к файлу резюме
     * @return строка с результатом анализа
     * @throws IOException если произошла ошибка при чтении файла
     */
    public String analyzeResume(String filePath) throws IOException {
        String resumeText = extractionService.extractText(filePath);
        String prompt = "Представь резюме в виде json (и только, без комментариев): expirence & skills. Другие данные не нужны " + resumeText;
        return gptService.analyzeResume(prompt);
    }
}