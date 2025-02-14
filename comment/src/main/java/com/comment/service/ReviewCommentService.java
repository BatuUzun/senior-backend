package com.comment.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import com.comment.constant.Constants;
import com.comment.dto.ReviewCommentRequestDTO;
import com.comment.dto.ReviewCommentResponseDTO;
import com.comment.dto.ReviewCommentUpdateRequestDTO;
import com.comment.entity.Review;
import com.comment.entity.ReviewComment;
import com.comment.proxy.ReviewProxy;
import com.comment.repository.ReviewCommentRepository;
import com.comment.repository.ReviewRepository;

import jakarta.transaction.Transactional;

@Service
public class ReviewCommentService {

    @Autowired
    private ReviewCommentRepository reviewCommentRepository;
    @Autowired
    private ReviewRepository reviewRepository;
    private static final Logger logger = LoggerFactory.getLogger(ReviewCommentService.class);
    
    @Autowired
    private RedisTemplate<String, Long> redisTemplate;

    
    
    private static final String REDIS_TOP_REVIEWS_PREFIX = "popular_reviews:";

    private ZSetOperations<String, Long> getZSetOperations() {
        return redisTemplate.opsForZSet();
    }

    @Autowired
    private ReviewProxy reviewProxy;
    
    

    // Add a new comment
    @Transactional
    public ReviewCommentResponseDTO addComment(ReviewCommentRequestDTO requestDTO) {
        Optional<Review> optionalReview = reviewRepository.findById(requestDTO.getReviewId());
        if (optionalReview.isEmpty()) {
            throw new IllegalArgumentException("Review not found.");
        }
        
        Review review = optionalReview.get();
        ReviewComment reviewComment = new ReviewComment();
        reviewComment.setReview(review);
        reviewComment.setUserId(requestDTO.getUserId());
        reviewComment.setComment(requestDTO.getComment());

        try {
            ReviewComment savedComment = reviewCommentRepository.save(reviewComment);
            updateTopReviewsInRedis(review.getSpotifyId(), review.getId(), 1); // Update Redis ZSET
            return mapToResponseDTO(savedComment);
        } catch (Exception e) {
            logger.error("Error while adding comment: {}", e.getMessage());
            throw new RuntimeException("Failed to add comment.");
        }
    }

    // ✅ Delete a comment and update Redis cache
    @Transactional
    public void deleteComment(Long commentId) {
        Optional<ReviewComment> optionalComment = reviewCommentRepository.findById(commentId);
        if (optionalComment.isEmpty()) {
            throw new IllegalArgumentException("Comment not found.");
        }

        ReviewComment comment = optionalComment.get();
        try {
            reviewCommentRepository.delete(comment);
            updateTopReviewsInRedis(comment.getReview().getSpotifyId(), comment.getReview().getId(), -1);
        } catch (Exception e) {
            logger.error("Error deleting comment: {}", e.getMessage());
            throw new RuntimeException("Failed to delete comment.");
        }
    }
    
    public List<Long> getTop100PopularReviewsBySpotifyId(String spotifyId) {
        String redisKey = REDIS_TOP_REVIEWS_PREFIX + spotifyId;
        Set<Long> topReviews = getZSetOperations().reverseRange(redisKey, 0, Constants.POPULER_SIZE); // Fetch top 100

        return (topReviews != null) ? new ArrayList<>(topReviews) : new ArrayList<>();
    }

    
    
    private void updateTopReviewsInRedis(String spotifyId, Long reviewId, int delta) {
        String redisKey = REDIS_TOP_REVIEWS_PREFIX + spotifyId;
        ZSetOperations<String, Long> zSetOperations = getZSetOperations();

        // Increment or decrement like count in Redis sorted set
        Double currentScore = zSetOperations.score(redisKey, reviewId);
        double newScore = (currentScore != null ? currentScore : 0) + delta;

        if (newScore <= 0) {
            zSetOperations.remove(redisKey, reviewId); // Remove if count becomes 0
        } else {
            zSetOperations.add(redisKey, reviewId, newScore); // Update count
        }
    }

    // Update a comment
    @Transactional
    public ReviewCommentResponseDTO updateComment(Long commentId, ReviewCommentUpdateRequestDTO requestDTO) {
        Optional<ReviewComment> optionalComment = reviewCommentRepository.findById(commentId);
        if (optionalComment.isEmpty()) {
            throw new IllegalArgumentException("Comment with ID " + commentId + " does not exist.");
        }

        ReviewComment reviewComment = optionalComment.get();
        Review review = reviewComment.getReview(); // Get the associated review

        if (review == null) {
            throw new IllegalArgumentException("Associated review does not exist.");
        }

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
        return new ReviewCommentResponseDTO(
            reviewComment.getId(),
            reviewComment.getReview().getId(),
            reviewComment.getUserId(),
            reviewComment.getComment(),
            reviewComment.getCreatedAt()
        );
    }
}
