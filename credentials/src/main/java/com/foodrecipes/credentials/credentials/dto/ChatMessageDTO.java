package com.foodrecipes.credentials.credentials.dto;

public class ChatMessageDTO {
    private Long senderId;
    private Long receiverId;
    private String content;
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
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public ChatMessageDTO(Long senderId, Long receiverId, String content) {
		super();
		this.senderId = senderId;
		this.receiverId = receiverId;
		this.content = content;
	}
	public ChatMessageDTO() {
		super();
	}

    // Getters and Setters
    
    
}
