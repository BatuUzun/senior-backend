package com.chat_send.proxy;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.chat_send.dto.CreateConversationRequest;


@FeignClient(name = "conversation")
public interface ConversationProxy {
	@PostMapping("/conversation/create-conversation")
    Long createConversation(@RequestBody CreateConversationRequest createConversationRequest);
}
