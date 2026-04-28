/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.ldap.internal.configuration;

import com.liferay.portal.kernel.encryptor.EncryptorException;
import com.liferay.portal.kernel.encryptor.EncryptorUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.security.Key;

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
public class LDAPCredentialCipherTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		_ldapCredentialCipher = new LDAPCredentialCipher();

		_companyLocalService = Mockito.mock(CompanyLocalService.class);

		ReflectionTestUtil.setFieldValue(
			_ldapCredentialCipher, "_companyLocalService",
			_companyLocalService);

		_company = Mockito.mock(Company.class);
		_key = new SecretKeySpec(new byte[16], "AES");

		Mockito.when(
			_companyLocalService.getCompany(_COMPANY_ID)
		).thenReturn(
			_company
		);

		Mockito.when(
			_company.getKeyObj()
		).thenReturn(
			_key
		);
	}

	@Test
	public void testResolveDecryptsEncryptedValue() {
		try (MockedStatic<EncryptorUtil> encryptorUtilMockedStatic =
				Mockito.mockStatic(EncryptorUtil.class)) {

			encryptorUtilMockedStatic.when(
				() -> EncryptorUtil.decrypt(_key, "cipher")
			).thenReturn(
				"plaintext"
			);

			String resolved = _ldapCredentialCipher.resolve(
				_COMPANY_ID, "{ENC}cipher");

			Assert.assertEquals("plaintext", resolved);
		}
	}

	@Test
	public void testResolveEmptyStringPassesThrough() {
		Assert.assertEquals("", _ldapCredentialCipher.resolve(_COMPANY_ID, ""));
	}

	@Test
	public void testResolveNullPassesThrough() {
		Assert.assertNull(_ldapCredentialCipher.resolve(_COMPANY_ID, null));
	}

	@Test
	public void testResolvePlainTextPassesThrough() {
		Assert.assertEquals(
			"legacy-password",
			_ldapCredentialCipher.resolve(_COMPANY_ID, "legacy-password"));
	}

	@Test
	public void testResolveReturnsOriginalOnDecryptFailure() {
		try (MockedStatic<EncryptorUtil> encryptorUtilMockedStatic =
				Mockito.mockStatic(EncryptorUtil.class)) {

			encryptorUtilMockedStatic.when(
				() -> EncryptorUtil.decrypt(_key, "cipher")
			).thenThrow(
				new EncryptorException("bad ciphertext")
			);

			String resolved = _ldapCredentialCipher.resolve(
				_COMPANY_ID, "{ENC}cipher");

			Assert.assertEquals("{ENC}cipher", resolved);
		}
	}

	@Test
	public void testResolveReturnsOriginalOnMissingCompany() throws Exception {
		Mockito.when(
			_companyLocalService.getCompany(9999L)
		).thenThrow(
			new PortalException("no such company")
		);

		Assert.assertEquals(
			"{ENC}cipher", _ldapCredentialCipher.resolve(9999L, "{ENC}cipher"));
	}

	private static final long _COMPANY_ID = 42L;

	private Company _company;
	private CompanyLocalService _companyLocalService;
	private Key _key;
	private LDAPCredentialCipher _ldapCredentialCipher;

}