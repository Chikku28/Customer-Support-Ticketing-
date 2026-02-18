package com.thbs.ticket.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.thbs.ticket.Entity.Ticket;
import com.thbs.ticket.Entity.TicketDTO;
import com.thbs.ticket.Entity.TicketPatchDto;
import com.thbs.ticket.Service.TicketService;
import com.thbs.user.entity.User;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

	@Autowired
	private TicketService ticketService;
	

	@PostMapping("/ticket")
	public ResponseEntity<TicketDTO> createTicket(@RequestBody TicketDTO ticket, Authentication auth) {
		return new ResponseEntity<>(ticketService.createTicket(ticket), HttpStatus.CREATED);
	}

	@GetMapping
	public ResponseEntity<List<Ticket>> getAllTickets() {
		return ResponseEntity.ok(ticketService.getAllTickets());
	}

	@GetMapping("/{id}")
	public ResponseEntity<Ticket> getTicketById(@PathVariable Long id) {
		return ResponseEntity.ok(ticketService.getTicketById(id));
	}

	
	@PutMapping("/{id}")
	public ResponseEntity<Ticket> updateTicket(@PathVariable Long id, @RequestBody Ticket ticket) {
		return ResponseEntity.ok(ticketService.updateTicket(id, ticket));
	}

	@DeleteMapping("/ticket/{id}")
	public ResponseEntity<String> deleteTicket(@PathVariable Long id) {
		ticketService.deleteTicket(id);
		return ResponseEntity.ok("Ticket deleted successfully");
	}

	@PatchMapping(value = "/{id}", consumes = "application/json", produces = "application/json")
	public ResponseEntity<Ticket> patchTicket(@PathVariable Long id, @RequestBody TicketPatchDto ticketPatch																								
	) {
		Ticket updated = ticketService.patchTicket(id, ticketPatch);
		return ResponseEntity.ok(updated);
	}

	@PatchMapping(value = "/{id}/assign", produces = "application/json")
	public ResponseEntity<Ticket> assignTicket(@PathVariable Long id, @RequestParam User agent_id) {
		Ticket ticket = ticketService.assignTicket(id, agent_id);
		return ResponseEntity.ok(ticket);
	}

	@PatchMapping(value = "/tickets/{id}/requestresolution", produces = "application/json")
	public ResponseEntity<Ticket> requestResolution(@PathVariable Long id) {
		return ResponseEntity.ok(ticketService.requestResolution(id));
	}

	@PatchMapping(value = "/{id}/resolve", produces = "application/json")
	public ResponseEntity<Ticket> resolveTicket(@PathVariable Long id, @RequestParam boolean approved) {
		return ResponseEntity.ok(ticketService.resolveTicket(id, approved));
	}

	@PatchMapping(value = "/{id}/reject", produces = "application/json")
	public ResponseEntity<Ticket> rejectResolution(@PathVariable Long id) {
		return ResponseEntity.ok(ticketService.rejectResolution(id));
	}
}