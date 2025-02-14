package com.review.entity.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.review.entity.ReviewComment;

public interface ReviewCommentRepository extends JpaRepository<ReviewComment, Long> {    
	
	
	@Query("""
		    SELECT r.spotifyId, r.id, COALESCE(COUNT(rc.id), 0) 
		    FROM Review r
		    LEFT JOIN ReviewComment rc ON r.id = rc.review.id
		    GROUP BY r.spotifyId, r.id
		""")
		List<Object[]> findCommentCountsBySpotifyId();







}