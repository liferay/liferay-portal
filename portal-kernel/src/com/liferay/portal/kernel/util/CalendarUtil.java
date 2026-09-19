/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.util;

import com.liferay.petra.string.StringBundler;

import java.sql.Timestamp;

import java.text.Format;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Brian Wing Shun Chan
 */
public class CalendarUtil {

	public static final String[] DAYS_ABBREVIATION = {
		"sunday-abbreviation", "monday-abbreviation", "tuesday-abbreviation",
		"wednesday-abbreviation", "thursday-abbreviation",
		"friday-abbreviation", "saturday-abbreviation"
	};

	public static final String ICAL_EXTENSION = "ics";

	public static final int[] MONTH_IDS = {
		Calendar.JANUARY, Calendar.FEBRUARY, Calendar.MARCH, Calendar.APRIL,
		Calendar.MAY, Calendar.JUNE, Calendar.JULY, Calendar.AUGUST,
		Calendar.SEPTEMBER, Calendar.OCTOBER, Calendar.NOVEMBER,
		Calendar.DECEMBER
	};

	public static boolean afterByDay(Date date1, Date date2) {
		long millis1 = _getTimeInMillis(date1);
		long millis2 = _getTimeInMillis(date2);

		if (millis1 > millis2) {
			return true;
		}

		return false;
	}

	public static boolean beforeByDay(Date date1, Date date2) {
		long millis1 = _getTimeInMillis(date1);
		long millis2 = _getTimeInMillis(date2);

		if (millis1 < millis2) {
			return true;
		}

		return false;
	}

	public static boolean equalsByDay(Date date1, Date date2) {
		long millis1 = _getTimeInMillis(date1);
		long millis2 = _getTimeInMillis(date2);

		if (millis1 == millis2) {
			return true;
		}

		return false;
	}

	public static int getAge(Date date, Calendar today) {
		Calendar birthday = CalendarFactoryUtil.getCalendar();

		birthday.setTime(date);

		int yearDiff = today.get(Calendar.YEAR) - birthday.get(Calendar.YEAR);

		if (today.get(Calendar.MONTH) < birthday.get(Calendar.MONTH)) {
			yearDiff--;
		}
		else if ((today.get(Calendar.MONTH) == birthday.get(Calendar.MONTH)) &&
				 (today.get(Calendar.DATE) < birthday.get(Calendar.DATE))) {

			yearDiff--;
		}

		return yearDiff;
	}

	public static int getAge(Date date, TimeZone tz) {
		return getAge(date, CalendarFactoryUtil.getCalendar(tz));
	}

	public static String[] getDays(Locale locale) {
		return getDays(locale, null);
	}

	public static String[] getDays(Locale locale, String pattern) {
		if (Validator.isNull(pattern)) {
			pattern = "EEEE";
		}

		String key = StringBundler.concat(
			"days_", pattern, "_", locale.getLanguage(), "_",
			locale.getCountry());

		String[] days = _calendarPool.get(key);

		if (days == null) {
			days = new String[7];

			Format dayFormat = FastDateFormatFactoryUtil.getSimpleDateFormat(
				pattern, locale);

			Calendar cal = CalendarFactoryUtil.getCalendar();

			cal.set(Calendar.DATE, 1);

			for (int i = 0; i < 7; i++) {
				cal.set(Calendar.DAY_OF_WEEK, i + 1);

				days[i] = dayFormat.format(cal.getTime());
			}

			_calendarPool.put(key, days);
		}

		return days;
	}

	public static int getDaysInMonth(Calendar cal) {
		return getDaysInMonth(cal.get(Calendar.MONTH), cal.get(Calendar.YEAR));
	}

	public static int getDaysInMonth(int month, int year) {
		month++;

		if ((month == 1) || (month == 3) || (month == 5) || (month == 7) ||
			(month == 8) || (month == 10) || (month == 12)) {

			return 31;
		}

		if ((month == 4) || (month == 6) || (month == 9) || (month == 11)) {
			return 30;
		}

		if ((((year % 4) == 0) && ((year % 100) != 0)) || ((year % 400) == 0)) {
			return 29;
		}

		return 28;
	}

