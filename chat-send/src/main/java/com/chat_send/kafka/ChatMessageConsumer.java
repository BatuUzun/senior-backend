package com.chat_send.kafka;

import com.chat_send.dto.ChatMessageRequestDTO;
import com.chat_send.entity.ChatMessage;
import com.chat_send.repository.ChatMessageRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class ChatMessageConsumer {

    private static final Logger logger = LoggerFactory.getLogger(ChatMessageConsumer.class);
    
    private final ChatMessageRepository chatMessageRepository;
    private final ObjectMapper objectMapper;

    public ChatMessageConsumer(ChatMessageRepository chatMessageRepository, ObjectMapper objectMapper) {
        this.chatMessageRepository = chatMessageRepository;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "chat_messages", groupId = "chat-group")
    public void consumeMessage(ConsumerRecord<String, String> record) {
        try {
            String messageJson = record.value();

            // Convert JSON to DTO
            ChatMessageRequestDTO chatMessageRequest = objectMapper.readValue(messageJson, ChatMessageRequestDTO.class);

            // Convert DTO to Entity
            ChatMessage chatMessage = new ChatMessage(
                    chatMessageRequest.getConversationId(),
                    chatMessageRequest.getSenderId(),
                    chatMessageRequest.getReceiverId(),
                    chatMessageRequest.getMessage(),
                    chatMessageRequest.getSentAt()
            );

            // Save to Cassandra
            chatMessageRepository.save(chatMessage);
            logger.info("✅ Message saved to Cassandra | Conversation ID: {} | Sender: {} | Message: {}",
                    chatMessage.getId().getConversationId(), chatMessage.getSenderId(), chatMessage.getMessage());

        } catch (Exception e) {
            logger.error("❌ Failed to process Kafka message. Offset: {}, Partition: {}, Error: {}",
                    record.offset(), record.partition(), e.getMessage(), e);
        }
    }
}
