package com.agroo.agroo.service.impl;

import com.agroo.agroo.dto.response.ChatMessageResponse;
import com.agroo.agroo.model.User;
import com.agroo.agroo.repository.ChatMessageRepository;
import com.agroo.agroo.repository.UserRepository;
import com.agroo.agroo.service.ChatMessageService;
import com.agroo.agroo.service.GroupMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatMessageServiceImpl implements ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private final GroupMemberService groupMemberService;

    @Override
    @Transactional(readOnly = true)
    public Page<ChatMessageResponse> getGroupMessages(Long groupId, Pageable pageable, String username) {
        // Check if user is member
        if (!groupMemberService.isMember(groupId, username)) {
            throw new AccessDeniedException("You are not a member of this group");
        }

        return chatMessageRepository.findByChatGroupIdOrderByCreatedAtDesc(groupId, pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional
    public void markMessagesAsRead(Long groupId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if user is member
        if (!groupMemberService.isMember(groupId, username)) {
            throw new AccessDeniedException("You are not a member of this group");
        }

        chatMessageRepository.markMessagesAsRead(groupId, user.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public Long getUnreadCount(Long groupId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if user is member
        if (!groupMemberService.isMember(groupId, username)) {
            throw new AccessDeniedException("You are not a member of this group");
        }

        return chatMessageRepository.countUnreadMessages(groupId, user.getId());
    }

    private ChatMessageResponse mapToResponse(com.agroo.agroo.model.ChatMessage message) {
        return ChatMessageResponse.builder()
                .id(message.getId())
                .content(message.getContent())
                .messageType(message.getMessageType())
                .mediaUrl(message.getMediaUrl())
                .isRead(message.getIsRead())
                .sender(ChatMessageResponse.SenderInfo.builder()
                        .id(message.getSender().getId())
                        .username(message.getSender().getUsername())
                        .fullName(message.getSender().getFullName())
                        .profileImageUrl(message.getSender().getProfileImageUrl())
                        .build())
                .createdAt(message.getCreatedAt())
                .build();
    }
}