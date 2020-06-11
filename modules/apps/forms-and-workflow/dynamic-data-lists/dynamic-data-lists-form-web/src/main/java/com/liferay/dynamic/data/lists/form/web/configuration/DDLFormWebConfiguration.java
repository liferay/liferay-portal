/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.dynamic.data.lists.form.web.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Lino Alves
 */
@ExtendedObjectClassDefinition(
	category = "forms-and-workflow",
	scope = ExtendedObjectClassDefinition.Scope.GROUP
)
@Meta.OCD(
	id = "com.liferay.dynamic.data.lists.form.web.configuration.DDLFormWebConfiguration",
	localization = "content/Language", name = "ddl-form-web-configuration-name"
)
public interface DDLFormWebConfiguration {

	@Meta.AD(
		deflt = "enabled-with-warning", name = "csv-export",
		optionLabels = {"Enabled", "enabled-with-warning", "Disabled"},
		optionValues = {"enabled", "enabled-with-warning", "disabled"},
		required = false
	)
	public String csvExport();

	@Meta.AD(
		deflt = "descriptive", name = "default-display-view",
		optionLabels = {"Descriptive", "List"},
		optionValues = {"descriptive", "list"}, required = false
	)
	public String defaultDisplayView();

}