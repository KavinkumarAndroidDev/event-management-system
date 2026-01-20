package com.ems.menu;

import java.util.Scanner;

import com.ems.exception.AuthenticationException;
import com.ems.exception.AuthorizationException;
import com.ems.model.User;
import com.ems.service.UserService;
import com.ems.util.InputValidationUtil;

public class MainMenu {
	Scanner scanner;

	public MainMenu(Scanner scanner) {
		this.scanner = scanner;
		this.start();
	}

	private void start() {
		UserService us = new UserService(scanner);
		while(true) {
			System.out.println("\nMain Menu"
					+ "\n\nEnter your choice:"
			        + "\n1. Login"
			        + "\n2. Register as User"
			        + "\n3. Register as Organizer"
			        + "\n4. Continue as Guest"
			        + "\nAny other number to Exit Application"
			        + "\n>");
			int input = InputValidationUtil.readInt(scanner, "");
			switch(input) {
				case 1:
					String email = InputValidationUtil.readString(scanner, "Enter the email address: ");
					String password = InputValidationUtil.readString(scanner, "Enter the password: ");
					try {
						User user = UserService.login(email, password);
						int role = UserService.getRole(user);
						if(role == 0) {
							System.out.println("Unexpected role");
						}else if(role == 1) {
							new AdminMenu(scanner, user);
						}else if(role == 2) {
							new UserMenu(scanner, user);
						}else if(role == 3) {
							new OrganizerMenu(scanner, user);
						}
					} catch (AuthorizationException | AuthenticationException e) {
						System.out.println(e.getMessage());
					}
					
					break;
				case 2:
					us.createAccount(1);
					break;
				case 3:
					us.createAccount(2);
					break;
				case 4:
					new GuestMenu(scanner);
					break;
				default:
					System.out.println("Thank you for using our event management system");
					return;
			}
			
		}
	}

}
