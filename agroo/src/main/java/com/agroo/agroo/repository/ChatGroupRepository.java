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

    // Find groups where user is a member
    @Query("SELECT g FROM ChatGroup g JOIN g.members m WHERE m.user.id = :userId AND g.isActive = true")
    Page<ChatGroup> findGroupsByUserId(@Param("userId") Long userId, Pageable pageable);

    // Find groups created by user
    @Query("SELECT g FROM ChatGroup g WHERE g.createdBy.id = :userId AND g.isActive = true")
    Page<ChatGroup> findGroupsCreatedByUser(@Param("userId") Long userId, Pageable pageable);

    // Search groups by name
    @Query("SELECT g FROM ChatGroup g WHERE LOWER(g.name) LIKE LOWER(CONCAT('%', :keyword, '%')) AND g.isActive = true")
    Page<ChatGroup> searchByName(@Param("keyword") String keyword, Pageable pageable);

    // Count active groups
    Long countByIsActiveTrue();
}