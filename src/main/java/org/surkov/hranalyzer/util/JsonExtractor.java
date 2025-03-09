package org.surkov.hranalyzer.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Экстрактор текста из JSON ответа Giga Chat.
 */
@Component
@RequiredArgsConstructor
public class JsonExtractor {

    private final ObjectMapper objectMapper;

    /**
     * Извлекает текст из JSON ответа Giga Chat.
     *
     * @param response JSON ответ от Giga Chat
     * @return строка с извлеченным текстом
     * @throws IOException ошибка при обработке JSON
     */
    public String extractTextFromMessage(final String response) throws IOException {
        JsonNode rootNode = objectMapper.readTree(response);
        JsonNode messageNode = rootNode.path("choices").get(0).path("message").path("content");
        return messageNode.asText();
    }
}
