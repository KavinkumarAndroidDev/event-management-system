package com.ems.actions;

import com.ems.service.SystemLogService;
import com.ems.util.ApplicationUtil;

public class SystemLogAction {
    private final SystemLogService systemLogService;

    public SystemLogAction() {
        this.systemLogService = ApplicationUtil.systemLogService();
    }

    public void printAllLogs() {
        systemLogService.printAllLogs();
    }
}