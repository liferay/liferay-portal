/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mule.internal.connection.authentication;

import java.nio.charset.StandardCharsets;

import java.util.Base64;

/**
 * @author Matija Petanjek
 */
public class BasicAuthentication implements HttpAuthentication {

	public BasicAuthentication(String username, String password) {
		this.username = username;
		this.password = password;
	}

	@Override
	public String getAuthorizationHeader() {
		String credentials = username + ":" + password;

		Base64.Encoder encoder = Base64.getEncoder();

		String base64Credentials = encoder.encodeToString(
			credentials.getBytes(StandardCharsets.UTF_8));

		return "Basic " + base64Credentials;
	}

	private final String password;
	private final String username;

}