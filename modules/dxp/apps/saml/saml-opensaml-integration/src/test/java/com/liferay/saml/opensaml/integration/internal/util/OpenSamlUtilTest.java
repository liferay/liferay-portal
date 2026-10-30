/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.opensaml.integration.internal.util;

import com.liferay.petra.concurrent.DCLSingleton;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.saml.opensaml.integration.internal.BaseSamlTestCase;

import java.util.Arrays;
import java.util.List;

import org.apache.xml.security.stax.ext.XMLSecurityConstants;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.Issuer;
import org.opensaml.security.credential.Credential;
import org.opensaml.xmlsec.SignatureSigningConfiguration;
import org.opensaml.xmlsec.config.DefaultSecurityConfigurationBootstrap;
import org.opensaml.xmlsec.impl.BasicSignatureSigningConfiguration;
import org.opensaml.xmlsec.signature.Signature;
import org.opensaml.xmlsec.signature.support.SignatureConstants;

/**
 * @author Rafael Praxedes
 */
public class OpenSamlUtilTest extends BaseSamlTestCase {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testBuildIssuer() throws Exception {
		DCLSingleton<?> dclSingleton = ReflectionTestUtil.getFieldValue(
			OpenSamlUtil.class, "_xmlObjectBuilderFactoryDCLSingleton");

		dclSingleton.destroy(null);

		ReflectionTestUtil.setFieldValue(
			ConfigurationServiceBootstrapUtil.class, "_initialized", false);

		String value = RandomTestUtil.randomString();

		try (AutoCloseable autoCloseable =
				ReflectionTestUtil.setFieldValueWithAutoCloseable(
					XMLSecurityConstants.class, "xmlOutputFactory", null)) {

			Assert.assertThrows(
				IllegalStateException.class,
				() -> OpenSamlUtil.buildIssuer(value));
		}

		Issuer issuer = OpenSamlUtil.buildIssuer(value);

		Assert.assertEquals(value, issuer.getValue());
	}

	@Test
	public void testSignObject() throws Exception {
		BasicSignatureSigningConfiguration basicSignatureSigningConfiguration =
			DefaultSecurityConfigurationBootstrap.
				buildDefaultSignatureSigningConfiguration();

		basicSignatureSigningConfiguration.setWhitelistedAlgorithms(
			Arrays.asList(
				SignatureConstants.ALGO_ID_DIGEST_SHA512,
				SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA512));

		SignatureSigningConfiguration signatureSigningConfiguration =
			ConfigurationServiceBootstrapUtil.get(
				SignatureSigningConfiguration.class);

		ConfigurationServiceBootstrapUtil.register(
			SignatureSigningConfiguration.class,
			basicSignatureSigningConfiguration);

		try {
			Assertion assertion = OpenSamlUtil.buildAssertion();

			OpenSamlUtil.signObject(
				assertion, getCredential(IDP_ENTITY_ID), null);

			Signature signature = assertion.getSignature();

			Assert.assertEquals(
				SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA512,
				signature.getSignatureAlgorithm());

			List<Credential> signingCredentials =
				basicSignatureSigningConfiguration.getSigningCredentials();

			Assert.assertTrue(signingCredentials.isEmpty());
		}
		finally {
			ConfigurationServiceBootstrapUtil.register(
				SignatureSigningConfiguration.class,
				signatureSigningConfiguration);
		}
	}

}