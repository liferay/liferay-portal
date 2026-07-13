/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Tancredi Covioli
 */
@ExtendedObjectClassDefinition(
	category = "orders", featureFlagKey = "LPD-89850",
	scope = ExtendedObjectClassDefinition.Scope.GROUP
)
@Meta.OCD(
	id = "com.liferay.commerce.configuration.CommerceAccountEntryValidationConfiguration",
	localization = "content/Language",
	name = "commerce-account-entry-validation-configuration-name"
)
public interface CommerceAccountEntryValidationConfiguration {

	@Meta.AD(
		deflt = "disabled", name = "account-entry-validation",
		optionValues = {
			"allow-all", "allow-successes-only", "allow-technical-failures",
			"disabled"
		},
		required = false
	)
	public String validationMode();

}