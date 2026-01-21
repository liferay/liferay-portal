/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.taglib.form.navigator.internal.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Sergio González
 */
@ExtendedObjectClassDefinition(
	category = "form-navigator-extensions",
	factoryInstanceLabelAttribute = "formNavigatorId"
)
@Meta.OCD(
	factory = true,
	id = "com.liferay.frontend.taglib.form.navigator.internal.configuration.FormNavigatorConfiguration",
	localization = "content/Language",
	name = "form-navigator-configuration-name"
)
public interface FormNavigatorConfiguration {

	@Meta.AD(
		description = "form-navigator-entry-keys-help",
		name = "form-navigator-entry-keys"
	)
	public String[] formNavigatorEntryKeys();

	@Meta.AD(name = "form-navigator-id")
	public String formNavigatorId();

}