package com.foodrecipes.credentials.credentials.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.foodrecipes.credentials.credentials.dto.ChatMessageDTO;
import com.foodrecipes.credentials.credentials.dto.ConversationSummaryDTO;
import com.foodrecipes.credentials.credentials.dto.UserProfileResponseProfileGetterDTO;
import com.foodrecipes.credentials.credentials.entity.ChatMessage;
import com.foodrecipes.credentials.credentials.entity.Conversation;
import com.foodrecipes.credentials.credentials.repository.ChatMessageRepository;
import com.foodrecipes.credentials.credentials.repository.ConversationRepository;

@Service
public class ChatMessageService {

	private final ChatMessageRepository chatMessageRepository;
    private final ConversationRepository conversationRepository;
    private final UserProfileService userProfileService;

    public ChatMessageService(
            ChatMessageRepository chatMessageRepository,
            ConversationRepository conversationRepository,
            UserProfileService userProfileService
    ) {
        this.chatMessageRepository = chatMessageRepository;
        this.conversationRepository = conversationRepository;
        this.userProfileService = userProfileService;
    }
    

    @Transactional
    public ChatMessage saveAndSendMessage(ChatMessageDTO dto) {
        Long conversationId = dto.getConversationId();

        if (conversationId == null) {
            Conversation conversation = conversationRepository
                .findByUserPair(dto.getSenderId(), dto.getReceiverId())
                .orElseGet(() -> {
                    Conversation newConv = new Conversation();
                    if (dto.getSenderId() < dto.getReceiverId()) {
                        newConv.setUser1(dto.getSenderId());
                        newConv.setUser2(dto.getReceiverId());
                    } else {
                        newConv.setUser1(dto.getReceiverId());
                        newConv.setUser2(dto.getSenderId());
                    }
                    return conversationRepository.save(newConv);
                });

            conversationId = conversation.getId();
        }

        ChatMessage message = new ChatMessage();
        message.setSenderId(dto.getSenderId());
        message.setReceiverId(dto.getReceiverId());
        message.setContent(dto.getContent());
        message.setConversationId(conversationId);

        return chatMessageRepository.save(message); // ✅ return saved message with ID
    }



    public List<ConversationSummaryDTO> getConversationSummaries(Long userId) {
        List<Conversation> conversations = conversationRepository.findByUser1OrUser2(userId, userId);

        List<Long> opponentIds = conversations.stream()
            .map(conv -> conv.getUser1().equals(userId) ? conv.getUser2() : conv.getUser1())
            .distinct()
            .toList();

        List<UserProfileResponseProfileGetterDTO> opponentProfiles = userProfileService.getUserProfilesByIds(opponentIds);

        Map<Long, UserProfileResponseProfileGetterDTO> profileMap = opponentProfiles.stream()
            .collect(Collectors.toMap(UserProfileResponseProfileGetterDTO::getUserId, p -> p));

        List<ConversationSummaryDTO> result = new ArrayList<>();

        for (Conversation conversation : conversations) {
            Long opponentId = conversation.getUser1().equals(userId) ? conversation.getUser2() : conversation.getUser1();

            ChatMessage lastMessage = chatMessageRepository
                .findTopByConversationIdOrderByTimestampDesc(conversation.getId());

            UserProfileResponseProfileGetterDTO profile = profileMap.get(opponentId);

            if (profile == null) continue;

            ConversationSummaryDTO dto = new ConversationSummaryDTO();
            dto.setConversationId(conversation.getId());
            dto.setOpponentId(opponentId);
            dto.setOpponentUsername(profile.getUsername());
            dto.setOpponentProfileImage(profile.getProfileImage());
            dto.setLastMessage(lastMessage != null ? lastMessage.getContent() : null);
            dto.setTimestamp(lastMessage != null ? lastMessage.getTimestamp() : null);

            result.add(dto);
        }

        // ✅ Sort by latest timestamp descending
        result.sort((a, b) -> {
            if (a.getTimestamp() == null) return 1;
            if (b.getTimestamp() == null) return -1;
            return b.getTimestamp().compareTo(a.getTimestamp());
        });

        return result;
    }

    
    public List<Long> getConversationIdsForUser(Long userId) {
        return conversationRepository.findConversationIdsByUserId(userId);
    }
    
    
    
    public List<ChatMessage> getMessagesWithCursor(Long conversationId, LocalDateTime before) {
        Pageable pageable = PageRequest.of(0, 20, Sort.by("timestamp").descending());

        if (before == null) {
            return chatMessageRepository.findMessagesWithoutCursor(conversationId, pageable);
        } else {
            return chatMessageRepository.findMessagesBeforeTimestamp(conversationId, before, pageable);
        }
    }


}