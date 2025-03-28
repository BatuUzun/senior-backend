package com.foodrecipes.credentials.credentials.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.foodrecipes.credentials.credentials.dto.NotificationRequest;
import com.foodrecipes.credentials.credentials.entity.UserDeviceToken;
import com.foodrecipes.credentials.credentials.repository.UserDeviceTokenRepository;

@Service
public class NotificationService {
    @Autowired
	private UserDeviceTokenRepository deviceTokenRepository;
    @Autowired
    private FirebaseNotificationService firebaseService;

    
    public void saveDeviceToken(Long userId, String token) {
    	
    	deviceTokenRepository.findByUserId(userId).ifPresent(deviceTokenRepository::delete);
        
        UserDeviceToken newToken = new UserDeviceToken();
        newToken.setUserId(userId);
        newToken.setDeviceToken(token);
        deviceTokenRepository.save(newToken);
    }

    public ResponseEntity<String> sendNotifications(NotificationRequest request) {
		Optional<String> token = deviceTokenRepository.findByUserId(request.getUserId()).map(t -> t.getDeviceToken());

		if (token.isPresent()) {
			firebaseService.sendNotification(token.get(), request.getTitle(), request.getBody());
			return ResponseEntity.ok("Notification sent successfully!");
		} else {
			return ResponseEntity.badRequest().body("User does not have a registered device token.");
		}
	}
}
