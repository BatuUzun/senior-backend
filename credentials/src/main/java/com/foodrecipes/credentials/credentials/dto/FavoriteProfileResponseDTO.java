package com.foodrecipes.credentials.credentials.dto;

import java.time.LocalDateTime;

public class FavoriteProfileResponseDTO {
    private Long id;
    private String spotifyId;
    private String type;
    private LocalDateTime createdAt;

    public FavoriteProfileResponseDTO(Long id, String spotifyId, String type, LocalDateTime createdAt) {
        this.id = id;
        this.spotifyId = spotifyId;
        this.type = type;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public String getSpotifyId() { return spotifyId; }
    public String getType() { return type; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
