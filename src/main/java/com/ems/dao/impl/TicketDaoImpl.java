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

public class TicketDaoImpl implements  TicketDao{

	@Override
	public int getAvailableTickets(int eventId) {
		String sql = "SELECT SUM(available_quantity) AS total_available\n"
				+ "FROM tickets\n"
				+ "WHERE event_id = ?\n";
		try(Connection con = new DBConnectionUtil().getConnection();
				PreparedStatement ps = con.prepareStatement(sql)){
			ps.setInt(1, eventId);
			try (ResultSet rs = ps.executeQuery()) {
	            if (rs.next()) {
	                return rs.getInt("total_available");
	            }
	        }
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return 0;
	}

	@Override
	public List<Ticket> getTicketTypes(int eventId) {
		String sql = "select * from tickets where event_id = ? and available_quantity > 0";
		List<Ticket> tickets = new ArrayList<>();
		try(Connection con = new DBConnectionUtil().getConnection();
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
			System.out.println(e.getMessage());
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return tickets;
	}
	
}

