package com.agroo.agroo.repository;

import com.agroo.agroo.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    Page<Comment> findByPostId(Long postId, Pageable pageable);
    List<Comment> findByPostId(Long postId);

    Page<Comment> findByPostIdAndParentCommentIsNull(Long postId, Pageable pageable);
    Page<Comment> findByParentCommentId(Long parentCommentId, Pageable pageable);

    Long countByPostId(Long postId);
    Long countByUserId(Long userId);

    void deleteByPostId(Long postId);
}