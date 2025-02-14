package com.review_like.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.review_like.constant.Constants;
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
    
    private static final String REDIS_TOP_REVIEWS_PREFIX = "popular_reviews:";

    private ZSetOperations<String, Long> getZSetOperations() {
        return redisTemplate.opsForZSet();
    }


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
    
    @PostConstruct
    public void initializePopularLikesCache() {
        List<Object[]> likesData = reviewLikeRepository.findLikeCountsBySpotifyId(); // Fetch likes grouped by spotifyId

        for (Object[] row : likesData) {
            String spotifyId = (String) row[0]; // Get Spotify ID
            Long reviewId = (Long) row[1];      // Get Review ID
            Long count = (Long) row[2];         // Get Like Count

            // Store most liked reviews in Redis Sorted Set for Spotify ID
            getZSetOperations().add(REDIS_TOP_REVIEWS_PREFIX + spotifyId, reviewId, count);
        }

        logger.info("✅ Redis cache initialized with likes count and popular reviews.");
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
            ReviewLike l = reviewLikeRepository.save(new ReviewLike(userId, review));
            incrementReviewsLikesCount(review.getId());
            updateTopReviewsInRedis(review.getSpotifyId(), review.getId(), 1); // Update Redis ZSET

            return new ReviewLikeResponseDTO(true, "Like added successfully.", l.getId(), HttpStatus.OK);
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
            updateTopReviewsInRedis(review.getSpotifyId(), review.getId(), -1); // Update Redis ZSET

            return new ReviewLikeResponseDTO(true, "Like removed successfully.", null, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error while removing like: {}", e.getMessage());
            return new ReviewLikeResponseDTO(false, "Internal Server Error while removing the like.", null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void updateTopReviewsInRedis(String spotifyId, Long reviewId, int delta) {
        String redisKey = REDIS_TOP_REVIEWS_PREFIX + spotifyId;
        ZSetOperations<String, Long> zSetOperations = getZSetOperations();

        // Increment or decrement like count in Redis sorted set
        Double currentScore = zSetOperations.score(redisKey, reviewId);
        double newScore = (currentScore != null ? currentScore : 0) + delta;

        if (newScore <= 0) {
            zSetOperations.remove(redisKey, reviewId); // Remove if count becomes 0
        } else {
            zSetOperations.add(redisKey, reviewId, newScore); // Update count
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
    
    public List<Long> getTop100PopularReviewsBySpotifyId(String spotifyId) {
        String redisKey = REDIS_TOP_REVIEWS_PREFIX + spotifyId;
        Set<Long> topReviews = getZSetOperations().reverseRange(redisKey, 0, Constants.POPULER_SIZE); // Fetch top 100

        return (topReviews != null) ? new ArrayList<>(topReviews) : new ArrayList<>();
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
