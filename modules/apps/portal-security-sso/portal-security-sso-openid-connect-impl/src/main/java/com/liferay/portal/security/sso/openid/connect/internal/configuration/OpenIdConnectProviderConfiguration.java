/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.sso.openid.connect.internal.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Michael C. Han
 */
@ExtendedObjectClassDefinition(
	category = "sso", factoryInstanceLabelAttribute = "providerName",
	scope = ExtendedObjectClassDefinition.Scope.COMPANY
)
@Meta.OCD(
	factory = true,
	id = "com.liferay.portal.security.sso.openid.connect.internal.configuration.OpenIdConnectProviderConfiguration",
	localization = "content/Language",
	name = "open-id-connect-provider-configuration-name"
)
public interface OpenIdConnectProviderConfiguration {

	@Meta.AD(
		deflt = "", description = "authorization-endpoint-help",
		name = "authorization-endpoint", required = false
	)
	public String authorizationEndpoint();

	@Meta.AD(
		deflt = "",
		description = "custom-authorization-request-parameters-help",
		name = "custom-authorization-request-parameters", required = false
	)
	public String[] customAuthorizationRequestParameters();

	@Meta.AD(
		deflt = "", description = "custom-claims-help", name = "custom-claims",
		required = false
	)
	public String[] customClaims();

	@Meta.AD(
		deflt = "", description = "custom-token-request-parameters-help",
		name = "custom-token-request-parameters", required = false
	)
	public String[] customTokenRequestParameters();

	@Meta.AD(
		deflt = "", description = "discovery-endpoint-help",
		name = "discovery-endpoint", required = false
	)
	public String discoveryEndpoint();

	@Meta.AD(
		deflt = "360000", description = "discovery-endpoint-cache-help",
		name = "discovery-endpoint-cache-in-millis", required = false
	)
	public long discoveryEndpointCacheInMillis();

	@Meta.AD(
		deflt = "RS256", description = "id-token-signing-alg-values-help",
		name = "id-token-signing-alg-values", required = false
	)
	public String[] idTokenSigningAlgValues();

	@Meta.AD(
		deflt = "", description = "issuer-url-help", name = "issuer-url",
		required = false
	)
	public String issuerURL();

	@Meta.AD(
		deflt = "", description = "jwks-uri-help", name = "jwks-uri",
		required = false
	)
	public String jwksURI();

	@Meta.AD(
		deflt = "", description = "open-id-connect-client-id-help",
		name = "open-id-connect-client-id"
	)
	public String openIdConnectClientId();

	@Meta.AD(
		deflt = "", description = "open-id-connect-client-secret-help",
		name = "open-id-connect-client-secret"
	)
	public String openIdConnectClientSecret();

	@Meta.AD(
		deflt = "", description = "provider-name-help", name = "provider-name"
	)
	public String providerName();

	@Meta.AD(
		deflt = "", description = "registered-id-token-signing-alg-help",
		name = "registered-id-token-signing-alg", required = false
	)
	public String registeredIdTokenSigningAlg();

	@Meta.AD(
		deflt = "openid email profile", description = "scopes-help",
		name = "scopes"
	)
	public String scopes();

	@Meta.AD(
		deflt = "", description = "subject-types-help", name = "subject-types",
		required = false
	)
	public String[] subjectTypes();

	@Meta.AD(
		deflt = "1000", description = "token-connection-timeout-help",
		name = "token-connection-timeout", required = false
	)
	public int tokenConnectionTimeout();

	@Meta.AD(
		deflt = "", description = "token-endpoint-help",
		name = "token-endpoint", required = false
	)
	public String tokenEndpoint();

	@Meta.AD(
		deflt = "", description = "user-info-endpoint-help",
		name = "user-info-endpoint", required = false
	)
	public String userInfoEndpoint();

}