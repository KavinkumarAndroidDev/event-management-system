package com.ems.actions;

import java.util.List;

import com.ems.model.Event;
import com.ems.model.Ticket;
import com.ems.service.AdminService;
import com.ems.service.EventService;
import com.ems.util.AdminMenuHelper;
import com.ems.util.ApplicationUtil;
import com.ems.util.InputValidationUtil;
import com.ems.util.MenuHelper;
import com.ems.util.ScannerUtil;

public class AdminEventManagementAction {
    private final AdminService adminService;
    private final EventService eventService;

    public AdminEventManagementAction() {
        this.adminService = ApplicationUtil.adminService();
        this.eventService = ApplicationUtil.eventService();
    }
    
    
    public void listAllEvents(){
    	List<Event> events = getAllEvents();
		if (events.isEmpty()) {
			System.out.println("No events available at the moment.");
		} else {
			AdminMenuHelper.printAllEventsWithStatus(events);
		}
    }
    
	public void printEventDetails() {
		Event selectedEvent = selectAnyEvent();
		if (selectedEvent == null)
			return;

		MenuHelper.printEventDetails(selectedEvent);
	}
	
    public void listTicketsForEvent() {
    	Event selectedEvent = selectAnyEvent();
		if (selectedEvent == null)
			return;

		List<Ticket> tickets = eventService.getTicketTypes(selectedEvent.getEventId());

		if (tickets.isEmpty()) {
			System.out.println("No ticket options are available for this event.");
			return;
		}

		AdminMenuHelper.printTicketDetails(tickets);
    }
	
    
    
    public List<Event> getAllEvents() {
        return eventService.getAllEvents();
    }
    
    public Event getEventById(int eventId) {
        return eventService.getEventById(eventId);
    }



    public List<Event> getEventsAwaitingApproval() {
        return eventService.listEventsYetToApprove();
    }

    public void approveEvent(int adminId) {
    	
		List<Event> events = getEventsAwaitingApproval();

		if (events == null || events.isEmpty()) {
			System.out.println("There are no events waiting for approval.");
			return;
		}

		MenuHelper.printEventSummaries(events);

		int eventChoice = InputValidationUtil.readInt(ScannerUtil.getScanner(),
				"Select an event to approve (1-" + events.size() + "): ");

		while (eventChoice < 1 || eventChoice > events.size()) {
			eventChoice = InputValidationUtil.readInt(ScannerUtil.getScanner(), "Enter a valid choice: ");
		}

		Event selectedEvent = events.get(eventChoice - 1);
		char approveChoice = InputValidationUtil.readChar(ScannerUtil.getScanner(),
				"Approve this event? (Y/N)\n");
		if (approveChoice == 'Y' || approveChoice == 'y') {
			adminService.approveEvents(adminId, selectedEvent.getEventId());
			System.out.println("Event approved successfully and the organizer has been notified.");
		} else {
			System.out.println("Action cancelled by user.");
		}
    	
    	
        
    }

    public List<Event> getAvailableAndDraftEvents() {
        return eventService.listAvailableAndDraftEvents();
    }

    public void cancelEvent() {
    	List<Event> events = getAvailableAndDraftEvents();

		if (events.isEmpty()) {
			System.out.println("No events available to cancel");
			return;
		}

		MenuHelper.printEventSummaries(events);

		int eventChoice = InputValidationUtil.readInt(ScannerUtil.getScanner(),
				"Select an event to cancel (1-" + events.size() + "): ");

		while (eventChoice < 1 || eventChoice > events.size()) {
			eventChoice = InputValidationUtil.readInt(ScannerUtil.getScanner(), "Enter a valid choice: ");
		}

		Event selectedEvent = events.get(eventChoice - 1);

		char cancelChoice = InputValidationUtil.readChar(ScannerUtil.getScanner(),
				"Cancel this event? (Y/N)\n");
		if (cancelChoice == 'Y' || cancelChoice == 'y') {
			adminService.cancelEvent(selectedEvent.getEventId());

			System.out.println("Event cancelled successfully.");
		} else {
			System.out.println("Action cancelled by user.");
		}
    }

    public void markCompletedEvents() {
        adminService.markCompletedEvents();
    }

    public int getAvailableTickets(int eventId) {
        return eventService.getAvailableTickets(eventId);
    }
    
	public Event selectAnyEvent() {

		List<Event> events = getAllEvents();

		if (events.isEmpty()) {
			System.out.println("No events available at the moment.");
			return null;
		}

		MenuHelper.printEventSummaries(events);

		int choice = InputValidationUtil.readInt(ScannerUtil.getScanner(),
				"Select an event (1-" + events.size() + "): ");

		while (choice < 1 || choice > events.size()) {
			choice = InputValidationUtil.readInt(ScannerUtil.getScanner(), "Enter a valid choice: ");
		}

		return events.get(choice - 1);
	}


}