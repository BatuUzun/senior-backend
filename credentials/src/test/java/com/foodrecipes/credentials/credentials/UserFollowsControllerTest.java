package com.foodrecipes.credentials.credentials;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Set;

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

import com.foodrecipes.credentials.credentials.dto.FollowRequestDTO;
import com.foodrecipes.credentials.credentials.dto.PagedResponse;
import com.foodrecipes.credentials.credentials.dto.UserProfileResponseProfileGetterDTO;
import com.foodrecipes.credentials.credentials.restcontrollers.UserFollowsController;
import com.foodrecipes.credentials.credentials.service.UserFollowsService;

@WebMvcTest(UserFollowsController.class)
@Import(UserFollowsControllerTest.NoSecurityConfig.class)
class UserFollowsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserFollowsService userFollowsService;

    @TestConfiguration
    static class NoSecurityConfig {
        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
            http.csrf().disable().authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
            return http.build();
        }
    }

    @Test
    void followUser_success() throws Exception {
        FollowRequestDTO request = new FollowRequestDTO(1L, 2L);
        when(userFollowsService.followUser(1L, 2L)).thenReturn("Successfully followed the user.");

        mockMvc.perform(post("/user-follow/follow")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"followerId\":1,\"followedId\":2}"))
                .andExpect(status().isCreated())
                .andExpect(content().string("Successfully followed the user."));
    }

    @Test
    void followUser_conflict() throws Exception {
        when(userFollowsService.followUser(1L, 2L)).thenReturn("You are already following this user.");

        mockMvc.perform(post("/user-follow/follow")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"followerId\":1,\"followedId\":2}"))
                .andExpect(status().isConflict())
                .andExpect(content().string("You are already following this user."));
    }

    @Test
    void followUser_selfFollow() throws Exception {
        when(userFollowsService.followUser(1L, 1L)).thenReturn("You cannot follow yourself!");

        mockMvc.perform(post("/user-follow/follow")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"followerId\":1,\"followedId\":1}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("You cannot follow yourself!"));
    }

    @Test
    void unfollowUser_success() throws Exception {
        when(userFollowsService.unfollowUser(1L, 2L)).thenReturn("Successfully unfollowed the user.");

        mockMvc.perform(delete("/user-follow/unfollow")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"followerId\":1,\"followedId\":2}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Successfully unfollowed the user."));
    }

    @Test
    void unfollowUser_notFollowing() throws Exception {
        when(userFollowsService.unfollowUser(1L, 2L)).thenReturn("You are not following this user.");

        mockMvc.perform(delete("/user-follow/unfollow")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"followerId\":1,\"followedId\":2}"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("You are not following this user."));
    }

    @Test
    void isFollowing_shouldReturnTrue() throws Exception {
        when(userFollowsService.isFollowing(1L, 2L)).thenReturn(true);

        mockMvc.perform(get("/user-follow/is-following")
                        .param("followerId", "1")
                        .param("followedId", "2"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void getFollowerCount() throws Exception {
        when(userFollowsService.getFollowerCount(1L)).thenReturn(5L);

        mockMvc.perform(get("/user-follow/follower-count")
                        .param("userProfileId", "1"))
                .andExpect(status().isOk())
                .andExpect(content().string("5"));
    }

    @Test
    void getFollowingCount() throws Exception {
        when(userFollowsService.getFollowingCount(1L)).thenReturn(3L);

        mockMvc.perform(get("/user-follow/following-count")
                        .param("userProfileId", "1"))
                .andExpect(status().isOk())
                .andExpect(content().string("3"));
    }

    



    @Test
    void getFollowedUsers() throws Exception {
        Set<Long> ids = Set.of(2L, 3L);
        when(userFollowsService.getFollowedUsers(1L)).thenReturn(ids);

        mockMvc.perform(get("/user-follow/1/followed"))
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$").value(org.hamcrest.Matchers.containsInAnyOrder(2, 3)));
    }
} 
