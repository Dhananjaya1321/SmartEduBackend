package com.smartEdu.SmartEduBackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class SmartEduBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmartEduBackendApplication.class, args);
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		String rawPassword = "admin123";
		String hashedPassword = "$2a$12$6Yh36Au5spIb3mdnZueJVuxkUwwhqdu2w9F40toHesDvb7aaoUKdS";
		System.out.println(encoder.matches(rawPassword, hashedPassword));

		BCryptPasswordEncoder encoder1 = new BCryptPasswordEncoder();
		System.out.println(encoder1.encode("admin123"));
	}

}
