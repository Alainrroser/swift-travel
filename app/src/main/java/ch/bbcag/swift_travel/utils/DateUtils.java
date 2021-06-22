package ch.bbcag.swift_travel.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class DateUtils {
	public static long parseDateToMillis(String dateString) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
		java.util.Date date = sdf.parse(dateString);
		if (date != null) {
			// Subtract a day in milliseconds so you can be at two locations in one day
			date.setTime(date.getTime() - (24 * 60 * 60 * 1000));

			return date.getTime();
		}
		return -1;
	}

	public static long getDaysCountFromTimeSpan(long startDate, long endDate) {
		return (startDate - endDate) / (1000 * 60 * 60 * 24);
	}
}
