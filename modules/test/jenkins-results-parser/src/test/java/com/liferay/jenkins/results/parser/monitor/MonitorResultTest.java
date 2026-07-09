/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.monitor;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Brittney Nguyen
 */
public class MonitorResultTest extends com.liferay.jenkins.results.parser.Test {

	@Test
	public void testGetMetricsIsUnmodifiable() {
		Map<String, String> metrics = Collections.singletonMap("disk", "88");

		MonitorResult monitorResult = new MonitorResult(
			"disk high", metrics, MonitorResult.Status.WARN, 1L);

		Map<String, String> returnedMetrics = monitorResult.getMetrics();

		try {
			returnedMetrics.put("cpu", "50");

			Assert.fail("Expected UnsupportedOperationException");
		}
		catch (UnsupportedOperationException unsupportedOperationException) {
		}
	}

	@Test
	public void testGetMetricsNullYieldsEmpty() {
		MonitorResult monitorResult = new MonitorResult(
			"ok", null, MonitorResult.Status.OK, 1L);

		Map<String, String> metrics = monitorResult.getMetrics();

		Assert.assertTrue(metrics.isEmpty());
	}

	@Test
	public void testGetMostSevere() {
		testEquals(
			MonitorResult.Status.CRITICAL,
			MonitorResult.Status.getMostSevere(
				Arrays.asList(
					MonitorResult.Status.OK, MonitorResult.Status.WARN,
					MonitorResult.Status.CRITICAL)));
		testEquals(
			MonitorResult.Status.OK,
			MonitorResult.Status.getMostSevere(
				Arrays.asList(
					MonitorResult.Status.OK, MonitorResult.Status.OK)));
		testEquals(
			MonitorResult.Status.UNKNOWN,
			MonitorResult.Status.getMostSevere(
				Arrays.asList(
					MonitorResult.Status.OK, MonitorResult.Status.UNKNOWN)));
		testEquals(
			MonitorResult.Status.UNKNOWN,
			MonitorResult.Status.getMostSevere(
				Collections.<MonitorResult.Status>emptyList()));
	}

}