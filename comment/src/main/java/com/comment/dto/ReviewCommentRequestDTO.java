package com.comment.dto;

public class ReviewCommentRequestDTO {
    private Long reviewId;
    private Long userId;
    private String comment;

    // Getters and Setters
    public Long getReviewId() {
        return reviewId;
    }

    public void setReviewId(Long reviewId) {
        this.reviewId = reviewId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

	public ReviewCommentRequestDTO(Long reviewId, Long userId, String comment) {
		super();
		this.reviewId = reviewId;
		this.userId = userId;
		this.comment = comment;
	}

	public ReviewCommentRequestDTO() {
		super();
	}
    
    
}