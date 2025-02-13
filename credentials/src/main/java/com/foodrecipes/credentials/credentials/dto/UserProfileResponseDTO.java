package com.foodrecipes.credentials.credentials.dto;

public class UserProfileResponseDTO {
    private Long userId;
    private String username;
    private String description;
    private String bio;
    private String link;
    private String location;
    private String profileImage;

    public UserProfileResponseDTO(Long userId, String username, String description, String bio, String link, String location, String profileImage) {
        this.userId = userId;
        this.username = username;
        this.description = description;
        this.bio = bio;
        this.link = link;
        this.location = location;
        this.profileImage = profileImage;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getDescription() {
        return description;
    }

    public String getBio() {
        return bio;
    }

    public String getLink() {
        return link;
    }

    public String getLocation() {
        return location;
    }

    public String getProfileImage() {
        return profileImage;
    }
}
