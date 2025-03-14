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

    /**
     * Creates a new user.
     *
     * @param userDTO The DTO containing user registration details (email, password).
     * @return ResponseEntity containing a success message and user ID if the user is created,
     *         or a CONFLICT response if a user with the given email already exists.
     */
	@PostMapping("/create-user")
	public ResponseEntity<?> createUser(@Valid @RequestBody UserDTO userDTO) {
	    // Check if a user with the given email already exists
	    if (userService.isUserExist(userDTO.getEmail())) {
	        return ResponseEntity.status(HttpStatus.CONFLICT)
	                .body(Map.of("error", "User with this email already exists"));
	    }

	    // Check if the username is already taken
	    if (userProfileService.isUserProfileExist(userDTO.getUsername())) {
	        return ResponseEntity.status(HttpStatus.CONFLICT)
	                .body(Map.of("error", "Username already taken"));
	    }

	    // Hash the password before storing for security reasons
	    String hashedPassword = userService.hashPassword(userDTO.getPassword());

	    // Create a new User entity and populate its fields
	    User user = new User();
	    user.setEmail(userDTO.getEmail());
	    user.setPassword(hashedPassword);

	    // Save the user in the database
	    user = userService.createUser(user);

	    // Create a new UserProfile entity and populate its fields
	    UserProfile userProfile = new UserProfile();
	    userProfile.setUser(user);
	    userProfile.setUsername(userDTO.getUsername()); // Set the username
	    userProfile.setDescription(""); // Empty description
	    userProfile.setBio(""); // Empty bio
	    userProfile.setLink(""); // Empty link
	    userProfile.setLocation(""); // Empty location
	    userProfile.setProfileImage(Constants.DEFAULT_PROFILE_IMAGE); // Assign a default profile image

	    // Save the user profile in the database
	    userProfileService.createUserProfile(userProfile);

	    // Return a response with the created user's ID and profile ID
	    return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
	        "message", "User and user profile successfully created",
	        "userId", user.getId(),
	        "userProfileId", userProfile.getId()
	    ));
	}

    /**
     * Creates a user profile.
     *
     * @param userProfileDTO The DTO containing user profile details (username, bio, etc.).
     * @return ResponseEntity containing a success message and user profile ID if successful,
     *         or an error message if the user does not exist, profile already exists, or username is taken.
     */
    @PostMapping("/create-user-profile")
    public ResponseEntity<?> createUserProfile(@Valid @RequestBody UserProfileDTO userProfileDTO) {
        // Check if the user exists in the system
        Optional<User> userOptional = userService.findUserById(userProfileDTO.getUserId());
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "User not found"));
        }

        // Check if the user already has a profile
        if (userProfileService.existsByUserId(userProfileDTO.getUserId())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "User profile already exists"));
        }

        // Check if the chosen username is already taken
        if (userProfileService.isUserProfileExist(userProfileDTO.getUsername())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Username already taken"));
        }

        // Retrieve the existing user entity
        User user = userOptional.get();

        // Create a new UserProfile entity and populate its fields
        UserProfile userProfile = new UserProfile();
        userProfile.setUser(user);
        userProfile.setUsername(userProfileDTO.getUsername());
        userProfile.setDescription(userProfileDTO.getDescription());
        userProfile.setBio(userProfileDTO.getBio());
        userProfile.setLink(userProfileDTO.getLink());
        userProfile.setLocation(userProfileDTO.getLocation());
        userProfile.setProfileImage(Constants.DEFAULT_PROFILE_IMAGE); // Assign a default profile image

        // Save the user profile in the database
        userProfileService.createUserProfile(userProfile);

        // Return a response with the created user's profile ID
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
            "message", "User profile created successfully",
            "userProfileId", userProfile.getId()
        ));
    }

    /**
     * Retrieves a user by email.
     *
     * @param email The email address of the user to retrieve.
     * @return ResponseEntity containing the user object if found,
     *         or NOT_FOUND response if the user does not exist.
     */
    @GetMapping("/get-user-email/")
    public ResponseEntity<User> getUserByEmail(@RequestParam String email) {
        // Attempt to retrieve the user by email
        Optional<User> user = userService.findUserByEmail(email);

        // Return the user if found, otherwise return a NOT_FOUND response
        if (user.isPresent()) {
            return ResponseEntity.ok(user.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }


    
    /**
     * Checks user login credentials.
     *
     * @param authenticationDTO DTO containing the user's email and password.
     * @return ResponseEntity with login success details if credentials match,
     *         or UNAUTHORIZED response if credentials are incorrect.
     */
    @PostMapping("/check-login-credentials")
    public ResponseEntity<?> checkLoginCredentials(@Valid @RequestBody AuthenticationDTO authenticationDTO) {
        // Check if a user exists with the provided email
        Optional<User> targetOptional = userService.findUserByEmail(authenticationDTO.getEmail());

        // If no user found, return UNAUTHORIZED response
        if (targetOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid email or password"));
        }

        User target = targetOptional.get();

        // Validate the provided password against the stored hashed password
        if (passwordService.matchPasswords(authenticationDTO.getPassword(), target.getPassword())) {

            String generatedToken = null;

            // Generate a token if the "Remember Me" option is selected
            if (authenticationDTO.isRememberMe()) {
                generatedToken = UUID.randomUUID().toString();
                Token token = new Token(generatedToken, target);
                tokenService.addToken(token);
            }

            // Construct the response JSON object
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Login successful");
            response.put("userId", target.getId());
            response.put("email", target.getEmail());
            response.put("isVerified", target.isVerified());

            // Include the token in the response if it was generated
            if (generatedToken != null) {
                response.put("token", generatedToken);
            }

            return ResponseEntity.ok(response);
        }

        // If password does not match, return UNAUTHORIZED response
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Invalid email or password"));
    }

    /**
     * Retrieves a user by authentication token.
     *
     * @param token The authentication token.
     * @return ResponseEntity containing the User object if found,
     *         or NOT_FOUND response if no user is associated with the token.
     */
    @GetMapping("/get-user-token")
    public ResponseEntity<User> getUserByToken(@RequestParam String token) {
        // Fetch the token details from the database
        Token tokenTarget = tokenService.findToken(token);

        // If the token does not exist, return NOT_FOUND response
        if (tokenTarget == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        // Retrieve the associated user using the user ID from the token
        Optional<User> userOptional = userService.findUserById(tokenTarget.getUser().getId());

        // Return the user if found, otherwise return NOT_FOUND response
        return userOptional.map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    /**
     * Verifies a user's email.
     *
     * @param email The email address of the user to verify.
     * @return ResponseEntity containing true if the email is verified,
     *         or NOT_FOUND response if the user does not exist.
     */
    @PutMapping("/verify-email")
    public ResponseEntity<Boolean> verifyEmail(@RequestParam String email) {
        // Attempt to find the user by email
        Optional<User> userOptional = userService.findUserByEmail(email);

        // If user exists, update the verification status
        if (userOptional.isPresent()) {
            userService.updateVerified(email);
            return ResponseEntity.ok(true);
        }

        // If user does not exist, return NOT_FOUND response
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(false);
    }

    /**
     * Retrieves a user profile using an authentication token.
     *
     * @param token The authentication token.
     * @return ResponseEntity containing the UserProfile if found,
     *         or NOT_FOUND response if no profile is associated with the token.
     */
    @GetMapping("/get-user-profile-by-token")
    public ResponseEntity<UserProfile> getUserProfileByToken(@RequestParam String token) {
        // Attempt to find the user profile associated with the token
        Optional<UserProfile> userProfileOptional = userProfileService.getUserProfileByToken(token);

        // Return the profile if found, otherwise return NOT_FOUND response
        return userProfileOptional.map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    /**
     * Deletes an authentication token for a user.
     *
     * @param userId The ID of the user whose token should be deleted.
     * @param token The authentication token to delete.
     * @return ResponseEntity with a success message if deleted,
     *         or NOT_FOUND response if the token does not exist.
     */
    @DeleteMapping("/delete-token")
    public ResponseEntity<?> deleteToken(@RequestParam Long userId, @RequestParam String token) {
        // Attempt to delete the token
        boolean isDeleted = tokenService.deleteToken(userId, token);

        // Return success message if deletion was successful
        if (isDeleted) {
            return ResponseEntity.ok(Map.of("message", "Token successfully deleted"));
        }

        // If token not found, return NOT_FOUND response
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Token not found"));
    }


    
    /**
     * Checks if a user exists by their email address.
     *
     * @param email The email address to check.
     * @return ResponseEntity containing `true` if the user exists,
     *         or NOT_FOUND response with `false` if the user does not exist.
     */
    @GetMapping("/exists-by-email/{email}")
    public ResponseEntity<Boolean> userExistsByEmail(@PathVariable String email) {
        // Check if the email exists in the system
        boolean exists = userService.isUserExist(email);

        // Return OK response with `true` if the user exists, otherwise return NOT_FOUND with `false`
        if (exists) {
            return ResponseEntity.ok(true);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(false);
        }
    }

    /**
     * Retrieves the user profile by user ID.
     *
     * @param userId The ID of the user.
     * @return ResponseEntity containing UserProfileResponseDTO if the profile exists,
     *         or throws UserNotFoundException if not found.
     */
    @GetMapping("/user-profile/{userId}")
    public ResponseEntity<UserProfileResponseDTO> getUserProfileByUserId(@PathVariable Long userId) {
        // Attempt to find the user profile by user ID, throw exception if not found
        UserProfile userProfile = userProfileService.findByUserId(userId)
            .orElseThrow(() -> new UserNotFoundException("User profile not found"));

        // Create a response DTO with user profile details
        UserProfileResponseDTO responseDTO = new UserProfileResponseDTO(
            userProfile.getUser().getId(),
            userProfile.getUsername(),
            userProfile.getDescription(),
            userProfile.getBio(),
            userProfile.getLink(),
            userProfile.getLocation(),
            userProfile.getProfileImage()
        );

        // Return the user profile data in the response
        return ResponseEntity.ok(responseDTO);
    }

    /**
     * Allows a user to change their password.
     *
     * @param request DTO containing the user's email, current password, and new password.
     * @return ResponseEntity with a success message if password is changed,
     *         or throws UserNotFoundException if the user does not exist,
     *         or IllegalArgumentException if the current password is incorrect.
     */
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        // Find the user by email, throw exception if not found
        User user = userService.findUserByEmail(request.getEmail())
            .orElseThrow(() -> new UserNotFoundException("User not found"));

        // Check if the provided current password matches the stored hashed password
        if (!passwordService.matchPasswords(request.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        // Hash the new password before storing it
        String newHashedPassword = userService.hashPassword(request.getNewPassword());
        user.setPassword(newHashedPassword);

        // Save the updated user password in the database
        userService.createUser(user);

        // Return success response
        return ResponseEntity.ok(Map.of("message", "Password successfully changed"));
    }

    /**
     * Allows a user to reset their password if they forgot it.
     *
     * @param request DTO containing the user's email and new password.
     * @return ResponseEntity with a success message if password is reset,
     *         or throws UserNotFoundException if the user does not exist.
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        // Find the user by email, throw exception if not found
        User user = userService.findUserByEmail(request.getEmail())
            .orElseThrow(() -> new UserNotFoundException("User not found"));

        // Hash the new password before storing it
        String newHashedPassword = userService.hashPassword(request.getNewPassword());
        user.setPassword(newHashedPassword);

        // Save the updated user password in the database
        userService.createUser(user);

        // Return success response
        return ResponseEntity.ok(Map.of("message", "Password successfully changed"));
    }
}