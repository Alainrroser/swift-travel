package ch.bbcag.swift_travel.utils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class DateTimeUtils {
	public static long parseDateToMilliseconds(String dateString) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
		return LocalDate.parse(dateString, formatter).atStartOfDay().atZone(ZoneId.of("+0")).toInstant().toEpochMilli();
	}

	public static long parseTimeToMilliseconds(String timeString) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
		return LocalTime.parse(timeString, formatter).atDate(LocalDate.now(ZoneId.of("+0"))).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
	}

	public static long getDaysCountFromTimeSpan(long startDate, long endDate) {
		return ((endDate + (1000 * 60 * 60 * 24)) - startDate) / (1000 * 60 * 60 * 24);
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
