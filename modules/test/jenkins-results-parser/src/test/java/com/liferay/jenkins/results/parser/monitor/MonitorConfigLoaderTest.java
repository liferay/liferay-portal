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

		buildProperties.setProperty("monitor[masters].type", "http-endpoint");
		buildProperties.setProperty("monitor[masters].severity", "high");
		buildProperties.setProperty("monitor[masters].cadence", "900");
		buildProperties.setProperty(
			"monitor[masters].parameter[target]",
			"https://test-1-0.liferay.com/computer/api/json");
		buildProperties.setProperty("monitor[masters].threshold[disk]", "85");

		List<MonitorConfig> monitorConfigs =
			MonitorConfigLoader.getMonitorConfigs(buildProperties);

		Assert.assertEquals(
			monitorConfigs.toString(), 1, monitorConfigs.size());

		MonitorConfig monitorConfig = monitorConfigs.get(0);

		testEquals("masters", monitorConfig.getID());
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
	public void testGetMonitorConfigsInvalidCadenceFailsLoud() {
		Properties buildProperties = new Properties();

		buildProperties.setProperty("monitor[a].type", "http-endpoint");
		buildProperties.setProperty("monitor[a].cadence", "not-a-number");

		try {
			MonitorConfigLoader.getMonitorConfigs(buildProperties);

			Assert.fail("Expected IllegalArgumentException");
		}
		catch (IllegalArgumentException illegalArgumentException) {
		}
	}

	@Test
	public void testGetMonitorConfigsInvalidSeverityFailsLoud() {
		Properties buildProperties = new Properties();

		buildProperties.setProperty("monitor[a].type", "http-endpoint");
		buildProperties.setProperty("monitor[a].severity", "bogus");

		try {
			MonitorConfigLoader.getMonitorConfigs(buildProperties);

			Assert.fail("Expected IllegalArgumentException");
		}
		catch (IllegalArgumentException illegalArgumentException) {
		}
	}

	@Test
	public void testGetMonitorConfigsMissingTypeFailsLoud() {
		Properties buildProperties = new Properties();

		buildProperties.setProperty("monitor[a].severity", "high");

		try {
			MonitorConfigLoader.getMonitorConfigs(buildProperties);

			Assert.fail("Expected IllegalArgumentException");
		}
		catch (IllegalArgumentException illegalArgumentException) {
		}
	}

}