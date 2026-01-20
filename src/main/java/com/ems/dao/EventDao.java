package com.ems.dao;

import java.util.List;

import com.ems.model.Event;

public interface EventDao {
	List<Event> listAvailableEvents();
}
