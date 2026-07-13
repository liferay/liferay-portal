/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.settings.internal.security.auth.verifier;

import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.FIPSAlgorithmTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.servlet.http.HttpServletRequest;

import java.security.KeyFactory;
import java.security.Signature;
import java.security.spec.InvalidKeySpecException;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Lucas Miranda
 */
public class AnalyticsSecurityAuthVerifierTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testValidateSignature() throws Exception {
		FIPSAlgorithmTestUtil.assertAlgorithmSwitch(
			"DSA", KeyFactory.class, "EC", KeyFactory::getInstance,
			this::_invokeValidateSignature);
		FIPSAlgorithmTestUtil.assertAlgorithmSwitch(
			"DSA", Signature.class, "SHA256withECDSA", Signature::getInstance,
			this::_invokeValidateSignature);
	}

	private void _invokeValidateSignature() {
		Assert.assertThrows(
			InvalidKeySpecException.class,
			() -> ReflectionTestUtil.invoke(
				new AnalyticsSecurityAuthVerifier(), "_validateSignature",
				new Class<?>[] {
					HttpServletRequest.class, String.class, String.class,
					String.class
				},
				null, RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), RandomTestUtil.randomString()));
	}

}