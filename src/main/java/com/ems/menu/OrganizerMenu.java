package com.ems.menu;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.ems.enums.EventStatus;
import com.ems.model.Category;
import com.ems.model.Event;
import com.ems.model.Ticket;
import com.ems.model.User;
import com.ems.model.Venue;
import com.ems.service.EventService;
import com.ems.service.NotificationService;
import com.ems.service.OrganizerService;
import com.ems.service.impl.OrganizerServiceImpl;
import com.ems.util.ApplicationUtil;
import com.ems.util.DateTimeUtil;
import com.ems.util.InputValidationUtil;
import com.ems.util.MenuHelper;
import com.ems.util.ScannerUtil;

public class OrganizerMenu extends BaseMenu {

	private final OrganizerService organizerService;
	private final NotificationService notificationService;
	private final EventService eventService;

	public OrganizerMenu(User user) {
		super(user);
		this.organizerService = new OrganizerServiceImpl();
		this.notificationService = ApplicationUtil.notificationService();
		this.eventService = ApplicationUtil.eventService();
	}

	public void start() {

		notificationService.displayUnreadNotifications(loggedInUser.getUserId());

		while (true) {
			System.out.println("\nOrganizer Menu\n" + "1. Event Management\n" + "2. Ticket Management\n"
					+ "3. Registrations\n" + "4. Reports\n" + "5. Notifications\n" + "6. Logout\n\n>");

			int choice = InputValidationUtil.readInt(ScannerUtil.getScanner(), "");

			switch (choice) {
			case 1:
				eventManagementMenu();
				break;
			case 2:
				ticketManagementMenu();
				break;
			case 3:
				registrationMenu();
				break;
			case 4:
				reportMenu();
				break;
			case 5:
				notificationMenu();
				break;
			case 6:
				if (confirmLogout()) {
					return;
				}
				break;
			default:
				System.out.println("Invalid option");

			}
		}
	}

	private void eventManagementMenu() {

		while (true) {
			System.out.println("\nEvent Management\n" + "1. Create new event\n" + "2. Update event details\n"
					+ "3. Update event capacity\n" + "4. Publish event\n"
					+ "5. Cancel event\n" + "6. Back\n\n>");

			int choice = InputValidationUtil.readInt(ScannerUtil.getScanner(), "");

			switch (choice) {
			case 1:
				createEvent();
				break;
			case 2:
				updateEventDetails();
				break;
			case 3:
				updateEventCapacity();
				break;
			case 4:
				publishEvent();
				break;
			case 5:
				cancelEvent();
				break;
			case 6: {
				return;
			}
			default:
				System.out.println("Invalid option");
			}
		}
	}

