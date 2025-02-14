package com.comment.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.comment.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    
}
