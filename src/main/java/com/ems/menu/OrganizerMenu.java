package com.ems.menu;

import java.util.Scanner;

import com.ems.model.User;
import com.ems.util.InputValidationUtil;

public class OrganizerMenu {
	Scanner scanner;
	User loggedInUser;
	
	OrganizerMenu(Scanner scanner, User user){
		this.scanner = scanner;
		this.loggedInUser = user;
		this.start();
	}
	private void start() {
		while(true) {
			System.out.println("\nOrganizer Menu"
					+ "\n\nEnter your choice:\r\n"
			        + "1. Create new event\r\n"
			        + "2. Update event details\r\n"
			        + "3. Publish event for approval\r\n"
			        + "4. Cancel event\r\n"
			        + "5. Create ticket type\r\n"
			        + "6. Update ticket price\r\n"
			        + "7. Update ticket quantity\r\n"
			        + "8. View registrations for event\r\n"
			        + "9. View ticket sales\r\n"
			        + "10. View revenue summary\r\n"
			        + "11. Send event notifications\r\n"
			        + "12. View my notifications\r\n"
			        + "13. Logout"
			        + "\n>");
			int input = InputValidationUtil.readInt(scanner, "");
			switch(input) {
				case 1:
					break;
				case 2:
					break;
				case 3:
					break;
				case 4:
					break;
			}
			
		}
	}
}


