/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.internal.test;

import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;

/**
 * @author Arthur Chan
 */
public class PasswordAuthorizationGrant implements AuthorizationGrant {

	public PasswordAuthorizationGrant(String userName, String password) {
		_authorizationGrantParameters.add("grant_type", "password");
		_authorizationGrantParameters.add("password", password);
		_authorizationGrantParameters.add("username", userName);
	}

	@Override
	public MultivaluedMap<String, String> getAuthorizationGrantParameters() {
		return _authorizationGrantParameters;
	}

	private final MultivaluedMap<String, String> _authorizationGrantParameters =
		new MultivaluedHashMap<>();

}