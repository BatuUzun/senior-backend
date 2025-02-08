package com.like.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.like.entity.Like;

public interface LikeRepository extends JpaRepository<Like, Long> {

    Page<Like> findByUserId(Long userId, Pageable pageable);

    long countBySpotifyId(String spotifyId);

    Optional<Like> findBySpotifyIdAndUserIdAndType(String spotifyId, Long userId, String type);

    void deleteBySpotifyIdAndUserIdAndType(String spotifyId, Long userId, String type);
    
    @Query("SELECT l.spotifyId, COUNT(l) FROM Like l GROUP BY l.spotifyId")
    List<Object[]> countLikesBySpotifyId();
    
    Optional<Like> findBySpotifyIdAndUserId(String spotifyId, Long userId);
    
    Page<Like> findByUserIdAndType(Long userId, String type, Pageable pageable);

}
