/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.monitor;

import com.liferay.jenkins.results.parser.JenkinsMasterTestUtil;
import com.liferay.jenkins.results.parser.RandomTestUtil;

import java.util.List;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Brittney Nguyen
 */
public class MonitorFactoryTest
	extends com.liferay.jenkins.results.parser.Test {

	@Test
	public void testNewMonitorHTTPEndpoint() {
		Properties monitorProperties = new Properties();

		monitorProperties.setProperty(
			"monitor[a].parameter[url]",
			"https://" + RandomTestUtil.randomString());
		monitorProperties.setProperty("monitor[a].type", "http-endpoint");

		List<MonitorConfig> monitorConfigs =
			MonitorConfigLoader.getMonitorConfigs(monitorProperties);

		Monitor monitor = MonitorFactory.newMonitor(monitorConfigs.get(0));

		Assert.assertTrue(monitor instanceof HTTPEndpointMonitor);
	}

	@Test
	public void testNewMonitorJobHealth() {
		String masterName = RandomTestUtil.randomString();

		JenkinsMasterTestUtil.getJenkinsMaster(
			masterName, "http://" + masterName);

		Properties monitorProperties = new Properties();

		monitorProperties.setProperty(
			"monitor[a].parameter[job.name]", RandomTestUtil.randomString());
		monitorProperties.setProperty(
			"monitor[a].parameter[master.name]", masterName);
		monitorProperties.setProperty("monitor[a].type", "job-health");

		List<MonitorConfig> monitorConfigs =
			MonitorConfigLoader.getMonitorConfigs(monitorProperties);

		Monitor monitor = MonitorFactory.newMonitor(monitorConfigs.get(0));

		Assert.assertTrue(monitor instanceof JobHealthMonitor);
	}

	@Test
	public void testNewMonitorNullType() {
		_testNewMonitorExpectedIllegalArgumentException(
			new MonitorConfig(
				"a", 0, null, MonitorConfig.Severity.MEDIUM, null, 60, null));
	}

	@Test
	public void testNewMonitorReportFreshness() {
		Properties monitorProperties = new Properties();

		monitorProperties.setProperty("monitor[a].parameter[cadence]", "3600");
		monitorProperties.setProperty(
			"monitor[a].parameter[report.name]", RandomTestUtil.randomString());
		monitorProperties.setProperty(
			"monitor[a].parameter[url]",
			"file:///" + RandomTestUtil.randomString());
		monitorProperties.setProperty("monitor[a].type", "report-freshness");

		List<MonitorConfig> monitorConfigs =
			MonitorConfigLoader.getMonitorConfigs(monitorProperties);

		Monitor monitor = MonitorFactory.newMonitor(monitorConfigs.get(0));

		Assert.assertTrue(monitor instanceof ReportFreshnessMonitor);
	}

	@Test
	public void testNewMonitorResourceThreshold() {
		String masterName = RandomTestUtil.randomString();

		JenkinsMasterTestUtil.getJenkinsMaster(
			masterName, "http://" + masterName);

		Properties monitorProperties = new Properties();

		monitorProperties.setProperty(
			"monitor[a].parameter[master.name]", masterName);
		monitorProperties.setProperty("monitor[a].parameter[metric]", "ram");
		monitorProperties.setProperty("monitor[a].threshold[warn]", "80");
		monitorProperties.setProperty("monitor[a].type", "resource-threshold");

		List<MonitorConfig> monitorConfigs =
			MonitorConfigLoader.getMonitorConfigs(monitorProperties);

		Monitor monitor = MonitorFactory.newMonitor(monitorConfigs.get(0));

		Assert.assertTrue(monitor instanceof ResourceThresholdMonitor);
	}

	@Test
	public void testNewMonitorUnknownType() {
		_testNewMonitorExpectedIllegalArgumentException(
			new MonitorConfig(
				"a", 0, null, MonitorConfig.Severity.MEDIUM, null, 60,
				"unknown-type"));
	}

	private void _testNewMonitorExpectedIllegalArgumentException(
		MonitorConfig monitorConfig) {

		try {
			MonitorFactory.newMonitor(monitorConfig);

			Assert.fail("Expected IllegalArgumentException");
		}
		catch (IllegalArgumentException illegalArgumentException) {
		}
	}

}