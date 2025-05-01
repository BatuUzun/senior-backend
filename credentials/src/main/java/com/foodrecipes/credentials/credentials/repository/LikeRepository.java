package com.foodrecipes.credentials.credentials.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.foodrecipes.credentials.credentials.entity.Like;

public interface LikeRepository extends JpaRepository<Like, Long> {

    Page<Like> findByUserId(Long userId, Pageable pageable);

    long countBySpotifyId(String spotifyId);

    Optional<Like> findBySpotifyIdAndUserIdAndType(String spotifyId, Long userId, String type);

    void deleteBySpotifyIdAndUserIdAndType(String spotifyId, Long userId, String type);
    
    @Query("SELECT l.spotifyId, COUNT(l) FROM Like l GROUP BY l.spotifyId")
    List<Object[]> countLikesBySpotifyId();
    
    Optional<Like> findBySpotifyIdAndUserId(String spotifyId, Long userId);
    
    Page<Like> findByUserIdAndType(Long userId, String type, Pageable pageable);
    
    List<Like> findByUserIdIn(List<Long> userIds);

    List<Like> findTop20ByUserIdInAndCreatedAtBeforeOrderByCreatedAtDesc(List<Long> userIds, LocalDateTime cursor);

    List<Like> findTop11ByUserIdInAndCreatedAtBeforeOrderByCreatedAtDesc(List<Long> userIds, LocalDateTime createdAt);

    void deleteByUserId(Long userId);

}
