package com.foodrecipes.credentials.credentials.dto;

public class IsLikedResponseDto {
    private Long id;
    private Long userId;

    public IsLikedResponseDto(Long id, Long userId) {
        this.id = id;
        this.userId = userId;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }
}
