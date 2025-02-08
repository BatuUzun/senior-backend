package com.foodrecipes.searchprofile.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.foodrecipes.searchprofile.dto.SearchCriteria;
import com.foodrecipes.searchprofile.entity.UserProfile;
import com.foodrecipes.searchprofile.service.UserProfileService;

@RestController
@RequestMapping("/search-profile")
public class SearchController {
	
	@Autowired
    private UserProfileService userProfileService;
	@Autowired
	private Environment environment;
	
    @PostMapping("/search")
    public List<UserProfile> searchUserProfiles(@RequestParam("username") String username) {
    	String port = environment.getProperty("local.server.port");
        System.out.println("port: " + port);
        List<UserProfile> list = userProfileService.searchUsers(username);
        for(int i = 0; i<list.size();i++) {
        	System.out.println(list.get(i).getProfileImage());
        }
        return list;
    }
	
}
