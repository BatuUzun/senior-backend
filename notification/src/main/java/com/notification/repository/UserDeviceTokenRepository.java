package com.notification.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.notification.entity.UserDeviceToken;

public interface UserDeviceTokenRepository extends JpaRepository<UserDeviceToken, Long> {
    Optional<UserDeviceToken> findByUserId(Long userId);
}
