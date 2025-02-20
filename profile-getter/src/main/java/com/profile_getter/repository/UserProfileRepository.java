package com.profile_getter.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.profile_getter.entity.UserProfile;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
	List<UserProfile> findByIdIn(List<Long> userIds);
}
