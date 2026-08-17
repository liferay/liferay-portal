/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.auth;

import java.io.Serializable;

import java.util.Arrays;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;

/**
 * @author Brian Wing Shun Chan
 */
public class PortalCallbackHandler implements CallbackHandler, Serializable {

	public PortalCallbackHandler(String name, String password) {
		_name = name;
		_password = password;
	}

	@Override
	public void handle(Callback[] callbacks) {
		for (Callback callback : callbacks) {
			if (callback instanceof NameCallback) {
				NameCallback nameCallback = (NameCallback)callback;

				nameCallback.setName(_name);
			}
			else if (callback instanceof PasswordCallback) {
				PasswordCallback passwordCallback = (PasswordCallback)callback;

				char[] passwordChars = _password.toCharArray();

				try {
					passwordCallback.setPassword(passwordChars);
				}
				finally {
					Arrays.fill(passwordChars, '\0');
				}
			}
		}
	}

	private final String _name;
	private final String _password;

}