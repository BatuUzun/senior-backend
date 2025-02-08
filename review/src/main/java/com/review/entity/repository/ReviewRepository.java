package com.review.entity.repository;

import java.time.LocalDateTime;
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
}
