package com.like.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.like.constant.Constants;
import com.like.dto.LikeDTO;
import com.like.dto.LikeResponseDTO;
import com.like.entity.Like;
import com.like.repository.LikeRepository;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;

@Service
public class LikeService {

	@Autowired
	private LikeRepository likeRepository;

	@Autowired
	private RedisTemplate<String, Long> redisTemplate;

	private static final String REDIS_KEY_PREFIX = "likes:";

	// Initialize Redis with data on application startup
	@PostConstruct
	public void initializeLikesCache() {
	    List<Object[]> likesData = likeRepository.countLikesBySpotifyId();
	    for (Object[] row : likesData) {
	        // Validate and cast row[0] (spotifyId) and row[1] (count)
	        if (row[0] instanceof String && row[1] instanceof Long) {
	            String spotifyId = (String) row[0];
	            Long count = (Long) row[1];
	            redisTemplate.opsForValue().set(REDIS_KEY_PREFIX + spotifyId, count);
	        } else {
	            throw new ClassCastException("Unexpected types: spotifyId=" + row[0].getClass() + ", count=" + row[1].getClass());
	        }
	    }
	}

	// Increment the likes count in Redis when a new like is added
	public void incrementLikesCount(String spotifyId) {
	    String key = REDIS_KEY_PREFIX + spotifyId;
	    redisTemplate.opsForValue().increment(key, 1);
	}

	public long getLikesCountBySpotifyId(String spotifyId) {
	    String key = REDIS_KEY_PREFIX + spotifyId;
	    Long count = redisTemplate.opsForValue().get(key);

	    if (count == null) {
	        // Fallback to the database if the key is not present in Redis
	        count = likeRepository.countBySpotifyId(spotifyId);
	        redisTemplate.opsForValue().set(key, count);
	    }

	    return count;
	}

	// Mapping method to convert Entity to Response DTO
	private LikeResponseDTO mapToResponseDTO(Like like) {
		LikeResponseDTO dto = new LikeResponseDTO();
		dto.setUserId(like.getUserId());
		dto.setSpotifyId(like.getSpotifyId());
		dto.setCreatedAt(like.getCreatedAt().toString());
		return dto;
	}

	public LikeResponseDTO addLike(LikeDTO likeDTO) {
		Optional<Like> existingLike = likeRepository.findBySpotifyIdAndUserId(likeDTO.getSpotifyId(),
				likeDTO.getUserId());
		if (existingLike.isPresent()) {
			throw new IllegalArgumentException("User already liked this content.");
		}

		Like like = new Like();
		like.setUserId(likeDTO.getUserId());
		like.setSpotifyId(likeDTO.getSpotifyId());
		Like savedLike = likeRepository.save(like);

		incrementLikesCount(likeDTO.getSpotifyId()); // Increment Redis count
		return mapToResponseDTO(savedLike);
	}

	@Transactional
	public void removeLike(Long userId, String spotifyId) {
	    Optional<Like> existingLike = likeRepository.findBySpotifyIdAndUserId(spotifyId, userId);
	    if (existingLike.isEmpty()) {
	        throw new IllegalArgumentException("Like does not exist."); // You can replace this with a silent return or custom response
	    }

	    // Remove the like from the database
	    likeRepository.deleteBySpotifyIdAndUserId(spotifyId, userId);

	    // Safely decrement the Redis count
	    decrementLikesCountSafely(spotifyId);
	}
	
	private void decrementLikesCountSafely(String spotifyId) {
	    String key = REDIS_KEY_PREFIX + spotifyId;
	    Long currentCount = redisTemplate.opsForValue().get(key);

	    if (currentCount == null || currentCount <= 0) {
	        // If no count exists or it's already 0, explicitly set the count to 0 in Redis
	        redisTemplate.opsForValue().set(key, 0L);
	        return;
	    }

	    // Decrement the count in Redis
	    Long updatedCount = redisTemplate.opsForValue().decrement(key, 1);

	    // If the updated count becomes 0, ensure it's stored as 0 in Redis
	    if (updatedCount != null && updatedCount <= 0) {
	        redisTemplate.opsForValue().set(key, 0L);
	    }
	}

	public Page<LikeResponseDTO> getLikesByUserId(Long userId, int page) {
		return likeRepository
				.findByUserId(userId,
						PageRequest.of(page, Constants.PAGE_SIZE, Sort.by(Sort.Direction.DESC, "createdAt")))
				.map(this::mapToResponseDTO);
	}

	public Optional<LikeResponseDTO> getLikeBySpotifyIdAndUserId(String spotifyId, Long userId) {
		return likeRepository.findBySpotifyIdAndUserId(spotifyId, userId).map(this::mapToResponseDTO);
	}

}