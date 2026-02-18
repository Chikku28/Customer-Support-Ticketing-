// src/main/java/com/thbs/ticket/Service/TicketService.java
package com.thbs.ticket.Service;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.thbs.ticket.Entity.Ticket;
import com.thbs.ticket.Entity.TicketDTO;
import com.thbs.ticket.Entity.TicketPatchDto;
import com.thbs.ticket.Entity.TicketStatus;
import com.thbs.ticket.Exceptions.TicketNotFoundException;
import com.thbs.ticket.Repository.TicketRepository;
import com.thbs.ticket.events.TicketNotification;
import com.thbs.ticket.events.TicketNotificationProducer;
import com.thbs.ticket.mapper.TicketMapper;
import com.thbs.user.Repository.UserRepository;
import com.thbs.user.entity.User;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.bind.annotation.PathVariable;

@Service
public class TicketService {
	@Autowired
	private TicketRepository ticketRepo;
	@Autowired
	private UserRepository userRepo; // <-- add this (or your correct user repo bean)
	@Autowired
	private TicketNotificationProducer ticketNotificationProducer;

	@Transactional
	public TicketDTO createTicket(TicketDTO ticketDto) {

		User customer = userRepo.findById(ticketDto.getCustomerId())
				.orElseThrow(() -> new EntityNotFoundException("Customer not found: " + ticketDto.getCustomerId()));

		if (ticketDto.getStatus() == null) {
			ticketDto.setStatus(TicketStatus.open);
		}

		Ticket entity = TicketMapper.toEntity(ticketDto, customer, customer);

		Ticket saved = ticketRepo.save(entity);

		TicketNotification n = new TicketNotification();
		n.setTicketId(saved.getTicketId());
		n.setMessage("Ticket created");
		ticketNotificationProducer.sendCreated(n);

		return TicketMapper.toDTO(saved);
	}

	public Ticket getTicketById(Long id) {
		return ticketRepo.findById(id).orElseThrow(() -> new TicketNotFoundException("Ticket not found"));
	}

	public List<Ticket> getAllTickets() {
		return ticketRepo.findAll();
	}

	@Transactional
	public Ticket updateTicket(Long id, Ticket ticket) {
		Ticket existingTicket = getTicketById(id);
		existingTicket.setStatus(ticket.getStatus());
		existingTicket.setSubject(ticket.getSubject());
		existingTicket.setAgent(ticket.getAgent());
		existingTicket.setCreatedAt(ticket.getCreatedAt());
		existingTicket.setUpdatedAt(ticket.getUpdatedAt());
		return ticketRepo.save(existingTicket);
	}

	@Transactional
	public void deleteTicket(Long id) {
		Ticket ticket = getTicketById(id);
		ticketRepo.delete(ticket);
	}

//@Transactional
//public Ticket assignTicket1(Long id, Long agentId) {
//    Ticket ticket = ticketRepo.findById(id)
//        .orElseThrow(() -> new TicketNotFoundException("Ticket not found with id: " + id));
//
//    User agent = userRepo.findById(agentId)
//        .orElseThrow(() -> new EntityNotFoundException("Agent not found: " + agentId));

	// Optional: validate the role is AGENT for safety (depends on your model)
	// if (!agent.getRole().equalsIgnoreCase("AGENT")) { throw new
	// IllegalArgumentException("User is not an agent"); }

	// Idempotency: If already assigned to same agent, no-op
//    if (ticket.getAgent() == null || !ticket.getAgent().getId().equals(agent.getId())) {
//        ticket.setAgent(agent);
//        ticket.setStatus(TicketStatus.in_progress);
//        ticket.setUpdatedAt(LocalDateTime.now());
//    }

//    TicketNotification notification = new TicketNotification();
//    notification.setTicketId(updatedTicket.getId());
//    notification.setMessage("Ticket pending manager approval");
//    ticketNotificationProducer.sendNotification(notification);
//    return ticket;
//}

