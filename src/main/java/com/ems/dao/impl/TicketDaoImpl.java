package com.ems.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.ems.dao.TicketDao;
import com.ems.model.Ticket;
import com.ems.util.DBConnectionUtil;

public class TicketDaoImpl implements TicketDao{
	
	// returns total available tickets for an event
	@Override
	public int getAvailableTickets(int eventId) {
		String sql = "select sum(available_quantity) as total_available "
				+ "from tickets "
				+ "where event_id = ?";
		try(Connection con = DBConnectionUtil.getConnection();
				PreparedStatement ps = con.prepareStatement(sql)){
			ps.setInt(1, eventId);
			try (ResultSet rs = ps.executeQuery()) {
	            if (rs.next()) {
	                return rs.getInt("total_available");
	            }
	        }
		} catch (SQLException e) {
			System.out.println("Unexpected error occured: " + e.getMessage());
		} catch (Exception e) {
			System.out.println("Unexpected error occured: " + e.getMessage());
		}
		return 0;
	}
	
	// gets all available ticket types for an event
	@Override
	public List<Ticket> getTicketTypes(int eventId) {
		String sql = "select * from tickets where event_id = ? and available_quantity > 0";
		List<Ticket> tickets = new ArrayList<>();
		try(Connection con = DBConnectionUtil.getConnection();
				PreparedStatement ps = con.prepareStatement(sql)){
			ps.setInt(1, eventId);
			try(ResultSet rs = ps.executeQuery()){
				while(rs.next()) {
					Ticket ticket = new Ticket();
					ticket.setAvailableQuantity(rs.getInt("available_quantity"));
					ticket.setEventId(eventId);
					ticket.setPrice(rs.getDouble("price"));
					ticket.setTicketId(rs.getInt("ticket_id"));
					ticket.setTicketType(rs.getString("ticket_type"));
					ticket.setTotalQuantity(rs.getInt("total_quantity"));
					tickets.add(ticket);
				}
			}
	        
		} catch (SQLException e) {
			System.out.println("Unexpected error occured: " + e.getMessage());
		} catch (Exception e) {
			System.out.println("Unexpected error occured: " + e.getMessage());
		}
		return tickets;
	}

	// gets ticket details using ticket id
	@Override
	public Ticket getTicketById(int ticketId) {
		String sql = "select * from tickets where ticket_id = ?";
		Ticket ticket = new Ticket();
		try(Connection con = DBConnectionUtil.getConnection();
				PreparedStatement ps = con.prepareStatement(sql)){
			ps.setInt(1, ticketId);
			try(ResultSet rs = ps.executeQuery()){
				if(rs.next()) {
					ticket.setAvailableQuantity(rs.getInt("available_quantity"));
					ticket.setEventId(rs.getInt("event_id"));
					ticket.setPrice(rs.getDouble("price"));
					ticket.setTicketId(rs.getInt("ticket_id"));
					ticket.setTicketType(rs.getString("ticket_type"));
					ticket.setTotalQuantity(rs.getInt("total_quantity"));
					return ticket;
				}
			}
	        
		} catch (SQLException e) {
			System.out.println("Unexpected error occured: " + e.getMessage());
		} catch (Exception e) {
			System.out.println("Unexpected error occured: " + e.getMessage());
		}
		return null;
	}

	// updates available ticket quantity
	@Override
	public void updateAvailableQuantity(int ticketId, int i) {
		String sql = "update tickets set available_quantity = available_quantity + ?"
				+ " where ticket_id = ?";
		try(Connection con = DBConnectionUtil.getConnection();
				PreparedStatement ps = con.prepareStatement(sql)){
			ps.setInt(1, i);
			ps.setInt(2, ticketId);
			int rowsAffected = ps.executeUpdate();
			if(rowsAffected == 0) {
				System.out.println("Payment process failed, during registration ticket generation");
			}
		} catch (SQLException e) {
			System.out.println("Unexpected error occured: " + e.getMessage());
		} catch (Exception e) {
			System.out.println("Unexpected error occured: " + e.getMessage());
		}
	}
	
}
