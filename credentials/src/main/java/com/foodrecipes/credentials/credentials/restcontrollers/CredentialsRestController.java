package com.foodrecipes.credentials.credentials.restcontrollers;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.foodrecipes.credentials.credentials.constants.Constants;
import com.foodrecipes.credentials.credentials.dto.AuthenticationDTO;
import com.foodrecipes.credentials.credentials.dto.ChangePasswordRequest;
import com.foodrecipes.credentials.credentials.dto.ForgotPasswordRequest;
import com.foodrecipes.credentials.credentials.dto.UserDTO;
import com.foodrecipes.credentials.credentials.dto.UserProfileDTO;
import com.foodrecipes.credentials.credentials.dto.UserProfileResponseDTO;
import com.foodrecipes.credentials.credentials.entity.Token;
import com.foodrecipes.credentials.credentials.entity.User;
import com.foodrecipes.credentials.credentials.entity.UserProfile;
import com.foodrecipes.credentials.credentials.exception.UserNotFoundException;
import com.foodrecipes.credentials.credentials.service.PasswordService;
import com.foodrecipes.credentials.credentials.service.TokenService;
import com.foodrecipes.credentials.credentials.service.UserProfileService;
import com.foodrecipes.credentials.credentials.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/credentials")
public class CredentialsRestController {
	
	
	@Autowired
    private UserService userService;
	@Autowired
    private UserProfileService userProfileService;
	@Autowired
    private PasswordService passwordService;
	@Autowired
	private TokenService tokenService;

