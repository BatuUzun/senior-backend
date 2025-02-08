package com.foodrecipes.credentials.credentials.service;

import java.util.Optional;

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
        user.setPassword(hashPassword(user.getPassword())); // Ensure password is hashed
        return userRepository.save(user);
    }
    
    public boolean isUserExist(String email) {
        return userRepository.existsByEmail(email);
    }
    
    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    public Optional<User> findUserById(Long id) {
        return userRepository.findById(id);
    }
    
    @Transactional
    public void updateVerified(String email) {
        userRepository.updateIsVerifiedByEmail(email);
    }
}
