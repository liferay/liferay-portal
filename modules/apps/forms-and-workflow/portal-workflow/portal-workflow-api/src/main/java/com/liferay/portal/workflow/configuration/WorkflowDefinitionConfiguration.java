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

package com.liferay.portal.workflow.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Tomas Polesovsky
 */
@ExtendedObjectClassDefinition(category = "workflow")
@Meta.OCD(
	id = "com.liferay.portal.workflow.configuration.WorkflowDefinitionConfiguration",
	localization = "content/Language",
	name = "workflow-definition-configuration-name"
)
public interface WorkflowDefinitionConfiguration {

	@Meta.AD(
		deflt = "false",
		description = "allow-administrators-to-publish-and-edit-workflows-description",
		id = "company.administrator.can.publish",
		name = "allow-administrators-to-publish-and-edit-workflows",
		required = false
	)
	public boolean companyAdministratorCanPublish();

}