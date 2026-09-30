package com.gustavorodrigues.user_management_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing 
@SpringBootApplication
public class UserManagementApiApplication {

	public static void main(String[] args) {
		
		SpringApplication.run(UserManagementApiApplication.class, args);
	}

}
