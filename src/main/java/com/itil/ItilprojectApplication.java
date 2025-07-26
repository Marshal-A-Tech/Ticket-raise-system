package com.itil;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.itil")
public class ItilprojectApplication {

	public static void main(String[] args) {
		SpringApplication.run(ItilprojectApplication.class, args);
	}

}
