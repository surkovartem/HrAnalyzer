package org.surkov.hranalyzer.giga_chat.dto;

import lombok.Data;

/**
 * DTO для ответа от GigaChat API с токеном.
 */
@Data
public class TokenResponse {
    private String accessToken;
    private long expiresAt;
}
