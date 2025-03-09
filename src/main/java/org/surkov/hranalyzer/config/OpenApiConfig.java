package org.surkov.hranalyzer.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Конфигурация Swagger/OpenAPI для документации API.
 * Настраивает документацию API, требования к аутентификации и сервер в зависимости от активного профиля.
 */
@Configuration
public class OpenApiConfig {

    /**
     * URL сервера для документации API.
     * Загружается из конфигурации приложения в зависимости от профиля (например, localhost для local, продакшен-URL для prod).
     */
    @Value("${openapi.server-url}")
    private String serverUrl;

    /**
     * Создает и настраивает объект OpenAPI для документации API.
     * Добавляет информацию об API, настройки безопасности и сервер.
     *
     * @return настроенный объект OpenAPI
     */
    @Bean
    public OpenAPI customOpenAPI() {
        // Настройка сервера для предотвращения проблем с mixed content
        Server server = new Server()
                .url(serverUrl)
                .description("Сервер API");

        return new OpenAPI()
                // Добавление сервера в список серверов
                .servers(List.of(server))
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