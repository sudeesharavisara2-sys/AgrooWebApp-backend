package com.agroo.agroo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostResponse {
    private Long id;
    private String content;
    private String imageUrl;
    private String videoUrl;
    private String mediaUrl;      // Combined for display
    private String mediaType;     // IMAGE, VIDEO, NONE
    private Boolean isPublic;
    private Integer viewCount;
    private Integer shareCount;
    private Long commentCount;
    private Long likeCount;
    private Boolean userLiked;
    private UserInfo user;
    private List<CommentResponse> comments;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfo {
        private Long id;
        private String username;
        private String fullName;
        private String profileImageUrl;
    }
}