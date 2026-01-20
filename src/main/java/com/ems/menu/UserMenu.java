package com.ems.menu;

import java.util.List;
import java.util.Scanner;

import com.ems.dao.*;
import com.ems.dao.impl.*;
import com.ems.model.Event;
import com.ems.model.Ticket;
import com.ems.model.User;
import com.ems.service.UserService;
import com.ems.util.DateTimeUtil;
import com.ems.util.InputValidationUtil;

public class UserMenu {
	Scanner scanner;
	User loggedInUser;
	
	public UserMenu(Scanner scanner, User user) {
		this.scanner = scanner;
		this.loggedInUser = user;
		this.start();
	}

	private void start() {
		UserDao userDao = new UserDaoImpl();
		EventDao eventDao = new EventDaoImpl();
		VenueDao venueDao = new VenueDaoImpl();
		CategoryDao categoryDao = new CategoryDaoImpl();
		TicketDao ticketDao = new TicketDaoImpl();
		while(true) {
			System.out.println("\nUser Menu"
					+ "\n\nEnter your choice:\n"
			        + "1. Browse available events\r\n"
			        + "2. Search and filter events\r\n"
			        + "3. View my registrations\r\n"
			        + "4. View my notifications\r\n"
			        + "5. Register for an event\r\n"
			        + "6. Logout"
			        + "\n>");
			int input = InputValidationUtil.readInt(scanner, "");
			switch(input) {
				case 1:
					printAllAvailableEvents(eventDao, venueDao, categoryDao, ticketDao);
					break;
				case 2:
					while(true) {
						System.out.println("\nEnter your choice:\n"
								+ "1. Search by category\r\n"
								+ "2. Search by date\r\n"
								+ "3. Search by city\r\n"
								+ "4. Filter by price\r\n"
								+ "5. Filter by availability\r\n"
								+ "6. Exit to user menu"
						        + "\n>");
						int filterChoice = InputValidationUtil.readInt(scanner, "");
						switch(filterChoice) {
							case 1:
								searchBycategory(categoryDao);
								break;
							case 2:
								break;
							case 3:
								break;
								
							case 4:
								break;
							case 5:
								printAllAvailableEvents(eventDao, venueDao, categoryDao, ticketDao);
								break;
							case 6:
								return;
							default:
								return;
						}
					}
				case 3:
					break;
				case 4:
					break;
				case 5:
					break;
				case 6:
					System.out.println("Going back to main menu");
					return;
				default:
					return;
			}
			
		}
	}

	private void searchBycategory(CategoryDao categoryDao) {
		categoryDao.listAllCategory();
		
	}

	private void printAllAvailableEvents(EventDao eventDao, VenueDao venueDao, CategoryDao categoryDao, TicketDao ticketDao) {
		List<Event> events = eventDao.listAvailableEvents();
		for (Event event : events) {

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
	    }
	}

	
}

//
//Payment payment = new Payment();
//payment.setRegistrationId(regId);
//payment.setAmount(amount);
//payment.setPaymentMethod(method);
//
//paymentService.processPayment(payment);
//System.out.println("Payment successful");
