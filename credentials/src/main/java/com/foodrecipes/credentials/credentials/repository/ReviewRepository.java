package com.foodrecipes.credentials.credentials.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.foodrecipes.credentials.credentials.constants.Constants;
import com.foodrecipes.credentials.credentials.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {
	Page<Review> findByUserId(Long userId, Pageable pageable);

	Page<Review> findBySpotifyId(String spotifyId, Pageable pageable);

	@Query("""
			    SELECT r FROM Review r
			    WHERE r.spotifyId = :spotifyId
			    AND r.createdAt <= :referenceTime
			    ORDER BY r.createdAt DESC, r.id DESC
			""")
	Page<Review> findBySpotifyIdWithReference(@Param("spotifyId") String spotifyId,
			@Param("referenceTime") LocalDateTime referenceTime, Pageable pageable);

	@Query("SELECT AVG(r.rating) FROM Review r WHERE r.spotifyId = :spotifyId")
	Double findAverageRatingBySpotifyId(String spotifyId);

	Optional<Review> findByUserIdAndSpotifyId(Long userId, String spotifyId);

	long countByUserId(Long userId);

	@Query(value = """
		    SELECT r.spotify_id,
		           r.id,
		           COUNT(DISTINCT rl.id) AS like_count,
		           COUNT(DISTINCT rc.id) AS comment_count
		    FROM reviews r
		    LEFT JOIN review_like rl ON r.id = rl.review_id
		    LEFT JOIN review_comment rc ON r.id = rc.review_id
		    GROUP BY r.spotify_id, r.id
		    HAVING COUNT(DISTINCT rl.id) > 0 OR COUNT(DISTINCT rc.id) > 0
		    """, nativeQuery = true)
		List<Object[]> findReviewsWithLikesOrComments();


	@Query("SELECT r FROM Review r WHERE r.spotifyId = :spotifyId AND r.userId IN :userIds AND r.createdAt < :cursor ORDER BY r.createdAt DESC")
	List<Review> findReviewsBySpotifyIdAndUserIdsWithCursor(String spotifyId, List<Long> userIds, LocalDateTime cursor,
			Pageable pageable);

	
	
	@Query("SELECT r FROM Review r WHERE r.userId IN :userIds AND r.createdAt < :cursor ORDER BY r.createdAt DESC")
	List<Review> findReviewsByFollowedUsersWithCursor(List<Long> userIds, LocalDateTime cursor, Pageable pageable);

	
	@Query(value = """
		    SELECT r.id, 
		           COUNT(DISTINCT rl.id) AS like_count, 
		           COUNT(DISTINCT rc.id) AS comment_count
		    FROM reviews r
		    LEFT JOIN review_like rl ON r.id = rl.review_id
		    LEFT JOIN review_comment rc ON r.id = rc.review_id
		    GROUP BY r.id
		    ORDER BY (COUNT(DISTINCT rl.id) + COUNT(DISTINCT rc.id)) DESC
		    LIMIT 100
		""", nativeQuery = true)
		List<Object[]> findTopPopularReviews();


}
