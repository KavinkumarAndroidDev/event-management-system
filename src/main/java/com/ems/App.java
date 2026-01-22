package com.ems;

import com.ems.menu.MainMenu;
import com.ems.util.ScannerUtil;

public class App {
	public static void main(String[] arge) {
		System.out.println("Welcome to the Event management system");
		MainMenu mainMenu = new MainMenu();
		mainMenu.start();
		ScannerUtil.close();
	}
}
