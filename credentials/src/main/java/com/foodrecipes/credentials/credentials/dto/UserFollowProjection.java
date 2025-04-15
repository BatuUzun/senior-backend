package com.foodrecipes.credentials.credentials.dto;

import java.time.LocalDateTime;

public interface UserFollowProjection {
    Long getUserId(); // follower or followed
    LocalDateTime getDateFollowed();
}
