package com.foodrecipes.profileapi.dto;

public class FollowRequest {
	private Long followerId;
	private Long followedId;

	// Getters and setters
	public Long getFollowerId() {
		return followerId;
	}

	public void setFollowerId(Long followerId) {
		this.followerId = followerId;
	}

	public Long getFollowedId() {
		return followedId;
	}

	public void setFollowedId(Long followedId) {
		this.followedId = followedId;
	}

	public FollowRequest(Long followerId, Long followedId) {
		super();
		this.followerId = followerId;
		this.followedId = followedId;
	}

	public FollowRequest() {
		super();
	}
	
}