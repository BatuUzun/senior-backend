package com.comment.dto;

import java.time.LocalDateTime;

public class ReviewCommentResponseDTO {
    private Long id;
    private Long reviewId;
    private Long userId;
    private String comment;
    private LocalDateTime createdAt;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getReviewId() {
        return reviewId;
    }

    public void setReviewId(Long reviewId) {
        this.reviewId = reviewId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

	public ReviewCommentResponseDTO(Long id, Long reviewId, Long userId, String comment, LocalDateTime createdAt) {
		super();
		this.id = id;
		this.reviewId = reviewId;
		this.userId = userId;
		this.comment = comment;
		this.createdAt = createdAt;
	}

	public ReviewCommentResponseDTO() {
		super();
	}
    
    
}