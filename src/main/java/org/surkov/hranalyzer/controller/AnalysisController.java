package org.surkov.hranalyzer.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.surkov.hranalyzer.controller.api.AnalysisApi;
import org.surkov.hranalyzer.exception.UnsupportedFileTypeException;
import org.surkov.hranalyzer.service.analysis.ResumeAnalysisService;
import org.surkov.hranalyzer.service.extraction.FileType;

import java.io.IOException;

/**
 * Контроллер для обработки запросов, связанных с анализом резюме.
 * Реализует интерфейс {@link AnalysisApi}.
 */
@Slf4j
@RestController
@RequestMapping("/api/analysis")
@RequiredArgsConstructor
public class AnalysisController implements AnalysisApi { // Реализуем интерфейс

    private final ResumeAnalysisService resumeAnalysisService;

    @Override
    public ResponseEntity<String> analyzeResume(final MultipartFile file) {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Файл не должен быть пустым.");
        }

        try {
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null) {
                return ResponseEntity.badRequest().body("Невозможно определить имя файла.");
            }
            String fileExtension = "";
            int i = originalFilename.lastIndexOf('.');
            if (i > 0) {
                fileExtension = originalFilename.substring(i);
            }

            FileType.fromExtension(fileExtension);

            String analysisResult = resumeAnalysisService.analyzeResume(file.getInputStream(), fileExtension);
            return ResponseEntity.ok(analysisResult);

        } catch (IOException e) {
            log.error("Ошибка при обработке файла: {}", e.getMessage());
            return ResponseEntity.internalServerError().body("Ошибка при обработке файла: " + e.getMessage());
        } catch (UnsupportedFileTypeException e) {
            return ResponseEntity.badRequest().body("Ошибка: " + e.getMessage());
        }
    }
}