	public Ticket assignTicket(@PathVariable Long id, User agentId) {
		Ticket ticket = ticketRepo.findById(id)
				.orElseThrow(() -> new RuntimeException("Ticket not found with id: " + id));
		ticket.setAgent(agentId);
		ticket.setStatus(TicketStatus.in_progress);
		ticket.setUpdatedAt(LocalDateTime.now());

		Ticket updatedTicket = ticketRepo.save(ticket);

		TicketNotification notification = new TicketNotification();
		notification.setTicketId(updatedTicket.getTicketId());
		notification.setMessage("Ticket assigned to agent:" + agentId);

		ticketNotificationProducer.sendInProgress(notification);

		return updatedTicket;
	}

	public Ticket requestResolution(Long id) {
		Ticket ticket = ticketRepo.findById(id)
				.orElseThrow(() -> new RuntimeException("Ticket not found with id: " + id));
		ticket.setStatus(TicketStatus.pending_approval);
		ticket.setUpdatedAt(LocalDateTime.now());
		Ticket updatedTicket = ticketRepo.save(ticket);
		TicketNotification notification = new TicketNotification();
		notification.setTicketId(updatedTicket.getTicketId());
		notification.setMessage("Ticket pending manager approval");
		ticketNotificationProducer.sendInProgress(notification);
		return updatedTicket;
	}

	@Transactional
	public Ticket resolveTicket(Long id, boolean approved) { 
		Ticket ticket = ticketRepo.findById(id)
				.orElseThrow(() -> new RuntimeException("Ticket not found with id: " + id));
		if (approved) {
			ticket.setStatus(TicketStatus.resolved);
		} else {
			ticket.setStatus(TicketStatus.in_progress);
		}
		ticket.setUpdatedAt(LocalDateTime.now());
		Ticket updatedTicket = ticketRepo.save(ticket);
		TicketNotification notification = new TicketNotification();
		notification.setTicketId(updatedTicket.getTicketId());
		notification.setMessage(approved ? "Ticket resolved by manager" : "Ticket resolution rejected by manager");

	    TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
	        @Override
	        public void afterCommit() {
	            if (approved) {
	                ticketNotificationProducer.sendResolved(notification);        
	            } else {
	                ticketNotificationProducer.sendInProgress(notification);      
	            }
	        }
	    });

		return updatedTicket;
	}

	public Ticket rejectResolution(Long id) {
		Ticket ticket = ticketRepo.findById(id)
				.orElseThrow(() -> new RuntimeException("Ticket not found with id: " + id));
		ticket.setStatus(TicketStatus.pending_approval);
		ticket.setUpdatedAt(LocalDateTime.now());
		Ticket updatedTicket = ticketRepo.save(ticket);
		TicketNotification notification = new TicketNotification();
		notification.setTicketId(updatedTicket.getTicketId());
		notification.setMessage("Ticket back to in-progress");
		ticketNotificationProducer.sendInProgress(notification);
		return updatedTicket;
	}

	@Transactional
	public Ticket patchTicket(Long id, TicketPatchDto dto) {
		Ticket existing = ticketRepo.findById(id)
				.orElseThrow(() -> new TicketNotFoundException("Ticket not found with id: " + id));

		if (dto.getSubject() != null) {
			existing.setSubject(dto.getSubject());
		}
		if (dto.getDescription() != null) {
			existing.setDescription(dto.getDescription());
		}
		if (dto.getStatus() != null) {
			existing.setStatus(dto.getStatus());
		}
		if (dto.getAgentId() != null) {
			User agent = userRepo.findById(dto.getAgentId())
					.orElseThrow(() -> new EntityNotFoundException("Agent not found: " + dto.getAgentId()));
			existing.setAgent(agent);
		}
		if (dto.getCustomerId() != null) {
			User customer = userRepo.findById(dto.getCustomerId())
					.orElseThrow(() -> new EntityNotFoundException("Customer not found: " + dto.getCustomerId()));
			existing.setCustomer(customer);
		}

		
		existing.setUpdatedAt(LocalDateTime.now()); 

		return existing; 
	}
}
