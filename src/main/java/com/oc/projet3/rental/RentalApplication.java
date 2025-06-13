package com.oc.projet3.rental;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RentalApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(RentalApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		System.out.println("Hello world !");
	}

}
