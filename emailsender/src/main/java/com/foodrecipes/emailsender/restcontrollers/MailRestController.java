package com.foodrecipes.emailsender.restcontrollers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.foodrecipes.emailsender.service.EmailSenderService;

@RestController
@RequestMapping("/email-sender")
public class MailRestController {
	
	@Autowired
	private EmailSenderService emailService;
	
	@GetMapping("/send-verification-code/")
	private int generateVerificationCode(@RequestParam String email) {
		return emailService.generateVerificationCode(email);
	}
	
	@GetMapping("/send-change-password-code/")
	private int changePasswordCode(@RequestParam String email) {
		return emailService.generateChangePasswordCode(email);
	}
	
	
}
