package com.ems.service.impl;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.ems.dao.*;
import com.ems.enums.PaymentMethod;
import com.ems.exception.DataAccessException;
import com.ems.model.BookingDetail;
import com.ems.model.Event;
import com.ems.model.Ticket;
import com.ems.model.Category;
import com.ems.model.UserEventRegistration;
import com.ems.model.Venue;
import com.ems.service.EventService;
import com.ems.service.PaymentService;
import com.ems.service.SystemLogService;
import com.ems.util.DateTimeUtil;

/*
 * Handles event related business operations.
 *
 * Responsibilities:
 * - Event discovery and filtering
 * - Event registration and booking flow
 * - Venue, category, and ticket lookups
 * - Feedback submission
 */

public class EventServiceImpl implements EventService {

	private final EventDao eventDao;
	private final CategoryDao categoryDao;
	private final VenueDao venueDao;
	private final TicketDao ticketDao;
	private final PaymentService paymentService;
	private final FeedbackDao feedbackDao;
	private final SystemLogService systemLogService;

	/*
	 * Initializes EventService with required data access and payment dependencies.
	 */
	
	public EventServiceImpl(EventDao eventDao, CategoryDao categoryDao, VenueDao venueDao, TicketDao ticketDao,
			PaymentService paymentService, FeedbackDao feedbackDao, SystemLogService systemLogService) {
		this.eventDao = eventDao;
		this.categoryDao = categoryDao;
		this.venueDao = venueDao;
		this.ticketDao = ticketDao;
		this.paymentService = paymentService;
		this.feedbackDao = feedbackDao;
		this.systemLogService = systemLogService;
	}

	/*
	 * Retrieves all available ticket types for a given event.
	 */
	@Override
	public List<Ticket> getTicketTypes(int eventId) {
		List<Ticket> tickets = new ArrayList<>();
		try {
			tickets = ticketDao.getTicketTypes(eventId);
		} catch (DataAccessException e) {
			System.out.println(e.getMessage());
		}
		return tickets;
	}

	/*
	 * Filters events based on ticket price range.
	 *
	 * Rule: - Event is included if any ticket falls within the price range
	 */
	@Override
	public List<Event> filterByPrice(double minPrice, double maxPrice) {

		List<Event> filteredEvents = new ArrayList<>();
		try {
			List<Event> allEvents = eventDao.listAvailableEvents();

			filteredEvents = allEvents.stream().filter(event -> {
				List<Ticket> tickets = new ArrayList<>();
				try {
					tickets = ticketDao.getTicketTypes(event.getEventId());
				} catch (DataAccessException e) {
					System.out.println(e.getMessage());
				}

				return tickets.stream().anyMatch(t -> t.getPrice() >= minPrice && t.getPrice() <= maxPrice);
			}).collect(Collectors.toList());
			return filteredEvents;

		} catch (DataAccessException e) {
			System.out.println("Database error: " + e.getMessage());
		}
		return filteredEvents;
	}

	/*
	 * Retrieves a single event by its identifier.
	 */
	@Override
	public Event getEventById(int eventId) {
		Event event = new Event();
		try {
			event = eventDao.getEventById(eventId);
		} catch (DataAccessException e) {
			System.out.println("Database error: " + e.getMessage());
		}
		return event;
	}

	/*
	 * Searches events based on venue location.
	 */
	@Override
	public List<Event> searchByCity(int venueId) {
		List<Event> filteredEvents = new ArrayList<>();
		try {
			List<Event> allEvents = eventDao.listAvailableEvents();
			filteredEvents = allEvents.stream().filter(e -> e.getVenueId() == venueId).collect(Collectors.toList());
			return filteredEvents;
		} catch (DataAccessException e) {
			System.out.println("Database error: " + e.getMessage());
		}
		return filteredEvents;
	}

	/*
	 * Searches events occurring before the given date.
	 */
	@Override
	public List<Event> searchByDate(LocalDate localDate) {
		List<Event> filteredEvents = new ArrayList<>();
		try {
			List<Event> allEvents = eventDao.listAvailableEvents();
			filteredEvents = allEvents.stream().filter(e -> e.getStartDateTime().toLocalDate().isBefore(localDate))
					.collect(Collectors.toList());
		} catch (DataAccessException e) {
			System.out.println("Database error: " + e.getMessage());
		}
		return filteredEvents;
	}

