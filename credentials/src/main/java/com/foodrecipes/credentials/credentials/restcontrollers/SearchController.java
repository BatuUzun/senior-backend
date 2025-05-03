package com.foodrecipes.credentials.credentials.restcontrollers;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.foodrecipes.credentials.credentials.entity.UserProfileSearch;
import com.foodrecipes.credentials.credentials.service.UserProfileServiceSearch;

/**
 * Controller for searching user profiles.
 * Provides an API to search for users by username.
 */
@RestController
@RequestMapping("/search-profile")
public class SearchController {

    @Autowired
    private UserProfileServiceSearch userProfileService;

    @Autowired
    private Environment environment;

    /**
     * Searches for user profiles by username.
     *
     * @param username The username or partial username to search for.
     * @return A list of UserProfile objects that match the search criteria.
     */
    @PostMapping("/search")
    public List<UserProfileSearch> searchUserProfiles(
        @RequestParam("username") String username,
        @RequestParam(value = "offset", defaultValue = "0") int offset,
        @RequestParam(value = "limit", defaultValue = "10") int limit
    ) {
        // Retrieve the port the service is running on for debugging purposes
        String port = environment.getProperty("local.server.port");
        System.out.println("port: " + port);

        // Pagination için PageRequest oluştur
        Pageable pageable = PageRequest.of(offset / limit, limit);

        // Search for users matching the provided username
        List<UserProfileSearch> list = userProfileService.searchUsers(username, pageable);

        // Debugging: Print profile images of retrieved user profiles
        for (UserProfileSearch userProfile : list) {
            System.out.println(userProfile.getProfileImage());
        }

        return list;
    }
}