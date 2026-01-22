package com.ems.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.ems.dao.EventDao;
import com.ems.enums.EventStatus;
import com.ems.exception.DataAccessException;
import com.ems.model.Event;
import com.ems.util.DBConnectionUtil;
import com.ems.util.DateTimeUtil;

public class EventDaoImpl implements EventDao {
	
	//list all published events avaialable in the future
	@Override
	public  List<Event> listAvailableEvents() throws DataAccessException {
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
			throw new DataAccessException("Error fetching available events" + e.getMessage());
		} catch (Exception e) {
			System.out.println("Unexpected error occured: " + e.getMessage());

		}
		return events;
	}
	
	//List all approved events and yet to approve events
	@Override 
	public List<Event> listAvailableAndDraftEvents() throws DataAccessException {
		List<Event> events = new ArrayList<>();
		String sql =
		        "select distinct e.* " +
		        "from events e " +
		        "inner join tickets t on e.event_id = t.event_id " +
		        "where e.status in (?, ?) " +
		        "and t.available_quantity > 0 " +
		        "AND e.start_datetime > UTC_TIMESTAMP()";

		    try (Connection con = DBConnectionUtil.getConnection();
		         PreparedStatement ps = con.prepareStatement(sql)) {

		        ps.setString(1, "PUBLISHED");
		        ps.setString(2, "DRAFT");
		        ResultSet rs = ps.executeQuery();
		        events = getEventList(rs);
		} catch (SQLException e) {
			throw new DataAccessException("Error fetching events" + e.getMessage());
		} catch (Exception e) {
			System.out.println("Unexpected error occured: " + e.getMessage());

		}
		return events;
	}
	
	// It list all events
	@Override
	public List<Event> listAllEvents() throws DataAccessException {
		List<Event> events = new ArrayList<>();
		String sql =
		        "select distinct e.* " +
		        "from events e " +
		        "inner join tickets t on e.event_id = t.event_id ";

		    try (Connection con = DBConnectionUtil.getConnection();
		         Statement ps = con.createStatement()) {

		        ResultSet rs = ps.executeQuery(sql);
		        events = getEventList(rs);
		} catch (SQLException e) {
			throw new DataAccessException("Error fetching events: " + e.getMessage());
		} catch (Exception e) {
			System.out.println("Unexpected error occured: " + e.getMessage());

		}
		return events;
	}
	//To get the list of yet to approve events
	@Override
	public List<Event> listEventsYetToApprove() throws DataAccessException {
		List<Event> events = new ArrayList<>();
		String sql =
		        "select distinct e.* " +
		        "from events e " +
		        "inner join tickets t on e.event_id = t.event_id " +
		        "where e.status in (?, ?) " +
		        "and t.available_quantity > 0 " +
		        "AND e.start_datetime > UTC_TIMESTAMP()";

		    try (Connection con = DBConnectionUtil.getConnection();
		         PreparedStatement ps = con.prepareStatement(sql)) {

		        ps.setString(1, "DRAFT");
		        ps.setString(2, "CANCELLED");
		        ResultSet rs = ps.executeQuery();
		        events = getEventList(rs);
		} catch (SQLException e) {
			throw new DataAccessException("Error fetching events" + e.getMessage());
		} catch (Exception e) {
			System.out.println("Unexpected error occured: "+ e.getMessage());
		}
		return events;
	}
	
	//approves the event - changes the eventstatus to approve
	@Override
	public boolean approveEvent(int eventId, int userId) throws DataAccessException {
		String sql = "update events set status = ? , approved_by = ?, updated_at = ?, approved_at = ? where event_id = ? and start_datetime > ?";

		 try (Connection con = DBConnectionUtil.getConnection();
		         PreparedStatement ps = con.prepareStatement(sql)) {

		        ps.setString(1, "PUBLISHED");
		        ps.setInt(2, userId);
		        ps.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
		        ps.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
		        ps.setInt(5, eventId);
		        ps.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
		        int rowsUpdated = ps.executeUpdate();
		        if(rowsUpdated == 0) {
		        	System.out.println("No event found with the id: " + eventId);
		        	return false;
		        }else {
		        	System.out.println("Event has been approved: " + eventId);
		        }
		} catch (SQLException e) {
			throw new DataAccessException("Error while updating events" + e.getMessage());
		} catch (Exception e) {
			System.out.println("Unexpected error occured: " + e.getMessage());
		}
		return true;
	}
	
	// cancels the event - change the event state as cancelled
	@Override
	public boolean cancelEvent(int eventId) throws DataAccessException {

		String sql = "update events set status = ? , approved_by = null, updated_at = ?, approved_at = null where event_id = ? and start_datetime > ?";

		 try (Connection con = DBConnectionUtil.getConnection();
		         PreparedStatement ps = con.prepareStatement(sql)) {

		        ps.setString(1, "CANCELLED");
		        ps.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
		        ps.setInt(3, eventId);
		        ps.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
		        int rowsUpdated = ps.executeUpdate();
		        if(rowsUpdated == 0) {
		        	System.out.println("No event found with the id: " + eventId);
		        	return false;
		        }else {
		        	System.out.println("Event has been cancelled: " + eventId);
		        }
		        
		} catch (SQLException e) {
			throw new DataAccessException("Error while updating events" + e.getMessage());
		} catch (Exception e) {
			System.out.println("Unexpected error occured: " + e.getMessage());
		}
		 return true;
	}
	
	//returns the user_id of organizer of the particular event
	@Override
	public int getOrganizerId(int eventId) throws Exception {
		String sql = "select organizer_id from events where event_id = ?";
		try (Connection con = DBConnectionUtil.getConnection();
		         PreparedStatement ps = con.prepareStatement(sql)) {

		        ps.setInt(1, eventId);
		        ResultSet rs  = ps.executeQuery();
		        if(rs.next()) {
		        	return rs.getInt("organizer_id");
		        }
		        
		} catch (SQLException e) {
			throw new DataAccessException("Error fetching the event organiszer" + e.getMessage());
		} catch (Exception e) {
			System.out.println("Unexpected error occured: " + e.getMessage());
		}
		throw new Exception("Organizer not found");
	}
	
	
	public List<Event> getEventList(ResultSet rs) throws SQLException{
		List<Event> events = new ArrayList<>();
		while(rs.next()) {
			Event event = new Event();
			event.setEventId(rs.getInt("event_id"));
			event.setOrganizerId(rs.getInt("organizer_id"));
			event.setTitle(rs.getString("title"));
			String desc = rs.getString("description");
			if (desc != null && !desc.isEmpty()) {
			    event.setDescription(desc);
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
				    DateTimeUtil.convertUtcToLocal(approved_at).toLocalDateTime()
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
					    DateTimeUtil.convertUtcToLocal(approved_at).toLocalDateTime()
					);
				}
				if(rs.getTimestamp("created_at") != null) {
					Instant created_at = rs.getTimestamp("created_at").toInstant();
					event.setCreatedAt( DateTimeUtil.convertUtcToLocal(created_at).toLocalDateTime());
				}
				return event;
			}
			
		}
		return null;
	}

	@Override
	public String getEventName(int eventId) throws SQLException, Exception {
		String sql = "select title from events where event_id = ?";
		try(Connection con = DBConnectionUtil.getConnection();
				PreparedStatement ps = con.prepareStatement(sql)){
			ps.setInt(1, eventId);
			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				String eventName = rs.getString("title");
				return eventName;
			}
		}
		return null;
	}

	@Override
	public void completeEvents() throws SQLException, Exception {
		String sql = "update events set status = ? where status = ? "
				+ "and end_datetime <= CURRENT_TIMESTAMP";
		try(Connection con = DBConnectionUtil.getConnection();
				PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, EventStatus.PUBLISHED.toString());
			ps.setString(2, EventStatus.COMPLETED.toString());
			ps.executeUpdate();
		}
	}

	@Override
	public List<Event> getUserEvents(int userId) throws SQLException, Exception {
		List<Event> events = new ArrayList<>();
		String sql = "select distinct e.* from events e inner join registrations r on e.event_id = r.event_id"
				+ " where r.user_id = ?" ;
		try (Connection con = DBConnectionUtil.getConnection();
		         PreparedStatement ps = con.prepareStatement(sql)) {

		        ps.setInt(1, userId);
		        ResultSet rs = ps.executeQuery();
		        events = getEventList(rs);
		        return events;
		}
	}

	@Override
	public void viewBookingDetails(int userId) throws SQLException, Exception {
		String sql = "SELECT "
				+ "e.title AS event_name,"
				+ "e.start_datetime,"
				+ "v.name AS venue_name,"
				+ "v.street,"
				+ "v.city, "
				+ "t.ticket_type, "
				+ "rt.quantity AS tickets_booked, "
				+ "t.price AS unit_price, "
				+ "(rt.quantity * t.price) AS total_cost "
				+ "FROM registrations r "
				+ "INNER JOIN events e ON r.event_id = e.event_id "
				+ "INNER JOIN venues v ON e.venue_id = v.venue_id "
				+ "INNER JOIN registration_tickets rt ON r.registration_id = rt.registration_id "
				+ "INNER JOIN tickets t ON rt.ticket_id = t.ticket_id "
				+ "WHERE r.user_id = ? "
				+ "  AND r.status = 'CONFIRMED'";
		
		try(Connection con = DBConnectionUtil.getConnection();
				PreparedStatement ps = con.prepareStatement(sql)){
			ps.setInt(1, userId);
			
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
			    System.out.println("------------------------------------------");
			    System.out.println("Event: " + rs.getString("event_name"));
			    System.out.println("Venue: " + rs.getString("venue_name") + " (" + rs.getString("city") + ")");
			    System.out.println("Ticket types:  " + rs.getString("ticket_type") + " x" + rs.getInt("tickets_booked"));
			    System.out.println("Total: ₹" + rs.getDouble("total_cost"));
			    System.out.println("------------------------------------------");
			}

		}
	}

	@Override
	public void submitRating(int eventId, int userId, int rating, String comments) throws SQLException, Exception {
		String sql = "SELECT COUNT(*) FROM events e JOIN registrations r ON e.event_id = r.event_id " +
	             "WHERE r.user_id = ? AND e.event_id = ? AND e.status = 'COMPLETED' AND r.status = 'CONFIRMED'";

	try (Connection con = DBConnectionUtil.getConnection();
		PreparedStatement ps= con.prepareStatement(sql)) {
	    ps.setInt(1, userId);
	    ps.setInt(2, eventId);
	    ResultSet rs = ps.executeQuery();
	    
	    if (rs.next() && rs.getInt(1) > 0) {
	        String insertReview = "insert into feedback(event_id,user_id, rating, comments, submitted_at) values(?,?,?,?,now())";
	        PreparedStatement ps1 = con.prepareStatement(insertReview);
	        ps1.setInt(1, eventId);
	        ps1.setInt(2, userId);
	        ps1.setInt(3, rating);
	        ps1.setString(4, comments);
	        int affectedRows = ps.executeUpdate();
	        if(affectedRows > 0) System.out.println("Your valuable feedback has been stored!");
	        else System.out.println("Unexpected error occured during the feedback storage!");
	    } else {
	        System.out.println("Status: No completed booking found for this event/user.");
	    }
	}

	}
	
}