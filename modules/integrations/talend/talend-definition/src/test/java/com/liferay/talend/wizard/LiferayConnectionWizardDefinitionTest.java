/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.talend.wizard;

import com.liferay.talend.properties.connection.LiferayConnectionProperties;
import com.liferay.talend.tliferayinput.TLiferayInputProperties;

import java.util.Locale;

import org.hamcrest.CoreMatchers;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.talend.components.api.wizard.ComponentWizard;
import org.talend.daikon.definition.DefinitionImageType;

/**
 * @author Zoltán Takács
 */
public class LiferayConnectionWizardDefinitionTest {

	@Before
	public void setUp() {
		Locale.setDefault(Locale.ENGLISH);
		_liferayConnectionWizardDefinition =
			new LiferayConnectionWizardDefinition();

		_liferayConnectionProperties = new LiferayConnectionProperties(
			"connection");

		_liferayConnectionProperties.init();
	}

	@Test
	public void testCreateWizard() {
		ComponentWizard wizard =
			_liferayConnectionWizardDefinition.createWizard(
				_liferayConnectionProperties, _REPOSITORY_LOCATION);

		Assert.assertThat(
			wizard, CoreMatchers.instanceOf(LiferayConnectionWizard.class));
		Assert.assertEquals(
			_liferayConnectionWizardDefinition, wizard.getDefinition());
		Assert.assertEquals(
			_REPOSITORY_LOCATION, wizard.getRepositoryLocation());
	}

	@Test
	public void testGetMenuItemName() {
		LiferayConnectionWizardDefinition definition =
			new LiferayConnectionWizardDefinition();

		Assert.assertEquals(
			"Create Liferay Connection", definition.getMenuItemName());
	}

	@Test
	public void testImagePath() {
		Assert.assertNotNull(
			_liferayConnectionWizardDefinition.getImagePath(
				DefinitionImageType.TREE_ICON_16X16));
		Assert.assertNotNull(
			_liferayConnectionWizardDefinition.getImagePath(
				DefinitionImageType.WIZARD_BANNER_75X66));

		Assert.assertNull(
			_liferayConnectionWizardDefinition.getImagePath(
				DefinitionImageType.SVG_ICON));
	}

	@Test
	public void testSupportsProperties() {
		Assert.assertTrue(
			_liferayConnectionWizardDefinition.supportsProperties(
				LiferayConnectionProperties.class));
		Assert.assertFalse(
			_liferayConnectionWizardDefinition.supportsProperties(
				TLiferayInputProperties.class));
	}

	@Test
	public void testTopLevel() {
		Assert.assertTrue(_liferayConnectionWizardDefinition.isTopLevel());
	}

	private static final String _REPOSITORY_LOCATION = "___DRI";

	private LiferayConnectionProperties _liferayConnectionProperties;
	private LiferayConnectionWizardDefinition
		_liferayConnectionWizardDefinition;

}