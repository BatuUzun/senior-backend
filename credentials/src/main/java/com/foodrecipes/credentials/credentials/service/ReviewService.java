package com.foodrecipes.credentials.credentials.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
import org.springframework.stereotype.Service;

import com.foodrecipes.credentials.credentials.constants.Constants;
import com.foodrecipes.credentials.credentials.dto.FollowedReviewsRequestDTO;
import com.foodrecipes.credentials.credentials.dto.FollowedUsersReviewsWithoutSpotifyId;
import com.foodrecipes.credentials.credentials.dto.ReviewUpdateDTO;
import com.foodrecipes.credentials.credentials.entity.Review;
import com.foodrecipes.credentials.credentials.repository.ReviewRepository;

@Service
public class ReviewService {

	@Autowired
	private ReviewRepository reviewRepository;

	private static final Logger logger = LoggerFactory.getLogger(ReviewService.class);

	

	@Autowired
	private UserFollowsService userFollowService;


	

	/*public List<Long> getTopReviews() {
	    List<Object[]> topReviews = reviewRepository.findTopPopularReviews();

	    List<Long> reviewIds = new ArrayList<>();
	    for (Object[] row : topReviews) {
	        Long reviewId = (Long) row[0]; // First column is review ID
	        reviewIds.add(reviewId);
	    }

	    return reviewIds;
	}*/
	
	public List<Long> getTopReviews() {
	    List<Object[]> topReviews = reviewRepository.findTopPopularReviews();

	    List<Long> reviewIds = new ArrayList<>();
	    for (Object[] row : topReviews) {
	        Long reviewId = ((Number) row[0]).longValue();
	        reviewIds.add(reviewId);
	    }

	    return reviewIds;
	}





	

	public List<Review> getFollowedUserReviews(FollowedReviewsRequestDTO request) {
		// 1. Fetch followed user IDs from UserFollowProxy
		
		Set<Long> followedUserIds = userFollowService.getFollowedUsers(request.getUserId());

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
		return reviewRepository.findReviewsBySpotifyIdAndUserIdsWithCursor(request.getSpotifyId(),
				followedUserIds.stream().toList(), request.getCursor(), pageable);
	}
	
	public List<Review> getFollowedUserReviewsWithoutSpotifyId(FollowedUsersReviewsWithoutSpotifyId request) {
	    // 1. Fetch followed user IDs from UserFollowProxy
	    Set<Long> followedUserIds = userFollowService.getFollowedUsers(request.getUserId());

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

	    // 4. Fetch paginated reviews of followed users (WITHOUT filtering by Spotify ID)
	    return reviewRepository.findReviewsByFollowedUsersWithCursor(
	            followedUserIds.stream().toList(), request.getCursor(), pageable);
	}


	/*
	 * @PostConstruct public void initializePopularLikesCache() { List<Object[]>
	 * likesData = reviewLikeRepository.findLikeCountsBySpotifyId(); // Fetch likes
	 * grouped by spotifyId
	 * 
	 * for (Object[] row : likesData) { String spotifyId = (String) row[0]; // Get
	 * Spotify ID Long reviewId = (Long) row[1]; // Get Review ID Long count =
	 * (Long) row[2]; // Get Like Count
	 * 
	 * // Store most liked reviews in Redis Sorted Set for Spotify ID
	 * getZSetOperations().add(REDIS_TOP_REVIEWS_PREFIX + spotifyId, reviewId,
	 * count); }
	 * 
	 * logger.info("✅ Redis cache initialized with likes count and popular reviews."
	 * ); }
	 */

	

	public Review addReview(Review review) {
		// Check if the user has already reviewed this Spotify ID
		Optional<Review> existingReview = reviewRepository.findByUserIdAndSpotifyId(review.getUserId(),
				review.getSpotifyId());

		if (existingReview.isPresent()) {
			throw new IllegalStateException("User has already reviewed this Spotify ID");
		}

		return reviewRepository.save(review);
	}

	public void deleteReview(Long id) {
	    // Retrieve the review to get associated data before deleting
	    Optional<Review> reviewOptional = reviewRepository.findById(id);
	    
	    if (reviewOptional.isPresent()) {
	        
	        // Remove from global popularity Redis cache
	        
	        // Remove from Spotify-specific Redis cache

	        // Delete from the database
	        reviewRepository.deleteById(id);
	        
	        logger.info("✅ Review {} deleted from database and Redis.", id);
	    } else {
	        logger.warn("⚠️ Review with ID {} not found. No action taken.", id);
	    }
	}


	public Optional<Review> updateReview(ReviewUpdateDTO reviewUpdateDTO) {
		return reviewRepository.findById(reviewUpdateDTO.getId()).map(existingReview -> {
			existingReview.setRating(reviewUpdateDTO.getRating());
			existingReview.setComment(reviewUpdateDTO.getComment());
			return reviewRepository.save(existingReview);
		});
	}

	public Page<Review> getReviewsByUserId(Long userId, int page) {
		return reviewRepository.findByUserId(userId,
				PageRequest.of(page, Constants.PAGE_SIZE, Sort.by(Sort.Direction.DESC, "createdAt")));
	}

	public Page<Review> getReviewsBySpotifyId(String spotifyId, LocalDateTime referenceTime, int page) {
		Pageable pageable = PageRequest.of(page, Constants.PAGE_SIZE,
				Sort.by(Sort.Direction.DESC, "createdAt").and(Sort.by(Sort.Direction.DESC, "id")));

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
		BigDecimal roundedRating = BigDecimal.valueOf(averageRating).multiply(BigDecimal.valueOf(2)) // Scale to work
																										// with 0.5
																										// increments
				.setScale(0, RoundingMode.HALF_UP) // Round to nearest whole number
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
