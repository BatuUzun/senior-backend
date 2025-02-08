package com.foodrecipes.profileapi.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.foodrecipes.profileapi.entity.UserProfile;

import jakarta.transaction.Transactional;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long>{
	/*@Query("SELECT up FROM UserProfile up JOIN up.user u WHERE u.email = :email")
    UserProfile findByUserEmail(@Param("email") String email);*/
	
	@Transactional
    @Modifying
	@Query("UPDATE UserProfile up SET up.profileImage = :newProfileImage WHERE up.id = :userProfileId")
	void updateProfileImage(@Param("userProfileId") Long userProfileId, @Param("newProfileImage") String newProfileImage);
	
	@Query("SELECT u.profileImage FROM UserProfile u WHERE u.id = :id")
    String findUserProfileImageById(@Param("id") Long id);
	
	//List<UserProfile> findByIdIn(List<Long> ids);
	
    Optional<UserProfile> findById(Long id); // Method to find user profile by ID


}
