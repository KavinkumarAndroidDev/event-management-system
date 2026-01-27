package com.ems.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.ems.enums.PaymentMethod;
import com.ems.model.BookingDetail;
import com.ems.model.Category;
import com.ems.model.Event;
import com.ems.model.Ticket;
import com.ems.model.UserEventRegistration;
import com.ems.model.Venue;

public interface EventService {

	// ticket information
	List<Ticket> getTicketTypes(int eventId);

	int getAvailableTickets(int eventId);

	
	// event filtering
	List<Event> filterByPrice(double minPrice, double maxPrice);

	List<Event> searchByCity(int venueId);

	List<Event> searchByDate(LocalDate localDate);

	List<Event> searchByDateRange(LocalDate startDate, LocalDate endDate);

	List<Event> searchBycategory(int selectedCategoryId);

	
	// event registration and booking
	boolean registerForEvent(
		    int userId,
		    int eventId,
		    int ticketId,
		    int quantity,
		    double price,
		    PaymentMethod paymentMethod,
		    String offerCode
		);

	List<BookingDetail> viewBookingDetails(int userId);

	
	// user event history
	List<UserEventRegistration> viewUpcomingEvents(int userId);

	List<UserEventRegistration> viewPastEvents(int userId);

	
	// feedback
	void submitRating(int userId, int eventId, int rating, String comments);

	
	// Event listing & retrival
	List<Event> getAllEvents();

	List<Event> listAvailableEvents();

	List<Event> listEventsYetToApprove();

	List<Event> listAvailableAndDraftEvents();

	Event getEventById(int eventId);

	
	// Category & city lookups
	Category getCategory(int eventId);

	List<Category> getAllCategory();

	Map<Integer, String> getAllCities();

	
	// Venue information & availability
	String getVenueName(int venueId);

	String getVenueAddress(int venueId);

	List<Venue> getAllVenues();

	Venue getVenueById(int venueId);

	boolean isVenueAvailable(int venueId, LocalDateTime startTime, LocalDateTime endTime);

	boolean cancelRegistration(int userId, int registrationId);

}
