package com.ems.dao;

import java.sql.SQLException;

public interface RegistrationDao {

	void getEventWiseRegistrations(int eventId);

	int createRegistration(int userId, int eventId) throws SQLException, Exception;

	void addRegistrationTickets(int regId, int ticketId, int quantity) throws SQLException, Exception;

	void removeRegistrations(int regId) throws SQLException, Exception;

	void removeRegistrationTickets(int regId, int ticketId) throws SQLException, Exception;

}
