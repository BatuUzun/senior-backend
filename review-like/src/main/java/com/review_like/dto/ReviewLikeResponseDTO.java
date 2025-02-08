package com.review_like.dto;

import org.springframework.http.HttpStatus;

public class ReviewLikeResponseDTO {
    private boolean success;
    private String message;
    private Object data;
    private HttpStatus status;

    public ReviewLikeResponseDTO(boolean success, String message, Object data, HttpStatus status) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.status = status;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }
}
