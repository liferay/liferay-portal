/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.opensaml.integration.internal.util;

import com.liferay.petra.concurrent.DCLSingleton;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.apache.xml.security.stax.ext.XMLSecurityConstants;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.opensaml.saml.saml2.core.Issuer;

/**
 * @author Rafael Praxedes
 */
public class OpenSamlUtilTest {

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

}