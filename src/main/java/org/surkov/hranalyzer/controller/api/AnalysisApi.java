package org.surkov.hranalyzer.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.surkov.hranalyzer.giga_chat.enumiration.GigaModelType;
import org.surkov.hranalyzer.giga_chat.enumiration.PromptType;

/**
 * Интерфейс, определяющий API для анализа резюме.
 * Содержит Swagger-аннотации для автоматической генерации документации.
 */
public interface AnalysisApi {

    /**
     * Сообщение об успешном анализе резюме.
     */
    String RESUME_ANALYZED_SUCCESSFULLY = "Резюме успешно проанализировано";

    /**
     * Сообщение о неподдерживаемом типе файла или промпта.
     */
    String UNSUPPORTED_FILE_OR_PROMPT_TYPE =
            "Неподдерживаемый тип файла или неподдерживаемый тип промпта";

    /**
     * Сообщение о внутренней ошибке сервера.
     */
    String INTERNAL_SERVER_ERROR = "Внутренняя ошибка сервера";

    /**
     * Анализ резюме.
     *
     * @param file       Резюме формата PDF, RTF, DOCX, TXT.
     * @param promptType Тип системного промпта для анализа резюме.
     * @param modelType  Модель для анализа.
     * @return Текст анализа резюме.
     */
    @Operation(
            summary = "Анализ резюме",
            description = "Загрузите файл резюме для анализа."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = RESUME_ANALYZED_SUCCESSFULLY,
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = String.class))
                    }
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = UNSUPPORTED_FILE_OR_PROMPT_TYPE,
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = INTERNAL_SERVER_ERROR,
                    content = @Content
            )}
    )
    @PostMapping(
            value = "/resume",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    ResponseEntity<String> analyzeResume(
            @Parameter(
                    description = "Файл резюме для анализа",
                    required = true
            ) @RequestParam("file")
            MultipartFile file,
            @Parameter(
                    description = "Тип системного промпта для анализа",
                    required = true,
                    schema = @Schema(implementation = PromptType.class)
            )
            @RequestParam("promptType") PromptType promptType,
            @Parameter(
                    description = "Модель GigaChat",
                    required = true,
                    schema = @Schema(implementation = GigaModelType.class)
            )
            @RequestParam("gigaModelType") GigaModelType modelType);
}
