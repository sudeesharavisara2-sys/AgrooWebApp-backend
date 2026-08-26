package com.agroo.agroo.service.impl;

import com.agroo.agroo.model.*;
import com.agroo.agroo.model.enums.Role;
import com.agroo.agroo.repository.*;
import com.agroo.agroo.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final ChatGroupRepository chatGroupRepository;
    private final ActivityLogRepository activityLogRepository;

    @Override
    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    public Page<User> searchUsers(String keyword, Pageable pageable) {
        // Simple search implementation - can be enhanced
        return userRepository.findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(keyword, keyword, pageable);
    }

    @Override
    public User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    @Transactional
    public User activateUser(Long userId) {
        User user = getUser(userId);
        user.setIsActive(true);
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User deactivateUser(Long userId) {
        User user = getUser(userId);
        user.setIsActive(false);
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User makeAdmin(Long userId) {
        User user = getUser(userId);
        user.setRole(Role.ADMIN);
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User removeAdmin(Long userId) {
        User user = getUser(userId);
        user.setRole(Role.REGISTERED_USER);
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        User user = getUser(userId);
        userRepository.delete(user);
    }

    @Override
    @Transactional
    public void deleteProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        productRepository.delete(product);
    }

    @Override
    @Transactional
    public void deletePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        postRepository.delete(post);
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        commentRepository.delete(comment);
    }

    @Override
    @Transactional
    public void deleteGroup(Long groupId) {
        ChatGroup group = chatGroupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
        group.setIsActive(false);
        chatGroupRepository.save(group);
    }

    @Override
    public Page<ActivityLog> getActivityLogs(Pageable pageable) {
        return activityLogRepository.findAllByOrderByCreatedAtDesc(pageable);
    }

    @Override
    public Map<String, Long> getUserStats() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("total", userRepository.count());
        stats.put("active", userRepository.countByIsActiveTrue());
        stats.put("verified", userRepository.countByIsVerifiedTrue());
        return stats;
    }

    @Override
    public Map<String, Long> getProductStats() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("total", productRepository.count());
        stats.put("available", productRepository.countByIsAvailableTrue());
        return stats;
    }

    @Override
    public Map<String, Long> getPostStats() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("total", postRepository.count());
        stats.put("public", postRepository.countByIsPublicTrue());
        return stats;
    }

    @Override
    public Map<String, Long> getGroupStats() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("total", chatGroupRepository.count());
        stats.put("active", chatGroupRepository.countByIsActiveTrue());
        return stats;
    }
}