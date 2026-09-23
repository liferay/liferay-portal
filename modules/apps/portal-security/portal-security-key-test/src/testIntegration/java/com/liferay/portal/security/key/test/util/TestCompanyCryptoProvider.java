/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.key.test.util;

import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.security.key.ServiceIndicator;
import com.liferay.portal.security.key.crypto.CryptoKey;
import com.liferay.portal.security.key.crypto.CryptoServiceResult;
import com.liferay.portal.security.key.crypto.exception.CryptoException;
import com.liferay.portal.security.key.spi.ProviderStatus;
import com.liferay.portal.security.key.spi.crypto.CryptoProvider;

import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.SecureRandom;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

/**
 * @author Christopher Kian
 */
public class TestCompanyCryptoProvider implements CryptoProvider {

	public static final String KEY_IDENTIFIER = RandomTestUtil.randomString();

	public static final String PROVIDER_ID = RandomTestUtil.randomString();

	public TestCompanyCryptoProvider() throws GeneralSecurityException {
		KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");

		keyGenerator.init(256);

		_secretKey = keyGenerator.generateKey();
	}

	@Override
	public CryptoServiceResult<byte[]> decrypt(
			byte[] ciphertext, long companyId, String keyIdentifier)
		throws CryptoException {

		_validateKeyIdentifier(keyIdentifier);

		try {
			Cipher cipher = Cipher.getInstance(_CIPHER_TRANSFORMATION);

			cipher.init(
				Cipher.DECRYPT_MODE, _secretKey,
				new GCMParameterSpec(
					128,
					Arrays.copyOfRange(
						ciphertext, 0, _INITIALIZATION_VECTOR_LENGTH)));

			byte[] plaintext = cipher.doFinal(
				ciphertext, _INITIALIZATION_VECTOR_LENGTH,
				ciphertext.length - _INITIALIZATION_VECTOR_LENGTH);

			_decryptCount.incrementAndGet();

			return new CryptoServiceResult<>(_serviceIndicator, plaintext);
		}
		catch (GeneralSecurityException generalSecurityException) {
			throw new CryptoException(
				"Unable to decrypt with the test key",
				generalSecurityException);
		}
	}

	@Override
	public void deleteKey(long companyId, String keyIdentifier)
		throws CryptoException {

		throw new UnsupportedOperationException();
	}

	@Override
	public CryptoServiceResult<byte[]> encrypt(
			long companyId, String keyIdentifier, byte[] plaintext)
		throws CryptoException {

		_validateKeyIdentifier(keyIdentifier);

		try {
			byte[] initializationVector =
				new byte[_INITIALIZATION_VECTOR_LENGTH];

			_secureRandom.nextBytes(initializationVector);

			Cipher cipher = Cipher.getInstance(_CIPHER_TRANSFORMATION);

			cipher.init(
				Cipher.ENCRYPT_MODE, _secretKey,
				new GCMParameterSpec(128, initializationVector));

			byte[] encryptedBytes = cipher.doFinal(plaintext);

			byte[] ciphertext =
				new byte[_INITIALIZATION_VECTOR_LENGTH + encryptedBytes.length];

			System.arraycopy(
				initializationVector, 0, ciphertext, 0,
				_INITIALIZATION_VECTOR_LENGTH);

			System.arraycopy(
				encryptedBytes, 0, ciphertext, _INITIALIZATION_VECTOR_LENGTH,
				encryptedBytes.length);

			return new CryptoServiceResult<>(_serviceIndicator, ciphertext);
		}
		catch (GeneralSecurityException generalSecurityException) {
			throw new CryptoException(
				"Unable to encrypt with the test key",
				generalSecurityException);
		}
	}

	@Override
	public CryptoServiceResult<Key> exportKey(
			long companyId, String keyIdentifier)
		throws CryptoException {

		throw new UnsupportedOperationException();
	}

	@Override
	public CryptoServiceResult<String> generateAsymmetricKeyIdentifier(
			String algorithm, long companyId, String keyIdentifier)
		throws CryptoException {

		throw new UnsupportedOperationException();
	}

	@Override
	public CryptoServiceResult<String> generateSecretKeyIdentifier(
			String algorithm, long companyId, String keyIdentifier)
		throws CryptoException {

		throw new UnsupportedOperationException();
	}

	@Override
	public CryptoKey getCryptoKey(long companyId, String keyIdentifier)
		throws CryptoException {

		throw new UnsupportedOperationException();
	}

	public int getDecryptCount() {
		return _decryptCount.get();
	}

	@Override
	public List<String> getKeyIdentifiers(long companyId) {
		return Collections.singletonList(KEY_IDENTIFIER);
	}

	@Override
	public ProviderStatus getProviderStatus() {
		return ProviderStatus.OPERATIONAL;
	}

	@Override
	public CryptoServiceResult<String> importSecretKey(
			String algorithm, long companyId, byte[] keyBytes,
			String keyIdentifier)
		throws CryptoException {

		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isAllowedCompany(long companyId) {
		if (companyId == CompanyConstants.SYSTEM) {
			return false;
		}

		return true;
	}

	@Override
	public CryptoServiceResult<String> unwrap(
			long companyId, String keyIdentifier, String masterKeyIdentifier,
			String wrappedKeyAlgorithm, byte[] wrappedKeyBytes,
			int wrappedKeyCipherType)
		throws CryptoException {

		throw new UnsupportedOperationException();
	}

	@Override
	public CryptoServiceResult<byte[]> wrap(
			long companyId, String keyIdentifier, String masterKeyIdentifier)
		throws CryptoException {

		throw new UnsupportedOperationException();
	}

	private void _validateKeyIdentifier(String keyIdentifier)
		throws CryptoException {

		if (!Objects.equals(keyIdentifier, KEY_IDENTIFIER)) {
			throw new CryptoException(
				"Test key identifier " + keyIdentifier + " is unknown");
		}
	}

	private static final String _CIPHER_TRANSFORMATION = "AES/GCM/NoPadding";

	private static final int _INITIALIZATION_VECTOR_LENGTH = 12;

	private final AtomicInteger _decryptCount = new AtomicInteger();
	private final SecretKey _secretKey;
	private final SecureRandom _secureRandom = new SecureRandom();
	private final ServiceIndicator _serviceIndicator = new ServiceIndicator(
		true, RandomTestUtil.randomString());

}