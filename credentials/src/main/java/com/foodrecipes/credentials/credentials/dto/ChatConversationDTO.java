package com.foodrecipes.credentials.credentials.dto;

import java.time.LocalDateTime;

public class ChatConversationDTO {
    private Long opponentId;
    private String opponentUsername;
    private String opponentProfileImage;
    private String lastMessage;
    private LocalDateTime timestamp;

    public ChatConversationDTO(Long opponentId, String username, String profileImage,
                               String lastMessage, LocalDateTime timestamp) {
        this.opponentId = opponentId;
        this.opponentUsername = username;
        this.opponentProfileImage = profileImage;
        this.lastMessage = lastMessage;
        this.timestamp = timestamp;
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

    // Getters and setters...
    
    
}
