/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.taglib.util;

import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.lang.reflect.InvocationTargetException;

import java.math.BigDecimal;

import java.util.List;
import java.util.Locale;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Shuyang Zhou
 */
public class BeanGetterUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testGetLong() {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				BeanGetterUtil.class.getName(), LoggerTestUtil.ERROR)) {

			Assert.assertEquals(0, BeanGetterUtil.getLong(null, "id"));

			Bean bean = new Bean();

			Assert.assertEquals(0, BeanGetterUtil.getLong(bean, "active"));
			Assert.assertEquals(7, BeanGetterUtil.getLong(bean, "count"));
			Assert.assertEquals(0, BeanGetterUtil.getLong(bean, "description"));
			Assert.assertEquals(0, BeanGetterUtil.getLong(bean, "failure"));
			Assert.assertEquals(42, BeanGetterUtil.getLong(bean, "id"));
			Assert.assertEquals(0, BeanGetterUtil.getLong(bean, "missing"));
			Assert.assertEquals(0, BeanGetterUtil.getLong(bean, "name"));
			Assert.assertEquals(12, BeanGetterUtil.getLong(bean, "number"));
			Assert.assertEquals(3, BeanGetterUtil.getLong(bean, "ratio"));

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 2, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			Assert.assertEquals(
				"Unable to get property failure of " + Bean.class.getName(),
				logEntry.getMessage());
			Assert.assertEquals("ERROR", logEntry.getPriority());

			Throwable throwable = logEntry.getThrowable();

			Assert.assertSame(
				InvocationTargetException.class, throwable.getClass());
			Assert.assertSame(bean.illegalStateException, throwable.getCause());

			logEntry = logEntries.get(1);

			Assert.assertEquals(
				"Unable to get property missing of " + Bean.class.getName(),
				logEntry.getMessage());
			Assert.assertEquals("ERROR", logEntry.getPriority());
			Assert.assertNull(logEntry.getThrowable());
		}
	}

	@Test
	public void testGetObjectSilent() {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				BeanGetterUtil.class.getName(), LoggerTestUtil.WARN)) {

			Assert.assertNull(BeanGetterUtil.getObjectSilent(null, "id"));

			Bean bean = new Bean();

			Assert.assertEquals(
				Boolean.TRUE, BeanGetterUtil.getObjectSilent(bean, "active"));
			Assert.assertEquals(
				42L, BeanGetterUtil.getObjectSilent(bean, "id"));
			Assert.assertNull(BeanGetterUtil.getObjectSilent(bean, "missing"));
			Assert.assertEquals(
				new BigDecimal("12.50"),
				BeanGetterUtil.getObjectSilent(bean, "price"));

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 0, logEntries.size());
		}
	}

	@Test
	public void testGetStringSilent() {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				BeanGetterUtil.class.getName(), LoggerTestUtil.WARN)) {

			Assert.assertEquals(
				"", BeanGetterUtil.getStringSilent(null, "name"));
			Assert.assertEquals(
				"default",
				BeanGetterUtil.getStringSilent(null, "name", "default"));

			Bean bean = new Bean();

			Assert.assertEquals(
				"true", BeanGetterUtil.getStringSilent(bean, "active"));
			Assert.assertEquals(
				"default",
				BeanGetterUtil.getStringSilent(bean, "class", "default"));
			Assert.assertEquals(
				"default",
				BeanGetterUtil.getStringSilent(bean, "description", "default"));
			Assert.assertEquals(
				"default",
				BeanGetterUtil.getStringSilent(bean, "failure", "default"));
			Assert.assertEquals(
				"42", BeanGetterUtil.getStringSilent(bean, "id"));
			Assert.assertEquals(
				"", BeanGetterUtil.getStringSilent(bean, "missing"));
			Assert.assertEquals(
				"default",
				BeanGetterUtil.getStringSilent(bean, "missing", "default"));
			Assert.assertEquals(
				"Alice", BeanGetterUtil.getStringSilent(bean, "name"));
			Assert.assertEquals(
				"default",
				BeanGetterUtil.getStringSilent(bean, "name[]", "default"));
			Assert.assertEquals(
				"12.50", BeanGetterUtil.getStringSilent(bean, "price"));
			Assert.assertEquals(
				"3.75", BeanGetterUtil.getStringSilent(bean, "ratio"));
			Assert.assertEquals(
				"default",
				BeanGetterUtil.getStringSilent(bean, "staticName", "default"));

			Assert.assertEquals(
				"code",
				BeanGetterUtil.getStringSilent(
					new DefaultMethodBean(), "code"));
			Assert.assertEquals(
				"Bob",
				BeanGetterUtil.getStringSilent(new HiddenBean(), "name"));

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 0, logEntries.size());
		}
	}

	public static class Bean {

		public static String getStaticName() {
			return "static";
		}

		public int getCount() {
			return 7;
		}

		public String getDescription() {
			return null;
		}

		public String getFailure() {
			throw illegalStateException;
		}

		public long getId() {
			return 42;
		}

		public String getName() {
			return "Alice";
		}

		public String getName(Locale locale) {
			return "Alice " + locale;
		}

		public String getNumber() {
			return " 12 ";
		}

		public BigDecimal getPrice() {
			return new BigDecimal("12.50");
		}

		public double getRatio() {
			return 3.75;
		}

		public boolean isActive() {
			return true;
		}

		public final IllegalStateException illegalStateException =
			new IllegalStateException();

	}

	public static class DefaultMethodBean implements Coded {
	}

	public interface Coded {

		public default String getCode() {
			return "code";
		}

	}

	public interface Named {

		public String getName();

	}

	private static class HiddenBean implements Named {

		@Override
		public String getName() {
			return "Bob";
		}

	}

}