package com.foodrecipes.credentials.credentials.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.foodrecipes.credentials.credentials.entity.ChatMessage;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
	List<ChatMessage> findBySenderIdAndReceiverId(Long senderId, Long receiverId);

	ChatMessage findTopByConversationIdOrderByTimestampDesc(Long conversationId);

}
