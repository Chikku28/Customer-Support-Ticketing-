package com.thbs.ticket.Entity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.thbs.user.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;


@Entity
@Table(name = "Tickets")
public class Ticket {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long ticketId;
	@Column(nullable = false)
	private String subject;
	@Column(nullable = false, columnDefinition = "TEXT")
	private String description;
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private TicketStatus status = TicketStatus.open;
	
	@ManyToOne
	@JoinColumn(name = "customer_id", nullable = false)
	private User customer;
	
	@ManyToOne
	@JoinColumn(name = "agent_id", nullable = true)
	private User agent;
	
	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;
	
	@Column(nullable = false, updatable = false)
	private LocalDateTime updatedAt;
	
	@PrePersist
	protected void onCreate() {
		createdAt = LocalDateTime.now();
		updatedAt = LocalDateTime.now();
	}
	
	@PreUpdate
	protected void onUpdate() {
		updatedAt = LocalDateTime.now();
	}

	public Ticket() {
		super();
	}

	public Ticket(Long ticketId, String subject, String description, TicketStatus status, User customer, User agent,
			LocalDateTime createdAt, LocalDateTime updatedAt) {
		super();
		this.ticketId = ticketId;
		this.subject = subject;
		this.description = description;
		this.status = status;
		this.customer = customer;
		this.agent = agent;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	public Long getTicketId() {
		return ticketId;
	}

	public void setTicketId(Long ticketId) {
		this.ticketId = ticketId;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public TicketStatus getStatus() {
		return status;
	}

	public void setStatus(TicketStatus status) {
		this.status = status;
	}

	public User getCustomer() {
		return customer;
	}

	public void setCustomer(User customer) {
		this.customer = customer;
	}

	public User getAgent() {
		return agent;
	}

	public void setAgent(User agent) {
		this.agent = agent;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

	@Override
	public String toString() {
		return "Ticket [ticketId=" + ticketId + ", subject=" + subject + ", description=" + description + ", status="
				+ status + ", customer=" + customer + ", agent=" + agent + ", createdAt=" + createdAt + ", updatedAt="
				+ updatedAt + "]";
	}
}
