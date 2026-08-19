/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.json.web.service.client;

import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.json.web.service.client.internal.JSONWebServiceClientImpl;
import com.liferay.portal.kernel.security.fips.FIPSModeValidator;
import com.liferay.portal.kernel.test.util.PropsValuesTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Caio Farias
 */
public class BaseJSONWebServiceClientImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testGetSSLIOSessionStrategy() {
		JSONWebServiceClientImpl jsonWebServiceClientImpl =
			new JSONWebServiceClientImpl();

		try (MockedStatic<FIPSModeValidator> fipsModeValidatorMockedStatic =
				Mockito.mockStatic(FIPSModeValidator.class)) {

			jsonWebServiceClientImpl.getSSLIOSessionStrategy();

			fipsModeValidatorMockedStatic.verify(
				() -> FIPSModeValidator.validateTLSVerification(false));
		}

		try (SafeCloseable safeCloseable =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"FIPS_ENABLED", true)) {

			SecurityException securityException = Assert.assertThrows(
				SecurityException.class,
				jsonWebServiceClientImpl::getSSLIOSessionStrategy);

			Assert.assertEquals(
				"TLS verification must be enabled in FIPS mode",
				securityException.getMessage());

			jsonWebServiceClientImpl.setTrustSelfSignedCertificates(false);

			Assert.assertNotNull(
				jsonWebServiceClientImpl.getSSLIOSessionStrategy());
		}
	}

}