package com.ems.actions;

import java.util.List;

import com.ems.model.Ticket;
import com.ems.service.OrganizerService;
import com.ems.util.ApplicationUtil;

/**
 * Action class for organizer ticket management operations.
 * Delegates business logic to appropriate services.
 */
public class OrganizerTicketManagementAction {

    private final OrganizerService organizerService;

    public OrganizerTicketManagementAction() {
        this.organizerService = ApplicationUtil.organizerService();
    }

    /**
     * Creates a new ticket for an event.
     * 
     * @param ticket the ticket object to create
     */
    public void createTicket(Ticket ticket) {
        organizerService.createTicket(ticket);
    }

    /**
     * Views ticket availability for a specific event.
     * 
     * @param eventId the ID of the event
     * @return list of tickets with availability information
     */
    public List<Ticket> viewTicketAvailability(int eventId) {
        return organizerService.viewTicketAvailability(eventId);
    }

    /**
     * Updates the price of a ticket.
     * 
     * @param ticketId the ID of the ticket
     * @param newPrice the new price
     * @return true if update was successful, false otherwise
     */
    public boolean updateTicketPrice(int ticketId, double newPrice) {
        return organizerService.updateTicketPrice(ticketId, newPrice);
    }

    /**
     * Updates the quantity of a ticket.
     * 
     * @param ticketId the ID of the ticket
     * @param newQuantity the new quantity
     * @return true if update was successful, false otherwise
     */
    public boolean updateTicketQuantity(int ticketId, int newQuantity) {
        return organizerService.updateTicketQuantity(ticketId, newQuantity);
    }
}
