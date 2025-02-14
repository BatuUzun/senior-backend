package com.review.entity.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.review.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    Page<Review> findByUserId(Long userId, Pageable pageable);

    Page<Review> findBySpotifyId(String spotifyId, Pageable pageable);
    
    @Query("""
    	    SELECT r FROM Review r 
    	    WHERE r.spotifyId = :spotifyId 
    	    AND r.createdAt <= :referenceTime 
    	    ORDER BY r.createdAt DESC, r.id DESC
    	""")
    	Page<Review> findBySpotifyIdWithReference(
    	        @Param("spotifyId") String spotifyId,
    	        @Param("referenceTime") LocalDateTime referenceTime,
    	        Pageable pageable);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.spotifyId = :spotifyId")
    Double findAverageRatingBySpotifyId(String spotifyId);
    
    Optional<Review> findByUserIdAndSpotifyId(Long userId, String spotifyId);

    long countByUserId(Long userId);
    
    @Query("""
    	    SELECT r.spotifyId, 
    	           r.id, 
    	           COALESCE(COUNT(DISTINCT rl.id), 0) AS likeCount, 
    	           COALESCE(COUNT(DISTINCT rc.id), 0) AS commentCount
    	    FROM Review r
    	    LEFT JOIN ReviewLike rl ON r.id = rl.review.id
    	    LEFT JOIN ReviewComment rc ON r.id = rc.review.id
    	    GROUP BY r.spotifyId, r.id
    	    HAVING COUNT(DISTINCT rl.id) > 0 OR COUNT(DISTINCT rc.id) > 0
    	""")
    	List<Object[]> findReviewsWithLikesOrComments();


}
