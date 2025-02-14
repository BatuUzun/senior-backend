package com.comment.dto;

public class ReviewCommentUpdateRequestDTO {
    private String newComment;
    private Long commentId;

    public Long getCommentId() {
		return commentId;
	}

	public void setCommentId(Long commentId) {
		this.commentId = commentId;
	}

	// Getters and Setters
    public String getNewComment() {
        return newComment;
    }

    public void setNewComment(String newComment) {
        this.newComment = newComment;
    }

	public ReviewCommentUpdateRequestDTO(String newComment, Long commentId) {
		super();
		this.newComment = newComment;
		this.commentId = commentId;
	}

	public ReviewCommentUpdateRequestDTO() {
		super();
	}
    
    
}
