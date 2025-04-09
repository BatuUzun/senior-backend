package com.foodrecipes.credentials.credentials;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import com.foodrecipes.credentials.credentials.dto.NotificationRequest;
import com.foodrecipes.credentials.credentials.entity.UserDeviceToken;
import com.foodrecipes.credentials.credentials.repository.UserDeviceTokenRepository;
import com.foodrecipes.credentials.credentials.service.FirebaseNotificationService;
import com.foodrecipes.credentials.credentials.service.NotificationService;

public class NotificationServiceTest {

    @Mock
    private UserDeviceTokenRepository deviceTokenRepository;

    @Mock
    private FirebaseNotificationService firebaseService;

    @InjectMocks
    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ✅ saveDeviceToken test
    @Test
    void testSaveDeviceToken_NewToken() {
        Long userId = 1L;
        String token = "test-token";

        when(deviceTokenRepository.findByUserId(userId)).thenReturn(Optional.empty());

        notificationService.saveDeviceToken(userId, token);

        ArgumentCaptor<UserDeviceToken> captor = ArgumentCaptor.forClass(UserDeviceToken.class);
        verify(deviceTokenRepository).save(captor.capture());

        UserDeviceToken savedToken = captor.getValue();
        assert savedToken.getUserId().equals(userId);
        assert savedToken.getDeviceToken().equals(token);
    }

    @Test
    void testSaveDeviceToken_ExistingToken() {
        Long userId = 2L;
        String token = "new-token";
        UserDeviceToken existing = new UserDeviceToken(1L, userId, "old-token");

        when(deviceTokenRepository.findByUserId(userId)).thenReturn(Optional.of(existing));

        notificationService.saveDeviceToken(userId, token);

        verify(deviceTokenRepository).delete(existing);
        verify(deviceTokenRepository).save(any(UserDeviceToken.class));
    }

    // ✅ sendNotifications - success case
    @Test
    void testSendNotifications_Success() {
        NotificationRequest request = new NotificationRequest();
        request.setUserId(3L);
        request.setTitle("Hello");
        request.setBody("World");

        UserDeviceToken deviceToken = new UserDeviceToken(1L, 3L, "token-123");
        when(deviceTokenRepository.findByUserId(3L)).thenReturn(Optional.of(deviceToken));

        ResponseEntity<String> response = notificationService.sendNotifications(request);

        verify(firebaseService).sendNotification("token-123", "Hello", "World");
        assert response.getStatusCode().is2xxSuccessful();
        assert response.getBody().equals("Notification sent successfully!");
    }

    // ❌ sendNotifications - no token
    @Test
    void testSendNotifications_NoToken() {
        NotificationRequest request = new NotificationRequest();
        request.setUserId(999L);
        request.setTitle("Missing");
        request.setBody("Token");

        when(deviceTokenRepository.findByUserId(999L)).thenReturn(Optional.empty());

        ResponseEntity<String> response = notificationService.sendNotifications(request);

        verify(firebaseService, never()).sendNotification(anyString(), anyString(), anyString());
        assert response.getStatusCode().is4xxClientError();
        assert response.getBody().equals("User does not have a registered device token.");
    }
}