package com.agroo.agroo.repository;

import com.agroo.agroo.model.ActivityLog;
import com.agroo.agroo.model.enums.ActivityType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {

    // ============================================================
    // FIND ALL ORDERED BY CREATED AT DESC
    // ============================================================
    Page<ActivityLog> findAllByOrderByCreatedAtDesc(Pageable pageable);
    List<ActivityLog> findAllByOrderByCreatedAtDesc();

    // ============================================================
    // FIND BY USER
    // ============================================================
    Page<ActivityLog> findByUserId(Long userId, Pageable pageable);
    List<ActivityLog> findByUserId(Long userId);

    // ============================================================
    // FIND BY ACTIVITY TYPE
    // ============================================================
    Page<ActivityLog> findByActivityType(ActivityType activityType, Pageable pageable);
    List<ActivityLog> findByActivityType(ActivityType activityType);

    // ============================================================
    // FIND BY DATE RANGE
    // ============================================================
    @Query("SELECT a FROM ActivityLog a WHERE a.createdAt BETWEEN :startDate AND :endDate ORDER BY a.createdAt DESC")
    List<ActivityLog> findByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    // ============================================================
    // COUNT BY ACTIVITY TYPE
    // ============================================================
    @Query("SELECT a.activityType, COUNT(a) FROM ActivityLog a GROUP BY a.activityType")
    List<Object[]> countByActivityType();

    // ============================================================
    // COUNT BY DATE
    // ============================================================
    @Query("SELECT DATE(a.createdAt), COUNT(a) FROM ActivityLog a GROUP BY DATE(a.createdAt) ORDER BY DATE(a.createdAt) DESC")
    List<Object[]> countByDate();
}