/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.encryptor;

import java.security.Key;

/**
 * @author Brian Wing Shun Chan
 * @author Shuyang Zhou
 * @author Mika Koivisto
 */
public interface Encryptor {

	public String decrypt(Key key, String encryptedString)
		throws EncryptorException;

	public String decryptAuthenticated(Key key, String encryptedString)
		throws EncryptorException;

	public byte[] decryptUnencodedAsBytes(Key key, byte[] encryptedBytes)
		throws EncryptorException;

	public Key deserializeKey(String base64String);

	public String encrypt(Key key, String plaintext) throws EncryptorException;

	public String encryptAuthenticated(Key key, String plaintext)
		throws EncryptorException;

	public byte[] encryptUnencoded(Key key, byte[] plainBytes)
		throws EncryptorException;

	public byte[] encryptUnencoded(Key key, String plaintext)
		throws EncryptorException;

	public Key generateKey() throws EncryptorException;

	public String serializeKey(Key key);

}