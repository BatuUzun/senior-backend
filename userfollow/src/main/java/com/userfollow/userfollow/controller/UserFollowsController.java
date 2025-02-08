package com.userfollow.userfollow.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.userfollow.userfollow.dto.FollowRequestDTO;
import com.userfollow.userfollow.service.UserFollowsService;

@RestController
@RequestMapping("/user-follow")
public class UserFollowsController {

    @Autowired
    private UserFollowsService userFollowsService;

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

    @DeleteMapping("/unfollow")
    public ResponseEntity<String> unfollowUser(@RequestBody FollowRequestDTO request) {
        String response = userFollowsService.unfollowUser(request.getFollowerId(), request.getFollowedId());

        if (response.equals("You are not following this user.")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/is-following")
    public ResponseEntity<Boolean> isFollowing(
            @RequestParam Long followerId,
            @RequestParam Long followedId) {
        boolean isFollowing = userFollowsService.isFollowing(followerId, followedId);
        return ResponseEntity.ok(isFollowing);
    }

    @GetMapping("/follower-count")
    public ResponseEntity<Long> getFollowerCount(@RequestParam Long userProfileId) {
        long count = userFollowsService.getFollowerCount(userProfileId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/following-count")
    public ResponseEntity<Long> getFollowingCount(@RequestParam Long userProfileId) {
        long count = userFollowsService.getFollowingCount(userProfileId);
        return ResponseEntity.ok(count);
    }
}