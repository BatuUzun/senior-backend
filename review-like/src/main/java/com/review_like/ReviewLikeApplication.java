package com.review_like;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class ReviewLikeApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReviewLikeApplication.class, args);
	}

}
