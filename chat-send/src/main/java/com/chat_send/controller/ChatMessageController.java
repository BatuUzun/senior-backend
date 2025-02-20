package com.chat_send.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chat_send.dto.ChatMessageRequestDTO;
import com.chat_send.service.ChatMessageService;
import jakarta.validation.Valid;

/**
 * Controller for handling chat message sending functionality.
 * Provides an API endpoint for sending messages between users.
 */
@RestController
@RequestMapping("/chat-send")
public class ChatMessageController {

    @Autowired
    private ChatMessageService chatMessageService;

    /**
     * Sends a chat message.
     * 
     * @param chatMessageRequest DTO containing message details such as sender, receiver, and content.
     * @return ResponseEntity containing success message if the message is sent successfully,
     *         or an error message if the operation fails.
     */
    @PostMapping("/send")
    public ResponseEntity<String> sendMessage(@Valid @RequestBody ChatMessageRequestDTO chatMessageRequest) {
        try {
            // Process and send the chat message
            chatMessageService.processAndSendMessage(chatMessageRequest);
            return ResponseEntity.ok("Message sent!");
        } catch (Exception e) {
            // Handle errors and return an appropriate response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to send message: " + e.getMessage());
        }
    }
}
