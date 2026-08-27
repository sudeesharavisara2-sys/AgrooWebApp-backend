package com.agroo.agroo.repository;

import com.agroo.agroo.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    // ============================================================
    // FIND BY POST
    // ============================================================
    Page<Comment> findByPostId(Long postId, Pageable pageable);
    List<Comment> findByPostId(Long postId);

    // ============================================================
    // FIND BY USER
    // ============================================================
    Page<Comment> findByUserId(Long userId, Pageable pageable);
    List<Comment> findByUserId(Long userId);

    // ============================================================
    // FIND TOP-LEVEL COMMENTS (No parent)
    // ============================================================
    Page<Comment> findByPostIdAndParentCommentIsNull(Long postId, Pageable pageable);
    List<Comment> findByPostIdAndParentCommentIsNull(Long postId);

    // ============================================================
    // FIND REPLIES
    // ============================================================
    Page<Comment> findByParentCommentId(Long parentCommentId, Pageable pageable);
    List<Comment> findByParentCommentId(Long parentCommentId);

    // ============================================================
    // COUNT METHODS
    // ============================================================
    Long countByPostId(Long postId);
    Long countByUserId(Long userId);
    long count();

    // ============================================================
    // DELETE METHODS
    // ============================================================
    void deleteByPostId(Long postId);
}