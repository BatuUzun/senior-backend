package com.foodrecipes.credentials.credentials.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class PagedResponseActivity {
	private List<UnifiedActivityDTO> activities;
    private Map<String, Object> rawData; // Optional: keep original grouped map if needed
    private LocalDateTime reviewNextCursor;
    private LocalDateTime reviewLikeNextCursor;
    private LocalDateTime likeNextCursor;
    private LocalDateTime reviewCommentNextCursor;

    public PagedResponseActivity(List<UnifiedActivityDTO> activities,
                                 Map<String, Object> rawData,
                                 LocalDateTime reviewNextCursor,
                                 LocalDateTime reviewLikeNextCursor,
                                 LocalDateTime likeNextCursor,
                                 LocalDateTime reviewCommentNextCursor) {
        this.activities = activities;
        this.rawData = rawData;
        this.reviewNextCursor = reviewNextCursor;
        this.reviewLikeNextCursor = reviewLikeNextCursor;
        this.likeNextCursor = likeNextCursor;
        this.reviewCommentNextCursor = reviewCommentNextCursor;
    }

	public LocalDateTime getReviewCommentNextCursor() {
		return reviewCommentNextCursor;
	}

	public void setReviewCommentNextCursor(LocalDateTime reviewCommentNextCursor) {
		this.reviewCommentNextCursor = reviewCommentNextCursor;
	}

	public List<UnifiedActivityDTO> getActivities() {
		return activities;
	}

	public void setActivities(List<UnifiedActivityDTO> activities) {
		this.activities = activities;
	}

	public Map<String, Object> getRawData() {
		return rawData;
	}

	public void setRawData(Map<String, Object> rawData) {
		this.rawData = rawData;
	}

	public LocalDateTime getReviewNextCursor() {
		return reviewNextCursor;
	}

	public void setReviewNextCursor(LocalDateTime reviewNextCursor) {
		this.reviewNextCursor = reviewNextCursor;
	}

	public LocalDateTime getReviewLikeNextCursor() {
		return reviewLikeNextCursor;
	}

	public void setReviewLikeNextCursor(LocalDateTime reviewLikeNextCursor) {
		this.reviewLikeNextCursor = reviewLikeNextCursor;
	}

	public LocalDateTime getLikeNextCursor() {
		return likeNextCursor;
	}

	public void setLikeNextCursor(LocalDateTime likeNextCursor) {
		this.likeNextCursor = likeNextCursor;
	}

    // Getters & Setters
}

