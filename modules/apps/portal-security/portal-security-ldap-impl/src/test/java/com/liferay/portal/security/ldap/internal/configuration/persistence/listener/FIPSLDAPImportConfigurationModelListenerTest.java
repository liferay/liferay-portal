/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.ldap.internal.configuration.persistence.listener;

import com.liferay.portal.configuration.persistence.listener.ConfigurationModelListenerException;
import com.liferay.portal.kernel.security.fips.FIPSModeUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.security.ldap.exportimport.configuration.LDAPImportConfiguration;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Dictionary;
import java.util.Hashtable;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Jorge García Jiménez
 */
public class FIPSLDAPImportConfigurationModelListenerTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testAllowsImportPasswordWhenFIPSOffRegardlessOfAlgorithm()
		throws Exception {

		try (MockedStatic<FIPSModeUtil> fipsModeUtilMockedStatic =
				Mockito.mockStatic(
					FIPSModeUtil.class, Mockito.CALLS_REAL_METHODS)) {

			fipsModeUtilMockedStatic.when(
				FIPSModeUtil::isEnabled
			).thenReturn(
				false
			);

			_listener.onBeforeSave(_PID, _properties(true));
		}
	}

	@Test
	public void testAllowsImportPasswordWhenPortalAlgorithmApproved()
		throws Exception {

		try (MockedStatic<FIPSModeUtil> fipsModeUtilMockedStatic =
				Mockito.mockStatic(
					FIPSModeUtil.class, Mockito.CALLS_REAL_METHODS)) {

			fipsModeUtilMockedStatic.when(
				FIPSModeUtil::isEnabled
			).thenReturn(
				true
			);

			try (MockedStatic<PropsUtil> propsUtilMockedStatic =
					Mockito.mockStatic(PropsUtil.class)) {

				propsUtilMockedStatic.when(
					() -> PropsUtil.get(
						PropsKeys.PASSWORDS_ENCRYPTION_ALGORITHM)
				).thenReturn(
					"PBKDF2WithHmacSHA1/160/1300000"
				);

				_listener.onBeforeSave(_PID, _properties(true));
			}
		}
	}

	@Test
	public void testIgnoresWhenImportPasswordDisabled() throws Exception {
		try (MockedStatic<FIPSModeUtil> fipsModeUtilMockedStatic =
				Mockito.mockStatic(
					FIPSModeUtil.class, Mockito.CALLS_REAL_METHODS)) {

			fipsModeUtilMockedStatic.when(
				FIPSModeUtil::isEnabled
			).thenReturn(
				true
			);

			try (MockedStatic<PropsUtil> propsUtilMockedStatic =
					Mockito.mockStatic(PropsUtil.class)) {

				propsUtilMockedStatic.when(
					() -> PropsUtil.get(
						PropsKeys.PASSWORDS_ENCRYPTION_ALGORITHM)
				).thenReturn(
					"MD5"
				);

				_listener.onBeforeSave(_PID, _properties(false));
			}
		}
	}

	@Test(expected = ConfigurationModelListenerException.class)
	public void testRejectsImportPasswordWhenPortalAlgorithmNotApproved()
		throws Exception {

		try (MockedStatic<FIPSModeUtil> fipsModeUtilMockedStatic =
				Mockito.mockStatic(
					FIPSModeUtil.class, Mockito.CALLS_REAL_METHODS)) {

			fipsModeUtilMockedStatic.when(
				FIPSModeUtil::isEnabled
			).thenReturn(
				true
			);

			try (MockedStatic<PropsUtil> propsUtilMockedStatic =
					Mockito.mockStatic(PropsUtil.class)) {

				propsUtilMockedStatic.when(
					() -> PropsUtil.get(
						PropsKeys.PASSWORDS_ENCRYPTION_ALGORITHM)
				).thenReturn(
					"MD5"
				);

				_listener.onBeforeSave(_PID, _properties(true));
			}
		}
	}

	private Dictionary<String, Object> _properties(
		boolean importPasswordEnabled) {

		Dictionary<String, Object> properties = new Hashtable<>();

		properties.put("importUserPasswordEnabled", importPasswordEnabled);

		return properties;
	}

	private static final String _PID = LDAPImportConfiguration.class.getName();

	private final FIPSLDAPImportConfigurationModelListener _listener =
		new FIPSLDAPImportConfigurationModelListener();

}