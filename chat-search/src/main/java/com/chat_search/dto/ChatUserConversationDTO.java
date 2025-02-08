package com.chat_search.dto;

public class ChatUserConversationDTO {
    private Long conversationId;
    private String lastMessage;
    private String lastSentAt;
    private Long otherUserId;  // New field

    public ChatUserConversationDTO(Long conversationId, String lastMessage, String lastSentAt, Long otherUserId) {
        this.conversationId = conversationId;
        this.lastMessage = lastMessage;
        this.lastSentAt = lastSentAt;
        this.otherUserId = otherUserId;
    }

    public Long getConversationId() { return conversationId; }
    public String getLastMessage() { return lastMessage; }
    public String getLastSentAt() { return lastSentAt; }
    public Long getOtherUserId() { return otherUserId; } // Getter for other participant
}
