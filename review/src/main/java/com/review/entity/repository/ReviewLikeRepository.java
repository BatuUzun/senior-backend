package com.review.entity.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.review.entity.Review;
import com.review.entity.ReviewLike;

@Repository
public interface ReviewLikeRepository extends JpaRepository<ReviewLike, Long> {

    @Query("""
            SELECT r.spotifyId, r.id, COUNT(rl.id) 
            FROM Review r
            JOIN ReviewLike rl ON r.id = rl.review.id
            GROUP BY r.spotifyId, r.id
        """)
        List<Object[]> findLikeCountsBySpotifyId();
}
