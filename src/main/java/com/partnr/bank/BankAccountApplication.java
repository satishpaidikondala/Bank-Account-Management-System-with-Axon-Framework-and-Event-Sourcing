package com.partnr.bank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = {
    "com.partnr.bank",
    "org.axonframework.eventsourcing.eventstore.jpa",
    "org.axonframework.eventhandling.tokenstore.jpa"
})
public class BankAccountApplication {

    public static void main(String[] args) {
        SpringApplication.run(BankAccountApplication.class, args);
    }
}