	public static Date getGTDate(Calendar cal) {
		Calendar gtCal = (Calendar)cal.clone();

		gtCal.set(Calendar.HOUR_OF_DAY, 0);
		gtCal.set(Calendar.MINUTE, 0);
		gtCal.set(Calendar.SECOND, 0);
		gtCal.set(Calendar.MILLISECOND, 0);

		return gtCal.getTime();
	}

	public static int getGregorianDay(Calendar cal) {
		int year = cal.get(Calendar.YEAR) - 1600;

		int month = cal.get(Calendar.MONTH) + 1;

		if (month < 3) {
			month += 12;
		}

		int day = cal.get(Calendar.DATE);

		return (int)
			(6286 + (year * 365.25) - (year / 100) + (year / 400) +
				(30.6 * month) + 0.2 + day);
	}

	public static Date getLTDate(Calendar cal) {
		Calendar ltCal = (Calendar)cal.clone();

		ltCal.set(Calendar.HOUR_OF_DAY, 23);
		ltCal.set(Calendar.MINUTE, 59);
		ltCal.set(Calendar.SECOND, 59);
		ltCal.set(Calendar.MILLISECOND, 990);

		return ltCal.getTime();
	}

	public static int getLastDayOfWeek(Calendar cal) {
		int firstDayOfWeek = cal.getFirstDayOfWeek();

		if (firstDayOfWeek == Calendar.SUNDAY) {
			return Calendar.SATURDAY;
		}
		else if (firstDayOfWeek == Calendar.MONDAY) {
			return Calendar.SUNDAY;
		}
		else if (firstDayOfWeek == Calendar.TUESDAY) {
			return Calendar.MONDAY;
		}
		else if (firstDayOfWeek == Calendar.WEDNESDAY) {
			return Calendar.TUESDAY;
		}
		else if (firstDayOfWeek == Calendar.THURSDAY) {
			return Calendar.WEDNESDAY;
		}
		else if (firstDayOfWeek == Calendar.FRIDAY) {
			return Calendar.THURSDAY;
		}

		return Calendar.FRIDAY;
	}

	public static int[] getMonthIds() {
		return MONTH_IDS;
	}

	public static String[] getMonths(Locale locale) {
		return getMonths(locale, null);
	}

	public static String[] getMonths(Locale locale, String pattern) {
		if (Validator.isNull(pattern)) {
			pattern = "MMMM";
		}

		String key = StringBundler.concat(
			"months_", pattern, "_", locale.getLanguage(), "_",
			locale.getCountry());

		String[] months = _calendarPool.get(key);

		if (months == null) {
			months = new String[12];

			Format monthFormat = FastDateFormatFactoryUtil.getSimpleDateFormat(
				pattern, locale);

			Calendar cal = CalendarFactoryUtil.getCalendar();

			cal.set(Calendar.DATE, 1);

			for (int i = 0; i < 12; i++) {
				cal.set(Calendar.MONTH, i);

				months[i] = monthFormat.format(cal.getTime());
			}

			_calendarPool.put(key, months);
		}

		return months;
	}

	public static Timestamp getTimestamp(Date date) {
		if (date == null) {
			return null;
		}

		return new Timestamp(date.getTime());
	}

	public static boolean isAfter(
		int month1, int day1, int year1, int hour1, int minute1, int amPm1,
		int month2, int day2, int year2, int hour2, int minute2, int amPm2,
		TimeZone timeZone, Locale locale) {

		Calendar cal1 = CalendarFactoryUtil.getCalendar(timeZone, locale);

		cal1.set(Calendar.MONTH, month1);
		cal1.set(Calendar.DATE, day1);
		cal1.set(Calendar.YEAR, year1);
		cal1.set(Calendar.HOUR, hour1);
		cal1.set(Calendar.MINUTE, minute1);
		cal1.set(Calendar.AM_PM, amPm1);

		Calendar cal2 = CalendarFactoryUtil.getCalendar(timeZone, locale);

		cal2.set(Calendar.MONTH, month2);
		cal2.set(Calendar.DATE, day2);
		cal2.set(Calendar.YEAR, year2);
		cal2.set(Calendar.HOUR, hour2);
		cal2.set(Calendar.MINUTE, minute2);
		cal2.set(Calendar.AM_PM, amPm2);

		return cal1.after(cal2);
	}

