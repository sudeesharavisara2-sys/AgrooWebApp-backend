package com.agroo.agroo.service.impl;

import com.agroo.agroo.dto.response.DashboardStats;
import com.agroo.agroo.repository.*;
import com.agroo.agroo.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final PostRepository postRepository;
    private final ChatGroupRepository chatGroupRepository;
    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;
    private final AlertRepository alertRepository;

    @Override
    public DashboardStats getDashboardStats() {
        long totalUsers = userRepository.count();
        long activeUsers = userRepository.countByIsActiveTrue();
        long totalProducts = productRepository.count();
        long totalPosts = postRepository.count();
        long totalGroups = chatGroupRepository.countByIsActiveTrue();
        long totalComments = commentRepository.count();
        long totalLikes = likeRepository.count();
        long totalAlerts = alertRepository.count();

        // Category stats
        Map<String, Long> categoryStats = new HashMap<>();
        // You can add more detailed stats here

        return DashboardStats.builder()
                .totalUsers(totalUsers)
                .activeUsers(activeUsers)
                .totalProducts(totalProducts)
                .totalPosts(totalPosts)
                .totalGroups(totalGroups)
                .totalComments(totalComments)
                .totalLikes(totalLikes)
                .totalAlerts(totalAlerts)
                .categoryStats(categoryStats)
                .dailyActivity(new HashMap<>())
                .monthlyActivity(new HashMap<>())
                .build();
    }
}