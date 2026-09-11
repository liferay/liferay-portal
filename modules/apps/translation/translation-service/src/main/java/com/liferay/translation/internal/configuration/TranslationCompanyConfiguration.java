/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.translation.internal.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Akhash Ramprakash
 */
@ExtendedObjectClassDefinition(
	category = "translation",
	scope = ExtendedObjectClassDefinition.Scope.COMPANY
)
@Meta.OCD(
	id = "com.liferay.translation.internal.configuration.TranslationCompanyConfiguration",
	localization = "content/Language",
	name = "translation-company-configuration-name"
)
public interface TranslationCompanyConfiguration {

	@Meta.AD(
		deflt = "false",
		description = "protect-html-field-markup-with-xliff-inline-codes-help",
		name = "protect-html-field-markup-with-xliff-inline-codes",
		required = false
	)
	public boolean htmlInlineCodeProtectionEnabled();

}