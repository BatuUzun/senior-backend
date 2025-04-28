package com.foodrecipes.credentials.credentials.dto;

import java.time.LocalDateTime;

public class ReviewDTO {
    private Long id;
    private Long userId;
    private String spotifyId;
    private Double rating;
    private String comment;
    private LocalDateTime createdAt;
    public ReviewDTO(Long id, Long userId, String spotifyId, Double rating, String comment, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.spotifyId = spotifyId;
        this.rating = rating;
        this.comment = comment;
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

	public String getSpotifyId() {
		return spotifyId;
	}

	public void setSpotifyId(String spotifyId) {
		this.spotifyId = spotifyId;
	}

	public Double getRating() {
		return rating;
	}

	public void setRating(Double rating) {
		this.rating = rating;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

    // Getters and setters
}