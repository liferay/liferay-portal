/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.security.pwd;

import com.liferay.petra.string.CharPool;
import com.liferay.portal.kernel.exception.PwdEncryptorException;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Tomas Polesovsky
 */
@ProviderType
public interface PasswordEncryptor {

	public static final String TYPE_BCRYPT = "BCRYPT";

	public static final String TYPE_DEFAULT = "DEFAULT";

	public static final String TYPE_MD2 = "MD2";

	public static final String TYPE_MD5 = "MD5";

	public static final String TYPE_NONE = "NONE";

	public static final String TYPE_PBKDF2 = "PBKDF2";

	public static final String TYPE_SHA = "SHA";

	public static final String TYPE_SHA_256 = "SHA-256";

	public static final String TYPE_SHA_384 = "SHA-384";

	public static final String TYPE_SSHA = "SSHA";

	public static final String TYPE_UFC_CRYPT = "UFC-CRYPT";

	public default String encrypt(
			String algorithm, String plaintextPassword,
			String encryptedPassword)
		throws PwdEncryptorException {

		return encrypt(algorithm, plaintextPassword, encryptedPassword, false);
	}

	public String encrypt(
			String algorithm, String plaintextPassword,
			String encryptedPassword, boolean upgradeHashSecurity)
		throws PwdEncryptorException;

	public default String getEncryptedPasswordAlgorithmSettings(
		String encryptedPassword) {

		int index = encryptedPassword.indexOf(CharPool.CLOSE_CURLY_BRACE);

		if (index < 0) {
			return null;
		}

		return encryptedPassword.substring(1, index);
	}

}