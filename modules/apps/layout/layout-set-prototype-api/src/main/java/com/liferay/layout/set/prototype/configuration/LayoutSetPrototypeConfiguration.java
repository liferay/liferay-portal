/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.set.prototype.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Tamas Molnar
 */
@ExtendedObjectClassDefinition(
	category = "infrastructure",
	scope = ExtendedObjectClassDefinition.Scope.COMPANY
)
@Meta.OCD(
	description = "layout-set-prototype-configuration-description",
	id = "com.liferay.layout.set.prototype.configuration.LayoutSetPrototypeConfiguration",
	localization = "content/Language",
	name = "layout-set-prototype-configuration-name"
)
public interface LayoutSetPrototypeConfiguration {

	@Meta.AD(
		deflt = "true", description = "cancel-propagation-import-task-help",
		name = "cancel-propagation-import-task", required = false
	)
	public boolean cancelPropagationImportTask();

	@Meta.AD(
		deflt = "false", description = "trigger-propagation-help",
		name = "trigger-propagation", required = false
	)
	public boolean triggerPropagation();

}