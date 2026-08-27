/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.pwd;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.UserPasswordException;
import com.liferay.portal.kernel.model.PasswordPolicy;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.security.pwd.Toolkit;

import java.nio.charset.StandardCharsets;

import java.security.MessageDigest;

/**
 * @author Brian Wing Shun Chan
 */
public class PwdToolkitUtil {

	public static String generate(PasswordPolicy passwordPolicy) {
		Toolkit toolkit = _toolkitSnapshot.get();

		return toolkit.generate(passwordPolicy);
	}

	public static void validate(
			long userId, String password1, String password2,
			PasswordPolicy passwordPolicy)
		throws PortalException {

		if (!MessageDigest.isEqual(
				password1.getBytes(StandardCharsets.UTF_8),
				password2.getBytes(StandardCharsets.UTF_8))) {

			throw new UserPasswordException.MustMatch(userId);
		}

		if ((passwordPolicy != null) &&
			PwdToolkitUtilThreadLocal.isValidate()) {

			Toolkit toolkit = _toolkitSnapshot.get();

			toolkit.validate(userId, password1, password2, passwordPolicy);
		}
	}

	private PwdToolkitUtil() {
	}

	private static final Snapshot<Toolkit> _toolkitSnapshot = new Snapshot<>(
		PwdToolkitUtil.class, Toolkit.class, null, true);

}