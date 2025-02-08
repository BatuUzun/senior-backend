package com.comment.controller;

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

import com.comment.dto.ReviewCommentRequestDTO;
import com.comment.dto.ReviewCommentResponseDTO;
import com.comment.dto.ReviewCommentUpdateRequestDTO;
import com.comment.entity.ReviewComment;
import com.comment.service.ReviewCommentService;

@RestController
@RequestMapping("/comment")
public class ReviewCommentController {

	@Autowired
    private ReviewCommentService reviewCommentService;

    // Add a new comment
	@PostMapping("/add-comment")
    public ResponseEntity<?> addComment(@RequestBody ReviewCommentRequestDTO requestDTO) {
        try {
            ReviewCommentResponseDTO reviewComment = reviewCommentService.addComment(requestDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(reviewComment);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to add comment.");
        }
    }

    // Delete a comment
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

    // Update a comment
    @PutMapping("/update-comment/{commentId}")
    public ResponseEntity<?> updateComment(
            @PathVariable Long commentId,
            @RequestBody ReviewCommentUpdateRequestDTO requestDTO) {
        try {
            ReviewCommentResponseDTO updatedComment = reviewCommentService.updateComment(commentId, requestDTO);
            return ResponseEntity.ok(updatedComment);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update comment.");
        }
    }

    // Fetch comments by review ID with pagination and cursor
    @GetMapping("/get-comments/{reviewId}")
    public ResponseEntity<?> getCommentsByReviewId(
            @PathVariable Long reviewId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime referenceTime) {
        try {
            if (referenceTime == null) {
                referenceTime = LocalDateTime.now(); // Use the current time for the first request
            }
            Page<ReviewComment> comments = reviewCommentService.getCommentsByReviewId(reviewId, referenceTime, page);
            if (comments.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No comments found for review ID " + reviewId);
            }
            return ResponseEntity.ok(comments);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to fetch comments.");
        }
    }
}