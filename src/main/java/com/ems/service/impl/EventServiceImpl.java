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
    private final FeedbackDao feedbackDao;

    // initializes event service with required dependencies
    public EventServiceImpl(
            EventDao eventDao,
            CategoryDao categoryDao,
            VenueDao venueDao,
            TicketDao ticketDao,
            PaymentService paymentService,
            FeedbackDao feedbackDao
    ) {
        this.eventDao = eventDao;
        this.categoryDao = categoryDao;
        this.venueDao = venueDao;
        this.ticketDao = ticketDao;
        this.paymentService = paymentService;
        this.feedbackDao = feedbackDao;
    }
	
    // shows event details for selected event
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

    // shows available ticket options for an event
    @Override
	public void viewTicketOptions() {
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

    // filters events based on ticket price range
    @Override
	public void filterByPrice() {
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

    // searches events based on city
    @Override
	public void searchByCity() {
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
		try {
			List<Event> allEvents = eventDao.listAvailableEvents();
			List<Event> filteredEvents = allEvents.stream()
					.filter(e-> e.getVenueId() == selectedCityId)
					.collect(Collectors.toList());
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

    // searches events by date
    @Override
	public void searchByDate() {
		LocalDate localDate = DateTimeUtil.getLocalDate("Enter the date to get available event from the given date:");
		
		try {
			List<Event> allEvents = eventDao.listAvailableEvents();
			List<Event> filteredEvents = allEvents.stream()
					.filter(e -> e.getStartDateTime().toLocalDate().isBefore(localDate))
					.collect(Collectors.toList());
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

    // searches events within date range
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

    // searches events based on category
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
		try {
			List<Event> allEvents = eventDao.listAvailableEvents();
			List<Event> filteredEvents = allEvents.stream()
					.filter(e-> e.getCategoryId() == selectedCategoryId)
					.collect(Collectors.toList());
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

    // registers user for selected event
    @Override
	public void registerForEvent(int userId) {
	    try {
	        List<Event> events = eventDao.listAvailableEvents();
	        if (events == null || events.isEmpty()) {
	            System.out.println("There are no available events!");
	            return;
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
	        int eventId = selectedEvent.getEventId();

	        List<Ticket> tickets = ticketDao.getTicketTypes(eventId);
	        if (tickets == null ||tickets.isEmpty()) {
	            System.out.println("No ticket types available for this event.");
	            return;
	        }

	        System.out.println("\nAvailable Ticket Types:");
	        int displayIndex = 1;
	        for (Ticket ticket: tickets) {
	        	System.out.println(
	                    displayIndex + " | " +
	                    ticket.getTicketType() + " | ₹" +
	                    ticket.getPrice() + " | " +
	                    " | Tickets: " + ticket.getAvailableQuantity() +"/" + ticket.getTotalQuantity()
	                );

	                displayIndex++;
	        }
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
	        int ticketId = selectedEvent.getEventId();

	        int quantity = InputValidationUtil.readInt(ScannerUtil.getScanner(), "How many tickets? ");

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

    // shows upcoming events for user
    @Override
	public void viewUpcomingEvents(int userId) {
		List<Event> events = new ArrayList<>();
		try {
			events = eventDao.getUserEvents(userId);
			List<Event> upcomingEvents = events.stream()
					.filter(e -> e.getStartDateTime().isAfter(LocalDateTime.now()))
					.toList();	
			if(upcomingEvents.isEmpty()) {
				System.out.println("No upcoming events!");
				return;
			}
			System.out.println("--- Events found: " + upcomingEvents.size() + " ---");
			upcomingEvents.forEach(e -> printEventSummaries(e));
		} catch(DataAccessException e) {
			System.out.println(e.getMessage());
		}
	}

    // shows past events for user
    @Override
	public void viewPastEvents(int userId) {
		List<Event> events = new ArrayList<>();
		try {
			events = eventDao.getUserEvents(userId);
			List<Event> pastEvents = events.stream()
					.filter(e -> e.getStartDateTime().isBefore(LocalDateTime.now()))
					.toList();	
			if(pastEvents.isEmpty()) {
				System.out.println("No past events!");
				return;
			}
			System.out.println("--- Events found: " + pastEvents.size() + " ---");
			pastEvents.forEach(e -> printEventSummaries(e));
			
		} catch (DataAccessException e) {
			System.out.println(e.getMessage());
		}
	}

    // shows booking details for user
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

    // submits rating for past events
    @Override
	public void submitRating(int userId) {
		List<Event> events = new ArrayList<>();
		try {
			events = eventDao.getUserEvents(userId);
			if (events.isEmpty()) {
				System.out.println("No events registered by the user!");
				return;
			}
			List<Event> pastEvents = events.stream()
					.filter(e -> e.getStartDateTime().isBefore(LocalDateTime.now()))
					.toList();	
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
			String comments = InputValidationUtil.readString(
					ScannerUtil.getScanner(),
					"Enter the feedback:\n(Optional, Press enter to skip)"
			);
			if(comments.trim().isBlank()) {
				comments = null;
			}
			feedbackDao.submitRating(eventId,  userId, rating, comments);
			return;
		} catch (DataAccessException e) {
			System.out.println(e.getMessage());
		}
	}

    // marks events as completed
    @Override
	public void completeEvents() {
		try {
			eventDao.completeEvents();
		} catch (DataAccessException e) {
			System.out.println(e.getMessage());
		}
	}

    // prints all available events
    @Override
	public void printAllAvailableEvents() {
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
    public List<Event> getAllEvents(){
    	List<Event> events = null;
		try {
			events = eventDao.listAllEvents();
		} catch (DataAccessException e) {
			System.out.println(e.getMessage());
		}
		if(events.isEmpty()) {
			System.out.println("There are no events!");
			return null;
		}
		return events;
    }
    // prints all events
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

    // prints detailed view for events list
    @Override
	public void printEventDetails(List<Event> events) {
		events.forEach(event -> {
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

    // prints summary view for events list
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

    // prints single event details
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

    // prints single event summary
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
	public void updateEventSchedule() {
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
}
