package com.thbs.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@EnableDiscoveryClient
@SpringBootApplication

public class SupportTicketingApplication {

	public static void main(String[] args) {
		SpringApplication.run(SupportTicketingApplication.class, args);
	}

	@Bean
	@LoadBalanced
	RestTemplate getRestTemplate() {
		return new RestTemplate();
	}

}