	// Logic implemented
	private void createEvent() {
		String title = InputValidationUtil.readNonEmptyString(ScannerUtil.getScanner(), "Enter the event title: ");
		String description = InputValidationUtil.readString(ScannerUtil.getScanner(), "Enter the event description: ");

		List<Category> categories = eventService.getAllCategory();
		if (categories.isEmpty()) {
			System.out.println("There are no available category!");
			return;
		}
		int defaultIndex = 1;
		for (Category category : categories) {
			System.out.println(defaultIndex + ". " + category.getName());
			defaultIndex++;
		}
		int choice = InputValidationUtil.readInt(ScannerUtil.getScanner(), "Select category number: ");
		while (choice < 1 || choice > categories.size()) {
			choice = InputValidationUtil.readInt(ScannerUtil.getScanner(), "Enter a valid choice: ");
		}
		Category selectedCategory = categories.get(choice - 1);
		int categoryId = selectedCategory.getCategoryId();

		List<Venue> venues = eventService.getAllVenues();
		if (venues.isEmpty()) {
			System.out.println("There are no available venues!");
			return;
		}
		defaultIndex = 1;
		for (Venue venue : venues) {
			System.out
					.println(defaultIndex + ". " + venue.getName() +", \n"+ eventService.getVenueAddress(venue.getVenueId()) +"\n");
			defaultIndex++;
		}
		int venueChoice = InputValidationUtil.readInt(ScannerUtil.getScanner(), "Select venue number: ");
		while (venueChoice < 1 || venueChoice > categories.size()) {
			venueChoice = InputValidationUtil.readInt(ScannerUtil.getScanner(), "Enter a valid choice: ");
		}
		Venue selectedVenue = venues.get(venueChoice - 1);
		int venueId = selectedVenue.getVenueId();
		LocalDateTime startTime = DateTimeUtil.getLocalDateTime("Enter the event Start Date Time (dd-MM-yyyy HH:mm): ");
		LocalDateTime endTime = DateTimeUtil.getLocalDateTime("Enter the event End Date Time (dd-MM-yyyy HH:mm): ");

		while (!eventService.isVenueAvailable(venueId, startTime, endTime)) {
			System.out.println("\nEnter the valid option:" + "\n1. Change venue" + "\n2. Change event time"
					+ "\n3. Abort" + "\n>");
			int c = InputValidationUtil.readInt(ScannerUtil.getScanner(), "");
			switch (c) {
			case 1:
				venues = eventService.getAllVenues();
				if (venues.isEmpty()) {
					System.out.println("There are no available venues!");
					return;
				}
				defaultIndex = 1;
				for (Venue venue : venues) {
					System.out.println(
							defaultIndex + ". " + venue.getName() + eventService.getVenueAddress(venue.getVenueId()));
					defaultIndex++;
				}
				venueChoice = InputValidationUtil.readInt(ScannerUtil.getScanner(), "Select venue number: ");
				while (venueChoice < 1 || venueChoice > categories.size()) {
					venueChoice = InputValidationUtil.readInt(ScannerUtil.getScanner(), "Enter a valid choice: ");
				}
				selectedVenue = venues.get(venueChoice - 1);
				venueId = selectedVenue.getVenueId();
				break;
			case 2:
				startTime = DateTimeUtil.getLocalDateTime("Enter the event Start Date Time (dd-MM-yyyy HH:mm): ");
				endTime = DateTimeUtil.getLocalDateTime("Enter the event End Date Time (dd-MM-yyyy HH:mm): ");
				break;
			case 3:
				return;
			default:
				System.out.println("Enter the valid option!");
			}
		}

		int eventCapacity = InputValidationUtil.readInt(ScannerUtil.getScanner(),
				"Maximum capacity of the selected venue: " + selectedVenue.getMaxCapacity()
						+ "\nEnter the event capacity:");
		while (eventCapacity <= 0 || eventCapacity > selectedVenue.getMaxCapacity()) {
			eventCapacity = InputValidationUtil.readInt(ScannerUtil.getScanner(), "Enter the valid event capacity:");
		}
		Event event = new Event();
		event.setOrganizerId(loggedInUser.getUserId());
		event.setTitle(title);
		event.setDescription(description);
		event.setVenueId(venueId);
		event.setStartDateTime(startTime);
		event.setEndDateTime(endTime);
		event.setCapacity(eventCapacity);
		event.setCategoryId(categoryId);

		int id = organizerService.createEvent(event);
		System.out.println("Event created with ID: " + id);
	}

	// Logic implemented - venue change is not permitted
private void updateEventDetails() {

    List<Event> events = organizerService.getOrganizerEvents(loggedInUser.getUserId());
    if (events.isEmpty()) {
        System.out.println("The organizer hasn't hosted any events");
        return;
    }

    List<Event> sortedEvents = events.stream()
        .filter(e ->
            (EventStatus.PUBLISHED.toString().equals(e.getStatus())
             || EventStatus.DRAFT.toString().equals(e.getStatus()))
            && e.getStartDateTime().isAfter(LocalDateTime.now())
        )
        .sorted(Comparator.comparing(Event::getStartDateTime))
        .toList();

    if (sortedEvents.isEmpty()) {
        System.out.println("No upcoming editable events available");
        return;
    }

    MenuHelper.printEventSummaries(sortedEvents);

    int choice = InputValidationUtil.readInt(
        ScannerUtil.getScanner(),
        "Select an event (1-" + sortedEvents.size() + "): "
    );
    while (choice < 1 || choice > sortedEvents.size()) {
        choice = InputValidationUtil.readInt(
            ScannerUtil.getScanner(),
            "Enter a valid choice: "
        );
    }

    Event selectedEvent = sortedEvents.get(choice - 1);

    System.out.println("Press Enter to keep the current value");

    String title = InputValidationUtil.readString(
        ScannerUtil.getScanner(),
        "Title (" + selectedEvent.getTitle() + "): "
    );
    if (title.isBlank()) {
        title = selectedEvent.getTitle();
    }

    String description = InputValidationUtil.readString(
        ScannerUtil.getScanner(),
        "Description (" + selectedEvent.getDescription() + "): "
    );
    if (description.isBlank()) {
        description = selectedEvent.getDescription();
    }

    List<Category> categories = eventService.getAllCategory();
    if (categories.isEmpty()) {
        System.out.println("No categories available");
        return;
    }

    System.out.println("Categories (Enter 0 to keep current)");
    int index = 1;
    for (Category category : categories) {
        System.out.println(index + ". " + category.getName());
        index++;
    }

    int categoryChoice = InputValidationUtil.readInt(
        ScannerUtil.getScanner(),
        "Category (" + selectedEvent.getCategoryId() + "): "
    );

    int categoryId;
    if (categoryChoice == 0) {
        categoryId = selectedEvent.getCategoryId();
    } else {
        while (categoryChoice < 1 || categoryChoice > categories.size()) {
            categoryChoice = InputValidationUtil.readInt(
                ScannerUtil.getScanner(),
                "Enter a valid choice: "
            );
        }
        categoryId = categories.get(categoryChoice - 1).getCategoryId();
    }

    boolean result = organizerService.updateEventDetails(
        selectedEvent.getEventId(),
        title,
        description,
        categoryId,
        selectedEvent.getVenueId()
    );

    System.out.println(result ? "Updated successfully" : "Update failed");
}



