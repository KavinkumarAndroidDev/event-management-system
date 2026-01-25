package com.ems.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.ems.dao.TicketDao;
import com.ems.exception.DataAccessException;
import com.ems.model.Ticket;
import com.ems.util.DBConnectionUtil;

/**
 * Ticket DAO implementation - Database operations ONLY
 */
public class TicketDaoImpl implements TicketDao {
	
	@Override
	public int getAvailableTickets(int eventId) throws DataAccessException {
		String sql = "select sum(available_quantity) as total_available "
				+ "from tickets "
				+ "where event_id = ?";
		
		try (Connection con = DBConnectionUtil.getConnection();
				PreparedStatement ps = con.prepareStatement(sql)) {
			
			ps.setInt(1, eventId);
			
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return rs.getInt("total_available");
				}
			}
			
		} catch (SQLException e) {
			throw new DataAccessException("Error fetching available tickets: " + e.getMessage());
		}
		
		return 0;
	}
	
	@Override
	public List<Ticket> getTicketTypes(int eventId) throws DataAccessException {
		String sql = "select * from tickets where event_id = ? and available_quantity > 0";
		List<Ticket> tickets = new ArrayList<>();
		
		try (Connection con = DBConnectionUtil.getConnection();
				PreparedStatement ps = con.prepareStatement(sql)) {
			
			ps.setInt(1, eventId);
			
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					Ticket ticket = new Ticket();
					ticket.setTicketId(rs.getInt("ticket_id"));
					ticket.setEventId(eventId);
					ticket.setTicketType(rs.getString("ticket_type"));
					ticket.setPrice(rs.getDouble("price"));
					ticket.setTotalQuantity(rs.getInt("total_quantity"));
					ticket.setAvailableQuantity(rs.getInt("available_quantity"));
					tickets.add(ticket);
				}
			}
			
		} catch (SQLException e) {
			throw new DataAccessException("Error fetching ticket types: " + e.getMessage());
		}
		
		return tickets;
	}
	
	@Override
	public Ticket getTicketById(int ticketId) throws DataAccessException {
		String sql = "select * from tickets where ticket_id = ?";
		
		try (Connection con = DBConnectionUtil.getConnection();
				PreparedStatement ps = con.prepareStatement(sql)) {
			
			ps.setInt(1, ticketId);
			
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					Ticket ticket = new Ticket();
					ticket.setTicketId(rs.getInt("ticket_id"));
					ticket.setEventId(rs.getInt("event_id"));
					ticket.setTicketType(rs.getString("ticket_type"));
					ticket.setPrice(rs.getDouble("price"));
					ticket.setTotalQuantity(rs.getInt("total_quantity"));
					ticket.setAvailableQuantity(rs.getInt("available_quantity"));
					return ticket;
				}
			}
			
		} catch (SQLException e) {
			throw new DataAccessException("Error fetching ticket by ID: " + e.getMessage());
		}
		
		return null;
	}
	
	@Override
	public boolean updateAvailableQuantity(int ticketId, int quantity) 
			throws DataAccessException {
		String sql = "update tickets set available_quantity = available_quantity + ? "
				+ "where ticket_id = ?";
		
		try (Connection con = DBConnectionUtil.getConnection();
				PreparedStatement ps = con.prepareStatement(sql)) {
			
			ps.setInt(1, quantity);
			ps.setInt(2, ticketId);
			
			int rowsAffected = ps.executeUpdate();
			
			return rowsAffected > 0;
			
		} catch (SQLException e) {
			throw new DataAccessException("Error updating ticket quantity: " + e.getMessage());
		}
	}
	
	
	//Organizer functions:

    public boolean createTicket(Ticket ticket) {
        String sql = "insert into tickets (event_id,ticket_type,price,total_quantity,available_quantity) values (?,?,?,?,?)";
        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, ticket.getEventId());
            ps.setString(2, ticket.getTicketType());
            ps.setDouble(3, ticket.getPrice());
            ps.setInt(4, ticket.getTotalQuantity());
            ps.setInt(5, ticket.getAvailableQuantity());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean updateTicketPrice(int ticketId, double price) {
        String sql = "update tickets set price=? where ticket_id=?";
        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDouble(1, price);
            ps.setInt(2, ticketId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean updateTicketQuantity(int ticketId, int quantity) {
        String sql = "update tickets set total_quantity=?, available_quantity=? where ticket_id=?";
        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, quantity);
            ps.setInt(2, quantity);
            ps.setInt(3, ticketId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<Ticket> getTicketsByEvent(int eventId) {
        List<Ticket> list = new ArrayList<>();
        String sql = "select * from tickets where event_id=?";
        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, eventId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Ticket t = new Ticket();
                t.setTicketId(rs.getInt("ticket_id"));
                t.setEventId(rs.getInt("event_id"));
                t.setTicketType(rs.getString("ticket_type"));
                t.setPrice(rs.getDouble("price"));
                t.setTotalQuantity(rs.getInt("total_quantity"));
                t.setAvailableQuantity(rs.getInt("available_quantity"));
                list.add(t);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return list;
    }
}