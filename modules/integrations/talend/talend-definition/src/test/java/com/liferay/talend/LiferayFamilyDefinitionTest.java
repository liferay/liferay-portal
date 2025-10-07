/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.talend;

import com.liferay.talend.tliferaybatchfile.TLiferayBatchFileDefinition;
import com.liferay.talend.tliferaybatchoutput.TLiferayBatchOutputDefinition;
import com.liferay.talend.tliferayconnection.TLiferayConnectionDefinition;
import com.liferay.talend.tliferayinput.TLiferayInputDefinition;
import com.liferay.talend.tliferayoutput.TLiferayOutputDefinition;
import com.liferay.talend.wizard.LiferayConnectionEditWizardDefinition;
import com.liferay.talend.wizard.LiferayConnectionWizardDefinition;
import com.liferay.talend.wizard.LiferaySchemaWizardDefinition;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.mockito.Mockito;

import org.talend.components.api.ComponentInstaller.ComponentFrameworkContext;
import org.talend.components.liferay.LiferayFamilyDefinition;
import org.talend.daikon.definition.Definition;

/**
 * @author Zoltán Takács
 */
public class LiferayFamilyDefinitionTest {

	@Before
	public void setUp() {
		_liferayFamilyDefinition = new LiferayFamilyDefinition();
	}

	@SuppressWarnings("rawtypes")
	@Test
	public void testAllComponentsDefinitionsCreated() {
		List<Class> expectedDefinitions = new ArrayList<>();

		expectedDefinitions.add(TLiferayBatchFileDefinition.class);
		expectedDefinitions.add(TLiferayBatchOutputDefinition.class);
		expectedDefinitions.add(TLiferayConnectionDefinition.class);
		expectedDefinitions.add(TLiferayInputDefinition.class);
		expectedDefinitions.add(TLiferayOutputDefinition.class);
		expectedDefinitions.add(LiferayConnectionEditWizardDefinition.class);
		expectedDefinitions.add(LiferaySchemaWizardDefinition.class);
		expectedDefinitions.add(LiferayConnectionWizardDefinition.class);

		List<Class> actualDefinitionsNames = new ArrayList<>();

		for (Definition<?> definition :
				_liferayFamilyDefinition.getDefinitions()) {

			actualDefinitionsNames.add(definition.getClass());
		}

		Assert.assertEquals(expectedDefinitions, actualDefinitionsNames);
	}

	@Test
	public void testFamilyInstalled() {
		ComponentFrameworkContext componentFrameworkContext = Mockito.mock(
			ComponentFrameworkContext.class);

		_liferayFamilyDefinition.install(componentFrameworkContext);

		Mockito.verify(componentFrameworkContext);

		componentFrameworkContext.registerComponentFamilyDefinition(
			_liferayFamilyDefinition);
	}

	private LiferayFamilyDefinition _liferayFamilyDefinition;

}