package com.foodrecipes.credentials.credentials.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.foodrecipes.credentials.credentials.entity.UserProfileProfileAPI;
import com.foodrecipes.credentials.credentials.repository.ChatMessageRepository;
import com.foodrecipes.credentials.credentials.repository.ConversationRepository;
import com.foodrecipes.credentials.credentials.repository.FavoriteRepository;
import com.foodrecipes.credentials.credentials.repository.LikeRepository;
import com.foodrecipes.credentials.credentials.repository.ReviewCommentRepository;
import com.foodrecipes.credentials.credentials.repository.ReviewLikeRepository;
import com.foodrecipes.credentials.credentials.repository.ReviewRepository;
import com.foodrecipes.credentials.credentials.repository.TokenRepository;
import com.foodrecipes.credentials.credentials.repository.UserDeviceTokenRepository;
import com.foodrecipes.credentials.credentials.repository.UserFollowsRepository;
import com.foodrecipes.credentials.credentials.repository.UserProfileProfileApiRepository;
import com.foodrecipes.credentials.credentials.repository.UserRepository;

import jakarta.transaction.Transactional;


@Service
public class UserProfileProfileApiService {
	
	@Autowired
    private UserProfileProfileApiRepository userProfileRepository; 
	
	@Autowired
    private ChatMessageRepository chatMessageRepository; 

	@Autowired
    private UserFollowsRepository userFollowsRepository; 

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private ReviewLikeRepository reviewLikeRepository;
    
    @Autowired
    private ReviewCommentRepository reviewCommentRepository;

    
    @Autowired
    private ReviewRepository reviewRepository;
    
    @Autowired
    private UserDeviceTokenRepository userDeviceTokenRepository;

    @Autowired
    private TokenRepository tokensRepository;

    @Autowired
    private ConversationRepository conversationRepository;

    
    @Autowired
    private UserRepository usersRepository;

    
    
    
    
	
	public UserProfileProfileAPI getUserProfileById(Long id) {
        return userProfileRepository.findById(id).orElse(null);        
	}
	
	/*public UserProfile getUserProfileByEmail(String email) {
		return userProfileRepository.findByUserEmail(email);	
	}*/
	
	@Transactional
    public void updateProfilePicture(Long userProfileId, String newProfileImage) {
        userProfileRepository.updateProfileImage(userProfileId, newProfileImage);
    }
    
    public boolean isUserProfileExist(Long id) {
        return userProfileRepository.existsById(id);        
	}
    
    public String getUserProfilePictureById(Long id) {
    	return userProfileRepository.findUserProfileImageById(id);
    }
    
    
    
    public String getProfilePictureByUserId(Long userId) {
        return userProfileRepository.findById(userId)
                .map(UserProfileProfileAPI::getProfileImage)
                .orElseThrow(() -> new RuntimeException("UserProfile not found with id: " + userId));
    }
    
    
    
    public List<UserProfileProfileAPI> getAllProfiles() {
        return userProfileRepository.findAll();
    }

    public Optional<UserProfileProfileAPI> getProfileById(Long id) {
        return userProfileRepository.findById(id);
    }

    public UserProfileProfileAPI createProfile(UserProfileProfileAPI profile) {
        return userProfileRepository.save(profile);
    }

    public UserProfileProfileAPI updateProfile(Long id, UserProfileProfileAPI profileDetails) {
        UserProfileProfileAPI profile = userProfileRepository.findById(id).orElseThrow(() -> new RuntimeException("Profile not found"));
        profile.setUsername(profileDetails.getUsername());
        profile.setDescription(profileDetails.getDescription());
        profile.setBio(profileDetails.getBio());
        profile.setLink(profileDetails.getLink());
        profile.setLocation(profileDetails.getLocation());
        profile.setProfileImage(profileDetails.getProfileImage());
        return userProfileRepository.save(profile);
    }

    @Transactional
    public void deleteProfile(Long userProfileId) {
        // Delete chat messages where user is sender or receiver
        chatMessageRepository.deleteBySenderIdOrReceiverId(userProfileId, userProfileId);

        // Delete from user_follows (both follower and followed cases)
        userFollowsRepository.deleteByFollowerIdOrFollowedId(userProfileId, userProfileId);

        // Delete from favorite, likes
        favoriteRepository.deleteByUserId(userProfileId);
        likeRepository.deleteByUserId(userProfileId);

        // Delete review_likes first (depends on review)
        reviewLikeRepository.deleteByUserId(userProfileId);

        // Delete review_comments
        reviewCommentRepository.deleteByUserId(userProfileId);

        // Delete reviews (after review_likes/comments)
        reviewRepository.deleteByUserId(userProfileId);

        // Delete device tokens
        userDeviceTokenRepository.deleteByUserId(userProfileId);

        // Delete tokens from `tokens` table (via user_id from user table, see note below)
        tokensRepository.deleteByUserId(userProfileId);

        // Delete conversations where user is user1 or user2
        conversationRepository.deleteByUser1OrUser2(userProfileId, userProfileId);

        // Finally, delete the user profile
        userProfileRepository.deleteById(userProfileId);

        // If you also want to delete from `users` table:
        usersRepository.deleteById(userProfileId);
    }


}
