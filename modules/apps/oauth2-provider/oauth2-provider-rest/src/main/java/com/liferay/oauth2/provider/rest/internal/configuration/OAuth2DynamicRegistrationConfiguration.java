/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.rest.internal.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Jorge García Jiménez
 */
@ExtendedObjectClassDefinition(
	category = "oauth2", featureFlagKey = "LPD-63416",
	scope = ExtendedObjectClassDefinition.Scope.COMPANY
)
@Meta.OCD(
	id = "com.liferay.oauth2.provider.rest.internal.configuration.OAuth2DynamicRegistrationConfiguration",
	localization = "content/Language",
	name = "oauth2-dynamic-registration-configuration-name"
)
public interface OAuth2DynamicRegistrationConfiguration {

	@Meta.AD(
		deflt = "",
		description = "oauth2-dynamic-registration-allowed-grant-types-description",
		id = "dynamic.registration.allowed.grant.types",
		name = "oauth2-dynamic-registration-allowed-grant-types",
		required = false
	)
	public String[] allowedGrantTypes();

	@Meta.AD(
		deflt = "",
		description = "oauth2-dynamic-registration-allowed-hosts-description",
		id = "dynamic.registration.allowed.hosts",
		name = "oauth2-dynamic-registration-allowed-hosts", required = false
	)
	public String[] allowedHosts();

	@Meta.AD(
		deflt = "",
		description = "oauth2-dynamic-registration-allowed-redirect-uri-patterns-description",
		id = "dynamic.registration.allowed.redirect.uri.patterns",
		name = "oauth2-dynamic-registration-allowed-redirect-uri-patterns",
		required = false
	)
	public String[] allowedRedirectURIPatterns();

	@Meta.AD(
		deflt = "",
		description = "oauth2-dynamic-registration-allowed-scopes-description",
		id = "dynamic.registration.allowed.scopes",
		name = "oauth2-dynamic-registration-allowed-scopes", required = false
	)
	public String[] allowedScopes();

	@Meta.AD(
		deflt = "true",
		description = "oauth2-dynamic-registration-require-initial-access-token-description",
		id = "dynamic.registration.require.initial.access.token",
		name = "oauth2-dynamic-registration-require-initial-access-token",
		required = false
	)
	public boolean requireInitialAccessToken();

	@Meta.AD(
		deflt = "false",
		description = "oauth2-dynamic-registration-trust-proxy-headers-description",
		id = "dynamic.registration.trust.proxy.headers",
		name = "oauth2-dynamic-registration-trust-proxy-headers",
		required = false
	)
	public boolean trustProxyHeaders();

}