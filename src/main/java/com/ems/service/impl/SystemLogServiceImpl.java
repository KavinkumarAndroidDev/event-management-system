package com.ems.service.impl;

import java.util.List;

import com.ems.dao.SystemLogDao;
import com.ems.exception.DataAccessException;
import com.ems.model.SystemLog;
import com.ems.service.SystemLogService;

public class SystemLogServiceImpl implements SystemLogService {

	private final SystemLogDao systemLogDao;

	public SystemLogServiceImpl(SystemLogDao systemLogDao) {
		this.systemLogDao = systemLogDao;
	}

	@Override
	public void log(
			Integer userId,
			String action,
			String entity,
			Integer entityId,
			String message) {

		try {
			systemLogDao.log(
				userId,
				action,
				entity,
				entityId,
				message
			);
		}catch(DataAccessException e) {
			System.out.println(e);
		}
	}
	@Override
    public void printAllLogs() {
        try {
            List<SystemLog> logs = systemLogDao.findAll();

            if (logs.isEmpty()) {
                System.out.println("No logs found.");
                return;
            }

            for (SystemLog log : logs) {
                System.out.printf(
                    "%s | User:%s | %s %s | %s%n",
                    log.getCreatedAt(),
                    log.getUserId() == null ? "SYSTEM" : log.getUserId(),
                    log.getAction(),
                    log.getEntity(),
                    log.getMessage()
                );
            }

        } catch (DataAccessException e) {
            System.out.println("Failed to load system logs.");
        }
    }

}
