package com.foodrecipes.credentials.credentials;

import com.foodrecipes.credentials.credentials.dto.LikeDTO;
import com.foodrecipes.credentials.credentials.entity.Like;
import com.foodrecipes.credentials.credentials.repository.LikeRepository;
import com.foodrecipes.credentials.credentials.service.LikeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class LikeServiceTest {

    @Mock
    private LikeRepository likeRepository;

    @InjectMocks
    private LikeService likeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddLike_Success() {
        LikeDTO dto = new LikeDTO();
        dto.setUserId(1L);
        dto.setSpotifyId("abc123");
        dto.setType("song");

        when(likeRepository.findBySpotifyIdAndUserIdAndType(dto.getSpotifyId(), dto.getUserId(), dto.getType()))
                .thenReturn(Optional.empty());

        Like saved = new Like(1L, 1L, "abc123", "song", LocalDateTime.now());
        when(likeRepository.save(any(Like.class))).thenReturn(saved);

        var result = likeService.addLike(dto);

        assertEquals(dto.getUserId(), result.getUserId());
        assertEquals(dto.getSpotifyId(), result.getSpotifyId());
        assertEquals(dto.getType(), result.getType());
    }

    @Test
    void testAddLike_AlreadyExists() {
        LikeDTO dto = new LikeDTO();
        dto.setUserId(1L);
        dto.setSpotifyId("abc123");
        dto.setType("song");

        when(likeRepository.findBySpotifyIdAndUserIdAndType(dto.getSpotifyId(), dto.getUserId(), dto.getType()))
                .thenReturn(Optional.of(new Like()));

        assertThrows(IllegalArgumentException.class, () -> likeService.addLike(dto));
    }

    @Test
    void testRemoveLike_Success() {
        String spotifyId = "abc123";
        Long userId = 1L;
        String type = "song";

        when(likeRepository.findBySpotifyIdAndUserIdAndType(spotifyId, userId, type))
                .thenReturn(Optional.of(new Like()));

        likeService.removeLike(userId, spotifyId, type);

        verify(likeRepository).deleteBySpotifyIdAndUserIdAndType(spotifyId, userId, type);
    }

    @Test
    void testRemoveLike_NotExists() {
        String spotifyId = "abc123";
        Long userId = 1L;
        String type = "song";

        when(likeRepository.findBySpotifyIdAndUserIdAndType(spotifyId, userId, type)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> likeService.removeLike(userId, spotifyId, type));
    }

    @Test
    void testGetLikesByUserId() {
        Long userId = 1L;
        Page<Like> page = new PageImpl<>(List.of(new Like(1L, userId, "xyz", "song", LocalDateTime.now())));

        when(likeRepository.findByUserId(eq(userId), any(Pageable.class))).thenReturn(page);

        var result = likeService.getLikesByUserId(userId, 0);
        assertFalse(result.isEmpty());
    }

    @Test
    void testGetLikesCountBySpotifyId() {
        when(likeRepository.countBySpotifyId("track123")).thenReturn(42L);
        assertEquals(42L, likeService.getLikesCountBySpotifyId("track123"));
    }

    @Test
    void testGetLikeBySpotifyIdAndUserId() {
        Like like = new Like(1L, 2L, "trackX", "song", LocalDateTime.now());
        when(likeRepository.findBySpotifyIdAndUserId("trackX", 2L)).thenReturn(Optional.of(like));

        var result = likeService.getLikeBySpotifyIdAndUserId("trackX", 2L);
        assertTrue(result.isPresent());
        assertEquals("trackX", result.get().getSpotifyId());
    }

    @Test
    void testGetLikesByUserIdAndType() {
        Page<Like> page = new PageImpl<>(List.of(new Like(1L, 5L, "song123", "song", LocalDateTime.now())));
        when(likeRepository.findByUserIdAndType(eq(5L), eq("song"), any(Pageable.class))).thenReturn(page);

        var result = likeService.getLikesByUserIdAndType(5L, "song", 0);
        assertEquals(1, result.getContent().size());
    }
}
