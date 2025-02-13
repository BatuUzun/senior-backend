package com.userfollow.userfollow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class UserfollowApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserfollowApplication.class, args);
	}

}
