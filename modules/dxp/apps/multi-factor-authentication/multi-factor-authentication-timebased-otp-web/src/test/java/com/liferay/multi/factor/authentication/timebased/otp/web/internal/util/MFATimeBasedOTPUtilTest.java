/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.multi.factor.authentication.timebased.otp.web.internal.util;

import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.FIPSModeTestUtil;
import com.liferay.portal.kernel.test.util.PropsValuesTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import javax.crypto.Mac;

import jodd.util.Base32;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Lucas Miranda
 */
public class MFATimeBasedOTPUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testGenerateSharedSecret() throws Exception {
		Assert.assertEquals(
			10,
			Base32.decode(MFATimeBasedOTPUtil.generateSharedSecret(10)).length);

		try (SafeCloseable safeCloseable =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"FIPS_ENABLED", true)) {

			Assert.assertEquals(
				MFATimeBasedOTPUtil.
					MFA_TIMEBASED_OTP_FIPS_ALGORITHM_KEY_SIZE_MIN,
				Base32.decode(
					MFATimeBasedOTPUtil.generateSharedSecret(10)).length);
			Assert.assertEquals(
				20,
				Base32.decode(
					MFATimeBasedOTPUtil.generateSharedSecret(20)).length);
		}
	}

	@Test
	public void testGetAlgorithm() throws Exception {
		String sharedSecret = MFATimeBasedOTPUtil.generateSharedSecret(20);

		FIPSModeTestUtil.assertAlgorithmSwitch(
			"HmacSHA1", Mac.class, "HmacSHA256", Mac::getInstance,
			() -> _generateCurrentOTP(
				MFATimeBasedOTPUtil.getAlgorithm(), sharedSecret));
	}

	@Test
	public void testGetAlgorithms() throws Exception {
		Assert.assertArrayEquals(
			new String[] {"HmacSHA1", "HmacSHA256"},
			MFATimeBasedOTPUtil.getAlgorithms());

		try (SafeCloseable safeCloseable =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"FIPS_ENABLED", true)) {

			Assert.assertArrayEquals(
				new String[] {"HmacSHA256", "HmacSHA1"},
				MFATimeBasedOTPUtil.getAlgorithms());
		}
	}

	@Test
	public void testVerifyTimeBasedOTP() throws Exception {
		String sharedSecret = MFATimeBasedOTPUtil.generateSharedSecret(20);

		try (SafeCloseable safeCloseable =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"FIPS_ENABLED", true)) {

			Assert.assertTrue(
				MFATimeBasedOTPUtil.verifyTimeBasedOTP(
					"HmacSHA1", MFATimeBasedOTPUtil.MFA_TIMEBASED_OTP_COUNTER,
					sharedSecret,
					_generateCurrentOTP("HmacSHA1", sharedSecret)));
		}
	}

	private String _generateCurrentOTP(String algorithm, String sharedSecret) {
		String timeCountHex = ReflectionTestUtil.invoke(
			MFATimeBasedOTPUtil.class, "_getTimeCountHex",
			new Class<?>[] {long.class},
			System.currentTimeMillis() /
				MFATimeBasedOTPUtil.MFA_TIMEBASED_OTP_COUNTER);

		return ReflectionTestUtil.invoke(
			MFATimeBasedOTPUtil.class, "_generateTimeBasedOTP",
			new Class<?>[] {String.class, byte[].class, String.class},
			algorithm, Base32.decode(sharedSecret), timeCountHex);
	}

}