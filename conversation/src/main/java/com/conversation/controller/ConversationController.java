package com.conversation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.conversation.dto.CreateConversationRequest;
import com.conversation.service.ConversationService;

import jakarta.validation.Valid;

/**
 * Controller for handling conversation creation.
 * This API allows users to create or retrieve an existing conversation between two users.
 */
@RestController
@RequestMapping("/conversation")
public class ConversationController {

    @Autowired
    private ConversationService conversationService;

    /**
     * Creates a new conversation or retrieves an existing one.
     * 
     * @param request DTO containing the user IDs of the two participants.
     * @return ResponseEntity containing the conversation ID if successful,
     *         or an error response in case of validation failure or server issues.
     */
    @PostMapping("/create-conversation")
    public ResponseEntity<Long> createConversation(@RequestBody @Valid CreateConversationRequest request) {
        try {
            // Validate that user1 is not the same as user2
            request.validate();  

            // Create or retrieve the existing conversation
            Long conversationId = conversationService.createOrGetConversation(request.getUser1(), request.getUser2());

            // Return the conversation ID with a 200 OK response
            return ResponseEntity.ok(conversationId);
        } catch (IllegalArgumentException e) {
            // Handle invalid request (e.g., same user ID for both participants)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null); // 400 Bad Request
        } catch (Exception e) {
            // Handle unexpected server errors
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // 500 Internal Server Error
        }
    }
}
