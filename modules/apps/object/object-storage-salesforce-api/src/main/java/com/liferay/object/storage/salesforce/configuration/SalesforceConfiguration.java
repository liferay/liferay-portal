/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.storage.salesforce.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Guilherme Camacho
 */
@ExtendedObjectClassDefinition(
	category = "third-party", scope = ExtendedObjectClassDefinition.Scope.GROUP
)
@Meta.OCD(
	id = "com.liferay.object.storage.salesforce.configuration.SalesforceConfiguration",
	localization = "content/Language", name = "salesforce-configuration-name"
)
public interface SalesforceConfiguration {

	@Meta.AD(name = "consumer-key", required = false)
	public String consumerKey();

	@Meta.AD(
		name = "consumer-secret", required = false, type = Meta.Type.Password
	)
	public String consumerSecret();

	@Meta.AD(name = "login-url", required = false)
	public String loginURL();

	@Meta.AD(name = "password", required = false, type = Meta.Type.Password)
	public String password();

	@Meta.AD(name = "username", required = false)
	public String username();

}