/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.seo.studio.web.internal.util;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.Validator;

import java.time.DateTimeException;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAdjusters;

import java.util.Date;

/**
 * @author Jonathan McCann
 */
public class SEOStudioScanScheduleUtil {

	public static Date getNextScanDate(
		Instant instant, Integer scanDayOfMonth, String scanDayOfWeek,
		String scanFrequency, String scanTime, String scanTimeZone) {

		if (Validator.isNull(scanFrequency) || Validator.isNull(scanTime)) {
			return null;
		}

		LocalTime localTime = _getLocalTime(scanTime);

		if (localTime == null) {
			return null;
		}

		ZoneId zoneId = _getZoneId(scanTimeZone);

		ZonedDateTime currentZonedDateTime = instant.atZone(zoneId);

		ZonedDateTime nextZonedDateTime = null;

		if (scanFrequency.equals("daily")) {
			LocalDate localDate = currentZonedDateTime.toLocalDate();

			nextZonedDateTime = _getZonedDateTime(localDate, localTime, zoneId);

			if (!nextZonedDateTime.isAfter(currentZonedDateTime)) {
				nextZonedDateTime = _getZonedDateTime(
					localDate.plusDays(1), localTime, zoneId);
			}
		}
		else if (scanFrequency.equals("monthly")) {
			if ((scanDayOfMonth == null) || (scanDayOfMonth < 1)) {
				return null;
			}

			YearMonth yearMonth = YearMonth.from(currentZonedDateTime);

			nextZonedDateTime = _getZonedDateTime(
				localTime, scanDayOfMonth, yearMonth, zoneId);

			if (!nextZonedDateTime.isAfter(currentZonedDateTime)) {
				nextZonedDateTime = _getZonedDateTime(
					localTime, scanDayOfMonth, yearMonth.plusMonths(1), zoneId);
			}
		}
		else if (scanFrequency.equals("weekly")) {
			DayOfWeek dayOfWeek = _getDayOfWeek(scanDayOfWeek);

			if (dayOfWeek == null) {
				return null;
			}

			LocalDate localDate = currentZonedDateTime.toLocalDate();

			localDate = localDate.with(TemporalAdjusters.nextOrSame(dayOfWeek));

			nextZonedDateTime = _getZonedDateTime(localDate, localTime, zoneId);

			if (!nextZonedDateTime.isAfter(currentZonedDateTime)) {
				nextZonedDateTime = _getZonedDateTime(
					localDate.plusWeeks(1), localTime, zoneId);
			}
		}
		else {
			return null;
		}

		return Date.from(nextZonedDateTime.toInstant());
	}

	private static DayOfWeek _getDayOfWeek(String scanDayOfWeek) {
		if (Validator.isNull(scanDayOfWeek)) {
			return null;
		}

		if (scanDayOfWeek.equals("SU")) {
			return DayOfWeek.SUNDAY;
		}
		else if (scanDayOfWeek.equals("MO")) {
			return DayOfWeek.MONDAY;
		}
		else if (scanDayOfWeek.equals("TU")) {
			return DayOfWeek.TUESDAY;
		}
		else if (scanDayOfWeek.equals("WE")) {
			return DayOfWeek.WEDNESDAY;
		}
		else if (scanDayOfWeek.equals("TH")) {
			return DayOfWeek.THURSDAY;
		}
		else if (scanDayOfWeek.equals("FR")) {
			return DayOfWeek.FRIDAY;
		}
		else if (scanDayOfWeek.equals("SA")) {
			return DayOfWeek.SATURDAY;
		}

		return null;
	}

	private static LocalTime _getLocalTime(String scanTime) {
		try {
			return LocalTime.parse(
				scanTime, DateTimeFormatter.ofPattern("HH:mm"));
		}
		catch (DateTimeParseException dateTimeParseException) {
			if (_log.isDebugEnabled()) {
				_log.debug(dateTimeParseException);
			}

			return null;
		}
	}

	private static ZonedDateTime _getZonedDateTime(
		LocalDate localDate, LocalTime localTime, ZoneId zoneId) {

		ZonedDateTime zonedDateTime = ZonedDateTime.of(
			localDate, localTime, zoneId);

		return zonedDateTime.withLaterOffsetAtOverlap();
	}

	private static ZonedDateTime _getZonedDateTime(
		LocalTime localTime, int scanDayOfMonth, YearMonth yearMonth,
		ZoneId zoneId) {

		int dayOfMonth = Math.min(scanDayOfMonth, yearMonth.lengthOfMonth());

		return _getZonedDateTime(
			yearMonth.atDay(dayOfMonth), localTime, zoneId);
	}

	private static ZoneId _getZoneId(String scanTimeZone) {
		if (Validator.isNull(scanTimeZone)) {
			return ZoneId.of("UTC");
		}

		try {
			return ZoneId.of(scanTimeZone);
		}
		catch (DateTimeException dateTimeException) {
			if (_log.isDebugEnabled()) {
				_log.debug(dateTimeException);
			}

			return ZoneId.of("UTC");
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SEOStudioScanScheduleUtil.class);

}