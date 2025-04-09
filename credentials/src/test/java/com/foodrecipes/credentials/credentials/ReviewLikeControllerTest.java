package com.foodrecipes.credentials.credentials;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foodrecipes.credentials.credentials.dto.IsLikedResponseDto;
import com.foodrecipes.credentials.credentials.dto.ReviewLikeRequestDTO;
import com.foodrecipes.credentials.credentials.dto.ReviewLikeResponseDTO;
import com.foodrecipes.credentials.credentials.restcontrollers.ReviewLikeController;
import com.foodrecipes.credentials.credentials.service.ReviewLikeService;

@WebMvcTest(ReviewLikeController.class)
public class ReviewLikeControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private ReviewLikeService reviewLikeService;

	@Autowired
	private ObjectMapper objectMapper;

	@TestConfiguration
	static class TestSecurityConfig {
		@Bean
		public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
			http.csrf().disable().authorizeHttpRequests(authz -> authz.anyRequest().permitAll());
			return http.build();
		}
	}

	@Test
	void testLikeReview() throws Exception {
		ReviewLikeRequestDTO requestDTO = new ReviewLikeRequestDTO(1L, 2L);
		ReviewLikeResponseDTO responseDTO = new ReviewLikeResponseDTO(true, "Liked", 10L, HttpStatus.OK);

		when(reviewLikeService.addReviewsLike(1L, 2L)).thenReturn(responseDTO);

		mockMvc.perform(post("/review-like/like").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestDTO))).andExpect(status().isOk())
				.andExpect(jsonPath("$.message").value("Liked"));

		verify(reviewLikeService).addReviewsLike(1L, 2L);
	}

	@Test
	void testUnlikeReview() throws Exception {
		Long likeId = 5L;
		ReviewLikeResponseDTO response = new ReviewLikeResponseDTO(true, "Unliked", null, HttpStatus.OK);

		when(reviewLikeService.removeReviewsLikeById(likeId)).thenReturn(response);

		mockMvc.perform(delete("/review-like/unlike/{likeId}", likeId)).andExpect(status().isOk())
				.andExpect(jsonPath("$.message").value("Unliked"));

		verify(reviewLikeService).removeReviewsLikeById(likeId);
	}

	@Test
	void testIsReviewLikedByUser_True() throws Exception {
		Long reviewId = 1L;
		Long userId = 2L;
		IsLikedResponseDto dto = new IsLikedResponseDto(100L, userId);

		when(reviewLikeService.isReviewLikedByUser(userId, reviewId)).thenReturn(ResponseEntity.ok(dto));

		mockMvc.perform(get("/review-like/{reviewId}/is-liked/{userId}", reviewId, userId)).andExpect(status().isOk())
				.andExpect(jsonPath("$.userId").value(userId));

		verify(reviewLikeService).isReviewLikedByUser(userId, reviewId);
	}

	@Test
	void testGetLikeCount() throws Exception {
		Long reviewId = 1L;
		ReviewLikeResponseDTO response = new ReviewLikeResponseDTO(true, "Count OK", 5L, HttpStatus.OK);

		when(reviewLikeService.getReviewsLikeCount(reviewId)).thenReturn(response);

		mockMvc.perform(get("/review-like/{reviewId}/count", reviewId)).andExpect(status().isOk())
				.andExpect(jsonPath("$.data").value(5));

		verify(reviewLikeService).getReviewsLikeCount(reviewId);
	}

	@Test
	void testGetTopReviews() throws Exception {
		String spotifyId = "track123";
		List<Long> topIds = List.of(10L, 20L, 30L);

		when(reviewLikeService.getTop100PopularReviewsBySpotifyId(spotifyId)).thenReturn(topIds);

		mockMvc.perform(get("/review-like/top/{spotifyId}", spotifyId)).andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(3));

		verify(reviewLikeService).getTop100PopularReviewsBySpotifyId(spotifyId);
	}

	@Test
	void testLikeReview_serviceThrowsException_shouldReturn500() throws Exception {
		ReviewLikeRequestDTO requestDTO = new ReviewLikeRequestDTO(1L, 2L);

		when(reviewLikeService.addReviewsLike(1L, 2L)).thenThrow(new RuntimeException("Something went wrong"));

		mockMvc.perform(post("/review-like/like").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestDTO))).andExpect(status().isInternalServerError());
	}

	@Test
	void testUnlikeReview_invalidId_shouldReturn404() throws Exception {
		Long likeId = 999L;

		when(reviewLikeService.removeReviewsLikeById(likeId))
				.thenReturn(new ReviewLikeResponseDTO(false, "Not found", null, HttpStatus.NOT_FOUND));

		mockMvc.perform(delete("/review-like/unlike/{likeId}", likeId)).andExpect(status().isNotFound())
				.andExpect(jsonPath("$.message").value("Not found"));

		verify(reviewLikeService).removeReviewsLikeById(likeId);
	}

	@Test
	void testIsReviewLikedByUser_False() throws Exception {
		Long reviewId = 1L;
		Long userId = 2L;

		when(reviewLikeService.isReviewLikedByUser(userId, reviewId))
				.thenReturn(ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));

		mockMvc.perform(get("/review-like/{reviewId}/is-liked/{userId}", reviewId, userId))
				.andExpect(status().isNotFound());
	}

	@Test
	void testGetLikeCount_zeroLikes_shouldReturnOk() throws Exception {
		Long reviewId = 2L;
		ReviewLikeResponseDTO response = new ReviewLikeResponseDTO(true, "Count OK", 0L, HttpStatus.OK);

		when(reviewLikeService.getReviewsLikeCount(reviewId)).thenReturn(response);

		mockMvc.perform(get("/review-like/{reviewId}/count", reviewId)).andExpect(status().isOk())
				.andExpect(jsonPath("$.data").value(0));

		verify(reviewLikeService).getReviewsLikeCount(reviewId);
	}

	@Test
	void testGetTopReviews_emptyList_shouldReturnOk() throws Exception {
		String spotifyId = "emptyTrack";

		when(reviewLikeService.getTop100PopularReviewsBySpotifyId(spotifyId)).thenReturn(List.of());

		mockMvc.perform(get("/review-like/top/{spotifyId}", spotifyId)).andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(0));

		verify(reviewLikeService).getTop100PopularReviewsBySpotifyId(spotifyId);
	}

}
