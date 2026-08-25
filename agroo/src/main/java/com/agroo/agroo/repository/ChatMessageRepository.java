package com.agroo.agroo.repository;

import com.agroo.agroo.model.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    // Find messages by group
    Page<ChatMessage> findByChatGroupIdOrderByCreatedAtDesc(Long groupId, Pageable pageable);

    // Find messages by sender
    Page<ChatMessage> findBySenderId(Long senderId, Pageable pageable);

    // Mark messages as read
    @Modifying
    @Query("UPDATE ChatMessage m SET m.isRead = true WHERE m.chatGroup.id = :groupId AND m.sender.id != :userId AND m.isRead = false")
    void markMessagesAsRead(@Param("groupId") Long groupId, @Param("userId") Long userId);

    // Count unread messages
    @Query("SELECT COUNT(m) FROM ChatMessage m WHERE m.chatGroup.id = :groupId AND m.sender.id != :userId AND m.isRead = false")
    Long countUnreadMessages(@Param("groupId") Long groupId, @Param("userId") Long userId);

    // Get latest message for group
    @Query("SELECT m FROM ChatMessage m WHERE m.chatGroup.id = :groupId ORDER BY m.createdAt DESC")
    List<ChatMessage> findLatestMessage(@Param("groupId") Long groupId, Pageable pageable);
}