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
public class CheckConfigLoaderTest
	extends com.liferay.jenkins.results.parser.Test {

	@Test
	public void testGetCheckConfigs() {
		Properties buildProperties = new Properties();

		buildProperties.setProperty(
			"monitor.check[masters].type", "http-endpoint");
		buildProperties.setProperty("monitor.check[masters].severity", "high");
		buildProperties.setProperty("monitor.check[masters].cadence", "900");
		buildProperties.setProperty(
			"monitor.check[masters].parameter[target]",
			"https://test-1-0.liferay.com/computer/api/json");
		buildProperties.setProperty(
			"monitor.check[masters].threshold[disk]", "85");

		List<CheckConfig> checkConfigs = CheckConfigLoader.getCheckConfigs(
			buildProperties);

		Assert.assertEquals(checkConfigs.toString(), 1, checkConfigs.size());

		CheckConfig checkConfig = checkConfigs.get(0);

		Assert.assertEquals("masters", checkConfig.getID());
		Assert.assertEquals("http-endpoint", checkConfig.getType());
		Assert.assertEquals(
			CheckConfig.Severity.HIGH, checkConfig.getSeverity());
		Assert.assertEquals(900L, checkConfig.getCadence());

		Map<String, String> parameters = checkConfig.getParameters();

		Assert.assertEquals(
			"https://test-1-0.liferay.com/computer/api/json",
			parameters.get("target"));

		Map<String, String> thresholds = checkConfig.getThresholds();

		Assert.assertEquals("85", thresholds.get("disk"));
	}

	@Test
	public void testGetCheckConfigsInvalidCadenceFailsLoud() {
		Properties buildProperties = new Properties();

		buildProperties.setProperty("monitor.check[a].type", "http-endpoint");
		buildProperties.setProperty("monitor.check[a].cadence", "not-a-number");

		try {
			CheckConfigLoader.getCheckConfigs(buildProperties);

			Assert.fail("Expected IllegalArgumentException");
		}
		catch (IllegalArgumentException illegalArgumentException) {
		}
	}

	@Test
	public void testGetCheckConfigsInvalidSeverityFailsLoud() {
		Properties buildProperties = new Properties();

		buildProperties.setProperty("monitor.check[a].type", "http-endpoint");
		buildProperties.setProperty("monitor.check[a].severity", "bogus");

		try {
			CheckConfigLoader.getCheckConfigs(buildProperties);

			Assert.fail("Expected IllegalArgumentException");
		}
		catch (IllegalArgumentException illegalArgumentException) {
		}
	}

	@Test
	public void testGetCheckConfigsMissingTypeFailsLoud() {
		Properties buildProperties = new Properties();

		buildProperties.setProperty("monitor.check[a].severity", "high");

		try {
			CheckConfigLoader.getCheckConfigs(buildProperties);

			Assert.fail("Expected IllegalArgumentException");
		}
		catch (IllegalArgumentException illegalArgumentException) {
		}
	}

}