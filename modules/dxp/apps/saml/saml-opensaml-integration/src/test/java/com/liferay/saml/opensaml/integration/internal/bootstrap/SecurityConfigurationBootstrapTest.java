/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.opensaml.integration.internal.bootstrap;

import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.PropsValuesTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.saml.opensaml.integration.internal.util.ConfigurationServiceBootstrapUtil;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.opensaml.xmlsec.SignatureSigningConfiguration;
import org.opensaml.xmlsec.SignatureValidationConfiguration;
import org.opensaml.xmlsec.signature.support.SignatureConstants;

/**
 * @author Jorge García Jiménez
 */
public class SecurityConfigurationBootstrapTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		Class.forName(ConfigurationServiceBootstrapUtil.class.getName());
	}

	@Test
	public void testActivate() {
		try (SafeCloseable safeCloseable =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"FIPS_ENABLED", true)) {

			ReflectionTestUtil.invoke(
				new SecurityConfigurationBootstrap(), "activate",
				new Class<?>[] {Map.class}, Collections.emptyMap());

			SignatureSigningConfiguration signatureSigningConfiguration =
				ConfigurationServiceBootstrapUtil.get(
					SignatureSigningConfiguration.class);
			SignatureValidationConfiguration signatureValidationConfiguration =
				ConfigurationServiceBootstrapUtil.get(
					SignatureValidationConfiguration.class);

			_assertBlacklisted(
				signatureSigningConfiguration.getBlacklistedAlgorithms());
			_assertBlacklisted(
				signatureValidationConfiguration.getBlacklistedAlgorithms());
		}
	}

	private void _assertBlacklisted(Collection<String> blacklistedAlgorithms) {
		Assert.assertTrue(
			blacklistedAlgorithms.contains(
				SignatureConstants.ALGO_ID_DIGEST_SHA1));
		Assert.assertTrue(
			blacklistedAlgorithms.contains(
				SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA1));
	}

}