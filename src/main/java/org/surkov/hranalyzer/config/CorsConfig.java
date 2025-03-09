package org.surkov.hranalyzer.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Конфигурация CORS (Cross-Origin Resource Sharing).
 * Определяет правила доступа к API из разных источников в зависимости от активного профиля.
 */
@Configuration
public class CorsConfig {

    /**
     * Адрес разрешенного источника для CORS.
     * Загружается из конфигурации приложения в зависимости от профиля (например, localhost для local, продакшен-URL для prod).
     */
    @Value("${cors.allowed-origin}")
    private String allowedOrigin;

    /**
     * Создает и настраивает конфигуратор CORS.
     * Определяет разрешенные источники, методы и заголовки для доступа к API.
     *
     * @return настроенный конфигуратор CORS
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry
                        .addMapping("/api/**")
                        .allowedOrigins(allowedOrigin)
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }
}