package com.favorite.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.favorite.entity.Favorite;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

	Page<Favorite> findByUserId(Long userId, Pageable pageable);

    Optional<Favorite> findByUserIdAndSpotifyId(Long userId, String spotifyId);

    void deleteById(Long id);

    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END FROM Favorite f WHERE f.userId = :userId AND f.spotifyId = :spotifyId")
    boolean existsByUserIdAndSpotifyId(Long userId, String spotifyId);
}