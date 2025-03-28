package com.foodrecipes.credentials.credentials.dto;

import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

public class FollowedReviewsRequestDTO {
    private Long userId;
    private String spotifyId;
    private int page; // New field for pagination

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime cursor;

    public FollowedReviewsRequestDTO() {
    }

    public FollowedReviewsRequestDTO(Long userId, String spotifyId, LocalDateTime cursor, int page) {
        this.userId = userId;
        this.spotifyId = spotifyId;
        this.cursor = cursor;
        this.page = page;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getSpotifyId() {
        return spotifyId;
    }

    public void setSpotifyId(String spotifyId) {
        this.spotifyId = spotifyId;
    }

    public LocalDateTime getCursor() {
        return cursor;
    }

    public void setCursor(LocalDateTime cursor) {
        this.cursor = cursor;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }
}
