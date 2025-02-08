package com.chat_send;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class ChatSendApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChatSendApplication.class, args);
	}

}
