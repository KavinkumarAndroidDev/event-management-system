package com.ems.util;

import java.util.Scanner;

/*
 * Provides a shared scanner instance for console input.
 *
 * Responsibilities:
 * - Maintain a single Scanner tied to standard input
 * - Avoid multiple scanner instances across menus
 * - Ensure consistent input handling throughout the application
 *
 * Prevents resource conflicts in a menu-driven CLI environment.
 */
public final class ScannerUtil {


    private static final Scanner SCANNER = new Scanner(System.in);

    private ScannerUtil() {
        // prevent instantiation
    }

    public static Scanner getScanner() {
        return SCANNER;
    }

    public static void close() {
        SCANNER.close();
    }
}
