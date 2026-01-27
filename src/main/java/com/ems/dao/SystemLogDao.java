package com.ems.dao;

import com.ems.exception.DataAccessException;

public interface SystemLogDao {

	void log(
		Integer userId,
		String action,
		String entity,
		Integer entityId,
		String message
	) throws DataAccessException;
}