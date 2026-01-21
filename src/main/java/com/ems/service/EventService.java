package com.ems.service;

import java.util.List;
import java.util.Scanner;

import com.ems.dao.CategoryDao;
import com.ems.dao.EventDao;
import com.ems.dao.TicketDao;
import com.ems.dao.VenueDao;
import com.ems.dao.impl.*;
import com.ems.model.Event;
import com.ems.model.Ticket;
import com.ems.util.DateTimeUtil;
import com.ems.util.InputValidationUtil;
import com.ems.util.ScannerUtil;

public class EventService {
	private static EventDao eventDao = new EventDaoImpl();
	private static CategoryDao categoryDao = new CategoryDaoImpl();
	private static VenueDao venueDao = new VenueDaoImpl();
	private static TicketDao ticketDao = new TicketDaoImpl();

	public static void printAllAvailableEvents() {
		List<Event> events = eventDao.listAvailableEvents();
		if(events.isEmpty()) {
			System.out.println("There are no available events!");
			return;
		}
		printEventSummaries(events);
		
	}
	
	public static void printAllEvents() {
		
		List<Event> events = eventDao.listAllEvents();
		if(events.isEmpty()) {
			System.out.println("There are no events!");
			return;
		}
		printEventDetails(events);
	}

	
	public static void printEventDetails(List<Event> events) {
		for (Event event : events) {

	        String category = categoryDao.getCategory(event.getCategoryId());
	        String venueName = venueDao.getVenueName(event.getVenueId());
	        String venueAddress = venueDao.getVenueAddress(event.getVenueId());
	        int totalAvailable = ticketDao.getAvailableTickets(event.getEventId());
	        List<Ticket> tickets = ticketDao.getTicketTypes(event.getEventId());

	        System.out.println("\n==============================================");
	        System.out.println("Event ID        : " + event.getEventId());
	        System.out.println("Title           : " + event.getTitle());

	        if (event.getDescription() != null) {
	            System.out.println("Description     : " + event.getDescription());
	        }

	        System.out.println("Category        : " + category);
	        System.out.println("Duration        : "
	                + DateTimeUtil.formatDateTime(event.getStartDateTime())
	                + " to "
	                + DateTimeUtil.formatDateTime(event.getEndDateTime()));

	        System.out.println("Total Tickets   : " + totalAvailable);

	        System.out.println("\nTicket Types");
	        System.out.println("----------------------------------------------");

	        for (Ticket ticket : tickets) {
	            System.out.println("• "
	                    + ticket.getTicketType()
	                    + " | Price: ₹"
	                    + ticket.getPrice()
	                    + " | Available: "
	                    + ticket.getAvailableQuantity());
	        }

	        System.out.println("\nVenue");
	        System.out.println("----------------------------------------------");
	        System.out.println("Name            : " + venueName);
	        System.out.println("Address         : " + venueAddress);

	        System.out.println("==============================================");
	    }
	}
	
	public static void printEventSummaries(List<Event> events) {

	    System.out.println("\nAvailable Events");
	    System.out.println("----------------------------------------------");

	    for (Event event : events) {

	        String category = categoryDao.getCategory(event.getCategoryId());
	        int totalAvailable = ticketDao.getAvailableTickets(event.getEventId());

	        System.out.println(
	            event.getEventId()
	            + " | "
	            + event.getTitle()
	            + " | "
	            + category
	            + " | "
	            + DateTimeUtil.formatDateTime(event.getStartDateTime())
	            + " | Tickets: "
	            + totalAvailable
	        );
	    }

	    System.out.println("----------------------------------------------");
	}
	