	/*
	 * Searches events within a specified date range.
	 *
	 * Rule: - Start date must be before or equal to end date
	 */
	@Override
	public List<Event> searchByDateRange(LocalDate startDate, LocalDate endDate) {
		List<Event> filteredEvents = new ArrayList<>();
		if (startDate.isAfter(endDate)) {
			System.out.println("Error: Start date cannot be after end date.");
			return filteredEvents;
		}

		try {
			List<Event> allEvents = eventDao.listAvailableEvents();

			filteredEvents = allEvents.stream().filter(e -> {
				LocalDate eventDate = e.getStartDateTime().toLocalDate();
				return !eventDate.isBefore(startDate) && !eventDate.isAfter(endDate);
			}).collect(Collectors.toList());

			return filteredEvents;
		} catch (DataAccessException e) {
			System.out.println("Database error: " + e.getMessage());
		}
		return filteredEvents;
	}

	/*
	 * Searches events by category.
	 */
	@Override
	public List<Event> searchBycategory(int selectedCategoryId) {
		List<Event> filteredEvents = new ArrayList<>();

		try {
			List<Event> allEvents = eventDao.listAvailableEvents();
			filteredEvents = allEvents.stream().filter(e -> e.getCategoryId() == selectedCategoryId)
					.collect(Collectors.toList());

		} catch (DataAccessException e) {
			System.out.println("Database error: " + e.getMessage());
		}
		return filteredEvents;

	}

	/*
	 * Registers a user for an event and initiates payment.
	 *
	 * Rule: - Registration succeeds only if payment is successful
	 */
	@Override
	public boolean registerForEvent(
	        int userId,
	        int eventId,
	        int ticketId,
	        int quantity,
	        double price,
	        PaymentMethod paymentMethod,
	        String offerCode) {

	    try {
	    	boolean success = paymentService.processRegistration(
	    		    userId,
	    		    eventId,
	    		    ticketId,
	    		    quantity,
	    		    price,
	    		    paymentMethod,
	    		    offerCode
	    		);

	    		if (success) {
	    		    systemLogService.log(
	    		        userId,
	    		        "REGISTER",
	    		        "EVENT",
	    		        eventId,
	    		        "User registered for event using ticket " + ticketId +
	    		        (offerCode != null ? " with offer code " + offerCode : "")
	    		    );
	    		}

	    		return success;

	    } catch (Exception e) {
	        System.out.println("Error during registration: " + e.getMessage());
	        return false;
	    }
	}

	/*
	 * Retrieves upcoming events for a user.
	 */
	@Override
	public List<UserEventRegistration> viewUpcomingEvents(int userId) {
		List<UserEventRegistration> upcoming = new ArrayList<>();
		try {
			List<UserEventRegistration> registrations = eventDao.getUserRegistrations(userId);

			upcoming = registrations.stream().filter(r -> r.getStartDateTime().isAfter(LocalDateTime.now())).toList();

		} catch (DataAccessException e) {
			System.out.println(e.getMessage());
		}
		return upcoming;
	}

	/*
	 * Retrieves past events attended by a user.
	 */
	@Override
	public List<UserEventRegistration> viewPastEvents(int userId) {
		List<UserEventRegistration> past = new ArrayList<>();
		try {
			List<UserEventRegistration> registrations = eventDao.getUserRegistrations(userId);
			past = registrations.stream().filter(r -> r.getStartDateTime().isBefore(LocalDateTime.now())).toList();

		} catch (DataAccessException e) {
			System.out.println(e.getMessage());
		}
		return past;
	}

	/*
	 * Retrieves booking details for a user's registrations.
	 */
	@Override
	public List<BookingDetail> viewBookingDetails(int userId) {
		List<BookingDetail> bookings = new ArrayList<>();
		try {
			bookings = eventDao.viewBookingDetails(userId);
			return bookings;

		} catch (DataAccessException e) {
			System.out.println(e.getMessage());
		}
		return bookings;
	}

	/*
	 * Submits user feedback for a completed event.
	 */
	@Override
	public void submitRating(int userId, int eventId, int rating, String comments) {
		try {
			if (comments.trim().isBlank()) {
				comments = null;
			}
			feedbackDao.submitRating(eventId, userId, rating, comments);

			systemLogService.log(
			    userId,
			    "SUBMIT_FEEDBACK",
			    "EVENT",
			    eventId,
			    "User submitted rating: " + rating
			);

		} catch (DataAccessException e) {
			System.out.println(e.getMessage());
		}
	}

