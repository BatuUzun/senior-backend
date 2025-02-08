package com.foodrecipes.credentials.credentials.restcontrollers;

import java.util.Optional;

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
import com.foodrecipes.credentials.credentials.dto.UserProfileDTO;
import com.foodrecipes.credentials.credentials.entity.Token;
import com.foodrecipes.credentials.credentials.entity.User;
import com.foodrecipes.credentials.credentials.entity.UserProfile;
import com.foodrecipes.credentials.credentials.security.PasswordUtils;
import com.foodrecipes.credentials.credentials.service.PasswordService;
import com.foodrecipes.credentials.credentials.service.TokenService;
import com.foodrecipes.credentials.credentials.service.UserProfileService;
import com.foodrecipes.credentials.credentials.service.UserService;

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

	@PostMapping("/create-user/")
	public ResponseEntity<String> createUser(@RequestBody UserProfileDTO userProfileDTO) {

	    if (userProfileDTO == null || userProfileDTO.isNullOrEmpty()) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid request data");
	    }

	    if (userProfileDTO.getPassword().length() < Constants.MINIMUM_PASSWORD_SIZE) {
	        return ResponseEntity.status(HttpStatus.CONFLICT)
	                .body("Password should contain a minimum of " + Constants.MINIMUM_PASSWORD_SIZE + " characters");
	    }

	    // Check if email already exists
	    if (userService.isUserExist(userProfileDTO.getEmail())) {
	        return ResponseEntity.status(HttpStatus.CONFLICT).body("User with this email already exists");
	    }

	    // Check if username already exists
	    if (userProfileService.isUserProfileExist(userProfileDTO.getUsername())) {
	        return ResponseEntity.status(HttpStatus.CONFLICT).body("User with this username already exists");
	    }

	    // Create a new User entity
	    User user = new User();
	    user.setEmail(userProfileDTO.getEmail());

	    // Hash the password if not already hashed
	    if (!userProfileDTO.getPassword().startsWith(PasswordUtils.BCRYPT_PATTERN)) {
	        user.setPassword(userService.hashPassword(userProfileDTO.getPassword()).substring(PasswordUtils.BCRYPT_PATTERN_SIZE));
	    } else {
	        user.setPassword(userProfileDTO.getPassword().substring(PasswordUtils.BCRYPT_PATTERN_SIZE));
	    }

	    // Create a new UserProfile entity
	    UserProfile userProfile = new UserProfile();
	    userProfile.setUsername(userProfileDTO.getUsername());
	    userProfile.setProfileImage(Constants.DEFAULT_PROFILE_IMAGE);

	    // Save User
	    user = userService.createUser(user);
	    userProfile.setUser(user);

	    // Save UserProfile
	    userProfileService.createUserProfile(userProfile);

	    // Log the server port (useful for debugging in local environment)

	    return ResponseEntity.status(HttpStatus.CREATED).body("User successfully created");
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

    
    @PostMapping("/check-login-credentials/")
    public ResponseEntity<User> checkLoginCredentials(@RequestBody AuthenticationDTO authenticationDTO) {

        Optional<User> targetOptional = userService.findUserByEmail(authenticationDTO.getEmail());

        if (targetOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        User target = targetOptional.get();
        StringBuilder pw = new StringBuilder(PasswordUtils.BCRYPT_PATTERN);
        pw.append(target.getPassword());
        
        System.out.println(pw.toString());

        if (passwordService.matchPasswords(authenticationDTO.getPassword(), pw.toString())) {
            if (authenticationDTO.getToken() != null && !authenticationDTO.getToken().trim().isEmpty()) {
                System.out.println("here");
                Token tk = new Token(authenticationDTO.getToken(), target);
                tokenService.addToken(tk);
            }

            /*if (!target.isVerified()) {
                // Call email service
            }*/

            return ResponseEntity.ok(target);
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }

    
    @GetMapping("/get-user-token/")
    public ResponseEntity<User> getUserByToken(@RequestParam String token) {
        Token tokenTarget= tokenService.findToken(token);

        if (tokenTarget == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        Optional<User> userOptional = userService.findUserById(tokenTarget.getUser().getId());

        if (userOptional.isPresent()) {
            /*if (!userOptional.get().isVerified()) {
                // Call email service
            }*/
            return ResponseEntity.ok(userOptional.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    
    @PutMapping("/verify-email/")
    public ResponseEntity<Boolean> verifyEmail(@RequestParam String email) {
        Optional<User> userOptional = userService.findUserByEmail(email);

        if (userOptional.isPresent()) {
            userService.updateVerified(email);
            return ResponseEntity.ok(true);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(false);
        }
    }
    
    @GetMapping("/get-user-profile-by-token/")
    public ResponseEntity<UserProfile> getUserProfileByToken(@RequestParam String token) {
        Optional<UserProfile> userProfileOptional = userProfileService.getUserProfileByToken(token);

        if (userProfileOptional.isPresent()) {
            return ResponseEntity.ok(userProfileOptional.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    
    @DeleteMapping("/delete-token")
    public void deleteToken(@RequestParam Long userId, @RequestParam String token) {
        tokenService.deleteToken(userId, token);
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

    
    @PostMapping("/change-password")
    public ResponseEntity<Boolean> changePassword(@RequestBody ChangePasswordRequest request) {
        if (request.getNewPassword().length() < Constants.MINIMUM_PASSWORD_SIZE) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
        }

        Optional<User> userOptional = userService.findUserByEmail(request.getEmail());

        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(false);
        }

        User user = userOptional.get();

        if (!request.getNewPassword().startsWith(PasswordUtils.BCRYPT_PATTERN)) {
            user.setPassword(userService.hashPassword(request.getNewPassword()).substring(PasswordUtils.BCRYPT_PATTERN_SIZE));
        } else {
            user.setPassword(request.getNewPassword().substring(PasswordUtils.BCRYPT_PATTERN_SIZE));
        }

        User updatedUser = userService.createUser(user);

        if (updatedUser == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
        }

        return ResponseEntity.ok(true);
    }

    
}
