package com.agroo.agroo.dto.response;

import com.agroo.agroo.model.enums.MessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageResponse {
    private Long id;
    private String content;
    private MessageType messageType;
    private String mediaUrl;
    private Boolean isRead;
    private SenderInfo sender;
    private LocalDateTime createdAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SenderInfo {
        private Long id;
        private String username;
        private String fullName;
        private String profileImageUrl;
    }
}