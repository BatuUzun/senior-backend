package com.foodrecipes.credentials.credentials.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.foodrecipes.credentials.credentials.constants.Constants;
import com.foodrecipes.credentials.credentials.dto.UserFollowProjection;
import com.foodrecipes.credentials.credentials.entity.UserFollow;

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
    @Query(value = "SELECT follower_id AS userId, date_followed FROM user_follows " +
            "WHERE followed_id = :userId AND date_followed > :cursor " +
            "ORDER BY date_followed ASC LIMIT :limit", nativeQuery = true)
    List<UserFollowProjection> findFollowersWithCursor(
        @Param("userId") Long userId,
        @Param("cursor") LocalDateTime cursor,
        @Param("limit") int limit
    );
    @Query("SELECT u.followerId AS userId, u.dateFollowed AS dateFollowed " +
    	       "FROM UserFollow u WHERE u.followedId = :userId " +
    	       "ORDER BY u.dateFollowed DESC")
    	Page<UserFollowProjection> findFollowersByUserId(
    	    @Param("userId") Long userId,
    	    Pageable pageable
    	);

    @Query("SELECT u.followedId AS userId, u.dateFollowed AS dateFollowed " +
    	       "FROM UserFollow u WHERE u.followerId = :userId AND u.dateFollowed > :cursor " +
    	       "ORDER BY u.dateFollowed ASC")
    	List<UserFollowProjection> findFollowingsWithCursor(
    	    @Param("userId") Long userId,
    	    @Param("cursor") LocalDateTime cursor,
    	    Pageable pageable
    	);

    @Query("SELECT u.followedId AS userId, u.dateFollowed AS dateFollowed " +
    	       "FROM UserFollow u WHERE u.followerId = :userId " +
    	       "ORDER BY u.dateFollowed DESC")
    	Page<UserFollowProjection> findFollowingsByUserId(
    	    @Param("userId") Long userId,
    	    Pageable pageable
    	);


    void deleteByFollowerIdOrFollowedId(Long followerId, Long followedId);



    @Query("SELECT uf.followedId FROM UserFollow uf WHERE uf.followerId = :userId")
    Set<Long> findFollowedUsersByUserId(@Param("userId") Long userId);
}