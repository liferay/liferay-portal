/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.script.management.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Feliphe Marinho
 */
@ExtendedObjectClassDefinition(
	category = "script-management", generateUI = false,
	scope = ExtendedObjectClassDefinition.Scope.SYSTEM
)
@Meta.OCD(
	id = "com.liferay.portal.security.script.management.configuration.ScriptManagementConfiguration",
	localization = "content/Language",
	name = "script-management-configuration-name"
)
public interface ScriptManagementConfiguration {

	@Meta.AD(deflt = "false", required = false)
	public boolean allowScriptContentToBeExecutedOrIncluded();

}