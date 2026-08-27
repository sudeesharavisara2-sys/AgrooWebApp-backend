package com.agroo.agroo.controller;

import com.agroo.agroo.dto.response.ChatMessageResponse;
import com.agroo.agroo.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class ChatMessageController {

    private final ChatMessageService chatMessageService;

    // Get messages for a group
    @GetMapping("/group/{groupId}")
    public ResponseEntity<Page<ChatMessageResponse>> getGroupMessages(
            @PathVariable Long groupId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            Authentication authentication) {
        String username = authentication.getName();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(chatMessageService.getGroupMessages(groupId, pageable, username));
    }

    // Mark messages as read
    @PostMapping("/group/{groupId}/read")
    public ResponseEntity<Void> markMessagesAsRead(
            @PathVariable Long groupId,
            Authentication authentication) {
        String username = authentication.getName();
        chatMessageService.markMessagesAsRead(groupId, username);
        return ResponseEntity.ok().build();
    }

    // Get unread count
    @GetMapping("/group/{groupId}/unread")
    public ResponseEntity<Long> getUnreadCount(
            @PathVariable Long groupId,
            Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(chatMessageService.getUnreadCount(groupId, username));
    }
}