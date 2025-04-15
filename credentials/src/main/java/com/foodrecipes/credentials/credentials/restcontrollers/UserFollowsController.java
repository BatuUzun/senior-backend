package com.foodrecipes.credentials.credentials.restcontrollers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.foodrecipes.credentials.credentials.dto.FollowRequestDTO;
import com.foodrecipes.credentials.credentials.dto.UserProfileResponseProfileGetterDTO;
import com.foodrecipes.credentials.credentials.service.UserFollowsService;

/**
 * Controller for handling user follow/unfollow actions. Provides APIs to
 * follow/unfollow users, check follow status, retrieve followers and
 * followings, and count relationships.
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
	 * @return ResponseEntity with success message or an error message if the action
	 *         is not allowed.
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
	 * @return ResponseEntity with success message or NOT_FOUND if the user is not
	 *         currently followed.
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
	public ResponseEntity<Boolean> isFollowing(@RequestParam Long followerId, @RequestParam Long followedId) {
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
	public ResponseEntity<?> getFollowings(@RequestParam Long userId,
	                                       @RequestParam(required = false) LocalDateTime cursor) {
	    return ResponseEntity.ok(userFollowsService.getFollowings(userId, cursor));
	}


	/**
	 * Retrieves a list of followers for a user.
	 *
	 * @param userId The user ID.
	 * @param cursor The cursor for pagination (optional).
	 * @return ResponseEntity containing a list of follower user profiles.
	 */
	@GetMapping("/followers")
	public ResponseEntity<?> getFollowers(@RequestParam Long userId,
	                                      @RequestParam(required = false) LocalDateTime cursor) {
	    return ResponseEntity.ok(userFollowsService.getFollowers(userId, cursor));
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
