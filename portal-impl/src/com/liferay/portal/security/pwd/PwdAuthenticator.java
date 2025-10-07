/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.pwd;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PwdEncryptorException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.pwd.PasswordEncryptorUtil;
import com.liferay.portal.kernel.util.Base64;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.Validator;

import java.io.UnsupportedEncodingException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author Michael C. Han
 */
public class PwdAuthenticator {

	public static boolean authenticate(
			String login, String clearTextPassword,
			String currentEncryptedPassword)
		throws PwdEncryptorException {

		String encryptedPassword = PasswordEncryptorUtil.encrypt(
			clearTextPassword, currentEncryptedPassword);

		if (currentEncryptedPassword.equals(encryptedPassword)) {
			return true;
		}
		else if (GetterUtil.getBoolean(
					PropsUtil.get(PropsKeys.AUTH_MAC_ALLOW))) {

			try {
				MessageDigest digester = MessageDigest.getInstance(
					PropsUtil.get(PropsKeys.AUTH_MAC_ALGORITHM));

				digester.update(login.getBytes(StringPool.UTF8));

				String shardKey = PropsUtil.get(PropsKeys.AUTH_MAC_SHARED_KEY);

				if (Validator.isNull(shardKey)) {
					if (_log.isWarnEnabled()) {
						_log.warn(
							"Please set the property " +
								PropsKeys.AUTH_MAC_SHARED_KEY);
					}

					return false;
				}

				encryptedPassword = Base64.encode(
					digester.digest(shardKey.getBytes(StringPool.UTF8)));

				return clearTextPassword.equals(encryptedPassword);
			}
			catch (NoSuchAlgorithmException noSuchAlgorithmException) {
				throw new SystemException(noSuchAlgorithmException);
			}
			catch (UnsupportedEncodingException unsupportedEncodingException) {
				throw new SystemException(unsupportedEncodingException);
			}
		}

		return false;
	}

	public static void pretendToAuthenticate() throws PwdEncryptorException {
		authenticate(
			_PRETENDED_LOGIN, _PRETENDED_CLEAR_TEXT_PASSWORD,
			_PRETENDED_CURRENT_ENCRYPTED_PASSWORD);
	}

	private static final String _PRETENDED_CLEAR_TEXT_PASSWORD = "password";

	private static final String _PRETENDED_CURRENT_ENCRYPTED_PASSWORD;

	private static final String _PRETENDED_LOGIN = "login";

	private static final Log _log = LogFactoryUtil.getLog(
		PwdAuthenticator.class.getName());

	static {
		try {
			_PRETENDED_CURRENT_ENCRYPTED_PASSWORD =
				PasswordEncryptorUtil.encrypt("currentPassword");
		}
		catch (PwdEncryptorException pwdEncryptorException) {
			throw new RuntimeException(pwdEncryptorException);
		}
	}

}