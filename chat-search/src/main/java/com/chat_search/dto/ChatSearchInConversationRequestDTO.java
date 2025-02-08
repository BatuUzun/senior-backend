package com.chat_search.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ChatSearchInConversationRequestDTO {

    @NotNull(message = "Conversation ID cannot be null")
    private Long conversationId;

    @Size(min = 1, message = "Keyword must be at least 1 character long")
    private String keyword;

    private String lastSentAt; // Cursor-based pagination

    public ChatSearchInConversationRequestDTO() {}

    public ChatSearchInConversationRequestDTO(Long conversationId, String keyword, String lastSentAt) {
        this.conversationId = conversationId;
        this.keyword = keyword;
        this.lastSentAt = lastSentAt;
    }

    public Long getConversationId() { return conversationId; }
    public void setConversationId(Long conversationId) { this.conversationId = conversationId; }

    public String getKeyword() { return keyword; }
    public void setKeyword(String keyword) { this.keyword = keyword; }

    public String getLastSentAt() { return lastSentAt; }
    public void setLastSentAt(String lastSentAt) { this.lastSentAt = lastSentAt; }
}
