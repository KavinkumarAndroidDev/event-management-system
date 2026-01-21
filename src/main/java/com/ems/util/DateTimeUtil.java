package com.ems.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeUtil {
	public static String formatDateTime(LocalDateTime localDateTime) {
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
		return "Date: " + localDateTime.toLocalDate().format(dateTimeFormatter).toString() + " Time: "+ localDateTime.toLocalTime();
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
}