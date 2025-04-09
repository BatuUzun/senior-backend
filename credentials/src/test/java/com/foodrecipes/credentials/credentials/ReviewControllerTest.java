package com.foodrecipes.credentials.credentials;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foodrecipes.credentials.credentials.dto.FollowedReviewsRequestDTO;
import com.foodrecipes.credentials.credentials.dto.FollowedUsersReviewsWithoutSpotifyId;
import com.foodrecipes.credentials.credentials.dto.ReviewUpdateDTO;
import com.foodrecipes.credentials.credentials.entity.Review;
import com.foodrecipes.credentials.credentials.restcontrollers.ReviewController;
import com.foodrecipes.credentials.credentials.service.ReviewService;

@WebMvcTest(ReviewController.class)
public class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReviewService reviewService;

    @Autowired
    private ObjectMapper objectMapper;

    private Review review;
    
    @TestConfiguration
    static class NoSecurityConfig {
        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
            http.csrf().disable().authorizeHttpRequests(authz -> authz.anyRequest().permitAll());
            return http.build();
        }
    }

    @BeforeEach
    void setUp() {
        review = new Review(1L, 1L, 5, "Nice", "spotify123", LocalDateTime.now());
    }

    @Test
    void testAddReview_Success() throws Exception {
        when(reviewService.addReview(any())).thenReturn(review);

        mockMvc.perform(post("/review/add-review")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(review)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void testAddReview_Conflict() throws Exception {
        when(reviewService.addReview(any())).thenThrow(new IllegalStateException());

        mockMvc.perform(post("/review/add-review")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(review)))
                .andExpect(status().isConflict());
    }

    @Test
    void testDeleteReview() throws Exception {
        doNothing().when(reviewService).deleteReview(1L);

        mockMvc.perform(delete("/review/delete/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testUpdateReview_Found() throws Exception {
        ReviewUpdateDTO dto = new ReviewUpdateDTO(1L, 4, "Updated");
        when(reviewService.updateReview(any())).thenReturn(Optional.of(review));

        mockMvc.perform(put("/review/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void testUpdateReview_NotFound() throws Exception {
        ReviewUpdateDTO dto = new ReviewUpdateDTO(1L, 4, "Updated");
        when(reviewService.updateReview(any())).thenReturn(Optional.empty());

        mockMvc.perform(put("/review/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetReviewsByUserId() throws Exception {
        Page<Review> page = new PageImpl<>(Collections.singletonList(review));
        when(reviewService.getReviewsByUserId(1L, 0)).thenReturn(page);

        mockMvc.perform(get("/review/get-reviews/user/1?page=0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L));
    }

    @Test
    void testGetReviewsBySpotifyId() throws Exception {
        Page<Review> page = new PageImpl<>(Collections.singletonList(review));
        when(reviewService.getReviewsBySpotifyId(any(), any(), anyInt())).thenReturn(page);

        mockMvc.perform(get("/review/get-reviews/spotify/spotify123?page=0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L));
    }

    @Test
    void testCalculateAverageRating() throws Exception {
        when(reviewService.calculateAverageRating("spotify123")).thenReturn(4.2);

        mockMvc.perform(get("/review/calculate/spotify/spotify123/average-rating"))
                .andExpect(status().isOk())
                .andExpect(content().string("4.2"));
    }

    @Test
    void testGetReviewById_Found() throws Exception {
        when(reviewService.getReviewById(1L)).thenReturn(review);

        mockMvc.perform(get("/review/get-review/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void testGetReviewById_NotFound() throws Exception {
        when(reviewService.getReviewById(1L)).thenThrow(new IllegalArgumentException("Not found"));

        mockMvc.perform(get("/review/get-review/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetUserReview_Found() throws Exception {
        when(reviewService.getUserReview(1L, "spotify123")).thenReturn(Optional.of(review));

        mockMvc.perform(get("/review/user-review")
                        .param("userId", "1")
                        .param("spotifyId", "spotify123"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetUserReview_NotFound() throws Exception {
        when(reviewService.getUserReview(1L, "spotify123")).thenReturn(Optional.empty());

        mockMvc.perform(get("/review/user-review")
                        .param("userId", "1")
                        .param("spotifyId", "spotify123"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetUserReviewCount() throws Exception {
        when(reviewService.getUserReviewCount(1L)).thenReturn(10L);

        mockMvc.perform(get("/review/user-review-count")
                        .param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1L))
                .andExpect(jsonPath("$.reviewCount").value(10L));
    }

    @Test
    void testGetFollowedUserReviews() throws Exception {
        FollowedReviewsRequestDTO dto = new FollowedReviewsRequestDTO();
        when(reviewService.getFollowedUserReviews(any())).thenReturn(List.of(review));

        mockMvc.perform(post("/review/followed-by-spotify-id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void testGetPopularReviewIds() throws Exception {
        when(reviewService.getTopReviews()).thenReturn(List.of(1L, 2L));

        mockMvc.perform(get("/review/popular"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value(1L));
    }

    @Test
    void testGetFollowedUsersLatestReviews() throws Exception {
        FollowedUsersReviewsWithoutSpotifyId dto = new FollowedUsersReviewsWithoutSpotifyId();
        when(reviewService.getFollowedUserReviewsWithoutSpotifyId(any())).thenReturn(List.of(review));

        mockMvc.perform(post("/review/followed-reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }
}
