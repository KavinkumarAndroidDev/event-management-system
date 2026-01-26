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

    // tickets
    List<Ticket> getTicketTypes(int eventId);

    // event browsing & filtering
    List<Event> filterByPrice(double minPrice, double maxPrice);
    List<Event> searchByCity(int venueId);
    List<Event> searchByDate(LocalDate localDate);
    List<Event> searchByDateRange(LocalDate startDate, LocalDate endDate);
    List<Event> searchBycategory(int selectedCategoryId);

    // registration
    boolean registerForEvent(int userId, int eventId, int ticketId, int quantity, double d, PaymentMethod paymentMethod);

    // user events
    List<UserEventRegistration> viewUpcomingEvents(int userId);
    List<UserEventRegistration> viewPastEvents(int userId);
    List<BookingDetail> viewBookingDetails(int userId);

    // feedback
    void submitRating(int userId, int eventId, int rating, String comments);

    // admin / common
    List<Event> getAllEvents();
    Category getCategory(int eventId);
    List<Category> getAllCategory();
    Map<Integer, String> getAllCities();

	int getAvailableTickets(int eventId);

	String getVenueName(int venueId);

	String getVenueAddress(int venueId);

	List<Event> listAvailableEvents();

	List<Venue> getAllVenues();

	boolean isVenueAvailable(int venueId, LocalDateTime startTime, LocalDateTime endTime);

	Venue getVenueById(int venueId);

	List<Event> listEventsYetToApprove();
	
	List<Event> listAvailableAndDraftEvents();

	Event getEventById(int eventId);
}
