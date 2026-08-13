/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.opensaml.integration.internal.util;

import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.saml.opensaml.integration.internal.certificate.CertificateToolImpl;
import com.liferay.saml.runtime.certificate.CertificateEntityId;
import com.liferay.saml.runtime.certificate.CertificateTool;
import com.liferay.saml.runtime.credential.KeyStoreManager;
import com.liferay.saml.runtime.exception.CredentialAuthException;

import java.security.KeyPair;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

import java.util.Arrays;
import java.util.Date;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Pedro Victor Silvestre
 */
public class KeyStoreUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		_alias = RandomTestUtil.randomString();
		_password = RandomTestUtil.randomString();

		KeyPair keyPair = _certificateTool.generateKeyPair("RSA", 2048);

		CertificateEntityId certificateEntityId = new CertificateEntityId(
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString());

		Date startDate = new Date();

		X509Certificate x509Certificate = _certificateTool.generateCertificate(
			keyPair, certificateEntityId, certificateEntityId, startDate,
			new Date(startDate.getTime() + (365L * 24 * 60 * 60 * 1000)),
			"SHA256withRSA");

		_keyStore = KeyStore.getInstance("PKCS12");

		_keyStore.load(null, null);

		_keyStore.setEntry(
			_alias,
			new KeyStore.PrivateKeyEntry(
				keyPair.getPrivate(), new Certificate[] {x509Certificate}),
			new KeyStore.PasswordProtection(_password.toCharArray()));

		_keyStoreManager = new KeyStoreManager() {

			@Override
			public KeyStore getKeyStore() {
				return _keyStore;
			}

			@Override
			public void saveKeyStore(KeyStore keyStore) {
			}

		};
	}

	@Test
	public void testDestroyPasswordProtection() {
		char[] passwordChars = _password.toCharArray();

		KeyStore.PasswordProtection keyStorePasswordProtection =
			new KeyStore.PasswordProtection(passwordChars);

		char[] clonedPasswordChars = keyStorePasswordProtection.getPassword();

		KeyStoreUtil.destroyPasswordProtection(keyStorePasswordProtection);

		Assert.assertTrue(keyStorePasswordProtection.isDestroyed());

		Assert.assertFalse(
			Arrays.equals(_password.toCharArray(), clonedPasswordChars));

		Assert.assertArrayEquals(_password.toCharArray(), passwordChars);
	}

	@Test
	public void testDestroyPasswordProtectionWithNullPasswordProtection() {
		KeyStoreUtil.destroyPasswordProtection(null);
	}

	@Test
	public void testGetKeyStoreEntryWithCorrectPassword() throws Exception {
		KeyStore.Entry entry = KeyStoreUtil.getKeyStoreEntry(
			_alias, _password, _keyStoreManager);

		Assert.assertTrue(entry instanceof KeyStore.PrivateKeyEntry);
	}

	@Test
	public void testGetKeyStoreEntryWithIncorrectPassword() {
		Assert.assertThrows(
			CredentialAuthException.class,
			() -> KeyStoreUtil.getKeyStoreEntry(
				_alias, RandomTestUtil.randomString(), _keyStoreManager));
	}

	@Test
	public void testGetKeyStoreEntryWithRepeatedCalls() throws Exception {
		for (int i = 0; i < 3; i++) {
			Assert.assertTrue(
				KeyStoreUtil.getKeyStoreEntry(
					_alias, _password, _keyStoreManager) instanceof
						KeyStore.PrivateKeyEntry);
		}
	}

	private String _alias;
	private final CertificateTool _certificateTool = new CertificateToolImpl();
	private KeyStore _keyStore;
	private KeyStoreManager _keyStoreManager;
	private String _password;

}