	// Logic implemented
	private void updateEventCapacity() {
		List<Event> events = organizerService.getOrganizerEvents(loggedInUser.getUserId());
		if (events.isEmpty()) {
			System.out.println("The organizer has'nt hosted any events");
			return;
		}
		List<Event> sortedEvents = events.stream()
				.filter(e -> (EventStatus.PUBLISHED.toString().equals(e.getStatus())
						|| EventStatus.DRAFT.toString().equals(e.getStatus()) )
						&& e.getStartDateTime().isAfter(LocalDateTime.now()))
				.sorted(Comparator.comparing(e -> e.getStartDateTime())).collect(Collectors.toList());
		if(sortedEvents.isEmpty()) {
			System.out.println("The organizer has'nt have any upcoming events");
			return;
		}
		MenuHelper.printEventSummaries(sortedEvents);
		int choice = InputValidationUtil.readInt(ScannerUtil.getScanner(),
				"Select an event (1-" + sortedEvents.size() + "): ");
		while (choice < 1 || choice > sortedEvents.size()) {
			choice = InputValidationUtil.readInt(ScannerUtil.getScanner(), "Enter a valid choice: ");
		}
		Event selectedEvent = sortedEvents.get(choice - 1);
		int eventId = selectedEvent.getEventId();
		Venue venue = eventService.getVenueById(selectedEvent.getVenueId());
		System.out.println("The maximum capacity of venue is: " + venue.getMaxCapacity());
		int capacity = InputValidationUtil.readInt(ScannerUtil.getScanner(), "Enter the new capacity: ");
		while (capacity < 0 || capacity > venue.getMaxCapacity()) {
			capacity = InputValidationUtil.readInt(ScannerUtil.getScanner(), "Enter the valid capacity: ");
		}

		boolean result = organizerService.updateEventCapacity(eventId, capacity);
		System.out.println(result ? "Capacity updated" : "Update failed");
	}

