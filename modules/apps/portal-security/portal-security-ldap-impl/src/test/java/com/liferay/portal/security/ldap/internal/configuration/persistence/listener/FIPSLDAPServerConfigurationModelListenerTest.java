/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.ldap.internal.configuration.persistence.listener;

import com.liferay.portal.configuration.persistence.listener.ConfigurationModelListenerException;
import com.liferay.portal.kernel.security.fips.FIPSModeUtil;
import com.liferay.portal.security.ldap.configuration.LDAPServerConfiguration;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Dictionary;
import java.util.Hashtable;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Jorge García Jiménez
 */
public class FIPSLDAPServerConfigurationModelListenerTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testAcceptsLdapsWhenFIPSOn() throws Exception {
		try (MockedStatic<FIPSModeUtil> fipsModeUtilMockedStatic =
				Mockito.mockStatic(FIPSModeUtil.class)) {

			fipsModeUtilMockedStatic.when(
				FIPSModeUtil::isEnabled
			).thenReturn(
				true
			);

			_listener.onBeforeSave(_PID, _properties("ldaps://dc.example:636"));
		}
	}

	@Test
	public void testAcceptsLdapsWithMixedCaseWhenFIPSOn() throws Exception {
		try (MockedStatic<FIPSModeUtil> fipsModeUtilMockedStatic =
				Mockito.mockStatic(FIPSModeUtil.class)) {

			fipsModeUtilMockedStatic.when(
				FIPSModeUtil::isEnabled
			).thenReturn(
				true
			);

			_listener.onBeforeSave(_PID, _properties("LDAPS://dc.example:636"));
		}
	}

	@Test
	public void testAllowsPlainLdapWhenFIPSOff() throws Exception {
		try (MockedStatic<FIPSModeUtil> fipsModeUtilMockedStatic =
				Mockito.mockStatic(FIPSModeUtil.class)) {

			fipsModeUtilMockedStatic.when(
				FIPSModeUtil::isEnabled
			).thenReturn(
				false
			);

			_listener.onBeforeSave(_PID, _properties("ldap://dc.example:389"));
		}
	}

	@Test(expected = ConfigurationModelListenerException.class)
	public void testRejectsPlainLdapWhenFIPSOn() throws Exception {
		try (MockedStatic<FIPSModeUtil> fipsModeUtilMockedStatic =
				Mockito.mockStatic(FIPSModeUtil.class)) {

			fipsModeUtilMockedStatic.when(
				FIPSModeUtil::isEnabled
			).thenReturn(
				true
			);

			_listener.onBeforeSave(_PID, _properties("ldap://dc.example:389"));
		}
	}

	@Test
	public void testSkipsNullProviderURL() throws Exception {
		try (MockedStatic<FIPSModeUtil> fipsModeUtilMockedStatic =
				Mockito.mockStatic(FIPSModeUtil.class)) {

			fipsModeUtilMockedStatic.when(
				FIPSModeUtil::isEnabled
			).thenReturn(
				true
			);

			Dictionary<String, Object> properties = new Hashtable<>();

			_listener.onBeforeSave(_PID, properties);

			Assert.assertNull(properties.get("baseProviderURL"));
		}
	}

	private Dictionary<String, Object> _properties(String baseProviderURL) {
		Dictionary<String, Object> properties = new Hashtable<>();

		properties.put("baseProviderURL", baseProviderURL);

		return properties;
	}

	private static final String _PID = LDAPServerConfiguration.class.getName();

	private final FIPSLDAPServerConfigurationModelListener _listener =
		new FIPSLDAPServerConfigurationModelListener();

}