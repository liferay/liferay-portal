/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.scheduler;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.cal.DayAndPosition;
import com.liferay.portal.kernel.cal.Duration;
import com.liferay.portal.kernel.cal.Recurrence;
import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;

import jakarta.portlet.PortletRequest;

import java.text.Format;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * @author Tina Tian
 */
public class CronTextUtil {

	public static String getCronText(
		PortletRequest portletRequest, Calendar calendar,
		boolean timeZoneSensitive, int recurrenceType) {

		Calendar recurrenceCalendar = null;

		if (timeZoneSensitive) {
			recurrenceCalendar = CalendarFactoryUtil.getCalendar();

			recurrenceCalendar.setTime(calendar.getTime());
		}
		else {
			recurrenceCalendar = (Calendar)calendar.clone();
		}

		Recurrence recurrence = new Recurrence(
			recurrenceCalendar, new Duration(1, 0, 0, 0), recurrenceType);

		recurrence.setWeekStart(Calendar.SUNDAY);

		if (recurrenceType == Recurrence.DAILY) {
			int dailyType = ParamUtil.getInteger(portletRequest, "dailyType");

			if (dailyType == 0) {
				recurrence.setInterval(
					ParamUtil.getInteger(portletRequest, "dailyInterval", 1));
			}
			else {
				recurrence.setByDay(
					new DayAndPosition[] {
						new DayAndPosition(Calendar.MONDAY, 0),
						new DayAndPosition(Calendar.TUESDAY, 0),
						new DayAndPosition(Calendar.WEDNESDAY, 0),
						new DayAndPosition(Calendar.THURSDAY, 0),
						new DayAndPosition(Calendar.FRIDAY, 0)
					});
			}
		}
		else if (recurrenceType == Recurrence.WEEKLY) {
			recurrence.setInterval(
				ParamUtil.getInteger(portletRequest, "weeklyInterval"));

			List<DayAndPosition> dayPos = new ArrayList<>();

			_addWeeklyDayPos(portletRequest, dayPos, Calendar.SUNDAY);
			_addWeeklyDayPos(portletRequest, dayPos, Calendar.MONDAY);
			_addWeeklyDayPos(portletRequest, dayPos, Calendar.TUESDAY);
			_addWeeklyDayPos(portletRequest, dayPos, Calendar.WEDNESDAY);
			_addWeeklyDayPos(portletRequest, dayPos, Calendar.THURSDAY);
			_addWeeklyDayPos(portletRequest, dayPos, Calendar.FRIDAY);
			_addWeeklyDayPos(portletRequest, dayPos, Calendar.SATURDAY);

			if (dayPos.isEmpty()) {
				dayPos.add(new DayAndPosition(Calendar.MONDAY, 0));
			}

			recurrence.setByDay(dayPos.toArray(new DayAndPosition[0]));
		}
		else if (recurrenceType == Recurrence.MONTHLY) {
			int monthlyType = ParamUtil.getInteger(
				portletRequest, "monthlyType");

			if (monthlyType == 0) {
				recurrence.setByMonthDay(
					new int[] {
						ParamUtil.getInteger(portletRequest, "monthlyDay0", 1)
					});
				recurrence.setInterval(
					ParamUtil.getInteger(
						portletRequest, "monthlyInterval0", 1));
			}
			else {
				recurrence.setByDay(
					new DayAndPosition[] {
						new DayAndPosition(
							ParamUtil.getInteger(portletRequest, "monthlyDay1"),
							ParamUtil.getInteger(portletRequest, "monthlyPos"))
					});
				recurrence.setInterval(
					ParamUtil.getInteger(
						portletRequest, "monthlyInterval1", 1));
			}
		}
		else if (recurrenceType == Recurrence.YEARLY) {
			int yearlyType = ParamUtil.getInteger(portletRequest, "yearlyType");

			if (yearlyType == 0) {
				recurrence.setByMonth(
					new int[] {
						ParamUtil.getInteger(portletRequest, "yearlyMonth0")
					});
				recurrence.setByMonthDay(
					new int[] {
						ParamUtil.getInteger(portletRequest, "yearlyDay0", 1)
					});
				recurrence.setInterval(
					ParamUtil.getInteger(portletRequest, "yearlyInterval0", 1));
			}
			else {
				recurrence.setByDay(
					new DayAndPosition[] {
						new DayAndPosition(
							ParamUtil.getInteger(portletRequest, "yearlyDay1"),
							ParamUtil.getInteger(portletRequest, "yearlyPos"))
					});
				recurrence.setByMonth(
					new int[] {
						ParamUtil.getInteger(portletRequest, "yearlyMonth1")
					});
				recurrence.setInterval(
					ParamUtil.getInteger(portletRequest, "yearlyInterval1", 1));
			}
		}

		return _toCronText(recurrence);
	}

	private static void _addWeeklyDayPos(
		PortletRequest portletRequest, List<DayAndPosition> list, int day) {

		if (ParamUtil.getBoolean(portletRequest, "weeklyDayPos" + day)) {
			list.add(new DayAndPosition(day, 0));
		}
	}

