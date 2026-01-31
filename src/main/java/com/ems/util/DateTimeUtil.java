package com.ems.util;

import java.sql.Timestamp;
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

    private static final List<String> DATE_FORMATS =
            Arrays.asList("yyyy-MM-dd", "dd-MM-yyyy", "dd/MM/yyyy");

    private static final List<String> DATE_TIME_FORMATS =
            Arrays.asList(
                    "yyyy-MM-dd HH:mm",
                    "dd-MM-yyyy HH:mm",
                    "dd/MM/yyyy HH:mm",
                    "yyyy-MM-dd HH:mm:ss",
                    "dd-MM-yyyy HH:mm:ss",
                    "dd/MM/yyyy HH:mm:ss"
            );

    public static String formatDateTime(LocalDateTime localDateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        return "Date: " + localDateTime.toLocalDate().format(formatter)
                + " Time: " + localDateTime.toLocalTime();
    }

    public static Instant getCurrentUtc() {
        return Instant.now();
    }

    public static ZonedDateTime convertUtcToLocal(Instant utcInstant) {
        return utcInstant.atZone(ZoneId.systemDefault());
    }

    public static Instant convertLocalToUtc(Timestamp timestamp) {
        return timestamp.toInstant();
    }

    public static Instant convertLocalDefaultToUtc(LocalDateTime localDateTime) {
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant();
    }

    public static LocalDate parseLocalDate(String dateString) {
        for (String format : DATE_FORMATS) {
            try {
                return LocalDate.parse(
                        dateString,
                        DateTimeFormatter.ofPattern(format)
                );
            } catch (DateTimeParseException ignored) {
            }
        }
        return null;
    }

    public static LocalDateTime parseLocalDateTime(String dateTimeString) {
        for (String format : DATE_TIME_FORMATS) {
            try {
                return LocalDateTime.parse(
                        dateTimeString,
                        DateTimeFormatter.ofPattern(format)
                );
            } catch (DateTimeParseException ignored) {
            }
        }
        return null;
    }
}