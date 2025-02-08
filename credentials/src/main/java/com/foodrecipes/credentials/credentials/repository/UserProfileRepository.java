package com.foodrecipes.credentials.credentials.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.foodrecipes.credentials.credentials.entity.UserProfile;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
	
	boolean existsByUsername(String username);

	UserProfile findByUserId(Long userId);
	
	@Query("SELECT up FROM UserProfile up JOIN up.user u JOIN Token t ON u.id = t.user.id WHERE t.token = :token")
    UserProfile findByToken(@Param("token") String token);
	
}