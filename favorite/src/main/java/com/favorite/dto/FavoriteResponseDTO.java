package com.favorite.dto;

import java.time.LocalDateTime;

public class FavoriteResponseDTO {
    private Long id;
    private Long userId;
    private String spotifyId;
    private LocalDateTime createdAt;

    // Getters and Setters
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

	public FavoriteResponseDTO(Long id, Long userId, String spotifyId, LocalDateTime createdAt) {
		super();
		this.id = id;
		this.userId = userId;
		this.spotifyId = spotifyId;
		this.createdAt = createdAt;
	}

	public FavoriteResponseDTO() {
		super();
	}
    
    
}