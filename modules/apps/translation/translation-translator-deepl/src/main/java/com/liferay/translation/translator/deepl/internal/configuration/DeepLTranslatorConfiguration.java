/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.translation.translator.deepl.internal.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Yasuyuki Takeo
 */
@ExtendedObjectClassDefinition(
	category = "translation",
	scope = ExtendedObjectClassDefinition.Scope.COMPANY
)
@Meta.OCD(
	id = "com.liferay.translation.translator.deepl.internal.configuration.DeepLTranslatorConfiguration",
	localization = "content/Language",
	name = "deepl-translator-configuration-name"
)
public interface DeepLTranslatorConfiguration {

	@Meta.AD(name = "token", required = false, type = Meta.Type.Password)
	public String authKey();

	@Meta.AD(
		deflt = "false", description = "enabled-description[deepl-translation]",
		name = "enabled", required = false
	)
	public boolean enabled();

	@Meta.AD(
		deflt = "https://api-free.deepl.com/v2/translate", name = "api-url",
		required = false
	)
	public String url();

	@Meta.AD(
		deflt = "https://api-free.deepl.com/v2/languages",
		name = "validate-language-url[deepl-translation]", required = false
	)
	public String validateLanguageURL();

}