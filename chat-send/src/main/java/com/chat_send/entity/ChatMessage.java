package com.chat_send.entity;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.Instant;

@Table("chat_messages") // Use the table name without specifying the keyspace
public class ChatMessage {

    @PrimaryKey
    private ChatMessageId id;

    @Column("sender_id")
    private Long senderId;

    @Column("receiver_id")
    private Long receiverId;

    @Column("message")
    private String message;

    @Column("sent_at")
    private Instant sentAt;

    public ChatMessage() {
    }

    public ChatMessage(Long conversationId, Long senderId, Long receiverId, String message, Instant sentAt) {
        this.id = new ChatMessageId(conversationId, com.datastax.oss.driver.api.core.uuid.Uuids.timeBased());
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.message = message;
        this.sentAt = sentAt;
    }

    public ChatMessageId getId() {
        return id;
    }

    public void setId(ChatMessageId id) {
        this.id = id;
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public Long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Instant getSentAt() {
        return sentAt;
    }

    public void setSentAt(Instant sentAt) {
        this.sentAt = sentAt;
    }
}
