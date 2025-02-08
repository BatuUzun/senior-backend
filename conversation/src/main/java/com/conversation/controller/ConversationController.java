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

@RestController
@RequestMapping("/conversation")
public class ConversationController {

	@Autowired
	private ConversationService conversationService;

	@PostMapping("/create-conversation")
	public ResponseEntity<Long> createConversation(@RequestBody @Valid CreateConversationRequest request) {
        try {
            request.validate();  // Validate that user1 != user2
            Long conversationId = conversationService.createOrGetConversation(request.getUser1(), request.getUser2());
            return ResponseEntity.ok(conversationId); // 200 OK with conversation ID
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null); // 400 Bad Request
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // 500 Internal Server Error
        }
    }
}
