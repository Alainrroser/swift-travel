package ch.bbcag.swift_travel.utils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class DateTimeUtils {
	public static long parseDateToMilliseconds(String dateString) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
		// Subtract a day so you can be at two locations in one day
		return LocalDate.parse(dateString, formatter).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() - (24 * 60 * 60 * 1000);
	}

	public static long parseTimeToMilliseconds(String timeString) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
		return LocalTime.parse(timeString, formatter).atDate(LocalDate.now(ZoneId.systemDefault())).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
	}

	public static long getDaysCountFromTimeSpan(long startDate, long endDate) {
		// Add a day because otherwise the last day would be missing
		return ((endDate + 86400000) - startDate) / (1000 * 60 * 60 * 24);
	}

	public static String addZeroToHour(int hour) {
		if (hour < 10) {
			return "0" + hour;
		}
		return String.valueOf(hour);
	}

	public static String addZeroToMinute(int minute) {
		if (minute < 10) {
			return "00";
		}
		return String.valueOf(minute);
	}
}
