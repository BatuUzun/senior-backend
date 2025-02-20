package com.like.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.like.dto.LikeByUserDTO;
import com.like.dto.LikeDTO;
import com.like.dto.LikeResponseDTO;
import com.like.dto.LikesBySpotifyIdDTO;
import com.like.dto.RemoveLikeDTO;
import com.like.service.LikeService;

/**
 * Controller for handling "Like" operations.
 * Provides APIs for adding, removing, retrieving, and counting likes.
 */
@RestController
@RequestMapping("/like")
public class LikeController {

    @Autowired
    private LikeService likeService;

    /**
     * Adds a like to an item.
     *
     * @param likeDTO DTO containing user ID, Spotify ID, and type of like.
     * @return ResponseEntity containing the created LikeResponseDTO if successful,
     *         or a BadRequest response in case of invalid input.
     */
    @PostMapping("/add-like")
    public ResponseEntity<?> addLike(@RequestBody LikeDTO likeDTO) {
        try {
            LikeResponseDTO like = likeService.addLike(likeDTO);
            return ResponseEntity.ok(like);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Removes a like from an item.
     *
     * @param removeLikeDTO DTO containing user ID, Spotify ID, and type of like.
     * @return ResponseEntity with no content if the like is successfully removed.
     */
    @DeleteMapping("/remove-like")
    public ResponseEntity<Void> removeLike(@RequestBody RemoveLikeDTO removeLikeDTO) {
        likeService.removeLike(removeLikeDTO.getUserId(), removeLikeDTO.getSpotifyId(), removeLikeDTO.getType());
        return ResponseEntity.noContent().build();
    }

    /**
     * Retrieves paginated likes by a specific user.
     *
     * @param userId The ID of the user whose likes are being retrieved.
     * @param page The page number for pagination (default: 0).
     * @return ResponseEntity containing a Page of LikeResponseDTO.
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<LikeResponseDTO>> getLikesByUserId(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page) {
        Page<LikeResponseDTO> likes = likeService.getLikesByUserId(userId, page);
        return ResponseEntity.ok(likes);
    }

    /**
     * Retrieves the count of likes for a specific Spotify ID.
     *
     * @param request DTO containing the Spotify ID.
     * @return ResponseEntity containing the total count of likes.
     */
    @PostMapping("/count")
    public ResponseEntity<Long> getLikesCountBySpotifyId(@RequestBody LikesBySpotifyIdDTO request) {
        long count = likeService.getLikesCountBySpotifyId(request.getSpotifyId());
        return ResponseEntity.ok(count);
    }

    /**
     * Checks if a user has liked a specific item.
     *
     * @param request DTO containing the Spotify ID and user ID.
     * @return ResponseEntity containing an Optional of LikeResponseDTO.
     */
    @PostMapping("/is-liked-by-user")
    public ResponseEntity<Optional<LikeResponseDTO>> getLikeBySpotifyIdAndUserId(
            @RequestBody LikeByUserDTO request) {
        Optional<LikeResponseDTO> like = likeService.getLikeBySpotifyIdAndUserId(request.getSpotifyId(), request.getUserId());
        return ResponseEntity.ok(like);
    }

    /**
     * Retrieves paginated likes by a user and type.
     *
     * @param userId The ID of the user.
     * @param type The type of like (e.g., "song", "playlist").
     * @param page The page number for pagination (default: 0).
     * @return ResponseEntity containing a Page of LikeResponseDTO.
     */
    @GetMapping("/user/{userId}/type")
    public ResponseEntity<Page<LikeResponseDTO>> getLikesByUserIdAndType(
            @PathVariable Long userId,
            @RequestParam String type,
            @RequestParam(defaultValue = "0") int page) {
        Page<LikeResponseDTO> likes = likeService.getLikesByUserIdAndType(userId, type, page);
        return ResponseEntity.ok(likes);
    }
}
