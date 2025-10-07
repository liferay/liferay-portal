/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.pwd;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.UserPasswordException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.PasswordPolicy;
import com.liferay.portal.kernel.security.pwd.BasicToolkit;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.PwdGenerator;

/**
 * @author Brian Wing Shun Chan
 */
public class RegExpToolkit extends BasicToolkit {

	public RegExpToolkit() {
		_pattern = PropsUtil.get(PropsKeys.PASSWORDS_REGEXPTOOLKIT_PATTERN);
		_charset = PropsUtil.get(PropsKeys.PASSWORDS_REGEXPTOOLKIT_CHARSET);
		_length = GetterUtil.getInteger(
			PropsUtil.get(PropsKeys.PASSWORDS_REGEXPTOOLKIT_LENGTH));
	}

	@Override
	public String generate(PasswordPolicy passwordPolicy) {
		return PwdGenerator.getPassword(_charset, _length);
	}

	@Override
	public void validate(
			long userId, String password1, String password2,
			PasswordPolicy passwordPolicy)
		throws PortalException {

		boolean value = password1.matches(_pattern);

		if (!value) {
			if (_log.isWarnEnabled()) {
				_log.warn("User " + userId + " attempted an invalid password");
			}

			throw new UserPasswordException.MustComplyWithRegex(
				userId, _pattern);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(RegExpToolkit.class);

	private final String _charset;
	private final int _length;
	private final String _pattern;

}