/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.rest.internal.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Arthur Chan
 */
@ExtendedObjectClassDefinition(
	category = "oauth2", scope = ExtendedObjectClassDefinition.Scope.SYSTEM
)
@Meta.OCD(
	id = "com.liferay.oauth2.provider.rest.internal.configuration.OAuth2AuthorizationServerConfiguration",
	localization = "content/Language",
	name = "oauth2-authorization-server-configuration-name"
)
public interface OAuth2AuthorizationServerConfiguration {

	@Meta.AD(
		deflt = "false",
		description = "oauth2-authorization-server-issue-jwt-access-token-description",
		id = "oauth2.authorization.server.issue.jwt.access.token",
		name = "oauth2-authorization-server-issue-jwt-access-token",
		required = false
	)
	public boolean issueJWTAccessToken();

	@Meta.AD(
		deflt = "{}",
		description = "oauth2-authorization-server-jwt-access-token-signing-json-web-key-description",
		id = "oauth2.authorization.server.jwt.access.token.signing.json.web.key",
		name = "oauth2-authorization-server-jwt-access-token-signing-json-web-key",
		required = false, type = Meta.Type.Password
	)
	public String jwtAccessTokenSigningJSONWebKey();

}