package com.agroo.agroo.dto.request;

import com.agroo.agroo.model.enums.MessageType;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageRequest {

    @NotBlank(message = "Message content is required")
    private String content;

    private MessageType messageType = MessageType.TEXT;
    private String mediaUrl;
}