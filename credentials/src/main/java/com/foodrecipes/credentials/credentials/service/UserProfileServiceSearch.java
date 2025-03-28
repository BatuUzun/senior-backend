package com.foodrecipes.credentials.credentials.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.foodrecipes.credentials.credentials.constants.Constants;
import com.foodrecipes.credentials.credentials.entity.UserProfileSearch;
import com.foodrecipes.credentials.credentials.repository.UserProfileRepositorySearch;

@Service
public class UserProfileServiceSearch {
	@Autowired
	private UserProfileRepositorySearch userProfileRepository;
	
	public List<UserProfileSearch> searchUsers(String username) {
        return userProfileRepository.findByUsernameContaining(username, PageRequest.of(0, Constants.PAGE_LIMIT));
    }
}
