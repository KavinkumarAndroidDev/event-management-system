package com.ems.util;

import java.util.Scanner;

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
