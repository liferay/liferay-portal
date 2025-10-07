/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.internal.increment;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.CodeCoverageAssertor;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.List;
import java.util.Objects;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Shuyang Zhou
 */
public class BufferedIncrementConfigurationTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			CodeCoverageAssertor.INSTANCE, LiferayUnitTestRule.INSTANCE);

	@Test
	public void testInvalidSettingWithLog() {
		try (LogCapture logCapture = _testInvalidSetting(LoggerTestUtil.WARN)) {
			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 2, logEntries.size());

			LogEntry logEntry1 = logEntries.get(0);

			Assert.assertEquals(
				PropsKeys.BUFFERED_INCREMENT_THREADPOOL_KEEP_ALIVE_TIME +
					"[]=-3. Auto reset to 0.",
				logEntry1.getMessage());

			LogEntry logEntry2 = logEntries.get(1);

			Assert.assertEquals(
				PropsKeys.BUFFERED_INCREMENT_THREADPOOL_MAX_SIZE +
					"[]=-4. Auto reset to 1.",
				logEntry2.getMessage());
		}
	}

	@Test
	public void testInvalidSettingWithoutLog() {
		try (LogCapture logCapture = _testInvalidSetting(LoggerTestUtil.OFF)) {
			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertTrue(logEntries.toString(), logEntries.isEmpty());
		}
	}

	@Test
	public void testValidSetting() {
		PropsUtil.set(
			PropsKeys.BUFFERED_INCREMENT_STANDBY_QUEUE_THRESHOLD, "10");
		PropsUtil.set(
			PropsKeys.BUFFERED_INCREMENT_STANDBY_TIME_UPPER_LIMIT, "20");
		PropsUtil.set(
			PropsKeys.BUFFERED_INCREMENT_THREADPOOL_KEEP_ALIVE_TIME, "30");
		PropsUtil.set(PropsKeys.BUFFERED_INCREMENT_THREADPOOL_MAX_SIZE, "40");

		BufferedIncrementConfiguration bufferedIncrementConfiguration =
			new BufferedIncrementConfiguration(StringPool.BLANK);

		Assert.assertEquals(
			10, bufferedIncrementConfiguration.getStandbyQueueThreshold());
		Assert.assertEquals(
			20, bufferedIncrementConfiguration.getStandbyTimeUpperLimit());
		Assert.assertEquals(
			30, bufferedIncrementConfiguration.getThreadpoolKeepAliveTime());
		Assert.assertEquals(
			40, bufferedIncrementConfiguration.getThreadpoolMaxSize());
		Assert.assertTrue(bufferedIncrementConfiguration.isStandbyEnabled());

		try {
			bufferedIncrementConfiguration.calculateStandbyTime(-1);
		}
		catch (IllegalArgumentException illegalArgumentException) {
			Assert.assertEquals(
				"Negative queue length -1",
				illegalArgumentException.getMessage());
		}

		int standbyQueueThreshold =
			bufferedIncrementConfiguration.getStandbyQueueThreshold();
		long standbyTimeUpperLimit =
			bufferedIncrementConfiguration.getStandbyTimeUpperLimit();

		long standbyTime = bufferedIncrementConfiguration.calculateStandbyTime(
			0);

		Assert.assertEquals(standbyTimeUpperLimit * 1000, standbyTime);

		standbyTime = bufferedIncrementConfiguration.calculateStandbyTime(
			standbyQueueThreshold / 2);

		Assert.assertEquals(standbyTimeUpperLimit * 1000 / 2, standbyTime);

		standbyTime = bufferedIncrementConfiguration.calculateStandbyTime(
			standbyQueueThreshold);

		Assert.assertEquals(0, standbyTime);

		standbyTime = bufferedIncrementConfiguration.calculateStandbyTime(
			standbyQueueThreshold + 1);

		Assert.assertEquals(0, standbyTime);

		standbyTime = bufferedIncrementConfiguration.calculateStandbyTime(
			standbyQueueThreshold * 2);

		Assert.assertEquals(0, standbyTime);
	}

	private LogCapture _testInvalidSetting(String level) {
		if (Objects.equals(LoggerTestUtil.OFF, level)) {
			PropsUtil.set(
				PropsKeys.BUFFERED_INCREMENT_STANDBY_QUEUE_THRESHOLD, "1");
			PropsUtil.set(
				PropsKeys.BUFFERED_INCREMENT_STANDBY_TIME_UPPER_LIMIT, "-1");
		}
		else {
			PropsUtil.set(
				PropsKeys.BUFFERED_INCREMENT_STANDBY_QUEUE_THRESHOLD, "-1");
			PropsUtil.set(
				PropsKeys.BUFFERED_INCREMENT_STANDBY_TIME_UPPER_LIMIT, "1");
		}

		PropsUtil.set(
			PropsKeys.BUFFERED_INCREMENT_THREADPOOL_KEEP_ALIVE_TIME, "-3");
		PropsUtil.set(PropsKeys.BUFFERED_INCREMENT_THREADPOOL_MAX_SIZE, "-4");

		LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
			BufferedIncrementConfiguration.class.getName(), level);

		BufferedIncrementConfiguration bufferedIncrementConfiguration =
			new BufferedIncrementConfiguration(StringPool.BLANK);

		if (Objects.equals(LoggerTestUtil.OFF, level)) {
			Assert.assertEquals(
				1, bufferedIncrementConfiguration.getStandbyQueueThreshold());
			Assert.assertEquals(
				-1, bufferedIncrementConfiguration.getStandbyTimeUpperLimit());
		}
		else {
			Assert.assertEquals(
				-1, bufferedIncrementConfiguration.getStandbyQueueThreshold());
			Assert.assertEquals(
				1, bufferedIncrementConfiguration.getStandbyTimeUpperLimit());
		}

		Assert.assertEquals(
			0, bufferedIncrementConfiguration.getThreadpoolKeepAliveTime());
		Assert.assertEquals(
			1, bufferedIncrementConfiguration.getThreadpoolMaxSize());
		Assert.assertFalse(bufferedIncrementConfiguration.isStandbyEnabled());

		try {
			bufferedIncrementConfiguration.calculateStandbyTime(0);
		}
		catch (IllegalStateException illegalStateException) {
			Assert.assertEquals(
				"Standby is disabled", illegalStateException.getMessage());
		}

		return logCapture;
	}

}