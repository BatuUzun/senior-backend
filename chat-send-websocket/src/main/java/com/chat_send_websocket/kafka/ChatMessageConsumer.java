package com.chat_send_websocket.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.chat_send_websocket.dto.ChatMessageRequestDTO;
import com.chat_send_websocket.websocket.WebSocketMessageSender;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ChatMessageConsumer {
    
    private final WebSocketMessageSender webSocketMessageSender;
    private final ObjectMapper objectMapper;
    
    //private static final Logger logger = LoggerFactory.getLogger(ChatMessageConsumer.class);

    public ChatMessageConsumer(WebSocketMessageSender webSocketMessageSender, ObjectMapper objectMapper) {
        this.webSocketMessageSender = webSocketMessageSender;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "chat_messages", groupId = "chat-websocket-group")
    public void consumeMessage(ConsumerRecord<String, String> record) {
        try {
            String messageJson = record.value();
            ChatMessageRequestDTO chatMessageRequest = objectMapper.readValue(messageJson, ChatMessageRequestDTO.class);

            /*logger.info("📩 Received from Kafka: {} ➝ {}: {}", 
                    chatMessageRequest.getSenderId(), 
                    chatMessageRequest.getReceiverId(), 
                    chatMessageRequest.getMessage());*/
            System.out.println("✅ Received from Kafka: " + messageJson);

            // ✅ Send ONLY to the intended recipient
            webSocketMessageSender.sendMessageToUser(chatMessageRequest.getReceiverId(), chatMessageRequest);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
