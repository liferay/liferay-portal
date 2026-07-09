/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.monitor;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Brittney Nguyen
 */
public class MonitorConfigLoaderTest
	extends com.liferay.jenkins.results.parser.Test {

	@Test
	public void testGetMonitorConfigs() {
		Properties buildProperties = new Properties();

		buildProperties.setProperty("monitor[masters].cadence", "900");
		buildProperties.setProperty(
			"monitor[masters].parameter[target]",
			"https://test-1-0.liferay.com/computer/api/json");
		buildProperties.setProperty("monitor[masters].severity", "high");
		buildProperties.setProperty("monitor[masters].threshold[disk]", "85");
		buildProperties.setProperty("monitor[masters].type", "http-endpoint");

		List<MonitorConfig> monitorConfigs =
			MonitorConfigLoader.getMonitorConfigs(buildProperties);

		Assert.assertEquals(
			monitorConfigs.toString(), 1, monitorConfigs.size());

		MonitorConfig monitorConfig = monitorConfigs.get(0);

		testEquals("masters", monitorConfig.getId());
		testEquals("http-endpoint", monitorConfig.getType());
		testEquals(MonitorConfig.Severity.HIGH, monitorConfig.getSeverity());
		testEquals(900L, monitorConfig.getCadence());

		Map<String, String> parameters = monitorConfig.getParameters();

		testEquals(
			"https://test-1-0.liferay.com/computer/api/json",
			parameters.get("target"));

		Map<String, String> thresholds = monitorConfig.getThresholds();

		testEquals("85", thresholds.get("disk"));
	}

	@Test
	public void testGetMonitorConfigsCadence() {
		Properties buildProperties = new Properties();

		buildProperties.setProperty("monitor[a].cadence", "not-a-number");
		buildProperties.setProperty("monitor[a].type", "http-endpoint");

		_testGetMonitorConfigsExpectedIllegalArgumentException(buildProperties);
	}

	@Test
	public void testGetMonitorConfigsMissingType() {
		Properties buildProperties = new Properties();

		buildProperties.setProperty("monitor[a].severity", "high");

		_testGetMonitorConfigsExpectedIllegalArgumentException(buildProperties);
	}

	@Test
	public void testGetMonitorConfigsSeverity() {
		Properties buildProperties = new Properties();

		buildProperties.setProperty("monitor[a].severity", "bogus");
		buildProperties.setProperty("monitor[a].type", "http-endpoint");

		_testGetMonitorConfigsExpectedIllegalArgumentException(buildProperties);
	}

	private void _testGetMonitorConfigsExpectedIllegalArgumentException(
		Properties buildProperties) {

		try {
			MonitorConfigLoader.getMonitorConfigs(buildProperties);

			Assert.fail("Expected IllegalArgumentException");
		}
		catch (IllegalArgumentException illegalArgumentException) {
		}
	}

}