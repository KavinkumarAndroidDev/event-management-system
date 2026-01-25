package com.ems.util;

import java.util.Comparator;
import java.util.List;

import com.ems.model.Category;
import com.ems.model.Event;
import com.ems.model.Ticket;
import com.ems.model.User;
import com.ems.service.EventService;

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
    			    displayIndex + " | " +
    			    event.getTitle() + " | " +
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
    	if(!users.isEmpty()) {
            users.sort(Comparator.comparing(User::getFullName));
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

	    for (Category c : categories) {
	        System.out.println(
	            index + ". " +
	            c.getName()
	        );
	        index++;
	    }
	}

    
}
