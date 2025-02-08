package com.chat_send.entity;

import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

import java.io.Serializable;
import java.util.UUID;

import static org.springframework.data.cassandra.core.cql.PrimaryKeyType.CLUSTERED;
import static org.springframework.data.cassandra.core.cql.PrimaryKeyType.PARTITIONED;

@PrimaryKeyClass
public class ChatMessageId implements Serializable {

    @PrimaryKeyColumn(name = "conversation_id", type = PARTITIONED)  // Partition key
    private Long conversationId;

    @PrimaryKeyColumn(name = "message_id", type = CLUSTERED, ordering = Ordering.DESCENDING)  // Clustering key
    private UUID messageId; // Ensure UUID is used for TIMEUUID

    public ChatMessageId() {
    }

    public ChatMessageId(Long conversationId, UUID messageId) {
        this.conversationId = conversationId;
        this.messageId = messageId;
    }

    public Long getConversationId() {
        return conversationId;
    }

    public void setConversationId(Long conversationId) {
        this.conversationId = conversationId;
    }

    public UUID getMessageId() {
        return messageId;
    }

    public void setMessageId(UUID messageId) {
        this.messageId = messageId;
    }
}
