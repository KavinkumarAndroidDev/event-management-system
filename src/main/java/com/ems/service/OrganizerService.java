package com.ems.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.ems.model.Event;
import com.ems.model.Ticket;

public interface OrganizerService {

    // event creation & management
    int createEvent(Event event);

    boolean updateEventDetails(int eventId, String title, String description, int categoryId, int venueId);

    boolean updateEventSchedule(int eventId, LocalDateTime start, LocalDateTime end);

    boolean updateEventCapacity(int eventId, int capacity);

    boolean publishEvent(int eventId);

    boolean cancelEvent(int eventId);

    
    // ticket management
    boolean createTicket(Ticket ticket);

    boolean updateTicketPrice(int ticketId, double price);

    boolean updateTicketQuantity(int ticketId, int quantity);

    List<Ticket> viewTicketAvailability(int eventId);

    
    // registrations & reports
    int viewEventRegistrations(int eventId);

    List<Map<String, Object>> viewRegisteredUsers(int eventId);

    List<Map<String, Object>> getEventWiseRegistrations(int organizerId);

    List<Map<String, Object>> getTicketSales(int organizerId);

    double getRevenueSummary(int organizerId);

    
    // notifications
    void sendEventUpdate(int eventId, String message);

    void sendScheduleChange(int eventId, String message);

    
    // organizer data
    List<Event> getOrganizerEvents(int organizerId);
}
