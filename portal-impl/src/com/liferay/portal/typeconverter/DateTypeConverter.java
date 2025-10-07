/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.typeconverter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.TemporalAccessor;

import java.util.Calendar;
import java.util.Date;

import jodd.time.TimeUtil;

import jodd.typeconverter.TypeConversionException;
import jodd.typeconverter.TypeConverter;

import jodd.util.StringUtil;

/**
 * @author Eric Yan
 */
public class DateTypeConverter implements TypeConverter<Date> {

	@Override
	public Date convert(Object object) {
		if (object == null) {
			return null;
		}

		if (object instanceof Calendar) {
			Calendar calendar = (Calendar)object;

			return new Date(calendar.getTimeInMillis());
		}

		if (object instanceof Date) {
			return (Date)object;
		}

		if (object instanceof LocalDate) {
			return TimeUtil.toDate((LocalDate)object);
		}

		if (object instanceof LocalDateTime) {
			return TimeUtil.toDate((LocalDateTime)object);
		}

		if (object instanceof Number) {
			Number number = (Number)object;

			return new Date(number.longValue());
		}

		String stringValue = object.toString();

		stringValue = stringValue.trim();

		if (!StringUtil.containsOnlyDigits(stringValue)) {
			TemporalAccessor temporalAccessor = _dateTimeFormatter.parseBest(
				stringValue, ZonedDateTime::from, LocalDateTime::from,
				LocalDate::from);

			if (temporalAccessor instanceof LocalDate) {
				return TimeUtil.toDate((LocalDate)temporalAccessor);
			}

			if (temporalAccessor instanceof LocalDateTime) {
				return TimeUtil.toDate((LocalDateTime)temporalAccessor);
			}

			if (temporalAccessor instanceof ZonedDateTime) {
				ZonedDateTime zonedDateTime = (ZonedDateTime)temporalAccessor;

				return Date.from(zonedDateTime.toInstant());
			}

			throw new TypeConversionException(object);
		}

		try {
			return new Date(Long.parseLong(stringValue));
		}
		catch (NumberFormatException numberFormatException) {
			throw new TypeConversionException(object, numberFormatException);
		}
	}

	// May 1 is "5-1" with "M-d" while "05-01" with "MM-dd". See
	// java.time.format.DateTimeFormatterBuilder#appendPattern(String).

	private static final DateTimeFormatter _dateTimeFormatter =
		new DateTimeFormatterBuilder(
		).parseCaseInsensitive(
		).appendPattern(
			"yyyy-[MM][M]-[dd][d]"
		).optionalStart(
		).optionalStart(
		).appendLiteral(
			' '
		).optionalEnd(
		).optionalStart(
		).appendLiteral(
			'T'
		).optionalEnd(
		).appendOptional(
			DateTimeFormatter.ISO_TIME
		).toFormatter();

}