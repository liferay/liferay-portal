/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.ldap.internal.authenticator;

import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.security.auth.Authenticator;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.PropsValuesTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.security.ldap.configuration.ConfigurationProvider;
import com.liferay.portal.security.ldap.exportimport.configuration.LDAPImportConfiguration;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Manuele Castro
 */
public class LDAPAuthTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testGetAuthenticatedResult() {
		ConfigurationProvider<LDAPImportConfiguration>
			ldapImportConfigurationProvider = Mockito.mock(
				ConfigurationProvider.class);

		LDAPImportConfiguration ldapImportConfiguration = Mockito.mock(
			LDAPImportConfiguration.class);

		Mockito.when(
			ldapImportConfigurationProvider.getConfiguration(Mockito.anyLong())
		).thenReturn(
			ldapImportConfiguration
		);

		ReflectionTestUtil.setFieldValue(
			_ldapAuth, "_ldapImportConfigurationProvider",
			ldapImportConfigurationProvider);

		String password = RandomTestUtil.randomString(13);

		Assert.assertEquals(
			Authenticator.SKIP_LIFERAY_CHECK,
			_getAuthenticatedResult(password));

		Mockito.when(
			ldapImportConfiguration.importUserPasswordEnabled()
		).thenReturn(
			true
		);

		Assert.assertEquals(
			Authenticator.SUCCESS, _getAuthenticatedResult(password));

		try (SafeCloseable safeCloseable =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"FIPS_ENABLED", true)) {

			Assert.assertEquals(
				Authenticator.SUCCESS,
				_getAuthenticatedResult(RandomTestUtil.randomString(14)));
			Assert.assertEquals(
				Authenticator.SKIP_LIFERAY_CHECK,
				_getAuthenticatedResult(password));
		}
	}

	private int _getAuthenticatedResult(String password) {
		return ReflectionTestUtil.invoke(
			_ldapAuth, "_getAuthenticatedResult",
			new Class<?>[] {long.class, String.class},
			RandomTestUtil.randomLong(), password);
	}

	private static final LDAPAuth _ldapAuth = new LDAPAuth();

}