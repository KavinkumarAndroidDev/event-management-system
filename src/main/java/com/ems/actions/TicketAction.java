package com.ems.actions;

import java.util.List;

import com.ems.model.Ticket;
import com.ems.service.EventService;
import com.ems.util.ApplicationUtil;

public class TicketAction {

    private final EventService eventService;

    public TicketAction() {
        this.eventService = ApplicationUtil.eventService();
    }

    public List<Ticket> getTicketsForEvent(int eventId) {
        return eventService.getTicketTypes(eventId);
    }
}
