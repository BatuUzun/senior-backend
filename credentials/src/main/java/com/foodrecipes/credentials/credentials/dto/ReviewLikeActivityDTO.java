package com.foodrecipes.credentials.credentials.dto;

import java.time.LocalDateTime;

public class ReviewLikeActivityDTO {
    private Long id;
    private Long userId;
    private Long reviewId;
    private LocalDateTime createdAt;

    public ReviewLikeActivityDTO(Long id, Long userId, Long reviewId, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.reviewId = reviewId;
        this.createdAt = createdAt;
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