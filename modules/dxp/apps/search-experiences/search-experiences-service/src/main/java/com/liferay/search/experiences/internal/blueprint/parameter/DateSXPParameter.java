/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.internal.blueprint.parameter;

import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/**
 * @author Petteri Karttunen
 */
public class DateSXPParameter extends BaseSXPParameter {

	public DateSXPParameter(String name, boolean templateVariable, Date value) {
		super(name, templateVariable);

		_value = value;
	}

	@Override
	public boolean evaluateEquals(String format, Object object) {
		DateFormat dateFormat = new SimpleDateFormat(format);

		return Objects.equals(
			dateFormat.format(_value), GetterUtil.getString(object));
	}

	@Override
	public boolean evaluateRange(
		String format, Object gt, Object gte, Object lt, Object lte) {

		DateFormat dateFormat = new SimpleDateFormat(format);

		Date value = _parse(dateFormat, dateFormat.format(_value));

		Function<Object, Integer> function = object -> value.compareTo(
			_parse(dateFormat, String.valueOf(object)));

		if ((gt != null) && (function.apply(gt) <= 0)) {
			return false;
		}

		if ((gte != null) && (function.apply(gte) < 0)) {
			return false;
		}

		if ((lt != null) && (function.apply(lt) >= 0)) {
			return false;
		}

		if ((lte != null) && (function.apply(lte) > 0)) {
			return false;
		}

		return true;
	}

	@Override
	public String evaluateToString(Map<String, String> options) {
		if ((options == null) || (options.get("date_format") == null)) {
			return _value.toString();
		}

		Date date = _value;

		String modifier = options.get("modifier");

		if (modifier != null) {
			date = _modify(date, modifier);
		}

		String dateFormatString = options.get("date_format");

		if (dateFormatString.equals("timestamp")) {
			return String.valueOf(date.getTime());
		}

		DateFormat dateFormat = new SimpleDateFormat(dateFormatString);

		return dateFormat.format(date);
	}

	@Override
	public Date getValue() {
		return _value;
	}

	private Date _modify(Date date, String option) {
		if (Validator.isNull(option) ||
			!option.matches("^[\\+|\\-][0-9]+[h|d|w|M|y]")) {

			return date;
		}

		char operator = option.charAt(0);

		char unit = option.charAt(option.length() - 1);

		option = option.replaceAll("\\D+", "");

		long amount = GetterUtil.getLong(option);

		if (operator == '-') {
			amount *= -1;
		}

		Instant instant = date.toInstant();

		ZonedDateTime zonedDateTime = instant.atZone(ZoneId.systemDefault());

		LocalDateTime localDateTime = zonedDateTime.toLocalDateTime();

		if (unit == 'h') {
			localDateTime = localDateTime.plusHours(amount);
		}
		else if (unit == 'd') {
			localDateTime = localDateTime.plusDays(amount);
		}
		else if (unit == 'w') {
			localDateTime = localDateTime.plusWeeks(amount);
		}
		else if (unit == 'M') {
			localDateTime = localDateTime.plusMonths(amount);
		}
		else if (unit == 'y') {
			localDateTime = localDateTime.plusYears(amount);
		}

		zonedDateTime = localDateTime.atZone(ZoneId.systemDefault());

		return Date.from(zonedDateTime.toInstant());
	}

	private Date _parse(DateFormat dateFormat, String string) {
		try {
			return dateFormat.parse(string);
		}
		catch (ParseException parseException) {
			return ReflectionUtil.throwException(parseException);
		}
	}

	private final Date _value;

}