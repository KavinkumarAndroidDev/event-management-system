package com.ems.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeUtil {
	public static String formatDateTime(LocalDateTime localDateTime) {
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyy");
		return "Date: " + localDateTime.toLocalDate().format(dateTimeFormatter).toString() + " Time: "+ localDateTime.toLocalTime();
	}
}


//DateTimeUtil
//Convert LocalDateTime ↔ Timestamp
//Format dates for display