	private void publishEvent() {

		List<Event> events = organizerService.getOrganizerEvents(loggedInUser.getUserId());

		if (events.isEmpty()) {
			System.out.println("You have no events");
			return;
		}

		List<Event> eligibleEvents = events.stream()
				.filter(e -> EventStatus.DRAFT.toString().equals(e.getStatus())
						&& e.getStartDateTime().isAfter(LocalDateTime.now()) && e.getUpdatedAt() != null)
				.sorted(Comparator.comparing(Event::getStartDateTime)).collect(Collectors.toList());

		if (eligibleEvents.isEmpty()) {
			System.out.println("No eligible events available for publishing");
			return;
		}

		MenuHelper.printEventSummaries(eligibleEvents);

		int choice = InputValidationUtil.readInt(ScannerUtil.getScanner(),
				"Select an event (1-" + eligibleEvents.size() + "): ");

		while (choice < 1 || choice > eligibleEvents.size()) {
			choice = InputValidationUtil.readInt(ScannerUtil.getScanner(), "Enter a valid choice: ");
		}

		Event selectedEvent = eligibleEvents.get(choice - 1);
		int eventId = selectedEvent.getEventId();
		int capacity = selectedEvent.getCapacity();

		int remainingCapacity = capacity;

		while (remainingCapacity > 0) {

			System.out.println("\nEvent Capacity: " + capacity + "\nRemaining Capacity: " + remainingCapacity);

			System.out.println("\n1. Add ticket type\n" + "2. Abort publish\n\n>");

			int option = InputValidationUtil.readInt(ScannerUtil.getScanner(), "");

			if (option == 2) {
				return;
			}

			if (option != 1) {
				System.out.println("Invalid option");
				continue;
			}

			Ticket ticket = new Ticket();
			ticket.setEventId(eventId);

			ticket.setTicketType(InputValidationUtil.readNonEmptyString(ScannerUtil.getScanner(), "Ticket Type: "));

			ticket.setPrice(InputValidationUtil.readDouble(ScannerUtil.getScanner(), "Ticket Price: "));

			int qty = InputValidationUtil.readInt(ScannerUtil.getScanner(),
					"Ticket Quantity (max " + remainingCapacity + "): ");

			while (qty <= 0 || qty > remainingCapacity) {
				qty = InputValidationUtil.readInt(ScannerUtil.getScanner(),
						"Enter valid quantity (1-" + remainingCapacity + "): ");
			}

			ticket.setTotalQuantity(qty);

			organizerService.createTicket(ticket);

			remainingCapacity -= qty;
		}

		boolean published = organizerService.publishEvent(eventId);

		System.out.println(published ? "Event published successfully" : "Publish failed");
	}

	private void cancelEvent() {

		List<Event> events = organizerService.getOrganizerEvents(loggedInUser.getUserId());

		if (events.isEmpty()) {
			System.out.println("You have no events");
			return;
		}

		List<Event> cancellableEvents = events.stream()
				.filter(e -> EventStatus.DRAFT.toString().equals(e.getStatus())
						|| EventStatus.PUBLISHED.toString().equals(e.getStatus()))
				.sorted(Comparator.comparing(Event::getStartDateTime)).collect(Collectors.toList());

		if (cancellableEvents.isEmpty()) {
			System.out.println("No events available for cancellation");
			return;
		}

		MenuHelper.printEventSummaries(cancellableEvents);

		int choice = InputValidationUtil.readInt(ScannerUtil.getScanner(),
				"Select an event to cancel (1-" + cancellableEvents.size() + "): ");

		while (choice < 1 || choice > cancellableEvents.size()) {
			choice = InputValidationUtil.readInt(ScannerUtil.getScanner(), "Enter a valid choice: ");
		}

		Event selectedEvent = cancellableEvents.get(choice - 1);

		char confirm = InputValidationUtil.readChar(ScannerUtil.getScanner(),
				"Are you sure you want to cancel this event (Y/N): ");

		if (Character.toUpperCase(confirm) != 'Y') {
			System.out.println("Cancellation aborted");
			return;
		}

		boolean result = organizerService.cancelEvent(selectedEvent.getEventId());

		if (!result) {
			System.out.println("Cancel failed");
			return;
		}

		System.out.println("Event cancelled successfully");

		if (EventStatus.PUBLISHED.toString().equals(selectedEvent.getStatus())) {
			System.out.println("Refunds will be processed soon");
		}
	}

	private void ticketManagementMenu() {

		while (true) {
			System.out.println("\nTicket Management\n" + "1. Update ticket price\n"
					+ "2. Update ticket quantity\n" + "3. View ticket availability\n" + "4. Back\n\n>");

			int choice = InputValidationUtil.readInt(ScannerUtil.getScanner(), "");

			switch (choice) {
			case 1:
				updateTicketPrice();
				break;
			case 2:
				updateTicketQuantity();
				break;
			case 3:
				viewTicketAvailability();
				break;
			case 4: {
				return;
			}
			default:
				System.out.println("Invalid option");
			}
		}
	}

