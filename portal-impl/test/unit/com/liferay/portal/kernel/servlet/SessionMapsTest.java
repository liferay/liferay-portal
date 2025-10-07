/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.servlet;

import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.CodeCoverageAssertor;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Dante Wang
 */
public class SessionMapsTest extends BaseSessionMapsTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			CodeCoverageAssertor.INSTANCE, LiferayUnitTestRule.INSTANCE);

	@Test
	public void testAdd() {
		_sessionMaps.add(httpSession, _MAP_KEY, KEY1, VALUE1);
		_sessionMaps.add(httpSession, _MAP_KEY, KEY2, VALUE2);

		Assert.assertEquals(
			VALUE1, _sessionMaps.get(httpSession, _MAP_KEY, KEY1));
		Assert.assertEquals(
			VALUE2, _sessionMaps.get(httpSession, _MAP_KEY, KEY2));

		_sessionMaps.add(httpSession, _MAP_KEY, KEY1, VALUE3);
		_sessionMaps.add(httpSession, _MAP_KEY, KEY2, VALUE3);

		Assert.assertEquals(
			VALUE3, _sessionMaps.get(httpSession, _MAP_KEY, KEY1));
		Assert.assertEquals(
			VALUE3, _sessionMaps.get(httpSession, _MAP_KEY, KEY2));
	}

	@Test
	public void testClear() {
		_sessionMaps.clear(httpSession, _MAP_KEY);

		Assert.assertNull(_sessionMaps.get(httpSession, _MAP_KEY, KEY1));

		_sessionMaps.add(httpSession, _MAP_KEY, KEY1, VALUE1);

		Assert.assertNotNull(_sessionMaps.get(httpSession, _MAP_KEY, KEY1));

		_sessionMaps.clear(httpSession, _MAP_KEY);

		Assert.assertNull(_sessionMaps.get(httpSession, _MAP_KEY, KEY1));
	}

	@Test
	public void testContains() {
		Assert.assertFalse(
			"SessionMaps should not contain " + KEY1,
			_sessionMaps.contains(httpSession, _MAP_KEY, KEY1));

		_sessionMaps.add(httpSession, _MAP_KEY, KEY1, VALUE1);

		Assert.assertTrue(
			"SessionMaps should contain " + KEY1,
			_sessionMaps.contains(httpSession, _MAP_KEY, KEY1));
		Assert.assertFalse(
			"SessionMaps should not contain " + KEY2,
			_sessionMaps.contains(httpSession, _MAP_KEY, KEY2));
	}

	@Test
	public void testGet() {
		Assert.assertNull(_sessionMaps.get(httpSession, _MAP_KEY, KEY1));

		_sessionMaps.add(httpSession, _MAP_KEY, KEY1, VALUE1);

		Assert.assertEquals(
			VALUE1, _sessionMaps.get(httpSession, _MAP_KEY, KEY1));
		Assert.assertNull(_sessionMaps.get(httpSession, _MAP_KEY, KEY2));
	}

	@Test
	public void testInvalidatedSession() {
		_sessionMaps.add(httpSession, _MAP_KEY, KEY1, VALUE1);

		httpSessionInvocationHandler.setInvalidated(true);

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				SessionMaps.class.getName(), LoggerTestUtil.ERROR)) {

			Assert.assertTrue(_sessionMaps.isEmpty(httpSession, _MAP_KEY));

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 0, logEntries.size());

			logCapture.resetPriority(Level.FINE.toString());

			Assert.assertTrue(_sessionMaps.isEmpty(httpSession, _MAP_KEY));

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			Assert.assertEquals("Invalidated", logEntry.getMessage());
			Assert.assertTrue(
				logEntry.getThrowable() instanceof IllegalStateException);
		}
	}

	@Test
	public void testIsEmpty() {
		Assert.assertTrue(
			"The map should be empty when it does not exist in session",
			_sessionMaps.isEmpty(httpSession, _MAP_KEY));

		_sessionMaps.add(httpSession, _MAP_KEY, KEY1, VALUE1);

		Assert.assertFalse(
			"The map should not be empty",
			_sessionMaps.isEmpty(httpSession, _MAP_KEY));
	}

	@Test
	public void testIteratorAndKeySet() {
		Assert.assertEquals(
			Collections.emptySet(), _sessionMaps.keySet(httpSession, _MAP_KEY));
		Assert.assertEquals(
			Collections.emptyIterator(),
			_sessionMaps.iterator(httpSession, _MAP_KEY));

		Set<String> expectedKeys = new HashSet<>(
			Arrays.asList(KEY1, KEY2, KEY3));

		_sessionMaps.add(httpSession, _MAP_KEY, KEY1, VALUE1);
		_sessionMaps.add(httpSession, _MAP_KEY, KEY2, VALUE2);
		_sessionMaps.add(httpSession, _MAP_KEY, KEY3, VALUE3);

		// Key set

		Assert.assertEquals(
			expectedKeys, _sessionMaps.keySet(httpSession, _MAP_KEY));

		// Iterator

		Set<String> iteratorKeys = new HashSet<>();

		Iterator<String> iterator = _sessionMaps.iterator(
			httpSession, _MAP_KEY);

		while (iterator.hasNext()) {
			iteratorKeys.add(iterator.next());
		}

		Assert.assertEquals(expectedKeys, iteratorKeys);
	}

	@Test
	public void testNullSession() {
		_sessionMaps.add(null, _MAP_KEY, KEY1, VALUE1);

		Assert.assertNull(null, _sessionMaps.get(null, _MAP_KEY, KEY1));
	}

	@Test
	public void testRemove() {
		_sessionMaps.remove(httpSession, _MAP_KEY, KEY1);

		Assert.assertNull(_sessionMaps.get(httpSession, _MAP_KEY, KEY1));

		_sessionMaps.add(httpSession, _MAP_KEY, KEY1, VALUE1);

		Assert.assertEquals(
			VALUE1, _sessionMaps.get(httpSession, _MAP_KEY, KEY1));

		_sessionMaps.remove(httpSession, _MAP_KEY, KEY1);

		Assert.assertNull(_sessionMaps.get(httpSession, _MAP_KEY, KEY1));
	}

	@Test
	public void testSize() {
		Assert.assertEquals(0, _sessionMaps.size(httpSession, _MAP_KEY));

		_sessionMaps.add(httpSession, _MAP_KEY, KEY1, VALUE1);

		Assert.assertEquals(1, _sessionMaps.size(httpSession, _MAP_KEY));

		_sessionMaps.clear(httpSession, _MAP_KEY);

		Assert.assertEquals(0, _sessionMaps.size(httpSession, _MAP_KEY));
	}

	private static final String _MAP_KEY = SessionMapsTest.class.getName();

	private final SessionMaps _sessionMaps = new SessionMaps(HashMap::new);

}