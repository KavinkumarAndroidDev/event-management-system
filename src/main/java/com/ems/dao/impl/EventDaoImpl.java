package com.ems.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.ems.dao.EventDao;
import com.ems.model.Event;
import com.ems.model.User;
import com.ems.util.DBConnectionUtil;
import com.ems.util.DateTimeUtil;

public class EventDaoImpl implements EventDao {
	
	//list all published events avaialable in the future
	@Override
	public List<Event> listAvailableEvents() {
		List<Event> events = new ArrayList<>();
		String sql =
		        "select distinct e.* " +
		        "from events e " +
		        "inner join tickets t on e.event_id = t.event_id " +
		        "where e.status = ? " +
		        "and t.available_quantity > 0" +
		        " AND e.start_datetime > UTC_TIMESTAMP()";

		    try (Connection con = DBConnectionUtil.getConnection();
		         PreparedStatement ps = con.prepareStatement(sql)) {

		        ps.setString(1, "PUBLISHED");
		        ResultSet rs = ps.executeQuery();
		        events = getEventList(rs);
		} catch (SQLException e) {
			System.out.println("Unexpected error occured: " + e.getMessage());
		} catch (Exception e) {
			System.out.println("Unexpected error occured: " + e.getMessage());

		}
		return events;
	}
	
	//List all approved events and yet to approve events
	@Override 
	public List<Event> listAvailableAndDraftEvents() {
		List<Event> events = new ArrayList<>();
		String sql =
		        "select distinct e.* " +
		        "from events e " +
		        "inner join tickets t on e.event_id = t.event_id " +
		        "where e.status in (?, ?) " +
		        "and t.available_quantity > 0" +
		        "AND e.start_datetime > UTC_TIMESTAMP()";

		    try (Connection con = DBConnectionUtil.getConnection();
		         PreparedStatement ps = con.prepareStatement(sql)) {

		        ps.setString(1, "PUBLISHED");
		        ps.setString(1, "CANCELLED");
		        ResultSet rs = ps.executeQuery();
		        events = getEventList(rs);
		} catch (SQLException e) {
			System.out.println("Unexpected error occured: " + e.getMessage());
		} catch (Exception e) {
			System.out.println("Unexpected error occured: " + e.getMessage());

		}
		return events;
	}
	
	// It list all events
	@Override
	public List<Event> listAllEvents() {
		List<Event> events = new ArrayList<>();
		String sql =
		        "select distinct e.* " +
		        "from events e " +
		        "inner join tickets t on e.event_id = t.event_id ";

		    try (Connection con = DBConnectionUtil.getConnection();
		         PreparedStatement ps = con.prepareStatement(sql)) {

		        ResultSet rs = ps.executeQuery();
		        events = getEventList(rs);
		} catch (SQLException e) {
			System.out.println("Unexpected error occured: " + e.getMessage());
		} catch (Exception e) {
			System.out.println("Unexpected error occured: " + e.getMessage());

		}
		return events;
	}
	//To get the list of yet to approve events
	@Override
	public List<Event> listEventsYetToApprove() {
		List<Event> events = new ArrayList<>();
		String sql =
		        "select distinct e.* " +
		        "from events e " +
		        "inner join tickets t on e.event_id = t.event_id " +
		        "where e.status in (?, ?) " +
		        "and t.available_quantity > 0" +
		        "AND e.start_datetime > UTC_TIMESTAMP()";

		    try (Connection con = DBConnectionUtil.getConnection();
		         PreparedStatement ps = con.prepareStatement(sql)) {

		        ps.setString(1, "DRAFT");
		        ps.setString(2, "CANCELLED");
		        ResultSet rs = ps.executeQuery();
		        events = getEventList(rs);
		} catch (SQLException e) {
			System.out.println("Unexpected error occured: " + e.getMessage());
		} catch (Exception e) {
			System.out.println("Unexpected error occured: " + e.getMessage());

		}
		return events;
	}
	
	//approves the event - changes the eventstatus to approve
	@Override
	public void approveEvent(int eventId) {
		String sql = "update events set status = ? where event_id = ? and e.start_datetime > UTC_TIMESTAMP()";

		 try (Connection con = DBConnectionUtil.getConnection();
		         PreparedStatement ps = con.prepareStatement(sql)) {

		        ps.setString(1, "PUBLISHED");
		        ps.setInt(2, eventId);
		        int rowsUpdated = ps.executeUpdate();
		        if(rowsUpdated == 0) {
		        	System.out.println("No event found with the id: " + eventId);
		        }else {
		        	System.out.println("Event has been approved: " + eventId);
		        }
		        
		} catch (SQLException e) {
			System.out.println("Unexpected error occured: " + e.getMessage());
		} catch (Exception e) {
			System.out.println("Unexpected error occured: " + e.getMessage());
		}
	}
	
	// cancels the event - change the event state as cancelled
	@Override
	public void cancelEvent(int eventId) {
		String sql = "update events set status = ? where event_id = ? and e.start_datetime > UTC_TIMESTAMP()";

		 try (Connection con = DBConnectionUtil.getConnection();
		         PreparedStatement ps = con.prepareStatement(sql)) {

		        ps.setString(1, "CANCELLED");
		        ps.setInt(2, eventId);
		        int rowsUpdated = ps.executeUpdate();
		        if(rowsUpdated == 0) {
		        	System.out.println("No event found with the id: " + eventId);
		        }else {
		        	System.out.println("Event has been cancelled: " + eventId);
		        }
		        
		} catch (SQLException e) {
			System.out.println("Unexpected error occured: " + e.getMessage());
		} catch (Exception e) {
			System.out.println("Unexpected error occured: " + e.getMessage());
		}
	}
	
