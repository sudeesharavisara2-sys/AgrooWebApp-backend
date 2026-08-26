package com.agroo.agroo.service.impl;

import com.agroo.agroo.dto.response.GroupMemberResponse;
import com.agroo.agroo.model.ChatGroup;
import com.agroo.agroo.model.GroupMember;
import com.agroo.agroo.model.User;
import com.agroo.agroo.model.enums.MemberRole;
import com.agroo.agroo.repository.ChatGroupRepository;
import com.agroo.agroo.repository.GroupMemberRepository;
import com.agroo.agroo.repository.UserRepository;
import com.agroo.agroo.service.GroupMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GroupMemberServiceImpl implements GroupMemberService {

    private final GroupMemberRepository groupMemberRepository;
    private final ChatGroupRepository chatGroupRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public boolean isMember(Long groupId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return groupMemberRepository.existsByChatGroupIdAndUserId(groupId, user.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isAdmin(Long groupId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        GroupMember member = groupMemberRepository.findByChatGroupIdAndUserId(groupId, user.getId())
                .orElse(null);

        return member != null && member.getRole() == MemberRole.ADMIN;
    }

    @Override
    @Transactional
    public GroupMemberResponse addMember(Long groupId, Long userId, String username) {
        User admin = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if admin
        if (!isAdmin(groupId, username)) {
            throw new AccessDeniedException("Only admins can add members");
        }

        ChatGroup group = chatGroupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        User newMember = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if already member
        if (groupMemberRepository.existsByChatGroupIdAndUserId(groupId, userId)) {
            throw new RuntimeException("User is already a member");
        }

        GroupMember groupMember = new GroupMember();
        groupMember.setUser(newMember);
        groupMember.setChatGroup(group);
        groupMember.setRole(MemberRole.MEMBER);

        groupMember = groupMemberRepository.save(groupMember);
        return mapToResponse(groupMember);
    }

    @Override
    @Transactional
    public GroupMemberResponse removeMember(Long groupId, Long userId, String username) {
        User admin = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if admin
        if (!isAdmin(groupId, username)) {
            throw new AccessDeniedException("Only admins can remove members");
        }

        ChatGroup group = chatGroupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        // Cannot remove creator
        if (group.getCreatedBy().getId().equals(userId)) {
            throw new RuntimeException("Cannot remove the group creator");
        }

        GroupMember member = groupMemberRepository.findByChatGroupIdAndUserId(groupId, userId)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        groupMemberRepository.delete(member);
        return mapToResponse(member);
    }

    @Override
    @Transactional
    public GroupMemberResponse changeRole(Long groupId, Long userId, MemberRole role, String username) {
        User admin = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if admin
        if (!isAdmin(groupId, username)) {
            throw new AccessDeniedException("Only admins can change member roles");
        }

        ChatGroup group = chatGroupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        // Cannot change creator role
        if (group.getCreatedBy().getId().equals(userId)) {
            throw new RuntimeException("Cannot change the group creator's role");
        }

        GroupMember member = groupMemberRepository.findByChatGroupIdAndUserId(groupId, userId)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        member.setRole(role);
        member = groupMemberRepository.save(member);
        return mapToResponse(member);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<GroupMemberResponse> getGroupMembers(Long groupId, Pageable pageable) {
        return groupMemberRepository.findByChatGroupId(groupId, pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getMemberCount(Long groupId) {
        return groupMemberRepository.countByChatGroupId(groupId);
    }

    // Helper method
    private GroupMemberResponse mapToResponse(GroupMember member) {
        return GroupMemberResponse.builder()
                .id(member.getId())
                .role(member.getRole())
                .isActive(member.getIsActive())
                .joinedAt(member.getJoinedAt())
                .user(GroupMemberResponse.UserInfo.builder()
                        .id(member.getUser().getId())
                        .username(member.getUser().getUsername())
                        .fullName(member.getUser().getFullName())
                        .email(member.getUser().getEmail())
                        .profileImageUrl(member.getUser().getProfileImageUrl())
                        .phoneNumber(member.getUser().getPhoneNumber())
                        .build())
                .build();
    }
}