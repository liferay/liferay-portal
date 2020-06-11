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

package com.liferay.dynamic.data.lists.web.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Lino Alves
 */
@ExtendedObjectClassDefinition(category = "forms-and-workflow")
@Meta.OCD(
	id = "com.liferay.dynamic.data.lists.web.configuration.DDLWebConfiguration",
	localization = "content/Language", name = "ddl-web-configuration-name"
)
public interface DDLWebConfiguration {

	@Meta.AD(
		deflt = "false",
		description = "changeable-default-language-description",
		name = "changeable-default-language", required = false
	)
	public boolean changeableDefaultLanguage();

	@Meta.AD(
		deflt = "enabled-with-warning", name = "csv-export",
		optionLabels = {"Enabled", "enabled-with-warning", "Disabled"},
		optionValues = {"enabled", "enabled-with-warning", "disabled"},
		required = false
	)
	public String csvExport();

	@Meta.AD(
		deflt = "list", name = "default-display-view",
		optionLabels = {"List", "Descriptive"},
		optionValues = {"list", "descriptive"}, required = false
	)
	public String defaultDisplayView();

}