package com.thbs.ticket.events;

import org.springframework.beans.factory.annotation.Autowired; 
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;


@Service
public class TicketNotificationProducer {
	
//	@Value("${kafka.topic.ticket-notifications}")
    private String topicName;
 
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;
 
    public void sendNotification(TicketNotification notification) {
        kafkaTemplate.send(topicName, notification);
        
        System.out.println(" Sent notification to kafka for Ticket ID:" + notification.getTicketId());
    }
    

    public void sendCreated(TicketNotification n) {
        send("ticket.created", n.getTicketId(), n);
    }

    public void sendInProgress(TicketNotification n) {
        send("ticket.in_progress", n.getTicketId(), n);
    }

    public void sendPendingApproval(TicketNotification n) {
        send("ticket.pending_approval", n.getTicketId(), n);
    }

    public void sendResolved(TicketNotification n) {
        send("ticket.resolved", n.getTicketId(), n);
    }

    private void send(String topic, Long key, Object payload) {
        String keyStr = key == null ? null : key.toString();
        kafkaTemplate.send(topic, keyStr, payload)
            .whenComplete((res, ex) -> {
                if (ex != null) {
                    System.err.printf("❌ Kafka send failed topic=%s key=%s error=%s%n",
                            topic, keyStr, ex.getMessage());
                } else {
                    var md = res.getRecordMetadata();
                    System.out.printf("✅ Sent topic=%s partition=%d offset=%d key=%s%n",
                            md.topic(), md.partition(), md.offset(), keyStr);
                }
            });
    }
}

