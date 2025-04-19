package com.foodrecipes.credentials.credentials.restcontrollers;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.foodrecipes.credentials.credentials.dto.ConversationSummaryDTO;
import com.foodrecipes.credentials.credentials.entity.ChatMessage;
import com.foodrecipes.credentials.credentials.service.ChatMessageService;

@RestController
public class ChatController {
	@Autowired
	private ChatMessageService chatMessageService;

	@GetMapping("/conversation-summaries")
	public List<ConversationSummaryDTO> getConversationSummaries(@RequestParam Long userId) {
		return chatMessageService.getConversationSummaries(userId);
	}

	@GetMapping("/user-conversations")
	public List<Long> getConversationIds(@RequestParam Long userId) {
		return chatMessageService.getConversationIdsForUser(userId);
	}

	@GetMapping("/conversation/{conversationId}")
	public ResponseEntity<List<ChatMessage>> getChatMessages(@PathVariable Long conversationId,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime cursor) {

		List<ChatMessage> messages = chatMessageService.getMessagesWithCursor(conversationId, cursor);
		return ResponseEntity.ok(messages);
	}
}
