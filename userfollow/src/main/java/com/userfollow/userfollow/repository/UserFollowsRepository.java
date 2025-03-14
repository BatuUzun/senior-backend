package com.userfollow.userfollow.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.userfollow.userfollow.constant.Constants;
import com.userfollow.userfollow.entity.UserFollow;

@Repository
public interface UserFollowsRepository extends JpaRepository<UserFollow, Long> {
	boolean existsByFollowerIdAndFollowedId(Long followerId, Long followedId);

	void deleteByFollowerIdAndFollowedId(Long followerId, Long followedId);

	long countByFollowerId(Long followerId);

	long countByFollowedId(Long followedId);
	
    @Query("SELECT DISTINCT uf.followerId FROM UserFollow uf UNION SELECT DISTINCT uf.followedId FROM UserFollow uf")
    List<Long> findAllUserIds();

    // Fetch next 10 followings using cursor-based pagination
    @Query("SELECT u.followedId FROM UserFollow u WHERE u.followerId = :userId AND u.dateFollowed > :cursor ORDER BY u.dateFollowed ASC LIMIT "+Constants.PAGE_SIZE)
    List<Long> findFollowingsByUserId(@Param("userId") Long userId, @Param("cursor") LocalDateTime cursor);

    // Fetch next 10 followers using cursor-based pagination
    @Query("SELECT u.followerId FROM UserFollow u WHERE u.followedId = :userId AND u.dateFollowed > :cursor ORDER BY u.dateFollowed ASC LIMIT "+Constants.PAGE_SIZE)
    List<Long> findFollowersByUserId(@Param("userId") Long userId, @Param("cursor") LocalDateTime cursor);


    @Query("SELECT uf.followedId FROM UserFollow uf WHERE uf.followerId = :userId")
    Set<Long> findFollowedUsersByUserId(@Param("userId") Long userId);
}