package com.foodrecipes.credentials.credentials;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foodrecipes.credentials.credentials.dto.AuthenticationDTO;
import com.foodrecipes.credentials.credentials.dto.ChangePasswordRequest;
import com.foodrecipes.credentials.credentials.dto.ForgotPasswordRequest;
import com.foodrecipes.credentials.credentials.dto.UserDTO;
import com.foodrecipes.credentials.credentials.dto.UserProfileDTO;
import com.foodrecipes.credentials.credentials.entity.Token;
import com.foodrecipes.credentials.credentials.entity.User;
import com.foodrecipes.credentials.credentials.entity.UserProfile;
import com.foodrecipes.credentials.credentials.restcontrollers.CredentialsRestController;
import com.foodrecipes.credentials.credentials.service.PasswordService;
import com.foodrecipes.credentials.credentials.service.TokenService;
import com.foodrecipes.credentials.credentials.service.UserProfileService;
import com.foodrecipes.credentials.credentials.service.UserService;

@WebMvcTest(CredentialsRestController.class)
@Import(CredentialsRestControllerTest.NoSecurityConfig.class)
public class CredentialsRestControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private UserService userService;
	@MockBean
	private UserProfileService userProfileService;
	@MockBean
	private PasswordService passwordService;
	@MockBean
	private TokenService tokenService;

	@Autowired
	private ObjectMapper objectMapper;

	@TestConfiguration
	static class NoSecurityConfig {
		@Bean
		public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
			http.csrf().disable().authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
			return http.build();
		}
	}

	@Test
	void createUser_whenValid_shouldReturnCreated() throws Exception {
		UserDTO userDTO = new UserDTO();
		userDTO.setEmail("test@example.com");
		userDTO.setPassword("password123");
		userDTO.setUsername("testuser");

		User user = new User();
		user.setId(1L);
		user.setEmail(userDTO.getEmail());

		when(userService.isUserExist(userDTO.getEmail())).thenReturn(false);
		when(userProfileService.isUserProfileExist(userDTO.getUsername())).thenReturn(false);
		when(userService.hashPassword(userDTO.getPassword())).thenReturn("hashed");
		when(userService.createUser(any(User.class))).thenReturn(user);

		// Fix: Simulate setting ID when profile is saved
		doAnswer(invocation -> {
			UserProfile profileArg = invocation.getArgument(0);
			profileArg.setId(1L);
			return null;
		}).when(userProfileService).createUserProfile(any(UserProfile.class));

		mockMvc.perform(post("/credentials/create-user").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(userDTO))).andExpect(status().isCreated())
				.andExpect(jsonPath("$.userId").value(1L)).andExpect(jsonPath("$.userProfileId").value(1L));
	}

	@Test
	void createUser_whenEmailExists_shouldReturnConflict() throws Exception {
		UserDTO userDTO = new UserDTO();
		userDTO.setEmail("test@example.com");
		userDTO.setPassword("password123");
		userDTO.setUsername("testuser");

		when(userService.isUserExist(userDTO.getEmail())).thenReturn(true);

		mockMvc.perform(post("/credentials/create-user").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(userDTO))).andExpect(status().isConflict())
				.andExpect(jsonPath("$.error").value("User with this email already exists"));
	}

	@Test
	void checkLoginCredentials_invalidPassword_shouldReturnBadRequest() throws Exception {
		AuthenticationDTO dto = new AuthenticationDTO();
		dto.setEmail("test@example.com");
		dto.setPassword("any"); // too short
		dto.setRememberMe(false);

		mockMvc.perform(post("/credentials/check-login-credentials").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(dto))).andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.password").value("Password must be at least 8 characters long"));
	}

	@Test
	void verifyEmail_shouldReturnOk() throws Exception {
		String email = "test@example.com";
		when(userService.findUserByEmail(email)).thenReturn(Optional.of(new User()));

		mockMvc.perform(put("/credentials/verify-email").param("email", email)).andExpect(status().isOk())
				.andExpect(content().string("true"));
	}

	@Test
	void deleteToken_whenTokenFound_shouldReturnOk() throws Exception {
		when(tokenService.deleteToken(1L, "abc123")).thenReturn(true);

		mockMvc.perform(delete("/credentials/delete-token").param("userId", "1").param("token", "abc123"))
				.andExpect(status().isOk()).andExpect(jsonPath("$.message").value("Token successfully deleted"));
	}

	@Test
	void changePassword_success() throws Exception {
		ChangePasswordRequest req = new ChangePasswordRequest();
		req.setEmail("test@example.com");
		req.setCurrentPassword("oldpass");
		req.setNewPassword("newpass123");

		User user = new User("test@example.com", "hashed-old", false);
		when(userService.findUserByEmail(req.getEmail())).thenReturn(Optional.of(user));
		when(passwordService.matchPasswords(req.getCurrentPassword(), user.getPassword())).thenReturn(true);

		mockMvc.perform(post("/credentials/change-password").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(req))).andExpect(status().isOk())
				.andExpect(jsonPath("$.message").value("Password successfully changed"));
	}

	@Test
	void forgotPassword_invalidPassword_shouldReturnBadRequest() throws Exception {
		ForgotPasswordRequest req = new ForgotPasswordRequest();
		req.setEmail("test@example.com");
		req.setNewPassword("new"); // Invalid, too short

		mockMvc.perform(post("/credentials/forgot-password").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(req))).andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.newPassword").value("New password must be at least 8 characters long"));
	}

	@Test
	void createUserProfile_whenUserNotFound_shouldReturnNotFound() throws Exception {
		UserProfileDTO dto = new UserProfileDTO();
		dto.setUserId(99L);
		dto.setUsername("newuser");

		when(userService.findUserById(99L)).thenReturn(Optional.empty());

		mockMvc.perform(post("/credentials/create-user-profile").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(dto))).andExpect(status().isNotFound())
				.andExpect(jsonPath("$.error").value("User not found"));
	}

	@Test
	void createUserProfile_whenProfileExists_shouldReturnConflict() throws Exception {
		UserProfileDTO dto = new UserProfileDTO();
		dto.setUserId(1L);
		dto.setUsername("newuser");

		when(userService.findUserById(1L)).thenReturn(Optional.of(new User()));
		when(userProfileService.existsByUserId(1L)).thenReturn(true);

		mockMvc.perform(post("/credentials/create-user-profile").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(dto))).andExpect(status().isConflict())
				.andExpect(jsonPath("$.error").value("User profile already exists"));
	}

	@Test
	void createUserProfile_whenUsernameTaken_shouldReturnConflict() throws Exception {
		UserProfileDTO dto = new UserProfileDTO();
		dto.setUserId(1L);
		dto.setUsername("taken");

		when(userService.findUserById(1L)).thenReturn(Optional.of(new User()));
		when(userProfileService.existsByUserId(1L)).thenReturn(false);
		when(userProfileService.isUserProfileExist("taken")).thenReturn(true);

		mockMvc.perform(post("/credentials/create-user-profile").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(dto))).andExpect(status().isConflict())
				.andExpect(jsonPath("$.error").value("Username already taken"));
	}

	@Test
	void getUserByEmail_whenNotFound_shouldReturnNotFound() throws Exception {
		when(userService.findUserByEmail("missing@example.com")).thenReturn(Optional.empty());

		mockMvc.perform(get("/credentials/get-user-email/").param("email", "missing@example.com"))
				.andExpect(status().isNotFound());
	}

	@Test
	void getUserProfileByToken_whenInvalid_shouldReturnNotFound() throws Exception {
		when(userProfileService.getUserProfileByToken("invalid-token")).thenReturn(Optional.empty());

		mockMvc.perform(get("/credentials/get-user-profile-by-token").param("token", "invalid-token"))
				.andExpect(status().isNotFound());
	}

	@Test
	void getUserByToken_whenTokenNotFound_shouldReturnNotFound() throws Exception {
		when(tokenService.findToken("invalid")).thenReturn(null);

		mockMvc.perform(get("/credentials/get-user-token").param("token", "invalid")).andExpect(status().isNotFound());
	}

	@Test
	void getUserByToken_whenUserNotFound_shouldReturnNotFound() throws Exception {
		Token token = new Token("abc", new User());
		token.getUser().setId(123L);

		when(tokenService.findToken("abc")).thenReturn(token);
		when(userService.findUserById(123L)).thenReturn(Optional.empty());

		mockMvc.perform(get("/credentials/get-user-token").param("token", "abc")).andExpect(status().isNotFound());
	}

	@Test
	void userExistsByEmail_whenExists_shouldReturnOk() throws Exception {
		when(userService.isUserExist("test@example.com")).thenReturn(true);

		mockMvc.perform(get("/credentials/exists-by-email/test@example.com")).andExpect(status().isOk())
				.andExpect(content().string("true"));
	}

	@Test
	void userExistsByEmail_whenNotExists_shouldReturnNotFound() throws Exception {
		when(userService.isUserExist("missing@example.com")).thenReturn(false);

		mockMvc.perform(get("/credentials/exists-by-email/missing@example.com")).andExpect(status().isNotFound())
				.andExpect(content().string("false"));
	}

	@Test
	void checkLoginCredentials_whenUserNotFound_shouldReturnUnauthorized() throws Exception {
		AuthenticationDTO dto = new AuthenticationDTO();
		dto.setEmail("nonexistent@example.com");
		dto.setPassword("validPass123"); // ✅ Valid length
		dto.setRememberMe(false);

		when(userService.findUserByEmail(dto.getEmail())).thenReturn(Optional.empty());

		mockMvc.perform(post("/credentials/check-login-credentials").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(dto))).andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.error").value("Invalid email or password"));
	}

	@Test
	void checkLoginCredentials_whenPasswordTooShort_shouldReturnBadRequest() throws Exception {
		AuthenticationDTO dto = new AuthenticationDTO();
		dto.setEmail("test@example.com");
		dto.setPassword("abc"); // Too short
		dto.setRememberMe(false);

		mockMvc.perform(post("/credentials/check-login-credentials").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(dto))).andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.password").value("Password must be at least 8 characters long"));
	}

	@Test
	void checkLoginCredentials_whenPasswordMismatch_shouldReturnUnauthorized() throws Exception {
		AuthenticationDTO dto = new AuthenticationDTO();
		dto.setEmail("test@example.com");
		dto.setPassword("wrongpass");

		User user = new User("test@example.com", "hashed-correct-pass", true);
		when(userService.findUserByEmail(dto.getEmail())).thenReturn(Optional.of(user));
		when(passwordService.matchPasswords(dto.getPassword(), user.getPassword())).thenReturn(false);

		mockMvc.perform(post("/credentials/check-login-credentials").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(dto))).andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.error").value("Invalid email or password"));
	}

	@Test
	void checkLoginCredentials_withRememberMe_shouldReturnToken() throws Exception {
		AuthenticationDTO dto = new AuthenticationDTO();
		dto.setEmail("test@example.com");
		dto.setPassword("password123");
		dto.setRememberMe(true);

		User user = new User("test@example.com", "hashed", true);
		user.setId(1L);

		when(userService.findUserByEmail(dto.getEmail())).thenReturn(Optional.of(user));
		when(passwordService.matchPasswords(dto.getPassword(), user.getPassword())).thenReturn(true);

		mockMvc.perform(post("/credentials/check-login-credentials").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(dto))).andExpect(status().isOk())
				.andExpect(jsonPath("$.message").value("Login successful")).andExpect(jsonPath("$.userId").value(1L))
				.andExpect(jsonPath("$.token").exists());
	}

	@Test
	void changePassword_whenOldPasswordWrong_shouldReturnBadRequest() throws Exception {
		ChangePasswordRequest req = new ChangePasswordRequest();
		req.setEmail("test@example.com");
		req.setCurrentPassword("wrong");
		req.setNewPassword("newpassword"); // must be at least 8 chars for validation to pass

		User user = new User("test@example.com", "correct-hashed", false);

		when(userService.findUserByEmail(req.getEmail())).thenReturn(Optional.of(user));
		when(passwordService.matchPasswords("wrong", "correct-hashed")).thenReturn(false);

		mockMvc.perform(post("/credentials/change-password").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(req))).andExpect(status().isBadRequest()).andExpect(result -> {
					Throwable exception = result.getResolvedException();
					assertNotNull(exception);
					assertTrue(exception instanceof IllegalArgumentException);
					assertEquals("Current password is incorrect", exception.getMessage());
				});
	}

	@Test
	void changePassword_whenUserNotFound_shouldThrow() throws Exception {
		ChangePasswordRequest req = new ChangePasswordRequest();
		req.setEmail("missing@example.com");
		req.setCurrentPassword("any");
		req.setNewPassword("newpassword"); // ✅ meets minimum size requirement

		when(userService.findUserByEmail("missing@example.com")).thenReturn(Optional.empty());

		mockMvc.perform(post("/credentials/change-password").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(req))).andExpect(status().isNotFound());
	}

	@Test
	void forgotPassword_whenUserNotFound_shouldThrow() throws Exception {
		ForgotPasswordRequest req = new ForgotPasswordRequest();
		req.setEmail("missing@example.com");
		req.setNewPassword("newpassword"); // ✅ 10 chars

		when(userService.findUserByEmail(req.getEmail())).thenReturn(Optional.empty());

		mockMvc.perform(post("/credentials/forgot-password").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(req))).andExpect(status().isNotFound());
	}

	@Test
	void deleteToken_whenTokenNotFound_shouldReturn404() throws Exception {
		when(tokenService.deleteToken(1L, "invalid")).thenReturn(false);

		mockMvc.perform(delete("/credentials/delete-token").param("userId", "1").param("token", "invalid"))
				.andExpect(status().isNotFound()).andExpect(jsonPath("$.error").value("Token not found"));
	}

	@Test
	void getUserProfileByUserId_whenNotFound_shouldThrow() throws Exception {
		when(userProfileService.findByUserId(1L)).thenReturn(Optional.empty());

		mockMvc.perform(get("/credentials/user-profile/1")).andExpect(status().isNotFound());
	}

	@Test
	void getUserProfileByUserId_shouldReturnProfile() throws Exception {
		User user = new User("user@example.com", "pass", true);
		user.setId(1L);

		UserProfile profile = new UserProfile();
		profile.setUser(user);
		profile.setUsername("testuser");
		profile.setDescription("desc");
		profile.setBio("bio");
		profile.setLink("link");
		profile.setLocation("loc");
		profile.setProfileImage("image.png");

		when(userProfileService.findByUserId(1L)).thenReturn(Optional.of(profile));

		mockMvc.perform(get("/credentials/user-profile/1")).andExpect(status().isOk())
				.andExpect(jsonPath("$.username").value("testuser"));
	}

}
