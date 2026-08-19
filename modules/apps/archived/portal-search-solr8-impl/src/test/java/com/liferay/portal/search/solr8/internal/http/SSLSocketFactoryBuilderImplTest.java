/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.solr8.internal.http;

import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.security.fips.FIPSModeValidator;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
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
public class SSLSocketFactoryBuilderImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testBuild() throws Exception {
		SSLSocketFactoryBuilderImpl sslSocketFactoryBuilderImpl =
			new SSLSocketFactoryBuilderImpl();

		ReflectionTestUtil.setFieldValue(
			sslSocketFactoryBuilderImpl, "_keyStoreLoader",
			(KeyStoreLoader)
				(keyStoreType, keyStoreLocation, keyStorePassword) -> null);

		try (MockedStatic<FIPSModeValidator> fipsModeValidatorMockedStatic =
				Mockito.mockStatic(FIPSModeValidator.class);
			AutoCloseable autoCloseable1 =
				ReflectionTestUtil.setFieldValueWithAutoCloseable(
					sslSocketFactoryBuilderImpl, "_verifyServerCertificate",
					false);
			AutoCloseable autoCloseable2 =
				ReflectionTestUtil.setFieldValueWithAutoCloseable(
					sslSocketFactoryBuilderImpl, "_verifyServerHostname",
					false)) {

			Assert.assertNotNull(sslSocketFactoryBuilderImpl.build());

			fipsModeValidatorMockedStatic.verify(
				() -> FIPSModeValidator.validateTLSVerification(false),
				Mockito.times(2));
		}

		try (SafeCloseable safeCloseable =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"FIPS_ENABLED", true)) {

			Assert.assertNotNull(sslSocketFactoryBuilderImpl.build());

			try (AutoCloseable autoCloseable =
					ReflectionTestUtil.setFieldValueWithAutoCloseable(
						sslSocketFactoryBuilderImpl, "_verifyServerCertificate",
						false)) {

				SecurityException securityException = Assert.assertThrows(
					SecurityException.class,
					sslSocketFactoryBuilderImpl::build);

				Assert.assertEquals(
					"TLS verification must be enabled in FIPS mode",
					securityException.getMessage());
			}

			try (AutoCloseable autoCloseable =
					ReflectionTestUtil.setFieldValueWithAutoCloseable(
						sslSocketFactoryBuilderImpl, "_verifyServerHostname",
						false)) {

				SecurityException securityException = Assert.assertThrows(
					SecurityException.class,
					sslSocketFactoryBuilderImpl::build);

				Assert.assertEquals(
					"TLS verification must be enabled in FIPS mode",
					securityException.getMessage());
			}
		}
	}

}