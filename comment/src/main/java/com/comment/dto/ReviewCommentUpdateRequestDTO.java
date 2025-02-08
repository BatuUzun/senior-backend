package com.comment.dto;

public class ReviewCommentUpdateRequestDTO {
    private String newComment;

    // Getters and Setters
    public String getNewComment() {
        return newComment;
    }

    public void setNewComment(String newComment) {
        this.newComment = newComment;
    }

	public ReviewCommentUpdateRequestDTO(String newComment) {
		super();
		this.newComment = newComment;
	}

	public ReviewCommentUpdateRequestDTO() {
		super();
	}
    
    
}
