/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.microsoft.translator.internal.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Andrea Di Giorgi
 */
@ExtendedObjectClassDefinition(category = "localization")
@Meta.OCD(
	id = "com.liferay.microsoft.translator.internal.configuration.MicrosoftTranslatorConfiguration",
	localization = "content/Language",
	name = "microsoft-translator-configuration-name"
)
public interface MicrosoftTranslatorConfiguration {

	@Meta.AD(
		description = "subscription-key-description",
		name = "subscription-key-name", required = false,
		type = Meta.Type.Password
	)
	public String subscriptionKey();

}