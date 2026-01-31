package com.ems.actions;

import java.util.List;

import com.ems.model.Event;
import com.ems.model.Venue;
import com.ems.service.AdminService;
import com.ems.service.EventService;
import com.ems.util.ApplicationUtil;
import com.ems.util.InputValidationUtil;
import com.ems.util.MenuHelper;
import com.ems.util.ScannerUtil;

public class AdminVenueManagementAction {
    private final AdminService adminService;
    private final EventService eventService;

    public AdminVenueManagementAction() {
        this.adminService = ApplicationUtil.adminService();
        this.eventService = ApplicationUtil.eventService();
    }

    public List<Venue> getAllVenues() {
        return eventService.getAllVenues();
    }

    public void addVenue() {
		Venue venue = new Venue();

		venue.setName(
				InputValidationUtil.readNonEmptyString(ScannerUtil.getScanner(), "Enter the venue name: "));
		venue.setStreet(InputValidationUtil.readNonEmptyString(ScannerUtil.getScanner(), "Enter the street: "));
		venue.setCity(InputValidationUtil.readNonEmptyString(ScannerUtil.getScanner(), "Enter the city: "));
		venue.setState(InputValidationUtil.readNonEmptyString(ScannerUtil.getScanner(), "Enter the state: "));
		venue.setPincode(
				InputValidationUtil.readNonEmptyString(ScannerUtil.getScanner(), "Enter the pincode: "));
		venue.setMaxCapacity(
				InputValidationUtil.readInt(ScannerUtil.getScanner(), "Enter the maximum capacity: "));

        adminService.addVenue(venue);
		System.out.println("Venue added successfully.");
    }

    public void updateVenue() {
		Venue selectedVenue = selectVenue();
		if (selectedVenue == null)
			return;

		System.out.println("Press Enter to keep the current value");

		String name = InputValidationUtil.readString(ScannerUtil.getScanner(),
				"Venue name (" + selectedVenue.getName() + "): ");
		if (!name.isBlank()) {
			selectedVenue.setName(name);
		}

		String street = InputValidationUtil.readString(ScannerUtil.getScanner(),
				"Street (" + selectedVenue.getStreet() + "): ");
		if (!street.isBlank()) {
			selectedVenue.setStreet(street);
		}

		String city = InputValidationUtil.readString(ScannerUtil.getScanner(),
				"City (" + selectedVenue.getCity() + "): ");
		if (!city.isBlank()) {
			selectedVenue.setCity(city);
		}

		String state = InputValidationUtil.readString(ScannerUtil.getScanner(),
				"State (" + selectedVenue.getState() + "): ");
		if (!state.isBlank()) {
			selectedVenue.setState(state);
		}

		String pincode = InputValidationUtil.readString(ScannerUtil.getScanner(),
				"Pincode (" + selectedVenue.getPincode() + "): ");
		if (!pincode.isBlank()) {
			selectedVenue.setPincode(pincode);
		}

		int capacity = InputValidationUtil.readInt(ScannerUtil.getScanner(),
				"Maximum capacity (" + selectedVenue.getMaxCapacity() + ") enter 0 to skip: ");
		if (capacity > 0) {
			selectedVenue.setMaxCapacity(capacity);
		}

		adminService.updateVenue(selectedVenue);
		System.out.println("Venue updated successfully");
    }

    public void removeVenue() {
		Venue selectedVenue = selectVenue();
		if (selectedVenue == null)
			return;

		char confirm = InputValidationUtil.readChar(ScannerUtil.getScanner(),
				"Are you sure you want to remove this venue (Y/N): ");

		if (Character.toUpperCase(confirm) != 'Y') {
			System.out.println("Venue removal cancelled.");
			return;
		}

        adminService.removeVenue(selectedVenue.getVenueId());

		System.out.println("Venue removed successfully.");
    }

    public void listEventsByCity() {
    	Venue selectedVenue = selectVenue();
		if (selectedVenue == null)
			return;

		List<Event> events = eventService.searchByCity(selectedVenue.getVenueId());

		if (events.isEmpty()) {
			System.out.println("No events for this venue");
		} else {
			MenuHelper.printEventSummaries(events);
		}
    }

	public void listAllVenues() {
		List<Venue> venues = getAllVenues();

		if (venues.isEmpty()) {
			System.out.println("No venues found.");
		} else {
			MenuHelper.displayVenues(venues);
		}
	}
	
	private Venue selectVenue() {

		List<Venue> venues = getAllVenues();

		if (venues.isEmpty()) {
			System.out.println("No venues found.");
			return null;
		}

		MenuHelper.displayVenues(venues);

		int choice = InputValidationUtil.readInt(ScannerUtil.getScanner(),
				"Select a venue (1-" + venues.size() + "): ");

		while (choice < 1 || choice > venues.size()) {
			choice = InputValidationUtil.readInt(ScannerUtil.getScanner(), "Enter a valid choice: ");
		}

		return venues.get(choice - 1);
	}
}