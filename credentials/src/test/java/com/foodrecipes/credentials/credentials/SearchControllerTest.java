package com.foodrecipes.credentials.credentials;

import com.foodrecipes.credentials.credentials.entity.UserProfileSearch;
import com.foodrecipes.credentials.credentials.restcontrollers.SearchController;
import com.foodrecipes.credentials.credentials.service.UserProfileServiceSearch;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SearchController.class)
@Import(SearchControllerTest.NoSecurityConfig.class)
class SearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserProfileServiceSearch userProfileService;
    
    @TestConfiguration
    static class NoSecurityConfig {
        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            http.csrf().disable().authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
            return http.build();
        }
    }

    @Test
    void testSearchUserProfiles_returnsResults() throws Exception {
        UserProfileSearch user = new UserProfileSearch();
        user.setUsername("john");
        user.setProfileImage("img.png");

        // Offset = 0, Limit = 10 -> PageRequest.of(0, 10)
        when(userProfileService.searchUsers(eq("john"), eq(PageRequest.of(0, 10))))
            .thenReturn(Collections.singletonList(user));

        mockMvc.perform(post("/search-profile/search")
                        .param("username", "john")
                        .param("offset", "0")
                        .param("limit", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("john"))
                .andExpect(jsonPath("$[0].profileImage").value("img.png"));
    }

    @Test
    void testSearchUserProfiles_returnsEmptyList() throws Exception {
        when(userProfileService.searchUsers(eq("noone"), eq(PageRequest.of(0, 10))))
            .thenReturn(Collections.emptyList());

        mockMvc.perform(post("/search-profile/search")
                        .param("username", "noone")
                        .param("offset", "0")
                        .param("limit", "10"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void testSearchUserProfiles_missingUsernameParam_returns400() throws Exception {
        mockMvc.perform(post("/search-profile/search"))
        .andExpect(status().isInternalServerError());

    }
}
