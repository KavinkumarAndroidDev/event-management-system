package com.ems.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.ems.dao.EventDao;
import com.ems.dao.RegistrationDao;
import com.ems.dao.TicketDao;
import com.ems.dao.impl.EventDaoImpl;
import com.ems.dao.impl.RegistrationDaoImpl;
import com.ems.dao.impl.TicketDaoImpl;
import com.ems.exception.DataAccessException;
import com.ems.model.Event;
import com.ems.model.Ticket;
import com.ems.service.NotificationService;
import com.ems.service.OrganizerService;
import com.ems.util.ApplicationUtil;

/*
 * Handles organizer related business operations.
 *
 * Responsibilities:
 * - Create, update, publish, and cancel events
 * - Manage event schedules, capacity, and tickets
 * - Access registration, sales, and revenue data
 * - Send event related notifications to attendees
 */
public class OrganizerServiceImpl implements OrganizerService {

	private final EventDao eventDao = new EventDaoImpl();
	private final TicketDao ticketDao = new TicketDaoImpl();
	private final RegistrationDao registrationDao = new RegistrationDaoImpl();
	private final NotificationService notificationService = ApplicationUtil.notificationService();

	/*
	 * Creates a new event in DRAFT state.
	 *
	 * Rule: - Newly created events are always saved as DRAFT
	 */
	public int createEvent(Event event) {
		event.setStatus("DRAFT");
		try {
			return eventDao.createEvent(event);
		} catch (DataAccessException e) {
			System.out.println(e.getMessage());
		}
		return 0;
	}

	/*
	 * Updates basic event information.
	 *
	 * Used when organizer edits title, description, category, or venue.
	 */
	public boolean updateEventDetails(int eventId, String title, String description, int categoryId, int venueId) {
		try {
			return eventDao.updateEventDetails(eventId, title, description, categoryId, venueId);
		} catch (DataAccessException e) {
			System.out.println(e.getMessage());
		}
		return false;
	}

	/*
	 * Updates the event start and end schedule.
	 *
	 * Used for rescheduling upcoming events.
	 */
	public boolean updateEventSchedule(int eventId, LocalDateTime start, LocalDateTime end) {
		try {
			return eventDao.updateEventSchedule(eventId, start, end);
		} catch (DataAccessException e) {
			System.out.println(e.getMessage());
		}
		return false;
	}

	/*
	 * Updates the maximum allowed capacity for an event.
	 */
	public boolean updateEventCapacity(int eventId, int capacity) {
		try {
			return eventDao.updateEventCapacity(eventId, capacity);
		} catch (DataAccessException e) {
			System.out.println(e.getMessage());
		}
		return false;
	}

	/*
	 * Publishes an event and makes it visible to users.
	 */
	public boolean publishEvent(int eventId) {
		try {
			return eventDao.updateEventStatus(eventId, "PUBLISHED");
		} catch (DataAccessException e) {
			System.out.println(e.getMessage());
		}
		return false;
	}

	/*
	 * Cancels an existing event.
	 *
	 * Used when an event cannot proceed as planned.
	 */
	public boolean cancelEvent(int eventId) {
		try {
			return eventDao.updateEventStatus(eventId, "CANCELLED");
		} catch (DataAccessException e) {
			System.out.println(e.getMessage());
		}
		return false;
	}

	/*
	 * Creates a ticket type for an event.
	 *
	 * Rule: - Available quantity is initialized to total quantity
	 */
	public boolean createTicket(Ticket ticket) {
		ticket.setAvailableQuantity(ticket.getTotalQuantity());
		return ticketDao.createTicket(ticket);
	}

	/*
	 * Updates the price of an existing ticket type.
	 */
	public boolean updateTicketPrice(int ticketId, double price) {
		return ticketDao.updateTicketPrice(ticketId, price);
	}

	/*
	 * Updates the total quantity of tickets available.
	 */
	public boolean updateTicketQuantity(int ticketId, int quantity) {
		return ticketDao.updateTicketQuantity(ticketId, quantity);
	}

	/*
	 * Retrieves current ticket availability for an event.
	 */
	public List<Ticket> viewTicketAvailability(int eventId) {
		return ticketDao.getTicketsByEvent(eventId);
	}

	/*
	 * Returns the total number of registrations for an event.
	 */
	public int viewEventRegistrations(int eventId) {
		return registrationDao.getEventRegistrationCount(eventId);
	}

	/*
	 * Retrieves all users registered for a specific event.
	 */
	public List<Map<String, Object>> viewRegisteredUsers(int eventId) {
		return registrationDao.getRegisteredUsers(eventId);
	}

	/*
	 * Retrieves registration data grouped by events for an organizer.
	 */
	public List<Map<String, Object>> getEventWiseRegistrations(int organizerId) {
		return registrationDao.getOrganizerWiseRegistrations(organizerId);
	}

	/*
	 * Retrieves all events created by a specific organizer.
	 */
	public List<Event> getOrganizerEvents(int organizerId) {
		try {
			return eventDao.getEventsByOrganizer(organizerId);
		} catch (DataAccessException e) {
			System.out.println(e.getMessage());
		}
		return new ArrayList<>();
	}

	/*
	 * Retrieves ticket sales details for organizer events.
	 */
	public List<Map<String, Object>> getTicketSales(int organizerId) {
		return registrationDao.getTicketSales(organizerId);
	}

	/*
	 * Calculates total revenue generated by an organizer.
	 */
	public double getRevenueSummary(int organizerId) {
		return registrationDao.getRevenueSummary(organizerId);
	}

	/*
	 * Sends a general update notification to all event attendees.
	 */
	public void sendEventUpdate(int eventId, String message) {
		notificationService.sendEventNotification(eventId, message, "EVENT_UPDATE");
	}

	/*
	 * Sends a schedule change notification to all event attendees.
	 */
	public void sendScheduleChange(int eventId, String message) {
		notificationService.sendEventNotification(eventId, message, "SCHEDULE_CHANGE");
	}
}
