package com.chat_search.service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.chat_search.dto.ChatUserConversationDTO;
import com.chat_search.entity.ChatMessageIndex;
import com.chat_search.repository.ChatMessageRepository;

@Service
public class ChatSearchService {

	@Autowired
    private ChatMessageRepository chatMessageRepository;

	public List<ChatUserConversationDTO> fetchUserConversations(Long userId) {
        PageRequest pageRequest = PageRequest.of(0, 100);  // Fetch 100 latest messages

        List<ChatMessageIndex> messages = chatMessageRepository.findMessagesByUserId(userId, pageRequest);

        // Group by conversationId and get the latest message
        Map<Long, ChatMessageIndex> latestMessages = messages.stream()
            .collect(Collectors.toMap(
                ChatMessageIndex::getConversationId,
                msg -> msg,
                (msg1, msg2) -> {
                    Instant sentAt1 = Instant.parse(msg1.getSentAt());
                    Instant sentAt2 = Instant.parse(msg2.getSentAt());
                    return sentAt1.isAfter(sentAt2) ? msg1 : msg2;
                }
            ));

        return latestMessages.values().stream()
            .map(msg -> new ChatUserConversationDTO(
                msg.getConversationId(),
                msg.getMessage(),
                msg.getSentAt(),
                (msg.getReceiverId().equals(userId)) ? msg.getSenderId() : msg.getReceiverId() // Get other participant's ID
            ))
            .collect(Collectors.toList());
    }



    
    // Fetch latest 20 messages in a conversation (cursor-based pagination)
    public List<ChatMessageIndex> fetchLatestMessagesByConversationId(Long conversationId, String lastSentAt) {
        if (lastSentAt == null || lastSentAt.isEmpty()) {
            // Fetch the latest 20 messages initially
            return chatMessageRepository.findByConversationIdOrderBySentAtDesc(conversationId, PageRequest.of(0, 20));
        } else {
            // Fetch older messages before `lastSentAt`
            return chatMessageRepository.findByConversationIdAndSentAtLessThanOrderBySentAtDesc(conversationId, lastSentAt, PageRequest.of(0, 20));
        }
    }
    
    
    public List<ChatMessageIndex> searchMessagesInConversation(Long conversationId, String keyword, String lastSentAt) {
        PageRequest pageRequest = PageRequest.of(0, 20);

        if (lastSentAt == null || lastSentAt.isBlank()) {
            // Call query without date filter
            return chatMessageRepository.searchMessagesInConversationWithoutDate(keyword, conversationId, pageRequest);
        } else {
            // Call query with range filter
            return chatMessageRepository.searchMessagesInConversationWithDate(keyword, conversationId, lastSentAt, pageRequest);
        }
    }


    public List<ChatMessageIndex> searchMessagesByKeyword(Long userId, String keyword, String lastSentAt) {
        PageRequest pageRequest = PageRequest.of(0, 20);

        if (lastSentAt == null || lastSentAt.isBlank()) {
            // Use query without date filtering
            return chatMessageRepository.searchGlobalMessagesWithoutDate(keyword, userId, pageRequest);
        } else {
            // Use query with range filter
            return chatMessageRepository.searchGlobalMessagesWithDate(keyword, userId, lastSentAt, pageRequest);
        }
    }



}
