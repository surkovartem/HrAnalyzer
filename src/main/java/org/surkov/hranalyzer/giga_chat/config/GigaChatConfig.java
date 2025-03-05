package org.surkov.hranalyzer.giga_chat.config;

import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;
import org.surkov.hranalyzer.giga_chat.utils.SecurityUtils;

/**
 * Конфигурация для GigaChat API.
 * Содержит параметры подключения к API, учетные данные и настройки ретраев.
 * Значения загружаются из свойств приложения с префиксом "giga-chat".
 */
@Slf4j
@Data
@Configuration
@ConfigurationProperties(prefix = "giga-chat")
@Validated
public class GigaChatConfig {

    /**
     * URL API GigaChat для выполнения запросов.
     * Не может быть пустым или {@code null}.
     */
    @NotBlank
    private String apiUrl;

    /**
     * URL для аутентификации в GigaChat API (получение токена доступа).
     * Не может быть пустым или {@code null}.
     */
    @NotBlank
    private String authUrl;

    /**
     * Идентификатор клиента для аутентификации в GigaChat API.
     * Не может быть пустым или {@code null}.
     */
    @NotBlank
    private String clientId;

    /**
     * Секретный ключ клиента для аутентификации в GigaChat API.
     * Не может быть пустым или {@code null}.
     */
    @NotBlank
    private String clientSecret;

    /**
     * Путь к файлу сертификата для настройки SSL/TLS.
     * Не может быть пустым или {@code null}.
     */
    @NotBlank
    private String certPath;

    /**
     * Запас времени (в миллисекундах) перед истечением
     * срока действия токена, чтобы обновить его заранее.
     * Значение по умолчанию: 60_000 мс (60 секунд).
     */
    private int tokenRefreshBufferMs = 60_000;

    /**
     * Начальная задержка (в миллисекундах) перед
     * повторной попыткой получения токена в случае ошибки.
     * Значение по умолчанию: 1_000 мс (1 секунда).
     */
    private int retryInitialDelayMs = 1_000;

    /**
     * Множитель для экспоненциального увеличения
     * задержки между повторными попытками получения токена.
     * Значение по умолчанию: 2.
     */
    private int retryDelayMultiplier = 2;

    /**
     * Логирует конфигурацию после инициализации.
     * Выполняется после создания бина для отображения
     * загруженных параметров в логах.
     */
    @PostConstruct
    public void logConfig() {
        log.info(
                "GigaChatConfig loaded: "
                        + "apiUrl={}, "
                        + "authUrl={}, "
                        + "clientId={}, "
                        + "certPath={}",
                apiUrl,
                authUrl,
                SecurityUtils.maskSensitiveData(clientId),
                certPath
        );
    }
}
