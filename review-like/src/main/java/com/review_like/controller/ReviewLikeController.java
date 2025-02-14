package com.review_like.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.review_like.dto.IsLikedResponseDto;
import com.review_like.dto.ReviewLikeRequestDTO;
import com.review_like.dto.ReviewLikeResponseDTO;
import com.review_like.service.ReviewLikeService;

@RestController
@RequestMapping("/review-like")
public class ReviewLikeController {

    @Autowired
    private ReviewLikeService reviewLikeService;

    @PostMapping("/like")
    public ResponseEntity<ReviewLikeResponseDTO> likeReview(@RequestBody ReviewLikeRequestDTO request) {
        ReviewLikeResponseDTO response = reviewLikeService.addReviewsLike(request.getUserId(), request.getReviewId());
        return new ResponseEntity<>(response, response.getStatus());
    }

    @DeleteMapping("/unlike/{likeId}")
    public ResponseEntity<ReviewLikeResponseDTO> unlikeReview(@PathVariable Long likeId) {
        ReviewLikeResponseDTO response = reviewLikeService.removeReviewsLikeById(likeId);
        return new ResponseEntity<>(response, response.getStatus());
    }
    
    @GetMapping("/{reviewId}/is-liked/{userId}")
    public ResponseEntity<IsLikedResponseDto> isReviewLikedByUser(@PathVariable Long reviewId, @PathVariable Long userId) {
        return reviewLikeService.isReviewLikedByUser(userId, reviewId);
    }
    
    @GetMapping("/{reviewId}/count")
    public ResponseEntity<ReviewLikeResponseDTO> getLikeCount(@PathVariable Long reviewId) {
        ReviewLikeResponseDTO response = reviewLikeService.getReviewsLikeCount(reviewId);
        return new ResponseEntity<>(response, response.getStatus());
    }
    
    @GetMapping("/top/{spotifyId}")
    public ResponseEntity<List<Long>> getTopReviews(@PathVariable String spotifyId) {
        List<Long> topReviews = reviewLikeService.getTop100PopularReviewsBySpotifyId(spotifyId);
        return ResponseEntity.ok(topReviews);
    }

}