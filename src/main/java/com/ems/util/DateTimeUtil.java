package com.ems.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;

/*
 * Utility methods for date and time handling across the application.
 *
 * Responsibilities:
 * - Convert between local time and UTC
 * - Parse and format dates and date-time values
 * - Provide consistent date-time input handling for console interactions
 *
 * Centralizes date logic to avoid duplication
 * and inconsistent time conversions.
 */
public final class DateTimeUtil {

	private DateTimeUtil() {
		
	}
	public static String formatDateTime(LocalDateTime localDateTime) {
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
		return "Date: " + localDateTime.toLocalDate().format(dateTimeFormatter).toString() + " Time: "+ localDateTime.toLocalTime();
	}
	public static LocalDate formatDate(String date) {
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
		return LocalDate.parse(date, dateTimeFormatter);
	}
	public static Instant getCurrentUtc() {
        return Instant.now();
    }
	public static ZonedDateTime convertUtcToLocal(Instant utcInstant) {
        return utcInstant.atZone(ZoneId.systemDefault());
    }
	
	public static Instant convertLocalToUtc(ZonedDateTime localZonedDateTime) {
        return localZonedDateTime.toInstant();
    }
    
    public static Instant convertLocalDefaultToUtc(LocalDateTime localDateTime) {
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant();
    }
    public static LocalDate getLocalDate(String message) {
        List<String> formats = Arrays.asList("yyyy-MM-dd", "dd-MM-yyyy", "dd/MM/yyyy");
        LocalDate localDate = null;

        while (localDate == null) {
            String dateString = InputValidationUtil.readString(ScannerUtil.getScanner(), message);
            
            for (String format : formats) {
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
                    localDate = LocalDate.parse(dateString, formatter);
                    break;
                } catch (DateTimeParseException ignored) {

                }
            }

            if (localDate == null) {
                System.out.println("Invalid date. Please use one of: " + formats);
            }
        }
        return localDate;
    }
    
    public static LocalDateTime getLocalDateTime(String message) {
        List<String> formats = Arrays.asList(
            "yyyy-MM-dd HH:mm",
            "dd-MM-yyyy HH:mm",
            "dd/MM/yyyy HH:mm",
            "yyyy-MM-dd HH:mm:ss",
            "dd-MM-yyyy HH:mm:ss",
            "dd/MM/yyyy HH:mm:ss"
        );

        LocalDateTime localDateTime = null;

        while (localDateTime == null) {
            String dateTimeString = InputValidationUtil.readString(
                ScannerUtil.getScanner(),
                message
            );

            for (String format : formats) {
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
                    localDateTime = LocalDateTime.parse(dateTimeString, formatter);
                    break;
                } catch (DateTimeParseException ignored) {
                }
            }

            if (localDateTime == null) {
                System.out.println("Invalid date time. Please use one of: " + formats);
            }
        }

        return localDateTime;
    }

}