/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.internal.test;

import com.liferay.oauth2.provider.internal.test.util.JWTAssertionUtil;

import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;

import java.util.List;

import org.apache.cxf.rs.security.oauth2.grants.jwt.Constants;

/**
 * @author Arthur Chan
 */
public class JWTAssertionAuthorizationGrant implements AuthorizationGrant {

	public JWTAssertionAuthorizationGrant(
		String issuer, List<String> scopes, String subject,
		WebTarget audienceWebTarget) {

		_authorizationGrantParameters.add(
			"assertion",
			JWTAssertionUtil.getJWTAssertionRS256(
				audienceWebTarget.getUri(), issuer, JWTAssertionUtil.JWKS,
				subject));
		_authorizationGrantParameters.add(
			"grant_type", Constants.JWT_BEARER_GRANT);

		if (scopes != null) {
			_authorizationGrantParameters.put("scope", scopes);
		}
	}

	@Override
	public MultivaluedMap<String, String> getAuthorizationGrantParameters() {
		return _authorizationGrantParameters;
	}

	private final MultivaluedMap<String, String> _authorizationGrantParameters =
		new MultivaluedHashMap<>();

}