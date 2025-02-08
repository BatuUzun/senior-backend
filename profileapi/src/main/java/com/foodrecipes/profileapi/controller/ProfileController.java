package com.foodrecipes.profileapi.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.foodrecipes.profileapi.constants.Constants;
import com.foodrecipes.profileapi.entity.UserProfile;
import com.foodrecipes.profileapi.proxy.Amazons3Proxy;
import com.foodrecipes.profileapi.response.ResultResponse;
import com.foodrecipes.profileapi.service.UserProfileService;

@RestController
@RequestMapping("/profile-api")
public class ProfileController {
	
	@Autowired
	private Amazons3Proxy profileProxy;
	
	@Autowired
    private UserProfileService userProfileService;
	
	@PostMapping("/change-profile-picture")
	public String changeProfilePicture(@RequestParam("file") MultipartFile file, @RequestParam("userId") Long userProfileId) {
		ResultResponse response = null;
		UserProfile userProfile = userProfileService.getUserProfileById(userProfileId);
		if(userProfile != null) {
			String currentPP = userProfile.getProfileImage();
			if(!currentPP.equals(Constants.DEFAULT_PROFILE_IMAGE)) {
				profileProxy.delete(currentPP);
			}
			
			response = profileProxy.upload(file);
		    
		    String imageName = "";
		    if (response.getResult() instanceof String) {
		        imageName = (String) response.getResult();
		    }
		    userProfileService.updateProfilePicture(userProfileId, imageName);
		    // You can now use imageName as needed
		    System.out.println("Uploaded image name: " + imageName);
		    System.out.println("User ID: " + userProfileId);
		    return imageName;
		}
	    return "";
	}
	
	
	
	
	
	/*@GetMapping("/get-user-profile-by-email/")
	public ResponseEntity<UserProfile> getUserProfileByEmail(@RequestParam String email) {
        UserProfile userProfile = userProfileService.getUserProfileByEmail(email);
        if (userProfile != null) {
            return ResponseEntity.ok(userProfile);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }*/
	
	@GetMapping("/is-user-exist-by-id/")
	public Boolean isUserExistById(@RequestParam Long id) {
        return userProfileService.isUserProfileExist(id);
        
    }
	
	@GetMapping("/get-user-profile/{id}")
	public UserProfile getUserProfileById(@PathVariable("id") Long id) {
        return userProfileService.getUserProfileById(id);
    }
	
	
	
	@GetMapping("/{userId}/profile-picture")
    public ResponseEntity<String> getProfilePicture(@PathVariable Long userId) {
        String profilePicture = userProfileService.getProfilePictureByUserId(userId);
        return profilePicture != null 
            ? ResponseEntity.ok(profilePicture) 
            : ResponseEntity.notFound().build(); // Return 404 if not found
    }
	
	
	
	
	
	
	
	
	
	@GetMapping("/get-all-profiles")
    public List<UserProfile> getAllProfiles() {
        return userProfileService.getAllProfiles();
    }

    @GetMapping("/get-profile-by-id/{id}")
    public ResponseEntity<UserProfile> getProfileById(@PathVariable Long id) {
        return userProfileService.getProfileById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/create-profile")
    public UserProfile createProfile(@RequestBody UserProfile profile) {
        return userProfileService.createProfile(profile);
    }

    @PutMapping("/update-profile/{id}")
    public ResponseEntity<UserProfile> updateProfile(@PathVariable Long id, @RequestBody UserProfile profileDetails) {
        try {
            UserProfile updatedProfile = userProfileService.updateProfile(id, profileDetails);
            return ResponseEntity.ok(updatedProfile);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/delete-profile/{id}")
    public ResponseEntity<Void> deleteProfile(@PathVariable Long id) {
    	userProfileService.deleteProfile(id);
        return ResponseEntity.noContent().build();
    }
	
	

}
