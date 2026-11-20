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
	public void testGetBoolean() {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				BeanGetterUtil.class.getName(), LoggerTestUtil.ERROR)) {

			Assert.assertFalse(BeanGetterUtil.getBoolean(null, "active"));

			Bean bean = new Bean();

			Assert.assertTrue(BeanGetterUtil.getBoolean(bean, "active"));
			Assert.assertFalse(BeanGetterUtil.getBoolean(bean, "description"));
			Assert.assertFalse(BeanGetterUtil.getBoolean(bean, "failure"));
			Assert.assertFalse(BeanGetterUtil.getBoolean(bean, "missing"));
			Assert.assertFalse(BeanGetterUtil.getBoolean(bean, "name"));

			_assertLogEntries(bean, logCapture.getLogEntries());
		}
	}

	@Test
	public void testGetBooleanSilent() {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				BeanGetterUtil.class.getName(), LoggerTestUtil.WARN)) {

			Assert.assertTrue(
				BeanGetterUtil.getBooleanSilent(null, "active", true));

			Bean bean = new Bean();

			Assert.assertTrue(
				BeanGetterUtil.getBooleanSilent(bean, "active", false));
			Assert.assertTrue(
				BeanGetterUtil.getBooleanSilent(bean, "description", true));
			Assert.assertTrue(
				BeanGetterUtil.getBooleanSilent(bean, "failure", true));
			Assert.assertTrue(
				BeanGetterUtil.getBooleanSilent(bean, "missing", true));
			Assert.assertFalse(
				BeanGetterUtil.getBooleanSilent(bean, "name", true));

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 0, logEntries.size());
		}
	}

	@Test
	public void testGetDoubleSilent() {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				BeanGetterUtil.class.getName(), LoggerTestUtil.WARN)) {

			Assert.assertEquals(
				1.5, BeanGetterUtil.getDoubleSilent(null, "ratio", 1.5), 0);

			Bean bean = new Bean();

			Assert.assertEquals(
				7, BeanGetterUtil.getDoubleSilent(bean, "count", 1.5), 0);
			Assert.assertEquals(
				1.5, BeanGetterUtil.getDoubleSilent(bean, "description", 1.5),
				0);
			Assert.assertEquals(
				1.5, BeanGetterUtil.getDoubleSilent(bean, "failure", 1.5), 0);
			Assert.assertEquals(
				1.5, BeanGetterUtil.getDoubleSilent(bean, "missing", 1.5), 0);
			Assert.assertEquals(
				12, BeanGetterUtil.getDoubleSilent(bean, "number", 1.5), 0);
			Assert.assertEquals(
				12.5, BeanGetterUtil.getDoubleSilent(bean, "price", 1.5), 0);
			Assert.assertEquals(
				3.75, BeanGetterUtil.getDoubleSilent(bean, "ratio", 1.5), 0);

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 0, logEntries.size());
		}
	}

	@Test
	public void testGetIntegerSilent() {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				BeanGetterUtil.class.getName(), LoggerTestUtil.WARN)) {

			Assert.assertEquals(
				5, BeanGetterUtil.getIntegerSilent(null, "count", 5));

			Bean bean = new Bean();

			Assert.assertEquals(
				7, BeanGetterUtil.getIntegerSilent(bean, "count", 5));
			Assert.assertEquals(
				5, BeanGetterUtil.getIntegerSilent(bean, "description", 5));
			Assert.assertEquals(
				5, BeanGetterUtil.getIntegerSilent(bean, "failure", 5));
			Assert.assertEquals(
				42, BeanGetterUtil.getIntegerSilent(bean, "id", 5));
			Assert.assertEquals(
				5, BeanGetterUtil.getIntegerSilent(bean, "missing", 5));
			Assert.assertEquals(
				12, BeanGetterUtil.getIntegerSilent(bean, "number", 5));
			Assert.assertEquals(
				3, BeanGetterUtil.getIntegerSilent(bean, "ratio", 5));

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 0, logEntries.size());
		}
	}

	@Test
	public void testGetLong() {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				BeanGetterUtil.class.getName(), LoggerTestUtil.ERROR)) {

			Assert.assertEquals(0, BeanGetterUtil.getLong(null, "id"));
			Assert.assertEquals(5, BeanGetterUtil.getLong(null, "id", 5));

			Bean bean = new Bean();

			Assert.assertEquals(0, BeanGetterUtil.getLong(bean, "active"));
			Assert.assertEquals(7, BeanGetterUtil.getLong(bean, "count"));
			Assert.assertEquals(0, BeanGetterUtil.getLong(bean, "description"));
			Assert.assertEquals(
				5, BeanGetterUtil.getLong(bean, "description", 5));
			Assert.assertEquals(0, BeanGetterUtil.getLong(bean, "failure"));
			Assert.assertEquals(42, BeanGetterUtil.getLong(bean, "id"));
			Assert.assertEquals(42, BeanGetterUtil.getLong(bean, "id", 5));
			Assert.assertEquals(0, BeanGetterUtil.getLong(bean, "missing"));
			Assert.assertEquals(0, BeanGetterUtil.getLong(bean, "name"));
			Assert.assertEquals(12, BeanGetterUtil.getLong(bean, "number"));
			Assert.assertEquals(3, BeanGetterUtil.getLong(bean, "ratio"));

			_assertLogEntries(bean, logCapture.getLogEntries());
		}
	}

	@Test
	public void testGetLongSilent() {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				BeanGetterUtil.class.getName(), LoggerTestUtil.WARN)) {

			Assert.assertEquals(5, BeanGetterUtil.getLongSilent(null, "id", 5));

			Bean bean = new Bean();

			Assert.assertEquals(
				7, BeanGetterUtil.getLongSilent(bean, "count", 5));
			Assert.assertEquals(
				5, BeanGetterUtil.getLongSilent(bean, "description", 5));
			Assert.assertEquals(
				5, BeanGetterUtil.getLongSilent(bean, "failure", 5));
			Assert.assertEquals(
				42, BeanGetterUtil.getLongSilent(bean, "id", 5));
			Assert.assertEquals(
				5, BeanGetterUtil.getLongSilent(bean, "missing", 5));
			Assert.assertEquals(
				12, BeanGetterUtil.getLongSilent(bean, "number", 5));
			Assert.assertEquals(
				3, BeanGetterUtil.getLongSilent(bean, "ratio", 5));

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 0, logEntries.size());
		}
	}

	@Test
	public void testGetObject() {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				BeanGetterUtil.class.getName(), LoggerTestUtil.ERROR)) {

			Assert.assertNull(BeanGetterUtil.getObject(null, "id"));

			Bean bean = new Bean();

			Assert.assertEquals(
				Boolean.TRUE, BeanGetterUtil.getObject(bean, "active"));
			Assert.assertNull(BeanGetterUtil.getObject(bean, "failure"));
			Assert.assertEquals(42L, BeanGetterUtil.getObject(bean, "id"));
			Assert.assertNull(BeanGetterUtil.getObject(bean, "missing"));

			_assertLogEntries(bean, logCapture.getLogEntries());
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
	public void testGetString() {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				BeanGetterUtil.class.getName(), LoggerTestUtil.ERROR)) {

			Assert.assertEquals("", BeanGetterUtil.getString(null, "name"));
			Assert.assertEquals(
				"default", BeanGetterUtil.getString(null, "name", "default"));

			Bean bean = new Bean();

			Assert.assertEquals(
				"true", BeanGetterUtil.getString(bean, "active"));
			Assert.assertEquals(
				"", BeanGetterUtil.getString(bean, "description"));
			Assert.assertEquals(
				"default",
				BeanGetterUtil.getString(bean, "description", "default"));
			Assert.assertEquals(
				"default",
				BeanGetterUtil.getString(bean, "failure", "default"));
			Assert.assertEquals("42", BeanGetterUtil.getString(bean, "id"));
			Assert.assertEquals(
				"default",
				BeanGetterUtil.getString(bean, "missing", "default"));
			Assert.assertEquals(
				"Alice", BeanGetterUtil.getString(bean, "name"));
			Assert.assertEquals(
				"Alice", BeanGetterUtil.getString(bean, "name", "default"));
			Assert.assertEquals(
				"12.50", BeanGetterUtil.getString(bean, "price"));

			_assertLogEntries(bean, logCapture.getLogEntries());
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

	private void _assertLogEntries(Bean bean, List<LogEntry> logEntries) {
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

	private static class HiddenBean implements Named {

		@Override
		public String getName() {
			return "Bob";
		}

	}

}