package com.ems.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ems.dao.EventDao;
import com.ems.enums.EventStatus;
import com.ems.exception.DataAccessException;
import com.ems.model.BookingDetail;
import com.ems.model.Event;
import com.ems.util.DBConnectionUtil;
import com.ems.util.DateTimeUtil;

public class EventDaoImpl implements EventDao {

	// fetch functions
	// lists all future published events with available tickets
	@Override
	public List<Event> listAvailableEvents() throws DataAccessException {
		List<Event> events = new ArrayList<>();
		String sql = "select distinct e.* " + "from events e " + "inner join tickets t on e.event_id = t.event_id "
				+ "where e.status = ? " + "and t.available_quantity > 0" + " and e.start_datetime > UTC_TIMESTAMP()";

		try (Connection con = DBConnectionUtil.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, "PUBLISHED");
			ResultSet rs = ps.executeQuery();
			events = getEventList(rs);
			rs.close();
		} catch (SQLException e) {
			throw new DataAccessException("Error while fetching available events: " + e.getMessage());
		}
		return events;
	}

	// lists published and draft events
	@Override
	public List<Event> listAvailableAndDraftEvents() throws DataAccessException {
		List<Event> events = new ArrayList<>();
		String sql = "select distinct e.* " + "from events e " + "inner join tickets t on e.event_id = t.event_id "
				+ "where e.status in (?, ?) " + "and t.available_quantity > 0 "
				+ "AND e.start_datetime > UTC_TIMESTAMP()";

		try (Connection con = DBConnectionUtil.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setString(1, "PUBLISHED");
			ps.setString(2, "DRAFT");
			ResultSet rs = ps.executeQuery();
			events = getEventList(rs);
			rs.close();
		} catch (SQLException e) {
			throw new DataAccessException("Error while fetching available and draft events: " + e.getMessage());
		}
		return events;
	}

	// lists all events
	@Override
	public List<Event> listAllEvents() throws DataAccessException {
		List<Event> events = new ArrayList<>();
		String sql = "select distinct e.* " + "from events e " + "inner join tickets t on e.event_id = t.event_id ";

		try (Connection con = DBConnectionUtil.getConnection(); Statement ps = con.createStatement()) {

			ResultSet rs = ps.executeQuery(sql);
			events = getEventList(rs);
			rs.close();
		} catch (SQLException e) {
			throw new DataAccessException("Error fetching events: " + e.getMessage());
		}
		return events;
	}

	// lists events that are not approved yet
	@Override
	public List<Event> listEventsYetToApprove() throws DataAccessException {
		List<Event> events = new ArrayList<>();
		String sql = "select distinct e.* " + "from events e " + "inner join tickets t on e.event_id = t.event_id "
				+ "where e.status in (?, ?) " + "and t.available_quantity > 0 "
				+ "AND e.start_datetime > UTC_TIMESTAMP()";

		try (Connection con = DBConnectionUtil.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setString(1, "DRAFT");
			ps.setString(2, "CANCELLED");
			ResultSet rs = ps.executeQuery();
			events = getEventList(rs);
			rs.close();
		} catch (SQLException e) {
			throw new DataAccessException("Error fetching yet to approve events" + e.getMessage());
		}
		return events;
	}

	// returns organizer id for an event
	@Override
	public int getOrganizerId(int eventId) throws DataAccessException {
		String sql = "select organizer_id from events where event_id = ?";
		try (Connection con = DBConnectionUtil.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setInt(1, eventId);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getInt("organizer_id");
			}
		} catch (SQLException e) {
			throw new DataAccessException("Error fetching the event organiszer" + e.getMessage());
		}
		throw new DataAccessException("Organizer not found");
	}

	// gets event details using event id
	@Override
	public Event getEventById(int eventId) throws DataAccessException {
		Event event = new Event();
		String sql = " select * from events where event_id = ?";
		try (Connection con = DBConnectionUtil.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setInt(1, eventId);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				event.setEventId(rs.getInt("event_id"));
				event.setOrganizerId(rs.getInt("organizer_id"));
				event.setTitle(rs.getString("title"));
				if (rs.getString("description").isEmpty() || rs.getString("description") == null) {
					event.setDescription(rs.getString("description"));
				}
				event.setCategoryId(rs.getInt("category_id"));
				event.setVenueId(rs.getInt("venue_id"));
				Instant startDateTime = rs.getTimestamp("start_datetime").toInstant();
				event.setStartDateTime(DateTimeUtil.convertUtcToLocal(startDateTime).toLocalDateTime());
				Instant endDateTime = rs.getTimestamp("end_datetime").toInstant();
				event.setEndDateTime(DateTimeUtil.convertUtcToLocal(endDateTime).toLocalDateTime());

				event.setCapacity(rs.getInt("capacity"));
				event.setStatus(rs.getString("status"));
				Integer approvedBy = rs.getInt("approved_by");
				if (approvedBy != null) {
					event.setApprovedBy(approvedBy);
				}

				if (rs.getTimestamp("updated_at") != null) {
					Instant updated_at = rs.getTimestamp("updated_at").toInstant();
					event.setUpdatedAt(DateTimeUtil.convertUtcToLocal(updated_at).toLocalDateTime());
				}
				if (rs.getTimestamp("approved_at") != null) {
					Instant approved_at = rs.getTimestamp("approved_at").toInstant();
					event.setApprovedAt(DateTimeUtil.convertUtcToLocal(approved_at).toLocalDateTime());
				}
				if (rs.getTimestamp("created_at") != null) {
					Instant created_at = rs.getTimestamp("created_at").toInstant();
					event.setCreatedAt(DateTimeUtil.convertUtcToLocal(created_at).toLocalDateTime());
				}
				return event;
			}
			rs.close();

		} catch (SQLException e) {
			throw new DataAccessException("Error while fetching events: " + e.getMessage());
		}
		return null;
	}

	// returns event name
	@Override
	public String getEventName(int eventId) throws DataAccessException {
		String sql = "select title from events where event_id = ?";
		try (Connection con = DBConnectionUtil.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setInt(1, eventId);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				String eventName = rs.getString("title");
				rs.close();
				return eventName;
			}
		} catch (SQLException e) {
			throw new DataAccessException("Error while fetching event details: " + e.getMessage());
		}
		return null;
	}

	// returns events registered by user
	@Override
	public List<Event> getUserEvents(int userId) throws DataAccessException {
		List<Event> events = new ArrayList<>();
		String sql = "select distinct e.* from events e inner join registrations r on e.event_id = r.event_id"
				+ " where r.user_id = ?";
		try (Connection con = DBConnectionUtil.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setInt(1, userId);
			ResultSet rs = ps.executeQuery();
			events = getEventList(rs);
			return events;
		} catch (SQLException e) {
			throw new DataAccessException("Error while fetching events registered by users: " + e.getMessage());
		}
	}

	// shows booking details for user
	@Override
	public List<BookingDetail> viewBookingDetails(int userId) throws DataAccessException {

		List<BookingDetail> bookings = new ArrayList<>();

		String sql = "SELECT e.title, e.start_datetime, v.name, v.city, "
				+ "t.ticket_type, rt.quantity, (rt.quantity * t.price) AS total_cost " + "FROM registrations r "
				+ "JOIN events e ON r.event_id = e.event_id " + "JOIN venues v ON e.venue_id = v.venue_id "
				+ "JOIN registration_tickets rt ON r.registration_id = rt.registration_id "
				+ "JOIN tickets t ON rt.ticket_id = t.ticket_id " + "WHERE r.user_id = ? AND r.status = 'CONFIRMED'";

		try (Connection con = DBConnectionUtil.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setInt(1, userId);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				bookings.add(new BookingDetail(rs.getString("title"),
						rs.getTimestamp("start_datetime").toLocalDateTime(), rs.getString("name"), rs.getString("city"),
						rs.getString("ticket_type"), rs.getInt("quantity"), rs.getDouble("total_cost")));
			}
			rs.close();
		} catch (SQLException e) {
			throw new DataAccessException("Error fetching booking details", e);
		}

		return bookings;
	}

	/// Event details manipulations
	// approves an event
	@Override
	public boolean approveEvent(int eventId, int userId) throws DataAccessException {
		String sql = "update events set approved_by = ?, updated_at = ?, approved_at = ? where event_id = ? and start_datetime > ?";

		try (Connection con = DBConnectionUtil.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setInt(1, userId);
			ps.setTimestamp(2, Timestamp.from(DateTimeUtil.getCurrentUtc()));
			ps.setTimestamp(3, Timestamp.from(DateTimeUtil.getCurrentUtc()));
			ps.setInt(4, eventId);
			ps.setTimestamp(5, Timestamp.from(DateTimeUtil.getCurrentUtc()));
			int rowsUpdated = ps.executeUpdate();
			if (rowsUpdated == 0) {
				System.out.println("No event found with the id: " + eventId);
				return false;
			} else {
				System.out.println("Event has been approved: " + eventId);
			}
		} catch (SQLException e) {
			throw new DataAccessException("Error while updating events" + e.getMessage());
		}
		return true;
	}

	// cancels an event
	@Override
	public boolean cancelEvent(int eventId) throws DataAccessException {

		String sql = "update events set status = ? , approved_by = null, updated_at = ?, approved_at = null where event_id = ? and start_datetime > ?";

		try (Connection con = DBConnectionUtil.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setString(1, "CANCELLED");
			ps.setTimestamp(2, Timestamp.from(DateTimeUtil.getCurrentUtc()));
			ps.setInt(3, eventId);
			ps.setTimestamp(4, Timestamp.from(DateTimeUtil.getCurrentUtc()));
			int rowsUpdated = ps.executeUpdate();
			if (rowsUpdated == 0) {
				System.out.println("No event found with the id: " + eventId);
				return false;
			} else {
				System.out.println("Event has been cancelled: " + eventId);
			}

		} catch (SQLException e) {
			throw new DataAccessException("Error while updating events" + e.getMessage());
		}
		return true;
	}

	// marks completed events
	@Override
	public void completeEvents() throws DataAccessException {
		String sql = "update events set status = ? where status = ? " + "and end_datetime <= CURRENT_TIMESTAMP";
		try (Connection con = DBConnectionUtil.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, EventStatus.PUBLISHED.toString());
			ps.setString(2, EventStatus.COMPLETED.toString());
			ps.executeUpdate();
		} catch (SQLException e) {
			throw new DataAccessException("Error while updating events: " + e.getMessage());
		}
	}

	// helper function
	// maps resultset to event list
	public List<Event> getEventList(ResultSet rs) throws DataAccessException {
		List<Event> events = new ArrayList<>();
		try {
			while (rs.next()) {
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
				event.setStartDateTime(DateTimeUtil.convertUtcToLocal(startDateTime).toLocalDateTime());
				Instant endDateTime = rs.getTimestamp("end_datetime").toInstant();
				event.setEndDateTime(DateTimeUtil.convertUtcToLocal(endDateTime).toLocalDateTime());

				event.setCapacity(rs.getInt("capacity"));
				event.setStatus(rs.getString("status"));
				Integer approvedBy = rs.getInt("approved_by");
				if (approvedBy != null) {
					event.setApprovedBy(approvedBy);
				}

				if (rs.getTimestamp("updated_at") != null) {
					Instant updated_at = rs.getTimestamp("updated_at").toInstant();
					event.setUpdatedAt(DateTimeUtil.convertUtcToLocal(updated_at).toLocalDateTime());
				}
				if (rs.getTimestamp("approved_at") != null) {
					Instant approved_at = rs.getTimestamp("approved_at").toInstant();
					event.setApprovedAt(DateTimeUtil.convertUtcToLocal(approved_at).toLocalDateTime());
				}
				if (rs.getTimestamp("created_at") != null) {
					Instant created_at = rs.getTimestamp("created_at").toInstant();
					event.setCreatedAt(DateTimeUtil.convertUtcToLocal(created_at).toLocalDateTime());
				}

				events.add(event);
			}
			rs.close();
		} catch (SQLException e) {
			throw new DataAccessException("Error while fetching event list: " + e.getMessage());
		}

		return events;
	}

	// report daos
	@Override
	public Map<String, Double> getEventWiseRevenue() throws DataAccessException {
		Map<String, Double> revenueMap = new HashMap<>();
		String sql = "select e.title, sum(p.amount) as revenue " + "from payments p "
				+ "join registrations r on p.registration_id = r.registration_id "
				+ "join events e on r.event_id = e.event_id " + "where r.status = 'CONFIRMED' " + "group by e.title";

		try (Connection con = DBConnectionUtil.getConnection();
				PreparedStatement ps = con.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {
			while (rs.next()) {
				revenueMap.put(rs.getString("title"), rs.getDouble("revenue"));
			}
		} catch (Exception e) {
			throw new DataAccessException("Failed to fetch revenue report", e);
		}

		return revenueMap;
	}

	@Override
	public Map<String, Integer> getOrganizerWiseEventCount() throws DataAccessException {
		Map<String, Integer> result = new HashMap<>();
		String sql = "select u.full_name, count(e.event_id) as total_events " + "from events e "
				+ "join users u on e.organizer_id = u.user_id " + "group by u.full_name";
		try (Connection con = DBConnectionUtil.getConnection();
				PreparedStatement ps = con.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {
			while (rs.next()) {
				result.put(rs.getString("full_name"), rs.getInt("total_events"));
			}
		} catch (Exception e) {
			throw new DataAccessException("Failed to fetch organizer performance", e);
		}

		return result;
	}
}
