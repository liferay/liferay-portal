/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.calendar.util;

import com.google.ical.iter.RecurrenceIterator;
import com.google.ical.iter.RecurrenceIteratorFactory;
import com.google.ical.util.TimeUtils;
import com.google.ical.values.DateValue;
import com.google.ical.values.DateValueImpl;

import com.liferay.calendar.model.CalendarBooking;
import com.liferay.calendar.recurrence.Frequency;
import com.liferay.calendar.recurrence.PositionalWeekday;
import com.liferay.calendar.recurrence.Recurrence;
import com.liferay.calendar.recurrence.Weekday;
import com.liferay.calendar.util.comparator.CalendarBookingStartTimeComparator;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.text.ParseException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

/**
 * @author Marcellus Tavares
 */
public class RecurrenceUtil {

	public static List<CalendarBooking> expandCalendarBooking(
		CalendarBooking calendarBooking, long startTime, long endTime,
		int maxSize) {

		return expandCalendarBooking(
			calendarBooking, startTime, endTime, calendarBooking.getTimeZone(),
			maxSize);
	}

	public static List<CalendarBooking> expandCalendarBooking(
		CalendarBooking calendarBooking, long startTime, long endTime,
		TimeZone displayTimeZone, int maxSize) {

		List<CalendarBooking> expandedCalendarBookings = new ArrayList<>();

		try {
			CalendarBookingIterator calendarBookingIterator =
				new CalendarBookingIterator(calendarBooking, displayTimeZone);

			while (calendarBookingIterator.hasNext()) {
				CalendarBooking newCalendarBooking =
					calendarBookingIterator.next();

				if (newCalendarBooking.getEndTime() < startTime) {
					continue;
				}

				if (newCalendarBooking.getStartTime() > endTime) {
					break;
				}

				expandedCalendarBookings.add(newCalendarBooking);

				if ((maxSize > 0) &&
					(expandedCalendarBookings.size() >= maxSize)) {

					break;
				}
			}
		}
		catch (ParseException parseException) {
			_log.error("Unable to parse data ", parseException);
		}

		return expandedCalendarBookings;
	}

	public static List<CalendarBooking> expandCalendarBookings(
		List<CalendarBooking> calendarBookings, long startTime, long endTime) {

		return expandCalendarBookings(calendarBookings, startTime, endTime, 0);
	}

	public static List<CalendarBooking> expandCalendarBookings(
		List<CalendarBooking> calendarBookings, long startTime, long endTime,
		int maxSize) {

		List<CalendarBooking> expandedCalendarBookings = new ArrayList<>();

		for (CalendarBooking calendarBooking : calendarBookings) {
			List<CalendarBooking> expandedCalendarBooking =
				expandCalendarBooking(
					calendarBooking, startTime, endTime, maxSize);

			expandedCalendarBookings.addAll(expandedCalendarBooking);
		}

		return expandedCalendarBookings;
	}

	public static List<CalendarBooking> expandCalendarBookings(
		List<CalendarBooking> calendarBookings, long startTime, long endTime,
		TimeZone displayTimeZone) {

		return expandCalendarBookings(
			calendarBookings, startTime, endTime, displayTimeZone, 0);
	}

	public static List<CalendarBooking> expandCalendarBookings(
		List<CalendarBooking> calendarBookings, long startTime, long endTime,
		TimeZone displayTimeZone, int maxSize) {

		List<CalendarBooking> expandedCalendarBookings = new ArrayList<>();

		for (CalendarBooking calendarBooking : calendarBookings) {
			List<CalendarBooking> expandedCalendarBooking =
				expandCalendarBooking(
					calendarBooking, startTime, endTime, displayTimeZone,
					maxSize);

			expandedCalendarBookings.addAll(expandedCalendarBooking);
		}

		return expandedCalendarBookings;
	}

	public static CalendarBooking getCalendarBookingInstance(
		CalendarBooking calendarBooking, int instanceIndex) {

		try {
			CalendarBookingIterator calendarBookingIterator =
				new CalendarBookingIterator(calendarBooking);

			while (calendarBookingIterator.hasNext()) {
				CalendarBooking calendarBookingInstance =
					calendarBookingIterator.next();

				if (calendarBookingInstance.getInstanceIndex() ==
						instanceIndex) {

					return calendarBookingInstance;
				}
			}
		}
		catch (ParseException parseException) {
			_log.error("Unable to parse data ", parseException);
		}

		return null;
	}

