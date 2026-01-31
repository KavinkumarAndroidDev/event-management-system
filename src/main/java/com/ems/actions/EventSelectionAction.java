package com.ems.actions;

import java.util.List;

import com.ems.model.Event;
import com.ems.service.EventService;
import com.ems.util.ApplicationUtil;

public class EventSelectionAction {
	private final EventService eventService;

    public EventSelectionAction() {
        this.eventService = ApplicationUtil.eventService();
    }

    public List<Event> getAvailableEvents() {
        return eventService.listAvailableEvents();
    }

    public Event getEventByIndex(List<Event> events, int index) {
        if (index < 1 || index > events.size()) {
            return null;
        }
        return events.get(index - 1);
    }
}
