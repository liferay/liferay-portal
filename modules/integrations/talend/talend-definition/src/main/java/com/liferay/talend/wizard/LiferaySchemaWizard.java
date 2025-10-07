/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.talend.wizard;

import com.liferay.talend.properties.connection.LiferayConnectionProperties;

import org.talend.components.api.properties.ComponentProperties;
import org.talend.components.api.wizard.ComponentWizard;
import org.talend.components.api.wizard.ComponentWizardDefinition;
import org.talend.daikon.properties.presentation.Form;

/**
 * @author Ivica Cardic
 * @author Igor Beslic
 */
public class LiferaySchemaWizard extends ComponentWizard {

	public LiferaySchemaWizard(
		ComponentWizardDefinition componentWizardDefinition,
		ComponentProperties componentProperties, String repositoryLocation) {

		super(componentWizardDefinition, repositoryLocation);

		schemaList = new LiferaySchemaListProperties("schemaList");

		schemaList.setConnection(
			(LiferayConnectionProperties)componentProperties);

		schemaList.setRepositoryLocation(getRepositoryLocation());

		schemaList.init();

		addForm(schemaList.getForm(Form.MAIN));
	}

	public boolean supportsProperties(ComponentProperties componentProperties) {
		if (componentProperties instanceof LiferaySchemaListProperties) {
			return true;
		}

		return false;
	}

	public final LiferaySchemaListProperties schemaList;

}