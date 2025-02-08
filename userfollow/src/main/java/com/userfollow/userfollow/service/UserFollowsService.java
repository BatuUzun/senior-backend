package com.userfollow.userfollow.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.userfollow.userfollow.entity.UserFollow;
import com.userfollow.userfollow.repository.UserFollowsRepository;

import jakarta.annotation.PostConstruct;

@Service
public class UserFollowsService {

    @Autowired
    private UserFollowsRepository userFollowsRepository;

    @Autowired
    private RedisTemplate<String, Long> redisTemplate;

    private static final String FOLLOWER_COUNT_KEY = "followerCount:";
    private static final String FOLLOWING_COUNT_KEY = "followingCount:";

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

        decrementRedisCount(FOLLOWER_COUNT_KEY + followedId);
        decrementRedisCount(FOLLOWING_COUNT_KEY + followerId);

        return "Successfully unfollowed the user.";
    }

    public boolean isFollowing(Long followerId, Long followedId) {
        return userFollowsRepository.existsByFollowerIdAndFollowedId(followerId, followedId);
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



}

