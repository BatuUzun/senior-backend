package com.like.dto;

public class LikeDTO {
    private Long userId;
    private String spotifyId;

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

	public LikeDTO(Long userId, String spotifyId) {
		super();
		this.userId = userId;
		this.spotifyId = spotifyId;
	}

	public LikeDTO() {
		super();
	}
    
    
}

