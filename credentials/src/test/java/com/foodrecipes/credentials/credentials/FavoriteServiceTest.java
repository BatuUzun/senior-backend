package com.foodrecipes.credentials.credentials;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.foodrecipes.credentials.credentials.dto.FavoriteDTO;
import com.foodrecipes.credentials.credentials.dto.FavoriteResponseDTO;
import com.foodrecipes.credentials.credentials.entity.Favorite;
import com.foodrecipes.credentials.credentials.repository.FavoriteRepository;
import com.foodrecipes.credentials.credentials.service.FavoriteService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;

class FavoriteServiceTest {

    @Mock
    private FavoriteRepository favoriteRepository;

    @InjectMocks
    private FavoriteService favoriteService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddFavorite_Success() {
        FavoriteDTO dto = new FavoriteDTO(1L, "abc123", "song");

        when(favoriteRepository.findByUserIdAndType(1L, "song")).thenReturn(List.of());
        when(favoriteRepository.findByUserIdAndSpotifyId(1L, "abc123")).thenReturn(Optional.empty());

        Favorite saved = new Favorite(1L, 1L, "abc123", "song", LocalDateTime.now());
        when(favoriteRepository.save(any())).thenReturn(saved);

        FavoriteResponseDTO response = favoriteService.addFavorite(dto);

        assertEquals("abc123", response.getSpotifyId());
        assertEquals(1L, response.getUserId());
        assertEquals("song", response.getType());
    }

    @Test
    void testAddFavorite_AlreadyExists() {
        FavoriteDTO dto = new FavoriteDTO(1L, "xyz999", "song");

        when(favoriteRepository.findByUserIdAndType(1L, "song")).thenReturn(List.of());
        when(favoriteRepository.findByUserIdAndSpotifyId(1L, "xyz999")).thenReturn(Optional.of(new Favorite()));

        assertThrows(IllegalArgumentException.class, () -> favoriteService.addFavorite(dto));
    }

    @Test
    void testAddFavorite_LimitReached() {
        List<Favorite> maxFavorites = List.of(
                new Favorite(), new Favorite(), new Favorite(), new Favorite()
        );

        when(favoriteRepository.findByUserIdAndType(1L, "song")).thenReturn(maxFavorites);

        FavoriteDTO dto = new FavoriteDTO(1L, "new", "song");

        assertThrows(IllegalArgumentException.class, () -> favoriteService.addFavorite(dto));
    }

    @Test
    void testRemoveFavoriteById_Success() {
        when(favoriteRepository.findById(100L)).thenReturn(Optional.of(new Favorite()));

        favoriteService.removeFavoriteById(100L);

        verify(favoriteRepository).deleteById(100L);
    }

    @Test
    void testRemoveFavoriteById_NotFound() {
        when(favoriteRepository.findById(200L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> favoriteService.removeFavoriteById(200L));
    }

    @Test
    void testReplaceFavorite_Success() {
        Favorite old = new Favorite(1L, 1L, "old123", "song", LocalDateTime.now());

        when(favoriteRepository.findByUserIdAndSpotifyId(1L, "old123"))
            .thenReturn(Optional.of(old));

        FavoriteDTO dto = new FavoriteDTO(1L, "new456", "playlist");

        Favorite updated = new Favorite(1L, 1L, "new456", "playlist", LocalDateTime.now());
        when(favoriteRepository.save(any())).thenReturn(updated);

        FavoriteResponseDTO result = favoriteService.replaceFavorite("old123", dto);

        assertEquals("new456", result.getSpotifyId());
        assertEquals("playlist", result.getType());
    }

    @Test
    void testReplaceFavorite_NotFound() {
        FavoriteDTO dto = new FavoriteDTO(1L, "new", "type");

        when(favoriteRepository.findByUserIdAndSpotifyId(1L, "old"))
            .thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> favoriteService.replaceFavorite("old", dto));
    }

    @Test
    void testIsFavoritedByUser() {
        when(favoriteRepository.existsByUserIdAndSpotifyId(5L, "abc123")).thenReturn(true);

        assertTrue(favoriteService.isFavoritedByUser(5L, "abc123"));
    }

    @Test
    void testGetFavoritesByUserIdAndType() {
        Page<Favorite> page = new PageImpl<>(List.of(
            new Favorite(1L, 1L, "abc", "song", LocalDateTime.now())
        ));

        when(favoriteRepository.findByUserIdAndType(eq(1L), eq("song"), any())).thenReturn(page);

        Page<FavoriteResponseDTO> result = favoriteService.getFavoritesByUserIdAndType(1L, "song", 0);

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void testGetLatestFavorites() {
        List<Favorite> favList = List.of(new Favorite(1L, 1L, "abc", "song", LocalDateTime.now()));
        when(favoriteRepository.findTop4ByUserIdAndType(eq(1L), eq("song"), any())).thenReturn(favList);

        var result = favoriteService.getLatestFavorites(1L, "song");

        assertEquals(1, result.size());
        assertEquals("abc", result.get(0).getSpotifyId());
    }
}
