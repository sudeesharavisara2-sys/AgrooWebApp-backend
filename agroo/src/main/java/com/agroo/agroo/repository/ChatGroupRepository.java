package com.agroo.agroo.repository;

import com.agroo.agroo.model.ChatGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatGroupRepository extends JpaRepository<ChatGroup, Long> {

    // ============================================================
    // FIND GROUPS BY USER (Member)
    // ============================================================
    @Query("SELECT g FROM ChatGroup g JOIN g.members m WHERE m.user.id = :userId AND g.isActive = true")
    Page<ChatGroup> findGroupsByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT g FROM ChatGroup g JOIN g.members m WHERE m.user.id = :userId AND g.isActive = true")
    List<ChatGroup> findGroupsByUserId(@Param("userId") Long userId);

    // ============================================================
    // FIND GROUPS CREATED BY USER
    // ============================================================
    @Query("SELECT g FROM ChatGroup g WHERE g.createdBy.id = :userId AND g.isActive = true")
    Page<ChatGroup> findGroupsCreatedByUser(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT g FROM ChatGroup g WHERE g.createdBy.id = :userId AND g.isActive = true")
    List<ChatGroup> findGroupsCreatedByUser(@Param("userId") Long userId);

    // ============================================================
    // SEARCH GROUPS BY NAME
    // ============================================================
    @Query("SELECT g FROM ChatGroup g WHERE LOWER(g.name) LIKE LOWER(CONCAT('%', :keyword, '%')) AND g.isActive = true")
    Page<ChatGroup> searchByName(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT g FROM ChatGroup g WHERE LOWER(g.name) LIKE LOWER(CONCAT('%', :keyword, '%')) AND g.isActive = true")
    List<ChatGroup> searchByName(@Param("keyword") String keyword);

    // ============================================================
    // COUNT METHODS
    // ============================================================
    long countByIsActiveTrue();
    long count();

    // ============================================================
    // FIND GROUPS WITH MOST MEMBERS
    // ============================================================
    @Query("SELECT g FROM ChatGroup g ORDER BY SIZE(g.members) DESC")
    Page<ChatGroup> findMostActiveGroups(Pageable pageable);

    // ============================================================
    // FIND GROUPS WITH IMAGES
    // ============================================================
    @Query("SELECT g FROM ChatGroup g WHERE g.imageUrl IS NOT NULL AND g.imageUrl != '' AND g.isActive = true")
    List<ChatGroup> findGroupsWithImages();

    // ============================================================
    // FIND RECENTLY CREATED GROUPS
    // ============================================================
    @Query("SELECT g FROM ChatGroup g WHERE g.isActive = true ORDER BY g.createdAt DESC")
    Page<ChatGroup> findRecentGroups(Pageable pageable);

    @Query("SELECT g FROM ChatGroup g WHERE g.isActive = true ORDER BY g.createdAt DESC")
    List<ChatGroup> findRecentGroups();

    // ============================================================
    // FIND ALL ACTIVE GROUPS
    // ============================================================
    List<ChatGroup> findByIsActiveTrue();
}