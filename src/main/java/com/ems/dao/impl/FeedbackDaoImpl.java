package com.ems.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.ems.dao.FeedbackDao;
import com.ems.exception.DataAccessException;
import com.ems.util.DBConnectionUtil;

public class FeedbackDaoImpl implements FeedbackDao{
	
	// submits rating and feedback for completed event
	@Override
	public void submitRating(int eventId, int userId, int rating, String comments) throws DataAccessException{
		String sql = "select count(*) from events e join registrations r on e.event_id = r.event_id " +
	             "where r.user_id = ? and e.event_id = ? and e.status = 'COMPLETED' and r.status = 'CONFIRMED'";

		try (Connection con = DBConnectionUtil.getConnection();
			PreparedStatement ps= con.prepareStatement(sql)) {
		    ps.setInt(1, userId);
		    ps.setInt(2, eventId);
		    ResultSet rs = ps.executeQuery();
		    
		    if (rs.next() && rs.getInt(1) > 0) {
		        String insertReview = "insert into feedback(event_id,user_id, rating, comments, submitted_at) values(?,?,?,?,utc_timestamp())";
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
		    rs.close();
		}catch (SQLException e) {
			throw new DataAccessException("Error while submitting rating: " + e.getMessage());
		}
	}
}
