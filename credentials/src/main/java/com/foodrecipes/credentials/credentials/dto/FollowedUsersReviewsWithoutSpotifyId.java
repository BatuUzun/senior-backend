package com.foodrecipes.credentials.credentials.dto;

import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

public class FollowedUsersReviewsWithoutSpotifyId {
    private Long userId;
    private int page; // Pagination

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime cursor;

    public FollowedUsersReviewsWithoutSpotifyId() {
    }

    public FollowedUsersReviewsWithoutSpotifyId(Long userId, LocalDateTime cursor, int page) {
        this.userId = userId;
        this.cursor = cursor;
        this.page = page;
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public LocalDateTime getCursor() { return cursor; }
    public void setCursor(LocalDateTime cursor) { this.cursor = cursor; }

    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }
}
