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
		String sql = "select e.title as event_title, u.full_name, t.ticket_type, rt.quantity, r.registration_date " +
	             "from registrations r " + 
	             "inner join users u on r.user_id = u.user_id " +
	             "inner join registration_tickets rt on r.registration_id = rt.registration_id " +
	             "inner join tickets t on rt.ticket_id = t.ticket_id " + 
	             "inner join events e on r.event_id = e.event_id " + 
	             "where r.event_id = ? " +
	             "order by r.registration_date desc;";

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

	@Override
	public int createRegistration(int userId, int eventId) throws SQLException, Exception {
		String sql = "insert into registrations (user_id, event_id, registration_date, status) values (?,?,now(),'CONFIRMED')";
		try (Connection con = DBConnectionUtil.getConnection();
		         PreparedStatement ps = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
			ps.setInt(1, userId);
			ps.setInt(2, eventId);
			int affectedRows = ps.executeUpdate();
			if(affectedRows > 0) {
				try(ResultSet rs = ps.getGeneratedKeys()){
					if(rs.next()) {
						return rs.getInt(1);
					}
				}
			}
		}
		return 0;
	}

	@Override
	public void addRegistrationTickets(int regId, int ticketId, int quantity) throws SQLException, Exception {
		String sql = "insert into registration_tickets (registration_id, ticket_id, quantity) values (?,?,?)";
		try (Connection con = DBConnectionUtil.getConnection();
		         PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setInt(1, regId);
			ps.setInt(2, ticketId);
			ps.setInt(3, quantity);
			ps.executeUpdate();
		} 
	}

	@Override
	public void removeRegistrations(int regId) throws SQLException, Exception {
		String sql = "delete from registrations where registration_id = ?";
		try (Connection con = DBConnectionUtil.getConnection();
		         PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setInt(1, regId);
			ps.executeUpdate();

		} 
	}

	@Override
	public void removeRegistrationTickets(int regId, int ticketId) throws SQLException, Exception {
		String sql = "delete from registrations_tickets where registration_id = ? and ticket_id = ?";
		try (Connection con = DBConnectionUtil.getConnection();
		         PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setInt(1, regId);
			ps.setInt(2, ticketId);
			ps.executeUpdate();

		} 
		
	}
	

}
