package com.userfollow.userfollow.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.userfollow.userfollow.dto.FollowRequestDTO;
import com.userfollow.userfollow.dto.UserProfileResponseDTO;
import com.userfollow.userfollow.exception.ProxyServiceException;
import com.userfollow.userfollow.service.UserFollowsService;

/**
 * Controller for handling user follow/unfollow actions.
 * Provides APIs to follow/unfollow users, check follow status, retrieve followers and followings, and count relationships.
 */
@RestController
@RequestMapping("/user-follow")
public class UserFollowsController {

    @Autowired
    private UserFollowsService userFollowsService;

    /**
     * Allows a user to follow another user.
     *
     * @param request DTO containing followerId and followedId.
     * @return ResponseEntity with success message or an error message if the action is not allowed.
     */
    @PostMapping("/follow")
    public ResponseEntity<String> followUser(@RequestBody FollowRequestDTO request) {
        String response = userFollowsService.followUser(request.getFollowerId(), request.getFollowedId());

        if (response.equals("You are already following this user.")) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        } else if (response.equals("You cannot follow yourself!")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Allows a user to unfollow another user.
     *
     * @param request DTO containing followerId and followedId.
     * @return ResponseEntity with success message or NOT_FOUND if the user is not currently followed.
     */
    @DeleteMapping("/unfollow")
    public ResponseEntity<String> unfollowUser(@RequestBody FollowRequestDTO request) {
        String response = userFollowsService.unfollowUser(request.getFollowerId(), request.getFollowedId());

        if (response.equals("You are not following this user.")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * Checks if a user is following another user.
     *
     * @param followerId The ID of the follower.
     * @param followedId The ID of the followed user.
     * @return ResponseEntity containing a boolean value indicating follow status.
     */
    @GetMapping("/is-following")
    public ResponseEntity<Boolean> isFollowing(
            @RequestParam Long followerId,
            @RequestParam Long followedId) {
        boolean isFollowing = userFollowsService.isFollowing(followerId, followedId);
        return ResponseEntity.ok(isFollowing);
    }

    /**
     * Retrieves the number of followers a user has.
     *
     * @param userProfileId The user profile ID.
     * @return ResponseEntity containing the follower count.
     */
    @GetMapping("/follower-count")
    public ResponseEntity<Long> getFollowerCount(@RequestParam Long userProfileId) {
        long count = userFollowsService.getFollowerCount(userProfileId);
        return ResponseEntity.ok(count);
    }

    /**
     * Retrieves the number of users a user is following.
     *
     * @param userProfileId The user profile ID.
     * @return ResponseEntity containing the following count.
     */
    @GetMapping("/following-count")
    public ResponseEntity<Long> getFollowingCount(@RequestParam Long userProfileId) {
        long count = userFollowsService.getFollowingCount(userProfileId);
        return ResponseEntity.ok(count);
    }

    /**
     * Retrieves a list of users that a user is following.
     *
     * @param userId The user ID.
     * @param cursor The cursor for pagination (optional).
     * @return ResponseEntity containing a list of following user profiles.
     */
    @GetMapping("/followings")
    public ResponseEntity<?> getFollowings(@RequestParam Long userId, @RequestParam(required = false) LocalDateTime cursor) {
        try {
            List<UserProfileResponseDTO> followings = userFollowsService.getFollowings(userId, cursor);
            return ResponseEntity.ok(followings);
        } catch (ProxyServiceException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(e.getMessage());
        }
    }

    /**
     * Retrieves a list of followers for a user.
     *
     * @param userId The user ID.
     * @param cursor The cursor for pagination (optional).
     * @return ResponseEntity containing a list of follower user profiles.
     */
    @GetMapping("/followers")
    public ResponseEntity<?> getFollowers(@RequestParam Long userId, @RequestParam(required = false) LocalDateTime cursor) {
        try {
            List<UserProfileResponseDTO> followers = userFollowsService.getFollowers(userId, cursor);
            return ResponseEntity.ok(followers);
        } catch (ProxyServiceException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(e.getMessage());
        }
    }

    /**
     * Retrieves a list of user IDs that a specific user is following.
     *
     * @param userId The user ID.
     * @return ResponseEntity containing a set of followed user IDs.
     */
    @GetMapping("/{userId}/followed")
    public ResponseEntity<Set<Long>> getFollowedUsers(@PathVariable Long userId) {
        return ResponseEntity.ok(userFollowsService.getFollowedUsers(userId));
    }
}
