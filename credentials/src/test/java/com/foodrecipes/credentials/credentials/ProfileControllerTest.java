package com.foodrecipes.credentials.credentials;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foodrecipes.credentials.credentials.entity.UserProfileProfileAPI;
import com.foodrecipes.credentials.credentials.restcontrollers.ProfileController;
import com.foodrecipes.credentials.credentials.service.S3Service;
import com.foodrecipes.credentials.credentials.service.UserProfileProfileApiService;

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
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest(ProfileController.class)
@Import(ProfileControllerTest.NoSecurityConfig.class)
public class ProfileControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private S3Service s3Service;
    @MockBean private UserProfileProfileApiService userProfileService;

    @TestConfiguration
    static class NoSecurityConfig {
        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            http.csrf().disable()
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
            return http.build();
        }
    }

    @Test
    void testIsUserExistById() throws Exception {
        when(userProfileService.isUserProfileExist(1L)).thenReturn(true);

        mockMvc.perform(get("/profile-api/is-user-exist-by-id/")
                .param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void testGetUserProfileById() throws Exception {
        UserProfileProfileAPI profile = new UserProfileProfileAPI(1L, "user", "desc", "bio", "link", "loc", "img.jpg");
        when(userProfileService.getUserProfileById(1L)).thenReturn(profile);

        mockMvc.perform(get("/profile-api/get-user-profile/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("user"));
    }

    @Test
    void testGetProfilePicture() throws Exception {
        when(userProfileService.getProfilePictureByUserId(1L)).thenReturn("img.jpg");

        mockMvc.perform(get("/profile-api/1/profile-picture"))
                .andExpect(status().isOk())
                .andExpect(content().string("img.jpg"));
    }

    @Test
    void testGetAllProfiles() throws Exception {
        UserProfileProfileAPI profile = new UserProfileProfileAPI(1L, "user", "desc", "bio", "link", "loc", "img.jpg");
        when(userProfileService.getAllProfiles()).thenReturn(Arrays.asList(profile));

        mockMvc.perform(get("/profile-api/get-all-profiles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("user"));
    }

    @Test
    void testGetProfileById() throws Exception {
        UserProfileProfileAPI profile = new UserProfileProfileAPI(1L, "user", "desc", "bio", "link", "loc", "img.jpg");
        when(userProfileService.getProfileById(1L)).thenReturn(Optional.of(profile));

        mockMvc.perform(get("/profile-api/get-profile-by-id/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("user"));
    }

    @Test
    void testCreateProfile() throws Exception {
        UserProfileProfileAPI profile = new UserProfileProfileAPI(1L, "user", "desc", "bio", "link", "loc", "img.jpg");
        when(userProfileService.createProfile(any())).thenReturn(profile);

        mockMvc.perform(post("/profile-api/create-profile")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(profile)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("user"));
    }

    @Test
    void testUpdateProfile() throws Exception {
        UserProfileProfileAPI profile = new UserProfileProfileAPI(1L, "updated", "desc", "bio", "link", "loc", "img.jpg");
        when(userProfileService.updateProfile(eq(1L), any())).thenReturn(profile);

        mockMvc.perform(put("/profile-api/update-profile/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(profile)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("updated"));
    }

    @Test
    void testDeleteProfile() throws Exception {
        doNothing().when(userProfileService).deleteProfile(1L);

        mockMvc.perform(delete("/profile-api/delete-profile/1"))
                .andExpect(status().isNoContent());
    }
}
