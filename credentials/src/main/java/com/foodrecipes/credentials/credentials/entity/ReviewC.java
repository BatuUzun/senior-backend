package com.foodrecipes.credentials.credentials.entity;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;

@Entity
@Table(name = "review")
public class ReviewC {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false)
    private double rating;

    @Column(name = "spotify_id", nullable = false)
    private String spotifyId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<ReviewCommentC> comments;

    // Constructors
    public ReviewC() {}

    public ReviewC(Long id, Long userId, double rating, String spotifyId, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.rating = rating;
        this.spotifyId = spotifyId;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public String getSpotifyId() { return spotifyId; }
    public void setSpotifyId(String spotifyId) { this.spotifyId = spotifyId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public List<ReviewCommentC> getComments() { return comments; }
    public void setComments(List<ReviewCommentC> comments) { this.comments = comments; }
}
