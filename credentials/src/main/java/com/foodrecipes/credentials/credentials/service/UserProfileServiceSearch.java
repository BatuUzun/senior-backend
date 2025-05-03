package com.foodrecipes.credentials.credentials.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.foodrecipes.credentials.credentials.entity.UserProfileSearch;
import com.foodrecipes.credentials.credentials.repository.UserProfileRepositorySearch;

@Service
public class UserProfileServiceSearch {
    @Autowired
    private UserProfileRepositorySearch userProfileRepository;

    public List<UserProfileSearch> searchUsers(String username, Pageable pageable) {
        return userProfileRepository.findByUsernameContaining(username, pageable);
    }
}