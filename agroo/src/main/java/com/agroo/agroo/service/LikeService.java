package com.agroo.agroo.service;

import com.agroo.agroo.dto.request.LikeRequest;
import com.agroo.agroo.dto.response.LikeResponse;

public interface LikeService {
    LikeResponse likePost(Long postId, LikeRequest request, String username);
    void unlikePost(Long postId, String username);
    Long getLikeCount(Long postId);
    boolean hasUserLiked(Long postId, String username);
    void deleteAllLikes(Long postId);
}