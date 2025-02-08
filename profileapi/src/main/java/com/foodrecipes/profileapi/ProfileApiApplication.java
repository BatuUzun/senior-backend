package com.foodrecipes.profileapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.foodrecipes.profileapi")
public class ProfileApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProfileApiApplication.class, args);
	}

}
