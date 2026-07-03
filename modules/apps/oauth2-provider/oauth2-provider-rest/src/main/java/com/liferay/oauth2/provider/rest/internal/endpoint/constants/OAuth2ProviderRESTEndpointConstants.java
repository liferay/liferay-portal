/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.rest.internal.endpoint.constants;

/**
 * @author Tomas Polesovsky
 */
public class OAuth2ProviderRESTEndpointConstants {

	/**
	 * Embraces CXF OAuthConstants naming + RFC grant_type value formats.
	 */
	public static final String AUTHORIZATION_CODE_PKCE_GRANT =
		"authorization_code_pkce";

	public static final String COOKIE_NAME_REMEMBER_DEVICE_PREFIX =
		"OAUTH2_REMEMBER_DEVICE_";

	public static final String DYNAMIC_REGISTRATION_MODE_AUTHENTICATED =
		"authenticated";

	public static final String DYNAMIC_REGISTRATION_MODE_OPEN = "open";

	public static final String ERROR_INVALID_CLIENT_METADATA =
		"invalid_client_metadata";

	public static final String ERROR_INVALID_REDIRECT_URI =
		"invalid_redirect_uri";

	public static final String ERROR_INVALID_TOKEN = "invalid_token";

	public static final String ERROR_SERVER_ERROR = "server_error";

	public static final String EVENT_TYPE_DYNAMIC_REGISTRATION_ADD =
		"DYNAMIC_REGISTRATION_ADD";

	public static final String EVENT_TYPE_DYNAMIC_REGISTRATION_REJECT =
		"DYNAMIC_REGISTRATION_REJECT";

	public static final String PROPERTY_KEY_CLIENT_FEATURE_PREFIX = "feature.";

	public static final String PROPERTY_KEY_CLIENT_FEATURE_TOKEN_INTROSPECTION =
		"token.introspection";

	public static final String PROPERTY_KEY_CLIENT_FEATURES = "features";

	public static final String PROPERTY_KEY_CLIENT_JWKS = "jwks";

	public static final String PROPERTY_KEY_CLIENT_JWKS_URI = "jwks_uri";

	public static final String PROPERTY_KEY_CLIENT_REMEMBER_DEVICE =
		"remember.device";

	public static final String PROPERTY_KEY_CLIENT_REMOTE_ADDR =
		"client.remote.addr";

	public static final String PROPERTY_KEY_CLIENT_REMOTE_HOST =
		"client.remote.host";

	public static final String PROPERTY_KEY_CLIENT_SOFTWARE_ID = "software_id";

	public static final String PROPERTY_KEY_CLIENT_TRUSTED_APPLICATION =
		"trusted.application";

	public static final String PROPERTY_KEY_COMPANY_ID = "company.id";

	public static final String PROPERTY_KEY_DYNAMIC_REGISTRATION_MODE =
		"dynamic.registration.mode";

	public static final String PROPERTY_KEY_REMEMBER_DEVICE =
		"oauth2.remember.device";

}