package com.dimash.springbank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.security.autoconfigure.UserDetailsServiceAutoConfiguration;

// Auth is handled by JwtFilter; exclude the default in-memory user with its generated password
@SpringBootApplication(exclude = UserDetailsServiceAutoConfiguration.class)
public class SpringbankApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringbankApplication.class, args);
	}

}
