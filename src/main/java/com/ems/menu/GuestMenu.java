package com.ems.menu;

import com.ems.service.GuestService;
import com.ems.util.ApplicationUtil;
import com.ems.util.InputValidationUtil;
import com.ems.util.ScannerUtil;

public class GuestMenu extends BaseMenu {
	private final GuestService guestService;
	public GuestMenu() {
		super(null);
		this.guestService = ApplicationUtil.guestService();
	}
	public void start() {
		while(true) {
			System.out.println("\nGuest menu"
					+ "\n\nPlease select an option\n"
					+ "1. Browse Events\n" 
		            + "2. Search & Filter Events\n"
			        + "3. Register account\n"
			        + "4. Exit Guest Mode\n"
			        + "Guest accounts have limited access.\n"
			        + "Please register or log in to use all features.\n"
			        + "\n>");
			int input = InputValidationUtil.readInt(ScannerUtil.getScanner(), "");
			switch(input) {
			case 1:
                browseEventsMenu();
                break;
            case 2:
                guestService.searchEvents();
                break;
			case 3:
				guestService.createAccount();
				break;
			case 4:
			    System.out.println("Exiting Guest Mode...");
			    return;   
			default:
				System.out.println("Enter the valid option");
				break;
			}
			
		}
	}
	private void browseEventsMenu() {

	    while (true) {
	        System.out.println(
	            "\nBrowse Events\n" +
	            "1. View all available events\n" +
	            "2. View event details\n" +
	            "3. View ticket options\n" +
	            "4. Back\n>"
	        );

	        int choice = InputValidationUtil.readInt(
	            ScannerUtil.getScanner(), ""
	        );

	        switch (choice) {
	            case 1:
	            	guestService.printAllAvailableEvents();
	                break;
	            case 2:
	            	guestService.viewEventDetails();
	                break;
	            case 3:
	            	guestService.viewTicketOptions();
	                break;
	            case 4:
	                return;
	            default:
	                System.out.println("Invalid option");
	        }
	    }
	}
}