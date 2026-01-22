package com.ems.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.ems.dao.*;
import com.ems.exception.DataAccessException;
import com.ems.model.BookingDetail;
import com.ems.model.Event;
import com.ems.model.Ticket;
import com.ems.service.EventService;
import com.ems.service.PaymentService;
import com.ems.util.DateTimeUtil;
import com.ems.util.InputValidationUtil;
import com.ems.util.ScannerUtil;

public class EventServiceImpl implements EventService {

    private final EventDao eventDao;
    private final CategoryDao categoryDao;
    private final VenueDao venueDao;
    private final TicketDao ticketDao;
    private final PaymentService paymentService;

    public EventServiceImpl(
            EventDao eventDao,
            CategoryDao categoryDao,
            VenueDao venueDao,
            TicketDao ticketDao,
            PaymentService paymentService
    ) {
        this.eventDao = eventDao;
        this.categoryDao = categoryDao;
        this.venueDao = venueDao;
        this.ticketDao = ticketDao;
        this.paymentService = paymentService;
    }
	
    
	//details fetch functions
    @Override
	public void viewEventDetails() {

    	List<Event> events = new ArrayList<>();
		try {
			events = eventDao.listAvailableEvents();
			if (events.isEmpty()) {
			    System.out.println("There are no available events!");
			    return;
			}
		} catch (DataAccessException e) {
			System.out.println(e.getMessage());
		}
    	printEventSummaries(events);
    	int choice = InputValidationUtil.readInt(
	    	    ScannerUtil.getScanner(),
	    	    "Select an event (1-" + events.size() + "): "
	    	    
    	);
    	while (choice < 1 || choice > events.size()) {
    	    choice = InputValidationUtil.readInt(
    	        ScannerUtil.getScanner(),
    	        "Enter a valid choice: "
    	    );
    	}
    	Event selectedEvent = events.get(choice - 1);
    	printEventDetails(selectedEvent);
	}
    @Override
	public  void viewTicketOptions() {
		List<Event> events = null;
		try {
			events = eventDao.listAvailableEvents();
		} catch (DataAccessException e) {
			System.out.println(e.getMessage());
		}
		if(events.isEmpty()) {
			System.out.println("There are no available events!");
			return;
		}
		printEventSummaries(events);
		int choice = InputValidationUtil.readInt(ScannerUtil.getScanner(), "Select event number: ");
		while (choice < 1 || choice > events.size()) {
		    choice = InputValidationUtil.readInt(
		        ScannerUtil.getScanner(),
		        "Enter a valid choice: "
		    );
		}
		Event selectedEvent = events.get(choice - 1);
		int eventId = selectedEvent.getEventId();

		List<Ticket> tickets = new ArrayList<>();
		try {
			tickets = ticketDao.getTicketTypes(eventId);
		} catch (DataAccessException e) {
			System.out.println(e.getMessage());
		}
		if(!tickets.isEmpty()) {
			System.out.println("\nAvailable ticekt types: ");
			tickets.forEach(t -> System.out.println(t));
		}else {
			System.out.println("No ticket types for the given event id");
			return;
		}
		
	}
    @Override
	public  void filterByPrice() {
		double minPrice = InputValidationUtil.readDouble(ScannerUtil.getScanner(), "Enter the minimum price: ");
		double maxPrice = InputValidationUtil.readDouble(ScannerUtil.getScanner(), "Enter the maximum price: ");
		
	    try {
	        List<Event> allEvents = eventDao.listAvailableEvents();

	        List<Event> filteredEvents = allEvents.stream()
	            .filter(event -> {
	                List<Ticket> tickets = new ArrayList<>();
					try {
						tickets = ticketDao.getTicketTypes(event.getEventId());
					} catch (DataAccessException e) {
						System.out.println(e.getMessage());
					}
	                
	                return tickets.stream()
	                    .anyMatch(t -> t.getPrice() >= minPrice && 
	                                   t.getPrice() <= maxPrice);
	            })
	            .collect(Collectors.toList());
            if(filteredEvents.isEmpty()) {
            	System.out.println("No events available on given range!");
            }else {
            	System.out.println("--- Events found: " + filteredEvents.size() + " ---");
    	        filteredEvents.forEach(e -> printEventDetails(e));
            }

	    } catch (DataAccessException e) {
	        System.err.println("Database error: " + e.getMessage());
	    }
	}

