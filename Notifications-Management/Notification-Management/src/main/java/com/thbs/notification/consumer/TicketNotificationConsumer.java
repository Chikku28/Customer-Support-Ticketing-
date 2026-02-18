package com.thbs.notification.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.thbs.notification.dto.TicketNotification;

@Service
public class TicketNotificationConsumer {
	

    @KafkaListener(topics = "ticket.created", groupId = "notification-group")
    public void onCreated(TicketNotification n) {
        System.out.println(" [ticket.created] " + n);
    }

    @KafkaListener(topics = "ticket.in_progress", groupId = "notification-group")
    public void onInProgress(TicketNotification n) {
        System.out.println(" [ticket.in_progress] " + n);
    }

    @KafkaListener(topics = "ticket.pending_approval", groupId = "notification-group")
    public void onPendingApproval(TicketNotification n) {
        System.out.println(" [ticket.pending_approval] " + n);
    }

    @KafkaListener(topics = "ticket.resolved", groupId = "notification-group")
    public void onResolved(TicketNotification n) {
        System.out.println(" [ticket.resolved] " + n);
    }
}

