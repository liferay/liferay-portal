/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.multi.factor.authentication.web.internal.company.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Pedro Victor Silvestre
 */
@ExtendedObjectClassDefinition(
	category = "multi-factor-authentication",
	scope = ExtendedObjectClassDefinition.Scope.COMPANY, strictScope = true
)
@Meta.OCD(
	id = "com.liferay.multi.factor.authentication.web.internal.company.configuration.MFACompanyConfiguration",
	localization = "content/Language", name = "mfa-company-configuration-name"
)
public interface MFACompanyConfiguration {

	@Meta.AD(
		deflt = "false", description = "mfa-enabled-description",
		name = "enabled", required = false
	)
	public boolean enabled();

}