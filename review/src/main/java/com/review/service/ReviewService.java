package com.review.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.review.Constants;
import com.review.dto.ReviewUpdateDTO;
import com.review.entity.Review;
import com.review.entity.repository.ReviewRepository;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    public Review addReview(Review review) {
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
}
