package com.foodrecipes.credentials.credentials.dto;

public class AuthenticationDTO {
	
    private String email;
    private String password;
    private String token;
    
    
	public String getEmail() {
		return email;
	}
	public String getPassword() {
		return password;
	}
	
	public String getToken() {
		return token;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	public void setToken(String token) {
		this.token = token;
	}
	

    // Getters and Setters
    
	public boolean isNullOrEmpty() {
        return (email == null || email.isBlank()) ||
               (password == null || password.isBlank()) ||
               (token == null || token.isBlank()
               );
    }

	
}
