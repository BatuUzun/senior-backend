package com.foodrecipes.credentials.credentials.restcontrollers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.foodrecipes.credentials.credentials.dto.ChatConversationDTO;
import com.foodrecipes.credentials.credentials.dto.ChatMessageDTO;
import com.foodrecipes.credentials.credentials.service.ChatMessageService;

@Controller
public class ChatWebSocketController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private ChatMessageService chatMessageService;

    @MessageMapping("/chat.send")
    public void processMessage(ChatMessageDTO dto) {
        // Save to DB
        chatMessageService.saveAndSendMessage(dto);

        // Send to specific user
        messagingTemplate.convertAndSendToUser(
            dto.getReceiverId().toString(),
            "/queue/messages",
            dto
        );
    }
    
    
}
