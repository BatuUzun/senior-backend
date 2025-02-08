package com.chat_search.kafka;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.bulk.BulkOperation;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.bulk.BulkResponseItem;
import com.chat_search.dto.ChatMessageDTO;
import com.chat_search.entity.ChatMessageIndex;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ChatMessageConsumer {

    private final ElasticsearchClient elasticsearchClient;
    private final ObjectMapper objectMapper;
    private final List<BulkOperation> bulkOperations = new ArrayList<>();

    public ChatMessageConsumer(ElasticsearchClient elasticsearchClient, ObjectMapper objectMapper) {
        this.elasticsearchClient = elasticsearchClient;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "chat_messages", groupId = "chat-search-group")
    public void consumeMessage(String messageJson) {
        try {
            System.out.println("Received Kafka Message: " + messageJson);  // Debug log

            ChatMessageDTO chatMessageDTO = objectMapper.readValue(messageJson, ChatMessageDTO.class);

            ChatMessageIndex chatMessageIndex = new ChatMessageIndex(
                    chatMessageDTO.getConversationId(),
                    chatMessageDTO.getSenderId(),
                    chatMessageDTO.getReceiverId(),
                    chatMessageDTO.getMessage(),
                    chatMessageDTO.getSentAt()
            );

            synchronized (bulkOperations) {
                bulkOperations.add(BulkOperation.of(b -> b.index(idx -> idx
                        .index("chat_messages")
                        .document(chatMessageIndex)
                )));
            }

            System.out.println("Added to bulkOperations: " + chatMessageIndex); // Debug log

        } catch (Exception e) {
            System.err.println("Error processing Kafka message: " + e.getMessage());
        }
    }

    @Scheduled(fixedRate = 10000) // Every 10 seconds
    public void executeBulkInsert() {
        synchronized (bulkOperations) {
            if (!bulkOperations.isEmpty()) {
                try {
                    System.out.println("Executing Bulk Insert for " + bulkOperations.size() + " messages...");  // Debug log

                    BulkRequest bulkRequest = BulkRequest.of(b -> b.operations(bulkOperations));
                    BulkResponse bulkResponse = elasticsearchClient.bulk(bulkRequest);

                    // Log Bulk Insert Response
                    for (BulkResponseItem item : bulkResponse.items()) {
                        if (item.error() != null) {
                            System.err.println("Failed to insert document: " + item.error().reason());
                        } else {
                            System.out.println("Successfully inserted document: " + item.id());
                        }
                    }

                    bulkOperations.clear();
                    System.out.println("Bulk insert completed.");

                } catch (Exception e) {
                    System.err.println("Bulk insert failed: " + e.getMessage());
                }
            } else {
                System.out.println("No new messages to insert.");  // Debug log
            }
        }
    }
}
