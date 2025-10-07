/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.util;

import com.liferay.petra.string.StringPool;

import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/**
 * @author Brian Wing Shun Chan
 */
public class DateUtil {

	public static final String ISO_8601_PATTERN = "yyyy-MM-dd'T'HH:mm:ssZ";

	public static int compareTo(Date date1, Date date2) {
		if (date1 == date2) {
			return 0;
		}

		if (date1 == null) {
			return 1;
		}

		if (date2 == null) {
			return -1;
		}

		return Long.compare(date1.getTime(), date2.getTime());
	}

	public static boolean equals(Date date1, Date date2) {
		if (compareTo(date1, date2) == 0) {
			return true;
		}

		return false;
	}

	public static String formatDate(
			String fromPattern, String dateString, Locale locale)
		throws ParseException {

		Date dateValue = parseDate(fromPattern, dateString, locale);

		Format dateFormat = FastDateFormatFactoryUtil.getDate(locale);

		return dateFormat.format(dateValue);
	}

	public static String getCurrentDate(String pattern, Locale locale) {
		return getDate(new Date(), pattern, locale);
	}

	public static String getCurrentDate(
		String pattern, Locale locale, TimeZone timeZone) {

		return getDate(new Date(), pattern, locale, timeZone);
	}

	public static String getDate(Date date, String pattern, Locale locale) {
		Format dateFormat = FastDateFormatFactoryUtil.getSimpleDateFormat(
			pattern, locale);

		return dateFormat.format(date);
	}

	public static String getDate(
		Date date, String pattern, Locale locale, TimeZone timeZone) {

		Format dateFormat = FastDateFormatFactoryUtil.getSimpleDateFormat(
			pattern, locale, timeZone);

		return dateFormat.format(date);
	}

	public static int getDaysBetween(Date date1, Date date2) {
		return getDaysBetween(date1, date2, null);
	}

	public static int getDaysBetween(
		Date date1, Date date2, TimeZone timeZone) {

		int daysBetween = 0;

		if (date1.after(date2)) {
			Date tempDate = date1;

			date1 = date2;
			date2 = tempDate;
		}

		Calendar startCal = null;
		Calendar endCal = null;

		int offsetDate1 = 0;
		int offsetDate2 = 0;

		if (timeZone == null) {
			startCal = new GregorianCalendar();
			endCal = new GregorianCalendar();
		}
		else {
			startCal = new GregorianCalendar(timeZone);
			endCal = new GregorianCalendar(timeZone);

			offsetDate1 = timeZone.getOffset(date1.getTime());
			offsetDate2 = timeZone.getOffset(date2.getTime());
		}

		startCal.setTime(date1);

		startCal.add(Calendar.MILLISECOND, offsetDate1);

		endCal.setTime(date2);

		endCal.add(Calendar.MILLISECOND, offsetDate2);

		while (CalendarUtil.beforeByDay(startCal.getTime(), endCal.getTime())) {
			startCal.add(Calendar.DAY_OF_MONTH, 1);

			daysBetween++;
		}

		return daysBetween;
	}

	public static DateFormat getISO8601Format() {
		return DateFormatFactoryUtil.getSimpleDateFormat(ISO_8601_PATTERN);
	}

	public static DateFormat getISOFormat() {
		return getISOFormat(StringPool.BLANK);
	}

	public static DateFormat getISOFormat(String text) {
		String pattern = StringPool.BLANK;

		if (text.length() == 8) {
			pattern = "yyyyMMdd";
		}
		else if (text.length() == 12) {
			pattern = "yyyyMMddHHmm";
		}
		else if (text.length() == 13) {
			pattern = "yyyyMMdd'T'HHmm";
		}
		else if (text.length() == 14) {
			pattern = "yyyyMMddHHmmss";
		}
		else if (text.length() == 15) {
			pattern = "yyyyMMdd'T'HHmmss";
		}
		else if ((text.length() > 8) && (text.charAt(8) == 'T')) {
			pattern = "yyyyMMdd'T'HHmmssz";
		}
		else {
			pattern = "yyyyMMddHHmmssz";
		}

		return DateFormatFactoryUtil.getSimpleDateFormat(pattern);
	}

	public static Date getTomorrowDate() {
		Calendar calendar = Calendar.getInstance();

		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);

		calendar.add(Calendar.DATE, 1);

		return calendar.getTime();
	}

	public static DateFormat getUTCFormat() {
		return getUTCFormat(StringPool.BLANK);
	}

	public static DateFormat getUTCFormat(String text) {
		String pattern = StringPool.BLANK;

		if (text.length() == 8) {
			pattern = "yyyyMMdd";
		}
		else if (text.length() == 12) {
			pattern = "yyyyMMddHHmm";
		}
		else if (text.length() == 13) {
			pattern = "yyyyMMdd'T'HHmm";
		}
		else if (text.length() == 14) {
			pattern = "yyyyMMddHHmmss";
		}
		else if (text.length() == 15) {
			pattern = "yyyyMMdd'T'HHmmss";
		}
		else {
			pattern = "yyyyMMdd'T'HHmmssz";
		}

		return DateFormatFactoryUtil.getSimpleDateFormat(
			pattern, TimeZoneUtil.getTimeZone(StringPool.UTC));
	}

	public static int getYear(Date date) {
		Calendar cal = Calendar.getInstance();

		cal.setTime(date);

		return cal.get(Calendar.YEAR);
	}

	public static boolean isFormatAmPm(Locale locale) {
		Boolean formatAmPm = _formatAmPmMap.get(locale);

		if (formatAmPm == null) {
			SimpleDateFormat simpleDateFormat =
				(SimpleDateFormat)DateFormat.getTimeInstance(
					DateFormat.SHORT, locale);

			String pattern = simpleDateFormat.toPattern();

			formatAmPm = pattern.contains("a");

			_formatAmPmMap.put(locale, formatAmPm);
		}

		return formatAmPm;
	}

	public static Date newDate() {
		return new Date();
	}

	public static Date newDate(long date) {
		return new Date(date);
	}

	public static long newTime() {
		Date date = new Date();

		return date.getTime();
	}

	public static Date parseDate(String dateString, Locale locale)
		throws ParseException {

		return parseDate(null, dateString, locale);
	}

	public static Date parseDate(
			String pattern, String dateString, Locale locale)
		throws ParseException {

		DateFormat dateFormat = null;

		if (Validator.isNull(pattern)) {
			dateFormat = DateFormat.getDateInstance(DateFormat.SHORT, locale);
		}
		else {
			dateFormat = DateFormatFactoryUtil.getSimpleDateFormat(
				pattern, locale);
		}

		return dateFormat.parse(dateString);
	}

	private static final Map<Locale, Boolean> _formatAmPmMap = new HashMap<>();

}