    @Override
	public  void searchByCity() {
		Map<Integer, String> cities = new HashMap<>();
		try {
			cities = venueDao.getAllCities();
		} catch (DataAccessException e) {
			System.out.println(e.getMessage());
		}
		if(!cities.isEmpty()) {
			cities.forEach((key, value) -> System.out.println(key + ". " + value));
		}else {
			System.out.println("No cities found!");
			return;
		}
		int cityId = InputValidationUtil.readInt(ScannerUtil.getScanner(), "Enter the city id:");
		while(!cities.containsKey(cityId)) {			
			cityId = InputValidationUtil.readInt(ScannerUtil.getScanner(), "Enter the valid city id:");
		}
		final int selectedCityId = cityId;
		//java requires it to be final to ensure that the value the Lambda "sees" is the same value that actually exists in your code.
		try {
			List<Event> allEvents = eventDao.listAvailableEvents();
			List<Event> filteredEvents = allEvents.stream().filter(e-> e.getVenueId() == selectedCityId).collect(Collectors.toList());
			if (filteredEvents.isEmpty()) {
			    System.out.println("No events found for the selected city.");
			} else {
	            System.out.println("--- Events found: " + filteredEvents.size() + " ---");

			    filteredEvents.forEach(e -> printEventDetails(e));
			}

		} catch (DataAccessException e) {
			System.err.println("Database error: " + e.getMessage());
		}
		
	}
    @Override
	public void searchByDate() {
		LocalDate localDate = DateTimeUtil.getLocalDate("Enter the date to get available event from the given date:");
		
		try {
			List<Event> allEvents = eventDao.listAvailableEvents();
			List<Event> filteredEvents = allEvents.stream().filter(e -> e.getStartDateTime().toLocalDate().isBefore(localDate)).collect(Collectors.toList());
			if (filteredEvents.isEmpty()) {
			    System.out.println("No events after the selected date!");
			} else {
	            System.out.println("--- Events found: " + filteredEvents.size() + " ---");

			    filteredEvents.forEach(e -> printEventDetails(e));
			}

		} catch (DataAccessException e) {
			System.err.println("Database error: " + e.getMessage());
		}
	}
    @Override
	public void searchByDateRange() {
	    LocalDate startDate = DateTimeUtil.getLocalDate("Enter start date (dd-mm-yyyy):");
	    LocalDate endDate = DateTimeUtil.getLocalDate("Enter end date (dd-mm-yyyy):");

	    if (startDate.isAfter(endDate)) {
	        System.out.println("Error: Start date cannot be after end date.");
	        return;
	    }

	    try {
	        List<Event> allEvents = eventDao.listAvailableEvents();
	        
	        List<Event> filteredEvents = allEvents.stream()
	            .filter(e -> {
	                LocalDate eventDate = e.getStartDateTime().toLocalDate();
	                return !eventDate.isBefore(startDate) && !eventDate.isAfter(endDate);
	            })
	            .collect(Collectors.toList());

	        if (filteredEvents.isEmpty()) {
	            System.out.println("No events found between " + startDate + " and " + endDate);
	        } else {
	            System.out.println("--- Events found: " + filteredEvents.size() + " ---");
	            filteredEvents.forEach(e -> printEventDetails(e));
	        }
	    } catch (DataAccessException e) {
	        System.err.println("Database error: " + e.getMessage());
	    }
	}

    @Override
	public void searchBycategory() {
		Map<Integer, String> categories = new HashMap<>();
		try {
			categories = categoryDao.getAllCategories();
		} catch (DataAccessException e) {
			System.out.println(e.getMessage());
		}
		if(!categories.isEmpty()) {
			categories.forEach((key, value) -> System.out.println(key + ". " + value));
		}else {
			System.out.println("No cities found!");
			return;
		}
		int categoryId = InputValidationUtil.readInt(ScannerUtil.getScanner(), "Enter the category id:");
		while(!categories.containsKey(categoryId)) {			
			categoryId = InputValidationUtil.readInt(ScannerUtil.getScanner(), "Enter the valid category id:");
		}
		final int selectedCategoryId = categoryId;
		//java requires it to be final to ensure that the value the Lambda "sees" is the same value that actually exists in your code.
		try {
			List<Event> allEvents = eventDao.listAvailableEvents();
			List<Event> filteredEvents = allEvents.stream().filter(e-> e.getCategoryId() == selectedCategoryId).collect(Collectors.toList());
			if (filteredEvents.isEmpty()) {
			    System.out.println("No events found for the selected category.");
			} else {
	            System.out.println("--- Events found: " + filteredEvents.size() + " ---");

			    filteredEvents.forEach(e -> printEventDetails(e));
			}

		} catch (DataAccessException e) {
			System.err.println("Database error: " + e.getMessage());
		}
		
	}

