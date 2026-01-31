package com.ems.dao;

import java.util.List;
import java.util.Map;

import com.ems.exception.DataAccessException;
import com.ems.model.EventRegistrationReport;
import com.ems.model.Registration;
import com.ems.model.RegistrationTicket;

public interface RegistrationDao {

	List<EventRegistrationReport> getEventWiseRegistrations(int eventId)  throws DataAccessException;

	//Organizer functions:
    int getEventRegistrationCount(int eventId) throws DataAccessException;

    List<Map<String, Object>> getRegisteredUsers(int eventId) throws DataAccessException;

    List<Map<String, Object>> getOrganizerWiseRegistrations(int organizerId) throws DataAccessException;

    List<Map<String, Object>> getTicketSales(int organizerId) throws DataAccessException;

    double getRevenueSummary(int organizerId) throws DataAccessException;
    
    List<Integer> getRegisteredUserIdsByEvent(int eventId) throws DataAccessException;

	Registration getById(int registrationId) throws DataAccessException;

	void updateStatus(int registrationId, String string)  throws DataAccessException;

	List<RegistrationTicket> getRegistrationTickets(int registrationId)  throws DataAccessException;

}
