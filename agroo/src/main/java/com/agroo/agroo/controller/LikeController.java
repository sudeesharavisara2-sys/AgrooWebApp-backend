package com.agroo.agroo.controller;

import com.agroo.agroo.dto.request.LikeRequest;
import com.agroo.agroo.dto.response.LikeResponse;
import com.agroo.agroo.service.LikeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/likes")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    // Like a post
    @PostMapping("/post/{postId}")
    public ResponseEntity<LikeResponse> likePost(
            @PathVariable Long postId,
            @Valid @RequestBody(required = false) LikeRequest request,
            Authentication authentication) {
        String username = authentication.getName();
        if (request == null) {
            request = new LikeRequest();
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(likeService.likePost(postId, request, username));
    }

    // Unlike a post
    @DeleteMapping("/post/{postId}")
    public ResponseEntity<Void> unlikePost(
            @PathVariable Long postId,
            Authentication authentication) {
        String username = authentication.getName();
        likeService.unlikePost(postId, username);
        return ResponseEntity.noContent().build();
    }

    // Get like count
    @GetMapping("/post/{postId}/count")
    public ResponseEntity<Map<String, Long>> getLikeCount(@PathVariable Long postId) {
        Map<String, Long> response = new HashMap<>();
        response.put("likeCount", likeService.getLikeCount(postId));
        return ResponseEntity.ok(response);
    }

    // Check if user liked
    @GetMapping("/post/{postId}/check")
    public ResponseEntity<Map<String, Boolean>> hasUserLiked(
            @PathVariable Long postId,
            Authentication authentication) {
        String username = authentication.getName();
        Map<String, Boolean> response = new HashMap<>();
        response.put("liked", likeService.hasUserLiked(postId, username));
        return ResponseEntity.ok(response);
    }
}