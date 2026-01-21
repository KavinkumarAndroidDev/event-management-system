package com.ems.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.ems.dao.RegistrationDao;
import com.ems.util.DBConnectionUtil;

public class RegistrationDaoImpl implements RegistrationDao {
	
	//Gets the details of registration with the event id
	@Override
	public void getEventWiseRegistrations(int eventId) {
		String sql = "SELECT e.title AS event_title, u.full_name, t.ticket_type, rt.quantity, r.registration_date " +
	             "FROM registrations r " + // Added 'r' alias
	             "INNER JOIN users u ON r.user_id = u.user_id " + // Added 'u' alias
	             "INNER JOIN registration_tickets rt ON r.registration_id = rt.registration_id " +
	             "INNER JOIN tickets t ON rt.ticket_id = t.ticket_id " + // Added 't' alias
	             "INNER JOIN events e ON r.event_id = e.event_id " + // Added 'e' alias
	             "WHERE r.event_id = ? " +
	             "ORDER BY r.registration_date DESC;";

		 try (Connection con = DBConnectionUtil.getConnection();
		         PreparedStatement ps = con.prepareStatement(sql)) {
			 ps.setInt(1, eventId);
			 ResultSet rs = ps.executeQuery();
			 while(rs.next()) {
				 System.out.println("Event title: "+rs.getString("event_title")+" | User name: " + rs.getString("full_name") +
						 " | Ticket type: "+rs.getString("ticket_type") + " | Quantity: "+ rs.getInt("quantity") + " | Registration date: "+ rs.getTimestamp("registration_date")
						);

			 }
		 } catch (SQLException e) {
			 System.out.println("Unexpected error occured: " + e.getMessage());
		} catch (Exception e) {
			System.out.println("Unexpected error occured: " + e.getMessage());
		}
		
	}
	

}
