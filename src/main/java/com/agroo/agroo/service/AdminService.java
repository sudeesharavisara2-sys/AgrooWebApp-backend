package com.agroo.agroo.service;

import com.agroo.agroo.model.ActivityLog;
import com.agroo.agroo.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;

public interface AdminService {
    // ============================================================
    // USER MANAGEMENT
    // ============================================================
    Page<User> getAllUsers(Pageable pageable);
    Page<User> searchUsers(String keyword, Pageable pageable);
    User getUser(Long userId);
    User activateUser(Long userId);
    User deactivateUser(Long userId);
    User makeAdmin(Long userId);
    User removeAdmin(Long userId);
    void deleteUser(Long userId);

    // ============================================================
    // CONTENT MANAGEMENT - DELETE
    // ============================================================
    void deleteProduct(Long productId);
    void deleteMachine(Long machineId);
    void deletePost(Long postId);
    void deleteComment(Long commentId);
    void deleteGroup(Long groupId);

    // ============================================================
    // ACTIVITY LOGS
    // ============================================================
    Page<ActivityLog> getActivityLogs(Pageable pageable);

    // ============================================================
    // STATISTICS
    // ============================================================
    Map<String, Long> getUserStats();
    Map<String, Long> getProductStats();
    Map<String, Long> getMachineStats();
    Map<String, Long> getPostStats();
    Map<String, Long> getGroupStats();
}