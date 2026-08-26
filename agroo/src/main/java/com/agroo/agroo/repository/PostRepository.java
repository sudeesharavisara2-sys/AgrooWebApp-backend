package com.agroo.agroo.repository;

import com.agroo.agroo.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    // ============================================================
    // FIND BY USER
    // ============================================================
    Page<Post> findByUserId(Long userId, Pageable pageable);
    List<Post> findByUserId(Long userId);

    // ============================================================
    // FIND PUBLIC POSTS
    // ============================================================
    Page<Post> findByIsPublicTrue(Pageable pageable);
    List<Post> findByIsPublicTrue();

    // ============================================================
    // FIND RECENT POSTS
    // ============================================================
    @Query("SELECT p FROM Post p ORDER BY p.createdAt DESC")
    Page<Post> findRecentPosts(Pageable pageable);

    @Query("SELECT p FROM Post p ORDER BY p.createdAt DESC")
    List<Post> findRecentPosts();

    // ============================================================
    // SEARCH BY CONTENT
    // ============================================================
    @Query("SELECT p FROM Post p WHERE LOWER(p.content) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Post> searchByContent(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE LOWER(p.content) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Post> searchByContent(@Param("keyword") String keyword);

    // ============================================================
    // COUNT METHODS
    // ============================================================
    Long countByUserId(Long userId);
    long countByIsPublicTrue();
    long count();

    // ============================================================
    // FIND MOST POPULAR POSTS
    // ============================================================
    @Query("SELECT p FROM Post p ORDER BY SIZE(p.comments) DESC")
    Page<Post> findMostCommentedPosts(Pageable pageable);

    @Query("SELECT p FROM Post p ORDER BY SIZE(p.likes) DESC")
    Page<Post> findMostLikedPosts(Pageable pageable);

    // ============================================================
    // USER FEED
    // ============================================================
    @Query("SELECT p FROM Post p WHERE p.user.id IN :userIds ORDER BY p.createdAt DESC")
    Page<Post> findUserFeed(@Param("userIds") List<Long> userIds, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.user.id IN :userIds ORDER BY p.createdAt DESC")
    List<Post> findUserFeed(@Param("userIds") List<Long> userIds);

    // ============================================================
    // FIND POSTS WITH IMAGES
    // ============================================================
    @Query("SELECT p FROM Post p WHERE p.imageUrl IS NOT NULL AND p.imageUrl != ''")
    Page<Post> findPostsWithImages(Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.videoUrl IS NOT NULL AND p.videoUrl != ''")
    Page<Post> findPostsWithVideos(Pageable pageable);
}