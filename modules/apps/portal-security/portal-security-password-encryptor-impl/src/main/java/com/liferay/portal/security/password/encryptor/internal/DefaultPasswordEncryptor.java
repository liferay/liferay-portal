/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.password.encryptor.internal;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PwdEncryptorException;
import com.liferay.portal.kernel.security.pwd.PasswordEncryptor;
import com.liferay.portal.kernel.util.DigesterUtil;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.osgi.service.component.annotations.Component;

/**
 * @author Michael C. Han
 * @author Tomas Polesovsky
 */
@Component(
	property = "type=" + PasswordEncryptor.TYPE_DEFAULT,
	service = PasswordEncryptor.class
)
public class DefaultPasswordEncryptor implements PasswordEncryptor {

	@Override
	public String encrypt(
			String algorithm, String plainTextPassword,
			String encryptedPassword, boolean upgradeHashSecurity)
		throws PwdEncryptorException {

		try {
			MessageDigest.getInstance(algorithm);
		}
		catch (NoSuchAlgorithmException noSuchAlgorithmException) {
			throw new PwdEncryptorException.UnavailableAlgorithm(
				StringBundler.concat(
					"The algorithm \"", algorithm,
					"\" is not available from the configured security ",
					"provider"),
				noSuchAlgorithmException);
		}

		return DigesterUtil.digest(algorithm, plainTextPassword);
	}

}