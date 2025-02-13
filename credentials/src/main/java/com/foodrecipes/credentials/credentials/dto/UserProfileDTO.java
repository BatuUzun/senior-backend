package com.foodrecipes.credentials.credentials.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class UserProfileDTO {
	
	@NotNull(message = "User ID is required")
	private Long userId;

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    @Size(max = 255, message = "Description cannot exceed 255 characters")
    private String description;

    @Size(max = 255, message = "Bio cannot exceed 255 characters")
    private String bio;

    @Size(max = 2083, message = "Link must be a valid URL and cannot exceed 2083 characters")
    private String link;

    @Size(max = 100, message = "Location cannot exceed 100 characters")
    private String location;

    

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}
}
