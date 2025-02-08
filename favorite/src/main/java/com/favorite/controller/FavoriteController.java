package com.favorite.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.favorite.dto.FavoriteDTO;
import com.favorite.dto.FavoriteResponseDTO;
import com.favorite.service.FavoriteService;

@RestController
@RequestMapping("/favorite")
public class FavoriteController {

	@Autowired
    private FavoriteService favoriteService;

    // Add favorite
    @PostMapping("/add-favorite")
    public ResponseEntity<?> addFavorite(@RequestBody FavoriteDTO favoriteDTO) {
        try {
            FavoriteResponseDTO favorite = favoriteService.addFavorite(favoriteDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(favorite);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    // Remove favorite by ID
    @DeleteMapping("/remove-favorite/{id}")
    public ResponseEntity<?> removeFavoriteById(@PathVariable Long id) {
        try {
            favoriteService.removeFavoriteById(id);
            return ResponseEntity.status(HttpStatus.OK).body("Favorite with ID " + id + " successfully removed.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // Get favorites by user ID
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getFavoritesByUserId(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page) {
        Page<FavoriteResponseDTO> favorites = favoriteService.getFavoritesByUserId(userId, page);
        if (favorites.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No favorites found for user ID " + userId);
        }
        return ResponseEntity.ok(favorites);
    }

    // Check if favorited by user
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
}