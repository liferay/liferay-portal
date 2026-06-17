/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.seo.studio.web.internal.util;

import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.time.temporal.TemporalAdjusters;

import java.util.Date;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Jonathan McCann
 */
public class SEOStudioScanScheduleUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testGetNextScanDateDaily() {
		LocalDate localDate = LocalDate.of(2026, Month.JANUARY, 1);

		Assert.assertEquals(
			_toDate(localDate, 9),
			SEOStudioScanScheduleUtil.getNextScanDate(
				_toInstant(localDate, 8), null, null, "daily", "09:00", "UTC"));

		Assert.assertEquals(
			_toDate(localDate.plusDays(1), 9),
			SEOStudioScanScheduleUtil.getNextScanDate(
				_toInstant(localDate, 9), null, null, "daily", "09:00", "UTC"));

		Assert.assertEquals(
			_toDate(localDate.plusDays(1), 9),
			SEOStudioScanScheduleUtil.getNextScanDate(
				_toInstant(localDate, 10), null, null, "daily", "09:00",
				"UTC"));
	}

	@Test
	public void testGetNextScanDateDailyDuringDST() {
		Assert.assertEquals(
			_toDate(LocalDate.of(2026, Month.JULY, 2), 13),
			SEOStudioScanScheduleUtil.getNextScanDate(
				_toInstant(LocalDate.of(2026, Month.JULY, 1), 20), null, null,
				"daily", "09:00", "America/New_York"));
	}

	@Test
	public void testGetNextScanDateDailyDuringFallBackOverlap() {
		Assert.assertEquals(
			_toDate(LocalDate.of(2026, Month.NOVEMBER, 1), 6),
			SEOStudioScanScheduleUtil.getNextScanDate(
				_toInstant(LocalDate.of(2026, Month.NOVEMBER, 1), 5, 30), null,
				null, "daily", "01:00", "America/New_York"));
	}

	@Test
	public void testGetNextScanDateDailyDuringNoDST() {
		Assert.assertEquals(
			_toDate(LocalDate.of(2026, Month.JANUARY, 2), 14),
			SEOStudioScanScheduleUtil.getNextScanDate(
				_toInstant(LocalDate.of(2026, Month.JANUARY, 1), 20), null,
				null, "daily", "09:00", "America/New_York"));
	}

	@Test
	public void testGetNextScanDateDailyDuringSpringForwardGap() {
		Assert.assertEquals(
			_toDate(LocalDate.of(2026, Month.MARCH, 9), 6),
			SEOStudioScanScheduleUtil.getNextScanDate(
				_toInstant(LocalDate.of(2026, Month.MARCH, 8), 12), null, null,
				"daily", "02:00", "America/New_York"));
	}

	@Test
	public void testGetNextScanDateMonthly() {
		Assert.assertEquals(
			_toDate(LocalDate.of(2026, Month.JANUARY, 15), 9),
			SEOStudioScanScheduleUtil.getNextScanDate(
				_toInstant(LocalDate.of(2026, Month.JANUARY, 1), 8), 15, null,
				"monthly", "09:00", "UTC"));
	}

	@Test
	public void testGetNextScanDateMonthlyWhenDayOfMonthExceedsMonthLength() {
		Assert.assertEquals(
			_toDate(LocalDate.of(2026, Month.FEBRUARY, 28), 9),
			SEOStudioScanScheduleUtil.getNextScanDate(
				_toInstant(LocalDate.of(2026, Month.JANUARY, 31), 10), 31, null,
				"monthly", "09:00", "UTC"));
	}

	@Test
	public void testGetNextScanDateWeekly() {
		LocalDate localDate = LocalDate.of(2026, Month.JANUARY, 1);

		LocalDate wednesdayLocalDate = localDate.with(
			TemporalAdjusters.next(DayOfWeek.WEDNESDAY));

		Instant instant = _toInstant(wednesdayLocalDate, 8);

		Assert.assertEquals(
			_toDate(
				wednesdayLocalDate.with(
					TemporalAdjusters.next(DayOfWeek.MONDAY)),
				9),
			SEOStudioScanScheduleUtil.getNextScanDate(
				instant, null, "MO", "weekly", "09:00", "UTC"));

		Assert.assertEquals(
			_toDate(wednesdayLocalDate, 9),
			SEOStudioScanScheduleUtil.getNextScanDate(
				instant, null, "WE", "weekly", "09:00", "UTC"));

		Assert.assertEquals(
			_toDate(wednesdayLocalDate.plusWeeks(1), 9),
			SEOStudioScanScheduleUtil.getNextScanDate(
				_toInstant(wednesdayLocalDate, 10), null, "WE", "weekly",
				"09:00", "UTC"));
	}

	private Date _toDate(LocalDate localDate, int hour) {
		return Date.from(_toInstant(localDate, hour));
	}

	private Instant _toInstant(LocalDate localDate, int hour) {
		return _toInstant(localDate, hour, 0);
	}

	private Instant _toInstant(LocalDate localDate, int hour, int minute) {
		LocalDateTime localDateTime = localDate.atTime(hour, minute);

		return localDateTime.toInstant(ZoneOffset.UTC);
	}

}