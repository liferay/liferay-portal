/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.util;

import com.liferay.portal.kernel.configuration.Filter;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;

import java.text.Collator;
import java.text.RuleBasedCollator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Preston Crary
 */
public class CollatorUtilTest {

	@AfterClass
	public static void tearDownClass() {
		_propsUtilMockedStatic.close();
	}

	@Before
	public void setUp() {
		Map<?, ?> map = ReflectionTestUtil.getFieldValue(
			CollatorUtil.class, "_rules");

		map.clear();
	}

	@Test
	public void testGetInstanceWithInvalidProperty() {
		_propsUtilMockedStatic.when(
			() -> PropsUtil.get(
				Mockito.eq("collator.rules"), Mockito.any(Filter.class))
		).thenReturn(
			"<<<"
		);

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				CollatorUtil.class.getName(), LoggerTestUtil.ALL)) {

			CollatorUtil.getInstance(LocaleUtil.getDefault());

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			String message = logEntry.getMessage();

			Assert.assertTrue(
				logEntry.toString(),
				message.contains("missing chars (=,;<&): <<"));
		}
	}

	@Test
	public void testGetInstanceWithoutProperty() {
		_propsUtilMockedStatic.when(
			() -> PropsUtil.get(
				Mockito.eq("collator.rules"), Mockito.any(Filter.class))
		).thenReturn(
			""
		);

		Collator collator = CollatorUtil.getInstance(LocaleUtil.US);

		Assert.assertEquals(Collator.getInstance(LocaleUtil.US), collator);

		List<String> expected = new ArrayList<>();

		expected.add("AB");
		expected.add("A B");

		List<String> actual = new ArrayList<>();

		actual.add("A B");
		actual.add("AB");

		actual.sort(collator);

		Assert.assertEquals(expected, actual);
	}

	@Test
	public void testGetInstanceWithProperty() {
		_propsUtilMockedStatic.when(
			() -> PropsUtil.get(
				Mockito.eq("collator.rules"), Mockito.any(Filter.class))
		).thenReturn(
			_RULES
		);

		Collator collator = CollatorUtil.getInstance(LocaleUtil.getDefault());

		RuleBasedCollator ruleBasedCollator = (RuleBasedCollator)collator;

		Assert.assertEquals(_RULES, ruleBasedCollator.getRules());

		List<String> expected = new ArrayList<>();

		expected.add("A B");
		expected.add("AB");

		List<String> actual = new ArrayList<>();

		actual.add("AB");
		actual.add("A B");

		actual.sort(collator);

		Assert.assertEquals(expected, actual);
	}

	private static final String _RULES = "=A<b,' '<A";

	private static final MockedStatic<PropsUtil> _propsUtilMockedStatic =
		Mockito.mockStatic(PropsUtil.class);

}