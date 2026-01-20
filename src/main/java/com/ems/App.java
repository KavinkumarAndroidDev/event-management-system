package com.ems;

import java.util.Scanner;

import com.ems.menu.MainMenu;

public class App {
	public static void main(String[] arge) {
		Scanner scanner = new Scanner(System.in);
		System.out.println("Welcome to the Event management system");
		MainMenu mainMenu = new MainMenu(scanner);
		scanner.close();
	}
}
