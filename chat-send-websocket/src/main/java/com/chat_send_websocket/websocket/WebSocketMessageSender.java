package com.chat_send_websocket.websocket;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.chat_send_websocket.dto.ChatMessageRequestDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class WebSocketMessageSender {
    
    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;

    public WebSocketMessageSender(SimpMessagingTemplate messagingTemplate, ObjectMapper objectMapper) {
        this.messagingTemplate = messagingTemplate;
        this.objectMapper = objectMapper;
    }

    public void sendMessageToUser(Long receiverId, ChatMessageRequestDTO chatMessageRequest) {
        try {
            String messageJson = objectMapper.writeValueAsString(chatMessageRequest);
            
            // ✅ Ensure message is sent ONLY to the intended recipient
            messagingTemplate.convertAndSendToUser(
                receiverId.toString(), 
                "/queue/messages", 
                messageJson
            );

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
