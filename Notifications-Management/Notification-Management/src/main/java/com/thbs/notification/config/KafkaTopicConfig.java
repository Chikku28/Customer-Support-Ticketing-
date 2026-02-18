package com.thbs.notification.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicConfig {
	
	@Value("${kafka.topic.ticket-notifications}")
	private String topicName;
	
	@Bean
	public NewTopic ticketNotificationTopic() {
		return new NewTopic(topicName,1,(short)1);
		
	}
}

