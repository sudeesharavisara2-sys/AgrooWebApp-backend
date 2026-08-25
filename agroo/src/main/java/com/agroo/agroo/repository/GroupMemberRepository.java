package com.agroo.agroo.repository;

import com.agroo.agroo.model.GroupMember;
import com.agroo.agroo.model.enums.MemberRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {

    // Find member by group and user
    Optional<GroupMember> findByChatGroupIdAndUserId(Long groupId, Long userId);

    // Check if user is member
    boolean existsByChatGroupIdAndUserId(Long groupId, Long userId);

    // Find all members of a group
    List<GroupMember> findByChatGroupId(Long groupId);
    Page<GroupMember> findByChatGroupId(Long groupId, Pageable pageable);

    // Find all admins of a group
    List<GroupMember> findByChatGroupIdAndRole(Long groupId, MemberRole role);

    // Find groups where user is admin
    @Query("SELECT gm FROM GroupMember gm WHERE gm.user.id = :userId AND gm.role = 'ADMIN'")
    List<GroupMember> findAdminGroupsByUserId(@Param("userId") Long userId);

    // Count members in group
    Long countByChatGroupId(Long groupId);

    // Delete all members of a group
    @Modifying
    void deleteByChatGroupId(Long groupId);
}