	public static void viewEventDetails() {

	    int eventId = InputValidationUtil.readInt(
	        ScannerUtil.getScanner(),
	        "Enter Event ID: "
	    );

	    Event event = new Event();
		try {
			event = eventDao.getEventById(eventId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	    if (event == null) {
	        System.out.println("Event not found!");
	        return;
	    }

	    printEventDetails(event);
	}


	private static void printEventDetails(Event event) {
		String category = categoryDao.getCategory(event.getCategoryId());
        String venueName = venueDao.getVenueName(event.getVenueId());
        String venueAddress = venueDao.getVenueAddress(event.getVenueId());
        int totalAvailable = ticketDao.getAvailableTickets(event.getEventId());
        List<Ticket> tickets = ticketDao.getTicketTypes(event.getEventId());

        System.out.println("\n==============================================");
        System.out.println("Event ID        : " + event.getEventId());
        System.out.println("Title           : " + event.getTitle());

        if (event.getDescription() != null) {
            System.out.println("Description     : " + event.getDescription());
        }

        System.out.println("Category        : " + category);
        System.out.println("Duration        : "
                + DateTimeUtil.formatDateTime(event.getStartDateTime())
                + " to "
                + DateTimeUtil.formatDateTime(event.getEndDateTime()));

        System.out.println("Total Tickets   : " + totalAvailable);

        System.out.println("\nTicket Types");
        System.out.println("----------------------------------------------");

        for (Ticket ticket : tickets) {
            System.out.println("• "
                    + ticket.getTicketType()
                    + " | Price: ₹"
                    + ticket.getPrice()
                    + " | Available: "
                    + ticket.getAvailableQuantity());
        }

        System.out.println("\nVenue");
        System.out.println("----------------------------------------------");
        System.out.println("Name            : " + venueName);
        System.out.println("Address         : " + venueAddress);

        System.out.println("==============================================");
    
		
	}

	public static void searchEvents() {
		while(true) {
			System.out.println("\nEnter your choice:\n"
					+ "1. Search by category\r\n"
					+ "2. Search by date\r\n"
					+ "3. Search by city\r\n"
					+ "4. Filter by price\r\n"
					+ "5. Filter by availability\r\n"
					+ "6. Exit to user menu"
			        + "\n>");
			int filterChoice = InputValidationUtil.readInt(ScannerUtil.getScanner(), "");
			switch(filterChoice) {
				case 1:
					EventService.searchBycategory();
					break;
				case 2:
					EventService.searchByDate();
					break;
				case 3:
					EventService.searchByCity();
					break;
				case 4:
					EventService.filterByPrice();
					break;
				case 5:
					EventService.printAllAvailableEvents();
					break;
				case 6:
					return;
				default:
					System.out.println("Enter the valid option");
					break;
			}
		}
		
	}

	private static void filterByPrice() {
		// TODO Auto-generated method stub
		
	}

	private static void searchByCity() {
		// TODO Auto-generated method stub
		
	}

	private static void searchByDate() {
		// TODO Auto-generated method stub
		
	}

	private static void searchBycategory() {
		// TODO Auto-generated method stub
		
	}

	public static void viewTicketOptions() {
		// TODO Auto-generated method stub
		
	}

	public static void registerForEvent(int userId) {
		// TODO Auto-generated method stub
		
	}

	public static void viewUpcomingEvents(int userId) {
		// TODO Auto-generated method stub
		
	}

	public static void viewPastEvents(int userId) {
		// TODO Auto-generated method stub
		
	}

	public static void viewBookingDetails(int userId) {
		// TODO Auto-generated method stub
		
	}

	public static void submitRating(int userId) {
		// TODO Auto-generated method stub
		
	}

	public static void submitReview(int userId) {
		// TODO Auto-generated method stub
		
	}

	public static void createTicket() {
		// TODO Auto-generated method stub
		
	}

	public static void updateTicketPrice() {
		// TODO Auto-generated method stub
		
	}

	public static void updateTicketQuantity() {
		// TODO Auto-generated method stub
		
	}

	public static void viewTicketAvailability() {
		// TODO Auto-generated method stub
		
	}

	public static void createEvent() {
		// TODO Auto-generated method stub
		
	}

	public static void updateEventDetails() {
		// TODO Auto-generated method stub
		
	}

	public static void updateEventSchedule() {
		// TODO Auto-generated method stub
		
	}

	public static void updateEventCapacity() {
		// TODO Auto-generated method stub
		
	}

	public static void publishEvent() {
		// TODO Auto-generated method stub
		
	}

	public static void cancelEvent() {
		// TODO Auto-generated method stub
		
	}

}
