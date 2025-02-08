package com.chat_send_websocket.controller;

import com.chat_send_websocket.dto.ChatMessageRequestDTO;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class ChatWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;

    public ChatWebSocketController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/chat")
    public void processMessage(ChatMessageRequestDTO message) {
        System.out.println("📩 New WebSocket Message: " + message.getSenderId() + " ➝ " + message.getReceiverId() + ": " + message.getMessage());

        // ✅ Log the message destination
        System.out.println("📡 Sending message to: /user/" + message.getReceiverId() + "/queue/messages");

        
        // ✅ Only send message to the intended receiver
        messagingTemplate.convertAndSendToUser(
            message.getReceiverId().toString(),
            "/queue/messages",
            message
        );

        // ✅ Also send message back to the sender so they can see their own message
        /*messagingTemplate.convertAndSendToUser(
            message.getSenderId().toString(),
            "/queue/messages",
            message
        );*/
    }
}
