/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.encryptor;

import com.liferay.portal.kernel.module.service.Snapshot;

import java.security.Key;

/**
 * @author Julius Lee
 */
public class EncryptorUtil {

	public static String decrypt(Key key, String encryptedString)
		throws EncryptorException {

		Encryptor encryptor = _encryptorSnapshot.get();

		return encryptor.decrypt(key, encryptedString);
	}

	public static String decryptAuthenticated(Key key, String encryptedString)
		throws EncryptorException {

		Encryptor encryptor = _encryptorSnapshot.get();

		return encryptor.decryptAuthenticated(key, encryptedString);
	}

	public static byte[] decryptUnencodedAsBytes(Key key, byte[] encryptedBytes)
		throws EncryptorException {

		Encryptor encryptor = _encryptorSnapshot.get();

		return encryptor.decryptUnencodedAsBytes(key, encryptedBytes);
	}

	public static Key deserializeKey(String base64String) {
		Encryptor encryptor = _encryptorSnapshot.get();

		return encryptor.deserializeKey(base64String);
	}

	public static String encrypt(Key key, String plaintext)
		throws EncryptorException {

		Encryptor encryptor = _encryptorSnapshot.get();

		return encryptor.encrypt(key, plaintext);
	}

	public static String encryptAuthenticated(Key key, String plaintext)
		throws EncryptorException {

		Encryptor encryptor = _encryptorSnapshot.get();

		return encryptor.encryptAuthenticated(key, plaintext);
	}

	public static byte[] encryptUnencoded(Key key, byte[] plainBytes)
		throws EncryptorException {

		Encryptor encryptor = _encryptorSnapshot.get();

		return encryptor.encryptUnencoded(key, plainBytes);
	}

	public static byte[] encryptUnencoded(Key key, String plaintext)
		throws EncryptorException {

		Encryptor encryptor = _encryptorSnapshot.get();

		return encryptor.encryptUnencoded(key, plaintext);
	}

	public static Key generateKey() throws EncryptorException {
		Encryptor encryptor = _encryptorSnapshot.get();

		return encryptor.generateKey();
	}

	public static String serializeKey(Key key) {
		Encryptor encryptor = _encryptorSnapshot.get();

		return encryptor.serializeKey(key);
	}

	private static final Snapshot<Encryptor> _encryptorSnapshot =
		new Snapshot<>(EncryptorUtil.class, Encryptor.class);

}