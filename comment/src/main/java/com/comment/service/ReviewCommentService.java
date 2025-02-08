package com.comment.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.comment.constant.Constants;
import com.comment.dto.ReviewCommentRequestDTO;
import com.comment.dto.ReviewCommentResponseDTO;
import com.comment.dto.ReviewCommentUpdateRequestDTO;
import com.comment.entity.ReviewComment;
import com.comment.proxy.ReviewProxy;
import com.comment.repository.ReviewCommentRepository;

import jakarta.transaction.Transactional;

@Service
public class ReviewCommentService {

    @Autowired
    private ReviewCommentRepository reviewCommentRepository;

    @Autowired
    private ReviewProxy reviewProxy;

    // Add a new comment
    public ReviewCommentResponseDTO addComment(ReviewCommentRequestDTO requestDTO) {
        validateReviewExists(requestDTO.getReviewId());

        ReviewComment reviewComment = new ReviewComment();
        reviewComment.setReviewId(requestDTO.getReviewId());
        reviewComment.setUserId(requestDTO.getUserId());
        reviewComment.setComment(requestDTO.getComment());
        ReviewComment savedComment = reviewCommentRepository.save(reviewComment);

        return mapToResponseDTO(savedComment);
    }

    // Delete a comment
    @Transactional
    public void deleteComment(Long commentId) {
        if (!reviewCommentRepository.existsById(commentId)) {
            throw new IllegalArgumentException("Comment with ID " + commentId + " does not exist.");
        }
        reviewCommentRepository.deleteById(commentId);
    }

    // Update a comment
    @Transactional
    public ReviewCommentResponseDTO updateComment(Long commentId, ReviewCommentUpdateRequestDTO requestDTO) {
        Optional<ReviewComment> optionalComment = reviewCommentRepository.findById(commentId);
        if (optionalComment.isEmpty()) {
            throw new IllegalArgumentException("Comment with ID " + commentId + " does not exist.");
        }

        validateReviewExists(optionalComment.get().getReviewId());

        ReviewComment reviewComment = optionalComment.get();
        reviewComment.setComment(requestDTO.getNewComment());
        ReviewComment updatedComment = reviewCommentRepository.save(reviewComment);
        return mapToResponseDTO(updatedComment);
    }

    // Fetch comments by review ID with pagination and cursor
    public Page<ReviewComment> getCommentsByReviewId(Long reviewId, LocalDateTime referenceTime, int page) {
        validateReviewExists(reviewId);

        Pageable pageable = PageRequest.of(page, Constants.PAGE_SIZE, 
                Sort.by(Sort.Direction.ASC, "createdAt").and(Sort.by(Sort.Direction.ASC, "id")));
        return reviewCommentRepository.findByReviewIdWithReference(reviewId, referenceTime, pageable);
    }

    // Validate if the review ID exists via proxy
    private void validateReviewExists(Long reviewId) {
        try {
            reviewProxy.getReviewById(reviewId);
        } catch (Exception e) {
            throw new IllegalArgumentException("Review with ID " + reviewId + " does not exist.");
        }
    }

    // Map entity to response DTO
    private ReviewCommentResponseDTO mapToResponseDTO(ReviewComment reviewComment) {
        ReviewCommentResponseDTO dto = new ReviewCommentResponseDTO();
        dto.setId(reviewComment.getId());
        dto.setReviewId(reviewComment.getReviewId());
        dto.setUserId(reviewComment.getUserId());
        dto.setComment(reviewComment.getComment());
        dto.setCreatedAt(reviewComment.getCreatedAt());
        return dto;
    }
}
