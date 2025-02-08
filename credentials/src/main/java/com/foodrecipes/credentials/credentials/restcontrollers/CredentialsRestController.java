package com.foodrecipes.credentials.credentials.restcontrollers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
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
import org.springframework.web.multipart.MultipartFile;

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
	@Autowired
	private Environment environment;

    @PostMapping("/create-user/")
    public ResponseEntity<String> createUser(@RequestBody UserProfileDTO userProfileDTO) {
    	
    	if(!userProfileDTO.isNullOrEmpty()) {
    		if(userProfileDTO.getPassword().length() >= Constants.MINIMUM_PASSWORD_SIZE) {
    			User user = new User();
            user.setEmail(userProfileDTO.getEmail());
            
            UserProfile userProfile = new UserProfile();
            userProfile.setUsername(userProfileDTO.getUsername());
            
            if(userService.isUserExist(user)) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("User with this email already exists");
            }
            if(userProfileService.isUserProfileExist(userProfile)) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("User with this username already exists");
            }
            if(!userProfileDTO.getPassword().startsWith(PasswordUtils.BCRYPT_PATTERN)) {
            	user.setPassword(userService.hashPassword(userProfileDTO.getPassword()).substring(PasswordUtils.BCRYPT_PATTERN_SIZE));
            }
            else {
                user.setPassword(userProfileDTO.getPassword().substring(PasswordUtils.BCRYPT_PATTERN_SIZE));
            }
            
            //userProfile.setProfilePicture(userProfileDTO.getProfilePicture());
            String port = environment.getProperty("local.server.port");
            System.out.println("port: " + port);
            
            user = userService.createUser(user);
            userProfile.setUser(user);
            userProfile.setProfilePicture(Constants.DEFAULT_PROFILE_IMAGE);
            userProfileService.createUserProfile(userProfile);
            
            return ResponseEntity.status(HttpStatus.CREATED).body("User is successfully created");
    		}
    		else {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Password should contain minimum "+ Constants.MINIMUM_PASSWORD_SIZE +" characters");
    		}
    		
    		
    	}
    	
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Something is wrong");
    }
	
    @GetMapping("/get-user-email/")
    public User getUserByEmail(@RequestParam String email) {
    	User user = userService.findUserByEmail(email);
    	String port = environment.getProperty("local.server.port");
        user.setEnvironment(port);
    	return user;
    }
    
    @PostMapping("/check-login-credentials/")
    public User checkLoginCredentials(@RequestBody AuthenticationDTO authenticationDTO) {
    	User target = userService.findUserByEmail(authenticationDTO.getEmail());
    	if(target == null) {
    		return null;
    	}
    	StringBuilder pw = new StringBuilder(PasswordUtils.BCRYPT_PATTERN);
    	pw.append(target.getPassword());
    	System.out.println(pw.toString());
    	if(passwordService.matchPasswords(authenticationDTO.getPassword(), pw.toString())) {
            //userService.updateToken(target.getEmail(), user.getToken());
    		if(authenticationDTO.getToken()!= null && authenticationDTO.getToken().trim().length() != 0) {
    			System.out.println("here");
    			Token tk = new Token(authenticationDTO.getToken(), target);
    			tokenService.addToken(tk);
    		}
            
    		/*if(!target.isVerified()) {
            	// Call email service
            }*/
    		return target;
    	}
    	return null;
    }
    
    @GetMapping("/get-user-token/")
    public User getUserByToken(@RequestParam String token) {
    	Token tokenTarget = tokenService.findToken(token);
    	if(tokenTarget == null) {
    		return null;
    	}
    	User user = userService.findUserById(tokenTarget.getUser().getId());
    	if(user != null) {
    		/*if(!user.isVerified()) {
    			// Call email service
	    	}*/
    		return user;
    	}
    	else {
    		return null;
    	}
    }
    
    @PutMapping("/verify-email/")
    public Boolean verifyEmail(@RequestParam String email) {
    	User user = userService.findUserByEmail(email);
    	if(user != null) {
    		userService.updateVerified(email);
    		return true;
    	}
    	else {
    		return false;
    	}
    }
    
    @PostMapping("/change-profile-picture/")
    public void changeProfilePicture(@RequestParam("file") MultipartFile file) {
    	
    	
    	
    }
    
    @GetMapping("/get-user-profile-by-token/")
    public ResponseEntity<UserProfile> getUserProfileByToken(@RequestParam String token) {
        UserProfile userProfile = userProfileService.getUserProfileByToken(token);
        if (userProfile != null) {
            return ResponseEntity.ok(userProfile);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
    
    @DeleteMapping("/delete-token")
    public void deleteToken(@RequestParam Long userId, @RequestParam String token) {
        tokenService.deleteToken(userId, token);
    }
    
    @GetMapping("/exists-by-email/{email}")
    public boolean userExistsByEmail(@PathVariable String email) {
        return userService.userExists(email);
    }
    
    @PostMapping("/change-password")
    public boolean changePassword(@RequestBody ChangePasswordRequest request) {
    	if(request.getNewPassword().length() < Constants.MINIMUM_PASSWORD_SIZE) {
    		return false;
    	}
        User user = userService.findUserByEmail(request.getEmail());
        
        if(user == null) {
        	return false;
        }
        
        if(!request.getNewPassword().startsWith(PasswordUtils.BCRYPT_PATTERN)) {
        	user.setPassword(userService.hashPassword(request.getNewPassword()).substring(PasswordUtils.BCRYPT_PATTERN_SIZE));
        }
        else {
            user.setPassword(request.getNewPassword().substring(PasswordUtils.BCRYPT_PATTERN_SIZE));
        }
        
        User updatedUser = userService.createUser(user);
        
        if(updatedUser == null) {
        	return false;
        }
        
        return true;
    }
    
}
