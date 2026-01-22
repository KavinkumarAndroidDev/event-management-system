package com.ems.dao;

import java.util.List;

import com.ems.exception.DataAccessException;
import com.ems.model.Ticket;

public interface TicketDao {

	int getAvailableTickets(int eventId)  throws DataAccessException;

	List<Ticket> getTicketTypes(int eventId) throws DataAccessException;

	Ticket getTicketById(int ticketId)  throws DataAccessException;

	void updateAvailableQuantity(int ticketId, int i)  throws DataAccessException;
}
