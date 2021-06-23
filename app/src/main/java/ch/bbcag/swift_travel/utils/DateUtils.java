package ch.bbcag.swift_travel.utils;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class DateUtils {
	public static long parseDateToMillis(String dateString) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
		return LocalDate.parse(dateString, formatter).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
	}

	public static long getDaysCountFromTimeSpan(long startDate, long endDate) {
		// Add a day because otherwise the last day would be missing
		return ((endDate + 86400000) - startDate) / (1000 * 60 * 60 * 24);
	}
}
