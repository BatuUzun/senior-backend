package com.favorite.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.favorite.dto.FavoriteDTO;
import com.favorite.dto.FavoritePaginatedResponseDTO;
import com.favorite.dto.FavoriteProfileResponseDTO;
import com.favorite.dto.FavoriteResponseDTO;
import com.favorite.service.FavoriteService;

/**
 * Controller for managing favorite items.
 * Provides APIs to add, remove, retrieve, and check favorite status.
 */
@RestController
@RequestMapping("/favorite")
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    /**
     * Adds an item to the user's favorites.
     *
     * @param favoriteDTO DTO containing user ID and favorite item details.
     * @return ResponseEntity containing the created favorite response if successful,
     *         or a conflict response if the item is already favorited.
     */
    @PostMapping("/add-favorite")
    public ResponseEntity<?> addFavorite(@RequestBody FavoriteDTO favoriteDTO) {
        try {
            FavoriteResponseDTO favorite = favoriteService.addFavorite(favoriteDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(favorite);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }
    
    @PutMapping("/replace-favorite/{spotifyId}")
    public ResponseEntity<?> replaceFavorite(
        @PathVariable String spotifyId, // Accept Spotify ID as a String
        @RequestBody FavoriteDTO favoriteDTO) {
        try {
            FavoriteResponseDTO favorite = favoriteService.replaceFavorite(spotifyId, favoriteDTO);
            return ResponseEntity.ok(favorite);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Removes a favorite item by its ID.
     *
     * @param id The ID of the favorite to remove.
     * @return ResponseEntity with a success message if deleted, or NOT_FOUND if the ID does not exist.
     */
    @DeleteMapping("/remove-favorite/{id}")
    public ResponseEntity<?> removeFavoriteById(@PathVariable Long id) {
        try {
            favoriteService.removeFavoriteById(id);
            return ResponseEntity.status(HttpStatus.OK).body("Favorite with ID " + id + " successfully removed.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Retrieves paginated favorites by user ID and type.
     *
     * @param userId The ID of the user.
     * @param type The type of favorite (e.g., "song", "playlist").
     * @param page The page number for pagination (default: 0).
     * @return ResponseEntity containing a Page of FavoriteResponseDTOs, or NOT_FOUND if no favorites exist.
     */
    @GetMapping("/user/{userId}/{type}")
    public ResponseEntity<?> getFavoritesByUserId(
            @PathVariable Long userId,
            @PathVariable String type,
            @RequestParam(defaultValue = "0") int page) {
        Page<FavoriteResponseDTO> favorites = favoriteService.getFavoritesByUserIdAndType(userId, type, page);
        if (favorites.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No favorites found for user ID " + userId);
        }
        return ResponseEntity.ok(new FavoritePaginatedResponseDTO<>(favorites));
    }

    /**
     * Checks if a specific item is favorited by a user.
     *
     * @param userId The ID of the user.
     * @param spotifyId The Spotify ID of the item.
     * @return ResponseEntity with a success message if favorited, or NOT_FOUND if not.
     */
    @GetMapping("/is-favorited")
    public ResponseEntity<?> isFavoritedByUser(@RequestParam Long userId, @RequestParam String spotifyId) {
        boolean isFavorited = favoriteService.isFavoritedByUser(userId, spotifyId);
        if (isFavorited) {
            return ResponseEntity.ok("Spotify ID " + spotifyId + " is favorited by user ID " + userId);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Spotify ID " + spotifyId + " is not favorited by user ID " + userId);
        }
    }

    /**
     * Retrieves the latest favorite items for a user.
     *
     * @param userId The ID of the user.
     * @param type The type of favorite (e.g., "song", "playlist").
     * @return ResponseEntity containing a list of the latest favorite items.
     */
    @GetMapping("/latest-profile")
    public ResponseEntity<List<FavoriteProfileResponseDTO>> getLatestFavorites(
            @RequestParam Long userId,
            @RequestParam String type) {
        
        System.out.println("🔍 API çağrısı yapıldı: /latest-profile userId=" + userId + " type=" + type);
        
        List<FavoriteProfileResponseDTO> favorites = favoriteService.getLatestFavorites(userId, type);

        System.out.println("✅ API verisi döndü: " + favorites.size() + " favori bulundu.");
        
        return ResponseEntity.ok(favorites);
    }
}
