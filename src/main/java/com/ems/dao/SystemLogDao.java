package com.ems.dao;

import java.util.List;

import com.ems.exception.DataAccessException;
import com.ems.model.SystemLog;

public interface SystemLogDao {

	void log(
		Integer userId,
		String action,
		String entity,
		Integer entityId,
		String message
	) throws DataAccessException;
	
	List<SystemLog> findAll() throws DataAccessException;
}