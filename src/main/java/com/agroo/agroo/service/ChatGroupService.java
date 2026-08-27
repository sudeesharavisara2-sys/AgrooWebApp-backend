package com.agroo.agroo.service;

import com.agroo.agroo.dto.request.GroupMemberRequest;
import com.agroo.agroo.dto.request.GroupRequest;
import com.agroo.agroo.dto.response.ChatGroupResponse;
import com.agroo.agroo.dto.response.GroupMemberResponse;
import com.agroo.agroo.model.enums.MemberRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface ChatGroupService {
    ChatGroupResponse createGroup(GroupRequest request, MultipartFile image, String username);
    ChatGroupResponse updateGroup(Long groupId, GroupRequest request, MultipartFile image, String username);
    void deleteGroupImage(Long groupId, String username);
    ChatGroupResponse getGroup(Long groupId, String username);
    Page<ChatGroupResponse> getUserGroups(String username, Pageable pageable);
    Page<ChatGroupResponse> searchGroups(String keyword, Pageable pageable);
    GroupMemberResponse addMember(Long groupId, GroupMemberRequest request, String username);
    GroupMemberResponse addMemberByEmail(Long groupId, String email, String username);
    GroupMemberResponse removeMember(Long groupId, Long userId, String username);
    GroupMemberResponse makeAdmin(Long groupId, Long userId, String username);
    GroupMemberResponse removeAdmin(Long groupId, Long userId, String username);
    void deleteGroup(Long groupId, String username);
    Page<GroupMemberResponse> getGroupMembers(Long groupId, Pageable pageable);
    void leaveGroup(Long groupId, String username);
    boolean isMember(Long groupId, String username);
    boolean isAdmin(Long groupId, String username);
}