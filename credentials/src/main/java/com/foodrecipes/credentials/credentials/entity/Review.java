package com.foodrecipes.credentials.credentials.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "review")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false)
    private double rating;

    @Column(length = 1000)
    private String comment;

    @Column(name = "spotify_id", nullable = false)
    private String spotifyId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

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

	public double getRating() {
		return rating;
	}

	public void setRating(double d) {
		this.rating = d;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
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

	public Review(Long id, Long userId, double rating, String comment, String spotifyId, LocalDateTime createdAt) {
		super();
		this.id = id;
		this.userId = userId;
		this.rating = rating;
		this.comment = comment;
		this.spotifyId = spotifyId;
		this.createdAt = createdAt;
	}

	public Review() {
		super();
	}

    // Getters and Setters
    
    
}
