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

    @Operation(summary = "Анализ резюме", description = "Загрузите файл резюме для анализа.")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Резюме успешно проанализировано",
                            content = {
                                    @Content(
                                            mediaType = "application/json",
                                            schema = @Schema(implementation = String.class)
                                    )
                            }
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Неподдерживаемый тип файла или неподдерживаемый тип промпта",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Внутренняя ошибка сервера",
                            content = @Content
                    )
            }
    )
    @PostMapping(value = "/resume", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<String> analyzeResume(
            @Parameter(
                    description = "Файл резюме для анализа",
                    required = true
            )
            @RequestParam("file")
            MultipartFile file,

            @Parameter(
                    description = "Тип системного промпта для анализа",
                    required = true,
                    schema = @Schema(implementation = PromptType.class)
            )
            @RequestParam("promptType")
            PromptType promptType,

            @Parameter(
                    description = "Модель GigaChat",
                    required = true,
                    schema = @Schema(implementation = GigaModelType.class)
            )
            @RequestParam("gigaModelType")
            GigaModelType gigaModelType
    );
}