package com.foodrecipes.credentials.credentials.restcontrollers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.foodrecipes.credentials.credentials.dto.UserProfileResponseProfileGetterDTO;
import com.foodrecipes.credentials.credentials.service.UserProfileService;

/**
 * Controller responsible for retrieving user profile details based on user IDs.
 * This API allows fetching multiple user profiles in a single request. 
 */
@RestController
@RequestMapping("/profile-getter")
public class ProfileGetterController {

    @Autowired
    private UserProfileService userProfileService;

    /**
     * Fetches user profiles by a list of user IDs.
     *
     * @param userIds A list of user IDs for which profile details are requested.
     * @return ResponseEntity containing a list of user profiles if successful,
     *         or an error message if too many IDs are requested.
     */
    @PostMapping("/fetch-ids")
    public ResponseEntity<?> getUserProfiles(@RequestBody List<Long> userIds) {
        try {
            // Fetch user profiles from the service
            List<UserProfileResponseProfileGetterDTO> profiles = userProfileService.getUserProfilesByIds(userIds);
            return ResponseEntity.ok(profiles);
        } catch (IllegalArgumentException e) {
            // Handle case when more than 10 user IDs are requested
            return ResponseEntity.badRequest().body("{\"error\": \"At most 10 user IDs can be requested at once\"}");
        }
    }
}
