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
public class CheckResultTest extends com.liferay.jenkins.results.parser.Test {

	@Test
	public void testGetMetricsIsUnmodifiable() {
		Map<String, String> metrics = Collections.singletonMap("disk", "88");

		CheckResult checkResult = new CheckResult(
			CheckResult.Status.WARN, "disk high", metrics, 1L);

		Map<String, String> returnedMetrics = checkResult.getMetrics();

		try {
			returnedMetrics.put("cpu", "50");

			Assert.fail("Expected UnsupportedOperationException");
		}
		catch (UnsupportedOperationException unsupportedOperationException) {
		}
	}

	@Test
	public void testGetMetricsNullYieldsEmpty() {
		CheckResult checkResult = new CheckResult(
			CheckResult.Status.OK, "ok", null, 1L);

		Map<String, String> metrics = checkResult.getMetrics();

		Assert.assertTrue(metrics.isEmpty());
	}

	@Test
	public void testGetMostSevere() {
		Assert.assertEquals(
			CheckResult.Status.UNKNOWN,
			CheckResult.Status.getMostSevere(
				Collections.<CheckResult.Status>emptyList()));

		Assert.assertEquals(
			CheckResult.Status.OK,
			CheckResult.Status.getMostSevere(
				Arrays.asList(CheckResult.Status.OK, CheckResult.Status.OK)));

		Assert.assertEquals(
			CheckResult.Status.UNKNOWN,
			CheckResult.Status.getMostSevere(
				Arrays.asList(
					CheckResult.Status.OK, CheckResult.Status.UNKNOWN)));

		Assert.assertEquals(
			CheckResult.Status.CRITICAL,
			CheckResult.Status.getMostSevere(
				Arrays.asList(
					CheckResult.Status.OK, CheckResult.Status.WARN,
					CheckResult.Status.CRITICAL)));
	}

}