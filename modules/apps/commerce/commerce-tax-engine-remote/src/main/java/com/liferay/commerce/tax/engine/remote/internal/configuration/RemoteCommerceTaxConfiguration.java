/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.tax.engine.remote.internal.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Ivica Cardic
 */
@ExtendedObjectClassDefinition(
	category = "tax", scope = ExtendedObjectClassDefinition.Scope.GROUP
)
@Meta.OCD(
	id = "com.liferay.commerce.tax.engine.remote.internal.configuration.RemoteCommerceTaxConfiguration",
	localization = "content/Language",
	name = "remote-commerce-tax-configuration-name"
)
public interface RemoteCommerceTaxConfiguration {

	@Meta.AD(
		name = "tax-value-endpoint-authorization-token", required = false,
		type = Meta.Type.Password
	)
	public String taxValueEndpointAuthorizationToken();

	@Meta.AD(name = "tax-value-endpoint-url", required = false)
	public String taxValueEndpointURL();

}