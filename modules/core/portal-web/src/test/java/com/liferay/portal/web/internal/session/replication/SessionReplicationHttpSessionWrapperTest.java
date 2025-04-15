/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.web.internal.session.replication;

import com.liferay.petra.process.ClassPathUtil;
import com.liferay.portal.kernel.servlet.HttpSessionWrapper;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.CodeCoverageAssertor;
import com.liferay.portal.kernel.util.ProxyFactory;
import com.liferay.portal.kernel.util.TransientValue;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.servlet.http.HttpSession;

import java.lang.reflect.Constructor;

import java.net.URLClassLoader;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Tina Tian
 */
public class SessionReplicationHttpSessionWrapperTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			CodeCoverageAssertor.INSTANCE, LiferayUnitTestRule.INSTANCE);

	@Test
	public void testGetAttributeWithException() throws Exception {
		TestHttpSession testHttpSession = new TestHttpSession();

		SessionReplicationHttpSessionWrapper
			sessionReplicationHttpSessionWrapper =
				new SessionReplicationHttpSessionWrapper(testHttpSession);

		sessionReplicationHttpSessionWrapper.setAttribute(
			_TEST_KEY, new TransientValue<>(new Object()));

		testHttpSession.setAttribute(_TEST_KEY, new byte[0]);

		try (LogCapture logCapture = LoggerTestUtil.configureJDKLogger(
				SessionReplicationHttpSessionWrapper.class.getName(),
				Level.SEVERE)) {

			sessionReplicationHttpSessionWrapper.getAttribute(_TEST_KEY);

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			Assert.assertEquals(
				"Unable to deserialize object", logEntry.getMessage());
		}
	}

	@Test
	public void testGetSetAndRemoveAttributeWithNonserializableValue() {
		_testGetSetAndRemoveAttribute(new Object(), false);
	}

	@Test
	public void testGetSetAndRemoveAttributeWithSerializableValue()
		throws Exception {

		_testGetSetAndRemoveAttribute(
			_getSessionReplicationHttpSessionWrapperTestValue(_TEST_CONTENT),
			true);
	}

	@Test
	public void testGetSetAndRemoveAttributeWithStringValue() {
		_testGetSetAndRemoveAttribute(_TEST_CONTENT, false);
	}

	@Test
	public void testGetSetAndRemoveAttributeWithTransientValue() {
		_testGetSetAndRemoveAttribute(new TransientValue<>(null), true);
		_testGetSetAndRemoveAttribute(new TransientValue<>(new Object()), true);
	}

	@Test
	public void testMisc() {
		TestHttpSession testHttpSession = new TestHttpSession();

		SessionReplicationHttpSessionWrapper
			sessionReplicationHttpSessionWrapper =
				new SessionReplicationHttpSessionWrapper(testHttpSession);

		Assert.assertNull(
			sessionReplicationHttpSessionWrapper.getAttribute(_TEST_KEY));

		sessionReplicationHttpSessionWrapper.setAttribute(
			"test.key.1", "test.value.1");
		sessionReplicationHttpSessionWrapper.setAttribute(
			"test.key.2", new TransientValue<>(new Object()));
		sessionReplicationHttpSessionWrapper.setAttribute(
			"test.key.3", new TransientValue<>(new Object()));
		sessionReplicationHttpSessionWrapper.setAttribute(
			"test.key.4", new Object());

		Assert.assertEquals(
			"test.value.1",
			sessionReplicationHttpSessionWrapper.getAttribute("test.key.1"));

		sessionReplicationHttpSessionWrapper.removeAttribute("test.key.1");

		Assert.assertNull(
			sessionReplicationHttpSessionWrapper.getAttribute("test.key.1"));

		sessionReplicationHttpSessionWrapper.removeAttribute("test.key.4");

		Assert.assertNull(
			sessionReplicationHttpSessionWrapper.getAttribute("test.key.4"));
	}

	private Object _getSessionReplicationHttpSessionWrapperTestValue(
			String value)
		throws Exception {

		ClassLoader classLoader = new URLClassLoader(
			ClassPathUtil.getClassPathURLs(_CLASS_PATH), null);

		Class<?> clazz = classLoader.loadClass(
			SessionReplicationHttpSessionWrapperTestValue.class.getName());

		Constructor<?> constructor = clazz.getDeclaredConstructor(String.class);

		return constructor.newInstance(value);
	}

	private void _testGetSetAndRemoveAttribute(
		Object testValue, boolean scrubbed) {

		TestHttpSession testHttpSession = new TestHttpSession();

		SessionReplicationHttpSessionWrapper
			sessionReplicationHttpSessionWrapper =
				new SessionReplicationHttpSessionWrapper(testHttpSession);

		sessionReplicationHttpSessionWrapper.setAttribute(_TEST_KEY, testValue);

		Object retrievedValue =
			sessionReplicationHttpSessionWrapper.getAttribute(_TEST_KEY);

		if (testValue instanceof TransientValue) {
			TransientValue<?> transientValue = (TransientValue<?>)testValue;

			if (transientValue.isNull()) {
				Assert.assertNull(retrievedValue);
			}
		}
		else if (scrubbed) {
			SessionReplicationHttpSessionWrapperTestValue
				sessionReplicationHttpSessionWrapperTestValue =
					(SessionReplicationHttpSessionWrapperTestValue)
						retrievedValue;

			Assert.assertEquals(
				_TEST_CONTENT,
				sessionReplicationHttpSessionWrapperTestValue.getValue());
		}
		else {
			Assert.assertSame(testValue, retrievedValue);
		}

		if (scrubbed) {
			Assert.assertNotEquals(
				testValue, testHttpSession.getAttribute(_TEST_KEY));
		}
		else {
			Assert.assertEquals(
				testValue, testHttpSession.getAttribute(_TEST_KEY));
		}

		sessionReplicationHttpSessionWrapper.removeAttribute(_TEST_KEY);

		Assert.assertNull(
			sessionReplicationHttpSessionWrapper.getAttribute(_TEST_KEY));

		Assert.assertNull(testHttpSession.getAttribute(_TEST_KEY));
	}

	private static final String _CLASS_PATH = ClassPathUtil.getJVMClassPath(
		true);

	private static final String _TEST_CONTENT = "_TEST_CONTENT";

	private static final String _TEST_KEY = "TEST_KEY";

	private class TestHttpSession extends HttpSessionWrapper {

		@Override
		public Object getAttribute(String name) {
			return _attributes.get(name);
		}

		@Override
		public Enumeration<String> getAttributeNames() {
			return Collections.enumeration(_attributes.keySet());
		}

		@Override
		public void removeAttribute(String name) {
			_attributes.remove(name);
		}

		@Override
		public void setAttribute(String name, Object value) {
			_attributes.put(name, value);
		}

		private TestHttpSession() {
			super(ProxyFactory.newDummyInstance(HttpSession.class));
		}

		private Map<String, Object> _attributes = new HashMap<>();

	}

}