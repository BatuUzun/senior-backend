package com.foodrecipes.credentials.credentials.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.foodrecipes.credentials.credentials.entity.UserProfileSearch;

public interface UserProfileRepositorySearch extends JpaRepository<UserProfileSearch, Long> {
    @Query("SELECT new com.foodrecipes.credentials.credentials.entity.UserProfileSearch(up.id, up.username, up.description, up.bio, up.link, up.location, up.profileImage) " +
           "FROM UserProfile up WHERE up.username ILIKE %:username%")
    List<UserProfileSearch> findByUsernameContaining(@Param("username") String username, Pageable pageable);
}