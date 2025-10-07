/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.cookies.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Olivér Kecskeméty
 */
@ExtendedObjectClassDefinition(
	category = "privacy", scope = ExtendedObjectClassDefinition.Scope.GROUP
)
@Meta.OCD(
	id = "com.liferay.cookies.configuration.CookiesPreferenceHandlingConfiguration",
	localization = "content/Language",
	name = "cookie-preference-handling-configuration-name"
)
public interface CookiesPreferenceHandlingConfiguration {

	@Meta.AD(
		deflt = "false", description = "cookie-enabled-help", name = "enabled",
		required = false
	)
	public boolean enabled();

	@Meta.AD(
		deflt = "true",
		description = "cookie-explicit-consent-mode-help-deprecated",
		name = "cookie-explicit-consent-mode", required = false
	)
	public boolean explicitConsentMode();

}