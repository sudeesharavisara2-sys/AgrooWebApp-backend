package com.agroo.agroo.service;

import com.agroo.agroo.dto.response.GroupMemberResponse;
import com.agroo.agroo.model.enums.MemberRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GroupMemberService {
    boolean isMember(Long groupId, String username);
    boolean isAdmin(Long groupId, String username);
    GroupMemberResponse addMember(Long groupId, Long userId, String username);
    GroupMemberResponse removeMember(Long groupId, Long userId, String username);
    GroupMemberResponse changeRole(Long groupId, Long userId, MemberRole role, String username);
    Page<GroupMemberResponse> getGroupMembers(Long groupId, Pageable pageable);
    Long getMemberCount(Long groupId);
}