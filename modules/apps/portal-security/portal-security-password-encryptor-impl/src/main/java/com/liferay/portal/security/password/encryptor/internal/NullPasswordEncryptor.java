/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.password.encryptor.internal;

import com.liferay.portal.kernel.security.pwd.PasswordEncryptor;

import org.osgi.service.component.annotations.Component;

/**
 * @author Michael C. Han
 * @author Tomas Polesovsky
 */
@Component(
	property = "type=" + PasswordEncryptor.TYPE_NONE,
	service = PasswordEncryptor.class
)
public class NullPasswordEncryptor implements PasswordEncryptor {

	@Override
	public String encrypt(
		String algorithm, String plaintextPassword, String encryptedPassword,
		boolean upgradeHashSecurity) {

		return plaintextPassword;
	}

}