	private void updateTicketPrice() {

		List<Event> events = organizerService.getOrganizerEvents(loggedInUser.getUserId());

		if (events.isEmpty()) {
			System.out.println("You have no events");
			return;
		}

		List<Event> validEvents = events.stream()
				.filter(e -> EventStatus.DRAFT.toString().equals(e.getStatus())
						|| EventStatus.PUBLISHED.toString().equals(e.getStatus()))
				.sorted(Comparator.comparing(Event::getStartDateTime)).collect(Collectors.toList());

		if (validEvents.isEmpty()) {
			System.out.println("No events available");
			return;
		}

		MenuHelper.printEventSummaries(validEvents);

		int eventChoice = InputValidationUtil.readInt(ScannerUtil.getScanner(),
				"Select an event (1-" + validEvents.size() + "): ");

		while (eventChoice < 1 || eventChoice > validEvents.size()) {
			eventChoice = InputValidationUtil.readInt(ScannerUtil.getScanner(), "Enter a valid choice: ");
		}

		Event selectedEvent = validEvents.get(eventChoice - 1);

		List<Ticket> tickets = organizerService.viewTicketAvailability(selectedEvent.getEventId());

		if (tickets.isEmpty()) {
			System.out.println("No tickets available for this event");
			return;
		}

		int index = 1;
		for (Ticket t : tickets) {
			System.out.println(index + ". " + t.getTicketType() + " | Price: " + t.getPrice() + " | Quantity: "
					+ t.getTotalQuantity());
			index++;
		}

		int ticketChoice = InputValidationUtil.readInt(ScannerUtil.getScanner(),
				"Select a ticket (1-" + tickets.size() + "): ");

		while (ticketChoice < 1 || ticketChoice > tickets.size()) {
			ticketChoice = InputValidationUtil.readInt(ScannerUtil.getScanner(), "Enter a valid choice: ");
		}

		Ticket selectedTicket = tickets.get(ticketChoice - 1);

		double newPrice = InputValidationUtil.readDouble(ScannerUtil.getScanner(), "Enter new price: ");

		while (newPrice <= 0) {
			newPrice = InputValidationUtil.readDouble(ScannerUtil.getScanner(), "Enter a valid price: ");
		}

		boolean result = organizerService.updateTicketPrice(selectedTicket.getTicketId(), newPrice);

		System.out.println(result ? "Ticket price updated successfully" : "Update failed");
	}

	private void updateTicketQuantity() {

		List<Event> events = organizerService.getOrganizerEvents(loggedInUser.getUserId());

		if (events.isEmpty()) {
			System.out.println("You have no events");
			return;
		}

		List<Event> validEvents = events.stream()
				.filter(e -> EventStatus.DRAFT.toString().equals(e.getStatus())
						|| EventStatus.PUBLISHED.toString().equals(e.getStatus()))
				.sorted(Comparator.comparing(Event::getStartDateTime)).collect(Collectors.toList());

		if (validEvents.isEmpty()) {
			System.out.println("No events available");
			return;
		}

		MenuHelper.printEventSummaries(validEvents);

		int eventChoice = InputValidationUtil.readInt(ScannerUtil.getScanner(),
				"Select an event (1-" + validEvents.size() + "): ");

		while (eventChoice < 1 || eventChoice > validEvents.size()) {
			eventChoice = InputValidationUtil.readInt(ScannerUtil.getScanner(), "Enter a valid choice: ");
		}

		Event selectedEvent = validEvents.get(eventChoice - 1);
		int capacity = selectedEvent.getCapacity();

		List<Ticket> tickets = organizerService.viewTicketAvailability(selectedEvent.getEventId());

		int totalTickets = tickets.stream().mapToInt(Ticket::getTotalQuantity).sum();

		if (totalTickets == capacity) {
			System.out.println("Ticket quantity already matches event capacity");
			return;
		}

		int remaining = capacity - totalTickets;

		System.out.println("\nEvent Capacity: " + capacity + "\nCurrent Ticket Quantity: " + totalTickets
				+ "\nRemaining Capacity: " + remaining);

		System.out.println("\n1. Update existing ticket\n" + "2. Add new ticket\n\n>");

		int option = InputValidationUtil.readInt(ScannerUtil.getScanner(), "");

		if (option == 2) {
			createTicketForEvent(selectedEvent.getEventId(), remaining);
			return;
		}

		if (option != 1) {
			System.out.println("Invalid option");
			return;
		}

		if (tickets.isEmpty()) {
			System.out.println("No tickets available to update");
			return;
		}

		int index = 1;
		for (Ticket t : tickets) {
			System.out.println(index + ". " + t.getTicketType() + " | Total: " + t.getTotalQuantity());
			index++;
		}

		int ticketChoice = InputValidationUtil.readInt(ScannerUtil.getScanner(),
				"Select a ticket (1-" + tickets.size() + "): ");

		while (ticketChoice < 1 || ticketChoice > tickets.size()) {
			ticketChoice = InputValidationUtil.readInt(ScannerUtil.getScanner(), "Enter a valid choice: ");
		}

		Ticket selectedTicket = tickets.get(ticketChoice - 1);

		int maxAllowed = selectedTicket.getTotalQuantity() + remaining;

		int newQty = InputValidationUtil.readInt(ScannerUtil.getScanner(),
				"Enter new quantity (max " + maxAllowed + "): ");

		while (newQty <= 0 || newQty > maxAllowed) {
			newQty = InputValidationUtil.readInt(ScannerUtil.getScanner(), "Enter valid quantity: ");
		}

		boolean result = organizerService.updateTicketQuantity(selectedTicket.getTicketId(), newQty);

		System.out.println(result ? "Ticket quantity updated successfully" : "Update failed");
	}

