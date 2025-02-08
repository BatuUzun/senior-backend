package com.foodrecipes.credentials.credentials.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.foodrecipes.credentials.credentials.entity.Token;
import com.foodrecipes.credentials.credentials.repository.TokenRepository;

import jakarta.transaction.Transactional;

@Service
public class TokenService {
	
	@Autowired
	private TokenRepository tokenRepository;
	
	public boolean addToken(Token token) {
		
		tokenRepository.save(token);
		
		return true;
	}
	
	public Token findToken(String token) {
		return tokenRepository.findByToken(token);
	}
	
	@Transactional
	public void deleteToken(Long userId, String token) {
        tokenRepository.deleteByUserIdAndToken(userId, token);
    }
	
	
}
