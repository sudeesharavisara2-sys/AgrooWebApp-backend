package com.agroo.agroo.dto.response;

import com.agroo.agroo.model.enums.MemberRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupMemberResponse {
    private Long id;
    private MemberRole role;
    private Boolean isActive;
    private UserInfo user;
    private LocalDateTime joinedAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfo {
        private Long id;
        private String username;
        private String fullName;
        private String email;
        private String profileImageUrl;
        private String phoneNumber;
    }
}