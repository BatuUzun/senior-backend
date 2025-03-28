package com.foodrecipes.credentials.credentials.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.foodrecipes.credentials.credentials.entity.ReviewL;
import com.foodrecipes.credentials.credentials.entity.ReviewLLike;

@Repository
public interface ReviewLLikeRepository extends JpaRepository<ReviewLLike, Long> {

    boolean existsByUserIdAndReview(Long userId, ReviewL review);

    Optional<ReviewLLike> findByUserIdAndReview(Long userId, ReviewL review);

    
    @Query("SELECT COUNT(rl) FROM ReviewLike rl WHERE rl.review.id = :reviewId")
    int countByReviewId(Long reviewId);
    
    @Query("SELECT rl.review.id, COUNT(rl) FROM ReviewLike rl GROUP BY rl.review.id")
    List<Object[]> countLikesByReviewId();
    
    @Query("SELECT rl FROM ReviewLike rl WHERE rl.userId = :userId AND rl.review.id = :reviewId")
    Optional<ReviewLLike> findByUserIdAndReviewId(@Param("userId") Long userId, @Param("reviewId") Long reviewId);

    @Query("""
            SELECT r.spotifyId, r.id, COUNT(rl.id) 
            FROM Review r
            JOIN ReviewLike rl ON r.id = rl.review.id
            GROUP BY r.spotifyId, r.id
        """)
        List<Object[]> findLikeCountsBySpotifyId();
        
        @Query(value = """
        	    SELECT r.id
        	    FROM reviews r
        	    JOIN review_like rl ON r.id = rl.review_id
        	    WHERE r.spotify_id = :spotifyId
        	    GROUP BY r.id
        	    ORDER BY COUNT(rl.id) DESC
        	    LIMIT 100
        	""", nativeQuery = true)
        	List<Long> findTopReviewIdsBySpotifyId(@Param("spotifyId") String spotifyId);

}
