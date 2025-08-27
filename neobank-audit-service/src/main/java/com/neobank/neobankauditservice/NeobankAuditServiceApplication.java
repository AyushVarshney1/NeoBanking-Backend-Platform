package com.neobank.neobankauditservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class NeobankAuditServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(NeobankAuditServiceApplication.class, args);
    }

}
