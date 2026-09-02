/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.monitor;

import java.text.SimpleDateFormat;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Calum Ragan
 */
public class CronScheduleTest extends com.liferay.jenkins.results.parser.Test {

	@Test
	public void testCronSchedule() {
		_testCronScheduleExpectedIllegalArgumentException("* * * *");
		_testCronScheduleExpectedIllegalArgumentException("* * * * * *");

		_testCronScheduleExpectedIllegalArgumentException("0 0 * * MON");
		_testCronScheduleExpectedIllegalArgumentException("0 0 L * *");
		_testCronScheduleExpectedIllegalArgumentException("@daily");
		_testCronScheduleExpectedIllegalArgumentException("H(0-29) 3 * * *");

		_testCronScheduleExpectedIllegalArgumentException("0 0 * * H");
		_testCronScheduleExpectedIllegalArgumentException("0 0 * H *");
		_testCronScheduleExpectedIllegalArgumentException("0 0 H * *");
		_testCronScheduleExpectedIllegalArgumentException("0 0 H/2 * *");

		_testCronScheduleExpectedIllegalArgumentException("* * * * 8");
		_testCronScheduleExpectedIllegalArgumentException("* * * 13 *");
		_testCronScheduleExpectedIllegalArgumentException("* * 0 * *");
		_testCronScheduleExpectedIllegalArgumentException("* 24 * * *");
		_testCronScheduleExpectedIllegalArgumentException("60 * * * *");

		_testCronScheduleExpectedIllegalArgumentException("*/0 * * * *");
		_testCronScheduleExpectedIllegalArgumentException("30-10 * * * *");

		_testCronScheduleExpectedIllegalArgumentException("0 0 1 * 1");

		_testCronScheduleExpectedIllegalArgumentException("");
		_testCronScheduleExpectedIllegalArgumentException(null);
	}

	@Test
	public void testGetHashSpanSeconds() {
		_testGetHashSpanSeconds(0, "*/15 * * * *");
		_testGetHashSpanSeconds(0, "0 6 * * 1-5");

		_testGetHashSpanSeconds(3600, "H * * * *");
		_testGetHashSpanSeconds(3600, "H 3 * * *");
		_testGetHashSpanSeconds(900, "H/15 * * * *");
		_testGetHashSpanSeconds(90000, "H H * * 3");
	}

	@Test
	public void testGetPeriodSeconds() throws Exception {
		_testGetPeriodSeconds("2026-08-27 10:00", 3600, "H * * * *");
		_testGetPeriodSeconds("2026-08-27 10:00", 86400, "0 3 * * *");
		_testGetPeriodSeconds("2026-08-27 10:00", 86400, "0 6 * * 1-5");
		_testGetPeriodSeconds("2026-08-27 10:07", 900, "*/15 * * * *");
		_testGetPeriodSeconds("2026-08-31 10:00", 259200, "0 6 * * 1-5");

		_testGetPeriodSeconds("2026-08-27 10:00", -1, "0 0 31 2 *");
	}

	@Test
	public void testGetPreviousFireTimestamp() throws Exception {
		_testGetPreviousFireTimestamp(
			"2026-08-27 10:00", "2026-08-27 08:00", "0 */8 * * *");
		_testGetPreviousFireTimestamp(
			"2026-08-27 10:07", "2026-08-27 10:00", "*/15 * * * *");
		_testGetPreviousFireTimestamp(
			"2026-08-27 10:47", "2026-08-27 10:30", "*/30 * * * *");

		_testGetPreviousFireTimestamp(
			"2026-08-27 10:47", "2026-08-27 10:30", "0,30 * * * *");

		_testGetPreviousFireTimestamp(
			"2026-08-27 10:00", "2026-08-26 23:00", "0 23 * * *");

		_testGetPreviousFireTimestamp(
			"2026-08-27 10:00", "2026-08-20 21:00", "0 21 * * 4");
		_testGetPreviousFireTimestamp(
			"2026-08-29 10:00", "2026-08-28 06:00", "0 6 * * 1-5");
		_testGetPreviousFireTimestamp(
			"2026-08-31 10:00", "2026-08-31 06:00", "0 6 * * 1-5");

		_testGetPreviousFireTimestamp(
			"2026-08-27 03:10", "2026-08-27 03:00", "H 3 * * *");
		_testGetPreviousFireTimestamp(
			"2026-08-27 10:00", "2026-08-23 00:00", "H 0 * * 0");
		_testGetPreviousFireTimestamp(
			"2026-08-27 10:00", "2026-08-27 03:00", "H 3 * * *");
		_testGetPreviousFireTimestamp(
			"2026-08-27 10:30", "2026-08-27 10:00", "H * * * *");

		_testGetPreviousFireTimestamp(
			"2026-08-27 10:15", "2026-08-27 10:15", "*/15 * * * *");

		_testGetPreviousFireTimestampNoFire("2026-08-27 10:00", "0 0 31 2 *");
	}

	private long _getTimestamp(String dateString) throws Exception {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm");

		Date date = simpleDateFormat.parse(dateString);

		return date.getTime();
	}

	private void _testCronScheduleExpectedIllegalArgumentException(
		String spec) {

		try {
			new CronSchedule(spec);

			Assert.fail("Expected IllegalArgumentException");
		}
		catch (IllegalArgumentException illegalArgumentException) {
		}
	}

	private void _testGetHashSpanSeconds(long expected, String spec) {
		CronSchedule cronSchedule = new CronSchedule(spec);

		testEquals(expected, cronSchedule.getHashSpanSeconds());
	}

	private void _testGetPeriodSeconds(
			String currentDateString, long expectedPeriodSeconds, String spec)
		throws Exception {

		CronSchedule cronSchedule = new CronSchedule(spec);

		testEquals(
			expectedPeriodSeconds,
			cronSchedule.getPeriodSeconds(_getTimestamp(currentDateString)));
	}

	private void _testGetPreviousFireTimestamp(
			String currentDateString, String expectedDateString, String spec)
		throws Exception {

		CronSchedule cronSchedule = new CronSchedule(spec);

		testEquals(
			_getTimestamp(expectedDateString),
			cronSchedule.getPreviousFireTimestamp(
				_getTimestamp(currentDateString)));
	}

	private void _testGetPreviousFireTimestampNoFire(
			String currentDateString, String spec)
		throws Exception {

		CronSchedule cronSchedule = new CronSchedule(spec);

		testEquals(
			-1L,
			cronSchedule.getPreviousFireTimestamp(
				_getTimestamp(currentDateString)));
	}

}