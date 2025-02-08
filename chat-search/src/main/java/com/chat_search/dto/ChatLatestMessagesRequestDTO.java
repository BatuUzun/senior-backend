package com.chat_search.dto;

import jakarta.validation.constraints.NotNull;

public class ChatLatestMessagesRequestDTO {

    @NotNull(message = "Conversation ID cannot be null")
    private Long conversationId;

    private String lastSentAt;  // Cursor for pagination

    public ChatLatestMessagesRequestDTO() {}

    public ChatLatestMessagesRequestDTO(Long conversationId, String lastSentAt) {
        this.conversationId = conversationId;
        this.lastSentAt = lastSentAt;
    }

    public Long getConversationId() { return conversationId; }
    public void setConversationId(Long conversationId) { this.conversationId = conversationId; }

    public String getLastSentAt() { return lastSentAt; }
    public void setLastSentAt(String lastSentAt) { this.lastSentAt = lastSentAt; }
}
