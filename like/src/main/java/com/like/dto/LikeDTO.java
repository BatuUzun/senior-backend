package com.like.dto;

public class LikeDTO {
    private Long userId;
    private String spotifyId;
    private String type; // New field

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LikeDTO(Long userId, String spotifyId, String type) {
        this.userId = userId;
        this.spotifyId = spotifyId;
        this.type = type;
    }

    public LikeDTO() {}
}
