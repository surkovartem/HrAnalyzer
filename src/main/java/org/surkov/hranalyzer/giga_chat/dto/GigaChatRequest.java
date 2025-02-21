package org.surkov.hranalyzer.giga_chat.dto;

import lombok.Data;

import java.util.List;

/**
 * DTO для запроса к GigaChat API.
 */
@Data
public class GigaChatRequest {
    private String model;
    private List<GigaChatMessage> messages;
    private boolean stream;
}
