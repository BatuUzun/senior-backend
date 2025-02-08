package com.conversation.dto;

import jakarta.validation.constraints.Min;

public class CreateConversationRequest {

    @Min(value = 1, message = "user1 must be greater than 0")
    private Long user1;

    @Min(value = 1, message = "user2 must be greater than 0")
    private Long user2;

    public CreateConversationRequest() {}

    public CreateConversationRequest(Long user1, Long user2) {
        this.user1 = user1;
        this.user2 = user2;
    }

    public Long getUser1() {
        return user1;
    }

    public Long getUser2() {
        return user2;
    }

    // Custom validation method to ensure user1 and user2 are not equal
    public void validate() {
        if (user1.equals(user2)) {
            throw new IllegalArgumentException("user1 and user2 cannot be the same.");
        }
    }
}