	private void viewTicketAvailability() {

		List<Event> events = organizerService.getOrganizerEvents(loggedInUser.getUserId());

		if (events.isEmpty()) {
			System.out.println("You have no events");
			return;
		}

		List<Event> validEvents = events.stream()
				.filter(e -> EventStatus.DRAFT.toString().equals(e.getStatus())
						|| EventStatus.PUBLISHED.toString().equals(e.getStatus()))
				.sorted(Comparator.comparing(Event::getStartDateTime)).collect(Collectors.toList());

		if (validEvents.isEmpty()) {
			System.out.println("No events available");
			return;
		}

		MenuHelper.printEventSummaries(validEvents);

		int eventChoice = InputValidationUtil.readInt(ScannerUtil.getScanner(),
				"Select an event (1-" + validEvents.size() + "): ");

		while (eventChoice < 1 || eventChoice > validEvents.size()) {
			eventChoice = InputValidationUtil.readInt(ScannerUtil.getScanner(), "Enter a valid choice: ");
		}

		Event selectedEvent = validEvents.get(eventChoice - 1);

		List<Ticket> tickets = organizerService.viewTicketAvailability(selectedEvent.getEventId());

		if (tickets.isEmpty()) {
			System.out.println("No tickets available for this event");
			return;
		}

		int index = 1;
		for (Ticket t : tickets) {
			System.out.println(index + ". " + t.getTicketType() + " | Total: " + t.getTotalQuantity() + " | Available: "
					+ t.getAvailableQuantity() + " | Price: " + t.getPrice());
			index++;
		}
	}

	private void registrationMenu() {

		while (true) {
			System.out.println("\nRegistrations\n" + "1. View event registrations\n" + "2. View registered users\n"
					+ "3. Back\n\n>");

			int choice = InputValidationUtil.readInt(ScannerUtil.getScanner(), "");

			switch (choice) {

			case 1: {
				List<Event> events = organizerService.getOrganizerEvents(loggedInUser.getUserId());

				if (events.isEmpty()) {
					System.out.println("You have no events");
					return;
				}

				MenuHelper.printEventSummaries(events);

				int choice1 = InputValidationUtil.readInt(ScannerUtil.getScanner(),
						"Select an event (1-" + events.size() + "): ");

				while (choice1 < 1 || choice1 > events.size()) {
					choice1 = InputValidationUtil.readInt(ScannerUtil.getScanner(), "Enter a valid choice: ");
				}
				Event selectedEvent = events.get(choice1 - 1);
				if (selectedEvent == null)
					return;

				int count = organizerService.viewEventRegistrations(selectedEvent.getEventId());

				System.out.println("Total Registrations: " + count);
				break;
			}

			case 2: {
				List<Event> events = organizerService.getOrganizerEvents(loggedInUser.getUserId());

				if (events.isEmpty()) {
					System.out.println("You have no events");
					return;
				}

				MenuHelper.printEventSummaries(events);

				int choice1 = InputValidationUtil.readInt(ScannerUtil.getScanner(),
						"Select an event (1-" + events.size() + "): ");

				while (choice1 < 1 || choice1 > events.size()) {
					choice1 = InputValidationUtil.readInt(ScannerUtil.getScanner(), "Enter a valid choice: ");
				}
				Event selectedEvent = events.get(choice1 - 1);
				if (selectedEvent == null)
					return;

				List<Map<String, Object>> users = organizerService.viewRegisteredUsers(selectedEvent.getEventId());

				users.forEach(
						u -> System.out.println(u.get("userId") + " | " + u.get("name") + " | " + u.get("email")));
				break;
			}

			case 3: {
				return;
			}

			default: {
				System.out.println("Invalid option");
			}
			}
		}
	}

