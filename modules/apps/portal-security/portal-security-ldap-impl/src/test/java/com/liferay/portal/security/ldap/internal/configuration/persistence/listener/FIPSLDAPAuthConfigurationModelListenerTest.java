/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.ldap.internal.configuration.persistence.listener;

import com.liferay.portal.configuration.persistence.listener.ConfigurationModelListenerException;
import com.liferay.portal.kernel.security.fips.FIPSModeUtil;
import com.liferay.portal.security.ldap.LocalizedLDAPConfigurationException;
import com.liferay.portal.security.ldap.authenticator.configuration.LDAPAuthConfiguration;
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
public class FIPSLDAPAuthConfigurationModelListenerTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testAcceptsAllAlgorithmsWhenFIPSOff() throws Exception {
		try (MockedStatic<FIPSModeUtil> fipsModeUtilMockedStatic = _mockFIPS(
				false)) {

			_listener.onBeforeSave(
				_PID, _properties("password-compare", "MD5"));
			_listener.onBeforeSave(
				_PID, _properties("password-compare", "BCRYPT"));
			_listener.onBeforeSave(
				_PID, _properties("password-compare", "SSHA"));
			_listener.onBeforeSave(
				_PID, _properties("password-compare", "SHA-256"));
		}
	}

	@Test
	public void testAcceptsAnyAlgorithmWhenFIPSOnAndBind() throws Exception {
		try (MockedStatic<FIPSModeUtil> fipsModeUtilMockedStatic = _mockFIPS(
				true)) {

			_listener.onBeforeSave(_PID, _properties("bind", "MD5"));
			_listener.onBeforeSave(_PID, _properties("bind", "BCRYPT"));
			_listener.onBeforeSave(_PID, _properties("bind", "NONE"));
			_listener.onBeforeSave(_PID, _properties("bind", "SSHA"));
		}
	}

	@Test
	public void testAcceptsApprovedAlgorithmsWhenFIPSOn() throws Exception {
		try (MockedStatic<FIPSModeUtil> fipsModeUtilMockedStatic = _mockFIPS(
				true)) {

			_listener.onBeforeSave(
				_PID, _properties("password-compare", "SHA-256"));
			_listener.onBeforeSave(
				_PID, _properties("password-compare", "SHA-384"));
			_listener.onBeforeSave(
				_PID, _properties("password-compare", "PBKDF2"));
		}
	}

	@Test
	public void testMissingMethodIsNoOpWhenFIPSOn() throws Exception {
		try (MockedStatic<FIPSModeUtil> fipsModeUtilMockedStatic = _mockFIPS(
				true)) {

			_listener.onBeforeSave(_PID, _properties(null, "MD5"));
		}
	}

	@Test
	public void testRejectionCarriesLocalizedMessage() throws Exception {
		try (MockedStatic<FIPSModeUtil> fipsModeUtilMockedStatic = _mockFIPS(
				true)) {

			try {
				_listener.onBeforeSave(
					_PID, _properties("password-compare", "MD5"));

				Assert.fail("Expected LocalizedLDAPConfigurationException");
			}
			catch (LocalizedLDAPConfigurationException
						localizedLDAPConfigurationException) {

				Assert.assertEquals(
					"fips-mode-does-not-permit-ldap-password-encryption-" +
						"algorithm-x",
					localizedLDAPConfigurationException.getMessageKey());
				Assert.assertArrayEquals(
					new Object[] {"MD5"},
					localizedLDAPConfigurationException.getMessageArguments());
			}
		}
	}

	@Test(expected = ConfigurationModelListenerException.class)
	public void testRejectsBcryptWhenFIPSOnAndPasswordCompare()
		throws Exception {

		try (MockedStatic<FIPSModeUtil> fipsModeUtilMockedStatic = _mockFIPS(
				true)) {

			_listener.onBeforeSave(
				_PID, _properties("password-compare", "BCRYPT"));
		}
	}

	@Test(expected = ConfigurationModelListenerException.class)
	public void testRejectsEmptyAlgorithmWhenFIPSOnAndPasswordCompare()
		throws Exception {

		try (MockedStatic<FIPSModeUtil> fipsModeUtilMockedStatic = _mockFIPS(
				true)) {

			_listener.onBeforeSave(_PID, _properties("password-compare", ""));
		}
	}

	@Test(expected = ConfigurationModelListenerException.class)
	public void testRejectsMD5WhenFIPSOnAndPasswordCompare() throws Exception {
		try (MockedStatic<FIPSModeUtil> fipsModeUtilMockedStatic = _mockFIPS(
				true)) {

			_listener.onBeforeSave(
				_PID, _properties("password-compare", "MD5"));
		}
	}

	@Test(expected = ConfigurationModelListenerException.class)
	public void testRejectsNoneWhenFIPSOnAndPasswordCompare() throws Exception {
		try (MockedStatic<FIPSModeUtil> fipsModeUtilMockedStatic = _mockFIPS(
				true)) {

			_listener.onBeforeSave(
				_PID, _properties("password-compare", "NONE"));
		}
	}

	@Test(expected = ConfigurationModelListenerException.class)
	public void testRejectsSha1WhenFIPSOnAndPasswordCompare() throws Exception {
		try (MockedStatic<FIPSModeUtil> fipsModeUtilMockedStatic = _mockFIPS(
				true)) {

			_listener.onBeforeSave(
				_PID, _properties("password-compare", "SHA"));
		}
	}

	@Test(expected = ConfigurationModelListenerException.class)
	public void testRejectsSshaWhenFIPSOnAndPasswordCompare() throws Exception {
		try (MockedStatic<FIPSModeUtil> fipsModeUtilMockedStatic = _mockFIPS(
				true)) {

			_listener.onBeforeSave(
				_PID, _properties("password-compare", "SSHA"));
		}
	}

	private MockedStatic<FIPSModeUtil> _mockFIPS(boolean enabled) {
		MockedStatic<FIPSModeUtil> fipsModeUtilMockedStatic =
			Mockito.mockStatic(FIPSModeUtil.class, Mockito.CALLS_REAL_METHODS);

		fipsModeUtilMockedStatic.when(
			FIPSModeUtil::isEnabled
		).thenReturn(
			enabled
		);

		return fipsModeUtilMockedStatic;
	}

	private Dictionary<String, Object> _properties(
		String method, String algorithm) {

		Dictionary<String, Object> properties = new Hashtable<>();

		if (method != null) {
			properties.put("method", method);
		}

		properties.put("passwordEncryptionAlgorithm", algorithm);

		return properties;
	}

	private static final String _PID = LDAPAuthConfiguration.class.getName();

	private final FIPSLDAPAuthConfigurationModelListener _listener =
		new FIPSLDAPAuthConfigurationModelListener();

}