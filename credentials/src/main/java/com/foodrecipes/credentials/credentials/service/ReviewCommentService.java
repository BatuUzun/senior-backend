package com.foodrecipes.credentials.credentials.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.foodrecipes.credentials.credentials.constants.Constants;
import com.foodrecipes.credentials.credentials.dto.ReviewCommentRequestDTO;
import com.foodrecipes.credentials.credentials.dto.ReviewCommentResponseDTO;
import com.foodrecipes.credentials.credentials.dto.ReviewCommentUpdateRequestDTO;
import com.foodrecipes.credentials.credentials.entity.ReviewC;
import com.foodrecipes.credentials.credentials.entity.ReviewCommentC;
import com.foodrecipes.credentials.credentials.repository.ReviewCRepository;
import com.foodrecipes.credentials.credentials.repository.ReviewCommentRepository;

import jakarta.transaction.Transactional;

@Service
public class ReviewCommentService {

	@Autowired
	private ReviewCommentRepository reviewCommentRepository;
	@Autowired
	private ReviewCRepository reviewRepository;
	private static final Logger logger = LoggerFactory.getLogger(ReviewCommentService.class);

	@Autowired
	private ReviewService reviewService;
	// Add a new comment
	@Transactional
	public ReviewCommentResponseDTO addComment(ReviewCommentRequestDTO requestDTO) {
		Optional<ReviewC> optionalReview = reviewRepository.findById(requestDTO.getReviewId());
		if (optionalReview.isEmpty()) {
			throw new IllegalArgumentException("Review not found.");
		}

		ReviewC review = optionalReview.get();
		ReviewCommentC reviewComment = new ReviewCommentC();
		reviewComment.setReview(review);
		reviewComment.setUserId(requestDTO.getUserId());
		reviewComment.setComment(requestDTO.getComment());

		try {
			ReviewCommentC savedComment = reviewCommentRepository.save(reviewComment);
			return mapToResponseDTO(savedComment);
		} catch (Exception e) {
			logger.error("Error while adding comment: {}", e.getMessage());
			throw new RuntimeException("Failed to add comment.");
		}
	}

	// ✅ Delete a comment and update Redis cache
	@Transactional
	public void deleteComment(Long commentId) {
		Optional<ReviewCommentC> optionalComment = reviewCommentRepository.findById(commentId);
		if (optionalComment.isEmpty()) {
			throw new IllegalArgumentException("Comment not found.");
		}

		ReviewCommentC comment = optionalComment.get();
		try {
			reviewCommentRepository.delete(comment);
		} catch (Exception e) {
			logger.error("Error deleting comment: {}", e.getMessage());
			throw new RuntimeException("Failed to delete comment.");
		}
	}

	public List<Long> getTop100PopularReviewsBySpotifyId(String spotifyId) {
		Pageable top100 = PageRequest.of(0, Constants.POPULER_SIZE); // first page of size 100
		return reviewCommentRepository.findTopPopularReviewIdsBySpotifyId(spotifyId, top100).getContent();
	}

	// Update a comment
	@Transactional
	public ReviewCommentResponseDTO updateComment(Long commentId, ReviewCommentUpdateRequestDTO requestDTO) {
		Optional<ReviewCommentC> optionalComment = reviewCommentRepository.findById(commentId);
		if (optionalComment.isEmpty()) {
			throw new IllegalArgumentException("Comment with ID " + commentId + " does not exist.");
		}

		ReviewCommentC reviewComment = optionalComment.get();
		ReviewC review = reviewComment.getReview(); // Get the associated review

		if (review == null) {
			throw new IllegalArgumentException("Associated review does not exist.");
		}

		reviewComment.setComment(requestDTO.getNewComment());
		ReviewCommentC updatedComment = reviewCommentRepository.save(reviewComment);

		return mapToResponseDTO(updatedComment);
	}

	// Fetch comments by review ID with pagination and cursor
	public Page<ReviewCommentC> getCommentsByReviewId(Long reviewId, LocalDateTime referenceTime, int page) {
		validateReviewExists(reviewId);

		Pageable pageable = PageRequest.of(page, Constants.PAGE_SIZE,
				Sort.by(Sort.Direction.ASC, "createdAt").and(Sort.by(Sort.Direction.ASC, "id")));
		return reviewCommentRepository.findByReviewIdWithReference(reviewId, referenceTime, pageable);
	}

	

	// Validate if the review ID exists via proxy
	private void validateReviewExists(Long reviewId) {
		try {
			reviewService.getReviewById(reviewId);
		} catch (Exception e) {
			throw new IllegalArgumentException("Review with ID " + reviewId + " does not exist.");
		}
	}

	// Map entity to response DTO
	private ReviewCommentResponseDTO mapToResponseDTO(ReviewCommentC reviewComment) {
	    return new ReviewCommentResponseDTO(
	            reviewComment.getId(),
	            reviewComment.getReview().getId(),  // Make sure this isn't null
	            reviewComment.getUserId(),
	            reviewComment.getComment(),
	            reviewComment.getCreatedAt()
	    );
	}
}
