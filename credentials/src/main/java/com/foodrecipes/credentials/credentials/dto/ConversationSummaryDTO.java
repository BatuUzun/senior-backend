package com.foodrecipes.credentials.credentials.dto;

import java.time.LocalDateTime;

public class ConversationSummaryDTO {
    private Long conversationId;
    private Long opponentId;
    private String opponentUsername;
    private String opponentProfileImage;
    private String lastMessage;
    private LocalDateTime timestamp;
	public Long getConversationId() {
		return conversationId;
	}
	public void setConversationId(Long conversationId) {
		this.conversationId = conversationId;
	}
	public Long getOpponentId() {
		return opponentId;
	}
	public void setOpponentId(Long opponentId) {
		this.opponentId = opponentId;
	}
	public String getOpponentUsername() {
		return opponentUsername;
	}
	public void setOpponentUsername(String opponentUsername) {
		this.opponentUsername = opponentUsername;
	}
	public String getOpponentProfileImage() {
		return opponentProfileImage;
	}
	public void setOpponentProfileImage(String opponentProfileImage) {
		this.opponentProfileImage = opponentProfileImage;
	}
	public String getLastMessage() {
		return lastMessage;
	}
	public void setLastMessage(String lastMessage) {
		this.lastMessage = lastMessage;
	}
	public LocalDateTime getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}
	public ConversationSummaryDTO(Long conversationId, Long opponentId, String opponentUsername,
			String opponentProfileImage, String lastMessage, LocalDateTime timestamp) {
		super();
		this.conversationId = conversationId;
		this.opponentId = opponentId;
		this.opponentUsername = opponentUsername;
		this.opponentProfileImage = opponentProfileImage;
		this.lastMessage = lastMessage;
		this.timestamp = timestamp;
	}
	public ConversationSummaryDTO() {
		super();
	}

    // constructors, getters, setters
    
    
}
