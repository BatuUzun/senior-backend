package com.userfollow.userfollow.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.userfollow.userfollow.dto.UserProfileResponseDTO;
import com.userfollow.userfollow.entity.UserFollow;
import com.userfollow.userfollow.exception.ProxyServiceException;
import com.userfollow.userfollow.proxy.ProfileGetterProxy;
import com.userfollow.userfollow.repository.UserFollowsRepository;

import feign.FeignException;
import jakarta.annotation.PostConstruct;

@Service
public class UserFollowsService {

	@Autowired
	private UserFollowsRepository userFollowsRepository;

	@Autowired
	private RedisTemplate<String, Long> redisTemplate;

	@Autowired
	private ProfileGetterProxy profileapiProxy;

	private static final String FOLLOWER_COUNT_KEY = "followerCount:";
	private static final String FOLLOWING_COUNT_KEY = "followingCount:";
	
	@Autowired
    private StringRedisTemplate redisTemplateFollower;

    private static final String FOLLOW_KEY_PREFIX = "user:following:";

	@PostConstruct
	public void initializeRedisCounts() {
		List<Long> allUserIds = userFollowsRepository.findAllUserIds();

		for (Long userId : allUserIds) {
			// Initialize follower count
			String followerKey = FOLLOWER_COUNT_KEY + userId;
			if (redisTemplate.opsForValue().get(followerKey) == null) {
				long followerCount = userFollowsRepository.countByFollowedId(userId);
				redisTemplate.opsForValue().set(followerKey, followerCount);
			}

			// Initialize following count
			String followingKey = FOLLOWING_COUNT_KEY + userId;
			if (redisTemplate.opsForValue().get(followingKey) == null) {
				long followingCount = userFollowsRepository.countByFollowerId(userId);
				redisTemplate.opsForValue().set(followingKey, followingCount);
			}
		}
	}
	
	@PostConstruct
    public void initializeRedisWithFollowData() {
        System.out.println("Initializing Redis with follow relationships...");

        List<UserFollow> allFollows = userFollowsRepository.findAll();
        for (UserFollow follow : allFollows) {
            String key = FOLLOW_KEY_PREFIX + follow.getFollowerId();
            redisTemplateFollower.opsForSet().add(key, follow.getFollowedId().toString());
        }

        System.out.println("Redis initialization completed.");
    }

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
		
		redisTemplateFollower.opsForSet().add(FOLLOW_KEY_PREFIX + followerId, String.valueOf(followedId));


		incrementRedisCount(FOLLOWER_COUNT_KEY + followedId);
		incrementRedisCount(FOLLOWING_COUNT_KEY + followerId);

		return "Successfully followed the user.";
	}

	@Transactional
	public String unfollowUser(Long followerId, Long followedId) {
		boolean alreadyFollowing = userFollowsRepository.existsByFollowerIdAndFollowedId(followerId, followedId);
		if (!alreadyFollowing) {
			return "You are not following this user.";
		}

		userFollowsRepository.deleteByFollowerIdAndFollowedId(followerId, followedId);
	    redisTemplateFollower.opsForSet().remove(FOLLOW_KEY_PREFIX + followerId, String.valueOf(followedId));

		decrementRedisCount(FOLLOWER_COUNT_KEY + followedId);
		decrementRedisCount(FOLLOWING_COUNT_KEY + followerId);

		return "Successfully unfollowed the user.";
	}

	public boolean isFollowing(Long followerId, Long followedId) {
	    // Check Redis first
	    Boolean existsInRedis = redisTemplateFollower.opsForSet().isMember(FOLLOW_KEY_PREFIX + followerId, String.valueOf(followedId));

	    if (existsInRedis != null && existsInRedis) {
	        return true; // User is following (cached in Redis)
	    }

	    // Fallback to MySQL (if Redis doesn't have the data)
	    boolean existsInDB = userFollowsRepository.existsByFollowerIdAndFollowedId(followerId, followedId);

	    // If found in MySQL, cache it in Redis for future checks
	    if (existsInDB) {
	        redisTemplateFollower.opsForSet().add(FOLLOW_KEY_PREFIX + followerId, String.valueOf(followedId));
	    }

	    return existsInDB;
	}


	public long getFollowerCount(Long userProfileId) {
		String redisKey = FOLLOWER_COUNT_KEY + userProfileId;
		Long count = redisTemplate.opsForValue().get(redisKey);

		if (count == null) {
			count = userFollowsRepository.countByFollowedId(userProfileId);
			redisTemplate.opsForValue().set(redisKey, count);
		}
		return count != null ? count : 0L; // Ensure a non-null value is returned
	}

	public long getFollowingCount(Long userProfileId) {
		String redisKey = FOLLOWING_COUNT_KEY + userProfileId;
		Long count = redisTemplate.opsForValue().get(redisKey);

		if (count == null) {
			count = userFollowsRepository.countByFollowerId(userProfileId);
			redisTemplate.opsForValue().set(redisKey, count);
		}
		return count != null ? count : 0L; // Ensure a non-null value is returned
	}

	private void incrementRedisCount(String key) {
		Long currentValue = redisTemplate.opsForValue().get(key);

		// If the key does not exist, initialize it to 0
		if (currentValue == null) {
			redisTemplate.opsForValue().set(key, 0L);
		}

		// Increment the value
		redisTemplate.opsForValue().increment(key, 1L);
	}

	private void decrementRedisCount(String key) {
		Long currentValue = redisTemplate.opsForValue().get(key);

		// If the key does not exist, initialize it to 0
		if (currentValue == null) {
			redisTemplate.opsForValue().set(key, 0L);
		}

		// Decrement the value
		redisTemplate.opsForValue().increment(key, -1L);
	}

	public List<UserProfileResponseDTO> getFollowings(Long userId, LocalDateTime cursor) {
		if (cursor == null) {
			cursor = LocalDateTime.of(2000, 1, 1, 0, 0); // Default cursor (fetch from start)
		}

		List<Long> followedIds = userFollowsRepository.findFollowingsByUserId(userId, cursor);
		try {
			return profileapiProxy.getUserProfiles(followedIds).getBody();
		}catch(FeignException e) {
			throw new ProxyServiceException("Profile API is currently unavailable. Please try again later.");
		}
	}

	public List<UserProfileResponseDTO> getFollowers(Long userId, LocalDateTime cursor) {
		if (cursor == null) {
			cursor = LocalDateTime.of(2000, 1, 1, 0, 0);
		}

		List<Long> followerIds = userFollowsRepository.findFollowersByUserId(userId, cursor);
		try {
			return profileapiProxy.getUserProfiles(followerIds).getBody();
		}
		catch(FeignException e) {

			throw new ProxyServiceException("Profile API is currently unavailable. Please try again later.");
		}
	}
	
	public Set<Long> getFollowedUsers(Long userId) {
	    Set<String> followedUsers = redisTemplateFollower.opsForSet().members(FOLLOW_KEY_PREFIX + userId);
	    
	    return followedUsers.stream()
	                        .map(Long::valueOf)  // Convert each String to Long
	                        .collect(Collectors.toSet());
	}


}
