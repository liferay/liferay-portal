/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Mikel Lorza
 */
@ExtendedObjectClassDefinition(
	category = "site-configuration", generateUI = false,
	scope = ExtendedObjectClassDefinition.Scope.GROUP
)
@Meta.OCD(
	id = "com.liferay.site.configuration.MenuAccessConfiguration",
	localization = "content/Language"
)
public interface MenuAccessConfiguration {

	@Meta.AD(deflt = "", required = false)
	public String[] accessToControlMenuRoleExternalReferenceCodes();

	@Meta.AD(deflt = "false", required = false)
	public boolean showControlMenuByRole();

}