	/*
	 * Retrieves all events regardless of status.
	 */
	@Override
	public List<Event> getAllEvents() {
		List<Event> events = new ArrayList<>();
		try {
			events = eventDao.listAllEvents();
		} catch (DataAccessException e) {
			System.out.println(e.getMessage());
		}
		if (events.isEmpty()) {
			System.out.println("There are no events!");
			return events;
		}
		return events;
	}

	/*
	 * Retrieves category details for an event.
	 */
	@Override
	public Category getCategory(int categoryId) {
		Category category = null;
		try {
			category = categoryDao.getCategory(categoryId);
		} catch (DataAccessException e) {
			System.out.println(e.getMessage());
		}
		return category;
	}

	/*
	 * Returns total available tickets for an event.
	 */
	@Override
	public int getAvailableTickets(int eventId) {
		try {
			return ticketDao.getAvailableTickets(eventId);
		} catch (DataAccessException e) {
			System.out.println(e.getMessage());
		}
		return 0;

	}

	/*
	 * Retrieves venue name for display.
	 */
	@Override
	public String getVenueName(int venueId) {
		try {
			return venueDao.getVenueName(venueId);
		} catch (DataAccessException e) {
			System.out.println(e.getMessage());
		}
		return "";
	}

	/*
	 * Retrieves venue address for display.
	 */
	@Override
	public String getVenueAddress(int venueId) {
		try {
			return venueDao.getVenueAddress(venueId);
		} catch (DataAccessException e) {
			System.out.println(e.getMessage());
		}
		return "";
	}

	/*
	 * Retrieves all available categories.
	 */
	@Override
	public List<Category> getAllCategory() {
		List<Category> categories = new ArrayList<>();
		try {
			categories = categoryDao.getAllCategories();
			return categories;
		} catch (DataAccessException e) {
			System.out.println(e.getMessage());
		}

		return categories;
	}

	/*
	 * Retrieves all cities where venues are available.
	 */
	@Override
	public Map<Integer, String> getAllCities() {
		Map<Integer, String> cities = new HashMap<>();
		try {
			cities = venueDao.getAllCities();
		} catch (DataAccessException e) {
			System.out.println(e.getMessage());
		}
		return cities;
	}

	/*
	 * Lists all currently available events.
	 */
	@Override
	public List<Event> listAvailableEvents() {
		List<Event> events = new ArrayList<>();
		try {
			events = eventDao.listAvailableEvents();
		} catch (DataAccessException e) {
			System.out.println(e.getMessage());
		}
		return events;
	}

	/*
	 * Retrieves all venues in the system.
	 */
	@Override
	public List<Venue> getAllVenues() {
		List<Venue> venues = new ArrayList<>();
		try {
			venues = venueDao.getAllVenues();
		} catch (DataAccessException e) {
			System.out.println(e.getMessage());
		}
		return venues;
	}

	/*
	 * Checks whether a venue is available for a given time range.
	 */
	@Override
	public boolean isVenueAvailable(int venueId, LocalDateTime startTime, LocalDateTime endTime) {
		try {
			boolean isAvailable = venueDao.isVenueAvailable(venueId,
					Timestamp.from(DateTimeUtil.convertLocalDefaultToUtc(startTime)),
					Timestamp.from(DateTimeUtil.convertLocalDefaultToUtc(endTime)));
			return isAvailable;
		} catch (DataAccessException e) {
			System.out.println(e.getMessage());
		}
		return false;
	}

	/*
	 * Retrieves venue details by identifier.
	 */
	@Override
	public Venue getVenueById(int venueId) {
		Venue venue = new Venue();
		try {
			venue = venueDao.getVenueById(venueId);
			return venue;
		} catch (DataAccessException e) {
			System.out.println(e.getMessage());
		}
		return null;
	}

	/*
	 * Lists events pending admin approval.
	 */
	@Override
	public List<Event> listEventsYetToApprove() {
		List<Event> events = new ArrayList<>();
		try {
			events = eventDao.listEventsYetToApprove();
		} catch (DataAccessException e) {
			System.out.println(e.getMessage());
		}
		if (events.isEmpty()) {
			System.out.println("There are no events!");
			return null;
		}
		return events;
	}

	/*
	 * Lists events that are either available or in draft state.
	 */
	@Override
	public List<Event> listAvailableAndDraftEvents() {
		List<Event> events = null;
		try {
			events = eventDao.listAvailableAndDraftEvents();
		} catch (DataAccessException e) {
			System.out.println(e.getMessage());
		}
		if (events.isEmpty()) {
			System.out.println("There are no events!");
			return null;
		}
		return events;
	}

}
