package com.profile_getter.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.profile_getter.dto.UserProfileResponseDTO;
import com.profile_getter.entity.UserProfile;
import com.profile_getter.repository.UserProfileRepository;


@Service
public class UserProfileService {
	
	@Autowired
    private UserProfileRepository userProfileRepository; 

	public List<UserProfileResponseDTO> getUserProfilesByIds(List<Long> userIds) {
        if (userIds.size() > 10) {
            throw new IllegalArgumentException("At most 10 user IDs can be requested at once");
        }

        // Fetch profiles from DB (unordered)
        List<UserProfile> userProfiles = userProfileRepository.findByIdIn(userIds);

        // Convert list to a Map for quick lookup
        Map<Long, UserProfile> userProfileMap = userProfiles.stream()
            .collect(Collectors.toMap(UserProfile::getId, profile -> profile));

        // Maintain the original order by mapping userIds to the fetched profiles
        return userIds.stream()
            .map(id -> userProfileMap.getOrDefault(id, null)) // Ensure order is preserved
            .filter(profile -> profile != null) // Remove null values for missing profiles
            .map(profile -> new UserProfileResponseDTO(profile.getId(), profile.getUsername(), profile.getProfileImage()))
            .collect(Collectors.toList());
    }
}
