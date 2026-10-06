/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.servlet;

import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.CodeCoverageAssertor;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.servlet.http.HttpSession;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.FutureTask;
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
		SessionMaps.add(httpSession, _MAP_KEY, KEY1, VALUE1);
		SessionMaps.add(httpSession, _MAP_KEY, KEY2, VALUE2);

		Assert.assertEquals(
			VALUE1, SessionMaps.get(httpSession, _MAP_KEY, KEY1));
		Assert.assertEquals(
			VALUE2, SessionMaps.get(httpSession, _MAP_KEY, KEY2));

		SessionMaps.add(httpSession, _MAP_KEY, KEY1, VALUE3);
		SessionMaps.add(httpSession, _MAP_KEY, KEY2, VALUE3);

		Assert.assertEquals(
			VALUE3, SessionMaps.get(httpSession, _MAP_KEY, KEY1));
		Assert.assertEquals(
			VALUE3, SessionMaps.get(httpSession, _MAP_KEY, KEY2));
	}

	@Test
	public void testAddConcurrently() throws Exception {
		Map<String, Object> attributes = new ConcurrentHashMap<>();
		CountDownLatch countDownLatch1 = new CountDownLatch(2);
		CountDownLatch countDownLatch2 = new CountDownLatch(1);

		HttpSession sharedHttpSession = (HttpSession)ProxyUtil.newProxyInstance(
			HttpSession.class.getClassLoader(),
			new Class<?>[] {HttpSession.class},
			(proxy, method, args) -> {
				String methodName = method.getName();

				if (methodName.equals("getAttribute")) {
					Object attribute = attributes.get(args[0]);

					countDownLatch1.countDown();

					countDownLatch2.await();

					return attribute;
				}

				if (methodName.equals("setAttribute")) {
					attributes.put((String)args[0], args[1]);
				}

				return null;
			});

		FutureTask<Void> futureTask1 = new FutureTask<>(
			() -> SessionMaps.add(sharedHttpSession, _MAP_KEY, KEY1, VALUE1),
			null);
		FutureTask<Void> futureTask2 = new FutureTask<>(
			() -> SessionMaps.add(sharedHttpSession, _MAP_KEY, KEY2, VALUE2),
			null);

		Thread thread1 = new Thread(futureTask1);
		Thread thread2 = new Thread(futureTask2);

		thread1.start();
		thread2.start();

		countDownLatch1.await();

		countDownLatch2.countDown();

		futureTask1.get();
		futureTask2.get();

		Map<String, Object> map = (Map<String, Object>)attributes.get(_MAP_KEY);

		Assert.assertEquals(VALUE1, map.get(KEY1));
		Assert.assertEquals(VALUE2, map.get(KEY2));
		Assert.assertTrue(map instanceof ConcurrentHashMap);
	}

	@Test
	public void testClear() {
		SessionMaps.clear(httpSession, _MAP_KEY);

		Assert.assertNull(SessionMaps.get(httpSession, _MAP_KEY, KEY1));

		SessionMaps.add(httpSession, _MAP_KEY, KEY1, VALUE1);

		Assert.assertNotNull(SessionMaps.get(httpSession, _MAP_KEY, KEY1));

		SessionMaps.clear(httpSession, _MAP_KEY);

		Assert.assertNull(SessionMaps.get(httpSession, _MAP_KEY, KEY1));
	}

	@Test
	public void testConstructor() {
		new SessionMaps();
	}

	@Test
	public void testContains() {
		Assert.assertFalse(
			"SessionMaps should not contain " + KEY1,
			SessionMaps.contains(httpSession, _MAP_KEY, KEY1));

		SessionMaps.add(httpSession, _MAP_KEY, KEY1, VALUE1);

		Assert.assertTrue(
			"SessionMaps should contain " + KEY1,
			SessionMaps.contains(httpSession, _MAP_KEY, KEY1));
		Assert.assertFalse(
			"SessionMaps should not contain " + KEY2,
			SessionMaps.contains(httpSession, _MAP_KEY, KEY2));
	}

	@Test
	public void testGet() {
		Assert.assertNull(SessionMaps.get(httpSession, _MAP_KEY, KEY1));

		SessionMaps.add(httpSession, _MAP_KEY, KEY1, VALUE1);

		Assert.assertEquals(
			VALUE1, SessionMaps.get(httpSession, _MAP_KEY, KEY1));
		Assert.assertNull(SessionMaps.get(httpSession, _MAP_KEY, KEY2));
	}

	@Test
	public void testInvalidatedSession() {
		SessionMaps.add(httpSession, _MAP_KEY, KEY1, VALUE1);

		httpSessionInvocationHandler.setInvalidated(true);

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				SessionMaps.class.getName(), LoggerTestUtil.ERROR)) {

			Assert.assertTrue(SessionMaps.isEmpty(httpSession, _MAP_KEY));

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 0, logEntries.size());

			logCapture.resetPriority(Level.FINE.toString());

			Assert.assertTrue(SessionMaps.isEmpty(httpSession, _MAP_KEY));

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
			SessionMaps.isEmpty(httpSession, _MAP_KEY));

		SessionMaps.add(httpSession, _MAP_KEY, KEY1, VALUE1);

		Assert.assertFalse(
			"The map should not be empty",
			SessionMaps.isEmpty(httpSession, _MAP_KEY));
	}

	@Test
	public void testIteratorAndKeySet() {
		Assert.assertEquals(
			Collections.emptySet(), SessionMaps.keySet(httpSession, _MAP_KEY));
		Assert.assertEquals(
			Collections.emptyIterator(),
			SessionMaps.iterator(httpSession, _MAP_KEY));

		Set<String> expectedKeys = new HashSet<>(
			Arrays.asList(KEY1, KEY2, KEY3));

		SessionMaps.add(httpSession, _MAP_KEY, KEY1, VALUE1);
		SessionMaps.add(httpSession, _MAP_KEY, KEY2, VALUE2);
		SessionMaps.add(httpSession, _MAP_KEY, KEY3, VALUE3);

		// Key set

		Assert.assertEquals(
			expectedKeys, SessionMaps.keySet(httpSession, _MAP_KEY));

		// Iterator

		Set<String> iteratorKeys = new HashSet<>();

		Iterator<String> iterator = SessionMaps.iterator(httpSession, _MAP_KEY);

		while (iterator.hasNext()) {
			iteratorKeys.add(iterator.next());
		}

		Assert.assertEquals(expectedKeys, iteratorKeys);
	}

	@Test
	public void testNullSession() {
		SessionMaps.add(null, _MAP_KEY, KEY1, VALUE1);

		Assert.assertNull(SessionMaps.get(null, _MAP_KEY, KEY1));
	}

	@Test
	public void testRemove() {
		SessionMaps.remove(httpSession, _MAP_KEY, KEY1);

		Assert.assertNull(SessionMaps.get(httpSession, _MAP_KEY, KEY1));

		SessionMaps.add(httpSession, _MAP_KEY, KEY1, VALUE1);

		Assert.assertEquals(
			VALUE1, SessionMaps.get(httpSession, _MAP_KEY, KEY1));

		SessionMaps.remove(httpSession, _MAP_KEY, KEY1);

		Assert.assertNull(SessionMaps.get(httpSession, _MAP_KEY, KEY1));
	}

	@Test
	public void testSize() {
		Assert.assertEquals(0, SessionMaps.size(httpSession, _MAP_KEY));

		SessionMaps.add(httpSession, _MAP_KEY, KEY1, VALUE1);

		Assert.assertEquals(1, SessionMaps.size(httpSession, _MAP_KEY));

		SessionMaps.clear(httpSession, _MAP_KEY);

		Assert.assertEquals(0, SessionMaps.size(httpSession, _MAP_KEY));
	}

	private static final String _MAP_KEY = SessionMapsTest.class.getName();

}