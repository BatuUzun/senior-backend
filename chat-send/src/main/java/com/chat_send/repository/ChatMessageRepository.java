package com.chat_send.repository;

import org.springframework.data.cassandra.repository.CassandraRepository;
import com.chat_send.entity.ChatMessage;
import com.chat_send.entity.ChatMessageId;

public interface ChatMessageRepository extends CassandraRepository<ChatMessage, ChatMessageId> {
		
}
