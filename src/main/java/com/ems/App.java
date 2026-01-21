package com.ems;

import java.util.Scanner;

import com.ems.menu.MainMenu;
import com.ems.util.ScannerUtil;

public class App {
	public static void main(String[] arge) {
		Scanner scanner = new Scanner(System.in);
		System.out.println("Welcome to the Event management system");
		MainMenu mainMenu = new MainMenu();
		ScannerUtil.close();
	}
}
