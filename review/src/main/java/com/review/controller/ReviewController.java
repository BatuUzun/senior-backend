package com.review.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
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

import com.review.dto.FollowedReviewsRequestDTO;
import com.review.dto.FollowedUsersReviewsWithoutSpotifyId;
import com.review.dto.ReviewUpdateDTO;
import com.review.dto.UserReviewCountDTO;
import com.review.entity.Review;
import com.review.service.ReviewService;

@RestController
@RequestMapping("/review")
public class ReviewController {

	@Autowired
	private ReviewService reviewService;

    /**
     * Adds a new review.
     *
     * @param review The review object containing user and Spotify track details.
     * @return ResponseEntity containing the saved review if successful,
     *         or a CONFLICT response if the user has already reviewed this Spotify ID.
     */
    @PostMapping("/add-review")
    public ResponseEntity<?> addReview(@RequestBody Review review) {
        try {
            // Attempt to add the review
            Review savedReview = reviewService.addReview(review);
            return ResponseEntity.ok(savedReview);
        } catch (IllegalStateException e) {
            // Handle case where the user has already reviewed the specified Spotify track
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("{\"error\": \"User has already reviewed this Spotify ID\"}");
        }
    }

    /**
     * Deletes a review by its ID.
     *
     * @param id The ID of the review to be deleted.
     * @return ResponseEntity with no content if the deletion is successful.
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
        // Delete the review with the given ID
        reviewService.deleteReview(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Updates an existing review.
     *
     * @param reviewUpdateDTO DTO containing the updated review details.
     * @return ResponseEntity containing the updated review if successful,
     *         or NOT_FOUND if the review does not exist.
     */
    @PutMapping("/update")
    public ResponseEntity<Review> updateReview(@RequestBody ReviewUpdateDTO reviewUpdateDTO) {
        // Attempt to update the review and return appropriate response
        return reviewService.updateReview(reviewUpdateDTO).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Retrieves paginated reviews for a specific user.
     *
     * @param userId The ID of the user whose reviews are being fetched.
     * @param page The page number for pagination (default: 0).
     * @return ResponseEntity containing a Page of reviews.
     */
    @GetMapping("/get-reviews/user/{userId}")
    public ResponseEntity<Page<Review>> getReviewsByUserId(@PathVariable Long userId,
                                                           @RequestParam(defaultValue = "0") int page) {
        // Fetch and return the reviews for the specified user
        return ResponseEntity.ok(reviewService.getReviewsByUserId(userId, page));
    }

    /**
     * Retrieves paginated reviews for a specific Spotify track.
     *
     * @param spotifyId The Spotify track ID.
     * @param page The page number for pagination (default: 0).
     * @param referenceTime The timestamp for cursor-based pagination.
     *                       If not provided, defaults to the current time.
     * @return ResponseEntity containing a Page of reviews for the given Spotify track.
     */
    @GetMapping("/get-reviews/spotify/{spotifyId}")
    public ResponseEntity<Page<Review>> getReviewsBySpotifyId(@PathVariable String spotifyId,
                                                              @RequestParam(defaultValue = "0") int page,
                                                              @RequestParam(required = false)
                                                              @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime referenceTime) {
        // If no reference time is provided, default to the current timestamp
        if (referenceTime == null) {
            referenceTime = LocalDateTime.now();
        }
        // Fetch and return reviews for the given Spotify track
        return ResponseEntity.ok(reviewService.getReviewsBySpotifyId(spotifyId, referenceTime, page));
    }


    /**
     * Calculates the average rating for a specific Spotify track.
     *
     * @param spotifyId The Spotify track ID.
     * @return ResponseEntity containing the average rating as a Double.
     */
    @GetMapping("/calculate/spotify/{spotifyId}/average-rating")
    public ResponseEntity<Double> calculateAverageRating(@PathVariable String spotifyId) {
        // Fetch and return the average rating for the given Spotify track ID
        return ResponseEntity.ok(reviewService.calculateAverageRating(spotifyId));
    }

    /**
     * Retrieves a review by its ID.
     *
     * @param id The ID of the review.
     * @return ResponseEntity containing the review if found, or NOT_FOUND if not found.
     */
    @GetMapping("/get-review/{id}")
    public ResponseEntity<?> getReviewById(@PathVariable Long id) {
        try {
            // Fetch the review by its ID
            Review review = reviewService.getReviewById(id);
            return ResponseEntity.ok(review);
        } catch (IllegalArgumentException e) {
            // Handle case where the review ID does not exist
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Retrieves a user's review for a specific Spotify track.
     *
     * @param userId The ID of the user.
     * @param spotifyId The Spotify track ID.
     * @return ResponseEntity containing the review if found, or NOT_FOUND if no review exists.
     */
    @GetMapping("/user-review")
	public ResponseEntity<?> getUserReview(@RequestParam Long userId, @RequestParam String spotifyId) {

		Optional<Review> reviewOptional = reviewService.getUserReview(userId, spotifyId);

		if (reviewOptional.isPresent()) {
			return ResponseEntity.ok(reviewOptional.get());
		} else {
			return ResponseEntity.status(404).body("{\"error\": \"Review not found\"}");
		}
	}

    /**
     * Retrieves the total number of reviews made by a user.
     *
     * @param userId The ID of the user.
     * @return ResponseEntity containing the user's total review count.
     */
    @GetMapping("/user-review-count")
    public ResponseEntity<UserReviewCountDTO> getUserReviewCount(@RequestParam Long userId) {
        // Fetch and return the review count for the given user
        long reviewCount = reviewService.getUserReviewCount(userId);
        return ResponseEntity.ok(new UserReviewCountDTO(userId, reviewCount));
    }

    /**
     * Retrieves reviews from followed users based on a Spotify track ID.
     *
     * @param request DTO containing userId and Spotify track ID.
     * @return List of reviews from followed users.
     */
    @PostMapping("/followed-by-spotify-id")
    public List<Review> getFollowedUserReviews(@RequestBody FollowedReviewsRequestDTO request) {
        // Fetch and return reviews from users followed by the requesting user
        return reviewService.getFollowedUserReviews(request);
    }

	
    /**
     * Retrieves the IDs of the most popular reviews.
     *
     * @return List of review IDs ranked by popularity.
     */
    @GetMapping("/popular")
    public List<Long> getPopularReviewIds() {
        // Fetch and return the list of top reviews based on popularity
        return reviewService.getTopReviews();
    }

    /**
     * Retrieves the latest reviews from followed users.
     *
     * @param request DTO containing userId and pagination parameters.
     * @return List of the latest reviews from followed users.
     */
    @PostMapping("/followed-reviews")
    public List<Review> getFollowedUsersLatestReviews(@RequestBody FollowedUsersReviewsWithoutSpotifyId request) {
        // Fetch and return the latest reviews from users followed by the requesting user
        return reviewService.getFollowedUserReviewsWithoutSpotifyId(request);
    }


}
