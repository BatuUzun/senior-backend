package com.review.proxy;

import java.util.Set;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-follow")
public interface UserFollowProxy {
	@GetMapping("/user-follow/{userId}/followed")
	Set<Long> getFollowedUsers(@PathVariable Long userId);
}
