package com.foodrecipes.credentials.credentials.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.foodrecipes.credentials.credentials.entity.ChatMessage;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
	List<ChatMessage> findBySenderIdAndReceiverId(Long senderId, Long receiverId);

	ChatMessage findTopByConversationIdOrderByTimestampDesc(Long conversationId);

	

	@Query("""
		    SELECT m FROM ChatMessage m 
		    WHERE m.conversationId = :conversationId 
		    AND m.timestamp < :beforeTimestamp
		    ORDER BY m.timestamp DESC
		""")
		List<ChatMessage> findMessagesBeforeTimestamp(
		    @Param("conversationId") Long conversationId,
		    @Param("beforeTimestamp") LocalDateTime beforeTimestamp,
		    Pageable pageable
		);

		@Query("""
		    SELECT m FROM ChatMessage m 
		    WHERE m.conversationId = :conversationId
		    ORDER BY m.timestamp DESC
		""")
		List<ChatMessage> findMessagesWithoutCursor(
		    @Param("conversationId") Long conversationId,
		    Pageable pageable
		);

}
