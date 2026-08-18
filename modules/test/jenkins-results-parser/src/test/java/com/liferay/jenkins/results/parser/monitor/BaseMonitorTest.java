/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.monitor;

import com.liferay.jenkins.results.parser.RandomTestUtil;

import org.junit.Test;

/**
 * @author Brittney Nguyen
 */
public class BaseMonitorTest extends com.liferay.jenkins.results.parser.Test {

	@Test
	public void testGetAttemptTimeoutMillis() {
		BaseMonitor baseMonitor = _newBaseMonitor(60);

		testEquals(20000, baseMonitor.getAttemptTimeoutMillis());
	}

	@Test
	public void testGetAttemptTimeoutMillisClampedTimeout() {
		BaseMonitor baseMonitor = _newBaseMonitor(Long.MAX_VALUE);

		testEquals(715827666, baseMonitor.getAttemptTimeoutMillis());
	}

	@Test
	public void testGetAttemptTimeoutMillisNonPositiveTimeout() {
		BaseMonitor baseMonitor = _newBaseMonitor(0);

		testEquals(20000, baseMonitor.getAttemptTimeoutMillis());
	}

	@Test
	public void testGetSingleAttemptTimeoutMillis() {
		BaseMonitor baseMonitor = _newBaseMonitor(10);

		testEquals(4500, baseMonitor.getSingleAttemptTimeoutMillis());
	}

	@Test
	public void testGetSingleAttemptTimeoutMillisClampedTimeout() {
		BaseMonitor baseMonitor = _newBaseMonitor(Long.MAX_VALUE);

		testEquals(966367350, baseMonitor.getSingleAttemptTimeoutMillis());
	}

	private BaseMonitor _newBaseMonitor(long timeoutSeconds) {
		return new BaseMonitor(
			new MonitorConfig(
				RandomTestUtil.randomString(), 0, null,
				MonitorConfig.Severity.MEDIUM, null, timeoutSeconds,
				RandomTestUtil.randomString())) {

			@Override
			public MonitorResult execute() {
				return null;
			}

		};
	}

}