/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.talend;

import org.talend.components.api.component.ComponentDefinition;
import org.talend.components.api.service.common.DefinitionRegistry;
import org.talend.components.api.test.AbstractComponentTest2;
import org.talend.components.liferay.LiferayFamilyDefinition;
import org.talend.daikon.definition.Definition;
import org.talend.daikon.definition.service.DefinitionRegistryService;

/**
 * @author Zoltán Takács
 */
public abstract class LiferayAbstractComponentTestCase
	extends AbstractComponentTest2 {

	@Override
	public DefinitionRegistryService getDefinitionRegistry() {
		if (_definitionRegistry == null) {
			_definitionRegistry = new DefinitionRegistry();

			_definitionRegistry.registerComponentFamilyDefinition(
				new LiferayFamilyDefinition());
		}

		return _definitionRegistry;
	}

	protected void assertComponentIsRegistered(
		Class<? extends Definition> componentClass, String name) {

		assertComponentIsRegistered(
			ComponentDefinition.class, name, componentClass);
	}

	private DefinitionRegistry _definitionRegistry;

}