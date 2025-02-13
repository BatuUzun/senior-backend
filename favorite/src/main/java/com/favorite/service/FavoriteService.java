package com.favorite.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.favorite.constant.Constants;
import com.favorite.dto.FavoriteDTO;
import com.favorite.dto.FavoriteProfileResponseDTO;
import com.favorite.dto.FavoriteResponseDTO;
import com.favorite.entity.Favorite;
import com.favorite.repository.FavoriteRepository;

import jakarta.transaction.Transactional;

@Service
public class FavoriteService {

    @Autowired
    private FavoriteRepository favoriteRepository;

    public FavoriteResponseDTO addFavorite(FavoriteDTO favoriteDTO) {
        if (favoriteRepository.existsByUserIdAndSpotifyId(favoriteDTO.getUserId(), favoriteDTO.getSpotifyId())) {
            throw new IllegalArgumentException("User already favorited Spotify ID: " + favoriteDTO.getSpotifyId());
        }

        Favorite favorite = new Favorite();
        favorite.setUserId(favoriteDTO.getUserId());
        favorite.setSpotifyId(favoriteDTO.getSpotifyId());
        favorite.setType(favoriteDTO.getType()); // Set new field
        Favorite savedFavorite = favoriteRepository.save(favorite);

        return mapToResponseDTO(savedFavorite);
    }

    @Transactional
    public void removeFavoriteById(Long id) {
        if (favoriteRepository.findById(id).isEmpty()) {
            throw new IllegalArgumentException("Favorite with ID " + id + " does not exist.");
        }
        favoriteRepository.deleteById(id);
    }

    public Page<FavoriteResponseDTO> getFavoritesByUserId(Long userId, int page) {
        return favoriteRepository
                .findByUserId(userId, PageRequest.of(page, Constants.PAGE_SIZE, Sort.by(Sort.Direction.DESC, "createdAt")))
                .map(this::mapToResponseDTO);
    }

    public Page<FavoriteResponseDTO> getFavoritesByUserIdAndType(Long userId, String type, int page) {
        return favoriteRepository
                .findByUserIdAndType(userId, type, PageRequest.of(page, Constants.PAGE_SIZE, Sort.by(Sort.Direction.DESC, "createdAt")))
                .map(this::mapToResponseDTO);
    }

    public boolean isFavoritedByUser(Long userId, String spotifyId) {
        return favoriteRepository.existsByUserIdAndSpotifyId(userId, spotifyId);
    }

    private FavoriteResponseDTO mapToResponseDTO(Favorite favorite) {
        return new FavoriteResponseDTO(
                favorite.getId(),
                favorite.getUserId(),
                favorite.getSpotifyId(),
                favorite.getType(),
                favorite.getCreatedAt()
        );
    }
    
    public List<FavoriteProfileResponseDTO> getLatestFavorites(Long userId, String type) {
        return favoriteRepository.findTop4ByUserIdAndType(userId, type)
                .stream()
                .map(favorite -> new FavoriteProfileResponseDTO(
                        favorite.getId(),
                        favorite.getSpotifyId(),
                        favorite.getType(),
                        favorite.getCreatedAt()))
                .collect(Collectors.toList());
    }
}
