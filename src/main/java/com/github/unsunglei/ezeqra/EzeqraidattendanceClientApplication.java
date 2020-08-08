package com.github.unsunglei.ezeqra;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class EzeqraidattendanceClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(EzeqraidattendanceClientApplication.class, args);
	}

}
