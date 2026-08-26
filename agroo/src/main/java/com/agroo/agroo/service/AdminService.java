package com.agroo.agroo.service;

import com.agroo.agroo.model.ActivityLog;
import com.agroo.agroo.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;

public interface AdminService {
    Page<User> getAllUsers(Pageable pageable);
    Page<User> searchUsers(String keyword, Pageable pageable);
    User getUser(Long userId);
    User activateUser(Long userId);
    User deactivateUser(Long userId);
    User makeAdmin(Long userId);
    User removeAdmin(Long userId);
    void deleteUser(Long userId);
    void deleteProduct(Long productId);
    void deletePost(Long postId);
    void deleteComment(Long commentId);
    void deleteGroup(Long groupId);
    Page<ActivityLog> getActivityLogs(Pageable pageable);
    Map<String, Long> getUserStats();
    Map<String, Long> getProductStats();
    Map<String, Long> getPostStats();
    Map<String, Long> getGroupStats();
}