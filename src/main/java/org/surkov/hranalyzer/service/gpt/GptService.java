package org.surkov.hranalyzer.service.gpt;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.surkov.hranalyzer.giga_chat.GigaChatDialog;
import org.surkov.hranalyzer.util.JsonExtractor;

import java.io.IOException;

/**
 * Сервис для анализа резюме с помощью Giga Chat.
 */
@Service
@RequiredArgsConstructor
public class GptService {

    private final GigaChatDialog gigaChatDialog;
    private final JsonExtractor jsonExtractor;

    /**
     * Анализирует текст резюме с помощью Giga Chat.
     *
     * @param systemPrompt Cистемный промпта для анализа резюме.
     * @param text         Текст резюме для анализа.
     * @param model    Модель для анализа резюме.
     * @return строка с результатом анализа
     * @throws IOException произошла ошибка при обработке ответа
     */
    public String analyzeResume(
            final String systemPrompt,
            final String text,
            final String model) throws IOException {
        String response = gigaChatDialog.getResponse(systemPrompt, text, model);
        return jsonExtractor.extractTextFromMessage(response);
    }
}
