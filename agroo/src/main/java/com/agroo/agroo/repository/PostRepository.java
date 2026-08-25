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

    // Find posts by user
    Page<Post> findByUserId(Long userId, Pageable pageable);
    List<Post> findByUserId(Long userId);

    // Find public posts
    Page<Post> findByIsPublicTrue(Pageable pageable);

    // Find recent posts with pagination
    @Query("SELECT p FROM Post p ORDER BY p.createdAt DESC")
    Page<Post> findRecentPosts(Pageable pageable);

    // Search posts by content
    @Query("SELECT p FROM Post p WHERE LOWER(p.content) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Post> searchByContent(@Param("keyword") String keyword, Pageable pageable);

    // Count posts by user
    Long countByUserId(Long userId);

    // Find posts with most comments
    @Query("SELECT p FROM Post p ORDER BY SIZE(p.comments) DESC")
    Page<Post> findMostCommentedPosts(Pageable pageable);

    // Find posts with most likes
    @Query("SELECT p FROM Post p ORDER BY SIZE(p.likes) DESC")
    Page<Post> findMostLikedPosts(Pageable pageable);

    // Get user feed (posts from users a specific user follows - simplified)
    @Query("SELECT p FROM Post p WHERE p.user.id IN :userIds ORDER BY p.createdAt DESC")
    Page<Post> findUserFeed(@Param("userIds") List<Long> userIds, Pageable pageable);
}