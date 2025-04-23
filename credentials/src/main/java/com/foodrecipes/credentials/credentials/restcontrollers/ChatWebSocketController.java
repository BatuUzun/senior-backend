package com.foodrecipes.credentials.credentials.restcontrollers;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import com.foodrecipes.credentials.credentials.dto.ChatMessageDTO;
import com.foodrecipes.credentials.credentials.entity.ChatMessage;
import com.foodrecipes.credentials.credentials.service.ChatMessageService;

@Controller
public class ChatWebSocketController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private ChatMessageService chatMessageService;

    @MessageMapping("/chat.send")
    @SendToUser("/queue/messages") // This only goes to the sender
    public ChatMessage processMessage(ChatMessageDTO dto, Principal principal) {
        ChatMessage savedMessage = chatMessageService.saveAndSendMessage(dto);

        String receiverId = String.valueOf(dto.getReceiverId());
        String senderId = principal.getName(); // comes from query ?userId=...

        // ✅ Add debug logs
        System.out.println("➡️  Sender ID: " + senderId);
        System.out.println("➡️  Receiver ID: " + receiverId);
        System.out.println("✅ Sending to /user/" + receiverId + "/queue/messages");

        // ✅ Send to receiver
        messagingTemplate.convertAndSendToUser(
            receiverId,
            "/queue/messages",
            savedMessage
        );

        return savedMessage; // This goes back to sender
    }
}