    @Override
	//Registration function
	public void registerForEvent(int userId) {
	    try {
	        List<Event> events = eventDao.listAvailableEvents();
	        if (events == null || events.isEmpty()) {
	            System.out.println("There are no available events!");
	            return;
	        }

	        printEventSummaries(events);
	        int choice = InputValidationUtil.readInt(ScannerUtil.getScanner(),"Select an event (1-" + events.size() + "): ");
	        while (choice < 1 || choice > events.size()) {
	        	choice = InputValidationUtil.readInt(
	        			ScannerUtil.getScanner(),
	        	        "Enter a valid choice: "
	        	    );
	        	}

	       Event selectedEvent = events.get(choice - 1);
	       int eventId = selectedEvent.getEventId();


	        // Validate Event Selection
	        List<Ticket> tickets = ticketDao.getTicketTypes(eventId);
	        if (tickets.isEmpty()) {
	            System.out.println("No ticket types available for this event.");
	            return;
	        }

	        System.out.println("\nAvailable Ticket Types:");
	        tickets.forEach(System.out::println);

	        int ticketId = InputValidationUtil.readInt(ScannerUtil.getScanner(), "Enter the ticket id: ");
	        
	        // Find the selected ticket to get its price and availability
	        Ticket selectedTicket = tickets.stream()
	                .filter(t -> t.getTicketId() == ticketId)
	                .findFirst()
	                .orElseThrow(() -> new IllegalArgumentException("Invalid Ticket ID"));

	        int quantity = InputValidationUtil.readInt(ScannerUtil.getScanner(), "How many tickets? ");

	        // Call the service to handle the DB transactions
	        boolean success = false;
			try {
				success = paymentService.processRegistration(
				    userId, eventId, ticketId, quantity, selectedTicket.getPrice()
				);
			} catch (Exception e) {
				e.printStackTrace();
			}

	        if (success) {
	            System.out.println("Registration successful! Enjoy your event.");
	        } else {
	            System.out.println("Registration failed. Please check ticket availability.");
	        }

	    } catch (Exception e) {
	        System.out.println("Error during registration: " + e.getMessage());
	    }
	}
    @Override
	//Event history of specific user
	public void viewUpcomingEvents(int userId) {
		List<Event> events = new ArrayList<>();
		try {
			events = eventDao.getUserEvents(userId);
			List<Event> upcomingEvents = events.stream().filter(e -> e.getStartDateTime().isAfter(LocalDateTime.now())).toList();	
			if(upcomingEvents.isEmpty()) {
				System.out.println("No upcoming events!");
				return;
			
			}
			System.out.println("--- Events found: " + upcomingEvents.size() + " ---");
			upcomingEvents.stream().forEach(e -> printEventSummaries(e));
		} catch(DataAccessException e) {
			System.out.println(e.getMessage());
		}
		
	}
    @Override
	public void viewPastEvents(int userId) {
		List<Event> events = new ArrayList<>();
		try {
			events = eventDao.getUserEvents(userId);
			List<Event> pastEvents = events.stream().filter(e -> e.getStartDateTime().isBefore(LocalDateTime.now())).toList();	
			if(pastEvents.isEmpty()) {
				System.out.println("No past events!");
				return;
			}
			System.out.println("--- Events found: " + pastEvents.size() + " ---");
			pastEvents.stream().forEach(e -> printEventSummaries(e));
			
		} catch (DataAccessException e) {
			System.out.println(e.getMessage());
		}
		
	}
    @Override
	public void viewBookingDetails(int userId) {
    	try {
            List<BookingDetail> bookings = eventDao.viewBookingDetails(userId);

            if (bookings.isEmpty()) {
                System.out.println("No bookings found");
                return;
            }

            for (BookingDetail b : bookings) {
                System.out.println("------------------------------------------");
                System.out.println("Event  : " + b.getEventName());
                System.out.println("Venue : " + b.getVenueName() + " (" + b.getCity() + ")");
                System.out.println("Tickets: " + b.getTicketType() + " x" + b.getQuantity());
                System.out.println("Total : ₹" + b.getTotalCost());
                System.out.println("------------------------------------------");
            }

        } catch (DataAccessException e) {
            System.out.println(e.getMessage());
        }
		
	}

    
    @Override
	//Rating features
	public void submitRating(int userId) {
		List<Event> events = new ArrayList<>();
		try {
			events = eventDao.getUserEvents(userId);
			if (events.isEmpty()) {
				System.out.println("No events registered by the user!");
				return;
			}
			List<Event> pastEvents = events.stream().filter(e -> e.getStartDateTime().isBefore(LocalDateTime.now())).toList();	
			if(pastEvents.isEmpty()) {
				System.out.println("No past events available to rate!");
				return;
			}
			printEventSummaries(pastEvents);

			int choice = InputValidationUtil.readInt(
			    ScannerUtil.getScanner(),
			    "Select an event (1-" + pastEvents.size() + "): "
			);

			while (choice < 1 || choice > pastEvents.size()) {
			    choice = InputValidationUtil.readInt(
			        ScannerUtil.getScanner(),
			        "Enter a valid choice: "
			    );
			}

			Event selectedEvent = pastEvents.get(choice - 1);
			int eventId = selectedEvent.getEventId();

			int rating = InputValidationUtil.readInt(ScannerUtil.getScanner(), "Enter the rating (1-5): ");
			while(rating >5 || rating <1) {
				rating = InputValidationUtil.readInt(ScannerUtil.getScanner(), "Enter the rating (1-5): ");
			}
			String comments = InputValidationUtil.readString(ScannerUtil.getScanner(), "Enter the feedback:\n(Optional, Press enter to skip)");
			if(comments.trim().isBlank()) {
				comments = null;
			}
			eventDao.submitRating(eventId,  userId, rating, comments);
			return;
		} catch (DataAccessException e) {
			System.out.println(e.getMessage());
		}
		
	}

	
	//Organizer functions
    @Override
	public void createTicket() {
		// TODO Auto-generated method stub
		
	}
    @Override
	public void updateTicketPrice() {
		// TODO Auto-generated method stub
		
	}
    @Override
	public void updateTicketQuantity() {
		// TODO Auto-generated method stub
		
	}
    @Override
	public void viewTicketAvailability() {
		// TODO Auto-generated method stub
		
	}
    @Override
	public void createEvent() {
		// TODO Auto-generated method stub
		
	}
    @Override
	public void updateEventDetails() {
		// TODO Auto-generated method stub
		
	}
    @Override
	public  void updateEventSchedule() {
		// TODO Auto-generated method stub
		
	}
    @Override
	public void updateEventCapacity() {
		// TODO Auto-generated method stub
		
	}
    @Override
	public void publishEvent() {
		// TODO Auto-generated method stub
		
	}
    @Override
	public void cancelEvent() {
		// TODO Auto-generated method stub
		
	}
    @Override
	//Admin functions
	//note: this is a temporary function to make the events note as completed
	public  void completeEvents() {
		try {
			eventDao.completeEvents();
		} catch (DataAccessException e) {
			System.out.println(e.getMessage());
		}
	}
	
