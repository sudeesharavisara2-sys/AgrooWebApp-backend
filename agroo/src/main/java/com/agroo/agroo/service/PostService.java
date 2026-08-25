package com.agroo.agroo.service;

import com.agroo.agroo.dto.request.PostRequest;
import com.agroo.agroo.dto.response.PostResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface PostService {
    PostResponse createPost(PostRequest request, MultipartFile media, String username);
    PostResponse updatePost(Long postId, PostRequest request, MultipartFile media, String username);
    PostResponse getPost(Long postId, String username);
    Page<PostResponse> getAllPosts(Pageable pageable, String username);
    Page<PostResponse> getPostsByUser(Long userId, Pageable pageable, String username);
    Page<PostResponse> searchPosts(String keyword, Pageable pageable);
    Page<PostResponse> getFeed(String username, Pageable pageable);
    void deletePost(Long postId, String username);
    PostResponse toggleVisibility(Long postId, String username);
    void deleteMedia(Long postId, String username);
}