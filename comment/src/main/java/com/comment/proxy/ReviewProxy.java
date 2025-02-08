package com.comment.proxy;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.comment.entity.Review;

@FeignClient(name = "review")
public interface ReviewProxy {
	@GetMapping("/review/get-review/{id}")
    Review getReviewById(@PathVariable("id") Long id);
}