	//Display functions:
    @Override
	public  void printAllAvailableEvents() {
		List<Event> events = null;
		try {
			events = eventDao.listAvailableEvents();
		} catch (DataAccessException e) {
			System.out.println(e.getMessage());
		}
		if(events.isEmpty()) {
			System.out.println("There are no available events!");
			return;
		}
		printEventSummaries(events);
		
	}
    @Override
	public void printAllEvents() {
		
		List<Event> events = null;
		try {
			events = eventDao.listAllEvents();
		} catch (DataAccessException e) {
			System.out.println(e.getMessage());
		}
		if(events.isEmpty()) {
			System.out.println("There are no events!");
			return;
		}
		printEventDetails(events);
	}

    @Override
	public void printEventDetails(List<Event> events) {
		events.stream().forEach(event -> {
			try {
				

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
			}catch(DataAccessException e) {
				System.out.println(e.getMessage());
			}
	    });
		
	}
    @Override
    public void printEventSummaries(List<Event> events) {
        System.out.println("\nAvailable Events");
        System.out.println("----------------------------------------------");
        
        int displayIndex = 1;
        for (Event event : events) {
            try {
                String category = categoryDao.getCategory(event.getCategoryId());
                int totalAvailable = ticketDao.getAvailableTickets(event.getEventId());

                System.out.println(
                    displayIndex + " | " +
                    event.getTitle() + " | " +
                    category + " | " +
                    DateTimeUtil.formatDateTime(event.getStartDateTime()) +
                    " | Tickets: " + totalAvailable
                );

                displayIndex++;
            } catch (DataAccessException e) {
                System.out.println(e.getMessage());
            }
        }

        System.out.println("----------------------------------------------");
    }

    private void printEventDetails(Event event) {
    	try {
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
	    
    	}catch(Exception e) {
    		System.out.println(e.getMessage());
    	}
	}
	
    private void printEventSummaries(Event event) {
    	try {
    		
    	
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
	    
	
	        System.out.println("----------------------------------------------");
		}catch(DataAccessException e) {
			System.out.println(e.getMessage());
		}
    }
	
}