	public static int getIndexOfInstance(
		String recurrence, long recurrenceStartTime, long instanceStartTime) {

		int count = 0;

		DateValue instanceDateValue = _toDateValue(instanceStartTime);

		try {
			RecurrenceIterator recurrenceIterator =
				RecurrenceIteratorFactory.createRecurrenceIterator(
					recurrence, _toDateValue(recurrenceStartTime),
					TimeUtils.utcTimezone());

			while (recurrenceIterator.hasNext()) {
				DateValue dateValue = recurrenceIterator.next();

				if (dateValue.compareTo(instanceDateValue) >= 0) {
					break;
				}

				count++;
			}
		}
		catch (ParseException parseException) {
			_log.error("Unable to parse data ", parseException);
		}

		return count;
	}

	public static CalendarBooking getLastInstanceCalendarBooking(
		List<CalendarBooking> calendarBookings) {

		calendarBookings = ListUtil.sort(
			calendarBookings,
			CalendarBookingStartTimeComparator.getInstance(false));

		CalendarBooking lastCalendarBooking = calendarBookings.get(0);

		long lastStartTime = 0;

		for (CalendarBooking calendarBooking : calendarBookings) {
			Recurrence recurrence = calendarBooking.getRecurrenceObj();

			if (recurrence == null) {
				continue;
			}

			if (!hasLimit(recurrence)) {
				lastCalendarBooking = calendarBooking;

				break;
			}

			CalendarBooking lastCalendarBookingInstance =
				getLastCalendarBookingInstance(calendarBooking);

			if (lastCalendarBookingInstance.getStartTime() <= lastStartTime) {
				continue;
			}

			lastStartTime = lastCalendarBookingInstance.getStartTime();

			lastCalendarBooking = calendarBooking;
		}

		return lastCalendarBooking;
	}

	public static String getSummary(
		CalendarBooking calendarBooking, Recurrence recurrence) {

		if (recurrence == null) {
			return LanguageUtil.get(
				LocaleUtil.getMostRelevantLocale(), "false");
		}

		List<Object> arguments = new ArrayList<>();

		StringBundler sb = new StringBundler(4);

		Frequency frequency = recurrence.getFrequency();

		if (recurrence.getInterval() == 1) {
			sb.append(StringUtil.toLowerCase(frequency.toString()));
		}
		else {
			sb.append("every-x-");
			sb.append(_intervalUnits.get(frequency));

			arguments.add(recurrence.getInterval());
		}

		PositionalWeekday positionalWeekday = recurrence.getPositionalWeekday();

		List<Weekday> weekdays = recurrence.getWeekdays();

		if ((positionalWeekday != null) && (frequency != Frequency.WEEKLY)) {
			Weekday weekday = positionalWeekday.getWeekday();

			if (frequency == Frequency.MONTHLY) {
				sb.append("-on-x-x");

				arguments.add(
					_positionLabels.get(positionalWeekday.getPosition()));
				arguments.add(_weekdayLabels.get(weekday.toString()));
			}
			else {
				Date startDate = new Date(calendarBooking.getStartTime());

				sb.append("-on-x-x-of-x");

				arguments.add(
					_positionLabels.get(positionalWeekday.getPosition()));
				arguments.add(_weekdayLabels.get(weekday.toString()));
				arguments.add(_monthLabels.get(startDate.getMonth()));
			}
		}
		else if ((frequency == Frequency.WEEKLY) && !weekdays.isEmpty()) {
			sb.append("-on-x");

			arguments.add(
				ListUtil.toString(
					TransformUtil.transform(
						weekdays,
						weekday -> _weekdayLabels.get(weekday.toString())),
					StringPool.BLANK));
		}

		Calendar untilJCalendar = recurrence.getUntilJCalendar();

		if (recurrence.getCount() > 0) {
			sb.append("-x-times");

			arguments.add(recurrence.getCount());
		}
		else if (untilJCalendar != null) {
			sb.append("-until-x-x-x");

			arguments.add(_monthLabels.get(untilJCalendar.get(Calendar.MONTH)));
			arguments.add(untilJCalendar.get(Calendar.DATE));
			arguments.add(untilJCalendar.get(Calendar.YEAR));
		}

		return LanguageUtil.format(
			LocaleUtil.getMostRelevantLocale(), sb.toString(),
			arguments.toArray(new Object[0]));
	}

