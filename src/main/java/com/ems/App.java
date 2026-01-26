package com.ems;

import com.ems.menu.MainMenu;
import com.ems.util.ScannerUtil;

/*
 * Application entry point.
 *
 * Responsibilities:
 * - Bootstraps the console-based application
 * - Initializes the main menu flow
 * - Ensures shared resources are closed on exit
 */
public class App {

	public static void main(String[] arge) {
		System.out.println("Welcome to the Event management system");
		MainMenu mainMenu = new MainMenu();
		mainMenu.start();
		ScannerUtil.close();
	}
}








































//Logging feature