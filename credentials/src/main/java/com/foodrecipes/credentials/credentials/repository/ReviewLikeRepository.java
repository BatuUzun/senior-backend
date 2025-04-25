package com.foodrecipes.credentials.credentials.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.foodrecipes.credentials.credentials.entity.ReviewLike;

@Repository
public interface ReviewLikeRepository extends JpaRepository<ReviewLike, Long> {

    @Query("""
            SELECT r.spotifyId, r.id, COUNT(rl.id) 
            FROM Review r
            JOIN ReviewLike rl ON r.id = rl.review.id
            GROUP BY r.spotifyId, r.id
        """)
        List<Object[]> findLikeCountsBySpotifyId();
        
        List<ReviewLike> findByUserIdIn(List<Long> userIds);

        List<ReviewLike> findTop20ByUserIdInAndCreatedAtBeforeOrderByCreatedAtDesc(List<Long> userIds, LocalDateTime cursor);

        List<ReviewLike> findTop11ByUserIdInAndCreatedAtLessThanEqualOrderByCreatedAtDesc(List<Long> userIds, LocalDateTime cursor);

}
