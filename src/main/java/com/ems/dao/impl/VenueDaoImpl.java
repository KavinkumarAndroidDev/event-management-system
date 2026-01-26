package com.ems.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ems.dao.VenueDao;
import com.ems.exception.DataAccessException;
import com.ems.model.Venue;
import com.ems.util.DBConnectionUtil;
import com.ems.util.DateTimeUtil;

public class VenueDaoImpl implements VenueDao {

    @Override
    public String getVenueName(int venueId) throws DataAccessException{
        String sql = "select name from venues where venue_id=?";
        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, venueId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("name");
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error fetching venue name", e);
        }
        return null;
    }

    @Override
    public String getVenueAddress(int venueId) throws DataAccessException{
        String sql = "select street, city, state, pincode from venues where venue_id=?";
        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, venueId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("street") + ",\n"
                         + rs.getString("city") + ",\n"
                         + rs.getString("state") + " - "
                         + rs.getString("pincode");
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error fetching venue address");
        }
        return null;
    }

    @Override
    public Map<Integer, String> getAllCities() throws DataAccessException{
        String sql = "select venue_id, city from venues where is_active = TRUE order by city";
        Map<Integer, String> cities = new HashMap<>();

        try (Connection con = DBConnectionUtil.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                cities.put(rs.getInt("venue_id"), rs.getString("city"));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error fetching cities");
        }
        return cities;
    }

	@Override
	public List<Venue> getAllVenues() throws DataAccessException{
		String sql = "select * from venues where is_active = 1";
		List<Venue> venues = new ArrayList<>();
        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
        	ResultSet rs = ps.executeQuery();
        	while(rs.next()) {
        		Venue venue = new Venue();
        		venue.setName(rs.getString("name"));
        		
        		venue.setCity(rs.getString("city"));
        		venue.setVenueId(rs.getInt("venue_id"));
        		venue.setStreet(rs.getString("street"));
        		venue.setState(rs.getString("state"));
        		venue.setPincode(rs.getString("pincode"));
        		venue.setMaxCapacity(rs.getInt("max_capacity"));
        		venue.setCreatedAt(DateTimeUtil.convertUtcToLocal(rs.getTimestamp("created_at").toInstant()).toLocalDateTime());
        		if(rs.getTimestamp("updated_at") != null) {
        			venue.setUpdateAt(DateTimeUtil.convertUtcToLocal(rs.getTimestamp("updated_at").toInstant()).toLocalDateTime());
        		}
        		venues.add(venue);
        	}
        } catch (SQLException e) {
            throw new DataAccessException("Error fetching venues", e);
        }
        return venues;
	}

	@Override
	public boolean isVenueAvailable(int venueId, Timestamp to, Timestamp from) throws DataAccessException {
		String sql =
		        "SELECT COUNT(*) " +
		        "FROM events " +
		        "WHERE venue_id = ? " +
		        "AND status IN ('DRAFT', 'PUBLISHED') " +
		        "AND start_datetime < ? " +
		        "AND end_datetime > ?";

		    try (Connection conn = DBConnectionUtil.getConnection();
		         PreparedStatement ps = conn.prepareStatement(sql)) {

		        ps.setInt(1, venueId);
		        ps.setTimestamp(2, to);
		        ps.setTimestamp(3, from);

		        try (ResultSet rs = ps.executeQuery()) {
		            if (rs.next()) {
		                return rs.getInt(1) == 0;
		            }
		        }

		    } catch (SQLException e) {
	            throw new DataAccessException("Error fetching venues", e);
	        }

		    return false;
	}
	
	@Override
	public Venue getVenueById(int venueId) throws DataAccessException{
		String sql = "select * from venues where is_active = 1 and venue_id = ?";
		Venue venue = new Venue();
        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
        	ps.setInt(1, venueId);
        	ResultSet rs = ps.executeQuery();
        	if(rs.next()) {
        		venue.setCity(rs.getString("city"));
        		venue.setVenueId(rs.getInt("venue_id"));
        		venue.setStreet(rs.getString("street"));
        		venue.setState(rs.getString("state"));
        		venue.setPincode(rs.getString("pincode"));
        		venue.setMaxCapacity(rs.getInt("max_capacity"));
        		venue.setCreatedAt(DateTimeUtil.convertUtcToLocal(rs.getTimestamp("created_at").toInstant()).toLocalDateTime());
        		venue.setUpdateAt(DateTimeUtil.convertUtcToLocal(rs.getTimestamp("updated_at").toInstant()).toLocalDateTime());
        	}
        } catch (SQLException e) {
            throw new DataAccessException("Error fetching venues", e);
        }
        return venue;
	}
	
	
	@Override
	public void addVenue(Venue venue) throws DataAccessException {
	    String sql =
	        "insert into venues (name, street, city, state, pincode, max_capacity, created_at, is_active) " +
	        "values (?, ?, ?, ?, ?, ?, ?, 1)";

	    try (Connection con = DBConnectionUtil.getConnection();
	         PreparedStatement ps = con.prepareStatement(sql)) {

	        ps.setString(1, venue.getName());
	        ps.setString(2, venue.getStreet());
	        ps.setString(3, venue.getCity());
	        ps.setString(4, venue.getState());
	        ps.setString(5, venue.getPincode());
	        ps.setInt(6, venue.getMaxCapacity());
	        ps.setTimestamp(7, Timestamp.from(DateTimeUtil.convertLocalDefaultToUtc(LocalDateTime.now())));
	        ps.executeUpdate();
	    } catch (Exception e) {
	        throw new DataAccessException("Failed to add venue");
	    }
	}

	@Override
	public void updateVenue(Venue venue) throws DataAccessException {
	    String sql =
	        "update venues set name=?, street=?, city=?, state=?, pincode=?, max_capacity=? " +
	        "where venue_id=? and is_active=1";

	    try (Connection con = DBConnectionUtil.getConnection();
	         PreparedStatement ps = con.prepareStatement(sql)) {

	        ps.setString(1, venue.getName());
	        ps.setString(2, venue.getStreet());
	        ps.setString(3, venue.getCity());
	        ps.setString(4, venue.getState());
	        ps.setString(5, venue.getPincode());
	        ps.setInt(6, venue.getMaxCapacity());
	        ps.setInt(7, venue.getVenueId());

	        ps.executeUpdate();
	    } catch (Exception e) {
	        throw new DataAccessException("Failed to update venue");
	    }
	}

	@Override
	public void deactivateVenue(int venueId) throws DataAccessException {
	    String sql = "update venues set is_active=0 where venue_id=?";

	    try (Connection con = DBConnectionUtil.getConnection();
	         PreparedStatement ps = con.prepareStatement(sql)) {

	        ps.setInt(1, venueId);
	        ps.executeUpdate();
	    } catch (Exception e) {
	        throw new DataAccessException("Failed to remove venue");
	    }
	}

}
