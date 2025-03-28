package com.foodrecipes.credentials.credentials.service;

import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.foodrecipes.credentials.credentials.dto.IsLikedResponseDto;
import com.foodrecipes.credentials.credentials.dto.NotificationRequest;
import com.foodrecipes.credentials.credentials.dto.ReviewLikeResponseDTO;
import com.foodrecipes.credentials.credentials.entity.ReviewL;
import com.foodrecipes.credentials.credentials.entity.ReviewLLike;
import com.foodrecipes.credentials.credentials.repository.ReviewLLikeRepository;
import com.foodrecipes.credentials.credentials.repository.ReviewLRepository;

@Service
public class ReviewLikeService {

    private static final Logger logger = LoggerFactory.getLogger(ReviewLikeService.class);

    @Autowired
    private ReviewLLikeRepository reviewLikeRepository;

    @Autowired
    private ReviewLRepository reviewRepository;

    
    @Autowired
    private NotificationService notificationService;


    

    


    @Transactional
    public ReviewLikeResponseDTO addReviewsLike(Long userId, Long reviewId) {
        Optional<ReviewL> optionalReview = reviewRepository.findById(reviewId);
        if (optionalReview.isEmpty()) {
            return new ReviewLikeResponseDTO(false, "Review not found.", null, HttpStatus.NOT_FOUND);
        }
        ReviewL review = optionalReview.get();

        if (reviewLikeRepository.existsByUserIdAndReview(userId, review)) {
            return new ReviewLikeResponseDTO(false, "User already liked this review.", null, HttpStatus.CONFLICT);
        }

        try {
            ReviewLLike l = reviewLikeRepository.save(new ReviewLLike(userId, review));
            NotificationRequest request = new NotificationRequest(review.getUserId(), "New Like!", "Someone liked your post!");
            notificationService.sendNotifications(request);

            return new ReviewLikeResponseDTO(true, "Like added successfully.", l.getId(), HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error while adding like: {}", e.getMessage());
            return new ReviewLikeResponseDTO(false, "Internal Server Error while adding the like.", null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public ReviewLikeResponseDTO removeReviewsLikeById(Long likeId) {
        Optional<ReviewLLike> optionalReviewLike = reviewLikeRepository.findById(likeId);
        
        if (optionalReviewLike.isEmpty()) {
            return new ReviewLikeResponseDTO(false, "❌ Like not found.", null, HttpStatus.NOT_FOUND);
        }

        ReviewLLike reviewLike = optionalReviewLike.get();

        try {
            // Delete the like entry from DB
            reviewLikeRepository.delete(reviewLike);

            // Decrement Redis count

            return new ReviewLikeResponseDTO(true, "Like removed successfully.", null, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error while removing like: {}", e.getMessage());
            return new ReviewLikeResponseDTO(false, "Internal Server Error while removing the like.", null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

   

    public ReviewLikeResponseDTO getReviewsLikeCount(Long reviewId) {
        int count = reviewLikeRepository.countByReviewId(reviewId);
        return new ReviewLikeResponseDTO(true, "Like count retrieved successfully.", (long) count, HttpStatus.OK);
    }


    
    

    

    public ResponseEntity<IsLikedResponseDto> isReviewLikedByUser(Long userId, Long reviewId) {
        Optional<ReviewLLike> optionalReviewLike = reviewLikeRepository.findByUserIdAndReviewId(userId, reviewId);

        if (optionalReviewLike.isPresent()) {
            ReviewLLike reviewLike = optionalReviewLike.get();
            return ResponseEntity.ok(new IsLikedResponseDto(reviewLike.getId(), reviewLike.getUserId()));
        }

        return ResponseEntity.ok(null); // Return null if review is not liked
    }
    
    
    
    public List<Long> getTop100PopularReviewsBySpotifyId(String spotifyId) {
        return reviewLikeRepository.findTopReviewIdsBySpotifyId(spotifyId);
    }



    
}
