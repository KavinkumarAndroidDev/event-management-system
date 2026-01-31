package com.ems.actions;

import java.util.List;

import com.ems.enums.PaymentMethod;
import com.ems.model.Event;
import com.ems.model.Ticket;
import com.ems.service.EventService;
import com.ems.util.ApplicationUtil;
import com.ems.util.InputValidationUtil;
import com.ems.util.MenuHelper;
import com.ems.util.ScannerUtil;

public class EventBrowsingAction {
    private final EventService eventService;

    public EventBrowsingAction() {
        this.eventService = ApplicationUtil.eventService();
    }

    public List<Event> getAllAvailableEvents() {
        return eventService.listAvailableEvents();
    }

    public Event getEventByIndex(List<Event> events, int index) {
        if (index < 1 || index > events.size()) {
            return null;
        }
        return events.get(index - 1);
    }

    public List<Ticket> getTicketsForEvent(int eventId) {
        return eventService.getTicketTypes(eventId);
    }

	public void printAllAvailableEvents() {
		List<Event> filteredEvents = getAllAvailableEvents();
		if(filteredEvents.isEmpty()) {
			System.out.println("There is no available events!");
			return;
		}
		MenuHelper.printEventSummaries(filteredEvents);
		
	}

	public void viewEventDetails() {
    	List<Event> events = getAllAvailableEvents();
    	if (events.isEmpty()) {
		    System.out.println("No events available at the moment.");
		    return;
		}
		
    	MenuHelper.printEventSummaries(events);
    	int choice = InputValidationUtil.readInt(
	    	    ScannerUtil.getScanner(),
	    	    "Select an event number (1-" + events.size() + "): "
    	);
    	while (choice < 1 || choice > events.size()) {
    	    choice = InputValidationUtil.readInt(
    	        ScannerUtil.getScanner(),
    	        "Enter a valid choice: "
    	    );
    	}
    	Event selectedEvent = events.get(choice - 1);
    	MenuHelper.printEventDetails(selectedEvent);	
	}
	
	public void viewTicketOptions() {
		List<Event> events = getAllAvailableEvents();
		if (events.isEmpty()) {
		    System.out.println("No events available at the moment.");
		    return;
		}
		
    	MenuHelper.printEventSummaries(events);
    	int choice = InputValidationUtil.readInt(
	    	    ScannerUtil.getScanner(),
	    	    "Select an event number (1-" + events.size() + "): "
    	);
    	while (choice < 1 || choice > events.size()) {
    	    choice = InputValidationUtil.readInt(
    	        ScannerUtil.getScanner(),
    	        "Enter a valid choice: "
    	    );
    	}
    	Event selectedEvent = events.get(choice - 1);
		int eventId = selectedEvent.getEventId();

		List<Ticket> tickets = getTicketsForEvent(eventId);
		
		if(!tickets.isEmpty()) {
		    MenuHelper.printTicketSummaries(tickets);
		}else {
			System.out.println("No ticket types available for this event.\n");
			return;
		}
		
	}
	
	
}