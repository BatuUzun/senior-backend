package com.like.dto;

public class LikeResponseDTO {
    private Long userId;
    private String spotifyId;
    private String createdAt;

    // Getters and Setters
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

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

	public LikeResponseDTO(Long userId, String spotifyId, String createdAt) {
		super();
		this.userId = userId;
		this.spotifyId = spotifyId;
		this.createdAt = createdAt;
	}

	public LikeResponseDTO() {
		super();
	}
    
    
}
