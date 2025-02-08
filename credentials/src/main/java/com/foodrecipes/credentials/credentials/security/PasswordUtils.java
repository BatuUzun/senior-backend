package com.foodrecipes.credentials.credentials.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class PasswordUtils {

	public static final String BCRYPT_PATTERN = "$2a$10$";
	public static final int BCRYPT_PATTERN_SIZE = BCRYPT_PATTERN.length();
	
	
	@Bean
	public PasswordEncoder encoder() {
	    return new BCryptPasswordEncoder();
	}
	
	
	
}
