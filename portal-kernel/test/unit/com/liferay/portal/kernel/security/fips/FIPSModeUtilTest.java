/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.security.fips;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Jorge García Jiménez
 */
public class FIPSModeUtilTest {

	@Test
	public void testIsApprovedPasswordAlgorithmMixedCase() {
		Assert.assertTrue(FIPSModeUtil.isApprovedPasswordAlgorithm("sha-256"));
		Assert.assertTrue(FIPSModeUtil.isApprovedPasswordAlgorithm("pbkdf2"));
		Assert.assertTrue(
			FIPSModeUtil.isApprovedPasswordAlgorithm(
				"PBKDF2WithHmacSHA1/160/1300000"));
	}

	@Test
	public void testIsApprovedPasswordAlgorithmNullOrEmpty() {
		Assert.assertFalse(FIPSModeUtil.isApprovedPasswordAlgorithm(null));
		Assert.assertFalse(FIPSModeUtil.isApprovedPasswordAlgorithm(""));
	}

	@Test
	public void testIsApprovedPasswordAlgorithmRejected() {
		Assert.assertFalse(FIPSModeUtil.isApprovedPasswordAlgorithm("MD2"));
		Assert.assertFalse(FIPSModeUtil.isApprovedPasswordAlgorithm("MD5"));
		Assert.assertFalse(FIPSModeUtil.isApprovedPasswordAlgorithm("SHA"));
		Assert.assertFalse(FIPSModeUtil.isApprovedPasswordAlgorithm("SSHA"));
		Assert.assertFalse(FIPSModeUtil.isApprovedPasswordAlgorithm("BCRYPT"));
		Assert.assertFalse(
			FIPSModeUtil.isApprovedPasswordAlgorithm("BCRYPT/10"));
		Assert.assertFalse(
			FIPSModeUtil.isApprovedPasswordAlgorithm("UFC-CRYPT"));
		Assert.assertFalse(FIPSModeUtil.isApprovedPasswordAlgorithm("NONE"));
	}

	@Test
	public void testIsApprovedPasswordAlgorithmShaFamily() {
		Assert.assertTrue(FIPSModeUtil.isApprovedPasswordAlgorithm("SHA-256"));
		Assert.assertTrue(FIPSModeUtil.isApprovedPasswordAlgorithm("SHA-384"));
		Assert.assertTrue(FIPSModeUtil.isApprovedPasswordAlgorithm("SHA-512"));
	}

}