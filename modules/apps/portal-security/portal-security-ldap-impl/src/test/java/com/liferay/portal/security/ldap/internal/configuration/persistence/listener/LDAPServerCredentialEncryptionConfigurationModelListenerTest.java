/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.ldap.internal.configuration.persistence.listener;

import com.liferay.portal.configuration.persistence.listener.ConfigurationModelListenerException;
import com.liferay.portal.kernel.encryptor.EncryptorUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.security.ldap.configuration.LDAPServerConfiguration;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.security.Key;

import java.util.Dictionary;
import java.util.Hashtable;

import javax.crypto.spec.SecretKeySpec;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Jorge García Jiménez
 */
public class LDAPServerCredentialEncryptionConfigurationModelListenerTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		_listener =
			new LDAPServerCredentialEncryptionConfigurationModelListener();

		_companyLocalService = Mockito.mock(CompanyLocalService.class);

		ReflectionTestUtil.setFieldValue(
			_listener, "_companyLocalService", _companyLocalService);

		_key = new SecretKeySpec(new byte[16], "AES");

		Company company = Mockito.mock(Company.class);

		Mockito.when(
			_companyLocalService.getCompany(_COMPANY_ID)
		).thenReturn(
			company
		);

		Mockito.when(
			company.getKeyObj()
		).thenReturn(
			_key
		);
	}

	@Test
	public void testEmptyCredentialIsNoOp() throws Exception {
		Dictionary<String, Object> properties = _properties("");

		_listener.onBeforeSave(_PID, properties);

		Assert.assertEquals("", properties.get("securityCredential"));
	}

	@Test
	public void testEncryptsPlaintextCredential() throws Exception {
		try (MockedStatic<EncryptorUtil> encryptorUtilMockedStatic =
				Mockito.mockStatic(EncryptorUtil.class)) {

			encryptorUtilMockedStatic.when(
				() -> EncryptorUtil.encrypt(_key, "secret")
			).thenReturn(
				"cipher"
			);

			Dictionary<String, Object> properties = _properties("secret");

			_listener.onBeforeSave(_PID, properties);

			Assert.assertEquals(
				"{ENC}cipher", properties.get("securityCredential"));
		}
	}

	@Test(expected = ConfigurationModelListenerException.class)
	public void testFailsWhenCompanyIdIsMissing() throws Exception {
		Dictionary<String, Object> properties = new Hashtable<>();

		properties.put("securityCredential", "secret");
		properties.put("companyId", 0L);

		_listener.onBeforeSave(_PID, properties);
	}

	@Test(expected = ConfigurationModelListenerException.class)
	public void testFailsWhenCompanyLookupFails() throws Exception {
		Mockito.when(
			_companyLocalService.getCompany(_COMPANY_ID)
		).thenThrow(
			new PortalException("no such company")
		);

		_listener.onBeforeSave(_PID, _properties("secret"));
	}

	@Test
	public void testSkipsAlreadyEncryptedCredential() throws Exception {
		Dictionary<String, Object> properties = _properties("{ENC}cipher");

		_listener.onBeforeSave(_PID, properties);

		Assert.assertEquals(
			"{ENC}cipher", properties.get("securityCredential"));
	}

	private Dictionary<String, Object> _properties(String credential) {
		Dictionary<String, Object> properties = new Hashtable<>();

		properties.put("securityCredential", credential);
		properties.put("companyId", _COMPANY_ID);

		return properties;
	}

	private static final long _COMPANY_ID = 42L;

	private static final String _PID = LDAPServerConfiguration.class.getName();

	private CompanyLocalService _companyLocalService;
	private Key _key;
	private LDAPServerCredentialEncryptionConfigurationModelListener _listener;

}