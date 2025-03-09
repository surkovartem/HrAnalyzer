package org.surkov.hranalyzer.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Конфигурация Swagger/OpenAPI для документации API.
 * Настраивает документацию API, требования к аутентификации и HTTPS сервер.
 */
@Configuration
public class OpenApiConfig {

    /**
     * Создает и настраивает объект OpenAPI для документации API.
     * Добавляет информацию об API, настройки безопасности и HTTPS сервер.
     *
     * @return настроенный объект OpenAPI
     */
    @Bean
    public OpenAPI customOpenAPI() {
        // Настройка HTTPS сервера для предотвращения проблем с mixed content
        Server httpsServer = new Server()
                .url("https://hr-analyzer.sophia-lab.ru")
                .description("HTTPS сервер");

        return new OpenAPI()
                // Добавление HTTPS сервера в список серверов
                .servers(List.of(httpsServer))
                // Информация об API
                .info(new Info()
                        .title("Resume Analysis API")
                        .version("1.0")
                        .description("API для анализа резюме"))
                // Настройка схемы безопасности (Basic Auth)
                .components(new Components()
                        .addSecuritySchemes("basicAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("basic")))
                // Добавление требования безопасности ко всем операциям
                .addSecurityItem(new SecurityRequirement().addList("basicAuth"));
    }
}