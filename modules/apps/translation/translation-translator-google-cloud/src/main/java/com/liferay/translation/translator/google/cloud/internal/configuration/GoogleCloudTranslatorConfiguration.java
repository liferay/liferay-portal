/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.translation.translator.google.cloud.internal.configuration;

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
	id = "com.liferay.translation.translator.google.cloud.internal.configuration.GoogleCloudTranslatorConfiguration",
	localization = "content/Language",
	name = "google-cloud-translator-configuration-name"
)
public interface GoogleCloudTranslatorConfiguration {

	@Meta.AD(
		deflt = "false",
		description = "enabled-description[google-cloud-translation]",
		name = "enabled", required = false
	)
	public boolean enabled();

	@Meta.AD(
		deflt = "", description = "service-account-private-key-description",
		name = "service-account-private-key", required = false,
		type = Meta.Type.Password
	)
	public String serviceAccountPrivateKey();

}