/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.friendly.url.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Adolfo Pérez
 */
@ExtendedObjectClassDefinition(
	category = "pages", scope = ExtendedObjectClassDefinition.Scope.COMPANY
)
@Meta.OCD(
	description = "friendly-url-redirection-configuration-description",
	id = "com.liferay.friendly.url.configuration.FriendlyURLRedirectionConfiguration",
	localization = "content/Language",
	name = "friendly-url-redirection-configuration-name"
)
public interface FriendlyURLRedirectionConfiguration {

	@Meta.AD(
		deflt = "temporary", description = "redirection-type-description",
		name = "redirection-type-name",
		optionLabels = {"permanent", "temporary"},
		optionValues = {"permanent", "temporary"}, required = false
	)
	public String redirectionType();

	@Meta.AD(
		deflt = "true",
		description = "show-alternative-layout-friendly-url-message-description",
		name = "show-alternative-layout-friendly-url-message", required = false
	)
	public boolean showAlternativeLayoutFriendlyURLMessage();

}