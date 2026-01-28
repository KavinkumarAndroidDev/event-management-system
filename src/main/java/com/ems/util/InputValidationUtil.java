package com.ems.util;

import java.util.Scanner;

/*
 * Provides reusable console input helpers with built-in validation.
 *
 * Responsibilities:
 * - Safely read primitive and wrapper values from user input
 * - Enforce basic input constraints at the UI boundary
 * - Prevent invalid data from propagating into service and DAO layers
 *
 * Centralizes input handling to keep menu code clean and consistent.
 */
public final class InputValidationUtil {

	
	private InputValidationUtil() {
		
	}
    public static int readInt(Scanner scanner, String message) {
        while (true) {
            System.out.print(message);
            if (scanner.hasNextInt()) {
                int value = scanner.nextInt();
                scanner.nextLine();
                
                return value;
            } else {
                System.out.println("Invalid input. Please enter an integer.");
                scanner.nextLine();
            }
        }
    }
    //Int cannot be null, Integer can be nullable..
    //few datas in the db can be nullable for that condition Integer is used over int
    //understanding required....
    public static Integer readInteger(Scanner scanner, String message) {
        while (true) {
            System.out.print(message);
            if (scanner.hasNextInt()) {
                Integer value = scanner.nextInt();
                scanner.nextLine();
                return value;
            } else {
                System.out.println("Invalid input. Please enter an Integer.");
                scanner.nextLine();
            }
        }
    }

    public static double readDouble(Scanner scanner, String message) {
        while (true) {
            System.out.print(message);
            if (scanner.hasNextDouble()) {
                double value = scanner.nextDouble();
                scanner.nextLine();
                return value;
            } else {
                System.out.println("Invalid input. Please enter a decimal number.");
                scanner.nextLine();
            }
        }
    }

    public static float readFloat(Scanner scanner, String message) {
        while (true) {
            System.out.print(message);
            if (scanner.hasNextFloat()) {
                float value = scanner.nextFloat();
                scanner.nextLine();
                return value;
            } else {
                System.out.println("Invalid input. Please enter a float value.");
                scanner.nextLine();
            }
        }
    }

    public static Double readDecimal(Scanner scanner, String message) {
        while (true) {
            System.out.print(message);
            if (scanner.hasNextDouble()) {
                Double value = scanner.nextDouble();
                scanner.nextLine();
                return value;
            } else {
                System.out.println("Invalid input. Please enter a valid decimal number.");
                scanner.nextLine();
            }
        }
    }

    public static char readChar(Scanner scanner, String message) {
        while (true) {
            System.out.print(message);
            String input = scanner.nextLine();
            if (input.length() == 1) {
                return input.charAt(0);
            }
            System.out.println("Invalid input. Please enter a single character.");
        }
    }

    public static String readNonEmptyString(Scanner scanner, String message) {
        while (true) {
            System.out.print(message);
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                return input;
            }
            System.out.println("Input cannot be empty.");
        }
    }

    public static String readString(Scanner scanner, String message) {
        System.out.print(message);
        return scanner.nextLine();
    }
}
