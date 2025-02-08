package com.userfollow.userfollow.dto;

public class FollowRequestDTO {
    private Long followerId;
    private Long followedId;
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
	public FollowRequestDTO(Long followerId, Long followedId) {
		super();
		this.followerId = followerId;
		this.followedId = followedId;
	}
	public FollowRequestDTO() {
		super();
	}
    
    
}