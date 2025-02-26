package com.notification.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.notification.dto.NotificationRequest;
import com.notification.service.NotificationService;

@RestController
@RequestMapping("/notification")
public class DeviceTokenController {

	@Autowired
	private NotificationService notificationService;

	@PostMapping("/save")
	public ResponseEntity<String> saveDeviceToken(@RequestParam Long userId, @RequestParam String token) {
		notificationService.saveDeviceToken(userId, token);

		return ResponseEntity.ok("Token saved successfully");
	}
	
	@PostMapping("/send")
    public ResponseEntity<String> sendNotification(@RequestBody NotificationRequest request) {
        return notificationService.sendNotifications(request);
    }
}
