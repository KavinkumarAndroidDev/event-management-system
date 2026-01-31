package com.ems.util;

import java.time.LocalDateTime;
import java.util.List;

import com.ems.model.Event;
import com.ems.model.Offer;
import com.ems.model.OrganizerEventSummary;
import com.ems.model.Ticket;
import com.ems.service.EventService;

public class AdminMenuHelper {

    private static EventService eventService = ApplicationUtil.eventService();

    public static void printAllEventsWithStatus(List<Event> events) {
        System.out.println("\nAvailable Events");
        System.out.println("----------------------------------------------");

        int displayIndex = 1;
        for (Event event : events) {
            String category = eventService.getCategory(event.getCategoryId()).getName();
            int totalAvailable = eventService.getAvailableTickets(event.getEventId());

            System.out.println(
                displayIndex + " | Title: " +
                event.getTitle() + " | Category: " +
                category + " | " +
                DateTimeUtil.formatDateTime(event.getStartDateTime()) +
                " | Tickets: " + totalAvailable + " | Status: " + event.getStatus()
            );

            displayIndex++;
        }

        System.out.println("----------------------------------------------");
    }

    public static void printTicketDetails(List<Ticket> tickets) {
        System.out.println("\nAvailable ticket types:");

        int index = 1;
        for (Ticket ticket : tickets) {
            System.out.println(index + ". " + ticket.getTicketType() + " | ₹" + ticket.getPrice() + " | "
                    + "Tickets: " + ticket.getAvailableQuantity() + "/" + ticket.getTotalQuantity());
            index++;
        }
    }

    public static void printOrganizerEventSummary(List<OrganizerEventSummary> list) {
        System.out.println("\nOrganizer Events Summary");

        String currentStatus = "";

        for (OrganizerEventSummary s : list) {
            if (!s.getStatus().equals(currentStatus)) {
                currentStatus = s.getStatus();
                System.out.println("\n[" + currentStatus + "]");
            }

            System.out.println(
                s.getTitle()
                + " | Tickets Booked: " + s.getBookedTickets()
                + " out of " + s.getTotalTickets()
            );
        }
    }

    public static void printTicketCapacitySummary(List<Ticket> tickets) {
        int total = 0;
        int available = 0;

        for (Ticket t : tickets) {
            total += t.getTotalQuantity();
            available += t.getAvailableQuantity();
        }

        System.out.println("Event Capacity Summary\n" + "Total Tickets: " + total + "\n" + "Available Tickets: "
                + available);
    }

    public static List<Offer> filterActiveOffers(List<Offer> offers) {
        LocalDateTime now = LocalDateTime.now();
        return offers.stream()
                .filter(o -> o.getValidTo() != null && o.getValidTo().isAfter(now))
                .toList();
    }

    public static List<Offer> filterExpiredOffers(List<Offer> offers) {
        LocalDateTime now = LocalDateTime.now();
        return offers.stream()
                .filter(o -> o.getEventId() != 0 && o.getValidTo() != null && o.getValidTo().isBefore(now))
                .toList();
    }
}