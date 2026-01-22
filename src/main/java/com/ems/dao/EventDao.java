package com.ems.dao;

import java.sql.SQLException;
import java.util.List;

import com.ems.exception.DataAccessException;
import com.ems.model.Event;

public interface EventDao {
	List<Event> listAvailableEvents() throws DataAccessException;

	List<Event> listAllEvents() throws DataAccessException;

	List<Event> listEventsYetToApprove() throws DataAccessException;

	boolean approveEvent(int eventId, int userId) throws DataAccessException ;

	boolean cancelEvent(int eventId) throws DataAccessException;

	int getOrganizerId(int eventId) throws DataAccessException, Exception;

	List<Event> listAvailableAndDraftEvents() throws DataAccessException;

	Event getEventById(int eventId) throws SQLException, Exception;

	String getEventName(int eventId) throws SQLException, Exception;

	void completeEvents() throws SQLException, Exception;

	List<Event> getUserEvents(int userId) throws SQLException, Exception;

	void viewBookingDetails(int userId) throws SQLException, Exception;

	void submitRating(int eventId, int userId, int rating, String comments) throws SQLException, Exception;

}