	public static Recurrence inTimeZone(
		Recurrence recurrence, Calendar startTimeJCalendar, TimeZone timeZone) {

		if (recurrence == null) {
			return null;
		}

		recurrence = recurrence.clone();

		TimeZone originalTimeZone = recurrence.getTimeZone();

		List<Calendar> exceptionJCalendars =
			recurrence.getExceptionJCalendars();

		Calendar recurrenceStartTimeJCalendar = JCalendarUtil.getJCalendar(
			startTimeJCalendar, originalTimeZone);

		recurrence.setExceptionJCalendars(
			TransformUtil.transform(
				exceptionJCalendars,
				exceptionJCalendar -> {
					exceptionJCalendar = JCalendarUtil.mergeJCalendar(
						exceptionJCalendar, recurrenceStartTimeJCalendar,
						originalTimeZone);

					return JCalendarUtil.getJCalendar(
						exceptionJCalendar, timeZone);
				}));

		recurrence.setPositionalWeekdays(
			TransformUtil.transform(
				recurrence.getPositionalWeekdays(),
				positionalWeekday -> {
					Calendar jCalendar = JCalendarUtil.getJCalendar(
						startTimeJCalendar, originalTimeZone);

					Weekday weekday = positionalWeekday.getWeekday();

					jCalendar.set(
						Calendar.DAY_OF_WEEK, weekday.getCalendarWeekday());

					return new PositionalWeekday(
						Weekday.getWeekday(
							JCalendarUtil.getJCalendar(jCalendar, timeZone)),
						positionalWeekday.getPosition());
				}));
		recurrence.setTimeZone(timeZone);

		Calendar untilJCalendar = recurrence.getUntilJCalendar();

		if (untilJCalendar != null) {
			untilJCalendar = JCalendarUtil.mergeJCalendar(
				untilJCalendar, recurrenceStartTimeJCalendar, originalTimeZone);

			untilJCalendar = JCalendarUtil.getJCalendar(
				untilJCalendar, timeZone);

			recurrence.setUntilJCalendar(untilJCalendar);
		}

		return recurrence;
	}

	protected static CalendarBooking getLastCalendarBookingInstance(
		CalendarBooking calendarBooking) {

		List<CalendarBooking> calendarBookingInstances = expandCalendarBooking(
			calendarBooking, calendarBooking.getStartTime(), Long.MAX_VALUE, 0);

		if (!calendarBookingInstances.isEmpty()) {
			return calendarBookingInstances.get(
				calendarBookingInstances.size() - 1);
		}

		return calendarBooking;
	}

	protected static boolean hasLimit(Recurrence recurrence) {
		if ((recurrence.getUntilJCalendar() != null) ||
			(recurrence.getCount() != 0)) {

			return true;
		}

		return false;
	}

	private static DateValue _toDateValue(long time) {
		Calendar jCalendar = JCalendarUtil.getJCalendar(time);

		return new DateValueImpl(
			jCalendar.get(Calendar.YEAR), jCalendar.get(Calendar.MONTH) + 1,
			jCalendar.get(Calendar.DAY_OF_MONTH));
	}

	private static final Log _log = LogFactoryUtil.getLog(RecurrenceUtil.class);

	private static final Map<Frequency, String> _intervalUnits =
		HashMapBuilder.put(
			Frequency.DAILY, "days"
		).put(
			Frequency.MONTHLY, "months"
		).put(
			Frequency.WEEKLY, "weeks"
		).put(
			Frequency.YEARLY, "years"
		).build();
	private static final Map<Integer, String> _monthLabels = HashMapBuilder.put(
		0, "january"
	).put(
		1, "february"
	).put(
		2, "march"
	).put(
		3, "april"
	).put(
		4, "may"
	).put(
		5, "june"
	).put(
		6, "july"
	).put(
		7, "august"
	).put(
		8, "september"
	).put(
		9, "october"
	).put(
		10, "november"
	).put(
		11, "december"
	).build();
	private static final Map<Integer, String> _positionLabels =
		HashMapBuilder.put(
			-1, "position.last"
		).put(
			1, "position.first"
		).put(
			2, "position.second"
		).put(
			3, "position.third"
		).put(
			4, "position.fourth"
		).build();
	private static final Map<String, String> _weekdayLabels =
		HashMapBuilder.put(
			"FR", "weekday.FR"
		).put(
			"MO", "weekday.MO"
		).put(
			"SA", "weekday.SA"
		).put(
			"SU", "weekday.SU"
		).put(
			"TH", "weekday.TH"
		).put(
			"TU", "weekday.TU"
		).put(
			"WE", "weekday.WE"
		).build();

}