	//returns the user_id of organizer of the particular event
	@Override
	public int getOrganizerId(int eventId) {
		String sql = "select user_id from events where event_id = ?";
		Integer organizerId = null;
		try (Connection con = DBConnectionUtil.getConnection();
		         PreparedStatement ps = con.prepareStatement(sql)) {

		        ps.setInt(1, eventId);
		        ResultSet rs  = ps.executeQuery();
		        if(rs.next()) {
		        	return rs.getInt("user_id");
		        }
		        
		} catch (SQLException e) {
			System.out.println("Unexpected error occured: " + e.getMessage());
		} catch (Exception e) {
			System.out.println("Unexpected error occured: " + e.getMessage());
		}
		return 0;
	}
	
	

	//helper function to help to get the list of event from the result set
	public List<Event> getEventList(ResultSet rs) throws SQLException{
		List<Event> events = new ArrayList<>();
		while(rs.next()) {
			Event event = new Event();
			event.setEventId(rs.getInt("event_id"));
			event.setOrganizerId(rs.getInt("organizer_id"));
			event.setTitle(rs.getString("title"));
			if(rs.getString("description").isEmpty() || rs.getString("description") == null) {
				event.setDescription(rs.getString("description"));

			}
			event.setCategoryId(rs.getInt("category_id"));
			event.setVenueId(rs.getInt("venue_id"));
			Instant startDateTime = rs.getTimestamp("start_datetime").toInstant();
			event.setStartDateTime(
			    DateTimeUtil.convertUtcToLocal(startDateTime).toLocalDateTime()
			);
			Instant endDateTime = rs.getTimestamp("end_datetime").toInstant();
			event.setEndDateTime(
			    DateTimeUtil.convertUtcToLocal(endDateTime).toLocalDateTime()
			);

			event.setCapacity(rs.getInt("capacity"));
			event.setStatus(rs.getString("status"));
			Integer approvedBy = rs.getInt("approved_by");
			if (approvedBy != null) {
				event.setApprovedBy(approvedBy);
			}
			
			
			
			if(rs.getTimestamp("updated_at") != null ) {
				Instant updated_at = rs.getTimestamp("updated_at").toInstant();
				event.setUpdatedAt( DateTimeUtil.convertUtcToLocal(updated_at).toLocalDateTime());
			}
			if(rs.getTimestamp("approved_at") != null) {
				Instant approved_at = rs.getTimestamp("approved_at").toInstant();
				event.setApprovedAt(
				    DateTimeUtil.convertUtcToLocal(endDateTime).toLocalDateTime()
				);
			}
			if(rs.getTimestamp("created_at") != null) {
				Instant created_at = rs.getTimestamp("created_at").toInstant();
				event.setCreatedAt( DateTimeUtil.convertUtcToLocal(created_at).toLocalDateTime());
			}
			
			events.add(event);
		}
		return events;
	}

	@Override
	public Event getEventById(int eventId) throws SQLException, Exception {
		Event event = new Event();
		String sql = " select * from events where event_id = ?";
		try (Connection con = DBConnectionUtil.getConnection();
		         PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setInt(1, eventId);
			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				event.setEventId(rs.getInt("event_id"));
				event.setOrganizerId(rs.getInt("organizer_id"));
				event.setTitle(rs.getString("title"));
				if(rs.getString("description").isEmpty() || rs.getString("description") == null) {
					event.setDescription(rs.getString("description"));

				}
				event.setCategoryId(rs.getInt("category_id"));
				event.setVenueId(rs.getInt("venue_id"));
				Instant startDateTime = rs.getTimestamp("start_datetime").toInstant();
				event.setStartDateTime(
				    DateTimeUtil.convertUtcToLocal(startDateTime).toLocalDateTime()
				);
				Instant endDateTime = rs.getTimestamp("end_datetime").toInstant();
				event.setEndDateTime(
				    DateTimeUtil.convertUtcToLocal(endDateTime).toLocalDateTime()
				);

				event.setCapacity(rs.getInt("capacity"));
				event.setStatus(rs.getString("status"));
				Integer approvedBy = rs.getInt("approved_by");
				if (approvedBy != null) {
					event.setApprovedBy(approvedBy);
				}
				
				
				
				if(rs.getTimestamp("updated_at") != null ) {
					Instant updated_at = rs.getTimestamp("updated_at").toInstant();
					event.setUpdatedAt( DateTimeUtil.convertUtcToLocal(updated_at).toLocalDateTime());
				}
				if(rs.getTimestamp("approved_at") != null) {
					Instant approved_at = rs.getTimestamp("approved_at").toInstant();
					event.setApprovedAt(
					    DateTimeUtil.convertUtcToLocal(endDateTime).toLocalDateTime()
					);
				}
				if(rs.getTimestamp("created_at") != null) {
					Instant created_at = rs.getTimestamp("created_at").toInstant();
					event.setCreatedAt( DateTimeUtil.convertUtcToLocal(created_at).toLocalDateTime());
				}
			}
		} 
		return event;
	}
}