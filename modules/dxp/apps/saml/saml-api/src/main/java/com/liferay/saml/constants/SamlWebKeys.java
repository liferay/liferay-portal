/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.constants;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Mika Koivisto
 */
@ProviderType
public interface SamlWebKeys {

	public static final String FORCE_REAUTHENTICATION =
		"FORCE_REAUTHENTICATION";

	public static final String SAML_ACS_LOGIN = "SAML_ACS_LOGIN";

	public static final String SAML_ASSERTION_LIFETIME =
		"SAML_ASSERTION_LIFETIME";

	public static final String SAML_CERTIFICATE_TOOL = "SAML_CERTIFICATE_TOOL";

	public static final String SAML_CLOCK_SKEW = "SAML_CLOCK_SKEW";

	public static final String SAML_ENTITY_ID = "SAML_ENTITY_ID";

	public static final String SAML_IDP_REDIRECT_MESSAGE =
		"SAML_IDP_REDIRECT_MESSAGE";

	public static final String SAML_IDP_SP_CONNECTION =
		"SAML_IDP_SP_CONNECTION";

	public static final String SAML_IDP_SP_CONNECTIONS =
		"SAML_IDP_SP_CONNECTIONS";

	public static final String SAML_IDP_SP_CONNECTIONS_COUNT =
		"SAML_IDP_SP_CONNECTIONS_COUNT";

	public static final String SAML_KEEP_ALIVE_URL = "SAML_KEEP_ALIVE_URL";

	public static final String SAML_KEEP_ALIVE_URLS = "SAML_KEEP_ALIVE_URLS";

	public static final String SAML_KEYSTORE = "SAML_KEYSTORE";

	public static final String SAML_SLO_CONTEXT = "SAML_SLO_CONTEXT";

	public static final String SAML_SLO_REQUEST_INFO = "SAML_SLO_REQUEST_INFO";

	public static final String SAML_SP_ATTRIBUTES = "SAML_SP_ATTRIBUTES";

	public static final String SAML_SP_IDP_CONNECTION =
		"SAML_SP_IDP_CONNECTION";

	public static final String SAML_SP_IDP_CONNECTIONS =
		"SAML_SP_IDP_CONNECTIONS";

	public static final String SAML_SP_IDP_CONNECTIONS_COUNT =
		"SAML_SP_IDP_CONNECTIONS_COUNT";

	public static final String SAML_SP_NAME_ID_FORMAT =
		"SAML_SP_NAME_ID_FORMAT";

	public static final String SAML_SP_NAME_ID_VALUE = "SAML_SP_NAME_ID_VALUE";

	public static final String SAML_SP_SESSION_KEY = "SAML_SP_SESSION_KEY";

	public static final String SAML_SSO_ERROR = "SAML_SSO_ERROR";

	public static final String SAML_SSO_LOGIN_CONTEXT =
		"SAML_SSO_LOGIN_CONTEXT";

	public static final String SAML_SSO_REQUEST_CONTEXT =
		"SAML_SSO_REQUEST_CONTEXT";

	public static final String SAML_SSO_SESSION_ID = "SAML_SSO_SESSION_ID";

	public static final String SAML_SUBJECT_NAME_ID = "SAML_SUBJECT_NAME_ID";

	public static final String SAML_X509_CERTIFICATE = "SAML_X509_CERTIFICATE";

}