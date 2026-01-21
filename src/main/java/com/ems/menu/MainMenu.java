package com.ems.menu;

import com.ems.exception.AuthenticationException;
import com.ems.exception.AuthorizationException;
import com.ems.service.UserService;
import com.ems.util.InputValidationUtil;
import com.ems.util.ScannerUtil;

public class MainMenu {

	public MainMenu() {
		start();
	}

	private void start() {
		while(true) {
			System.out.println("\nMain Menu"
					+ "\n\nEnter your choice:"
			        + "\n1. Login"
			        + "\n2. Register as User"
			        + "\n3. Register as Organizer"
			        + "\n4. Continue as Guest"
			        + "\nAny other number to Exit Application"
			        + "\n>");
			int input = InputValidationUtil.readInt(ScannerUtil.getScanner(), "");
			switch(input) {
				case 1:
					try {
						UserService.login();
					} catch (AuthorizationException e) {
						System.out.println(e.getMessage());
					} catch (AuthenticationException e) {
						System.out.println(e.getMessage());
					}
					break;
				case 2:
					UserService.createAccount(1);
					break;
				case 3:
					UserService.createAccount(2);
					break;
				case 4:
					new GuestMenu();
					break;
				default:
					System.out.println("Thank you for using our event management system");
					return;
			}
			
		}
	}

}
