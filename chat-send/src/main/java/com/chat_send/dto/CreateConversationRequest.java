package com.chat_send.dto;

public class CreateConversationRequest {

    private Long user1;

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

}
