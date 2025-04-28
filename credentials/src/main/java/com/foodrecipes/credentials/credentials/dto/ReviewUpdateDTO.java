package com.foodrecipes.credentials.credentials.dto;

public class ReviewUpdateDTO {
    private Long id;
    private double rating;
    private String comment;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

	public ReviewUpdateDTO(Long id, double rating, String comment) {
		super();
		this.id = id;
		this.rating = rating;
		this.comment = comment;
	}

	public ReviewUpdateDTO() {
		super();
	}
    
}

