package com.favorite.dto;

public class FavoriteDTO {
    private Long userId;
    private String spotifyId;
    private String type; // New field

    // Constructors
    public FavoriteDTO() {}

    public FavoriteDTO(Long userId, String spotifyId, String type) {
        this.userId = userId;
        this.spotifyId = spotifyId;
        this.type = type;
    }

    // Getters and Setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getSpotifyId() { return spotifyId; }
    public void setSpotifyId(String spotifyId) { this.spotifyId = spotifyId; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}
