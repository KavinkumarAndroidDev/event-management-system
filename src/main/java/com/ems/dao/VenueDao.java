package com.ems.dao;

import java.util.Map;

import com.ems.exception.DataAccessException;

public interface VenueDao {

	String getVenueName(int venueId)  throws DataAccessException;

	String getVenueAddress(int venueId)  throws DataAccessException;
	
	Map<Integer, String> getAllCities()  throws DataAccessException;
}
