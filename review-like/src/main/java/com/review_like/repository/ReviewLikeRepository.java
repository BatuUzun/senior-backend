package com.review_like.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.review_like.entity.Review;
import com.review_like.entity.ReviewLike;

@Repository
public interface ReviewLikeRepository extends JpaRepository<ReviewLike, Long> {

    boolean existsByUserIdAndReview(Long userId, Review review);

    Optional<ReviewLike> findByUserIdAndReview(Long userId, Review review);

    
    @Query("SELECT COUNT(rl) FROM ReviewLike rl WHERE rl.review.id = :reviewId")
    int countByReviewId(Long reviewId);
    
    @Query("SELECT rl.review.id, COUNT(rl) FROM ReviewLike rl GROUP BY rl.review.id")
    List<Object[]> countLikesByReviewId();
    
    @Query("SELECT rl FROM ReviewLike rl WHERE rl.userId = :userId AND rl.review.id = :reviewId")
    Optional<ReviewLike> findByUserIdAndReviewId(@Param("userId") Long userId, @Param("reviewId") Long reviewId);


}
