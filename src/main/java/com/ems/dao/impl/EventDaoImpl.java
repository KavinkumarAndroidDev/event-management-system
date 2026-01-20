package com.ems.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.ems.dao.EventDao;
import com.ems.model.Event;
import com.ems.model.User;
import com.ems.util.DBConnectionUtil;

public class EventDaoImpl implements EventDao {

	@Override
	public List<Event> listAvailableEvents() {
		List<Event> events = new ArrayList<>();
		String sql =
		        "SELECT DISTINCT e.* " +
		        "FROM events e " +
		        "INNER JOIN tickets t ON e.event_id = t.event_id " +
		        "WHERE e.status = ? " +
		        "AND t.available_quantity > 0";

		    try (Connection con = DBConnectionUtil.getConnection();
		         PreparedStatement ps = con.prepareStatement(sql)) {

		        ps.setString(1, "PUBLISHED");
		        ResultSet rs = ps.executeQuery();

			
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
				event.setStartDateTime(rs.getTimestamp("start_datetime").toLocalDateTime());
				event.setEndDateTime(rs.getTimestamp("end_datetime").toLocalDateTime());
				event.setCapacity(rs.getInt("capacity"));
				event.setStatus(rs.getString("status"));
				Integer approvedBy = rs.getInt("approved_by");
				if (approvedBy != null) {
					event.setApprovedBy(approvedBy);
				}
				Timestamp approved_at = rs.getTimestamp("approved_at");
				Timestamp created_at = rs.getTimestamp("approved_at");
				Timestamp updated_at = rs.getTimestamp("updated_at");
				if(approved_at != null) {
					event.setApprovedAt(rs.getTimestamp("approved_at").toLocalDateTime());
				}
				if(created_at != null) {
					event.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
				}
				if(updated_at != null) {

					event.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
				}
				events.add(event);
			}
		} catch (SQLException e) {
			System.out.println("Unexpected error occured: " + e.getMessage());
		} catch (Exception e) {
			System.out.println("Unexpected error occured: " + e.getMessage());

		}
		return events;
	}

}
