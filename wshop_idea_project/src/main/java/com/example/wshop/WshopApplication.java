package com.example.wshop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan("com.example.wshop.model")
public class WshopApplication {

	public static void main(String[] args) {
		SpringApplication.run(WshopApplication.class, args);
	}

}
