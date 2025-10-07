/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.calendar.util;

import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.kernel.util.TimeZoneUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Calendar;
import java.util.TimeZone;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Adam Brandizzi
 */
public class JCalendarUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testGetDaysBetween() {
		Assert.assertEquals(
			1,
			JCalendarUtil.getDaysBetween(
				JCalendarUtil.getJCalendar(
					2025, Calendar.JUNE, 1, 12, 0, 0, 0, TimeZoneUtil.GMT),
				JCalendarUtil.getJCalendar(
					2025, Calendar.JUNE, 1, 12, 30, 0, 0, TimeZoneUtil.GMT)));
		Assert.assertEquals(
			1,
			JCalendarUtil.getDaysBetween(
				JCalendarUtil.getJCalendar(
					2025, Calendar.JUNE, 1, 12, 0, 0, 0, TimeZoneUtil.GMT),
				JCalendarUtil.getJCalendar(
					2025, Calendar.JUNE, 2, 0, 0, 0, 0, TimeZoneUtil.GMT)));
		Assert.assertEquals(
			2,
			JCalendarUtil.getDaysBetween(
				JCalendarUtil.getJCalendar(
					2025, Calendar.JUNE, 1, 12, 0, 0, 0, TimeZoneUtil.GMT),
				JCalendarUtil.getJCalendar(
					2025, Calendar.JUNE, 2, 0, 1, 0, 0, TimeZoneUtil.GMT)));
	}

	@Test
	public void testGetDSTShiftAtLosAngelesDuringDST() {
		Calendar jCalendar1 = JCalendarUtil.getJCalendar(
			2012, Calendar.MAY, 1, 12, 0, 0, 0, TimeZoneUtil.GMT);
		Calendar jCalendar2 = JCalendarUtil.getJCalendar(
			2013, Calendar.JULY, 2, 12, 0, 0, 0, TimeZoneUtil.GMT);

		int shift = JCalendarUtil.getDSTShift(
			jCalendar1, jCalendar2, _losAngelesTimeZone);

		Assert.assertEquals(0, shift);
	}

	@Test
	public void testGetDSTShiftAtLosAngelesDuringNoDST() {
		Calendar jCalendar1 = JCalendarUtil.getJCalendar(
			2013, Calendar.DECEMBER, 1, 12, 0, 0, 0, TimeZoneUtil.GMT);
		Calendar jCalendar2 = JCalendarUtil.getJCalendar(
			2013, Calendar.JANUARY, 2, 12, 0, 0, 0, TimeZoneUtil.GMT);

		int shift = JCalendarUtil.getDSTShift(
			jCalendar1, jCalendar2, _losAngelesTimeZone);

		Assert.assertEquals(0, shift);
	}

	@Test
	public void testGetDSTShiftAtLosAngelesFromDSTToNoDST() {
		Calendar jCalendar1 = JCalendarUtil.getJCalendar(
			2013, Calendar.JULY, 1, 12, 0, 0, 0, TimeZoneUtil.GMT);
		Calendar jCalendar2 = JCalendarUtil.getJCalendar(
			2013, Calendar.JANUARY, 1, 12, 0, 0, 0, TimeZoneUtil.GMT);

		int shift = JCalendarUtil.getDSTShift(
			jCalendar1, jCalendar2, _losAngelesTimeZone);

		Assert.assertEquals(JCalendarUtil.HOUR, shift);
	}

	@Test
	public void testGetDSTShiftAtLosAngelesFromNoDSTToDST() {
		Calendar jCalendar1 = JCalendarUtil.getJCalendar(
			2013, Calendar.JANUARY, 1, 12, 0, 0, 0, TimeZoneUtil.GMT);
		Calendar jCalendar2 = JCalendarUtil.getJCalendar(
			2013, Calendar.JULY, 1, 12, 0, 0, 0, TimeZoneUtil.GMT);

		int shift = JCalendarUtil.getDSTShift(
			jCalendar1, jCalendar2, _losAngelesTimeZone);

		Assert.assertEquals(-1 * JCalendarUtil.HOUR, shift);
	}

	@Test
	public void testGetJCalendar() {
		Calendar losAngelesJCalendar = CalendarFactoryUtil.getCalendar(
			randomYear(), randomMonth(), randomDayOfMonth(), randomHour(),
			randomMinute(), randomSecond(), randomMillisecond(),
			_losAngelesTimeZone);

		Calendar madridJCalendar = JCalendarUtil.getJCalendar(
			losAngelesJCalendar, _madridTimeZone);

		Assert.assertEquals(_madridTimeZone, madridJCalendar.getTimeZone());
		Assert.assertEquals(
			losAngelesJCalendar.getTimeInMillis(),
			madridJCalendar.getTimeInMillis());
	}

	@Test
	public void testIsMidnight() {
		Assert.assertTrue(
			JCalendarUtil.isMidnight(
				JCalendarUtil.getJCalendar(
					2025, Calendar.JUNE, 1, 0, 0, 0, 0, TimeZoneUtil.GMT)));
		Assert.assertFalse(
			JCalendarUtil.isMidnight(
				JCalendarUtil.getJCalendar(
					2025, Calendar.JUNE, 1, 12, 0, 0, 0, TimeZoneUtil.GMT)));
	}

	@Test
	public void testIsSameDayOfWeek() {
		Calendar jCalendar1 = CalendarFactoryUtil.getCalendar(
			2015, Calendar.DECEMBER, 4);
		Calendar jCalendar2 = CalendarFactoryUtil.getCalendar(
			2015, Calendar.DECEMBER, 11);

		Assert.assertTrue(
			JCalendarUtil.isSameDayOfWeek(jCalendar1, jCalendar2));

		jCalendar2 = CalendarFactoryUtil.getCalendar(
			2015, Calendar.DECEMBER, 12);

		Assert.assertFalse(
			JCalendarUtil.isSameDayOfWeek(jCalendar1, jCalendar2));
	}

	@Test
	public void testMergeJCalendar() {
		Calendar dateJCalendar = CalendarFactoryUtil.getCalendar(
			randomYear(), randomMonth(), randomDayOfMonth(), randomHour(),
			randomMinute(), randomSecond(), randomMillisecond(),
			_losAngelesTimeZone);
		Calendar timeJCalendar = CalendarFactoryUtil.getCalendar(
			randomYear(), randomMonth(), randomDayOfMonth(), randomHour(),
			randomMinute(), randomSecond(), randomMillisecond(),
			_madridTimeZone);

		Calendar jCalendar = JCalendarUtil.mergeJCalendar(
			dateJCalendar, timeJCalendar, _calcuttaTimeZone);

		Assert.assertEquals(
			dateJCalendar.get(Calendar.YEAR), jCalendar.get(Calendar.YEAR));
		Assert.assertEquals(
			dateJCalendar.get(Calendar.MONTH), jCalendar.get(Calendar.MONTH));
		Assert.assertEquals(
			dateJCalendar.get(Calendar.DAY_OF_MONTH),
			jCalendar.get(Calendar.DAY_OF_MONTH));
		Assert.assertEquals(
			timeJCalendar.get(Calendar.HOUR), jCalendar.get(Calendar.HOUR));
		Assert.assertEquals(
			timeJCalendar.get(Calendar.MINUTE), jCalendar.get(Calendar.MINUTE));
		Assert.assertEquals(
			timeJCalendar.get(Calendar.SECOND), jCalendar.get(Calendar.SECOND));
		Assert.assertEquals(
			timeJCalendar.get(Calendar.MILLISECOND),
			jCalendar.get(Calendar.MILLISECOND));
		Assert.assertEquals(
			timeJCalendar.get(Calendar.AM_PM), jCalendar.get(Calendar.AM_PM));
		Assert.assertEquals(_calcuttaTimeZone, jCalendar.getTimeZone());
	}

	protected int randomDayOfMonth() {
		return RandomTestUtil.randomInt(1, 29);
	}

	protected int randomHour() {
		return RandomTestUtil.randomInt(0, 24);
	}

	protected int randomMillisecond() {
		return RandomTestUtil.randomInt(0, 100);
	}

	protected int randomMinute() {
		return RandomTestUtil.randomInt(0, 60);
	}

	protected int randomMonth() {
		return RandomTestUtil.randomInt(0, 12);
	}

	protected int randomSecond() {
		return RandomTestUtil.randomInt(0, 60);
	}

	protected int randomYear() {
		return RandomTestUtil.randomInt(2000, 2100);
	}

	private static final TimeZone _calcuttaTimeZone = TimeZone.getTimeZone(
		"Asia/Calcutta");
	private static final TimeZone _losAngelesTimeZone = TimeZone.getTimeZone(
		"America/Los_Angeles");
	private static final TimeZone _madridTimeZone = TimeZone.getTimeZone(
		"Europe/Madrid");

}