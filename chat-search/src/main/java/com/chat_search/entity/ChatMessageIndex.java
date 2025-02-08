package com.chat_search.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "chat_messages")
public class ChatMessageIndex {

    @Id
    private String id;

    @Field(type = FieldType.Long)
    private Long conversationId;

    @Field(type = FieldType.Long)
    private Long senderId;

    @Field(type = FieldType.Long)
    private Long receiverId;

    @Field(type = FieldType.Text)
    private String message;

    @Field(type = FieldType.Date)
    private String sentAt;

    public ChatMessageIndex() {}

    public ChatMessageIndex(Long conversationId, Long senderId, Long receiverId, String message, String sentAt) {
        this.conversationId = conversationId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.message = message;
        this.sentAt = sentAt;
    }

    public Long getConversationId() { return conversationId; }
    public void setConversationId(Long conversationId) { this.conversationId = conversationId; }

    public Long getSenderId() { return senderId; }
    public void setSenderId(Long senderId) { this.senderId = senderId; }

    public Long getReceiverId() { return receiverId; }
    public void setReceiverId(Long receiverId) { this.receiverId = receiverId; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getSentAt() { return sentAt; }
    public void setSentAt(String sentAt) { this.sentAt = sentAt; }
}
