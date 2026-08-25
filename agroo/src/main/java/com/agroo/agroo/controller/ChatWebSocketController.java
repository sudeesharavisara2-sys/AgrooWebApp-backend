package com.agroo.agroo.controller;

import com.agroo.agroo.dto.request.ChatMessageRequest;
import com.agroo.agroo.dto.response.ChatMessageResponse;
import com.agroo.agroo.model.ChatMessage;
import com.agroo.agroo.model.ChatGroup;
import com.agroo.agroo.model.User;
import com.agroo.agroo.repository.ChatGroupRepository;
import com.agroo.agroo.repository.ChatMessageRepository;
import com.agroo.agroo.repository.UserRepository;
import com.agroo.agroo.service.GroupMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatGroupRepository chatGroupRepository;
    private final UserRepository userRepository;
    private final GroupMemberService groupMemberService;

    @MessageMapping("/chat/{groupId}/send")
    public void sendMessage(
            @DestinationVariable Long groupId,
            @Payload ChatMessageRequest request,
            Authentication authentication) {
        String username = authentication.getName();
        User sender = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ChatGroup group = chatGroupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        // Check if user is member
        if (!groupMemberService.isMember(groupId, username)) {
            throw new RuntimeException("You are not a member of this group");
        }

        // Save message
        ChatMessage message = new ChatMessage();
        message.setContent(request.getContent());
        message.setMessageType(request.getMessageType());
        message.setMediaUrl(request.getMediaUrl());
        message.setSender(sender);
        message.setChatGroup(group);
        message.setCreatedAt(LocalDateTime.now());

        message = chatMessageRepository.save(message);

        // Create response
        ChatMessageResponse response = ChatMessageResponse.builder()
                .id(message.getId())
                .content(message.getContent())
                .messageType(message.getMessageType())
                .mediaUrl(message.getMediaUrl())
                .isRead(message.getIsRead())
                .sender(ChatMessageResponse.SenderInfo.builder()
                        .id(sender.getId())
                        .username(sender.getUsername())
                        .fullName(sender.getFullName())
                        .profileImageUrl(sender.getProfileImageUrl())
                        .build())
                .createdAt(message.getCreatedAt())
                .build();

        // Send to all members of the group
        messagingTemplate.convertAndSend("/topic/group/" + groupId, response);
    }

    @MessageMapping("/chat/{groupId}/typing")
    public void typingIndicator(
            @DestinationVariable Long groupId,
            @Payload String username) {
        messagingTemplate.convertAndSend("/topic/group/" + groupId + "/typing", username + " is typing...");
    }
}