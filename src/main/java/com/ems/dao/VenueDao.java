package com.ems.dao;

import java.util.Map;

public interface VenueDao {

	String getVenueName(int venueId);

	String getVenueAddress(int venueId);
	
	Map<Integer, String> getAllCities();
}
