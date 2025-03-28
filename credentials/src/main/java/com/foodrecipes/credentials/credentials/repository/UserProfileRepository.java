package com.foodrecipes.credentials.credentials.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.foodrecipes.credentials.credentials.entity.UserProfile;
import com.foodrecipes.credentials.credentials.entity.UserProfileProfileGetter;


public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    
    boolean existsByUsername(String username);

    Optional<UserProfile> findByUserId(Long userId);

    @Query("""
        SELECT up FROM UserProfile up 
        JOIN up.user u 
        JOIN Token t ON u.id = t.user.id 
        WHERE t.token = :token
    """)
    Optional<UserProfile> findByToken(@Param("token") String token);
    
    boolean existsByUserId(Long userId);
    
	List<UserProfileProfileGetter> findByIdIn(List<Long> userIds);


}
