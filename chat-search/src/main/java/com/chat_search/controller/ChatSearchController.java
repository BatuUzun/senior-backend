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

/**
 * Controller for handling chat search-related functionalities.
 * Provides APIs for searching messages, fetching user conversations,
 * and retrieving messages using cursor-based pagination.
 */
@RestController
@RequestMapping("/chat-search")
public class ChatSearchController {

    @Autowired
    private ChatSearchService chatSearchService;

    /**
     * Fetches a list of user conversations.
     * 
     * @param userId The ID of the user whose conversations are to be retrieved.
     * @return A list of ChatUserConversationDTO containing conversation details.
     */
    @PostMapping("/user/conversations")
    public List<ChatUserConversationDTO> getUserConversations(@RequestParam Long userId) {
        return chatSearchService.fetchUserConversations(userId);
    }

    /**
     * Searches messages globally based on a keyword.
     * Uses cursor-based pagination by providing the timestamp of the last retrieved message.
     * 
     * @param requestDTO DTO containing userId, keyword to search for, and lastSentAt timestamp for pagination.
     * @return A list of ChatMessageIndex objects containing matched messages.
     */
    @PostMapping("/search")
    public List<ChatMessageIndex> searchMessages(@Valid @RequestBody ChatSearchByKeywordRequestDTO requestDTO) {
        return chatSearchService.searchMessagesByKeyword(
            requestDTO.getUserId(),
            requestDTO.getKeyword(),
            requestDTO.getLastSentAt()
        );
    }

    /**
     * Searches messages inside a specific conversation.
     * Uses cursor-based pagination by providing the timestamp of the last retrieved message.
     * 
     * @param requestDTO DTO containing conversationId, keyword to search for, and lastSentAt timestamp for pagination.
     * @return A list of ChatMessageIndex objects containing matched messages in the conversation.
     */
    @PostMapping("/conversation/search")
    public List<ChatMessageIndex> searchMessagesInConversation(@Valid @RequestBody ChatSearchInConversationRequestDTO requestDTO) {
        return chatSearchService.searchMessagesInConversation(
            requestDTO.getConversationId(),
            requestDTO.getKeyword(),
            requestDTO.getLastSentAt()
        );
    }

    /**
     * Retrieves the latest 20 messages in a given conversation.
     * Uses cursor-based pagination by providing the timestamp of the last retrieved message.
     * 
     * @param requestDTO DTO containing conversationId and lastSentAt timestamp for pagination.
     * @return A list of ChatMessageIndex objects containing the latest messages in the conversation.
     */
    @PostMapping("/conversation/latest")
    public List<ChatMessageIndex> getLatestMessagesByConversationId(@Valid @RequestBody ChatLatestMessagesRequestDTO requestDTO) {
        return chatSearchService.fetchLatestMessagesByConversationId(
            requestDTO.getConversationId(),
            requestDTO.getLastSentAt()
        );
    }
}
