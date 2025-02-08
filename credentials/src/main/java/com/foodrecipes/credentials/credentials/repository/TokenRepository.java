package com.foodrecipes.credentials.credentials.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.foodrecipes.credentials.credentials.entity.Token;

public interface TokenRepository extends JpaRepository<Token, Long> {
	
	Token findByToken(String token);
	
	void deleteByUserIdAndToken(Long userId, String token);
	
}
