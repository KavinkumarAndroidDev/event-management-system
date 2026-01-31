package com.ems.actions;

import java.util.List;

import com.ems.model.Event;
import com.ems.model.Ticket;
import com.ems.service.AdminService;
import com.ems.service.EventService;
import com.ems.util.ApplicationUtil;

public class AdminTicketManagementAction {
    private final EventService eventService;
    private final AdminService adminService;

    public AdminTicketManagementAction() {
        this.eventService = ApplicationUtil.eventService();
        this.adminService = ApplicationUtil.adminService();
    }

    public List<Event> getAvailableEvents() {
        return eventService.listAvailableEvents();
    }

    public List<Ticket> getTicketsForEvent(int eventId) {
        return eventService.getTicketTypes(eventId);
    }

    public void getEventWiseRegistrations(int eventId) {
        adminService.getEventWiseRegistrations(eventId);
    }
}