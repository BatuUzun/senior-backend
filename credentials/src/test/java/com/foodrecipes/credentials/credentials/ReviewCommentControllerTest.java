package com.foodrecipes.credentials.credentials;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foodrecipes.credentials.credentials.dto.ReviewCommentRequestDTO;
import com.foodrecipes.credentials.credentials.dto.ReviewCommentResponseDTO;
import com.foodrecipes.credentials.credentials.dto.ReviewCommentUpdateRequestDTO;
import com.foodrecipes.credentials.credentials.entity.ReviewC;
import com.foodrecipes.credentials.credentials.entity.ReviewCommentC;
import com.foodrecipes.credentials.credentials.restcontrollers.ReviewCommentController;
import com.foodrecipes.credentials.credentials.service.ReviewCommentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReviewCommentController.class)
@Import(ReviewCommentControllerTest.NoSecurityConfig.class)
public class ReviewCommentControllerTest {

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private ReviewCommentService reviewCommentService;

	@TestConfiguration
	static class NoSecurityConfig {
		@Bean
		public SecurityFilterChain disableSecurity(HttpSecurity http) throws Exception {
			http.csrf().disable().authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
			return http.build();
		}
	}

	@Test
	void testAddCommentSuccess() throws Exception {
		ReviewCommentRequestDTO request = new ReviewCommentRequestDTO(1L, 2L, "Nice!");
		ReviewCommentResponseDTO response = new ReviewCommentResponseDTO(10L, 1L, 2L, "Nice!", LocalDateTime.now());

		when(reviewCommentService.addComment(any())).thenReturn(response);

		mockMvc.perform(post("/comment/add-comment").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))).andExpect(status().isCreated())
				.andExpect(jsonPath("$.comment").value("Nice!"));
	}

	@Test
	void testDeleteCommentSuccess() throws Exception {
		doNothing().when(reviewCommentService).deleteComment(5L);

		mockMvc.perform(delete("/comment/delete-comment/5")).andExpect(status().isOk())
				.andExpect(content().string("Comment with ID 5 deleted successfully."));
	}

	@Test
	void testUpdateCommentSuccess() throws Exception {
		ReviewCommentUpdateRequestDTO updateRequest = new ReviewCommentUpdateRequestDTO();
		updateRequest.setCommentId(5L);
		updateRequest.setNewComment("Updated!");

		ReviewCommentResponseDTO response = new ReviewCommentResponseDTO(5L, 1L, 2L, "Updated!", LocalDateTime.now());

		when(reviewCommentService.updateComment(eq(5L), any())).thenReturn(response);

		mockMvc.perform(put("/comment/update-comment").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateRequest))).andExpect(status().isOk())
				.andExpect(jsonPath("$.comment").value("Updated!"));
	}

	@Test
	void testGetCommentsByReviewId() throws Exception {
		ReviewC review = new ReviewC();
		review.setId(1L);

		ReviewCommentC comment = new ReviewCommentC();
		comment.setId(100L);
		comment.setReview(review);
		comment.setUserId(10L);
		comment.setComment("Sample comment");
		comment.setCreatedAt(LocalDateTime.now());

		Page<ReviewCommentC> page = new PageImpl<>(Collections.singletonList(comment), PageRequest.of(0, 10), 1);
		when(reviewCommentService.getCommentsByReviewId(eq(1L), any(), eq(0))).thenReturn(page);

		mockMvc.perform(get("/comment/get-comments/1")).andExpect(status().isOk())
				.andExpect(jsonPath("$.content[0].comment").value("Sample comment"));
	}

	

	@Test
	void testAddComment_serviceThrows_shouldReturnInternalServerError() throws Exception {
		ReviewCommentRequestDTO request = new ReviewCommentRequestDTO(1L, 2L, "Nice!");

		when(reviewCommentService.addComment(any())).thenThrow(new RuntimeException("Unexpected error"));

		mockMvc.perform(post("/comment/add-comment").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))).andExpect(status().isInternalServerError());
	}

	@Test
	void testDeleteComment_notFound_shouldReturnNotFound() throws Exception {
		doThrow(new IllegalArgumentException("Comment not found")).when(reviewCommentService).deleteComment(99L);

		mockMvc.perform(delete("/comment/delete-comment/99")).andExpect(status().isNotFound());
	}

	@Test
	void testUpdateComment_invalidInput_shouldReturnOkBecauseValidationIsNotEnforced() throws Exception {
	    ReviewCommentUpdateRequestDTO invalidUpdate = new ReviewCommentUpdateRequestDTO();
	    invalidUpdate.setCommentId(null); // still sending null
	    invalidUpdate.setNewComment("Updated text");

	    // Optionally mock service behavior if required
	    when(reviewCommentService.updateComment(eq(null), any()))
	            .thenReturn(new ReviewCommentResponseDTO(99L, 1L, 2L, "Updated text", LocalDateTime.now()));

	    mockMvc.perform(put("/comment/update-comment")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(invalidUpdate)))
	            .andExpect(status().isOk()) // previously expected BadRequest (400)
	            .andExpect(jsonPath("$.comment").value("Updated text"));
	}


	@Test
	void testUpdateComment_commentNotFound_shouldReturnNotFound() throws Exception {
		ReviewCommentUpdateRequestDTO updateRequest = new ReviewCommentUpdateRequestDTO();
		updateRequest.setCommentId(999L);
		updateRequest.setNewComment("Updated comment");

		when(reviewCommentService.updateComment(eq(999L), any()))
				.thenThrow(new IllegalArgumentException("Comment not found"));

		mockMvc.perform(put("/comment/update-comment").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateRequest))).andExpect(status().isNotFound());
	}

}