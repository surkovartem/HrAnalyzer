package org.surkov.hranalyzer.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.surkov.hranalyzer.controller.api.AnalysisApi;
import org.surkov.hranalyzer.giga_chat.enumiration.GigaModelType;
import org.surkov.hranalyzer.giga_chat.enumiration.PromptType;
import org.surkov.hranalyzer.exception.UnsupportedFileTypeException;
import org.surkov.hranalyzer.giga_chat.utils.GigaModel;
import org.surkov.hranalyzer.giga_chat.utils.SystemPrompt;
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
public class AnalysisController implements AnalysisApi {

    private final ResumeAnalysisService resumeAnalysisService;

    @Override
    public ResponseEntity<String> analyzeResume(
            final MultipartFile file,
            final PromptType promptType,
            final GigaModelType gigaModelType) {

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
            String analysisResult = resumeAnalysisService.analyzeResume(
                    file.getInputStream(),
                    fileExtension,
                    getSystemPrompt(promptType),
                    getGigaModel(gigaModelType)
            );
            return ResponseEntity.ok(analysisResult);
        } catch (IOException e) {
            log.error("Ошибка при обработке файла: {}", e.getMessage());
            return ResponseEntity.internalServerError().body("Ошибка при обработке файла: " + e.getMessage());
        } catch (UnsupportedFileTypeException e) {
            return ResponseEntity.badRequest().body("Ошибка: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("Неподдерживаемый тип промпта: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Ошибка: " + e.getMessage());
        }
    }

    /**
     * Возвращает системный промпт на основе указанного типа.
     *
     * @param promptType тип промпта (BASE_ANALYSIS, JUNIOR_ANALYSIS, MIDDLE_ANALYSIS, SENIOR_ANALYSIS)
     * @return строка с системным промптом
     * @throws IllegalArgumentException если тип промпта не поддерживается
     */
    private String getSystemPrompt(PromptType promptType) {
        return switch (promptType) {
            case BASE_ANALYSIS -> SystemPrompt.BASE_ANALYSIS_PROMPT;
            case JUNIOR_ANALYSIS -> SystemPrompt.JUNIOR_ANALYSIS_PROMPT;
            case MIDDLE_ANALYSIS -> SystemPrompt.MIDDLE_ANALYSIS_PROMPT;
            case SENIOR_ANALYSIS -> SystemPrompt.SENIOR_ANALYSIS_PROMPT;
        };
    }

    /**
     * Возвращает модель GigaChat на основе указанного типа.
     *
     * @param gigaModelType тип модели (GIGA_MODEL_LITE, GIGA_MODEL_PRO, GIGA_MODEL_MAX)
     * @return строка с наименованием модели
     * @throws IllegalArgumentException если тип модели не поддерживается
     */
    private String getGigaModel(GigaModelType gigaModelType) {
        return switch (gigaModelType) {
            case GigaModelType.GIGA_MODEL_LITE -> GigaModel.GIGA_MODEL_LITE;
            case GigaModelType.GIGA_MODEL_PRO -> GigaModel.GIGA_MODEL_PRO;
            case GigaModelType.GIGA_MODEL_MAX -> GigaModel.GIGA_MODEL_MAX;
        };
    }
}