package com.ems.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.ems.model.Event;
import com.ems.model.Ticket;

public interface OrganizerService {

    int createEvent(Event event);

    boolean updateEventDetails(int eventId, String title, String description, int categoryId, int venueId);

    boolean updateEventSchedule(int eventId, LocalDateTime start, LocalDateTime end);

    boolean updateEventCapacity(int eventId, int capacity);

    boolean publishEvent(int eventId);

    boolean cancelEvent(int eventId);

    boolean createTicket(Ticket ticket);

    boolean updateTicketPrice(int ticketId, double price);

    boolean updateTicketQuantity(int ticketId, int quantity);

    List<Ticket> viewTicketAvailability(int eventId);

    int viewEventRegistrations(int eventId);

    List<Map<String, Object>> viewRegisteredUsers(int eventId);

    List<Map<String, Object>> getEventWiseRegistrations(int organizerId);

    List<Map<String, Object>> getTicketSales(int organizerId);

    double getRevenueSummary(int organizerId);

    void sendEventUpdate(int eventId, String message);

    void sendScheduleChange(int eventId, String message);
    
    List<Event> getOrganizerEvents(int organizerId);
}
