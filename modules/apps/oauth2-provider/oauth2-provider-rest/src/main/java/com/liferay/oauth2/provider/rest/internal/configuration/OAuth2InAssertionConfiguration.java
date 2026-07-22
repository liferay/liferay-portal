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
	category = "oauth2",
	factoryInstanceLabelAttribute = "oauth2.in.assertion.issuer",
	scope = ExtendedObjectClassDefinition.Scope.COMPANY
)
@Meta.OCD(
	factory = true,
	id = "com.liferay.oauth2.provider.rest.internal.configuration.OAuth2InAssertionConfiguration",
	localization = "content/Language",
	name = "oauth2-in-assertion-configuration-name"
)
public interface OAuth2InAssertionConfiguration {

	@Meta.AD(
		description = "oauth2-in-assertion-issuer-help",
		id = "oauth2.in.assertion.issuer", name = "oauth2-in-assertion-issuer"
	)
	public String issuer();

	@Meta.AD(
		description = "oauth2-in-assertion-signature-json-web-key-set-help",
		id = "oauth2.in.assertion.signature.json.web.key.set",
		name = "oauth2-in-assertion-signature-json-web-key-set",
		type = Meta.Type.Password
	)
	public String signatureJSONWebKeySet();

	@Meta.AD(
		deflt = "UUID", description = "oauth2-in-assertion-user-auth-type-help",
		id = "oauth2.in.assertion.user.auth.type",
		name = "oauth2-in-assertion-user-auth-type",
		optionValues = {"emailAddress", "screenName", "userId", "UUID"}
	)
	public String userAuthType();

}