package com.foodrecipes.searchprofile.repository;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.foodrecipes.searchprofile.entity.UserProfile;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long>{
	@Query("SELECT u FROM UserProfile u WHERE u.username ILIKE %:username%")
	List<UserProfile> findByUsernameContaining(String username, Pageable pageable);}
