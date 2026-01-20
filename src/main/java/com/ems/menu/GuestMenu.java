package com.ems.menu;

import java.util.Scanner;

import com.ems.util.InputValidationUtil;

public class GuestMenu {
	Scanner scanner;
	public GuestMenu(Scanner scanner) {
		this.scanner = scanner;
		this.start();
	}
	
	private void start() {
		while(true) {
			System.out.println("\nGuest menu"
					+ "\n\nPlease select an option:\r\n"
			        + "1. View published events\r\n"
			        + "2. Search and Filter events\r\n"
			        + "3. Register account\r\n"
			        + "4. Exit Guest Mode\n"
			        + "Guest accounts have limited access.\n"
			        + "Please register or log in to use all features.\n"
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
			case 5:
				break;
			case 6:
			    System.out.println("Exiting Guest Mode...");
			    return;   
			default:
				System.out.println("Enter the valid option");
				break;
			}
			
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
