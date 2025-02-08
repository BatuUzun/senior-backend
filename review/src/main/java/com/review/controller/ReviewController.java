package com.review.controller;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.review.dto.ReviewUpdateDTO;
import com.review.entity.Review;
import com.review.service.ReviewService;

@RestController
@RequestMapping("/review")
public class ReviewController {

	@Autowired
	private ReviewService reviewService;

	@PostMapping("/add-review")
	public ResponseEntity<Review> addReview(@RequestBody Review review) {
		return ResponseEntity.ok(reviewService.addReview(review));
	}

	@DeleteMapping("/delete/{id}")
	public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
		reviewService.deleteReview(id);
		return ResponseEntity.noContent().build();
	}

	@PutMapping("/update")
	public ResponseEntity<Review> updateReview(@RequestBody ReviewUpdateDTO reviewUpdateDTO) {
		return reviewService.updateReview(reviewUpdateDTO).map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}

	@GetMapping("/get-reviews/user/{userId}")
	public ResponseEntity<Page<Review>> getReviewsByUserId(@PathVariable Long userId,
			@RequestParam(defaultValue = "0") int page) {
		return ResponseEntity.ok(reviewService.getReviewsByUserId(userId, page));
	}

	@GetMapping("/get-reviews/spotify/{spotifyId}")
	public ResponseEntity<Page<Review>> getReviewsBySpotifyId(
	        @PathVariable String spotifyId,
	        @RequestParam(defaultValue = "0") int page,
	        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime referenceTime) {
	    if (referenceTime == null) {
	        referenceTime = LocalDateTime.now(); // Use the current time for the first request
	    }
	    return ResponseEntity.ok(reviewService.getReviewsBySpotifyId(spotifyId, referenceTime, page));
	}

	@GetMapping("/calculate/spotify/{spotifyId}/average-rating")
	public ResponseEntity<Double> calculateAverageRating(@PathVariable String spotifyId) {
		return ResponseEntity.ok(reviewService.calculateAverageRating(spotifyId));
	}
	
	@GetMapping("/get-review/{id}")
    public ResponseEntity<?> getReviewById(@PathVariable Long id) {
        try {
            Review review = reviewService.getReviewById(id);
            return ResponseEntity.ok(review);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }
}
