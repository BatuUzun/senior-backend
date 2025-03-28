package com.foodrecipes.credentials.credentials.dto;

public class UserReviewCountDTO {
    private Long userId;
    private long reviewCount;

    public UserReviewCountDTO(Long userId, long reviewCount) {
        this.userId = userId;
        this.reviewCount = reviewCount;
    }

    public Long getUserId() {
        return userId;
    }

    public long getReviewCount() {
        return reviewCount;
    }
}