	@PostMapping("/create-user")
	public ResponseEntity<?> createUser(@Valid @RequestBody UserDTO userDTO) {
	    if (userService.isUserExist(userDTO.getEmail())) {
	        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", "User with this email already exists"));
	    }

	    // Hash the password before storing
	    String hashedPassword = userService.hashPassword(userDTO.getPassword());

	    User user = new User();
	    user.setEmail(userDTO.getEmail());
	    user.setPassword(hashedPassword);
	    user = userService.createUser(user);

	    return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
	        "message", "User successfully created",
	        "userId", user.getId()
	    ));
	}


	@PostMapping("/create-user-profile")
    public ResponseEntity<?> createUserProfile(@Valid @RequestBody UserProfileDTO userProfileDTO) {
        // Check if the user exists
        Optional<User> userOptional = userService.findUserById(userProfileDTO.getUserId());
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "User not found"));
        }

     // Check if the user already has a profile
        if (userProfileService.existsByUserId(userProfileDTO.getUserId())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", "User profile already exists"));
        }
        
        // Check if username already exists
        if (userProfileService.isUserProfileExist(userProfileDTO.getUsername())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", "Username already taken"));
        }

        User user = userOptional.get();

        // Create and save the user profile
        UserProfile userProfile = new UserProfile();
        userProfile.setUser(user);
        userProfile.setUsername(userProfileDTO.getUsername());
        userProfile.setDescription(userProfileDTO.getDescription());
        userProfile.setBio(userProfileDTO.getBio());
        userProfile.setLink(userProfileDTO.getLink());
        userProfile.setLocation(userProfileDTO.getLocation());
        userProfile.setProfileImage(Constants.DEFAULT_PROFILE_IMAGE);

        userProfileService.createUserProfile(userProfile);

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
            "message", "User profile created successfully",
            "userProfileId", userProfile.getId()
        ));
    }

    
    
    @GetMapping("/get-user-email/")
    public ResponseEntity<User> getUserByEmail(@RequestParam String email) {
        Optional<User> user = userService.findUserByEmail(email);

        if (user.isPresent()) {
            return ResponseEntity.ok(user.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    
    @PostMapping("/check-login-credentials")
    public ResponseEntity<?> checkLoginCredentials(@Valid @RequestBody AuthenticationDTO authenticationDTO) {

        Optional<User> targetOptional = userService.findUserByEmail(authenticationDTO.getEmail());

        if (targetOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid email or password"));
        }

        User target = targetOptional.get();

        if (passwordService.matchPasswords(authenticationDTO.getPassword(), target.getPassword())) {

            String generatedToken = null;

            if (authenticationDTO.isRememberMe()) {
                generatedToken = UUID.randomUUID().toString();
                Token token = new Token(generatedToken, target);
                tokenService.addToken(token);
            }

            // ✅ Fix: Only include "token" in response if generatedToken is NOT null
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Login successful");
            response.put("userId", target.getId());
            response.put("email", target.getEmail());
            response.put("isVerified", target.isVerified());

            if (generatedToken != null) {
                response.put("token", generatedToken);
            }

            return ResponseEntity.ok(response);
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid email or password"));
    }


    
    @GetMapping("/get-user-token")
    public ResponseEntity<User> getUserByToken(@RequestParam String token) {
        Token tokenTarget= tokenService.findToken(token);

        if (tokenTarget == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        Optional<User> userOptional = userService.findUserById(tokenTarget.getUser().getId());

        if (userOptional.isPresent()) {
            
            return ResponseEntity.ok(userOptional.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    
    @PutMapping("/verify-email")
    public ResponseEntity<Boolean> verifyEmail(@RequestParam String email) {
        Optional<User> userOptional = userService.findUserByEmail(email);

        if (userOptional.isPresent()) {
            userService.updateVerified(email);
            return ResponseEntity.ok(true);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(false);
        }
    }
    
    @GetMapping("/get-user-profile-by-token")
    public ResponseEntity<UserProfile> getUserProfileByToken(@RequestParam String token) {
        Optional<UserProfile> userProfileOptional = userProfileService.getUserProfileByToken(token);

        if (userProfileOptional.isPresent()) {
            return ResponseEntity.ok(userProfileOptional.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    
    @DeleteMapping("/delete-token")
    public ResponseEntity<?> deleteToken(@RequestParam Long userId, @RequestParam String token) {
        boolean isDeleted = tokenService.deleteToken(userId, token);
        
        if (isDeleted) {
            return ResponseEntity.ok(Map.of("message", "Token successfully deleted"));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Token not found"));
        }
    }

    
    @GetMapping("/exists-by-email/{email}")
    public ResponseEntity<Boolean> userExistsByEmail(@PathVariable String email) {
        boolean exists = userService.isUserExist(email);

        if (exists) {
            return ResponseEntity.ok(true);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(false);
        }
    }
    @GetMapping("/user-profile/{userId}")
    public ResponseEntity<UserProfileResponseDTO> getUserProfileByUserId(@PathVariable Long userId) {
        UserProfile userProfile = userProfileService.findByUserId(userId)
            .orElseThrow(() -> new UserNotFoundException("User profile not found"));

        UserProfileResponseDTO responseDTO = new UserProfileResponseDTO(
            userProfile.getUser().getId(),
            userProfile.getUsername(),
            userProfile.getDescription(),
            userProfile.getBio(),
            userProfile.getLink(),
            userProfile.getLocation(),
            userProfile.getProfileImage()
        );

        return ResponseEntity.ok(responseDTO);
    }

    
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        User user = userService.findUserByEmail(request.getEmail())
            .orElseThrow(() -> new UserNotFoundException("User not found"));


        if (!passwordService.matchPasswords(request.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        // Hash the new password before updating
        String newHashedPassword = userService.hashPassword(request.getNewPassword());
        user.setPassword(newHashedPassword);
        userService.createUser(user);

        return ResponseEntity.ok(Map.of("message", "Password successfully changed"));
    }
    
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        User user = userService.findUserByEmail(request.getEmail())
            .orElseThrow(() -> new UserNotFoundException("User not found"));

        // Hash the new password before updating
        String newHashedPassword = userService.hashPassword(request.getNewPassword());
        user.setPassword(newHashedPassword);
        userService.createUser(user);

        return ResponseEntity.ok(Map.of("message", "Password successfully changed"));
    }



    
}
