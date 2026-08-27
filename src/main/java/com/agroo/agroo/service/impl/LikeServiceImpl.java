package com.agroo.agroo.service.impl;

import com.agroo.agroo.dto.request.LikeRequest;
import com.agroo.agroo.dto.response.LikeResponse;
import com.agroo.agroo.model.Like;
import com.agroo.agroo.model.Post;
import com.agroo.agroo.model.User;
import com.agroo.agroo.repository.LikeRepository;
import com.agroo.agroo.repository.PostRepository;
import com.agroo.agroo.repository.UserRepository;
import com.agroo.agroo.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {

    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public LikeResponse likePost(Long postId, LikeRequest request, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        // Check if already liked
        if (likeRepository.existsByUserIdAndPostId(user.getId(), postId)) {
            throw new RuntimeException("You already liked this post");
        }

        Like like = new Like();
        like.setUser(user);
        like.setPost(post);
        like.setLikeType(request.getLikeType() != null ? request.getLikeType() : com.agroo.agroo.model.enums.LikeType.LIKE);

        like = likeRepository.save(like);

        return LikeResponse.builder()
                .id(like.getId())
                .likeType(like.getLikeType())
                .user(LikeResponse.UserInfo.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .fullName(user.getFullName())
                        .build())
                .createdAt(like.getCreatedAt())
                .build();
    }

    @Override
    @Transactional
    public void unlikePost(Long postId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!likeRepository.existsByUserIdAndPostId(user.getId(), postId)) {
            throw new RuntimeException("You haven't liked this post");
        }

        likeRepository.deleteByUserIdAndPostId(user.getId(), postId);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getLikeCount(Long postId) {
        return likeRepository.countByPostId(postId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasUserLiked(Long postId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return likeRepository.existsByUserIdAndPostId(user.getId(), postId);
    }

    @Override
    @Transactional
    public void deleteAllLikes(Long postId) {
        likeRepository.deleteByPostId(postId);
    }
}