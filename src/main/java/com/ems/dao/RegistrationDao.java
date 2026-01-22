package com.ems.dao;

import java.util.List;

import com.ems.exception.DataAccessException;
import com.ems.model.EventRegistrationReport;

public interface RegistrationDao {

	List<EventRegistrationReport> getEventWiseRegistrations(int eventId)  throws DataAccessException;

	int createRegistration(int userId, int eventId) throws DataAccessException;

	void addRegistrationTickets(int regId, int ticketId, int quantity) throws DataAccessException;

	void removeRegistrations(int regId) throws DataAccessException;

	void removeRegistrationTickets(int regId, int ticketId) throws DataAccessException;

}
