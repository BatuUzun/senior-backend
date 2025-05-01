package com.foodrecipes.credentials.credentials.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.foodrecipes.credentials.credentials.entity.UserDeviceToken;

public interface UserDeviceTokenRepository extends JpaRepository<UserDeviceToken, Long> {
    Optional<UserDeviceToken> findByUserId(Long userId);
    
    void deleteByUserId(Long userId);

}
