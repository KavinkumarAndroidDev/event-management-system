package com.ems.dao;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import com.ems.exception.DataAccessException;
import com.ems.model.Venue;

public interface VenueDao {

	String getVenueName(int venueId)  throws DataAccessException;

	String getVenueAddress(int venueId)  throws DataAccessException;
	
	Map<Integer, String> getAllCities()  throws DataAccessException;

	List<Venue> getAllVenues() throws DataAccessException;

	boolean isVenueAvailable(int venueId, Timestamp from, Timestamp from2) throws DataAccessException;

	Venue getVenueById(int venueId) throws DataAccessException;
}
