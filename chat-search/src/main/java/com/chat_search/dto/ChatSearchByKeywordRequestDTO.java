package com.chat_search.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ChatSearchByKeywordRequestDTO {

    @NotNull(message = "User ID cannot be null")
    private Long userId;

    @Size(min = 1, message = "Keyword must be at least 1 character long")
    private String keyword;

    private String lastSentAt; // Cursor-based pagination

    public ChatSearchByKeywordRequestDTO() {}

    public ChatSearchByKeywordRequestDTO(Long userId, String keyword, String lastSentAt) {
        this.userId = userId;
        this.keyword = keyword;
        this.lastSentAt = lastSentAt;
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getKeyword() { return keyword; }
    public void setKeyword(String keyword) { this.keyword = keyword; }

    public String getLastSentAt() { return lastSentAt; }
    public void setLastSentAt(String lastSentAt) { this.lastSentAt = lastSentAt; }
}
