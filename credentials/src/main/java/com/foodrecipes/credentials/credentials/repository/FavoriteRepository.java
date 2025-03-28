package com.foodrecipes.credentials.credentials.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.foodrecipes.credentials.credentials.constants.Constants;
import com.foodrecipes.credentials.credentials.entity.Favorite;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    Page<Favorite> findByUserId(Long userId, Pageable pageable);

    Optional<Favorite> findByUserIdAndSpotifyId(Long userId, String spotifyId);
    
    void deleteById(Long id);

    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END FROM Favorite f WHERE f.userId = :userId AND f.spotifyId = :spotifyId")
    boolean existsByUserIdAndSpotifyId(Long userId, String spotifyId);

    // New query to get favorites by type
    Page<Favorite> findByUserIdAndType(Long userId, String type, Pageable pageable);
    
 // Fetch latest 4 favorites for a given user ID and type
    @Query("SELECT f FROM Favorite f WHERE f.userId = :userId AND f.type = :type ORDER BY f.createdAt DESC LIMIT "+Constants.PAGE_SIZE_PROFILE)
    List<Favorite> findTop4ByUserIdAndType(@Param("userId") Long userId, @Param("type") String type, Pageable pageable);
    
    List<Favorite> findByUserIdAndType(Long userId, String type);
}
