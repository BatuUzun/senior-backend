package com.review.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import com.review.constant.Constants;
import com.review.dto.FollowedReviewsRequestDTO;
import com.review.dto.ReviewUpdateDTO;
import com.review.entity.Review;
import com.review.entity.repository.ReviewRepository;
import com.review.proxy.UserFollowProxy;

import jakarta.annotation.PostConstruct;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;
    
    
    private static final Logger logger = LoggerFactory.getLogger(ReviewService.class);

    @Autowired
    private RedisTemplate<String, Long> redisTemplate;
    
    @Autowired
    private UserFollowProxy userFollowProxy;

    
    private static final String REDIS_TOP_REVIEWS_PREFIX = "popular_reviews:";
    
    
    private ZSetOperations<String, Long> getZSetOperations() {
        return redisTemplate.opsForZSet();
    }

    public List<Review> getFollowedUserReviews(FollowedReviewsRequestDTO request) {
        // 1. Fetch followed user IDs from UserFollowProxy
        Set<Long> followedUserIds = userFollowProxy.getFollowedUsers(request.getUserId());

        // 2. If user follows no one, return an empty list
        if (followedUserIds == null || followedUserIds.isEmpty()) {
            return List.of();
        }

        // 3. If cursor is null, use current time (first request)
        if (request.getCursor() == null) {
        	request.setCursor(LocalDateTime.now());
        }
        int page = request.getPage(); // Get page from DTO

        PageRequest pageable = PageRequest.of(page, Constants.PAGE_SIZE);
        // 4. Fetch paginated reviews using cursor
        return reviewRepository.findReviewsBySpotifyIdAndUserIdsWithCursor(request.getSpotifyId(), followedUserIds.stream().toList(), request.getCursor(), pageable);
    }
    
    /*@PostConstruct
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
    }*/
    
    @PostConstruct
    public void initializePopularLikesCache() {
        logger.info("🚀 Initializing Redis cache for popular reviews...");

        // Fetch engagement data (only for reviews with likes or comments)
        List<Object[]> engagementData = reviewRepository.findReviewsWithLikesOrComments();
        logger.info("✅ Engagement Data: {}", engagementData);

        for (Object[] row : engagementData) {
            String spotifyId = (String) row[0]; // Spotify ID
            Long reviewId = (Long) row[1];      // Review ID
            Long likeCount = (Long) row[2];     // Like Count
            Long commentCount = (Long) row[3];  // Comment Count

            logger.info("🟢 Processing Review ID: {}, Spotify ID: {}, Likes: {}, Comments: {}",
                        reviewId, spotifyId, likeCount, commentCount);

            // Calculate popularity score = likes + comments
            Long popularityScore = likeCount + commentCount;

            // Store in Redis Sorted Set (ZSET)
            getZSetOperations().add(REDIS_TOP_REVIEWS_PREFIX + spotifyId, reviewId, popularityScore);

            logger.info("✅ Stored in Redis: SpotifyID: {}, ReviewID: {}, Popularity Score: {}",
                        spotifyId, reviewId, popularityScore);
        }

        logger.info("🎉 Redis cache successfully initialized with popularity scores (likes + comments)!");
    }





    public Review addReview(Review review) {
        // Check if the user has already reviewed this Spotify ID
        Optional<Review> existingReview = reviewRepository.findByUserIdAndSpotifyId(review.getUserId(), review.getSpotifyId());

        if (existingReview.isPresent()) {
            throw new IllegalStateException("User has already reviewed this Spotify ID");
        }

        return reviewRepository.save(review);
    }

    public void deleteReview(Long id) {
        reviewRepository.deleteById(id);
    }

    public Optional<Review> updateReview(ReviewUpdateDTO reviewUpdateDTO) {
        return reviewRepository.findById(reviewUpdateDTO.getId()).map(existingReview -> {
            existingReview.setRating(reviewUpdateDTO.getRating());
            existingReview.setComment(reviewUpdateDTO.getComment());
            return reviewRepository.save(existingReview);
        });
    }

    public Page<Review> getReviewsByUserId(Long userId, int page) {
        return reviewRepository.findByUserId(
            userId,
            PageRequest.of(page, Constants.PAGE_SIZE, Sort.by(Sort.Direction.DESC, "createdAt"))
        );
    }

    

    public Page<Review> getReviewsBySpotifyId(String spotifyId, LocalDateTime referenceTime, int page) {
        Pageable pageable = PageRequest.of(page, Constants.PAGE_SIZE, Sort.by(Sort.Direction.DESC, "createdAt").and(Sort.by(Sort.Direction.DESC, "id")));

        // Use the referenceTime for filtering reviews
        return reviewRepository.findBySpotifyIdWithReference(spotifyId, referenceTime, pageable);
    }

    public Optional<Review> getUserReview(Long userId, String spotifyId) {
        return reviewRepository.findByUserIdAndSpotifyId(userId, spotifyId);
    }


    public Double calculateAverageRating(String spotifyId) {
        Double averageRating = reviewRepository.findAverageRatingBySpotifyId(spotifyId);
        
        if (averageRating == null) {
            return null; // or return 0.0 if you prefer a default value
        }
        
        // Round to the nearest 0.5
        BigDecimal roundedRating = BigDecimal.valueOf(averageRating)
            .multiply(BigDecimal.valueOf(2))       // Scale to work with 0.5 increments
            .setScale(0, RoundingMode.HALF_UP)    // Round to nearest whole number
            .divide(BigDecimal.valueOf(2), 1, RoundingMode.HALF_UP); // Scale back to 0.5 increments
        
        return roundedRating.doubleValue();
    }
    
    public Review getReviewById(Long reviewId) {
        Optional<Review> review = reviewRepository.findById(reviewId);
        return review.orElseThrow(() -> new IllegalArgumentException("Review with ID " + reviewId + " not found."));
    }
    
    public long getUserReviewCount(Long userId) {
        return reviewRepository.countByUserId(userId);
    }
    
}
