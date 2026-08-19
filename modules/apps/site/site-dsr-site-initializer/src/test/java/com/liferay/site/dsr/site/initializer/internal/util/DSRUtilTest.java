/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.dsr.site.initializer.internal.util;

import com.liferay.portal.kernel.license.util.App;
import com.liferay.portal.kernel.license.util.LicenseManagerUtil;
import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Michele Vigilante
 */
public class DSRUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		_calendarFactoryUtilMockedStatic.when(
			CalendarFactoryUtil::getCalendar
		).thenReturn(
			_calendar
		);
	}

	@After
	public void tearDown() {
		_calendarFactoryUtilMockedStatic.close();
		_licenseManagerUtilMockedStatic.close();
	}

	@Test
	public void testGetExpirationDays() {
		_testGetExpirationDaysWhenExpired();
		_testGetExpirationDaysWhenExpiringInLessThanADay();
		_testGetExpirationDaysWhenExpiringSoon();
	}

	@Test
	public void testIsExpired() {
		_testIsExpiredWhenAppIsDisabled();
		_testIsExpiredWhenExpired();
		_testIsExpiredWhenExpiringSoon();
		_testIsExpiredWhenLicenseIsUnlimited();
	}

	private void _setUpLicenseManagerUtil(
		boolean appEnabled, Long timeToExpiration) {

		_licenseManagerUtilMockedStatic.when(
			() -> LicenseManagerUtil.isAppEnabled(App.DSR)
		).thenReturn(
			appEnabled
		);

		Date expirationDate = null;

		if (timeToExpiration != null) {
			expirationDate = new Date(
				_calendar.getTimeInMillis() + timeToExpiration);
		}

		_licenseManagerUtilMockedStatic.when(
			() -> LicenseManagerUtil.getAppExpirationDate(App.DSR)
		).thenReturn(
			expirationDate
		);
	}

	private void _testGetExpirationDaysWhenExpired() {
		_setUpLicenseManagerUtil(true, -Time.DAY);

		Assert.assertNull(DSRUtil.getExpirationDays());
	}

	private void _testGetExpirationDaysWhenExpiringInLessThanADay() {
		_setUpLicenseManagerUtil(true, 12 * Time.HOUR);

		Assert.assertEquals(Integer.valueOf(0), DSRUtil.getExpirationDays());
	}

	private void _testGetExpirationDaysWhenExpiringSoon() {
		_setUpLicenseManagerUtil(true, 30 * Time.DAY);

		Assert.assertEquals(Integer.valueOf(30), DSRUtil.getExpirationDays());
	}

	private void _testIsExpiredWhenAppIsDisabled() {
		_setUpLicenseManagerUtil(false, null);

		Assert.assertTrue(DSRUtil.isExpired());
	}

	private void _testIsExpiredWhenExpired() {
		_setUpLicenseManagerUtil(true, -Time.DAY);

		Assert.assertTrue(DSRUtil.isExpired());
	}

	private void _testIsExpiredWhenExpiringSoon() {
		_setUpLicenseManagerUtil(true, 25 * Time.DAY);

		Assert.assertFalse(DSRUtil.isExpired());
	}

	private void _testIsExpiredWhenLicenseIsUnlimited() {
		_setUpLicenseManagerUtil(true, null);

		Assert.assertFalse(DSRUtil.isExpired());
	}

	private final Calendar _calendar = new GregorianCalendar(
		2026, Calendar.JANUARY, 1);
	private final MockedStatic<CalendarFactoryUtil>
		_calendarFactoryUtilMockedStatic = Mockito.mockStatic(
			CalendarFactoryUtil.class);
	private final MockedStatic<LicenseManagerUtil>
		_licenseManagerUtilMockedStatic = Mockito.mockStatic(
			LicenseManagerUtil.class);

}