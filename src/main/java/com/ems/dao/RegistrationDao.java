package com.ems.dao;

import java.util.List;
import java.util.Map;

import com.ems.exception.DataAccessException;
import com.ems.model.EventRegistrationReport;

public interface RegistrationDao {

	List<EventRegistrationReport> getEventWiseRegistrations(int eventId)  throws DataAccessException;

	int createRegistration(int userId, int eventId) throws DataAccessException;

	void addRegistrationTickets(int regId, int ticketId, int quantity) throws DataAccessException;

	void removeRegistrations(int regId) throws DataAccessException;

	void removeRegistrationTickets(int regId, int ticketId) throws DataAccessException;
	
	//Organizer functions:
    int getEventRegistrationCount(int eventId);

    List<Map<String, Object>> getRegisteredUsers(int eventId);

    List<Map<String, Object>> getOrganizerWiseRegistrations(int organizerId);

    List<Map<String, Object>> getTicketSales(int organizerId);

    double getRevenueSummary(int organizerId);
    
    List<Integer> getRegisteredUserIdsByEvent(int eventId) throws DataAccessException;


}
