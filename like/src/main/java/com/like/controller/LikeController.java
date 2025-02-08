package com.like.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.like.dto.LikeDTO;
import com.like.dto.LikeResponseDTO;
import com.like.service.LikeService;

@RestController
@RequestMapping("/like")
public class LikeController {

	@Autowired
    private LikeService likeService;

	@PostMapping("/add-like")
	public ResponseEntity<?> addLike(@RequestBody LikeDTO likeDTO) {
	    try {
	        LikeResponseDTO like = likeService.addLike(likeDTO);
	        return ResponseEntity.ok(like);
	    } catch (IllegalArgumentException e) {
	        return ResponseEntity.badRequest().body(e.getMessage());
	    }
	}

    @DeleteMapping("/remove-like")
    public ResponseEntity<Void> removeLike(@RequestParam Long userId, @RequestParam String spotifyId) {
        likeService.removeLike(userId, spotifyId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<LikeResponseDTO>> getLikesByUserId(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page) {
        Page<LikeResponseDTO> likes = likeService.getLikesByUserId(userId, page);
        return ResponseEntity.ok(likes);
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getLikesCountBySpotifyId(@RequestParam String spotifyId) {
        long count = likeService.getLikesCountBySpotifyId(spotifyId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/is-liked-by-user")
    public ResponseEntity<Optional<LikeResponseDTO>> getLikeBySpotifyIdAndUserId(
            @RequestParam String spotifyId,
            @RequestParam Long userId) {
        Optional<LikeResponseDTO> like = likeService.getLikeBySpotifyIdAndUserId(spotifyId, userId);
        return ResponseEntity.ok(like);
    }
}