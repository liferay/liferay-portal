/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.web.internal.util;

import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.portal.kernel.util.LocaleThreadLocal;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.TimeZoneUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.text.ParseException;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Adam Brandizzi
 */
public class DateRangeFactoryUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		_defaultLocale = LocaleThreadLocal.getDefaultLocale();
	}

	@After
	public void tearDown() {
		LocaleThreadLocal.setDefaultLocale(_defaultLocale);
	}

	@Test
	public void testBoundedRange() throws Exception {
		_assertForEachLocale(
			() -> Assert.assertEquals(
				"[20180131000000 TO 20180228235959]",
				DateRangeFactoryUtil.getRangeString(
					"2018-01-31", "2018-02-28", TimeZoneUtil.GMT)));
	}

	@Test(expected = ParseException.class)
	public void testInvalidRangeAliases() throws Exception {
		_assertForEachLocale(
			() -> DateRangeFactoryUtil.validateRanges(
				"[{\"label\":\"past-hour\", \"range\":\"[past-test TO *]\"}]"));
	}

	@Test(expected = ParseException.class)
	public void testInvalidRangeDateFormat() throws Exception {
		_assertForEachLocale(
			() -> DateRangeFactoryUtil.validateRanges(
				"[{\"label\":\"past-hour\", \"range\":\"[20190908 TO *]\"}]"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvalidRangeWithoutBrackets() throws Exception {
		_assertForEachLocale(
			() -> DateRangeFactoryUtil.validateRanges(
				"[{\"label\":\"past-hour\", \"range\":\"past-hour TO *\"}]"));
	}

	@Test
	public void testNamedRange() throws Exception {
		_assertForEachLocale(
			() -> {
				Assert.assertEquals(
					"[20180515225959 TO 20180515235959]",
					DateRangeFactoryUtil.getRangeString(
						"past-hour", _calendar));
				Assert.assertEquals(
					"[20180514235959 TO 20180515235959]",
					DateRangeFactoryUtil.getRangeString(
						"past-24-hours", _calendar));
			});
	}

	@Test
	public void testReplaceAliasPast24Hours() throws Exception {
		_assertForEachLocale(
			() -> Assert.assertEquals(
				"[20180514235959 TO 20180515225959]",
				DateRangeFactoryUtil.replaceAliases(
					"[past-24-hours TO past-hour]", _calendar)));
	}

	@Test
	public void testReplaceAliasPastHour() throws Exception {
		_assertForEachLocale(
			() -> Assert.assertEquals(
				"[20180515225959 TO 20180515235959]",
				DateRangeFactoryUtil.replaceAliases(
					"[past-hour TO *]", _calendar)));
	}

	@Test
	public void testReplaceAliasPastMonth() throws Exception {
		_assertForEachLocale(
			() -> Assert.assertEquals(
				"[20180415235959 TO 20180508235959]",
				DateRangeFactoryUtil.replaceAliases(
					"[past-month TO past-week]", _calendar)));
	}

	@Test
	public void testReplaceAliasPastWeek() throws Exception {
		_assertForEachLocale(
			() -> Assert.assertEquals(
				"[20180508235959 TO 20180514235959]",
				DateRangeFactoryUtil.replaceAliases(
					"[past-week TO past-24-hours]", _calendar)));
	}

	@Test
	public void testReplaceAliasPastYear() throws Exception {
		_assertForEachLocale(
			() -> Assert.assertEquals(
				"[20170515235959 TO 20180415235959]",
				DateRangeFactoryUtil.replaceAliases(
					"[past-year TO past-month]", _calendar)));
	}

	private void _assertForEachLocale(UnsafeRunnable<Exception> unsafeRunnable)
		throws Exception {

		for (Locale locale : _locales) {
			LocaleThreadLocal.setDefaultLocale(locale);

			unsafeRunnable.run();
		}
	}

	private final Calendar _calendar = new GregorianCalendar(
		2018, Calendar.MAY, 15, 23, 59, 59);
	private Locale _defaultLocale;
	private final List<Locale> _locales = List.of(
		LocaleUtil.US, new Locale("ar", "SA"));

}