package com.foodrecipes.credentials.credentials.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.foodrecipes.credentials.credentials.entity.UserProfile;
import com.foodrecipes.credentials.credentials.repository.UserProfileRepository;

@Service
public class UserProfileService {

    @Autowired
    private UserProfileRepository userProfileRepository;
    
    public Boolean isUserProfileExist(UserProfile userProfile) {
        return userProfileRepository.existsByUsername(userProfile.getUsername());
    }
    
    public void createUserProfile(UserProfile userProfile) {
    	userProfileRepository.save(userProfile);
    }
    
    public UserProfile getUserProfileByUserId(Long userId) {
    	return userProfileRepository.findByUserId(userId);
    }
    
    public UserProfile getUserProfileByToken(String token) {
        return userProfileRepository.findByToken(token);
    }
}