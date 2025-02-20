package com.chat_send_websocket.controller;

import com.chat_send_websocket.dto.ChatMessageRequestDTO;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

/**
 * WebSocket controller for handling real-time chat messaging.
 * Uses Spring WebSockets with STOMP to send and receive messages.
 */
@Controller
public class ChatWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Constructor-based dependency injection for SimpMessagingTemplate.
     * This template is used to send messages to specific users.
     *
     * @param messagingTemplate The messaging template for WebSocket communication.
     */
    public ChatWebSocketController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Handles incoming WebSocket chat messages.
     * 
     * @param message The chat message received via WebSocket.
     * 
     * The method:
     * 1. Logs the received message.
     * 2. Sends the message to the intended receiver using "/user/{receiverId}/queue/messages".
     * 3. (Optional) Can also send the message back to the sender for UI updates.
     */
    @MessageMapping("/chat")
    public void processMessage(ChatMessageRequestDTO message) {
        System.out.println("📩 New WebSocket Message: " + message.getSenderId() + " ➝ " + message.getReceiverId() + ": " + message.getMessage());

        // ✅ Log the message destination
        System.out.println("📡 Sending message to: /user/" + message.getReceiverId() + "/queue/messages");

        // ✅ Send the message to the intended recipient's private queue
        messagingTemplate.convertAndSendToUser(
            message.getReceiverId().toString(), // Receiver's unique session identifier
            "/queue/messages", // WebSocket destination
            message // Payload (ChatMessageRequestDTO)
        );

        // ✅ Optionally send the message back to the sender for UI updates
        /*messagingTemplate.convertAndSendToUser(
            message.getSenderId().toString(),
            "/queue/messages",
            message
        );*/
    }
}
