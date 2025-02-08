package com.foodrecipes.credentials.credentials.dto;

public class UserProfileDTO {
    private String email;
    private String password;
    private String username;
    
    
	public String getEmail() {
		return email;
	}
	public String getPassword() {
		return password;
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	

    // Getters and Setters
    
	public boolean isNullOrEmpty() {
        return (email == null || email.isBlank()) ||
               (password == null || password.isBlank()) ||
               (username == null || username.isBlank()
               );
    }
}