package com.userfollow.userfollow.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.userfollow.userfollow.entity.UserFollow;

@Repository
public interface UserFollowsRepository extends JpaRepository<UserFollow, Long> {
	boolean existsByFollowerIdAndFollowedId(Long followerId, Long followedId);

	void deleteByFollowerIdAndFollowedId(Long followerId, Long followedId);

	long countByFollowerId(Long followerId);

	long countByFollowedId(Long followedId);
	
    @Query("SELECT DISTINCT uf.followerId FROM UserFollow uf UNION SELECT DISTINCT uf.followedId FROM UserFollow uf")
    List<Long> findAllUserIds();


}