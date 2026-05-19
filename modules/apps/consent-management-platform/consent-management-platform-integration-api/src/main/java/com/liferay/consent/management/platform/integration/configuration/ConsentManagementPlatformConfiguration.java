/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.consent.management.platform.integration.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Christian Moura
 */
@ExtendedObjectClassDefinition(
	category = "privacy", featureFlagKey = "LPD-65299",
	scope = ExtendedObjectClassDefinition.Scope.GROUP
)
@Meta.OCD(
	id = "com.liferay.consent.management.platform.integration.configuration.ConsentManagementPlatformConfiguration",
	localization = "content/Language",
	name = "consent-management-platform-configuration-name"
)
public interface ConsentManagementPlatformConfiguration {

	@Meta.AD(
		description = "consent-management-platform-bridge-script-help",
		name = "consent-management-platform-bridge-script", required = false
	)
	public String bridgeScript();

	@Meta.AD(deflt = "false", name = "enabled", required = false)
	public boolean enabled();

	@Meta.AD(
		description = "consent-management-platform-provider-name-help",
		name = "consent-management-platform-provider-name"
	)
	public String providerName();

	@Meta.AD(
		description = "consent-management-platform-script-tag-name-help",
		name = "consent-management-platform-script-tag-name"
	)
	public String scriptTag();

}