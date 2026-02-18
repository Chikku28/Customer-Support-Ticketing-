package com.thbs.ticket.Entity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import jakarta.persistence.PrePersist;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class TicketDTO {
	
	private Long ticketId;
	
    @NotBlank(message = "subject is required")
    private String subject;

    @NotBlank(message = "description is required")
    private String description;
    
    
    @NotNull(message = "status is required")
    private TicketStatus status;

    @NotNull(message = "customerId is required")
    private Long customerId;
    
    @JsonProperty(access = Access.READ_ONLY)
    private LocalDateTime createdAt;
    
	
	protected void onCreate() {
		createdAt = LocalDateTime.now();
	}

	public TicketDTO() {
		super();
	}

	public TicketDTO(Long ticketId, @NotBlank(message = "subject is required") String subject,
			@NotBlank(message = "description is required") String description,
			@NotNull(message = "status is required") TicketStatus status,
			@NotNull(message = "customerId is required") Long customerId, LocalDateTime createdAt) {
		super();
		this.ticketId = ticketId;
		this.subject = subject;
		this.description = description;
		this.status = status;
		this.customerId = customerId;
		this.createdAt = createdAt;
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

	public Long getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	@Override
	public String toString() {
		return "TicketDTO [ticketId=" + ticketId + ", subject=" + subject + ", description=" + description + ", status="
				+ status + ", customerId=" + customerId + ", createdAt=" + createdAt + "]";
	}
}
