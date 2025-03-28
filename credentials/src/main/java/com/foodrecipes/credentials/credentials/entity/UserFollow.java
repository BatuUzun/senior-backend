package com.foodrecipes.credentials.credentials.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "user_follows")
public class UserFollow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "follower_id", nullable = false)
    private Long followerId;

    @Column(name = "followed_id", nullable = false)
    private Long followedId;

    @Column(name = "date_followed", nullable = false)
    private LocalDateTime dateFollowed = LocalDateTime.now();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getFollowerId() { return followerId; }
    public void setFollowerId(Long followerId) { this.followerId = followerId; }

    public Long getFollowedId() { return followedId; }
    public void setFollowedId(Long followedId) { this.followedId = followedId; }

    public LocalDateTime getDateFollowed() { return dateFollowed; }
    public void setDateFollowed(LocalDateTime dateFollowed) { this.dateFollowed = dateFollowed; }

    public UserFollow(Long id, Long followerId, Long followedId, LocalDateTime dateFollowed) {
        this.id = id;
        this.followerId = followerId;
        this.followedId = followedId;
        this.dateFollowed = dateFollowed;
    }

    public UserFollow() {}
}
