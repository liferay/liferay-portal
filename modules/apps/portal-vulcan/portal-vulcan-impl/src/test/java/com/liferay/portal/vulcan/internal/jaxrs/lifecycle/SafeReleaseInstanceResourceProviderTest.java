/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.jaxrs.lifecycle;

import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.cxf.jaxrs.lifecycle.ResourceProvider;
import org.apache.cxf.message.Message;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Shuyang Zhou
 */
public class SafeReleaseInstanceResourceProviderTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testReleaseInstanceIgnoresUnknownInstance() {
		AtomicInteger releaseCounter = new AtomicInteger();

		SafeReleaseInstanceResourceProvider
			safeReleaseInstanceResourceProvider =
				new SafeReleaseInstanceResourceProvider(
					new TestResourceProvider(null, releaseCounter));

		safeReleaseInstanceResourceProvider.releaseInstance(null, new Object());

		Assert.assertEquals(0, releaseCounter.get());
	}

	@Test
	public void testReleaseInstanceReleasesOnce() {
		AtomicInteger releaseCounter = new AtomicInteger();

		SafeReleaseInstanceResourceProvider
			safeReleaseInstanceResourceProvider =
				new SafeReleaseInstanceResourceProvider(
					new TestResourceProvider(null, releaseCounter));

		Object instance = safeReleaseInstanceResourceProvider.getInstance(null);

		safeReleaseInstanceResourceProvider.releaseInstance(null, instance);
		safeReleaseInstanceResourceProvider.releaseInstance(null, instance);

		Assert.assertEquals(1, releaseCounter.get());
	}

	@Test
	public void testReleaseInstanceToleratesUnregisteredRegistration() {
		IllegalArgumentException illegalArgumentException =
			new IllegalArgumentException(
				"The service parameter was not provided by this object");

		AtomicInteger releaseCounter = new AtomicInteger();

		SafeReleaseInstanceResourceProvider
			safeReleaseInstanceResourceProvider =
				new SafeReleaseInstanceResourceProvider(
					new TestResourceProvider(
						illegalArgumentException, releaseCounter));

		Object instance = safeReleaseInstanceResourceProvider.getInstance(null);

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				SafeReleaseInstanceResourceProvider.class.getName(),
				LoggerTestUtil.DEBUG)) {

			safeReleaseInstanceResourceProvider.releaseInstance(null, instance);

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			Assert.assertEquals(
				"The service parameter was not provided by this object",
				logEntry.getMessage());
			Assert.assertSame(
				illegalArgumentException, logEntry.getThrowable());
		}

		Assert.assertEquals(1, releaseCounter.get());
	}

	private static class TestResourceProvider implements ResourceProvider {

		public TestResourceProvider(
			IllegalArgumentException illegalArgumentException,
			AtomicInteger releaseCounter) {

			_illegalArgumentException = illegalArgumentException;
			_releaseCounter = releaseCounter;
		}

		@Override
		public Object getInstance(Message message) {
			return new Object();
		}

		@Override
		public Class<?> getResourceClass() {
			return Object.class;
		}

		@Override
		public boolean isSingleton() {
			return false;
		}

		@Override
		public void releaseInstance(Message message, Object object) {
			_releaseCounter.incrementAndGet();

			if (_illegalArgumentException != null) {
				throw _illegalArgumentException;
			}
		}

		private final IllegalArgumentException _illegalArgumentException;
		private final AtomicInteger _releaseCounter;

	}

}