package com.foodrecipes.credentials.credentials.restcontrollers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.foodrecipes.credentials.credentials.dto.ConversationSummaryDTO;
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
}
