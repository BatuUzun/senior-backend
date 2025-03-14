package com.favorite.service;

import java.util.List;
import java.util.Optional;
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
        // Check if the user already has 4 favorites of the same type
        List<Favorite> existingFavorites = favoriteRepository.findByUserIdAndType(
            favoriteDTO.getUserId(), 
            favoriteDTO.getType()
        );

        if (existingFavorites.size() >= 4) {
            throw new IllegalArgumentException("User already has 4 favorites of type: " + favoriteDTO.getType());
        }

        // Check if the favorite already exists
        Optional<Favorite> existingFavorite = favoriteRepository.findByUserIdAndSpotifyId(
            favoriteDTO.getUserId(), 
            favoriteDTO.getSpotifyId()
        );

        if (existingFavorite.isPresent()) {
            throw new IllegalArgumentException("User already favorited Spotify ID: " + favoriteDTO.getSpotifyId());
        }

        // Create and save the new favorite
        Favorite favorite = new Favorite();
        favorite.setUserId(favoriteDTO.getUserId());
        favorite.setSpotifyId(favoriteDTO.getSpotifyId());
        favorite.setType(favoriteDTO.getType());
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
    
    public FavoriteResponseDTO replaceFavorite(String spotifyId, FavoriteDTO favoriteDTO) {
        // Find the existing favorite by spotifyId and userId
        Favorite existingFavorite = favoriteRepository.findByUserIdAndSpotifyId(
            favoriteDTO.getUserId(), spotifyId)
            .orElseThrow(() -> new IllegalArgumentException("Favorite not found"));

        // Update the existing favorite with new data
        existingFavorite.setSpotifyId(favoriteDTO.getSpotifyId());
        existingFavorite.setType(favoriteDTO.getType());
        Favorite updatedFavorite = favoriteRepository.save(existingFavorite);

        return mapToResponseDTO(updatedFavorite);
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
        System.out.println("Fetching latest favorites for user: " + userId + " and type: " + type);
        
        List<Favorite> favorites = favoriteRepository.findTop4ByUserIdAndType(userId, type, PageRequest.of(0, Constants.PAGE_SIZE_PROFILE));
        
        System.out.println("Fetched favorites count: " + favorites.size());

        return favorites.stream()
                .map(favorite -> new FavoriteProfileResponseDTO(
                        favorite.getId(),
                        favorite.getSpotifyId(),
                        favorite.getType(),
                        favorite.getCreatedAt()))
                .collect(Collectors.toList());
    }

}
