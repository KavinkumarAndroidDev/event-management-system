package com.ems.util;

import java.time.format.DateTimeFormatter;
import java.util.List;

import com.ems.model.BookingDetail;
import com.ems.model.Category;
import com.ems.model.Event;
import com.ems.model.Offer;
import com.ems.model.Ticket;
import com.ems.model.User;
import com.ems.model.UserEventRegistration;
import com.ems.model.Venue;
import com.ems.service.EventService;

/*
 * Provides reusable console display helpers for menu-driven flows.
 *
 * Responsibilities:
 * - Format and render domain objects for console output
 * - Centralize presentation logic for menus
 * - Keep menu classes focused on navigation and user interaction
 *
 * Acts as a lightweight view layer for the CLI application.
 */
public class MenuHelper {

	private static EventService eventService = ApplicationUtil.eventService();
	

	public static void printEventDetails(List<Event> events) {
		if(!events.isEmpty()) {
			events.forEach(event -> {
				String category = eventService.getCategory(event.getCategoryId()).getName();
				String venueName = eventService.getVenueName(event.getVenueId());
				String venueAddress = eventService.getVenueAddress(event.getVenueId());
				int totalAvailable = eventService.getAvailableTickets(event.getEventId());
				List<Ticket> tickets = eventService.getTicketTypes(event.getEventId());

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
		    });
		}
	}

    public static void printEventSummaries(List<Event> events) {
        if(!events.isEmpty()) {
        	System.out.println("\nAvailable Events");
            System.out.println("----------------------------------------------");
            
            int displayIndex = 1;
            for (Event event : events) {
                String category = eventService.getCategory(event.getCategoryId()).getName();
    			int totalAvailable = eventService.getAvailableTickets(event.getEventId());

    			System.out.println(
    					displayIndex + " | Title: " +
    						    event.getTitle() + " | Category: " +
    						    category + " | " +
    						    DateTimeUtil.formatDateTime(event.getStartDateTime()) +
    						    " | Tickets: " + totalAvailable 
    			);
    			

    			displayIndex++;
            }

            System.out.println("----------------------------------------------");
        }
    }
    
    public static void displayUsers(List<User> users) {
    	if (users == null || users.isEmpty()) {
    	    System.out.println("No users found.");
    	    return;
    	}

    	if(!users.isEmpty()) {
            System.out.println("\n==============================================================");
            System.out.printf(
                "%-5s %-5s %-20s %-10s %-25s %-15s %-10s%n",
                "NO" ,"ID", "Name", "Gender", "Email", "Phone", "Status"
            );
            System.out.println("==============================================================");
            int displayIndex = 1;
            for(User user: users) {
                System.out.printf(
                    "%-5d %-5d %-20s %-10s %-25s %-15s %-10s%n",
                    displayIndex,
                    user.getUserId(),
                    user.getFullName(),
                    user.getGender(),
                    user.getEmail(),
                    user.getPhone() == null ? "-" : user.getPhone(),
                    user.getStatus()
                );
                displayIndex++;
            }

            System.out.println("==============================================================");
    	}
        
    }

	public static void printEventDetails(Event event) {
		String category = eventService.getCategory(event.getCategoryId()).getName();
		String venueName = eventService.getVenueName(event.getVenueId());
		String venueAddress = eventService.getVenueAddress(event.getVenueId());
		int totalAvailable = eventService.getAvailableTickets(event.getEventId());
		List<Ticket> tickets = eventService.getTicketTypes(event.getEventId());

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

	public static void displayCategories(List<Category> categories) {

	    int index = 1;
	    System.out.println("\nAvailable categories:");
	    for (Category c : categories) {
	        System.out.println(
	            index + ". " +
	            c.getName()
	        );
	        index++;
	    }
	}

	public static void displayVenues(List<Venue> venues) {
		int index = 1;
		System.out.println("\nAvailable venues:");
		for(Venue venue: venues) {
			System.out.println(
					index + ". " + venue.getName());
			index++;
		}
		
	}
	
	public static void displayOffers(List<Offer> offers) {

	    int index = 1;
	    System.out.println("\nAvailable offers");
	    for (Offer o : offers) {
	    	Event event = eventService.getEventById(o.getEventId());
	        System.out.println(
	        			index + ". " 
	            		+"Event: " + event.getTitle()  
	            		+ "\nOffer code: " + o.getCode()
	            		+ "\nDiscount: " + o.getDiscountPercentage() + "%"
	            		+ "\nValidity: " + DateTimeUtil.formatDateTime(o.getValidFrom()) + " to " + DateTimeUtil.formatDateTime(o.getValidTo())
	        );
	        System.out.println("----------------------------------------------");
	        index++;
	    }
	}

	public static void printTicketSummaries(List<Ticket> tickets) {
		if(!tickets.isEmpty()) {
			System.out.println("\nAvailable ticket types: ");			
			
			int displayIndex = 1;
	        for (Ticket ticket: tickets) {
	        	System.out.println(
	                    displayIndex + " | " +
	                    ticket.getTicketType() + " | ₹" +
	                    ticket.getPrice() + " | " +
	                    "Tickets: " + ticket.getAvailableQuantity() +"/" + ticket.getTotalQuantity()
	                );

	                displayIndex++;
	        }
		}else {
			System.out.println("No ticket types for the given event id");
			return;
		}
		
	}
	
    public static void printBookingDetails(List<BookingDetail> bookingDetails) {
        System.out.println("Booking Details\n");
        for (BookingDetail b : bookingDetails) {
            System.out.println("------------------------------------------");
            System.out.println("Event  : " + b.getEventName());
            System.out.println("Venue : " + b.getVenueName() + " (" + b.getCity() + ")");
            System.out.println("Tickets: " + b.getTicketType() + " x " + b.getQuantity() + " tickets");
            System.out.println("Total : ₹" + b.getTotalCost());
        }
        System.out.println("------------------------------------------");
    }
    
    
    public static void printEventsList(List<UserEventRegistration> events) {
        System.out.println("\n--------------------------------------------------------------------------------------");
        System.out.printf(
            "%-3s %-22s %-12s %-12s %-18s %-7s%n",
            "No", "Title", "Category", "Ticket Type", "Date & Time", "Tickets"
        );
        System.out.println("--------------------------------------------------------------------------------------");

        int i = 1;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");
        for (UserEventRegistration r : events) {
            String title = r.getTitle();
            String displayTitle = (title != null && title.length() > 22) 
                ? title.substring(0, 22 - 3) + "..." 
                : title;

            String category = r.getCategory();
            String displayCategory = (category != null && category.length() > 12) 
                ? category.substring(0, 12 - 3) + "..." 
                : category;
            
            String ticketType = r.getTicketType();
            String displayTicketType =
                (ticketType != null && ticketType.length() > 12)
                    ? ticketType.substring(0, 9) + "..."
                    : ticketType;

            System.out.printf(
                "%-3d %-22s %-12s %-12s %-18s %-7d%n",
                i++,
                displayTitle,
                displayCategory,
                displayTicketType,
                r.getStartDateTime().format(formatter),
                r.getTicketsPurchased()
            );
        }
        System.out.println("--------------------------------------------------------------------------------------");
    }

}
