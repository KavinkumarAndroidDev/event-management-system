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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ems.dao.EventDao;
import com.ems.enums.EventStatus;
import com.ems.exception.DataAccessException;
import com.ems.model.BookingDetail;
import com.ems.model.Event;
import com.ems.model.OrganizerEventSummary;
import com.ems.model.UserEventRegistration;
import com.ems.util.DBConnectionUtil;
import com.ems.util.DateTimeUtil;
/*
 * Handles database operations related to events.
 *
 * Responsibilities:
 * - Retrieve event data for listings, details, and reports
 * - Persist event creation and updates
 * - Manage event status transitions and approvals
 * - Fetch registration, booking, and revenue related data
 */
public class EventDaoImpl implements EventDao {


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
			throw new DataAccessException("Error while fetching available events");
		}
		return events;
	}

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
			throw new DataAccessException("Error while fetching available and draft events");
		}
		return events;
	}

	@Override
	public List<Event> listAllEvents() throws DataAccessException {
		List<Event> events = new ArrayList<>();
		String sql = "select distinct e.* " + "from events e " + "inner join tickets t on e.event_id = t.event_id ";

		try (Connection con = DBConnectionUtil.getConnection(); Statement ps = con.createStatement()) {

			ResultSet rs = ps.executeQuery(sql);
			events = getEventList(rs);
			rs.close();
		} catch (SQLException e) {
			throw new DataAccessException("Error fetching events");
		}
		return events;
	}

	@Override
	public List<Event> listEventsYetToApprove() throws DataAccessException {
		List<Event> events = new ArrayList<>();
		String sql = "select distinct e.* " + "from events e " + "inner join tickets t on e.event_id = t.event_id "
				+ "where e.status in (?, ?) " + "and t.available_quantity > 0 "
				+ "AND e.start_datetime > UTC_TIMESTAMP() and e.approved_at = null";

		try (Connection con = DBConnectionUtil.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setString(1, "DRAFT");
			ps.setString(2, "CANCELLED");
			ResultSet rs = ps.executeQuery();
			events = getEventList(rs);
			rs.close();
		} catch (SQLException e) {
			throw new DataAccessException("Error fetching yet to approve events");
		}
		return events;
	}

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
			throw new DataAccessException("Error fetching the event organiszer");
		}
		throw new DataAccessException("Organizer not found");
	}

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
			throw new DataAccessException("Error while fetching event details");
		}
		return null;
	}

	@Override
	public List<UserEventRegistration> getUserRegistrations(int userId)
        throws DataAccessException {

	    List<UserEventRegistration> list = new ArrayList<>();
	
	    String sql = "select "
	    		+ "  r.registration_id, "
	    		+ "  r.registration_date, "
	    		+ "  r.status as registration_status, "
	    		+ "  e.event_id, "
	    		+ "  e.title, "
	    		+ "  e.start_datetime, "
	    		+ "  e.end_datetime, "
	    		+ "  c.name as category_name, "
	    		+ "  (select coalesce(sum(rt.quantity), 0) "
	            + "     from registration_tickets rt "
	            + "     where rt.registration_id = r.registration_id) as tickets_purchased, "
	            + "  (select coalesce(sum(p.amount), 0) "
	            + "     from payments p "
	            + "     where p.registration_id = r.registration_id) as amount_paid "
	    		+ "from registrations r "
	    		+ "join events e on r.event_id = e.event_id "
	    		+ "join categories c on e.category_id = c.category_id "
	    		+ "where r.user_id = ? ";
	
	    try (Connection con = DBConnectionUtil.getConnection();
	         PreparedStatement ps = con.prepareStatement(sql)) {
	
	        ps.setInt(1, userId);
	        ResultSet rs = ps.executeQuery();
	
	        while (rs.next()) {
	            UserEventRegistration uer = new UserEventRegistration();
	
	            uer.setRegistrationId(rs.getInt("registration_id"));
	            uer.setRegistrationDate(rs.getTimestamp("registration_date").toLocalDateTime());
	            uer.setRegistrationStatus(rs.getString("registration_status"));
	            uer.setEventId(rs.getInt("event_id"));
	            uer.setTitle(rs.getString("title"));
	            uer.setCategory(rs.getString("category_name"));
	            uer.setStartDateTime(rs.getTimestamp("start_datetime").toLocalDateTime());
	            uer.setEndDateTime(rs.getTimestamp("end_datetime").toLocalDateTime());
	            uer.setTicketsPurchased(rs.getInt("tickets_purchased"));
	            uer.setAmountPaid(rs.getDouble("amount_paid"));
	
	            list.add(uer);
	        }
	
	    } catch (SQLException e) {
	        throw new DataAccessException("Error fetching user registrations");
	    }
	
	    return list;
	}

	@Override
	public List<BookingDetail> viewBookingDetails(int userId) throws DataAccessException {

		List<BookingDetail> bookings = new ArrayList<>();

		String sql = "select e.title, e.start_datetime, v.name, v.city, "
				+ "t.ticket_type, rt.quantity, (rt.quantity * t.price) as total_cost " + "from registrations r "
				+ "join events e ON r.event_id = e.event_id " + "join venues v on e.venue_id = v.venue_id "
				+ "join registration_tickets rt on r.registration_id = rt.registration_id "
				+ "join tickets t on rt.ticket_id = t.ticket_id " + "where r.user_id = ? and r.status = 'CONFIRMED'";

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
			throw new DataAccessException("Error fetching booking details");
		}

		return bookings;
	}

	// approves an event
	@Override
	public boolean approveEvent(int eventId, int userId) throws DataAccessException {
		String sql = "update events set status = ? ,approved_by = ?, updated_at = ?, approved_at = ? where event_id = ? and start_datetime > ?";

		try (Connection con = DBConnectionUtil.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, "draft");
			ps.setInt(2, userId);
			ps.setTimestamp(3, Timestamp.from(DateTimeUtil.getCurrentUtc()));
			ps.setTimestamp(4, Timestamp.from(DateTimeUtil.getCurrentUtc()));
			ps.setInt(5, eventId);
			ps.setTimestamp(6, Timestamp.from(DateTimeUtil.getCurrentUtc()));
			int rowsUpdated = ps.executeUpdate();
			
			return rowsUpdated > 0;
			
		} catch (SQLException e) {
			throw new DataAccessException("Error while updating events");
		}
	}

	@Override
	public boolean cancelEvent(int eventId) throws DataAccessException {

		String sql = "update events set status = ? , approved_by = null, updated_at = ?, approved_at = null where event_id = ? and start_datetime > ?";

		try (Connection con = DBConnectionUtil.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setString(1, "CANCELLED");
			ps.setTimestamp(2, Timestamp.from(DateTimeUtil.getCurrentUtc()));
			ps.setInt(3, eventId);
			ps.setTimestamp(4, Timestamp.from(DateTimeUtil.getCurrentUtc()));
			int rowsUpdated = ps.executeUpdate();
			
			return rowsUpdated > 0;
			
		} catch (SQLException e) {
			throw new DataAccessException("Error while updating events");
		}
	}

	@Override
	public void completeEvents() throws DataAccessException {
		String sql = "update events set status = ? where status = ? " + "and end_datetime <= CURRENT_TIMESTAMP";
		try (Connection con = DBConnectionUtil.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, EventStatus.PUBLISHED.toString());
			ps.setString(2, EventStatus.COMPLETED.toString());
			ps.executeUpdate();
		} catch (SQLException e) {
			throw new DataAccessException("Error while updating events");
		}
	}

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
			throw new DataAccessException("Error while fetching event list");
		}

		return events;
	}

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
			throw new DataAccessException("Failed to fetch revenue report");
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
			throw new DataAccessException("Failed to fetch organizer performance");
		}

		return result;
	}
	
	
	
	
	//organizer functions:
	@Override
    public int createEvent(Event event) throws DataAccessException {
        String sql = "insert into events (organizer_id,title,description,category_id,venue_id,start_datetime,end_datetime,capacity,status,created_at) values (?,?,?,?,?,?,?,?,?,?)";
        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, event.getOrganizerId());
            ps.setString(2, event.getTitle());
            ps.setString(3, event.getDescription());
            ps.setInt(4, event.getCategoryId());
            ps.setInt(5, event.getVenueId());
            ps.setTimestamp(6, Timestamp.valueOf(event.getStartDateTime()));
            ps.setTimestamp(7, Timestamp.valueOf(event.getEndDateTime()));
            ps.setInt(8, event.getCapacity());
            ps.setString(9, event.getStatus());
            ps.setTimestamp(10, Timestamp.valueOf(LocalDateTime.now()));

            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            return rs.next() ? rs.getInt(1) : 0;
        } catch (Exception e) {
			throw new DataAccessException("Failed to create event");
		}
    }
	@Override
    public boolean updateEventDetails(int eventId, String title, String description, int categoryId, int venueId) throws DataAccessException {
        String sql = "update events set title=?, description=?, category_id=?, venue_id=?, updated_at=? where event_id=?";
        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, title);
            ps.setString(2, description);
            ps.setInt(3, categoryId);
            ps.setInt(4, venueId);
            ps.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
            ps.setInt(6, eventId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
			throw new DataAccessException("Failed to update event details");
		}
    }
	@Override
    public boolean updateEventSchedule(int eventId, LocalDateTime start, LocalDateTime end) throws DataAccessException {
        String sql = "update events set start_datetime=?, end_datetime=?, updated_at=? where event_id=?";
        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setTimestamp(1, Timestamp.valueOf(start));
            ps.setTimestamp(2, Timestamp.valueOf(end));
            ps.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            ps.setInt(4, eventId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
			throw new DataAccessException("Failed to update the event schedule");
		}
    }
	@Override
    public boolean updateEventCapacity(int eventId, int capacity) throws DataAccessException {
        String sql = "update events set capacity=?, updated_at=? where event_id=?";
        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, capacity);
            ps.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            ps.setInt(3, eventId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
			throw new DataAccessException("Failed to update event capacity");
		}
    }
	@Override
    public boolean updateEventStatus(int eventId, String status) throws DataAccessException {
        String sql = "update events set status=?, updated_at=? where event_id=?";
        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            ps.setInt(3, eventId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
			throw new DataAccessException("Failed to update event status");
		}
    }

    public List<Event> getEventsByOrganizer(int organizerId) throws DataAccessException {
        List<Event> list = new ArrayList<>();
        String sql = "select * from events where organizer_id=?";
        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, organizerId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                return getEventList(rs);
            }
        } catch (SQLException e) {
			throw new DataAccessException("Failed to fetch organizer events");
		}
        return list;
    }

	@Override
	public List<OrganizerEventSummary> getEventSummaryByOrganizer(int organizerId) throws DataAccessException {
		String sql = "SELECT  "
				+ "    e.event_id, "
				+ "    e.title, "
				+ "    e.status, "
				+ "    COALESCE(SUM(t.total_quantity), 0) AS total_tickets, "
				+ "    COALESCE(SUM(t.total_quantity - t.available_quantity), 0) AS booked_tickets "
				+ "FROM events e "
				+ "LEFT JOIN tickets t ON e.event_id = t.event_id "
				+ "WHERE e.organizer_id = ? "
				+ "GROUP BY e.event_id, e.title, e.status "
				+ "ORDER BY e.status, e.start_datetime; ";
		List<OrganizerEventSummary> eventSummaries = new ArrayList<>();
		try (Connection con = DBConnectionUtil.getConnection();
	             PreparedStatement ps = con.prepareStatement(sql)) {

	            ps.setInt(1, organizerId);
	            ResultSet rs = ps.executeQuery();
	            while (rs.next()) {
	            	OrganizerEventSummary eventSummary = new OrganizerEventSummary();
	            	eventSummary.setBookedTickets(rs.getInt("booked_tickets"));
	            	eventSummary.setEventId(rs.getInt("event_id"));
	            	eventSummary.setStatus(rs.getString("status"));
	            	eventSummary.setTitle(rs.getString("title"));
	            	eventSummary.setTotalTickets(rs.getInt("total_ticekts"));
	            	eventSummaries.add(eventSummary);
	            }
	        } catch (SQLException e) {
				throw new DataAccessException("Failed to fetch organizer events");
			}
		return eventSummaries;
	}

}
