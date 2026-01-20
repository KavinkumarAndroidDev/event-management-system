package com.ems.dao;

import java.util.List;

import com.ems.model.Ticket;

public interface TicketDao {

	int getAvailableTickets(int eventId);

	List<Ticket> getTicketTypes(int eventId);
}
