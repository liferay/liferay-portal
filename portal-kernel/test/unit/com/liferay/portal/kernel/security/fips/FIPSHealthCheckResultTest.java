/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.security.fips;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Lucas Miranda
 */
public class FIPSHealthCheckResultTest {

	@Test
	public void testFailed() {
		FIPSHealthCheckResult result = FIPSHealthCheckResult.failed(
			"BCFIPS", "AES-KAT", "ERROR", "boom");

		Assert.assertEquals(
			FIPSHealthCheckResult.Status.FAILED, result.getStatus());
		Assert.assertEquals("BCFIPS", result.getProviderName());
		Assert.assertEquals("AES-KAT", result.getFailedTest());
		Assert.assertEquals("ERROR", result.getFipsState());
		Assert.assertEquals("boom", result.getProviderMessage());
	}

	@Test
	public void testHealthy() {
		FIPSHealthCheckResult result = FIPSHealthCheckResult.healthy("BCFIPS");

		Assert.assertEquals(
			FIPSHealthCheckResult.Status.HEALTHY, result.getStatus());
		Assert.assertEquals("BCFIPS", result.getProviderName());
		Assert.assertNull(result.getFailedTest());
	}

	@Test
	public void testNotApplicable() {
		FIPSHealthCheckResult result = FIPSHealthCheckResult.notApplicable();

		Assert.assertEquals(
			FIPSHealthCheckResult.Status.NOT_APPLICABLE, result.getStatus());
	}

}