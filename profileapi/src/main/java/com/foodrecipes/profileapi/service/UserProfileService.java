package com.foodrecipes.profileapi.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.foodrecipes.profileapi.entity.UserProfile;
import com.foodrecipes.profileapi.repository.UserProfileRepository;

import jakarta.transaction.Transactional;


@Service
public class UserProfileService {
	
	@Autowired
    private UserProfileRepository userProfileRepository; 
	
	
	public UserProfile getUserProfileById(Long id) {
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
    
    /*public List<UserProfile> getUserProfilesInOrder(List<Long> ids) {
        // Fetch all profiles by IDs
        List<UserProfile> profiles = userProfileRepository.findByIdIn(ids);
        
        // Create a map for quick lookup
        Map<Long, UserProfile> profileMap = profiles.stream()
                .collect(Collectors.toMap(UserProfile::getId, profile -> profile));

        // Order profiles according to the input IDs
        List<UserProfile> orderedProfiles = new ArrayList<>();
        for (Long id : ids) {
            UserProfile profile = profileMap.get(id);
            if (profile != null) {
                orderedProfiles.add(profile);
            }
        }

        return orderedProfiles;
    */
    
    public String getProfilePictureByUserId(Long userId) {
        return userProfileRepository.findById(userId)
                .map(UserProfile::getProfileImage)
                .orElseThrow(() -> new RuntimeException("UserProfile not found with id: " + userId));
    }
    
    
    
    public List<UserProfile> getAllProfiles() {
        return userProfileRepository.findAll();
    }

    public Optional<UserProfile> getProfileById(Long id) {
        return userProfileRepository.findById(id);
    }

    public UserProfile createProfile(UserProfile profile) {
        return userProfileRepository.save(profile);
    }

    public UserProfile updateProfile(Long id, UserProfile profileDetails) {
        UserProfile profile = userProfileRepository.findById(id).orElseThrow(() -> new RuntimeException("Profile not found"));
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
