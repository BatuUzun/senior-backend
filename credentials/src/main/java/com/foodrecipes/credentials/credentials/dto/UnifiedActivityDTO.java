package com.foodrecipes.credentials.credentials.dto;

import java.time.LocalDateTime;

public class UnifiedActivityDTO {
    private String type;
    private Long id;
    private Long userId;
    private LocalDateTime createdAt;
    private Object details;
    private UserProfileResponseProfileGetterDTO userProfile; // ✅ Add this field

    public UnifiedActivityDTO(String type, Long id, Long userId, LocalDateTime createdAt, Object details,
                              UserProfileResponseProfileGetterDTO userProfile) {
        this.type = type;
        this.id = id;
        this.userId = userId;
        this.createdAt = createdAt;
        this.details = details;
        this.userProfile = userProfile;
    }

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
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

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public Object getDetails() {
		return details;
	}

	public void setDetails(Object details) {
		this.details = details;
	}

	public UserProfileResponseProfileGetterDTO getUserProfile() {
		return userProfile;
	}

	public void setUserProfile(UserProfileResponseProfileGetterDTO userProfile) {
		this.userProfile = userProfile;
	}

    // Getters and setters...
}
