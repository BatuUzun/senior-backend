package com.conversation.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "conversations", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"user1", "user2"}))
public class Conversation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long user1;
    private Long user2;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public Conversation() {}

    public Conversation(Long user1, Long user2) {
        this.user1 = user1;
        this.user2 = user2;
        this.createdAt = LocalDateTime.now();  // Automatically set timestamp
    }

    public Long getId() {
        return id;
    }

    public Long getUser1() {
        return user1;
    }

    public Long getUser2() {
        return user2;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
