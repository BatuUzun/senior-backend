package com.foodrecipes.credentials.credentials.dto;

import java.time.LocalDateTime;
import java.util.List;

public class PagedReviewResponse {

    private List<UnifiedActivityDTO> activities;
    private LocalDateTime nextCursor;

    public PagedReviewResponse(List<UnifiedActivityDTO> activities, LocalDateTime nextCursor) {
        this.activities = activities;
        this.nextCursor = nextCursor;
    }

    public List<UnifiedActivityDTO> getActivities() {
        return activities;
    }

    public void setActivities(List<UnifiedActivityDTO> activities) {
        this.activities = activities;
    }

    public LocalDateTime getNextCursor() {
        return nextCursor;
    }

    public void setNextCursor(LocalDateTime nextCursor) {
        this.nextCursor = nextCursor;
    }
}