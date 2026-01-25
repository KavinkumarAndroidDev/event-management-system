package com.ems.dao;

import java.util.List;

import com.ems.exception.DataAccessException;
import com.ems.model.Ticket;

public interface TicketDao {

	int getAvailableTickets(int eventId)  throws DataAccessException;

	List<Ticket> getTicketTypes(int eventId) throws DataAccessException;

	Ticket getTicketById(int ticketId)  throws DataAccessException;

	boolean updateAvailableQuantity(int ticketId, int i)  throws DataAccessException;

	//organizer functions
    boolean createTicket(Ticket ticket);

    boolean updateTicketPrice(int ticketId, double price);

    boolean updateTicketQuantity(int ticketId, int quantity);

    List<Ticket> getTicketsByEvent(int eventId);
}