	public static boolean isBroadcastDate(int month, int day, int year) {
		if (!isDate(month, day, year)) {
			return false;
		}

		Calendar cal1 = CalendarFactoryUtil.getCalendar();

		cal1.setFirstDayOfWeek(Calendar.MONDAY);
		cal1.set(Calendar.MONTH, month);
		cal1.set(Calendar.DATE, day);
		cal1.set(Calendar.YEAR, year);

		Calendar cal2 = CalendarFactoryUtil.getCalendar();

		cal2.setFirstDayOfWeek(Calendar.MONDAY);
		cal2.set(Calendar.MONTH, month + 1);
		cal2.set(Calendar.DATE, 1);
		cal2.set(Calendar.YEAR, year);

		if ((cal2.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) &&
			(cal2.get(Calendar.WEEK_OF_YEAR) == cal1.get(
				Calendar.WEEK_OF_YEAR))) {

			return false;
		}

		return true;
	}

	public static boolean isDate(int month, int day, int year) {
		return Validator.isDate(month, day, year);
	}

	public static boolean isFuture(int month, int year) {
		return isFuture(
			month, year, TimeZoneUtil.getDefault(), LocaleUtil.getDefault());
	}

	public static boolean isFuture(int month, int day, int year) {
		return isFuture(
			month, day, year, TimeZoneUtil.getDefault(),
			LocaleUtil.getDefault());
	}

	public static boolean isFuture(
		int month, int day, int year, int hour, int minute, int amPm) {

		return isFuture(
			month, day, year, hour, minute, amPm, TimeZoneUtil.getDefault(),
			LocaleUtil.getDefault());
	}

	public static boolean isFuture(
		int month, int day, int year, int hour, int minute, int amPm,
		TimeZone timeZone, Locale locale) {

		Calendar curCal = CalendarFactoryUtil.getCalendar(timeZone, locale);

		Calendar cal = (Calendar)curCal.clone();

		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.DATE, day);
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.HOUR, hour);
		cal.set(Calendar.MINUTE, minute);
		cal.set(Calendar.AM_PM, amPm);

		return cal.after(curCal);
	}

	public static boolean isFuture(
		int month, int day, int year, TimeZone timeZone, Locale locale) {

		Calendar curCal = CalendarFactoryUtil.getCalendar(timeZone, locale);

		Calendar cal = (Calendar)curCal.clone();

		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.DATE, day);
		cal.set(Calendar.YEAR, year);

		return cal.after(curCal);
	}

	public static boolean isFuture(
		int month, int year, TimeZone timeZone, Locale locale) {

		Calendar curCal = CalendarFactoryUtil.getCalendar(timeZone, locale);

		curCal.set(Calendar.DATE, 1);

		Calendar cal = (Calendar)curCal.clone();

		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.YEAR, year);

		return cal.after(curCal);
	}

	public static boolean isGregorianDate(int month, int day, int year) {
		return Validator.isGregorianDate(month, day, year);
	}

	public static boolean isJulianDate(int month, int day, int year) {
		return Validator.isJulianDate(month, day, year);
	}

	public static Calendar roundByMinutes(Calendar cal, int interval) {
		int minutes = cal.get(Calendar.MINUTE);

		int delta = 0;

		if (minutes < interval) {
			delta = interval - minutes;
		}
		else {
			delta = interval - (minutes % interval);
		}

		if (delta == interval) {
			delta = 0;
		}

		cal.add(Calendar.MINUTE, delta);

		return cal;
	}

	private static long _getTimeInMillis(Date date) {
		Calendar cal = CalendarFactoryUtil.getCalendar();

		cal.setTime(date);

		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH);
		int day = cal.get(Calendar.DATE);

		int hour = 0;
		int minute = 0;
		int second = 0;

		cal.set(year, month, day, hour, minute, second);

		return cal.getTimeInMillis() / Time.DAY;
	}

	private static final Map<String, String[]> _calendarPool =
		new ConcurrentHashMap<>();

}