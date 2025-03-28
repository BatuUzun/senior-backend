package com.foodrecipes.credentials.credentials.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.foodrecipes.credentials.credentials.dto.UserProfileResponseProfileGetterDTO;
import com.foodrecipes.credentials.credentials.entity.UserProfile;
import com.foodrecipes.credentials.credentials.entity.UserProfileProfileGetter;
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
    
    public List<UserProfileResponseProfileGetterDTO> getUserProfilesByIds(List<Long> userIds) {
        if (userIds.size() > 10) {
            throw new IllegalArgumentException("At most 10 user IDs can be requested at once");
        }

        // Fetch profiles from DB (unordered)
        List<UserProfileProfileGetter> userProfiles = userProfileRepository.findByIdIn(userIds);

        // Convert list to a Map for quick lookup
        Map<Long, UserProfileProfileGetter> userProfileMap = userProfiles.stream()
            .collect(Collectors.toMap(UserProfileProfileGetter::getId, profile -> profile));

        // Maintain the original order by mapping userIds to the fetched profiles
        return userIds.stream()
            .map(id -> userProfileMap.getOrDefault(id, null)) // Ensure order is preserved
            .filter(profile -> profile != null) // Remove null values for missing profiles
            .map(profile -> new UserProfileResponseProfileGetterDTO(profile.getId(), profile.getUsername(), profile.getProfileImage()))
            .collect(Collectors.toList());
    }

}
