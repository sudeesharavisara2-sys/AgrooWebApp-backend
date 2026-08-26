package com.agroo.agroo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStats {
    private Long totalUsers;
    private Long activeUsers;
    private Long totalProducts;
    private Long totalPosts;
    private Long totalGroups;
    private Long totalComments;
    private Long totalLikes;
    private Long totalAlerts;
    private Map<String, Long> dailyActivity;
    private Map<String, Long> monthlyActivity;
    private Map<String, Long> categoryStats;
}