	private static String _getDayOfWeek(DayAndPosition dayPos) {
		Calendar calendar = CalendarFactoryUtil.getCalendar();

		calendar.set(Calendar.DAY_OF_WEEK, dayPos.getDayOfWeek());

		Format format = FastDateFormatFactoryUtil.getSimpleDateFormat(
			"EEE", LocaleUtil.US);

		return StringUtil.toUpperCase(format.format(calendar));
	}

	private static String _getIntervalValue(int interval, int period) {
		if ((interval <= 0) && (period == Recurrence.WEEKLY)) {
			return StringPool.BLANK;
		}
		else if (interval <= 0) {
			return StringPool.FORWARD_SLASH + 1;
		}

		return StringPool.FORWARD_SLASH + interval;
	}

	private static String _toCronText(Recurrence recurrence) {
		Calendar dtStart = recurrence.getDtStart();

		int frequency = recurrence.getFrequency();
		int interval = recurrence.getInterval();

		DayAndPosition[] byDay = recurrence.getByDay();
		int[] byMonthDay = recurrence.getByMonthDay();

		String startDateSecond = String.valueOf(dtStart.get(Calendar.SECOND));
		String startDateMinute = String.valueOf(dtStart.get(Calendar.MINUTE));

		int startDateHour = dtStart.get(Calendar.HOUR);

		if (dtStart.get(Calendar.AM_PM) == Calendar.PM) {
			startDateHour += 12;
		}

		String dayOfMonth = String.valueOf(dtStart.get(Calendar.DAY_OF_MONTH));
		String month = String.valueOf(dtStart.get(Calendar.MONTH) + 1);
		String dayOfWeek = String.valueOf(dtStart.get(Calendar.DAY_OF_WEEK));
		String year = String.valueOf(dtStart.get(Calendar.YEAR));

		if (frequency == Recurrence.NO_RECURRENCE) {
			dayOfWeek = StringPool.QUESTION;
		}
		else if (frequency == Recurrence.DAILY) {
			dayOfMonth = 1 + _getIntervalValue(interval, Recurrence.DAILY);
			month = StringPool.STAR;
			dayOfWeek = StringPool.QUESTION;
			year = StringPool.STAR;

			if (byDay != null) {
				dayOfMonth = StringPool.QUESTION;

				dayOfWeek = StringPool.BLANK;

				for (int i = 0; i < byDay.length; i++) {
					if (i > 0) {
						dayOfWeek += StringPool.COMMA;
					}

					dayOfWeek += _getDayOfWeek(byDay[i]);
				}
			}
		}
		else if (frequency == Recurrence.WEEKLY) {
			dayOfMonth = StringPool.QUESTION;
			month = StringPool.STAR;
			year = StringPool.STAR;

			if (byDay != null) {
				dayOfWeek = StringPool.BLANK;

				for (int i = 0; i < byDay.length; i++) {
					if (i > 0) {
						dayOfWeek += StringPool.COMMA;
					}

					dayOfWeek += _getDayOfWeek(byDay[i]);
				}
			}

			dayOfWeek += _getIntervalValue(interval, Recurrence.WEEKLY);
		}
		else if (frequency == Recurrence.MONTHLY) {
			dayOfMonth = StringPool.QUESTION;
			month = 1 + _getIntervalValue(interval, Recurrence.MONTHLY);
			dayOfWeek = StringPool.QUESTION;
			year = StringPool.STAR;

			if ((byMonthDay != null) && (byMonthDay.length == 1)) {
				dayOfMonth = String.valueOf(byMonthDay[0]);
			}
			else if ((byDay != null) && (byDay.length == 1)) {
				String pos = String.valueOf(byDay[0].getDayPosition());

				if (pos.equals("-1")) {
					dayOfWeek = _getDayOfWeek(byDay[0]) + "L";
				}
				else {
					dayOfWeek =
						_getDayOfWeek(byDay[0]) + StringPool.POUND + pos;
				}
			}
		}
		else if (frequency == Recurrence.YEARLY) {
			int[] byMonth = recurrence.getByMonth();

			dayOfMonth = StringPool.QUESTION;
			dayOfWeek = StringPool.QUESTION;
			year += _getIntervalValue(interval, Recurrence.YEARLY);

			if ((byMonth != null) && (byMonth.length == 1)) {
				month = String.valueOf(byMonth[0] + 1);

				if ((byMonthDay != null) && (byMonthDay.length == 1)) {
					dayOfMonth = String.valueOf(byMonthDay[0]);
				}
				else if ((byDay != null) && (byDay.length == 1)) {
					String pos = String.valueOf(byDay[0].getDayPosition());

					if (pos.equals("-1")) {
						dayOfWeek = _getDayOfWeek(byDay[0]) + "L";
					}
					else {
						dayOfWeek =
							_getDayOfWeek(byDay[0]) + StringPool.POUND + pos;
					}
				}
			}
		}

		return StringBundler.concat(
			startDateSecond, StringPool.SPACE, startDateMinute,
			StringPool.SPACE, startDateHour, StringPool.SPACE, dayOfMonth,
			StringPool.SPACE, month, StringPool.SPACE, dayOfWeek,
			StringPool.SPACE, year);
	}

}