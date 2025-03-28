package com.foodrecipes.credentials.credentials.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.foodrecipes.credentials.credentials.entity.UserProfileProfileAPI;
import com.foodrecipes.credentials.credentials.repository.UserProfileProfileApiRepository;

import jakarta.transaction.Transactional;


@Service
public class UserProfileProfileApiService {
	
	@Autowired
    private UserProfileProfileApiRepository userProfileRepository; 
	
	
	public UserProfileProfileAPI getUserProfileById(Long id) {
        return userProfileRepository.findById(id).orElse(null);        
	}
	
	/*public UserProfile getUserProfileByEmail(String email) {
		return userProfileRepository.findByUserEmail(email);	
	}*/
	
	@Transactional
    public void updateProfilePicture(Long userProfileId, String newProfileImage) {
        userProfileRepository.updateProfileImage(userProfileId, newProfileImage);
    }
    
    public boolean isUserProfileExist(Long id) {
        return userProfileRepository.existsById(id);        
	}
    
    public String getUserProfilePictureById(Long id) {
    	return userProfileRepository.findUserProfileImageById(id);
    }
    
    
    
    public String getProfilePictureByUserId(Long userId) {
        return userProfileRepository.findById(userId)
                .map(UserProfileProfileAPI::getProfileImage)
                .orElseThrow(() -> new RuntimeException("UserProfile not found with id: " + userId));
    }
    
    
    
    public List<UserProfileProfileAPI> getAllProfiles() {
        return userProfileRepository.findAll();
    }

    public Optional<UserProfileProfileAPI> getProfileById(Long id) {
        return userProfileRepository.findById(id);
    }

    public UserProfileProfileAPI createProfile(UserProfileProfileAPI profile) {
        return userProfileRepository.save(profile);
    }

    public UserProfileProfileAPI updateProfile(Long id, UserProfileProfileAPI profileDetails) {
        UserProfileProfileAPI profile = userProfileRepository.findById(id).orElseThrow(() -> new RuntimeException("Profile not found"));
        profile.setUsername(profileDetails.getUsername());
        profile.setDescription(profileDetails.getDescription());
        profile.setBio(profileDetails.getBio());
        profile.setLink(profileDetails.getLink());
        profile.setLocation(profileDetails.getLocation());
        profile.setProfileImage(profileDetails.getProfileImage());
        return userProfileRepository.save(profile);
    }

    public void deleteProfile(Long id) {
    	userProfileRepository.deleteById(id);
    }

}
