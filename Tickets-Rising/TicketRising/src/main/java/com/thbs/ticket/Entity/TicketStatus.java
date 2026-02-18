package com.thbs.ticket.Entity;

public enum TicketStatus {
	open,
	in_progress,
	pending_approval,
	resolved;

	public boolean isBlank() {
		// TODO Auto-generated method stub
		return false;
	}
}
