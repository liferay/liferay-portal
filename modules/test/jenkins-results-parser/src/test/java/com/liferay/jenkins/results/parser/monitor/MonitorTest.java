/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.monitor;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Brittney Nguyen
 */
public class MonitorTest extends com.liferay.jenkins.results.parser.Test {

	@Test
	public void testExecute() {
		MonitorConfig monitorConfig = new MonitorConfig(
			"masters", "http-endpoint", MonitorConfig.Severity.HIGH, 900, null,
			null);

		MonitorResult monitorResult = new MonitorResult(
			MonitorResult.Status.OK, "ok", null, 1L);

		Monitor monitor = new Monitor() {

			@Override
			public MonitorResult execute() {
				return monitorResult;
			}

			@Override
			public String getID() {
				return monitorConfig.getID();
			}

			@Override
			public MonitorConfig getMonitorConfig() {
				return monitorConfig;
			}

		};

		Assert.assertSame(monitorResult, monitor.execute());
		Assert.assertEquals("masters", monitor.getID());
		Assert.assertSame(monitorConfig, monitor.getMonitorConfig());
	}

}