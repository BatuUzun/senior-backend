package com.foodrecipes.credentials.credentials.dto;

public class UserProfileResponseProfileGetterDTO {
    private Long userId;
    private String username;
    private String profileImage;

    public UserProfileResponseProfileGetterDTO(Long userId, String username, String profileImage) {
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
