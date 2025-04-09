package com.foodrecipes.credentials.credentials;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foodrecipes.credentials.credentials.dto.UserProfileResponseProfileGetterDTO;
import com.foodrecipes.credentials.credentials.restcontrollers.ProfileGetterController;
import com.foodrecipes.credentials.credentials.service.UserProfileService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProfileGetterController.class)
@Import(ProfileGetterControllerTest.NoSecurityConfig.class)
public class ProfileGetterControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private UserProfileService userProfileService;

    @TestConfiguration
    static class NoSecurityConfig {
        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            http.csrf().disable().authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
            return http.build();
        }
    }

    @Test
    void testGetUserProfiles_Success() throws Exception {
        List<Long> userIds = Arrays.asList(1L, 2L);
        List<UserProfileResponseProfileGetterDTO> profiles = Arrays.asList(
                new UserProfileResponseProfileGetterDTO(1L, "user1", "img1.jpg"),
                new UserProfileResponseProfileGetterDTO(2L, "user2", "img2.jpg")
        );

        when(userProfileService.getUserProfilesByIds(userIds)).thenReturn(profiles);

        mockMvc.perform(post("/profile-getter/fetch-ids")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userIds)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value(1))
                .andExpect(jsonPath("$[0].username").value("user1"))
                .andExpect(jsonPath("$[1].username").value("user2"));

        verify(userProfileService).getUserProfilesByIds(userIds);
    }

    @Test
    void testGetUserProfiles_TooManyIds() throws Exception {
        List<Long> userIds = Arrays.asList(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L, 11L);

        when(userProfileService.getUserProfilesByIds(userIds))
                .thenThrow(new IllegalArgumentException("At most 10 user IDs can be requested at once"));

        mockMvc.perform(post("/profile-getter/fetch-ids")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userIds)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("{\"error\": \"At most 10 user IDs can be requested at once\"}"));
    }
}
