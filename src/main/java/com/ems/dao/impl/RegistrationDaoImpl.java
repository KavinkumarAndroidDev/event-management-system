package com.ems.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.ems.dao.RegistrationDao;
import com.ems.exception.DataAccessException;
import com.ems.model.EventRegistrationReport;
import com.ems.util.DBConnectionUtil;

public class RegistrationDaoImpl implements RegistrationDao {
	
	// gets registration details for a specific event
	@Override
	public List<EventRegistrationReport> getEventWiseRegistrations(int eventId)
        throws DataAccessException {

	    List<EventRegistrationReport> reports = new ArrayList<>();
	
	    String sql =
	        "select e.title as event_title, u.full_name, t.ticket_type, " +
	        "rt.quantity, r.registration_date " +
	        "from registrations r " +
	        "inner join users u on r.user_id = u.user_id " +
	        "inner join registration_tickets rt on r.registration_id = rt.registration_id " +
	        "inner join tickets t on rt.ticket_id = t.ticket_id " +
	        "inner join events e on r.event_id = e.event_id " +
	        "where r.event_id = ?";
	
	    try (Connection con = DBConnectionUtil.getConnection();
	         PreparedStatement ps = con.prepareStatement(sql)) {
	
	        ps.setInt(1, eventId);
	        ResultSet rs = ps.executeQuery();
	
	        while (rs.next()) {
	            EventRegistrationReport report = new EventRegistrationReport();
	            report.setEventTitle(rs.getString("event_title"));
	            report.setUserName(rs.getString("full_name"));
	            report.setTicketType(rs.getString("ticket_type"));
	            report.setQuantity(rs.getInt("quantity"));
	            report.setRegistrationDate(
	                rs.getTimestamp("registration_date").toLocalDateTime()
	            );
	
	            reports.add(report);
	        }
	        rs.close();
	
	    } catch (SQLException e) {
	        throw new DataAccessException(
	            "Error while fetching event wise registration: " + e.getMessage()
	        );
	    }

	    return reports;
	}

	// creates a new registration entry
	@Override
	public int createRegistration(int userId, int eventId) throws DataAccessException {
		String sql = "insert into registrations (user_id, event_id, registration_date, status) values (?,?,utc_timestamp(),'CONFIRMED')";
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
		}catch (SQLException e) {
			throw new DataAccessException("Error while creating registration: " + e.getMessage());
		}
		return 0;
	}

	// adds ticket details for a registration
	@Override
	public void addRegistrationTickets(int regId, int ticketId, int quantity) throws DataAccessException {
		String sql = "insert into registration_tickets (registration_id, ticket_id, quantity) values (?,?,?)";
		try (Connection con = DBConnectionUtil.getConnection();
		         PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setInt(1, regId);
			ps.setInt(2, ticketId);
			ps.setInt(3, quantity);
			ps.executeUpdate();
		} catch (SQLException e) {
			throw new DataAccessException("Error while updating registration tickets: " + e.getMessage());
		}
	}

	// removes registration entry
	@Override
	public void removeRegistrations(int regId) throws DataAccessException {
		String sql = "delete from registrations where registration_id = ?";
		try (Connection con = DBConnectionUtil.getConnection();
		         PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setInt(1, regId);
			ps.executeUpdate();

		} catch (SQLException e) {
			throw new DataAccessException("Error while fetching categories: " + e.getMessage());
		}
	}

	// removes tickets linked to a registration
	@Override
	public void removeRegistrationTickets(int regId, int ticketId) throws DataAccessException {
		String sql = "delete from registrations_tickets where registration_id = ? and ticket_id = ?";
		try (Connection con = DBConnectionUtil.getConnection();
		         PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setInt(1, regId);
			ps.setInt(2, ticketId);
			ps.executeUpdate();

		} catch (SQLException e) {
			throw new DataAccessException("Error while updating registration tickets: " + e.getMessage());
		}
		
	}
}
