package com.chat_search.repository;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import com.chat_search.entity.ChatMessageIndex;

public interface ChatMessageRepository extends ElasticsearchRepository<ChatMessageIndex, String> {

    // Fetch latest 20 messages (initial load)
    List<ChatMessageIndex> findByConversationIdOrderBySentAtDesc(Long conversationId, Pageable pageable);

    // Query when `lastSentAt` is NOT null
    @Query("{ \"bool\": { \"must\": [ { \"match_phrase_prefix\": { \"message\": \"?0\" } }, "
             + "{ \"term\": { \"conversationId\": \"?1\" } }, { \"range\": { \"sentAt\": { \"lt\": \"?2\" } } } ] } }")
    List<ChatMessageIndex> searchMessagesInConversationWithDate(String keyword, Long conversationId, String lastSentAt, Pageable pageable);

    // Query when `lastSentAt` is NULL (removes `range` filter)
    @Query("{ \"bool\": { \"must\": [ { \"match_phrase_prefix\": { \"message\": \"?0\" } }, "
             + "{ \"term\": { \"conversationId\": \"?1\" } } ] } }")
    List<ChatMessageIndex> searchMessagesInConversationWithoutDate(String keyword, Long conversationId, Pageable pageable);

    
    // Fetch next 20 messages after a given timestamp (cursor-based pagination)
    List<ChatMessageIndex> findByConversationIdAndSentAtLessThanOrderBySentAtDesc(Long conversationId, String lastSentAt, Pageable pageable);

    @Query("""
    	    {
    	      "bool": {
    	        "should": [
    	          { "term": { "senderId": "?0" } },
    	          { "term": { "receiverId": "?0" } }
    	        ]
    	      }
    	    }
    	""")
    	List<ChatMessageIndex> findMessagesByUserId(Long userId, Pageable pageable);


    
    @Query("{ \"bool\": { \"must\": [ { \"match_phrase_prefix\": { \"message\": \"?0\" } }, "
            + "{ \"bool\": { \"should\": [ { \"term\": { \"senderId\": \"?1\" } }, { \"term\": { \"receiverId\": \"?1\" } } ] } }, "
            + "{ \"range\": { \"sentAt\": { \"lt\": \"?2\" } } } ] } }")
   List<ChatMessageIndex> searchGlobalMessagesWithDate(String keyword, Long userId, String lastSentAt, Pageable pageable);

    @Query("{ \"bool\": { \"must\": [ { \"match_phrase_prefix\": { \"message\": \"?0\" } }, "
            + "{ \"bool\": { \"should\": [ { \"term\": { \"senderId\": \"?1\" } }, { \"term\": { \"receiverId\": \"?1\" } } ] } } ] } }")
   List<ChatMessageIndex> searchGlobalMessagesWithoutDate(String keyword, Long userId, Pageable pageable);

}
