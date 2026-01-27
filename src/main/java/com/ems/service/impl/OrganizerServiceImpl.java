package com.ems.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.ems.dao.EventDao;
import com.ems.dao.RegistrationDao;
import com.ems.dao.TicketDao;
import com.ems.exception.DataAccessException;
import com.ems.model.Event;
import com.ems.model.OrganizerEventSummary;
import com.ems.model.Ticket;
import com.ems.service.NotificationService;
import com.ems.service.OrganizerService;
import com.ems.service.SystemLogService;

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

	private final EventDao eventDao;
	private final TicketDao ticketDao;
	private final RegistrationDao registrationDao;
	private final NotificationService notificationService;
	private final SystemLogService systemLogService;
	
	public OrganizerServiceImpl(EventDao eventDao, TicketDao ticketDao, RegistrationDao registrationDao,
			NotificationService notificationService, SystemLogService systemLogService) {
		this.eventDao = eventDao;
		this.ticketDao = ticketDao;
		this.registrationDao = registrationDao;
		this.notificationService = notificationService;
		this.systemLogService = systemLogService;
	}

	/*
	 * Creates a new event in DRAFT state.
	 *
	 * Rule: - Newly created events are always saved as DRAFT
	 */
	public int createEvent(Event event) {
		event.setStatus("DRAFT");
		try {

			int eventId = eventDao.createEvent(event);

			systemLogService.log(
			    event.getOrganizerId(),
			    "CREATE",
			    "EVENT",
			    eventId,
			    "Event created in DRAFT state"
			);

			return eventId;

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
			boolean updated = eventDao.updateEventDetails(eventId, title, description, categoryId, venueId);

			if (updated) {
			    systemLogService.log(
			        null,
			        "UPDATE",
			        "EVENT",
			        eventId,
			        "Event details updated"
			    );
			}

			return updated;

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
			boolean updated = eventDao.updateEventSchedule(eventId, start, end);

			if (updated) {
			    systemLogService.log(
			        null,
			        "UPDATE",
			        "EVENT",
			        eventId,
			        "Event schedule updated to " + start + " - " + end
			    );
			}

			return updated;

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
			boolean updated = eventDao.updateEventCapacity(eventId, capacity);

			if (updated) {
			    systemLogService.log(
			        null,
			        "UPDATE",
			        "EVENT",
			        eventId,
			        "Event capacity updated to " + capacity
			    );
			}

			return updated;

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
			boolean published = eventDao.updateEventStatus(eventId, "PUBLISHED");

			if (published) {
			    systemLogService.log(
			        null,
			        "PUBLISH",
			        "EVENT",
			        eventId,
			        "Event published"
			    );
			}

			return published;

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
			boolean cancelled = eventDao.updateEventStatus(eventId, "CANCELLED");

			if (cancelled) {
			    systemLogService.log(
			        null,
			        "CANCEL",
			        "EVENT",
			        eventId,
			        "Event cancelled by organizer"
			    );
			}

			return cancelled;

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
		try {
			ticket.setAvailableQuantity(ticket.getTotalQuantity());
			boolean created = ticketDao.createTicket(ticket);


			if (created) {
			    systemLogService.log(
			        null,
			        "CREATE",
			        "TICKET",
			        ticket.getEventId(),
			        "Ticket type created: " + ticket.getTicketType()
			    );
			}

			return created;
		} catch (DataAccessException e) {
			System.out.println(e.getMessage());
		}
		return false;
	}

	/*
	 * Updates the price of an existing ticket type.
	 */
	public boolean updateTicketPrice(int ticketId, double price) {
		try {
			boolean updated = ticketDao.updateTicketPrice(ticketId, price);

			if (updated) {
			    systemLogService.log(
			        null,
			        "UPDATE",
			        "TICKET",
			        ticketId,
			        "Ticket price updated to ₹" + price
			    );
			}

			return updated;
		} catch (DataAccessException e) {
			System.out.println(e.getMessage());
		}
		return false;
	}

	/*
	 * Updates the total quantity of tickets available.
	 */
	public boolean updateTicketQuantity(int ticketId, int quantity) {
		try {
			boolean updated = ticketDao.updateTicketQuantity(ticketId, quantity);

			if (updated) {
			    systemLogService.log(
			        null,
			        "UPDATE",
			        "TICKET",
			        ticketId,
			        "Ticket quantity updated to " + quantity
			    );
			}

			return updated;
		} catch (DataAccessException e) {
			System.out.println(e.getMessage());
		}
		return false;


	}

	/*
	 * Retrieves current ticket availability for an event.
	 */
	public List<Ticket> viewTicketAvailability(int eventId) {
		try {
			return ticketDao.getTicketsByEvent(eventId);
		} catch (DataAccessException e) {
			System.out.println(e.getMessage());
		}
		return new ArrayList<>();
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
	
	public List<OrganizerEventSummary> getOrganizerEventSummary(int organizerId) {
	    try {
			return eventDao.getEventSummaryByOrganizer(organizerId);
		} catch (DataAccessException e) {
			System.out.println(e.getMessage());
		}
		return new ArrayList<>();
	}

}
