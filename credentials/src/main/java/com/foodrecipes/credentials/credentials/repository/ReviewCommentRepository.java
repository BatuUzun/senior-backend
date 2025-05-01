package com.foodrecipes.credentials.credentials.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.foodrecipes.credentials.credentials.entity.ReviewCommentC;
import com.foodrecipes.credentials.credentials.entity.ReviewLike;

public interface ReviewCommentRepository extends JpaRepository<ReviewCommentC, Long> {    
	@Query("""
		    SELECT rc FROM ReviewCommentC rc
		    WHERE rc.review.id = :reviewId
		    AND rc.createdAt <= :referenceTime
		    ORDER BY rc.createdAt ASC, rc.id ASC
		""")
		Page<ReviewCommentC> findByReviewIdWithReference(
		        @Param("reviewId") Long reviewId,
		        @Param("referenceTime") LocalDateTime referenceTime,
		        Pageable pageable);
	
	@Query("""
		    SELECT rc.review.id
		    FROM ReviewCommentC rc
		    WHERE rc.review.spotifyId = :spotifyId
		    GROUP BY rc.review.id
		    ORDER BY COUNT(rc.id) DESC
		    """)
		Page<Long> findTopPopularReviewIdsBySpotifyId(@Param("spotifyId") String spotifyId, Pageable pageable);

    List<ReviewCommentC> findTop11ByUserIdInAndCreatedAtLessThanEqualOrderByCreatedAtDesc(List<Long> userIds, LocalDateTime cursor);

    void deleteByUserId(Long userId);

}