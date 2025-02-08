package com.foodrecipes.credentials.credentials.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.foodrecipes.credentials.credentials.entity.User;

import jakarta.transaction.Transactional;

public interface UserRepository extends JpaRepository<User, Long> {
	boolean existsByEmail(String email);

	User findByEmail(String email);
	//User findByToken(String token);

	
	/*@Modifying
    @Transactional
    @Query("UPDATE User u SET u.token = :token WHERE u.email = :email")
    void addToken(@Param("token") String token, @Param("email") String email);*/
	
	@Modifying
    @Transactional
    @Query("UPDATE User u SET u.isVerified = true WHERE u.email = :email")
    void updateIsVerifiedByEmail(@Param("email") String email);
}