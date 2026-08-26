package com.agroo.agroo.repository;

import com.agroo.agroo.model.Like;
import com.agroo.agroo.model.enums.LikeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {

    // ============================================================
    // FIND BY USER AND POST
    // ============================================================
    Optional<Like> findByUserIdAndPostId(Long userId, Long postId);
    boolean existsByUserIdAndPostId(Long userId, Long postId);

    // ============================================================
    // COUNT METHODS
    // ============================================================
    Long countByPostId(Long postId);
    Long countByUserId(Long userId);
    Long countByPostIdAndLikeType(Long postId, LikeType likeType);

    // ============================================================
    // DELETE METHODS
    // ============================================================
    void deleteByUserIdAndPostId(Long userId, Long postId);
    void deleteByPostId(Long postId);

    // ============================================================
    // FIND BY POST
    // ============================================================
    List<Like> findByPostId(Long postId);

    // ============================================================
    // FIND BY USER
    // ============================================================
    List<Like> findByUserId(Long userId);
}