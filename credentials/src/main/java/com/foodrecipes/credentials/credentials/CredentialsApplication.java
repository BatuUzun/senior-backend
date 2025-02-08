package com.foodrecipes.credentials.credentials;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class })
public class CredentialsApplication {

	public static void main(String[] args) {
		SpringApplication.run(CredentialsApplication.class, args);
	}

}
