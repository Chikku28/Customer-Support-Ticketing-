package com.thbs.ticket.events;

public class TicketNotification {
	
	private Long ticketId;
	private String message;
	
	public TicketNotification() {
	}


	public TicketNotification(Long ticketId, String message) {
		super();
		this.ticketId = ticketId;
		this.message = message;
	}


	public Long getTicketId() {
		return ticketId;
	}


	public void setTicketId(Long ticketId) {
		this.ticketId = ticketId;
	}


	public String getMessage() {
		return message;
	}


	public void setMessage(String message) {
		this.message = message;
	}
}
