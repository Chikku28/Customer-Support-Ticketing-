package com.thbs.ticket.mapper;

import com.thbs.ticket.Entity.Ticket;
import com.thbs.ticket.Entity.TicketDTO;
import com.thbs.ticket.Entity.TicketStatus;
import com.thbs.user.entity.User;

public class TicketMapper {

    public static Ticket toEntity(TicketDTO dto, User customer, User agent) {
        Ticket t = new Ticket();
        t.setSubject(dto.getSubject());
        t.setDescription(dto.getDescription());
        t.setStatus(dto.getStatus() == null ? TicketStatus.open : dto.getStatus());
        t.setCustomer(customer);
        return t;
    }

    public static TicketDTO toDTO(Ticket t) { 
        TicketDTO dto = new TicketDTO();
        dto.setTicketId(t.getTicketId());
        dto.setSubject(t.getSubject());
        dto.setDescription(t.getDescription());
        dto.setStatus(t.getStatus());
        dto.setCustomerId(t.getCustomer() != null ? t.getCustomer().getId() : null);
        dto.setCreatedAt(t.getCreatedAt());
        return dto;
    }
}