	private void reportMenu() {

		while (true) {
			System.out.println("\nReports\n" + "1. View event registrations\n" + "2. View ticket sales\n"
					+ "3. View revenue summary\n" + "4. Back\n\n>");

			int choice = InputValidationUtil.readInt(ScannerUtil.getScanner(), "");

			switch (choice) {
			case 1: {
				List<Map<String, Object>> list = organizerService.getEventWiseRegistrations(loggedInUser.getUserId());
				list.forEach(r -> System.out.println(r.get("event") + " | " + r.get("count")));
				break;
			}
			case 2: {
				List<Map<String, Object>> list = organizerService.getTicketSales(loggedInUser.getUserId());
				list.forEach(r -> System.out.println(r.get("ticketType") + " | Sold: " + r.get("sold")));
				break;
			}
			case 3: {
				double revenue = organizerService.getRevenueSummary(loggedInUser.getUserId());
				System.out.println("Total Revenue: " + revenue);
				break;
			}
			case 4: {
				return;
			}
			default:
				System.out.println("Invalid option");
			}
		}
	}

	private void notificationMenu() {

		while (true) {
			System.out.println("\nNotifications\n" + "1. Send event update\n" + "2. Send schedule change\n"
					+ "3. View my notifications\n" + "4. Back\n\n>");

			int choice = InputValidationUtil.readInt(ScannerUtil.getScanner(), "");

			switch (choice) {
			case 1: {
				List<Event> events = organizerService.getOrganizerEvents(loggedInUser.getUserId());

				if (events.isEmpty()) {
					System.out.println("You have no events");
					return;
				}

				MenuHelper.printEventSummaries(events);

				int choice1 = InputValidationUtil.readInt(ScannerUtil.getScanner(),
						"Select an event (1-" + events.size() + "): ");

				while (choice1 < 1 || choice1 > events.size()) {
					choice1 = InputValidationUtil.readInt(ScannerUtil.getScanner(), "Enter a valid choice: ");
				}
				Event selectedEvent = events.get(choice1 - 1);
				String msg = InputValidationUtil.readString(ScannerUtil.getScanner(), "Message: ");
				organizerService.sendEventUpdate(selectedEvent.getEventId(), msg);
				break;
			}
			case 2: {
				List<Event> events = organizerService.getOrganizerEvents(loggedInUser.getUserId());

				if (events.isEmpty()) {
					System.out.println("You have no events");
					return;
				}

				MenuHelper.printEventSummaries(events);

				int choice1 = InputValidationUtil.readInt(ScannerUtil.getScanner(),
						"Select an event (1-" + events.size() + "): ");

				while (choice1 < 1 || choice1 > events.size()) {
					choice1 = InputValidationUtil.readInt(ScannerUtil.getScanner(), "Enter a valid choice: ");
				}
				Event selectedEvent = events.get(choice1 - 1);
				String msg = InputValidationUtil.readString(ScannerUtil.getScanner(), "Message: ");
				organizerService.sendScheduleChange(selectedEvent.getEventId(), msg);
				break;
			}
			case 3:
				notificationService.displayAllNotifications(loggedInUser.getUserId());
				break;
			case 4: {
				return;
			}
			default:
				System.out.println("Invalid option");
			}
		}
	}

	private boolean confirmLogout() {
		char choice = InputValidationUtil.readChar(ScannerUtil.getScanner(), "Are you sure to logout (Y/N): ");
		return Character.toUpperCase(choice) == 'Y';
	}

	private void createTicketForEvent(int eventId, int remainingCapacity) {

		Ticket ticket = new Ticket();
		ticket.setEventId(eventId);

		ticket.setTicketType(InputValidationUtil.readNonEmptyString(ScannerUtil.getScanner(), "Ticket Type: "));

		ticket.setPrice(InputValidationUtil.readDouble(ScannerUtil.getScanner(), "Ticket Price: "));

		int qty = InputValidationUtil.readInt(ScannerUtil.getScanner(),
				"Ticket Quantity (max " + remainingCapacity + "): ");

		while (qty <= 0 || qty > remainingCapacity) {
			qty = InputValidationUtil.readInt(ScannerUtil.getScanner(), "Enter valid quantity: ");
		}

		ticket.setTotalQuantity(qty);

		organizerService.createTicket(ticket);

		System.out.println("Ticket added successfully");
	}

}
