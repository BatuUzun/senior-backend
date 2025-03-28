package com.foodrecipes.credentials.credentials.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.foodrecipes.credentials.credentials.constants.Constants;
import com.foodrecipes.credentials.credentials.dto.LikeDTO;
import com.foodrecipes.credentials.credentials.dto.LikeResponseDTO;
import com.foodrecipes.credentials.credentials.entity.Like;
import com.foodrecipes.credentials.credentials.repository.LikeRepository;

import jakarta.transaction.Transactional;

@Service
public class LikeService {

	@Autowired
	private LikeRepository likeRepository;


	public long getLikesCountBySpotifyId(String spotifyId) {

		long count = likeRepository.countBySpotifyId(spotifyId);
		

		return count;
	}

	// Mapping method to convert Entity to Response DTO
	private LikeResponseDTO mapToResponseDTO(Like like) {
		LikeResponseDTO dto = new LikeResponseDTO();
		dto.setUserId(like.getUserId());
		dto.setSpotifyId(like.getSpotifyId());
		dto.setCreatedAt(like.getCreatedAt().toString());
		dto.setType(like.getType());
		return dto;
	}

	public LikeResponseDTO addLike(LikeDTO likeDTO) {
		Optional<Like> existingLike = likeRepository.findBySpotifyIdAndUserIdAndType(likeDTO.getSpotifyId(),
				likeDTO.getUserId(), likeDTO.getType());

		if (existingLike.isPresent()) {
			throw new IllegalArgumentException("User already liked this content.");
		}

		Like like = new Like();
		like.setUserId(likeDTO.getUserId());
		like.setSpotifyId(likeDTO.getSpotifyId());
		like.setType(likeDTO.getType());

		try {
			Like savedLike = likeRepository.save(like);

			return mapToResponseDTO(savedLike);

		} catch (Exception e) {
			return null;
		}

	}

	@Transactional
	public void removeLike(Long userId, String spotifyId, String type) {
		Optional<Like> existingLike = likeRepository.findBySpotifyIdAndUserIdAndType(spotifyId, userId, type);
		if (existingLike.isEmpty()) {
			throw new IllegalArgumentException("Like does not exist.");
		}

		try {
			likeRepository.deleteBySpotifyIdAndUserIdAndType(spotifyId, userId, type);
		} catch (Exception e) {

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

	public Page<LikeResponseDTO> getLikesByUserIdAndType(Long userId, String type, int page) {
		return likeRepository
				.findByUserIdAndType(userId, type,
						PageRequest.of(page, Constants.PAGE_SIZE, Sort.by(Sort.Direction.DESC, "createdAt")))
				.map(this::mapToResponseDTO);
	}

}