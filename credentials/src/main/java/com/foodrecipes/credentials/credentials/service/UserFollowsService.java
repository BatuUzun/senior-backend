package com.foodrecipes.credentials.credentials.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.foodrecipes.credentials.credentials.constants.Constants;
import com.foodrecipes.credentials.credentials.dto.PagedResponse;
import com.foodrecipes.credentials.credentials.dto.UserFollowProjection;
import com.foodrecipes.credentials.credentials.dto.UserProfileResponseProfileGetterDTO;
import com.foodrecipes.credentials.credentials.entity.UserFollow;
import com.foodrecipes.credentials.credentials.repository.UserFollowsRepository;

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


    public PagedResponse<UserProfileResponseProfileGetterDTO> getFollowings(Long userId, LocalDateTime cursor) {
        if (cursor == null) {
            cursor = LocalDateTime.of(2000, 1, 1, 0, 0);
        }

        List<UserFollowProjection> projections = userFollowsRepository.findFollowingsWithCursor(userId, cursor, PageRequest.of(0, Constants.PAGE_SIZE));
        List<Long> userIds = projections.stream().map(UserFollowProjection::getUserId).collect(Collectors.toList());
        List<UserProfileResponseProfileGetterDTO> profiles = userProfileService.getUserProfilesByIds(userIds);

        LocalDateTime nextCursor = projections.size() == Constants.PAGE_SIZE
            ? projections.get(projections.size() - 1).getDateFollowed()
            : null;

        return new PagedResponse<>(profiles, nextCursor);
    }


	public PagedResponse<UserProfileResponseProfileGetterDTO> getFollowers(Long userId, LocalDateTime cursor) {
	    if (cursor == null) {
	        cursor = LocalDateTime.of(2000, 1, 1, 0, 0);
	    }

	    List<UserFollowProjection> projections = userFollowsRepository.findFollowersWithCursor(userId, cursor, Constants.PAGE_SIZE);
	    List<Long> userIds = projections.stream().map(UserFollowProjection::getUserId).collect(Collectors.toList());
	    List<UserProfileResponseProfileGetterDTO> profiles = userProfileService.getUserProfilesByIds(userIds);

	    LocalDateTime nextCursor = projections.size() == Constants.PAGE_SIZE
	        ? projections.get(projections.size() - 1).getDateFollowed()
	        : null;

	    return new PagedResponse<>(profiles, nextCursor);
	}


	public Set<Long> getFollowedUsers(Long userId) {
	    return userFollowsRepository.findFollowedUsersByUserId(userId);
	}


}
