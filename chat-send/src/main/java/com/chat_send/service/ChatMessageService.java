package com.chat_send.service;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.chat_send.constant.Constants;
import com.chat_send.dto.ChatMessageRequestDTO;
import com.chat_send.dto.CreateConversationRequest;
import com.chat_send.entity.ChatMessage;
import com.chat_send.kafka.ChatMessageProducer;
import com.chat_send.proxy.ConversationProxy;
import com.chat_send.repository.ChatMessageRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ChatMessageService {

    @Autowired
    private ChatMessageRepository chatMessageRepository;
    
	@Autowired
	private ChatMessageProducer chatMessageProducer;
	
	@Autowired
	private ConversationProxy conversationProxy;
	
    @Autowired
    private ObjectMapper objectMapper;



    public ChatMessage saveMessage(Long conversationId, Long senderId, Long receiverId, String message) {
        // Generate a TIMEUUID for message_id
        Instant sentAt = Instant.now();

        // Create a ChatMessage object
        ChatMessage chatMessage = new ChatMessage(conversationId, senderId, receiverId, message, sentAt);

        // Save the entity using the default save() method
        return chatMessageRepository.save(chatMessage);
    }


    public void processAndSendMessage(ChatMessageRequestDTO chatMessageRequest) throws Exception {
        if (chatMessageRequest.getConversationId() == null) {
            CreateConversationRequest createConversationRequest = new CreateConversationRequest(
                    chatMessageRequest.getReceiverId(), chatMessageRequest.getSenderId());

            chatMessageRequest.setConversationId(conversationProxy.createConversation(createConversationRequest));
        }

        if (chatMessageRequest.getConversationId() != null) {
            chatMessageRequest.setSentAt(Instant.now());

            // Use the properly configured ObjectMapper
            String messageJson = objectMapper.writeValueAsString(chatMessageRequest);
            chatMessageProducer.sendMessage(Constants.CHAT_TOPIC, messageJson);
        } else {
            throw new Exception("ConversationId is null!");
        }
    }

}
