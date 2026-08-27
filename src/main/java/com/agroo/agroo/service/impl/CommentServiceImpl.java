package com.agroo.agroo.service.impl;

import com.agroo.agroo.dto.request.CommentRequest;
import com.agroo.agroo.dto.response.CommentResponse;
import com.agroo.agroo.model.Comment;
import com.agroo.agroo.model.Post;
import com.agroo.agroo.model.User;
import com.agroo.agroo.repository.CommentRepository;
import com.agroo.agroo.repository.PostRepository;
import com.agroo.agroo.repository.UserRepository;
import com.agroo.agroo.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public CommentResponse addComment(Long postId, CommentRequest request, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        Comment comment = new Comment();
        comment.setContent(request.getContent());
        comment.setUser(user);
        comment.setPost(post);

        // Handle reply
        if (request.getParentCommentId() != null) {
            Comment parent = commentRepository.findById(request.getParentCommentId())
                    .orElseThrow(() -> new RuntimeException("Parent comment not found"));
            comment.setParentComment(parent);
        }

        comment = commentRepository.save(comment);
        return mapToResponse(comment);
    }

    @Override
    @Transactional
    public CommentResponse updateComment(Long commentId, CommentRequest request, String username) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        if (!comment.getUser().getUsername().equals(username)) {
            throw new AccessDeniedException("You don't have permission to update this comment");
        }

        comment.setContent(request.getContent());
        comment = commentRepository.save(comment);
        return mapToResponse(comment);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CommentResponse> getCommentsByPost(Long postId, Pageable pageable) {
        return commentRepository.findByPostIdAndParentCommentIsNull(postId, pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CommentResponse> getReplies(Long commentId, Pageable pageable) {
        return commentRepository.findByParentCommentId(commentId, pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId, String username) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        if (!comment.getUser().getUsername().equals(username)) {
            throw new AccessDeniedException("You don't have permission to delete this comment");
        }

        commentRepository.delete(comment);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getCommentCount(Long postId) {
        return commentRepository.countByPostId(postId);
    }

    private CommentResponse mapToResponse(Comment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .user(CommentResponse.UserInfo.builder()
                        .id(comment.getUser().getId())
                        .username(comment.getUser().getUsername())
                        .fullName(comment.getUser().getFullName())
                        .profileImageUrl(comment.getUser().getProfileImageUrl())
                        .build())
                .parentCommentId(comment.getParentComment() != null ? comment.getParentComment().getId() : null)
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }
}