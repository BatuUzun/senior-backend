package com.chat_search;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ChatSearchApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChatSearchApplication.class, args);
	}

}
