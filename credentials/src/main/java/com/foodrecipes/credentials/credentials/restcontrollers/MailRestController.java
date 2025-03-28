package com.foodrecipes.credentials.credentials.restcontrollers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.foodrecipes.credentials.credentials.service.EmailSenderService;

/**
 * Controller responsible for sending email verification and password reset codes.
 * Provides APIs for generating verification codes for user authentication.
 */
@RestController
@RequestMapping("/email-sender")
public class MailRestController {

    @Autowired
    private EmailSenderService emailService;

    /**
     * Generates and sends a verification code to the user's email.
     *
     * @param email The email address of the user.
     * @return The generated verification code.
     */
    @GetMapping("/send-verification-code/")
    private int generateVerificationCode(@RequestParam String email) {
        return emailService.generateVerificationCode(email);
    }

    /**
     * Generates and sends a password reset verification code to the user's email.
     *
     * @param email The email address of the user.
     * @return The generated verification code.
     */
    @GetMapping("/send-change-password-code/")
    private int changePasswordCode(@RequestParam String email) {
        return emailService.generateChangePasswordCode(email);
    }
}
