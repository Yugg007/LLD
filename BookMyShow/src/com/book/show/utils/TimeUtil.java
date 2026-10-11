package com.book.show.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class TimeUtil {

	private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy H:mm");

	// date = "09/10/2026", time = "14:30"
	public static LocalDateTime toDateTime(String date, String time) {
		try {
			return LocalDateTime.parse(date.trim() + " " + time.trim(), DATE_TIME_FORMAT);
		} catch (DateTimeParseException e) {
			throw new IllegalArgumentException(
					"Expected date dd/MM/yyyy and time HH:mm. Got: " + date + " " + time, e);
		}
	}
}