package com.comment.repository;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.comment.entity.ReviewComment;

public interface ReviewCommentRepository extends JpaRepository<ReviewComment, Long> {

    // Fetch comments by review ID with pagination
    
    
    
	@Query("""
	        SELECT rc FROM ReviewComment rc
	        WHERE rc.reviewId = :reviewId
	        AND rc.createdAt <= :referenceTime
	        ORDER BY rc.createdAt ASC, rc.id ASC
	    """)
	Page<ReviewComment> findByReviewIdWithReference(
	        @Param("reviewId") Long reviewId,
	        @Param("referenceTime") LocalDateTime referenceTime,
	        Pageable pageable);
}