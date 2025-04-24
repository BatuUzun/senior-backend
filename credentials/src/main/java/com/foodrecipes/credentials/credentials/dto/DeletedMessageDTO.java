package com.foodrecipes.credentials.credentials.dto;

public class DeletedMessageDTO {
    private Long messageId;

    public DeletedMessageDTO(Long messageId) {
        this.messageId = messageId;
    }

    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }
}
