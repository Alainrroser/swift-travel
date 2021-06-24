package ch.bbcag.swift_travel.utils;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import zion830.com.range_picker_dialog.TimeRange;

public class DateTimeUtils {
	public static long parseDateToMilliseconds(String dateString) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
		// Subtract a day so you can be at two locations in one day
		return LocalDate.parse(dateString, formatter).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() - (24 * 60 * 60 * 1000);
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
		if (minute == 0) {
			return "00";
		}
		return String.valueOf(minute);
	}

	public static int getHourForAmPm(TimeRange timeRange, boolean startHour) {
		if(startHour && timeRange.getReadableTimeRange().startsWith("PM", 1)) {
			return timeRange.getStartHour() + 12;
		} else if(timeRange.getReadableTimeRange().startsWith("PM", 10)) {
			return timeRange.getEndHour() + 12;
		} else if(startHour) {
			return timeRange.getStartHour();
		}
		return timeRange.getEndHour();
	}

}