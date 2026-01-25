package com.ems.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.ems.dao.EventDao;
import com.ems.dao.RegistrationDao;
import com.ems.dao.TicketDao;
import com.ems.dao.impl.EventDaoImpl;
import com.ems.dao.impl.RegistrationDaoImpl;
import com.ems.dao.impl.TicketDaoImpl;
import com.ems.exception.DataAccessException;
import com.ems.model.Event;
import com.ems.model.Ticket;
import com.ems.service.NotificationService;
import com.ems.service.OrganizerService;
import com.ems.util.ApplicationUtil;

public class OrganizerServiceImpl implements OrganizerService {

    private final EventDao eventDao = new EventDaoImpl();
    private final TicketDao ticketDao = new TicketDaoImpl();
    private final RegistrationDao registrationDao = new RegistrationDaoImpl();
    private final NotificationService notificationService = ApplicationUtil.notificationService();

    public int createEvent(Event event) {
        event.setStatus("DRAFT");
        try {
			return eventDao.createEvent(event);
		} catch (DataAccessException e) {
			System.out.println(e.getMessage());
		}
        return 0;
    }

    public boolean updateEventDetails(int eventId, String title, String description, int categoryId, int venueId) {
        try {
			return eventDao.updateEventDetails(eventId, title, description, categoryId, venueId);
		} catch (DataAccessException e) {
			System.out.println(e.getMessage());
		}
        return false;
    }

    public boolean updateEventSchedule(int eventId, LocalDateTime start, LocalDateTime end) {
        try {
			return eventDao.updateEventSchedule(eventId, start, end);
		} catch (DataAccessException e) {
			System.out.println(e.getMessage());
		}
        return false;
    }

    public boolean updateEventCapacity(int eventId, int capacity) {
        try {
			return eventDao.updateEventCapacity(eventId, capacity);
		} catch (DataAccessException e) {
			System.out.println(e.getMessage());
		}
        return false;
    }

    public boolean publishEvent(int eventId) {
        try {
			return eventDao.updateEventStatus(eventId, "PUBLISHED");
		} catch (DataAccessException e) {
			System.out.println(e.getMessage());
		}
        return false;
    }

    public boolean cancelEvent(int eventId) {
        try {
			return eventDao.updateEventStatus(eventId, "CANCELLED");
		} catch (DataAccessException e) {
			System.out.println(e.getMessage());
		}
        return false;
    }

    public boolean createTicket(Ticket ticket) {
        ticket.setAvailableQuantity(ticket.getTotalQuantity());
        return ticketDao.createTicket(ticket);
    }

    public boolean updateTicketPrice(int ticketId, double price) {
        return ticketDao.updateTicketPrice(ticketId, price);
    }

    public boolean updateTicketQuantity(int ticketId, int quantity) {
        return ticketDao.updateTicketQuantity(ticketId, quantity);
    }

    public List<Ticket> viewTicketAvailability(int eventId) {
        return ticketDao.getTicketsByEvent(eventId);
    }

    public int viewEventRegistrations(int eventId) {
        return registrationDao.getEventRegistrationCount(eventId);
    }

    public List<Map<String, Object>> viewRegisteredUsers(int eventId) {
        return registrationDao.getRegisteredUsers(eventId);
    }

    public List<Map<String, Object>> getEventWiseRegistrations(int organizerId) {
        return registrationDao.getOrganizerWiseRegistrations(organizerId);
    }
    
    public List<Event> getOrganizerEvents(int organizerId){
    	try {
			return eventDao.getEventsByOrganizer(organizerId);
		} catch (DataAccessException e) {
			System.out.println(e.getMessage());
		}
    	return new ArrayList<>();
    }

    public List<Map<String, Object>> getTicketSales(int organizerId) {
        return registrationDao.getTicketSales(organizerId);
    }

    public double getRevenueSummary(int organizerId) {
        return registrationDao.getRevenueSummary(organizerId);
    }

    public void sendEventUpdate(int eventId, String message) {
        notificationService.sendEventNotification(eventId, message, "EVENT_UPDATE");
    }

    public void sendScheduleChange(int eventId, String message) {
        notificationService.sendEventNotification(eventId, message, "SCHEDULE_CHANGE");
    }
}




//package com.ems.service;
//
//public interface OrganizerService {
//
//    void sendEventUpdate();
//
//    void sendScheduleChange();
//
//    void getRevenueSummary();
//
//    void getTicketSales();
//
//    void getEventWiseRegistrations();
//
//    void viewEventRegistrations();
//
//    void viewRegisteredUsers();
//    
//
//    // Organizer functions
//    void createTicket();
//
//    void updateTicketPrice();
//
//    void updateTicketQuantity();
//
//    void viewTicketAvailability();
//
//    void createEvent();
//
//    void updateEventDetails();
//
//    void updateEventSchedule();
//
//    void updateEventCapacity();
//
//    void publishEvent();
//
//    void cancelEvent();
//}
