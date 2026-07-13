/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.encryptor;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.encryptor.Encryptor;
import com.liferay.portal.kernel.encryptor.EncryptorException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.SecureRandomUtil;
import com.liferay.portal.kernel.security.fips.FIPSModeValidator;
import com.liferay.portal.kernel.util.Base64;
import com.liferay.portal.kernel.util.DigesterUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;

import java.security.Key;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.osgi.service.component.annotations.Component;

/**
 * @author Brian Wing Shun Chan
 * @author Shuyang Zhou
 * @author Mika Koivisto
 */
@Component(service = Encryptor.class)
public class EncryptorImpl implements Encryptor {

	public static final String ENCODING = DigesterUtil.ENCODING;

	public static final String KEY_ALGORITHM = StringUtil.toUpperCase(
		GetterUtil.getString(
			PropsUtil.get(PropsKeys.COMPANY_ENCRYPTION_ALGORITHM)));

	public static final int KEY_SIZE = GetterUtil.getInteger(
		PropsUtil.get(PropsKeys.COMPANY_ENCRYPTION_KEY_SIZE));

	@Override
	public String decrypt(Key key, String encryptedString)
		throws EncryptorException {

		byte[] encryptedBytes = Base64.decode(encryptedString);

		return _decryptUnencodedAsString(key, encryptedBytes);
	}

	@Override
	public byte[] decryptUnencodedAsBytes(Key key, byte[] encryptedBytes)
		throws EncryptorException {

		byte[] encodedBytes = key.getEncoded();

		FIPSModeValidator.validateKey(
			key.getAlgorithm(),
			(encodedBytes == null) ? 0 : encodedBytes.length * 8);

		if (PropsValues.FIPS_ENABLED) {
			return _decryptGCM(encryptedBytes, key);
		}

		try {
			String algorithm = key.getAlgorithm();

			String cacheKey = algorithm + StringPool.POUND + key;

			Cipher cipher = _decryptCipherMap.get(cacheKey);

			if (cipher == null) {
				cipher = Cipher.getInstance(algorithm);

				cipher.init(Cipher.DECRYPT_MODE, key);

				_decryptCipherMap.put(cacheKey, cipher);
			}

			synchronized (cipher) {
				return cipher.doFinal(encryptedBytes);
			}
		}
		catch (Exception exception) {
			throw new EncryptorException(exception);
		}
	}

	@Override
	public Key deserializeKey(String base64String) {
		byte[] bytes = Base64.decode(base64String);

		return new SecretKeySpec(bytes, KEY_ALGORITHM);
	}

	@Override
	public String encrypt(Key key, String plainText) throws EncryptorException {
		if (key == null) {
			if (_log.isWarnEnabled()) {
				_log.warn("Skip encrypting based on a null key");
			}

			return plainText;
		}

		byte[] encryptedBytes = encryptUnencoded(key, plainText);

		return Base64.encode(encryptedBytes);
	}

	@Override
	public byte[] encryptUnencoded(Key key, byte[] plainBytes)
		throws EncryptorException {

		byte[] encodedBytes = key.getEncoded();

		FIPSModeValidator.validateKey(
			key.getAlgorithm(),
			(encodedBytes == null) ? 0 : encodedBytes.length * 8);

		if (PropsValues.FIPS_ENABLED) {
			return _encryptGCM(key, plainBytes);
		}

		try {
			String algorithm = key.getAlgorithm();

			String cacheKey = algorithm + StringPool.POUND + key;

			Cipher cipher = _encryptCipherMap.get(cacheKey);

			if (cipher == null) {
				cipher = Cipher.getInstance(algorithm);

				cipher.init(Cipher.ENCRYPT_MODE, key);

				_encryptCipherMap.put(cacheKey, cipher);
			}

			synchronized (cipher) {
				return cipher.doFinal(plainBytes);
			}
		}
		catch (Exception exception) {
			throw new EncryptorException(exception);
		}
	}

