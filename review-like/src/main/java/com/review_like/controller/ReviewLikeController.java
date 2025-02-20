package com.review_like.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.review_like.dto.IsLikedResponseDto;
import com.review_like.dto.ReviewLikeRequestDTO;
import com.review_like.dto.ReviewLikeResponseDTO;
import com.review_like.service.ReviewLikeService;

/**
 * Controller for managing likes on reviews.
 * Provides APIs to like, unlike, check like status, and retrieve like counts.
 */
@RestController
@RequestMapping("/review-like")
public class ReviewLikeController {

    @Autowired
    private ReviewLikeService reviewLikeService;

    /**
     * Adds a like to a review.
     *
     * @param request DTO containing user ID and review ID.
     * @return ResponseEntity containing the like response and status.
     */
    @PostMapping("/like")
    public ResponseEntity<ReviewLikeResponseDTO> likeReview(@RequestBody ReviewLikeRequestDTO request) {
        ReviewLikeResponseDTO response = reviewLikeService.addReviewsLike(request.getUserId(), request.getReviewId());
        return new ResponseEntity<>(response, response.getStatus());
    }

    /**
     * Removes a like from a review.
     *
     * @param likeId The ID of the like to be removed.
     * @return ResponseEntity containing the unlike response and status.
     */
    @DeleteMapping("/unlike/{likeId}")
    public ResponseEntity<ReviewLikeResponseDTO> unlikeReview(@PathVariable Long likeId) {
        ReviewLikeResponseDTO response = reviewLikeService.removeReviewsLikeById(likeId);
        return new ResponseEntity<>(response, response.getStatus());
    }

    /**
     * Checks if a user has liked a specific review.
     *
     * @param reviewId The ID of the review.
     * @param userId The ID of the user.
     * @return ResponseEntity containing a boolean response indicating if the review is liked.
     */
    @GetMapping("/{reviewId}/is-liked/{userId}")
    public ResponseEntity<IsLikedResponseDto> isReviewLikedByUser(@PathVariable Long reviewId, @PathVariable Long userId) {
        return reviewLikeService.isReviewLikedByUser(userId, reviewId);
    }

    /**
     * Retrieves the total like count for a review.
     *
     * @param reviewId The ID of the review.
     * @return ResponseEntity containing the like count.
     */
    @GetMapping("/{reviewId}/count")
    public ResponseEntity<ReviewLikeResponseDTO> getLikeCount(@PathVariable Long reviewId) {
        ReviewLikeResponseDTO response = reviewLikeService.getReviewsLikeCount(reviewId);
        return new ResponseEntity<>(response, response.getStatus());
    }

    /**
     * Retrieves the top 100 most liked reviews for a specific Spotify track.
     *
     * @param spotifyId The Spotify track ID.
     * @return ResponseEntity containing a list of the top 100 review IDs sorted by popularity.
     */
    @GetMapping("/top/{spotifyId}")
    public ResponseEntity<List<Long>> getTopReviews(@PathVariable String spotifyId) {
        List<Long> topReviews = reviewLikeService.getTop100PopularReviewsBySpotifyId(spotifyId);
        return ResponseEntity.ok(topReviews);
    }
}
