package org.surkov.hranalyzer.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.surkov.hranalyzer.service.analysis.ResumeAnalysisService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Контроллер для обработки запросов на анализ резюме.
 */
@Slf4j
@RestController
@RequestMapping("/api/analysis")
@RequiredArgsConstructor
public class AnalysisController {

    private final ResumeAnalysisService resumeAnalysisService;

    /**
     * Обрабатывает POST-запрос для анализа резюме.
     *
     * @param filePath путь к файлу резюме
     * @return ResponseEntity с результатом анализа или сообщением об ошибке
     */
    @PostMapping("/resume")
    public ResponseEntity<String> analyzeResume(@RequestBody String filePath) {
        if (!Files.exists(Paths.get(filePath)))
            return ResponseEntity.badRequest().body("Указанный файл не существует");
        try {
            String analysisResult = resumeAnalysisService.analyzeResume(filePath);
            return ResponseEntity.ok(analysisResult);
        } catch (IOException e) {
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().body("Error processing the file: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}