package com.foodrecipes.credentials.credentials.dto;

public class UserProfileResponseFollowDTO {
    private Long userId;
    private String username;
    private String profileImage;

    public UserProfileResponseFollowDTO(Long userId, String username, String profileImage) {
        this.userId = userId;
        this.username = username;
        this.profileImage = profileImage;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getProfileImage() {
        return profileImage;
    }
}
