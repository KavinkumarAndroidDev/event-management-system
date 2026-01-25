package com.ems.dao;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.ems.exception.DataAccessException;
import com.ems.model.BookingDetail;
import com.ems.model.Event;
import com.ems.model.UserEventRegistration;

public interface EventDao {
	List<Event> listAvailableEvents() throws DataAccessException;

	 List<Event> listAllEvents() throws DataAccessException;

	 List<Event> listEventsYetToApprove() throws DataAccessException;

	 boolean approveEvent(int eventId, int userId) throws DataAccessException ;

	 boolean cancelEvent(int eventId) throws DataAccessException;

	 int getOrganizerId(int eventId) throws DataAccessException;

	 List<Event> listAvailableAndDraftEvents() throws DataAccessException;

	 Event getEventById(int eventId) throws DataAccessException;

	 String getEventName(int eventId) throws DataAccessException;

	 void completeEvents() throws DataAccessException;

	 List<UserEventRegistration> getUserRegistrations(int userId) throws DataAccessException;

	 List<BookingDetail> viewBookingDetails(int userId) throws DataAccessException;
	
	 Map<String, Double> getEventWiseRevenue() throws DataAccessException;

	 Map<String, Integer> getOrganizerWiseEventCount() throws DataAccessException;
	 
	 
	 //organizer functions
	 int createEvent(Event event) throws DataAccessException;
	 
	 boolean updateEventDetails(int eventId, String title, String description, int categoryId, int venueId) throws DataAccessException;

	 boolean updateEventSchedule(int eventId, LocalDateTime start, LocalDateTime end) throws DataAccessException;

	 boolean updateEventCapacity(int eventId, int capacity) throws DataAccessException;

	 boolean updateEventStatus(int eventId, String status) throws DataAccessException;

	 List<Event> getEventsByOrganizer(int organizerId) throws DataAccessException;

}
