/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.social.util;

import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.social.kernel.util.SocialCounterPeriodUtil;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Zsolt Berentey
 */
public class SocialCounterPeriodUtilTest {

	@AfterClass
	public static void tearDownClass() {
		_propsUtilMockedStatic.close();
	}

	@Before
	public void setUp() throws Exception {
		_propsUtilMockedStatic.when(
			() -> PropsUtil.get(PropsKeys.SOCIAL_ACTIVITY_COUNTER_PERIOD_LENGTH)
		).thenReturn(
			"1"
		);
	}

	@Test
	public void testGetActivityDay() {
		Calendar calendar = new GregorianCalendar(2011, Calendar.JANUARY, 1);

		Assert.assertEquals(
			0, SocialCounterPeriodUtil.getActivityDay(calendar));
		Assert.assertEquals(
			0,
			SocialCounterPeriodUtil.getActivityDay(calendar.getTimeInMillis()));

		calendar = new GregorianCalendar(2011, Calendar.FEBRUARY, 1);

		Assert.assertEquals(
			31, SocialCounterPeriodUtil.getActivityDay(calendar));
		Assert.assertEquals(
			31,
			SocialCounterPeriodUtil.getActivityDay(calendar.getTimeInMillis()));
	}

	@Test
	public void testGetDate() {
		Calendar calendar = new GregorianCalendar(2011, Calendar.JANUARY, 1);

		Assert.assertEquals(
			calendar.getTime(), SocialCounterPeriodUtil.getDate(0));

		calendar = new GregorianCalendar(2011, Calendar.FEBRUARY, 1);

		Assert.assertEquals(
			calendar.getTime(), SocialCounterPeriodUtil.getDate(31));
	}

	@Test
	public void testGetPeriodLength() {
		Assert.assertEquals(1, SocialCounterPeriodUtil.getPeriodLength());
		Assert.assertEquals(1, SocialCounterPeriodUtil.getPeriodLength(-1));
	}

	@Test
	public void testGetStartPeriod() {
		Calendar calendar = new GregorianCalendar(2011, Calendar.JANUARY, 15);

		Assert.assertEquals(
			14,
			SocialCounterPeriodUtil.getStartPeriod(calendar.getTimeInMillis()));

		int startPeriod = SocialCounterPeriodUtil.getStartPeriod();

		Assert.assertEquals(
			startPeriod, SocialCounterPeriodUtil.getStartPeriod(0));
		Assert.assertEquals(
			startPeriod - 1, SocialCounterPeriodUtil.getStartPeriod(-1));
	}

	private static final MockedStatic<PropsUtil> _propsUtilMockedStatic =
		Mockito.mockStatic(PropsUtil.class);

}