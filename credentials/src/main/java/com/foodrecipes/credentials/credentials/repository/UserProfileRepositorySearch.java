package com.foodrecipes.credentials.credentials.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.foodrecipes.credentials.credentials.entity.UserProfileSearch;

public interface UserProfileRepositorySearch extends JpaRepository<UserProfileSearch, Long>{
	@Query("SELECT u FROM UserProfile u WHERE u.username ILIKE %:username%")
	List<UserProfileSearch> findByUsernameContaining(String username, Pageable pageable);}
