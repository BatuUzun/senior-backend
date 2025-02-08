package com.review_like.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.review_like.dto.IsLikedResponseDto;
import com.review_like.dto.ReviewLikeResponseDTO;
import com.review_like.entity.Review;
import com.review_like.entity.ReviewLike;
import com.review_like.repository.ReviewLikeRepository;
import com.review_like.repository.ReviewRepository;

import jakarta.annotation.PostConstruct;

@Service
public class ReviewLikeService {

    private static final Logger logger = LoggerFactory.getLogger(ReviewLikeService.class);

    @Autowired
    private ReviewLikeRepository reviewLikeRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private RedisTemplate<String, Long> redisTemplate;

    private static final String REDIS_KEY_PREFIX = "review_likes:";

    @PostConstruct
    public void initializeLikesCache() {
        List<Object[]> likesData = reviewLikeRepository.countLikesByReviewId();
        for (Object[] row : likesData) {
            Long reviewId = (Long) row[0];
            Long count = (Long) row[1];

            redisTemplate.opsForValue().set(REDIS_KEY_PREFIX + reviewId, count);
        }
        logger.info("✅ Redis cache initialized with likes count.");
    }

    @Transactional
    public ReviewLikeResponseDTO addReviewsLike(Long userId, Long reviewId) {
        Optional<Review> optionalReview = reviewRepository.findById(reviewId);
        if (optionalReview.isEmpty()) {
            return new ReviewLikeResponseDTO(false, "Review not found.", null, HttpStatus.NOT_FOUND);
        }
        Review review = optionalReview.get();

        if (reviewLikeRepository.existsByUserIdAndReview(userId, review)) {
            return new ReviewLikeResponseDTO(false, "User already liked this review.", null, HttpStatus.CONFLICT);
        }

        try {
            reviewLikeRepository.save(new ReviewLike(userId, review));
            incrementReviewsLikesCount(review.getId());

            return new ReviewLikeResponseDTO(true, "Like added successfully.", null, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error while adding like: {}", e.getMessage());
            return new ReviewLikeResponseDTO(false, "Internal Server Error while adding the like.", null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public ReviewLikeResponseDTO removeReviewsLikeById(Long likeId) {
        Optional<ReviewLike> optionalReviewLike = reviewLikeRepository.findById(likeId);
        
        if (optionalReviewLike.isEmpty()) {
            return new ReviewLikeResponseDTO(false, "❌ Like not found.", null, HttpStatus.NOT_FOUND);
        }

        ReviewLike reviewLike = optionalReviewLike.get();
        Review review = reviewLike.getReview();

        try {
            // Delete the like entry from DB
            reviewLikeRepository.delete(reviewLike);

            // Decrement Redis count
            decrementReviewsLikesCountSafely(review.getId());

            return new ReviewLikeResponseDTO(true, "Like removed successfully.", null, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error while removing like: {}", e.getMessage());
            return new ReviewLikeResponseDTO(false, "Internal Server Error while removing the like.", null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    

    public ReviewLikeResponseDTO getReviewsLikeCount(Long reviewId) {
        String key = REDIS_KEY_PREFIX + reviewId;
        Long count = redisTemplate.opsForValue().get(key);
        return new ReviewLikeResponseDTO(true, "Like count retrieved successfully.", count != null ? count : 0L, HttpStatus.OK);
    }

    private void incrementReviewsLikesCount(Long reviewId) {
        String key = REDIS_KEY_PREFIX + reviewId;
        redisTemplate.opsForValue().increment(key, 1);
    }
    

    

    public ResponseEntity<IsLikedResponseDto> isReviewLikedByUser(Long userId, Long reviewId) {
        Optional<ReviewLike> optionalReviewLike = reviewLikeRepository.findByUserIdAndReviewId(userId, reviewId);

        if (optionalReviewLike.isPresent()) {
            ReviewLike reviewLike = optionalReviewLike.get();
            return ResponseEntity.ok(new IsLikedResponseDto(reviewLike.getId(), reviewLike.getUserId()));
        }

        return ResponseEntity.ok(null); // Return null if review is not liked
    }

    private void decrementReviewsLikesCountSafely(Long reviewId) {
        String key = REDIS_KEY_PREFIX + reviewId;
        Long currentCount = redisTemplate.opsForValue().get(key);

        if (currentCount == null || currentCount <= 0) {
            redisTemplate.opsForValue().set(key, 0L);
            return;
        }

        redisTemplate.opsForValue().decrement(key, 1);
    }
}
