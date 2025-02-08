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

@RestController
@RequestMapping("/chat-send")
public class ChatMessageController {

	@Autowired
	private ChatMessageService chatMessageService;

	@PostMapping("/send")
	public ResponseEntity<String> sendMessage(@Valid @RequestBody ChatMessageRequestDTO chatMessageRequest) {
	    try {
	        chatMessageService.processAndSendMessage(chatMessageRequest);
	        return ResponseEntity.ok("Message sent!");
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body("Failed to send message: " + e.getMessage());
	    }
	}



}