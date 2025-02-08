package com.chat_search.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.chat_search.dto.ChatLatestMessagesRequestDTO;
import com.chat_search.dto.ChatSearchByKeywordRequestDTO;
import com.chat_search.dto.ChatSearchInConversationRequestDTO;
import com.chat_search.dto.ChatUserConversationDTO;
import com.chat_search.entity.ChatMessageIndex;
import com.chat_search.service.ChatSearchService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/chat-search")
public class ChatSearchController {

	@Autowired
    private ChatSearchService chatSearchService;

	// Fetch messages using cursor-based pagination
	@PostMapping("/user/conversations")
	public List<ChatUserConversationDTO> getUserConversations(@RequestParam Long userId) {
	    return chatSearchService.fetchUserConversations(userId);
	}


    // Search messages globally (cursor-based)
    @PostMapping("/search")
    public List<ChatMessageIndex> searchMessages(@Valid @RequestBody ChatSearchByKeywordRequestDTO requestDTO) {
        return chatSearchService.searchMessagesByKeyword(requestDTO.getUserId(), requestDTO.getKeyword(), requestDTO.getLastSentAt());
    }

    // Search messages inside a conversation (cursor-based)
    @PostMapping("/conversation/search")
    public List<ChatMessageIndex> searchMessagesInConversation(@Valid @RequestBody ChatSearchInConversationRequestDTO requestDTO) {
        return chatSearchService.searchMessagesInConversation(requestDTO.getConversationId(), requestDTO.getKeyword(), requestDTO.getLastSentAt());
    }

    // Fetch latest 20 messages in a conversation (cursor-based pagination)
    @PostMapping("/conversation/latest")
    public List<ChatMessageIndex> getLatestMessagesByConversationId(@Valid @RequestBody ChatLatestMessagesRequestDTO requestDTO) {
        return chatSearchService.fetchLatestMessagesByConversationId(requestDTO.getConversationId(), requestDTO.getLastSentAt());
    }

}
