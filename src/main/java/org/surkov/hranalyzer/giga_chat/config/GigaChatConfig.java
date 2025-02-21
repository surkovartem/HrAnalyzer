package org.surkov.hranalyzer.giga_chat.config;

import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

/**
 * Конфигурация для GigaChat API.
 */
@Slf4j
@Data
@Configuration
@ConfigurationProperties(prefix = "giga-chat")
@Validated
public class GigaChatConfig {
    @NotBlank
    private String apiUrl;
    @NotBlank
    private String authUrl;
    @NotBlank
    private String clientId;
    @NotBlank
    private String clientSecret;
    @NotBlank
    private String certPath;

    @PostConstruct
    public void logConfig() {
        log.info("GigaChatConfig loaded: apiUrl={}, authUrl={}, clientId={}, certPath={}",
                apiUrl, authUrl, maskSensitiveData(clientId), certPath);
    }

    private String maskSensitiveData(String data) {
        if (data == null || data.length() < 4) {
            return "****";
        }
        return data.substring(0, 4) + "****" + data.substring(data.length() - 4);
    }
}