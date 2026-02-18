package com.thbs.ticket;

import org.springframework.boot.SpringApplication;  
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
@EnableDiscoveryClient
@SpringBootApplication
@EntityScan(basePackages = {
	    "com.thbs.user.entity",
	    "com.thbs.ticket.Entity"
})
@EnableJpaRepositories(basePackages = {
    "com.thbs.ticket.Repository",
    "com.thbs.user.Repository"
})

public class TicketRisingApplication {

	public static void main(String[] args) {
		SpringApplication.run(TicketRisingApplication.class, args);
	}
}
