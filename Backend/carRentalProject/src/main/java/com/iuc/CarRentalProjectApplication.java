package com.iuc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.iuc.mapper", "com.iuc"})
public class CarRentalProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(CarRentalProjectApplication.class, args);
	}

}
