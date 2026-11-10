/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.taglib.aui;

import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.lang.reflect.InvocationTargetException;

import java.util.List;
import java.util.function.Function;
import java.util.function.ToLongFunction;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Shuyang Zhou
 */
public class AUIUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testGetBeanPropertyFunction() {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				AUIUtil.class.getName(), LoggerTestUtil.WARN)) {

			String defaultValue = RandomTestUtil.randomString();

			Function<Object, String> function = AUIUtil.getBeanPropertyFunction(
				"count", defaultValue);

			Bean bean = new Bean();

			Assert.assertEquals(
				String.valueOf(bean.getCount()), function.apply(bean));

			function = AUIUtil.getBeanPropertyFunction(
				"description", defaultValue);

			Assert.assertEquals(defaultValue, function.apply(bean));

			function = AUIUtil.getBeanPropertyFunction("failure", defaultValue);

			Assert.assertEquals(defaultValue, function.apply(bean));

			function = AUIUtil.getBeanPropertyFunction("missing", defaultValue);

			Assert.assertEquals(defaultValue, function.apply(bean));

			function = AUIUtil.getBeanPropertyFunction("name", defaultValue);

			Assert.assertEquals(bean.getName(), function.apply(bean));

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 0, logEntries.size());
		}
	}

	@Test
	public void testGetBeanPropertyToLongFunction() {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				AUIUtil.class.getName(), LoggerTestUtil.ERROR)) {

			ToLongFunction<Object> toLongFunction =
				AUIUtil.getBeanPropertyToLongFunction("failure");

			Bean bean = new Bean();

			Assert.assertEquals(0, toLongFunction.applyAsLong(bean));

			toLongFunction = AUIUtil.getBeanPropertyToLongFunction("id");

			Assert.assertEquals(bean.getId(), toLongFunction.applyAsLong(bean));

			toLongFunction = AUIUtil.getBeanPropertyToLongFunction("typeId");

			Assert.assertEquals(0, toLongFunction.applyAsLong(bean));

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 2, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			Assert.assertEquals(
				"Unable to get property failure of " + Bean.class.getName(),
				logEntry.getMessage());
			Assert.assertEquals("ERROR", logEntry.getPriority());

			Throwable throwable = logEntry.getThrowable();

			Assert.assertSame(bean.runtimeException, throwable.getCause());
			Assert.assertSame(
				InvocationTargetException.class, throwable.getClass());

			logEntry = logEntries.get(1);

			Assert.assertEquals(
				"Unable to get property typeId of " + Bean.class.getName(),
				logEntry.getMessage());
			Assert.assertEquals("ERROR", logEntry.getPriority());

			throwable = logEntry.getThrowable();

			Assert.assertSame(
				NoSuchMethodException.class, throwable.getClass());
		}
	}

	public static class Bean {

		public int getCount() {
			return _count;
		}

		public String getDescription() {
			return null;
		}

		public String getFailure() {
			throw runtimeException;
		}

		public long getId() {
			return _id;
		}

		public String getName() {
			return _name;
		}

		public final RuntimeException runtimeException = new RuntimeException();

		private final int _count = RandomTestUtil.randomInt();
		private final long _id = RandomTestUtil.randomLong();
		private final String _name = RandomTestUtil.randomString();

	}

}