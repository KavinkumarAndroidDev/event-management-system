package com.ems.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import com.ems.dao.VenueDao;
import com.ems.util.DBConnectionUtil;

public class VenueDaoImpl implements VenueDao {
	
	// returns venue name using venue id
	@Override
	public String getVenueName(int venueId) {
		String sql = "select name from venues where venue_id=?";
		try(Connection con = DBConnectionUtil.getConnection();
				PreparedStatement ps = con.prepareStatement(sql)){
			ps.setInt(1, venueId);
			try (ResultSet rs = ps.executeQuery()) {
	            if (rs.next()) {
	                return rs.getString("name");
	            }
	        }
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return null;
	}

	// returns venue address using venue id
	@Override
	public String getVenueAddress(int venueId) {
		String sql = "select street, city, state, pincode from venues where venue_id=?";
		try(Connection con = DBConnectionUtil.getConnection();
				PreparedStatement ps = con.prepareStatement(sql)){
			ps.setInt(1, venueId);
			try (ResultSet rs = ps.executeQuery()) {
	            if (rs.next()) {
	                String address = rs.getString("street") +",\n"
	                		+rs.getString("city") + ",\n"
	                		+rs.getString("state") + " - "
	                		+rs.getString("pincode") + ",\n";
	                return address;
	            }
	        }
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return null;
	}
	
	// gets all venue cities
	@Override
	public Map<Integer, String> getAllCities(){
		String sql = "select venue_id, city from venues where is_active = TRUE order by city ";
		Map<Integer, String> cities = new HashMap<>();
		try(Connection con = DBConnectionUtil.getConnection();
				Statement ps = con.createStatement()){
			ResultSet rs = ps.executeQuery(sql);
			while(rs.next()) {
				cities.put(rs.getInt("venue_id"), rs.getString("city"));
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return cities;
	}
}
