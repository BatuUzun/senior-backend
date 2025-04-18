package com.foodrecipes.credentials.credentials.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.foodrecipes.credentials.credentials.entity.Conversation;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    @Query("SELECT c FROM Conversation c WHERE (c.user1 = :u1 AND c.user2 = :u2) OR (c.user1 = :u2 AND c.user2 = :u1)")
    Optional<Conversation> findByUserPair(@Param("u1") Long u1, @Param("u2") Long u2);
    
    @Query("SELECT c.id FROM Conversation c WHERE c.user1 = :userId OR c.user2 = :userId")
    List<Long> findConversationIdsByUserId(@Param("userId") Long userId);

    List<Conversation> findByUser1OrUser2(Long user1, Long user2);

}
