package com.neobank.neobankaccountservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class NeobankAccountServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(NeobankAccountServiceApplication.class, args);
	}

}
