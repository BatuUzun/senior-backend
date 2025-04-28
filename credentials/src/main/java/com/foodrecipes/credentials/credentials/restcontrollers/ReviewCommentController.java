package com.foodrecipes.credentials.credentials.restcontrollers;

import java.time.LocalDateTime;

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

import com.foodrecipes.credentials.credentials.dto.ReviewCommentRequestDTO;
import com.foodrecipes.credentials.credentials.dto.ReviewCommentResponseDTO;
import com.foodrecipes.credentials.credentials.dto.ReviewCommentUpdateRequestDTO;
import com.foodrecipes.credentials.credentials.entity.ReviewCommentC;
import com.foodrecipes.credentials.credentials.service.ReviewCommentService;
import com.foodrecipes.credentials.credentials.entity.Review;
import com.foodrecipes.credentials.credentials.service.ReviewService;

/**
 * Controller for managing review comments.
 * Provides APIs to add, update, delete, and retrieve comments with pagination.
 */
@RestController
@RequestMapping("api/comment")
public class ReviewCommentController {

    @Autowired
    private ReviewCommentService reviewCommentService;

    @Autowired
    private ReviewService reviewService;
    
    /**
     * Adds a new comment to a review.
     *
     * @param requestDTO DTO containing the review ID, user ID, and comment text.
     * @return ResponseEntity with the created comment details or an error message.
     */
    @PostMapping("/add-comment")
    public ResponseEntity<?> addComment(@RequestBody ReviewCommentRequestDTO requestDTO) {
        try {
            // Verify the review exists
            Review review = reviewService.getReviewById(requestDTO.getReviewId());
            if (review == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Review not found");
            }
            
            ReviewCommentResponseDTO reviewComment = reviewCommentService.addComment(requestDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(reviewComment);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                               .body("Failed to add comment: " + e.getMessage());
        }
    }

    /**
     * Deletes a comment by its ID.
     *
     * @param commentId The ID of the comment to be deleted.
     * @return ResponseEntity with a success message or an error message.
     */
    @DeleteMapping("/delete-comment/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable Long commentId) {
        try {
            reviewCommentService.deleteComment(commentId);
            return ResponseEntity.ok("Comment with ID " + commentId + " deleted successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete comment.");
        }
    }

    /**
     * Updates an existing comment.
     *
     * @param requestDTO DTO containing the comment ID and updated text.
     * @return ResponseEntity with updated comment details or an error message.
     */
    @PutMapping("/update-comment")
    public ResponseEntity<?> updateComment(@RequestBody ReviewCommentUpdateRequestDTO requestDTO) {
        try {
            ReviewCommentResponseDTO updatedComment = reviewCommentService.updateComment(
                requestDTO.getCommentId(), 
                requestDTO
            );
            return ResponseEntity.ok(updatedComment);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update comment.");
        }
    }

    /**
     * Retrieves comments for a specific review using cursor-based pagination.
     *
     * @param reviewId The ID of the review whose comments are being fetched.
     * @param page The page number for pagination (default: 0).
     * @param referenceTime The timestamp for cursor-based pagination. If not provided, defaults to the current time.
     * @return ResponseEntity with a paginated list of comments or an error message.
     */
    @GetMapping("/get-comments/{reviewId}")
    public ResponseEntity<?> getCommentsByReviewId(
            @PathVariable Long reviewId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime referenceTime) {
        try {
            // Verify the review exists
            Review review = reviewService.getReviewById(reviewId);
            if (review == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Review not found");
            }

            // Default reference time to current timestamp if not provided
            if (referenceTime == null) {
                referenceTime = LocalDateTime.now();
            }

            Page<ReviewCommentC> comments = reviewCommentService.getCommentsByReviewId(
                reviewId, 
                referenceTime, 
                page
            );

            if (comments.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No comments found for review ID " + reviewId);
            }

            Page<ReviewCommentResponseDTO> commentDTOs = comments.map(this::mapToDTO);
            return ResponseEntity.ok(commentDTOs);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                               .body("Failed to fetch comments: " + e.getMessage());
        }
    }

    /**
     * Helper method to convert ReviewComment entity to ReviewCommentResponseDTO.
     *
     * @param reviewComment The ReviewComment entity.
     * @return A DTO representation of the review comment.
     */
    private ReviewCommentResponseDTO mapToDTO(ReviewCommentC reviewComment) {
        return new ReviewCommentResponseDTO(
                reviewComment.getId(),
                reviewComment.getReview().getId(),
                reviewComment.getUserId(),
                reviewComment.getComment(),
                reviewComment.getCreatedAt()
        );
    }
}
