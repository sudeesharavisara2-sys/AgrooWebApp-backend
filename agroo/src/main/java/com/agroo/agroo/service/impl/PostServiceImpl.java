package com.agroo.agroo.service.impl;

import com.agroo.agroo.dto.request.PostRequest;
import com.agroo.agroo.dto.response.CommentResponse;
import com.agroo.agroo.dto.response.PostResponse;
import com.agroo.agroo.model.Post;
import com.agroo.agroo.model.User;
import com.agroo.agroo.repository.CommentRepository;
import com.agroo.agroo.repository.LikeRepository;
import com.agroo.agroo.repository.PostRepository;
import com.agroo.agroo.repository.UserRepository;
import com.agroo.agroo.service.FileStorageService;
import com.agroo.agroo.service.PostService;
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
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;
    private final FileStorageService fileStorageService;

    private static final List<String> ALLOWED_IMAGE_TYPES = List.of("image/jpeg", "image/png", "image/jpg", "image/gif", "image/webp");
    private static final List<String> ALLOWED_VIDEO_TYPES = List.of("video/mp4", "video/webm", "video/ogg");

    @Override
    @Transactional
    public PostResponse createPost(PostRequest request, MultipartFile media, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Post post = new Post();
        post.setContent(request.getContent());
        post.setIsPublic(request.getIsPublic() != null ? request.getIsPublic() : true);
        post.setUser(user);

        // Handle media upload
        if (media != null && !media.isEmpty()) {
            String mediaUrl = fileStorageService.storeFile(media);
            String contentType = media.getContentType();

            if (isImage(contentType)) {
                post.setImageUrl(mediaUrl);
                post.setMediaType("IMAGE");
            } else if (isVideo(contentType)) {
                post.setVideoUrl(mediaUrl);
                post.setMediaType("VIDEO");
            }
        } else {
            post.setMediaType("NONE");
        }

        post = postRepository.save(post);
        return mapToResponse(post, username);
    }

    @Override
    @Transactional
    public PostResponse updatePost(Long postId, PostRequest request, MultipartFile media, String username) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        if (!post.getUser().getUsername().equals(username)) {
            throw new AccessDeniedException("You don't have permission to update this post");
        }

        post.setContent(request.getContent());
        post.setIsPublic(request.getIsPublic() != null ? request.getIsPublic() : true);

        // Handle new media upload
        if (media != null && !media.isEmpty()) {
            // Delete old media if exists
            if (post.getImageUrl() != null) {
                fileStorageService.deleteFile(post.getImageUrl());
            }
            if (post.getVideoUrl() != null) {
                fileStorageService.deleteFile(post.getVideoUrl());
            }

            String mediaUrl = fileStorageService.storeFile(media);
            String contentType = media.getContentType();

            if (isImage(contentType)) {
                post.setImageUrl(mediaUrl);
                post.setVideoUrl(null);
                post.setMediaType("IMAGE");
            } else if (isVideo(contentType)) {
                post.setVideoUrl(mediaUrl);
                post.setImageUrl(null);
                post.setMediaType("VIDEO");
            }
        }

        post = postRepository.save(post);
        return mapToResponse(post, username);
    }

    @Override
    @Transactional(readOnly = true)
    public PostResponse getPost(Long postId, String username) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        // Increment view count
        post.setViewCount(post.getViewCount() + 1);
        postRepository.save(post);

        return mapToResponse(post, username);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PostResponse> getAllPosts(Pageable pageable, String username) {
        return postRepository.findRecentPosts(pageable)
                .map(post -> mapToResponse(post, username));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PostResponse> getPostsByUser(Long userId, Pageable pageable, String username) {
        return postRepository.findByUserId(userId, pageable)
                .map(post -> mapToResponse(post, username));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PostResponse> searchPosts(String keyword, Pageable pageable) {
        return postRepository.searchByContent(keyword, pageable)
                .map(post -> mapToResponse(post, null));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PostResponse> getFeed(String username, Pageable pageable) {
        return postRepository.findByIsPublicTrue(pageable)
                .map(post -> mapToResponse(post, username));
    }

    @Override
    @Transactional
    public void deletePost(Long postId, String username) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        if (!post.getUser().getUsername().equals(username)) {
            throw new AccessDeniedException("You don't have permission to delete this post");
        }

        // Delete media files
        if (post.getImageUrl() != null) {
            fileStorageService.deleteFile(post.getImageUrl());
        }
        if (post.getVideoUrl() != null) {
            fileStorageService.deleteFile(post.getVideoUrl());
        }

        postRepository.delete(post);
    }

    @Override
    @Transactional
    public PostResponse toggleVisibility(Long postId, String username) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        if (!post.getUser().getUsername().equals(username)) {
            throw new AccessDeniedException("You don't have permission to modify this post");
        }

        post.setIsPublic(!post.getIsPublic());
        post = postRepository.save(post);
        return mapToResponse(post, username);
    }

    @Override
    @Transactional
    public void deleteMedia(Long postId, String username) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        if (!post.getUser().getUsername().equals(username)) {
            throw new AccessDeniedException("You don't have permission to delete this media");
        }

        if (post.getImageUrl() != null) {
            fileStorageService.deleteFile(post.getImageUrl());
            post.setImageUrl(null);
        }
        if (post.getVideoUrl() != null) {
            fileStorageService.deleteFile(post.getVideoUrl());
            post.setVideoUrl(null);
        }
        post.setMediaType("NONE");
        postRepository.save(post);
    }

    // ============================================================
    // HELPER METHODS
    // ============================================================
    private boolean isImage(String contentType) {
        return contentType != null && ALLOWED_IMAGE_TYPES.contains(contentType.toLowerCase());
    }

    private boolean isVideo(String contentType) {
        return contentType != null && ALLOWED_VIDEO_TYPES.contains(contentType.toLowerCase());
    }

    private PostResponse mapToResponse(Post post, String currentUsername) {
        Long userId = currentUsername != null ?
                userRepository.findByUsername(currentUsername).map(User::getId).orElse(null) : null;

        boolean userLiked = userId != null && likeRepository.existsByUserIdAndPostId(userId, post.getId());

        // Get top 3 comments
        List<CommentResponse> topComments = commentRepository
                .findByPostIdAndParentCommentIsNull(post.getId(), Pageable.ofSize(3))
                .stream()
                .map(this::mapCommentToResponse)
                .collect(Collectors.toList());

        // Determine media type for response
        String mediaUrl = post.getImageUrl() != null ? post.getImageUrl() : post.getVideoUrl();
        String mediaType = post.getMediaType();

        return PostResponse.builder()
                .id(post.getId())
                .content(post.getContent())
                .imageUrl(post.getImageUrl())
                .videoUrl(post.getVideoUrl())
                .mediaUrl(mediaUrl)
                .mediaType(mediaType)
                .isPublic(post.getIsPublic())
                .viewCount(post.getViewCount())
                .shareCount(post.getShareCount())
                .commentCount(commentRepository.countByPostId(post.getId()))
                .likeCount(likeRepository.countByPostId(post.getId()))
                .userLiked(userLiked)
                .user(PostResponse.UserInfo.builder()
                        .id(post.getUser().getId())
                        .username(post.getUser().getUsername())
                        .fullName(post.getUser().getFullName())
                        .profileImageUrl(post.getUser().getProfileImageUrl())
                        .build())
                .comments(topComments)
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }

    private CommentResponse mapCommentToResponse(com.agroo.agroo.model.Comment comment) {
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