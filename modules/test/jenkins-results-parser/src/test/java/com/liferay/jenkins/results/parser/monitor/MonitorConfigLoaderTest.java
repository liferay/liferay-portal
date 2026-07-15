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

		buildProperties.setProperty("monitor[masters].interval", "900");
		buildProperties.setProperty(
			"monitor[masters].parameter[target]",
			"https://test-1-0.liferay.com/computer/api/json");
		buildProperties.setProperty("monitor[masters].severity", "high");
		buildProperties.setProperty("monitor[masters].threshold[disk]", "85");
		buildProperties.setProperty("monitor[masters].timeout", "30");
		buildProperties.setProperty("monitor[masters].type", "http-endpoint");

		List<MonitorConfig> monitorConfigs =
			MonitorConfigLoader.getMonitorConfigs(buildProperties);

		Assert.assertEquals(
			monitorConfigs.toString(), 1, monitorConfigs.size());

		MonitorConfig monitorConfig = monitorConfigs.get(0);

		testEquals("masters", monitorConfig.getId());
		testEquals("http-endpoint", monitorConfig.getType());
		testEquals(MonitorConfig.Severity.HIGH, monitorConfig.getSeverity());
		testEquals(900L, monitorConfig.getIntervalSeconds());
		testEquals(30L, monitorConfig.getTimeout());

		Map<String, String> parameters = monitorConfig.getParameters();

		testEquals(
			"https://test-1-0.liferay.com/computer/api/json",
			parameters.get("target"));

		Map<String, String> thresholds = monitorConfig.getThresholds();

		testEquals("85", thresholds.get("disk"));
	}

	@Test
	public void testGetMonitorConfigsDefaultSeverity() {
		Properties buildProperties = new Properties();

		buildProperties.setProperty("monitor[a].type", "http-endpoint");

		List<MonitorConfig> monitorConfigs =
			MonitorConfigLoader.getMonitorConfigs(buildProperties);

		MonitorConfig monitorConfig = monitorConfigs.get(0);

		testEquals(MonitorConfig.Severity.MEDIUM, monitorConfig.getSeverity());
	}

	@Test
	public void testGetMonitorConfigsDefaultTimeout() {
		Properties buildProperties = new Properties();

		buildProperties.setProperty("monitor[a].type", "http-endpoint");

		List<MonitorConfig> monitorConfigs =
			MonitorConfigLoader.getMonitorConfigs(buildProperties);

		MonitorConfig monitorConfig = monitorConfigs.get(0);

		testEquals(60L, monitorConfig.getTimeout());
	}

	@Test
	public void testGetMonitorConfigsInterval() {
		Properties buildProperties = new Properties();

		buildProperties.setProperty("monitor[a].interval", "not-a-number");
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
	public void testGetMonitorConfigsNegativeInterval() {
		Properties buildProperties = new Properties();

		buildProperties.setProperty("monitor[a].interval", "-1");
		buildProperties.setProperty("monitor[a].type", "http-endpoint");

		_testGetMonitorConfigsExpectedIllegalArgumentException(buildProperties);
	}

	@Test
	public void testGetMonitorConfigsNegativeTimeout() {
		Properties buildProperties = new Properties();

		buildProperties.setProperty("monitor[a].timeout", "-1");
		buildProperties.setProperty("monitor[a].type", "http-endpoint");

		_testGetMonitorConfigsExpectedIllegalArgumentException(buildProperties);
	}

	@Test
	public void testGetMonitorConfigsSeverity() {
		Properties buildProperties = new Properties();

		buildProperties.setProperty("monitor[a].severity", "bogus");
		buildProperties.setProperty("monitor[a].type", "http-endpoint");

		_testGetMonitorConfigsExpectedIllegalArgumentException(buildProperties);
	}

	@Test
	public void testGetMonitorConfigsTimeout() {
		Properties buildProperties = new Properties();

		buildProperties.setProperty("monitor[a].timeout", "not-a-number");
		buildProperties.setProperty("monitor[a].type", "http-endpoint");

		_testGetMonitorConfigsExpectedIllegalArgumentException(buildProperties);
	}

	@Test
	public void testGetMonitorConfigsZeroInterval() {
		Properties buildProperties = new Properties();

		buildProperties.setProperty("monitor[a].interval", "0");
		buildProperties.setProperty("monitor[a].type", "http-endpoint");

		List<MonitorConfig> monitorConfigs =
			MonitorConfigLoader.getMonitorConfigs(buildProperties);

		MonitorConfig monitorConfig = monitorConfigs.get(0);

		testEquals(0L, monitorConfig.getIntervalSeconds());
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