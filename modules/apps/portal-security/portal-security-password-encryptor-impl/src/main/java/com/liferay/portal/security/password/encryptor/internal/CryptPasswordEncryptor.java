/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.password.encryptor.internal;

import com.liferay.portal.kernel.exception.PwdEncryptorException;
import com.liferay.portal.kernel.security.SecureRandom;
import com.liferay.portal.kernel.security.pwd.PasswordEncryptor;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.DigesterUtil;
import com.liferay.portal.kernel.util.Validator;

import java.io.UnsupportedEncodingException;

import java.util.Random;

import org.osgi.service.component.annotations.Component;

import org.vps.crypt.Crypt;

/**
 * @author Michael C. Han
 * @author Tomas Polesovsky
 */
@Component(
	property = "type=" + PasswordEncryptor.TYPE_UFC_CRYPT,
	service = PasswordEncryptor.class
)
public class CryptPasswordEncryptor implements PasswordEncryptor {

	@Override
	public String encrypt(
			String algorithm, String plainTextPassword,
			String encryptedPassword, boolean upgradeHashSecurity)
		throws PwdEncryptorException {

		if (upgradeHashSecurity) {
			encryptedPassword = null;
		}

		byte[] saltBytes = getSalt(encryptedPassword);

		try {
			return Crypt.crypt(
				saltBytes, plainTextPassword.getBytes(DigesterUtil.ENCODING));
		}
		catch (UnsupportedEncodingException unsupportedEncodingException) {
			throw new PwdEncryptorException.UnsupportedEncoding(
				unsupportedEncodingException.getMessage(),
				unsupportedEncodingException);
		}
	}

	protected byte[] getSalt(String encryptedPassword)
		throws PwdEncryptorException {

		byte[] saltBytes = null;

		try {
			if (Validator.isNull(encryptedPassword)) {
				Random random = new SecureRandom();

				int x = random.nextInt(Integer.MAX_VALUE) % _SALT.length;
				int y = random.nextInt(Integer.MAX_VALUE) % _SALT.length;

				String salt = _SALT[x].concat(_SALT[y]);

				saltBytes = salt.getBytes(DigesterUtil.ENCODING);
			}
			else {
				String salt = encryptedPassword.substring(0, 2);

				saltBytes = salt.getBytes(DigesterUtil.ENCODING);
			}
		}
		catch (UnsupportedEncodingException unsupportedEncodingException) {
			throw new PwdEncryptorException.UnsupportedEncoding(
				"Unable to extract salt from encrypted password " +
					unsupportedEncodingException.getMessage(),
				unsupportedEncodingException);
		}

		return saltBytes;
	}

	private static final String[] _SALT = ArrayUtil.toStringArray(
		"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789./".
			toCharArray());

}