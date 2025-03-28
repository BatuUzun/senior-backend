package com.foodrecipes.credentials.credentials.entity;

/*import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;

@Entity
@Table(name = "review_comments")
public class ReviewComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "review_id", insertable = false, updatable = false)
    private Long reviewId;

    @JoinColumn(name = "review_id", nullable = false, foreignKey = @ForeignKey(name = "fk_review"))
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private Review review;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "comment", nullable = false)
    private String comment;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // Constructors
    public ReviewComment() {}

    public ReviewComment(Long id, Review review, Long userId, String comment, LocalDateTime createdAt) {
        this.id = id;
        this.review = review;
        this.userId = userId;
        this.comment = comment;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Review getReview() { return review; }
    public void setReview(Review review) { this.review = review; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}*/
