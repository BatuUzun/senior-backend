package com.foodrecipes.credentials.credentials;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.foodrecipes.credentials.credentials.restcontrollers.MailRestController;
import com.foodrecipes.credentials.credentials.service.EmailSenderService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(MailRestController.class)
@Import(MailRestControllerTest.NoSecurityConfig.class)
public class MailRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmailSenderService emailService;

    @TestConfiguration
    static class NoSecurityConfig {
        @Bean
        public SecurityFilterChain disableSecurity(HttpSecurity http) throws Exception {
            http.csrf().disable()
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
            return http.build();
        }
    }

    @Test
    void testGenerateVerificationCode() throws Exception {
        String email = "asd@gmail.com";
        int fakeCode = 123456;

        when(emailService.generateVerificationCode(email)).thenReturn(fakeCode);
        when(emailService.verificationCodeEmailSender(email, fakeCode)).thenReturn(fakeCode); // ✅ FIXED

        mockMvc.perform(get("/email-sender/send-verification-code/")
                .param("email", email))
                .andExpect(status().isOk())
                .andExpect(content().string(String.valueOf(fakeCode)));

        verify(emailService).generateVerificationCode(email);
        verify(emailService).verificationCodeEmailSender(email, fakeCode);
    }


    @Test
    void testGenerateChangePasswordCode() throws Exception {
        String email = "asd@gmail.com";
        int resetCode = 654321;

        when(emailService.generateChangePasswordCode(email)).thenReturn(resetCode);

        mockMvc.perform(get("/email-sender/send-change-password-code/")
                .param("email", email))
                .andExpect(status().isOk())
                .andExpect(content().string(String.valueOf(resetCode)));

        verify(emailService).generateChangePasswordCode(email);
    }
}
