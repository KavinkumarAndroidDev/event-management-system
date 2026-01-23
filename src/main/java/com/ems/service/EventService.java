package com.ems.service;

import java.util.List;

import com.ems.model.Event;

public interface EventService {

    void viewEventDetails();

    void viewTicketOptions();

    void filterByPrice();

    void searchByCity();

    void searchByDate();

    void searchByDateRange();

    void searchBycategory();

    void registerForEvent(int userId);

    void viewUpcomingEvents(int userId);

    void viewPastEvents(int userId);

    void viewBookingDetails(int userId);

    void submitRating(int userId);

    // Organizer functions
    void createTicket();

    void updateTicketPrice();

    void updateTicketQuantity();

    void viewTicketAvailability();

    void createEvent();

    void updateEventDetails();

    void updateEventSchedule();

    void updateEventCapacity();

    void publishEvent();

    void cancelEvent();

    // Admin functions
    void completeEvents();

    // Display functions
    void printAllAvailableEvents();

    void printAllEvents();

    void printEventDetails(List<Event> events);

    void printEventSummaries(List<Event> events);

	List<Event> getAllEvents();
}
