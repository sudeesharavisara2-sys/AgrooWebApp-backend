package com.agroo.agroo.service;

import com.agroo.agroo.dto.request.CommentRequest;
import com.agroo.agroo.dto.response.CommentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentService {
    CommentResponse addComment(Long postId, CommentRequest request, String username);
    CommentResponse updateComment(Long commentId, CommentRequest request, String username);
    Page<CommentResponse> getCommentsByPost(Long postId, Pageable pageable);
    Page<CommentResponse> getReplies(Long commentId, Pageable pageable);
    void deleteComment(Long commentId, String username);
    Long getCommentCount(Long postId);
}