package com.foodrecipes.credentials.credentials.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.foodrecipes.credentials.credentials.entity.UserProfile;
import com.foodrecipes.credentials.credentials.repository.UserProfileRepository;

@Service
public class UserProfileService {

    @Autowired
    private UserProfileRepository userProfileRepository;
    
    public Boolean isUserProfileExist(String username) {
        return userProfileRepository.existsByUsername(username);
    }
    
    public void createUserProfile(UserProfile userProfile) {
        userProfileRepository.save(userProfile);
    }
    
    public Optional<UserProfile> getUserProfileByUserId(Long userId) {
        return userProfileRepository.findByUserId(userId);
    }
    
    public Optional<UserProfile> getUserProfileByToken(String token) {
        return userProfileRepository.findByToken(token);
    }
    
    public Optional<UserProfile> findByUserId(Long userId) {
        return userProfileRepository.findByUserId(userId);
    }
    
    public boolean existsByUserId(Long userId) {
        return userProfileRepository.existsByUserId(userId);
    }

}
