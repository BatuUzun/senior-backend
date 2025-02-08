package com.chat_send.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;

public class ChatMessageRequestDTO {

    private Long conversationId;

    @NotNull(message = "Sender ID cannot be null")
    private Long senderId;

    @NotNull(message = "Receiver ID cannot be null")
    private Long receiverId;

    @NotBlank(message = "Message cannot be empty")
    private String message;

    private Instant sentAt;  // Add this field

    public ChatMessageRequestDTO() {
    }

    public ChatMessageRequestDTO(Long conversationId, Long senderId, Long receiverId, String message, Instant sentAt) {
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

    public Instant getSentAt() { return sentAt; }
    public void setSentAt(Instant sentAt) { this.sentAt = sentAt; }
}
