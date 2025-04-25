package com.foodrecipes.credentials.credentials.dto;

import java.time.LocalDateTime;

public class ReviewCommentActivityDTO {
    private Long id;
    private Long userId;
    private Long reviewId;
    private LocalDateTime createdAt;
    private String comment;

    public ReviewCommentActivityDTO(Long id, Long userId, Long reviewId, LocalDateTime createdAt, String comment) {
        this.id = id;
        this.userId = userId;
        this.reviewId = reviewId;
        this.createdAt = createdAt;
        this.comment = comment;
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

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getReviewId() {
		return reviewId;
	}

	public void setReviewId(Long reviewId) {
		this.reviewId = reviewId;
	}

    // Getters and setters
}