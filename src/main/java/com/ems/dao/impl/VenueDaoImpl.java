package com.ems.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.ems.dao.VenueDao;
import com.ems.util.DBConnectionUtil;

public class VenueDaoImpl implements VenueDao {

	@Override
	public String getVenueName(int venueId) {
		String sql = "select name from venues where venue_id=?";
		try(Connection con = new DBConnectionUtil().getConnection();
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

	@Override
	public String getVenueAddress(int venueId) {
		String sql = "select * from venues where venue_id=?";
		try(Connection con = new DBConnectionUtil().getConnection();
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

}
