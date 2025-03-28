package com.foodrecipes.credentials.credentials.dto;

public class NotificationRequest {
    private Long userId;
    private String title;
    private String body;
	public NotificationRequest(Long userId, String title, String body) {
		super();
		this.userId = userId;
		this.title = title;
		this.body = body;
	}
	public NotificationRequest() {
		super();
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
    
    
}