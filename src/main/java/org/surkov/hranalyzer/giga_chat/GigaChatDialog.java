package org.surkov.hranalyzer.giga_chat;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.surkov.hranalyzer.giga_chat.config.GigaChatConfig;

/**
 * Фасад для взаимодействия с GigaChat API.
 * Отвечает за инициализацию компонента,
 * валидацию конфигурации и выполнение запросов к API.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GigaChatDialog {

    /**
     * Конфигурация GigaChat API, содержащая URL и другие параметры.
     */
    private final GigaChatConfig config;

    /**
     * Менеджер токенов, предоставляющий токен доступа для аутентификации в API.
     */
    private final TokenManager tokenManager;

    /**
     * Клиент для выполнения запросов к GigaChat API.
     */
    private final GigaChatApiClient apiClient;

    /**
     * Инициализирует компонент.
     * Проверяет конфигурацию и получает начальный токен доступа.
     *
     * @throws IllegalStateException конфигурация некорректна
     *                               или не удалось получить токен доступа
     */
    @PostConstruct
    public void init() {
        validateConfig();
        try {
            tokenManager.fetchAccessToken();
        } catch (Exception e) {
            log.error("Не удалось инициализировать GigaChatDialog", e);
            throw new IllegalStateException("Не удалось инициализировать GigaChatDialog", e);
        }
    }

    /**
     * Проверяет корректность конфигурации.
     *
     * @throws IllegalStateException URL аутентификации не использует HTTPS
     */
    private void validateConfig() {
        if (!config.getAuthUrl().startsWith("https")) {
            throw new IllegalStateException("AUTH_URL должен использовать HTTPS");
        }
    }

    /**
     * Выполняет запрос к GigaChat API для получения ответа на основе переданных данных.
     *
     * @param systemPrompt Системный промпт, задающий контекст для анализа.
     * @param text         Текст резюме для анализа.
     * @param model        Модель для анализа резюме.
     * @return Ответ от API в виде строки.
     */
    public String getResponse(
            final String systemPrompt,
            final String text,
            final String model
    ) {
        return apiClient.getResponse(systemPrompt, text, model);
    }
}
