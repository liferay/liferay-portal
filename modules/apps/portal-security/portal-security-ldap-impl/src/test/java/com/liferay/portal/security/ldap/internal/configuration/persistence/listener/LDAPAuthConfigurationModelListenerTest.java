/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.ldap.internal.configuration.persistence.listener;

import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.security.fips.FIPSModeValidator;
import com.liferay.portal.kernel.test.util.PropsValuesTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.security.ldap.LDAPConfigurationModelListenerException;
import com.liferay.portal.security.ldap.authenticator.configuration.LDAPAuthConfiguration;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Jorge García Jiménez
 */
public class LDAPAuthConfigurationModelListenerTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testOnBeforeSave() throws Exception {
		try (MockedStatic<FIPSModeValidator> fipsModeValidatorMockedStatic =
				Mockito.mockStatic(FIPSModeValidator.class);
			SafeCloseable safeCloseable =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"FIPS_ENABLED", false)) {

			for (String algorithm : _ALGORITHMS_NOT_ALLOWED) {
				_modelListener.onBeforeSave(
					LDAPAuthConfiguration.class.getName(),
					HashMapDictionaryBuilder.<String, Object>put(
						"method", "password-compare"
					).put(
						"passwordEncryptionAlgorithm", algorithm
					).build());

				fipsModeValidatorMockedStatic.verify(
					() -> FIPSModeValidator.validateAlgorithm(algorithm));
			}
		}

		try (SafeCloseable safeCloseable =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"FIPS_ENABLED", true)) {

			_modelListener.onBeforeSave(
				LDAPAuthConfiguration.class.getName(),
				HashMapDictionaryBuilder.<String, Object>put(
					"method", "bind"
				).put(
					"passwordEncryptionAlgorithm", RandomTestUtil.randomString()
				).build());

			_modelListener.onBeforeSave(
				LDAPAuthConfiguration.class.getName(),
				HashMapDictionaryBuilder.<String, Object>put(
					"passwordEncryptionAlgorithm", RandomTestUtil.randomString()
				).build());

			for (String algorithm : _ALGORITHMS_ALLOWED) {
				_modelListener.onBeforeSave(
					LDAPAuthConfiguration.class.getName(),
					HashMapDictionaryBuilder.<String, Object>put(
						"method", "password-compare"
					).put(
						"passwordEncryptionAlgorithm", algorithm
					).build());
			}

			for (String algorithm : _ALGORITHMS_NOT_ALLOWED) {
				LDAPConfigurationModelListenerException
					ldapConfigurationModelListenerException =
						Assert.assertThrows(
							LDAPConfigurationModelListenerException.class,
							() -> _modelListener.onBeforeSave(
								LDAPAuthConfiguration.class.getName(),
								HashMapDictionaryBuilder.<String, Object>put(
									"method", "password-compare"
								).put(
									"passwordEncryptionAlgorithm", algorithm
								).build()));

				Assert.assertArrayEquals(
					new Object[] {algorithm},
					ldapConfigurationModelListenerException.
						getMessageArguments());
				Assert.assertEquals(
					"the-algorithm-x-is-not-allowed-in-fips-mode",
					ldapConfigurationModelListenerException.getMessageKey());
			}
		}
	}

	private static final String[] _ALGORITHMS_ALLOWED = {
		"PBKDF2WithHmacSHA256", "SHA-256", "SHA-384", "SHA-512"
	};

	private static final String[] _ALGORITHMS_NOT_ALLOWED = {
		"", "BCRYPT", "MD5", "NONE", "SHA", "SSHA"
	};

	private final LDAPAuthConfigurationModelListener _modelListener =
		new LDAPAuthConfigurationModelListener();

}