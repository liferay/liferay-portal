/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.translation.translator.azure.internal.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Adolfo Pérez
 */
@ExtendedObjectClassDefinition(
	category = "translation",
	scope = ExtendedObjectClassDefinition.Scope.COMPANY
)
@Meta.OCD(
	id = "com.liferay.translation.translator.azure.internal.configuration.AzureTranslatorConfiguration",
	localization = "content/Language",
	name = "azure-translator-configuration-name"
)
public interface AzureTranslatorConfiguration {

	@Meta.AD(
		deflt = "false", description = "enabled-description[azure-translation]",
		name = "enabled", required = false
	)
	public boolean enabled();

	@Meta.AD(deflt = "", name = "resource-location-name", required = false)
	public String resourceLocation();

	@Meta.AD(
		deflt = "", name = "subscription-key-name", required = false,
		type = Meta.Type.Password
	)
	public String subscriptionKey();

}