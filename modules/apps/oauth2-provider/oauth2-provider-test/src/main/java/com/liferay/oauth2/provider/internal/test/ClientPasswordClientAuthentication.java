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
public class ClientPasswordClientAuthentication
	implements ClientAuthentication {

	public ClientPasswordClientAuthentication(
		String clientId, String clientSecret) {

		_clientAuthenticationData.add("client_id", clientId);
		_clientAuthenticationData.add("client_secret", clientSecret);
	}

	@Override
	public MultivaluedMap<String, String> getClientAuthenticationParameters() {
		return _clientAuthenticationData;
	}

	private final MultivaluedMap<String, String> _clientAuthenticationData =
		new MultivaluedHashMap<>();

}