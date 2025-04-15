/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.internal.test;

import com.liferay.oauth2.provider.internal.test.util.JWTAssertionUtil;

import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;

/**
 * @author Arthur Chan
 */
public class JWTAssertionClientAuthentication implements ClientAuthentication {

	public JWTAssertionClientAuthentication(
		WebTarget audienceWebTarget, String clientId, boolean clientIdInForm,
		String issuer, String key, boolean symmetricSignature) {

		String jwtAssertion = null;

		if (symmetricSignature) {
			jwtAssertion = JWTAssertionUtil.getJWTAssertionHS256(
				audienceWebTarget.getUri(), clientId, issuer, key);
		}
		else {
			jwtAssertion = JWTAssertionUtil.getJWTAssertionRS256(
				audienceWebTarget.getUri(), clientId, key, issuer);
		}

		_clientAuthenticationParameters.add("client_assertion", jwtAssertion);
		_clientAuthenticationParameters.add(
			"client_assertion_type",
			"urn:ietf:params:oauth:client-assertion-type:jwt-bearer");

		if (clientIdInForm) {
			_clientAuthenticationParameters.add("client_id", clientId);
		}
	}

	@Override
	public MultivaluedMap<String, String> getClientAuthenticationParameters() {
		return _clientAuthenticationParameters;
	}

	private final MultivaluedMap<String, String>
		_clientAuthenticationParameters = new MultivaluedHashMap<>();

}