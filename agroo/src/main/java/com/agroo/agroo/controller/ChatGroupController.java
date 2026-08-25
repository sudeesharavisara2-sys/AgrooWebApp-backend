package com.agroo.agroo.controller;

import com.agroo.agroo.dto.request.GroupMemberRequest;
import com.agroo.agroo.dto.request.GroupRequest;
import com.agroo.agroo.dto.response.ChatGroupResponse;
import com.agroo.agroo.dto.response.GroupMemberResponse;
import com.agroo.agroo.model.enums.MemberRole;
import com.agroo.agroo.service.ChatGroupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class ChatGroupController {

    private final ChatGroupService chatGroupService;

    // ============================================================
    // CREATE GROUP (with image upload)
    // ============================================================
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ChatGroupResponse> createGroup(
            @Valid @RequestPart("group") GroupRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image,
            Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(chatGroupService.createGroup(request, image, username));
    }

    // ============================================================
    // UPDATE GROUP (with image upload)
    // ============================================================
    @PutMapping(value = "/{groupId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ChatGroupResponse> updateGroup(
            @PathVariable Long groupId,
            @Valid @RequestPart("group") GroupRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image,
            Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(chatGroupService.updateGroup(groupId, request, image, username));
    }

    // ============================================================
    // DELETE GROUP IMAGE
    // ============================================================
    @DeleteMapping("/{groupId}/image")
    public ResponseEntity<Void> deleteGroupImage(
            @PathVariable Long groupId,
            Authentication authentication) {
        String username = authentication.getName();
        chatGroupService.deleteGroupImage(groupId, username);
        return ResponseEntity.noContent().build();
    }

    // ============================================================
    // GET GROUP
    // ============================================================
    @GetMapping("/{groupId}")
    public ResponseEntity<ChatGroupResponse> getGroup(
            @PathVariable Long groupId,
            Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(chatGroupService.getGroup(groupId, username));
    }

    // ============================================================
    // GET USER GROUPS
    // ============================================================
    @GetMapping
    public ResponseEntity<Page<ChatGroupResponse>> getUserGroups(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        String username = authentication.getName();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(chatGroupService.getUserGroups(username, pageable));
    }

    // ============================================================
    // SEARCH GROUPS
    // ============================================================
    @GetMapping("/search")
    public ResponseEntity<Page<ChatGroupResponse>> searchGroups(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        return ResponseEntity.ok(chatGroupService.searchGroups(keyword, pageable));
    }

    // ============================================================
    // ADD MEMBER
    // ============================================================
    @PostMapping("/{groupId}/members")
    public ResponseEntity<GroupMemberResponse> addMember(
            @PathVariable Long groupId,
            @Valid @RequestBody GroupMemberRequest request,
            Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(chatGroupService.addMember(groupId, request, username));
    }

    // ============================================================
    // ADD MEMBER BY EMAIL
    // ============================================================
    @PostMapping("/{groupId}/members/email")
    public ResponseEntity<GroupMemberResponse> addMemberByEmail(
            @PathVariable Long groupId,
            @RequestParam String email,
            Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(chatGroupService.addMemberByEmail(groupId, email, username));
    }

    // ============================================================
    // REMOVE MEMBER
    // ============================================================
    @DeleteMapping("/{groupId}/members/{userId}")
    public ResponseEntity<Void> removeMember(
            @PathVariable Long groupId,
            @PathVariable Long userId,
            Authentication authentication) {
        String username = authentication.getName();
        chatGroupService.removeMember(groupId, userId, username);
        return ResponseEntity.noContent().build();
    }

    // ============================================================
    // MAKE ADMIN
    // ============================================================
    @PatchMapping("/{groupId}/members/{userId}/make-admin")
    public ResponseEntity<GroupMemberResponse> makeAdmin(
            @PathVariable Long groupId,
            @PathVariable Long userId,
            Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(chatGroupService.makeAdmin(groupId, userId, username));
    }

    // ============================================================
    // REMOVE ADMIN
    // ============================================================
    @PatchMapping("/{groupId}/members/{userId}/remove-admin")
    public ResponseEntity<GroupMemberResponse> removeAdmin(
            @PathVariable Long groupId,
            @PathVariable Long userId,
            Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(chatGroupService.removeAdmin(groupId, userId, username));
    }

    // ============================================================
    // GET GROUP MEMBERS
    // ============================================================
    @GetMapping("/{groupId}/members")
    public ResponseEntity<Page<GroupMemberResponse>> getGroupMembers(
            @PathVariable Long groupId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        String username = authentication.getName();
        Pageable pageable = PageRequest.of(page, size, Sort.by("joinedAt").ascending());
        return ResponseEntity.ok(chatGroupService.getGroupMembers(groupId, pageable));
    }

    // ============================================================
    // LEAVE GROUP
    // ============================================================
    @PostMapping("/{groupId}/leave")
    public ResponseEntity<Void> leaveGroup(
            @PathVariable Long groupId,
            Authentication authentication) {
        String username = authentication.getName();
        chatGroupService.leaveGroup(groupId, username);
        return ResponseEntity.noContent().build();
    }

    // ============================================================
    // DELETE GROUP
    // ============================================================
    @DeleteMapping("/{groupId}")
    public ResponseEntity<Void> deleteGroup(
            @PathVariable Long groupId,
            Authentication authentication) {
        String username = authentication.getName();
        chatGroupService.deleteGroup(groupId, username);
        return ResponseEntity.noContent().build();
    }

    // ============================================================
    // CHECK ADMIN STATUS
    // ============================================================
    @GetMapping("/{groupId}/is-admin")
    public ResponseEntity<Boolean> isAdmin(
            @PathVariable Long groupId,
            Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(chatGroupService.isAdmin(groupId, username));
    }
}