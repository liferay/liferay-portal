/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.multi.factor.authentication.timebased.otp.web.internal.util;

import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.FIPSAlgorithmTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import javax.crypto.Mac;

import jodd.util.Base32;

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
	public void test() throws Exception {
		String sharedSecret = MFATimeBasedOTPUtil.generateSharedSecret(20);

		FIPSAlgorithmTestUtil.assertAlgorithmSwitch(
			"HmacSHA1", Mac.class, "HmacSHA256", Mac::getInstance,
			() -> _generateCurrentOTP(sharedSecret));
	}

	private String _generateCurrentOTP(String sharedSecret) {
		String timeCountHex = ReflectionTestUtil.invoke(
			MFATimeBasedOTPUtil.class, "_getTimeCountHex",
			new Class<?>[] {long.class},
			System.currentTimeMillis() /
				MFATimeBasedOTPUtil.MFA_TIMEBASED_OTP_COUNTER);

		return ReflectionTestUtil.invoke(
			MFATimeBasedOTPUtil.class, "_generateTimeBasedOTP",
			new Class<?>[] {byte[].class, String.class},
			Base32.decode(sharedSecret), timeCountHex);
	}

}