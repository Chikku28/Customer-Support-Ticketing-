package com.thbs.notification.dto;

public class TicketNotification {
	
	private long ticketId;
	private String message;
	
	
	public TicketNotification() {
		super();
	}


	public TicketNotification(long ticketId, String message) {
		super();
		this.ticketId = ticketId;
		this.message = message;
	}


	public long getTicketId() {
		return ticketId;
	}


	public void setTicketId(long ticketId) {
		this.ticketId = ticketId;
	}


	public String getMessage() {
		return message;
	}


	public void setMessage(String message) {
		this.message = message;
	}


	@Override
	public String toString() {
		return "TicketNotification [ticketId=" + ticketId + ", message=" + message + "]";
	}
}