package com.foodrecipes.credentials.credentials.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.foodrecipes.credentials.credentials.entity.User;
import com.foodrecipes.credentials.credentials.repository.UserRepository;
import jakarta.transaction.Transactional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    public String hashPassword(String plainPassword) {
        return passwordEncoder.encode(plainPassword);
    }

    @Transactional
    public User createUser(User user) {
        return userRepository.save(user);
    }
    
    public Boolean isUserExist(User user) {
        return userRepository.existsByEmail(user.getEmail());
    }
    
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    /*public void updateToken(String email, String token) {
        userRepository.updateTokenByEmail(token, email);
    }*/

	/*public User findUserByToken(String token) {
		return userRepository.findByToken(token);
	}*/
    
    public boolean userExists(String email) {
        return userRepository.existsByEmail(email);
    }
    
    public User findUserById(Long id) {
    	return userRepository.findById(id).orElse(null);
    }
	
	public void updateVerified(String email) {
		userRepository.updateIsVerifiedByEmail(email);
	}
    
}