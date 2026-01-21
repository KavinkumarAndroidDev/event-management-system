package com.ems.dao;

import java.sql.SQLException;
import java.util.List;

import com.ems.model.Event;

public interface EventDao {
	List<Event> listAvailableEvents();

	List<Event> listAllEvents();

	List<Event> listEventsYetToApprove();

	void approveEvent(int eventId) ;

	void cancelEvent(int eventId);

	int getOrganizerId(int eventId);

	List<Event> listAvailableAndDraftEvents();

	Event getEventById(int eventId) throws SQLException, Exception;

}
