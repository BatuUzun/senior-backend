package com.conversation.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.conversation.entity.Conversation;
import com.conversation.repository.ConversationRepository;

@Service
public class ConversationService {

    @Autowired
    private ConversationRepository conversationRepository;

    @Transactional
    public Long createOrGetConversation(Long user1, Long user2) {
        if (user1 == null || user2 == null || user1.equals(user2)) {
            throw new IllegalArgumentException("Invalid user IDs");
        }

        // Ensure user1 is always the smaller ID
        final Long smallerUserId = Math.min(user1, user2);
        final Long largerUserId = Math.max(user1, user2);

        // Check if conversation already exists
        return conversationRepository.findByUser1AndUser2(smallerUserId, largerUserId)
                .map(Conversation::getId)
                .orElseGet(() -> {
                    Conversation newConversation = new Conversation(smallerUserId, largerUserId);
                    conversationRepository.save(newConversation);
                    return newConversation.getId();
                });
    }

}
