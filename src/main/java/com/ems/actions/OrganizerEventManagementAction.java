package com.ems.actions;

import java.time.LocalDateTime;
import java.util.List;

import com.ems.model.Category;
import com.ems.model.Event;
import com.ems.model.Venue;
import com.ems.service.EventService;
import com.ems.service.OrganizerService;
import com.ems.util.ApplicationUtil;

/**
 * Action class for organizer event management operations.
 * Delegates business logic to appropriate services.
 */
public class OrganizerEventManagementAction {

    private final OrganizerService organizerService;
    private final EventService eventService;

    public OrganizerEventManagementAction() {
        this.organizerService = ApplicationUtil.organizerService();
        this.eventService = ApplicationUtil.eventService();
    }

    /**
     * Retrieves all events created by a specific organizer.
     * 
     * @param organizerId the ID of the organizer
     * @return list of events created by the organizer
     */
    public List<Event> getOrganizerEvents(int organizerId) {
        return organizerService.getOrganizerEvents(organizerId);
    }

    /**
     * Retrieves a specific event by ID for an organizer.
     * 
     * @param organizerId the ID of the organizer
     * @param eventId the ID of the event
     * @return the event if found and owned by organizer, null otherwise
     */
    public Event getOrganizerEventById(int organizerId, int eventId) {
        return organizerService.getOrganizerEventById(organizerId, eventId);
    }

    /**
     * Retrieves all available categories.
     * 
     * @return list of all categories
     */
    public List<Category> getAllCategory() {
        return eventService.getAllCategory();
    }

    /**
     * Retrieves all available venues.
     * 
     * @return list of all venues
     */
    public List<Venue> getAllVenues() {
        return eventService.getAllVenues();
    }

    /**
     * Gets the formatted address of a venue.
     * 
     * @param venueId the ID of the venue
     * @return formatted venue address
     */
    public String getVenueAddress(int venueId) {
        return eventService.getVenueAddress(venueId);
    }

    /**
     * Checks if a venue is available for the specified time period.
     * 
     * @param venueId the ID of the venue
     * @param startTime the start date and time
     * @param endTime the end date and time
     * @return true if venue is available, false otherwise
     */
    public boolean isVenueAvailable(int venueId, LocalDateTime startTime, LocalDateTime endTime) {
        return eventService.isVenueAvailable(venueId, startTime, endTime);
    }

    /**
     * Retrieves a venue by its ID.
     * 
     * @param venueId the ID of the venue
     * @return the venue object
     */
    public Venue getVenueById(int venueId) {
        return eventService.getVenueById(venueId);
    }

    /**
     * Creates a new event.
     * 
     * @param event the event object to create
     * @return the ID of the created event
     */
    public int createEvent(Event event) {
        return organizerService.createEvent(event);
    }

    /**
     * Updates event details (title, description, category).
     * 
     * @param eventId the ID of the event
     * @param title the new title
     * @param description the new description
     * @param categoryId the new category ID
     * @param venueId the venue ID (kept for consistency)
     * @return true if update was successful, false otherwise
     */
    public boolean updateEventDetails(int eventId, String title, String description, 
                                     int categoryId, int venueId) {
        return organizerService.updateEventDetails(eventId, title, description, categoryId, venueId);
    }

    /**
     * Updates the capacity of an event.
     * 
     * @param eventId the ID of the event
     * @param capacity the new capacity
     * @return true if update was successful, false otherwise
     */
    public boolean updateEventCapacity(int eventId, int capacity) {
        return organizerService.updateEventCapacity(eventId, capacity);
    }

    /**
     * Publishes an event, making it available to attendees.
     * 
     * @param eventId the ID of the event to publish
     * @return true if publishing was successful, false otherwise
     */
    public boolean publishEvent(int eventId) {
        return organizerService.publishEvent(eventId);
    }

    /**
     * Cancels an event.
     * 
     * @param eventId the ID of the event to cancel
     * @return true if cancellation was successful, false otherwise
     */
    public boolean cancelEvent(int eventId) {
        return organizerService.cancelEvent(eventId);
    }
}
