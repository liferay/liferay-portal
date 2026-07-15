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
import com.liferay.portal.security.ldap.configuration.LDAPServerConfiguration;
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
public class LDAPServerConfigurationModelListenerTest {

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

			String baseProviderURL = "ldap://" + RandomTestUtil.randomString();

			_modelListener.onBeforeSave(
				LDAPServerConfiguration.class.getName(),
				HashMapDictionaryBuilder.<String, Object>put(
					"baseProviderURL", baseProviderURL
				).build());

			fipsModeValidatorMockedStatic.verify(
				() -> FIPSModeValidator.validateURL(baseProviderURL),
				Mockito.atLeastOnce());
		}

		try (SafeCloseable safeCloseable =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"FIPS_ENABLED", true)) {

			_modelListener.onBeforeSave(
				LDAPServerConfiguration.class.getName(),
				HashMapDictionaryBuilder.<String, Object>put(
					"baseProviderURL",
					"ldaps://" + RandomTestUtil.randomString()
				).build());
			_modelListener.onBeforeSave(
				LDAPServerConfiguration.class.getName(),
				HashMapDictionaryBuilder.<String, Object>put(
					"baseProviderURL", ""
				).build());

			String baseProviderURL = "ldap://" + RandomTestUtil.randomString();

			LDAPConfigurationModelListenerException
				ldapConfigurationModelListenerException = Assert.assertThrows(
					LDAPConfigurationModelListenerException.class,
					() -> _modelListener.onBeforeSave(
						LDAPServerConfiguration.class.getName(),
						HashMapDictionaryBuilder.<String, Object>put(
							"baseProviderURL", baseProviderURL
						).build()));

			Assert.assertArrayEquals(
				new Object[] {baseProviderURL, "ldaps://"},
				ldapConfigurationModelListenerException.getMessageArguments());
			Assert.assertEquals(
				"the-base-provider-url-x-must-use-the-x-scheme-in-fips-mode",
				ldapConfigurationModelListenerException.getMessageKey());
		}
	}

	private final LDAPServerConfigurationModelListener _modelListener =
		new LDAPServerConfigurationModelListener();

}