	@Override
	public byte[] encryptUnencoded(Key key, String plainText)
		throws EncryptorException {

		try {
			byte[] decryptedBytes = plainText.getBytes(ENCODING);

			return encryptUnencoded(key, decryptedBytes);
		}
		catch (Exception exception) {
			throw new EncryptorException(exception);
		}
	}

	@Override
	public Key generateKey() throws EncryptorException {
		return _generateKey(KEY_ALGORITHM);
	}

	@Override
	public String serializeKey(Key key) {
		return Base64.encode(key.getEncoded());
	}

	private byte[] _decryptGCM(byte[] encryptedBytes, Key key)
		throws EncryptorException {

		try {
			byte[] cipherBytes = Arrays.copyOfRange(
				encryptedBytes, _GCM_INITIALIZATION_VECTOR_LENGTH,
				encryptedBytes.length);

			byte[] initializationVector = Arrays.copyOfRange(
				encryptedBytes, 0, _GCM_INITIALIZATION_VECTOR_LENGTH);

			Cipher cipher = Cipher.getInstance(_AES_GCM_NOPADDING);

			cipher.init(
				Cipher.DECRYPT_MODE, key,
				new GCMParameterSpec(
					_GCM_TAG_LENGTH_BITS, initializationVector));

			return cipher.doFinal(cipherBytes);
		}
		catch (Exception exception) {
			throw new EncryptorException(exception);
		}
	}

	private String _decryptUnencodedAsString(Key key, byte[] encryptedBytes)
		throws EncryptorException {

		try {
			byte[] decryptedBytes = decryptUnencodedAsBytes(
				key, encryptedBytes);

			return new String(decryptedBytes, ENCODING);
		}
		catch (Exception exception) {
			throw new EncryptorException(exception);
		}
	}

	private byte[] _encryptGCM(Key key, byte[] plainBytes)
		throws EncryptorException {

		byte[] initializationVector =
			new byte[_GCM_INITIALIZATION_VECTOR_LENGTH];

		for (int i = 0; i < initializationVector.length; i++) {
			initializationVector[i] = SecureRandomUtil.nextByte();
		}

		try {
			Cipher cipher = Cipher.getInstance(_AES_GCM_NOPADDING);

			cipher.init(
				Cipher.ENCRYPT_MODE, key,
				new GCMParameterSpec(
					_GCM_TAG_LENGTH_BITS, initializationVector));

			byte[] cipherBytes = cipher.doFinal(plainBytes);

			byte[] encryptedBytes =
				new byte[initializationVector.length + cipherBytes.length];

			System.arraycopy(
				initializationVector, 0, encryptedBytes, 0,
				initializationVector.length);
			System.arraycopy(
				cipherBytes, 0, encryptedBytes, initializationVector.length,
				cipherBytes.length);

			return encryptedBytes;
		}
		catch (Exception exception) {
			throw new EncryptorException(exception);
		}
	}

	private Key _generateKey(String algorithm) throws EncryptorException {
		try {
			KeyGenerator keyGenerator = KeyGenerator.getInstance(algorithm);

			keyGenerator.init(
				PropsValues.FIPS_ENABLED ? _AES_KEY_SIZE : KEY_SIZE);

			return keyGenerator.generateKey();
		}
		catch (Exception exception) {
			throw new EncryptorException(exception);
		}
	}

	private static final String _AES_GCM_NOPADDING = "AES/GCM/NoPadding";

	private static final int _AES_KEY_SIZE = 256;

	private static final int _GCM_INITIALIZATION_VECTOR_LENGTH = 12;

	private static final int _GCM_TAG_LENGTH_BITS = 128;

	private static final Log _log = LogFactoryUtil.getLog(EncryptorImpl.class);

	private final Map<String, Cipher> _decryptCipherMap =
		new ConcurrentHashMap<>(1, 1F, 1);
	private final Map<String, Cipher> _encryptCipherMap =
		new ConcurrentHashMap<>(1, 1F, 1);

}