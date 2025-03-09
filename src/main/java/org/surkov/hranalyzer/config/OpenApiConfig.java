package org.surkov.hranalyzer.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурация OpenAPI для Swagger.
 * Настраивает документацию API и требования к аутентификации.
 */
@Configuration
public class OpenApiConfig {

    /**
     * Создает и настраивает объект OpenAPI для документации API.
     * Добавляет информацию об API и настройки безопасности.
     *
     * @return настроенный объект OpenAPI
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Resume Analysis API")
                        .version("1.0")
                        .description("API для анализа резюме"))
                .components(new Components()
                        .addSecuritySchemes("basicAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("basic")))
                .addSecurityItem(new SecurityRequirement().addList("basicAuth"));
    }
}