package com.agroo.agroo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatResponse {
    private String reply;
    private String sessionId;
    private LocalDateTime timestamp;
    private String source; // "OPENAI", "FARMER_GUIDE", "GENERAL"
    private Boolean success;
    private String message;
}