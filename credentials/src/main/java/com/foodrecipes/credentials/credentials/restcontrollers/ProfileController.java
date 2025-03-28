package com.foodrecipes.credentials.credentials.restcontrollers;

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

import com.foodrecipes.credentials.credentials.constants.Constants;
import com.foodrecipes.credentials.credentials.dto.ResultResponse;
import com.foodrecipes.credentials.credentials.entity.UserProfileProfileAPI;
import com.foodrecipes.credentials.credentials.service.S3Service;
import com.foodrecipes.credentials.credentials.service.UserProfileProfileApiService;

/**
 * Controller for managing user profiles, including profile picture updates,
 * retrieving user profile details, and performing CRUD operations.
 */
@RestController
@RequestMapping("/profile-api")
public class ProfileController {

    @Autowired
    private S3Service s3Service;

    @Autowired
    private UserProfileProfileApiService userProfileService;

    /**
     * Changes a user's profile picture.
     *
     * @param file The new profile picture file.
     * @param userProfileId The ID of the user whose profile picture is being updated.
     * @return The name of the uploaded image if successful, or an empty string if the user does not exist.
     */
    @PostMapping("/change-profile-picture")
    public String changeProfilePicture(@RequestParam("file") MultipartFile file, @RequestParam("userId") Long userProfileId) {
        ResultResponse response = null;
        UserProfileProfileAPI userProfile = userProfileService.getUserProfileById(userProfileId);
        
        if (userProfile != null) {
            String currentPP = userProfile.getProfileImage();

            // If the user already has a profile picture, delete the old one from S3
            if (!currentPP.equals(Constants.DEFAULT_PROFILE_IMAGE)) {
                s3Service.delete(currentPP);
            }

            // Upload the new image to S3
            response = s3Service.upload(file);

            String imageName = "";
            if (response.getResult() instanceof String) {
                imageName = (String) response.getResult();
            }

            // Update the user's profile with the new profile picture
            userProfileService.updateProfilePicture(userProfileId, imageName);

            // Logging uploaded image details
            System.out.println("Uploaded image name: " + imageName);
            System.out.println("User ID: " + userProfileId);
            return imageName;
        }
        return "";
    }

    /**
     * Checks if a user exists by their ID.
     *
     * @param id The user ID to check.
     * @return true if the user exists, false otherwise.
     */
    @GetMapping("/is-user-exist-by-id/")
    public Boolean isUserExistById(@RequestParam Long id) {
        return userProfileService.isUserProfileExist(id);
    }

    /**
     * Retrieves a user's profile by ID.
     *
     * @param id The ID of the user profile.
     * @return The UserProfile object if found.
     */
    @GetMapping("/get-user-profile/{id}")
    public UserProfileProfileAPI getUserProfileById(@PathVariable("id") Long id) {
        return userProfileService.getUserProfileById(id);
    }

    /**
     * Retrieves the profile picture URL of a user.
     *
     * @param userId The ID of the user.
     * @return ResponseEntity containing the profile picture URL if found, or 404 Not Found.
     */
    @GetMapping("/{userId}/profile-picture")
    public ResponseEntity<String> getProfilePicture(@PathVariable Long userId) {
        String profilePicture = userProfileService.getProfilePictureByUserId(userId);
        return profilePicture != null 
            ? ResponseEntity.ok(profilePicture) 
            : ResponseEntity.notFound().build();
    }

    /**
     * Retrieves all user profiles.
     *
     * @return A list of all UserProfile objects.
     */
    @GetMapping("/get-all-profiles")
    public List<UserProfileProfileAPI> getAllProfiles() {
        return userProfileService.getAllProfiles();
    }

    /**
     * Retrieves a user profile by its ID.
     *
     * @param id The user profile ID.
     * @return ResponseEntity containing the user profile if found, or 404 Not Found.
     */
    @GetMapping("/get-profile-by-id/{id}")
    public ResponseEntity<UserProfileProfileAPI> getProfileById(@PathVariable Long id) {
        return userProfileService.getProfileById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Creates a new user profile.
     *
     * @param profile The user profile details.
     * @return The newly created UserProfile object.
     */
    @PostMapping("/create-profile")
    public UserProfileProfileAPI createProfile(@RequestBody UserProfileProfileAPI profile) {
        return userProfileService.createProfile(profile);
    }

    /**
     * Updates an existing user profile.
     *
     * @param id The ID of the profile to update.
     * @param profileDetails The updated profile details.
     * @return ResponseEntity containing the updated profile if successful, or 404 Not Found.
     */
    @PutMapping("/update-profile/{id}")
    public ResponseEntity<UserProfileProfileAPI> updateProfile(@PathVariable Long id, @RequestBody UserProfileProfileAPI profileDetails) {
        try {
            UserProfileProfileAPI updatedProfile = userProfileService.updateProfile(id, profileDetails);
            return ResponseEntity.ok(updatedProfile);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Deletes a user profile by ID.
     *
     * @param id The ID of the profile to delete.
     * @return ResponseEntity with no content if successful.
     */
    @DeleteMapping("/delete-profile/{id}")
    public ResponseEntity<Void> deleteProfile(@PathVariable Long id) {
        userProfileService.deleteProfile(id);
        return ResponseEntity.noContent().build();
    }
}
