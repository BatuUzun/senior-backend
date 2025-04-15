package com.foodrecipes.credentials.credentials.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.foodrecipes.credentials.credentials.dto.UserProfileResponseProfileGetterDTO;
import com.foodrecipes.credentials.credentials.entity.UserFollow;
import com.foodrecipes.credentials.credentials.repository.UserFollowsRepository;
import com.foodrecipes.credentials.credentials.constants.Constants;

@Service
public class UserFollowsService {

	@Autowired
	private UserFollowsRepository userFollowsRepository;

	@Transactional
	public String followUser(Long followerId, Long followedId) {
		if (followerId.equals(followedId)) {
			return "You cannot follow yourself!";
		}

		boolean alreadyFollowing = userFollowsRepository.existsByFollowerIdAndFollowedId(followerId, followedId);
		if (alreadyFollowing) {
			return "You are already following this user.";
		}

		UserFollow userFollow = new UserFollow();
		userFollow.setFollowerId(followerId);
		userFollow.setFollowedId(followedId);
		userFollowsRepository.save(userFollow);

		return "Successfully followed the user.";
	}

	@Transactional
	public String unfollowUser(Long followerId, Long followedId) {
		boolean alreadyFollowing = userFollowsRepository.existsByFollowerIdAndFollowedId(followerId, followedId);
		if (!alreadyFollowing) {
			return "You are not following this user.";
		}

		userFollowsRepository.deleteByFollowerIdAndFollowedId(followerId, followedId);

		return "Successfully unfollowed the user.";
	}

	public boolean isFollowing(Long followerId, Long followedId) {
		// Check Redis first

		// Fallback to MySQL (if Redis doesn't have the data)
		boolean existsInDB = userFollowsRepository.existsByFollowerIdAndFollowedId(followerId, followedId);

		return existsInDB;
	}

	public long getFollowerCount(Long userProfileId) {

		long count = userFollowsRepository.countByFollowedId(userProfileId);

		return count; // Ensure a non-null value is returned
	}

	public long getFollowingCount(Long userProfileId) {

		long count = userFollowsRepository.countByFollowerId(userProfileId);

		return count; // Ensure a non-null value is returned
	}

    @Autowired
    private UserProfileService userProfileService;


	public List<UserProfileResponseProfileGetterDTO> getFollowings(Long userId, LocalDateTime cursor) {
		if (cursor == null) {
			cursor = LocalDateTime.of(2000, 1, 1, 0, 0); // Default cursor (fetch from start)
		}

		List<Long> followedIds = userFollowsRepository.findFollowingsByUserId(userId, cursor);
		
			List<UserProfileResponseProfileGetterDTO> profiles = userProfileService.getUserProfilesByIds(followedIds);
			return profiles;
		
	}

	public List<UserProfileResponseProfileGetterDTO> getFollowers(Long userId, LocalDateTime cursor) {
		if (cursor == null) {
			cursor = LocalDateTime.of(2000, 1, 1, 0, 0);
		}

		List<Long> followerIds = userFollowsRepository.findFollowersByUserIdNative(userId, cursor, Constants.PAGE_SIZE);
		List<UserProfileResponseProfileGetterDTO> profiles = userProfileService.getUserProfilesByIds(followerIds);
		return profiles;
	}

	public Set<Long> getFollowedUsers(Long userId) {
	    return userFollowsRepository.findFollowedUsersByUserId(userId);
	}


}
