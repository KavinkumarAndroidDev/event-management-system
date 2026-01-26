package com.ems.service.impl;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.ems.dao.*;
import com.ems.enums.PaymentMethod;
import com.ems.exception.DataAccessException;
import com.ems.model.BookingDetail;
import com.ems.model.Event;
import com.ems.model.Ticket;
import com.ems.model.Category;
import com.ems.model.UserEventRegistration;
import com.ems.model.Venue;
import com.ems.service.EventService;
import com.ems.service.PaymentService;
import com.ems.util.DateTimeUtil;

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
	
    @Override 
    public List<Ticket> getTicketTypes(int eventId){
    	List<Ticket> tickets = new ArrayList<>();
		try {
			tickets = ticketDao.getTicketTypes(eventId);
		} catch (DataAccessException e) {
			System.out.println(e.getMessage());
		}
		return tickets;
    }


    // filters events based on ticket price range
    @Override
	public List<Event> filterByPrice(double minPrice, double maxPrice) {

    	List<Event> filteredEvents = new ArrayList<>();
	    try {
	        List<Event> allEvents = eventDao.listAvailableEvents();

	        filteredEvents = allEvents.stream()
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
            return filteredEvents;

	    } catch (DataAccessException e) {
	        System.err.println("Database error: " + e.getMessage());
	    }
	    return filteredEvents;
	}
    
    @Override
    public Event getEventById(int eventId) {
    	Event event = new Event();
    	try {
    		event = eventDao.getEventById(eventId);
    	} catch (DataAccessException e) {
	        System.err.println("Database error: " + e.getMessage());
	    }
    	return event;
    }

    // searches events based on city
    @Override
	public List<Event> searchByCity(int venueId) {
    	List<Event> filteredEvents = new ArrayList<>();
		try {
			List<Event> allEvents = eventDao.listAvailableEvents();
			filteredEvents = allEvents.stream()
					.filter(e-> e.getVenueId() == venueId)
					.collect(Collectors.toList());
			return filteredEvents;
		} catch (DataAccessException e) {
			System.err.println("Database error: " + e.getMessage());
		}
		return filteredEvents;
	}

    // searches events by date
    @Override
	public List<Event> searchByDate(LocalDate localDate) {
    	List<Event> filteredEvents = new ArrayList<>();
		try {
			List<Event> allEvents = eventDao.listAvailableEvents();
			filteredEvents = allEvents.stream()
					.filter(e -> e.getStartDateTime().toLocalDate().isBefore(localDate))
					.collect(Collectors.toList());
		} catch (DataAccessException e) {
			System.err.println("Database error: " + e.getMessage());
		}
		return filteredEvents;
	}

    // searches events within date range
    @Override
	public List<Event> searchByDateRange(LocalDate startDate, LocalDate endDate) {
    	List<Event> filteredEvents =  new ArrayList<>();
	    if (startDate.isAfter(endDate)) {
	        System.out.println("Error: Start date cannot be after end date.");
	        return filteredEvents;
	    }

	    try {
	        List<Event> allEvents = eventDao.listAvailableEvents();
	        
	        filteredEvents = allEvents.stream()
	            .filter(e -> {
	                LocalDate eventDate = e.getStartDateTime().toLocalDate();
	                return !eventDate.isBefore(startDate) && !eventDate.isAfter(endDate);
	            })
	            .collect(Collectors.toList());

	        return filteredEvents;
	    } catch (DataAccessException e) {
	        System.err.println("Database error: " + e.getMessage());
	    }
	    return filteredEvents;
	}

    // searches events based on category
    @Override
	public List<Event> searchBycategory(int selectedCategoryId) {
    	List<Event> filteredEvents =  new ArrayList<>();

		try {
			List<Event> allEvents = eventDao.listAvailableEvents();
			filteredEvents = allEvents.stream()
					.filter(e-> e.getCategoryId() == selectedCategoryId)
					.collect(Collectors.toList());
			
		} catch (DataAccessException e) {
			System.err.println("Database error: " + e.getMessage());
		}
		return filteredEvents;

	}

    // registers user for selected event
    @Override
	public boolean registerForEvent(int userId, int eventId, int ticketId, int quantity, double price, PaymentMethod paymentMethod) {
    	boolean success = false;
	    try {
	        success = paymentService.processRegistration(
				    userId, eventId, ticketId, quantity, price, paymentMethod
				);
	    } catch (Exception e) {
	        System.out.println("Error during registration: " + e.getMessage());
	    }
	    return success;
	}

    // shows upcoming events for user
    @Override
    public List<UserEventRegistration> viewUpcomingEvents(int userId) {
    	List<UserEventRegistration> upcoming = new ArrayList<>();
        try {
            List<UserEventRegistration> registrations =
                    eventDao.getUserRegistrations(userId);

            upcoming = registrations.stream()
                    .filter(r -> r.getStartDateTime().isAfter(LocalDateTime.now()))
                    .toList();

        } catch (DataAccessException e) {
            System.out.println(e.getMessage());
        }
        return upcoming;
    }


    // shows past events for user
    @Override
    public List<UserEventRegistration> viewPastEvents(int userId) {
    	List<UserEventRegistration> past = new ArrayList<>();
        try {
        	List<UserEventRegistration> registrations =
                    eventDao.getUserRegistrations(userId);
        	past = registrations.stream()
                    .filter(r -> r.getStartDateTime().isBefore(LocalDateTime.now()))
                    .toList();

            
        } catch (DataAccessException e) {
            System.out.println(e.getMessage());
        }
        return past;
    }


    // shows booking details for user
    @Override
	public List<BookingDetail> viewBookingDetails(int userId) {
    	List<BookingDetail> bookings  = new ArrayList<>();
    	try {
    		bookings = eventDao.viewBookingDetails(userId);
            return bookings;

        } catch (DataAccessException e) {
            System.out.println(e.getMessage());
        }
		return bookings;
	}

    // submits rating for past events
    @Override
	public void submitRating(int userId,int eventId, int rating, String comments) {
		try {
			if(comments.trim().isBlank()) {
				comments = null;
			}
			feedbackDao.submitRating(eventId,  userId, rating, comments);
		} catch (DataAccessException e) {
			System.out.println(e.getMessage());
		}
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

    
    @Override
    public Category getCategory(int categoryId) {
    	Category category = null;
    	try {
    		category = categoryDao.getCategory(categoryId);
		} catch (DataAccessException e) {
			System.out.println(e.getMessage());
		}
		return category;
    }

	@Override
	public int getAvailableTickets(int eventId) {
		try {
			return ticketDao.getAvailableTickets(eventId);
		} catch (DataAccessException e) {
			System.out.println(e.getMessage());
		}
		return 0;
		
	}

	@Override
	public String getVenueName(int venueId) {
		 try {
			return venueDao.getVenueName(venueId);
		 } catch (DataAccessException e) {
			 System.out.println(e.getMessage());
		 }
		 return "";
	}

	@Override
	public String getVenueAddress(int venueId) {
		try {
			return venueDao.getVenueAddress(venueId);
		} catch (DataAccessException e) {
			System.out.println(e.getMessage());
		}
		return "";
	}

	@Override
	public List<Category> getAllCategory() {
		List<Category> categories = new ArrayList<>();
		try {
			categories = categoryDao.getAllCategories();
			return categories;
		} catch (DataAccessException e) {
			System.out.println(e.getMessage());
		}
		
		return categories;
	}

	@Override
	public Map<Integer, String> getAllCities() {
		Map<Integer, String> cities = new HashMap<>();
		try {
			cities = venueDao.getAllCities();
		} catch (DataAccessException e) {
			System.out.println(e.getMessage());
		}
		return cities;
	}

	@Override
	public List<Event> listAvailableEvents() {
		List<Event> events = new ArrayList<>();
		try {
			events = eventDao.listAvailableEvents();
		} catch (DataAccessException e) {
			System.out.println(e.getMessage());
		}
		return events;
	}

	@Override
	public List<Venue> getAllVenues() {
		List<Venue> venues = new ArrayList<>();
		try {
			venues = venueDao.getAllVenues();
		} catch (DataAccessException e) {
			System.out.println(e.getMessage());
		}
		return venues;
	}

	@Override
	public boolean isVenueAvailable(int venueId, LocalDateTime startTime, LocalDateTime endTime) {
		try {
			boolean isAvailable = venueDao.isVenueAvailable(venueId,
					Timestamp.from(DateTimeUtil.convertLocalDefaultToUtc(startTime)),
					Timestamp.from(DateTimeUtil.convertLocalDefaultToUtc(endTime))
					);
			return isAvailable;
		} catch (DataAccessException e) {
			System.out.println(e.getMessage());
		}
		return false;
	}

	@Override
	public Venue getVenueById(int venueId) {
		Venue venue = new Venue();
		try {
			venue = venueDao.getVenueById(venueId);
			return venue;
		} catch (DataAccessException e) {
			System.out.println(e.getMessage());
		}
		return null;
	}

	@Override
	public List<Event> listEventsYetToApprove() {
		List<Event> events = null;
		try {
			events = eventDao.listEventsYetToApprove();
		} catch (DataAccessException e) {
			System.out.println(e.getMessage());
		}
		if(events.isEmpty()) {
			System.out.println("There are no events!");
			return null;
		}
		return events;
	}

	@Override
	public List<Event> listAvailableAndDraftEvents() {
		List<Event> events = null;
		try {
			events = eventDao.listAvailableAndDraftEvents();
		} catch (DataAccessException e) {
			System.out.println(e.getMessage());
		}
		if(events.isEmpty()) {
			System.out.println("There are no events!");
			return null;
		}
		return events;
	}


	
}
