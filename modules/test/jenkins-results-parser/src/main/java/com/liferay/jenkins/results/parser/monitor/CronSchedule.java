/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.monitor;

import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;

import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Calum Ragan
 */
public class CronSchedule {

	public CronSchedule(String spec) {
		if (JenkinsResultsParserUtil.isNullOrEmpty(spec)) {
			throw new IllegalArgumentException("Null or empty cron spec");
		}

		_spec = spec.trim();

		String[] fields = _spec.split("\\s+");

		if (fields.length != _FIELD_COUNT) {
			throw new IllegalArgumentException("Invalid cron spec: " + spec);
		}

		_minutes = _parseField(fields[0], true, 59, 0, "minute", 60);
		_hours = _parseField(fields[1], true, 23, 0, "hour", 3600);
		_daysOfMonth = _parseField(fields[2], false, 31, 1, "day of month", 0);
		_months = _parseField(fields[3], false, 12, 1, "month", 0);

		_daysOfWeek = _parseDaysOfWeek(fields[4]);

		if (!fields[2].equals("*") && !fields[4].equals("*")) {
			throw new IllegalArgumentException(
				"Unable to restrict both the day of month and the day of " +
					"week in cron spec: " + spec);
		}
	}

	public long getHashSpanSeconds() {
		return _hashSpanSeconds;
	}

	public long getPeriodSeconds(long currentTimeMillis) {
		long previousFireTimestamp = getPreviousFireTimestamp(
			currentTimeMillis);

		if (previousFireTimestamp <= 0) {
			return -1;
		}

		long earlierFireTimestamp = getPreviousFireTimestamp(
			previousFireTimestamp - _MILLIS_MINUTE);

		if (earlierFireTimestamp <= 0) {
			return -1;
		}

		return (previousFireTimestamp - earlierFireTimestamp) / 1000;
	}

	public long getPreviousFireTimestamp(long currentTimeMillis) {
		Calendar calendar = Calendar.getInstance();

		calendar.setTimeInMillis(currentTimeMillis);

		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.SECOND, 0);

		for (int i = 0; i < _MINUTES_SEARCH_MAXIMUM; i++) {
			if (_isFireTime(calendar)) {
				return calendar.getTimeInMillis();
			}

			calendar.add(Calendar.MINUTE, -1);
		}

		return -1;
	}

	public String getSpec() {
		return _spec;
	}

	private String _getInvalidMessage(String name, String value) {
		return JenkinsResultsParserUtil.combine(
			"Invalid ", name, " in cron spec: ", value);
	}

	private boolean _isFireTime(Calendar calendar) {
		if (!_minutes.contains(calendar.get(Calendar.MINUTE)) ||
			!_hours.contains(calendar.get(Calendar.HOUR_OF_DAY)) ||
			!_months.contains(calendar.get(Calendar.MONTH) + 1) ||
			!_daysOfMonth.contains(calendar.get(Calendar.DAY_OF_MONTH)) ||
			!_daysOfWeek.contains(calendar.get(Calendar.DAY_OF_WEEK) - 1)) {

			return false;
		}

		return true;
	}

	private Set<Integer> _parseDaysOfWeek(String fieldValue) {
		Set<Integer> values = _parseField(
			fieldValue, false, 7, 0, "day of week", 0);

		if (values.remove(7)) {
			values.add(0);
		}

		return values;
	}

	private Set<Integer> _parseField(
		String fieldValue, boolean hashAllowed, int maximum, int minimum,
		String name, int unitSeconds) {

		Set<Integer> values = new HashSet<>();

		for (String item : fieldValue.split(",")) {
			values.addAll(
				_parseItem(
					hashAllowed, item, maximum, minimum, name, unitSeconds));
		}

		return values;
	}

	private Set<Integer> _parseItem(
		boolean hashAllowed, String item, int maximum, int minimum, String name,
		int unitSeconds) {

		if (!hashAllowed && item.contains("H")) {
			throw new IllegalArgumentException(_getInvalidMessage(name, item));
		}

		if (item.equals("H")) {
			_hashSpanSeconds += ((maximum - minimum) + 1) * unitSeconds;

			return Collections.singleton(minimum);
		}

		String rangeValue = item;
		int step = 1;

		int slashIndex = item.indexOf('/');

		if (slashIndex != -1) {
			rangeValue = item.substring(0, slashIndex);

			step = _parseNumber(name, item.substring(slashIndex + 1));

			if (step < 1) {
				throw new IllegalArgumentException(
					_getInvalidMessage(name, item));
			}

			if (rangeValue.equals("H")) {
				_hashSpanSeconds += step * unitSeconds;
			}
		}

		int rangeMaximum = maximum;
		int rangeMinimum = minimum;

		if (!rangeValue.equals("*") && !rangeValue.equals("H")) {
			int dashIndex = rangeValue.indexOf('-');

			if (dashIndex == -1) {
				rangeMinimum = _parseNumber(name, rangeValue);

				rangeMaximum = rangeMinimum;
			}
			else {
				rangeMinimum = _parseNumber(
					name, rangeValue.substring(0, dashIndex));
				rangeMaximum = _parseNumber(
					name, rangeValue.substring(dashIndex + 1));
			}
		}

		if ((rangeMinimum < minimum) || (rangeMaximum > maximum) ||
			(rangeMinimum > rangeMaximum)) {

			throw new IllegalArgumentException(_getInvalidMessage(name, item));
		}

		Set<Integer> values = new HashSet<>();

		for (int value = rangeMinimum; value <= rangeMaximum; value += step) {
			values.add(value);
		}

		return values;
	}

	private int _parseNumber(String name, String value) {
		try {
			return Integer.parseInt(value);
		}
		catch (NumberFormatException numberFormatException) {
			throw new IllegalArgumentException(
				_getInvalidMessage(name, value), numberFormatException);
		}
	}

	private static final int _FIELD_COUNT = 5;

	private static final long _MILLIS_MINUTE = 60 * 1000;

	private static final int _MINUTES_SEARCH_MAXIMUM = 366 * 24 * 60;

	private final Set<Integer> _daysOfMonth;
	private final Set<Integer> _daysOfWeek;
	private long _hashSpanSeconds;
	private final Set<Integer> _hours;
	private final Set<Integer> _minutes;
	private final Set<Integer> _months;
	private final String _spec;

}