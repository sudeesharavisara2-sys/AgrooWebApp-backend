package com.agroo.agroo.service;

import com.agroo.agroo.dto.response.ChatMessageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ChatMessageService {
    Page<ChatMessageResponse> getGroupMessages(Long groupId, Pageable pageable, String username);
    void markMessagesAsRead(Long groupId, String username);
    Long getUnreadCount(Long groupId, String username);
}