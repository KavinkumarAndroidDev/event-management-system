package com.ems.dao;

import com.ems.exception.DataAccessException;

public interface FeedbackDao {
	
	void submitRating(int eventId, int userId, int rating, String comments) throws DataAccessException;
}
