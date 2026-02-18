package com.thbs.ticket.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.thbs.ticket.Entity.Ticket;
import com.thbs.ticket.Entity.TicketDTO;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long>{

	TicketDTO save(TicketDTO ticket);

}
