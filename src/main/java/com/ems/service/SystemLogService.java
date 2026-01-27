package com.ems.service;

public interface SystemLogService {

	void log(
		Integer userId,
		String action,
		String entity,
		Integer entityId,
		String message
	);

	void printAllLogs();
}
