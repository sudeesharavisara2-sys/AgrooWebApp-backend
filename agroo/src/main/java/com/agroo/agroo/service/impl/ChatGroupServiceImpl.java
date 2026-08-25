package com.agroo.agroo.service.impl;

import com.agroo.agroo.dto.request.GroupMemberRequest;
import com.agroo.agroo.dto.request.GroupRequest;
import com.agroo.agroo.dto.response.ChatGroupResponse;
import com.agroo.agroo.dto.response.GroupMemberResponse;
import com.agroo.agroo.model.ChatGroup;
import com.agroo.agroo.model.GroupMember;
import com.agroo.agroo.model.User;
import com.agroo.agroo.model.enums.MemberRole;
import com.agroo.agroo.repository.ChatGroupRepository;
import com.agroo.agroo.repository.GroupMemberRepository;
import com.agroo.agroo.repository.UserRepository;
import com.agroo.agroo.service.ChatGroupService;
import com.agroo.agroo.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatGroupServiceImpl implements ChatGroupService {

    private final ChatGroupRepository chatGroupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;

    private static final List<String> ALLOWED_IMAGE_TYPES = List.of("image/jpeg", "image/png", "image/jpg", "image/gif", "image/webp");

    @Override
    @Transactional
    public ChatGroupResponse createGroup(GroupRequest request, MultipartFile image, String username) {
        User creator = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ChatGroup group = new ChatGroup();
        group.setName(request.getName());
        group.setDescription(request.getDescription());
        group.setCreatedBy(creator);

        // Handle image upload
        if (image != null && !image.isEmpty()) {
            validateImage(image);
            String imageUrl = fileStorageService.storeFile(image);
            group.setImageUrl(imageUrl);
        }

        group = chatGroupRepository.save(group);

        // Add creator as ADMIN
        GroupMember adminMember = new GroupMember();
        adminMember.setUser(creator);
        adminMember.setChatGroup(group);
        adminMember.setRole(MemberRole.ADMIN);
        groupMemberRepository.save(adminMember);

        // Add initial members if specified
        if (request.getMemberIds() != null && !request.getMemberIds().isEmpty()) {
            for (Long userId : request.getMemberIds()) {
                if (!userId.equals(creator.getId())) {
                    User member = userRepository.findById(userId)
                            .orElseThrow(() -> new RuntimeException("User not found: " + userId));
                    GroupMember groupMember = new GroupMember();
                    groupMember.setUser(member);
                    groupMember.setChatGroup(group);
                    groupMember.setRole(MemberRole.MEMBER);
                    groupMemberRepository.save(groupMember);
                }
            }
        }

        return mapToResponse(group, creator.getId());
    }

    @Override
    @Transactional
    public ChatGroupResponse updateGroup(Long groupId, GroupRequest request, MultipartFile image, String username) {
        ChatGroup group = chatGroupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        // Check if user is admin
        if (!isAdmin(groupId, username)) {
            throw new AccessDeniedException("Only admins can update the group");
        }

        group.setName(request.getName());
        group.setDescription(request.getDescription());

        // Handle image upload
        if (image != null && !image.isEmpty()) {
            // Delete old image if exists
            if (group.getImageUrl() != null) {
                fileStorageService.deleteFile(group.getImageUrl());
            }
            validateImage(image);
            String imageUrl = fileStorageService.storeFile(image);
            group.setImageUrl(imageUrl);
        }

        group = chatGroupRepository.save(group);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return mapToResponse(group, user.getId());
    }

    @Override
    @Transactional
    public void deleteGroupImage(Long groupId, String username) {
        ChatGroup group = chatGroupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        if (!isAdmin(groupId, username)) {
            throw new AccessDeniedException("Only admins can delete group image");
        }

        if (group.getImageUrl() != null) {
            fileStorageService.deleteFile(group.getImageUrl());
            group.setImageUrl(null);
            chatGroupRepository.save(group);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ChatGroupResponse getGroup(Long groupId, String username) {
        ChatGroup group = chatGroupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!isMember(groupId, username)) {
            throw new AccessDeniedException("You are not a member of this group");
        }

        return mapToResponse(group, user.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ChatGroupResponse> getUserGroups(String username, Pageable pageable) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return chatGroupRepository.findGroupsByUserId(user.getId(), pageable)
                .map(group -> mapToResponse(group, user.getId()));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ChatGroupResponse> searchGroups(String keyword, Pageable pageable) {
        return chatGroupRepository.searchByName(keyword, pageable)
                .map(group -> mapToResponse(group, null));
    }

    @Override
    @Transactional
    public GroupMemberResponse addMember(Long groupId, GroupMemberRequest request, String username) {
        if (!isAdmin(groupId, username)) {
            throw new AccessDeniedException("Only admins can add members");
        }

        ChatGroup group = chatGroupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        User newMember = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (groupMemberRepository.existsByChatGroupIdAndUserId(groupId, newMember.getId())) {
            throw new RuntimeException("User is already a member");
        }

        GroupMember groupMember = new GroupMember();
        groupMember.setUser(newMember);
        groupMember.setChatGroup(group);
        groupMember.setRole(MemberRole.MEMBER);

        groupMember = groupMemberRepository.save(groupMember);
        return mapMemberToResponse(groupMember);
    }

    @Override
    @Transactional
    public GroupMemberResponse addMemberByEmail(Long groupId, String email, String username) {
        if (!isAdmin(groupId, username)) {
            throw new AccessDeniedException("Only admins can add members");
        }

        User newMember = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        GroupMemberRequest request = new GroupMemberRequest();
        request.setUserId(newMember.getId());

        return addMember(groupId, request, username);
    }

    @Override
    @Transactional
    public GroupMemberResponse removeMember(Long groupId, Long userId, String username) {
        if (!isAdmin(groupId, username)) {
            throw new AccessDeniedException("Only admins can remove members");
        }

        ChatGroup group = chatGroupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        if (group.getCreatedBy().getId().equals(userId)) {
            throw new RuntimeException("Cannot remove the group creator");
        }

        GroupMember member = groupMemberRepository.findByChatGroupIdAndUserId(groupId, userId)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        groupMemberRepository.delete(member);
        return mapMemberToResponse(member);
    }

    @Override
    @Transactional
    public GroupMemberResponse makeAdmin(Long groupId, Long userId, String username) {
        if (!isAdmin(groupId, username)) {
            throw new AccessDeniedException("Only admins can make other admins");
        }

        ChatGroup group = chatGroupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        if (group.getCreatedBy().getId().equals(userId)) {
            throw new RuntimeException("Cannot change the group creator's role");
        }

        GroupMember member = groupMemberRepository.findByChatGroupIdAndUserId(groupId, userId)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        member.setRole(MemberRole.ADMIN);
        member = groupMemberRepository.save(member);
        return mapMemberToResponse(member);
    }

    @Override
    @Transactional
    public GroupMemberResponse removeAdmin(Long groupId, Long userId, String username) {
        if (!isAdmin(groupId, username)) {
            throw new AccessDeniedException("Only admins can remove admin status");
        }

        ChatGroup group = chatGroupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        if (group.getCreatedBy().getId().equals(userId)) {
            throw new RuntimeException("Cannot change the group creator's role");
        }

        GroupMember member = groupMemberRepository.findByChatGroupIdAndUserId(groupId, userId)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        member.setRole(MemberRole.MEMBER);
        member = groupMemberRepository.save(member);
        return mapMemberToResponse(member);
    }

    @Override
    @Transactional
    public void deleteGroup(Long groupId, String username) {
        ChatGroup group = chatGroupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        if (!group.getCreatedBy().getUsername().equals(username)) {
            throw new AccessDeniedException("Only the group creator can delete the group");
        }

        // Delete group image if exists
        if (group.getImageUrl() != null) {
            fileStorageService.deleteFile(group.getImageUrl());
        }

        group.setIsActive(false);
        chatGroupRepository.save(group);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<GroupMemberResponse> getGroupMembers(Long groupId, Pageable pageable) {
        return groupMemberRepository.findByChatGroupId(groupId, pageable)
                .map(this::mapMemberToResponse);
    }

    @Override
    @Transactional
    public void leaveGroup(Long groupId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ChatGroup group = chatGroupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        if (group.getCreatedBy().getId().equals(user.getId())) {
            throw new RuntimeException("Group creator cannot leave. Delete the group instead.");
        }

        GroupMember member = groupMemberRepository.findByChatGroupIdAndUserId(groupId, user.getId())
                .orElseThrow(() -> new RuntimeException("You are not a member of this group"));

        groupMemberRepository.delete(member);
    }

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

    // ============================================================
    // HELPER METHODS
    // ============================================================
    private void validateImage(MultipartFile image) {
        String contentType = image.getContentType();
        if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType.toLowerCase())) {
            throw new RuntimeException("Invalid image format. Allowed: JPG, PNG, GIF, WEBP");
        }
        if (image.getSize() > 5 * 1024 * 1024) { // 5MB
            throw new RuntimeException("Image size too large. Max: 5MB");
        }
    }

    private ChatGroupResponse mapToResponse(ChatGroup group, Long currentUserId) {
        Long memberCount = groupMemberRepository.countByChatGroupId(group.getId());

        boolean isAdmin = currentUserId != null &&
                groupMemberRepository.findByChatGroupIdAndUserId(group.getId(), currentUserId)
                        .map(m -> m.getRole() == MemberRole.ADMIN)
                        .orElse(false);

        List<GroupMemberResponse> memberResponses = groupMemberRepository.findByChatGroupId(group.getId())
                .stream()
                .limit(5)
                .map(this::mapMemberToResponse)
                .collect(Collectors.toList());

        return ChatGroupResponse.builder()
                .id(group.getId())
                .name(group.getName())
                .description(group.getDescription())
                .imageUrl(group.getImageUrl())
                .isActive(group.getIsActive())
                .memberCount(memberCount)
                .isAdmin(isAdmin)
                .createdBy(ChatGroupResponse.UserInfo.builder()
                        .id(group.getCreatedBy().getId())
                        .username(group.getCreatedBy().getUsername())
                        .fullName(group.getCreatedBy().getFullName())
                        .profileImageUrl(group.getCreatedBy().getProfileImageUrl())
                        .build())
                .members(memberResponses)
                .createdAt(group.getCreatedAt())
                .updatedAt(group.getUpdatedAt())
                .build();
    }

    private GroupMemberResponse mapMemberToResponse(GroupMember member) {
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