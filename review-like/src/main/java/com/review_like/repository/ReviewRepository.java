package com.review_like.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.review_like.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    
}
