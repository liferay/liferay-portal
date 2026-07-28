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

		Assert.assertEquals("AES-KAT", result.getFailedTest());
		Assert.assertEquals("ERROR", result.getFipsState());
		Assert.assertEquals("boom", result.getProviderMessage());
		Assert.assertEquals("BCFIPS", result.getProviderName());
		Assert.assertEquals(FIPSHealthCheckStatus.FAILED, result.getStatus());
	}

	@Test
	public void testHealthy() {
		FIPSHealthCheckResult result = FIPSHealthCheckResult.healthy("BCFIPS");

		Assert.assertNull(result.getFailedTest());
		Assert.assertEquals("BCFIPS", result.getProviderName());
		Assert.assertEquals(FIPSHealthCheckStatus.HEALTHY, result.getStatus());
	}

	@Test
	public void testInProgress() {
		FIPSHealthCheckResult result = FIPSHealthCheckResult.inProgress();

		Assert.assertEquals(
			FIPSHealthCheckStatus.IN_PROGRESS, result.getStatus());
	}

	@Test
	public void testNotApplicable() {
		FIPSHealthCheckResult result = FIPSHealthCheckResult.notApplicable();

		Assert.assertEquals(
			FIPSHealthCheckStatus.NOT_APPLICABLE, result.getStatus());
	}

}