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

public class EventRegistrationAction {
    private final EventService eventService;

    public EventRegistrationAction() {
        this.eventService = ApplicationUtil.eventService();
    }

    public void registerForEvent(int userId) {
		List<Event> events = eventService.getAllEvents();
        if (events == null || events.isEmpty()) {
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

        List<Ticket> tickets = eventService.getTicketTypes(eventId);
        if (tickets == null ||tickets.isEmpty()) {
            System.out.println("No ticket types available for this event.");
            return;
        }

        MenuHelper.printTicketSummaries(tickets);;
        
        int ticketChoice = InputValidationUtil.readInt(
        		ScannerUtil.getScanner(),
        		"Select a ticket (1-" + tickets.size() + "): "
        );
        while (ticketChoice < 1 || ticketChoice > tickets.size()) {
        	ticketChoice = InputValidationUtil.readInt(
        			ScannerUtil.getScanner(),
        	        "Enter a valid choice: "
        	    );
        }
        Ticket selectedTicket = tickets.get(ticketChoice - 1);
        int ticketId = selectedTicket.getTicketId();

        int quantity = InputValidationUtil.readInt(ScannerUtil.getScanner(), "Enter number of tickets: ");
        while (quantity <= 0 || quantity > selectedTicket.getAvailableQuantity()) {
            quantity = InputValidationUtil.readInt(
                ScannerUtil.getScanner(),
                "Enter quantity (1-" + selectedTicket.getAvailableQuantity() + "): "
            );
        }

        
        
        System.out.println("\nAvailable payment methods:");

        PaymentMethod[] methods = PaymentMethod.values();
        for (int i = 0; i < methods.length; i++) {
            System.out.println(
                (i + 1) + ". " + methods[i].name().replace("_", " ")
            );
        }

        int paymentChoice = InputValidationUtil.readInt(
            ScannerUtil.getScanner(),
            "Enter choice (1-" + methods.length + "): "
        );

        while (paymentChoice < 1 || paymentChoice > methods.length) {
            System.out.println("Please select a valid payment method.");
            paymentChoice = InputValidationUtil.readInt(
                ScannerUtil.getScanner(),
                "Enter choice (1-" + methods.length + "): "
            );
        }
        
        System.out.println("Total amount: ₹" + quantity * selectedTicket.getPrice());
        PaymentMethod selectedMethod = methods[paymentChoice - 1];
        
        String offerCode = InputValidationUtil.readString(
        	    ScannerUtil.getScanner(),"Enter offer code (press Enter to skip): ");

        	if (offerCode.isBlank()) {
        	    offerCode = "";
        	}else {
        		offerCode = offerCode.trim().toUpperCase();
        	}

        	boolean success = eventService.registerForEvent(userId, eventId, ticketId, quantity, selectedTicket.getPrice(), selectedMethod,  offerCode);
            if (success) {
                System.out.println("Registration successful. Your tickets are confirmed.");
            } else {
                System.out.println("Registration failed. Please try again.\n");
            }
	}
    public void cancelRegistration(int userId, int registrationId) {
    	
    	
        eventService.cancelRegistration(userId, registrationId);
    }
}