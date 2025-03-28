package com.foodrecipes.credentials.credentials.dto;

import jakarta.validation.constraints.NotNull;

public class ReviewLikeRequestDTO {

    @NotNull
    private Long userId;

    @NotNull
    private Long reviewId;

    public ReviewLikeRequestDTO() {}

    public ReviewLikeRequestDTO(Long userId, Long reviewId) {
        this.userId = userId;
        this.reviewId = reviewId;
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
}
