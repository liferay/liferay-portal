/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.password.encryptor.internal;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.security.pwd.PasswordEncryptor;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jodd.util.BCrypt;

import org.osgi.service.component.annotations.Component;

/**
 * @author Michael C. Han
 * @author Tomas Polesovsky
 */
@Component(
	enabled = false, property = "type=" + PasswordEncryptor.TYPE_BCRYPT,
	service = PasswordEncryptor.class
)
public class BCryptPasswordEncryptor implements PasswordEncryptor {

	@Override
	public String encrypt(
		String algorithm, String plaintextPassword, String encryptedPassword,
		boolean upgradeHashSecurity) {

		String salt = null;

		if (upgradeHashSecurity) {
			encryptedPassword = null;
		}

		if (Validator.isNull(encryptedPassword)) {
			int rounds = _ROUNDS;

			Matcher matcher = _algorithmPattern.matcher(algorithm);

			if (matcher.matches()) {
				rounds = GetterUtil.getInteger(matcher.group(1), rounds);
			}

			salt = BCrypt.gensalt(rounds);
		}
		else {
			salt = encryptedPassword.substring(0, 29);
		}

		return BCrypt.hashpw(plaintextPassword, salt);
	}

	@Override
	public String getEncryptedPasswordAlgorithmSettings(
		String encryptedPassword) {

		int index = encryptedPassword.indexOf(CharPool.CLOSE_CURLY_BRACE);

		if (index < 0) {
			return null;
		}

		String rounds = String.valueOf(_ROUNDS);

		Matcher matcher = _encryptedPasswordPattern.matcher(encryptedPassword);

		if (matcher.find()) {
			rounds = matcher.group(1);
		}

		return StringBundler.concat(
			encryptedPassword.substring(1, index), CharPool.FORWARD_SLASH,
			rounds);
	}

	private static final int _ROUNDS = 10;

	private static final Pattern _algorithmPattern = Pattern.compile(
		"^BCrypt/([0-9]+)$", Pattern.CASE_INSENSITIVE);
	private static final Pattern _encryptedPasswordPattern = Pattern.compile(
		"\\{BCrypt}\\$2a\\$(\\d+)\\$", Pattern.CASE_INSENSITIVE);

}