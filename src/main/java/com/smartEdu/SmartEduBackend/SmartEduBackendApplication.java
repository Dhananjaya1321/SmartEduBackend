package com.smartEdu.SmartEduBackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class SmartEduBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmartEduBackendApplication.class, args);
//		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
//		String newPassword = "admin123";
//		String newHash = encoder.encode(newPassword);
//		System.out.println("New hash: " + newHash);
	}

}
