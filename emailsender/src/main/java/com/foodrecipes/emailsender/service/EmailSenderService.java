package com.foodrecipes.emailsender.service;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailSenderService {
	@Autowired
	private JavaMailSender javaMailSender;
	
	private final Random rand = new Random();
	private final int MAX = 999999;
	private final int MIN = 100000;
	
	@Value("${spring.mail.username}")
    private String SEND_EMAIL_FROM;
	
	
	@Async
	public int verificationCodeEmailSender(String email, int code) {
		
		MimeMessage mimeMessage = javaMailSender.createMimeMessage();
		MimeMessageHelper mimeMessageHelper = null;
		
		
		final String VERIFY_EMAIL_TEXT = "Hello,"+"\nThank you for signing up with Harmonia."
				+ " To complete the registration process, please use the following verification code:"
				+ "\nVerification Code: "+code+"\n\nHarmonia Team.";
		
		final String VERIFY_EMAIL_SUBJECT = "Verify your email";
		
		
		prepareEmailContent(mimeMessageHelper, mimeMessage, email, VERIFY_EMAIL_TEXT, VERIFY_EMAIL_SUBJECT);
		javaMailSender.send(mimeMessage);
		
		return code;
	}
	
	@Async
	public int changePasswordCodeEmailSender(String email, int code) {
		
		MimeMessage mimeMessage = javaMailSender.createMimeMessage();
		MimeMessageHelper mimeMessageHelper = null;
		
		
		final String EMAIL_SUBJECT = "Reset Your Harmonia Password";
	    final String EMAIL_BODY = "Dear User,\n\n" +
	                              "We received a request to reset the password for your Harmonia account. " +
	                              "Please use the verification code below to complete the password reset process:\n\n" +
	                              "Verification Code: " + code + "\n\n" +
	                              "Best regards,\n" +
	                              "The Harmonia Team";
		
		
		prepareEmailContent(mimeMessageHelper, mimeMessage, email, EMAIL_BODY, EMAIL_SUBJECT);
		javaMailSender.send(mimeMessage);
		
		return code;
	}

	public int generateVerificationCode(String email) {
		int code = rand.nextInt(MAX - MIN+ 1) + MIN;
		verificationCodeEmailSender(email, code);	
		return code;
	}
	
	public int generateChangePasswordCode(String email) {
		int code = rand.nextInt(MAX - MIN+ 1) + MIN;
		changePasswordCodeEmailSender(email, code);	
		return code;
	}
	
	private void prepareEmailContent(MimeMessageHelper mimeMessageHelper, MimeMessage mimeMessage, String email, 
			String VERIFY_EMAIL_TEXT, String VERIFY_EMAIL_SUBJECT) {
		
		try {
			mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
			mimeMessageHelper.setFrom(SEND_EMAIL_FROM);
			mimeMessageHelper.setTo(email);
			mimeMessageHelper.setText(VERIFY_EMAIL_TEXT);
			mimeMessageHelper.setSubject(VERIFY_EMAIL_SUBJECT);
		} catch (MessagingException e) {
			e.printStackTrace();
		}
		
	}
	
	
}
