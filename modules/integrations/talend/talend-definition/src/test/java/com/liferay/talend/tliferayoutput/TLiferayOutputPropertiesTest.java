/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.talend.tliferayoutput;

import com.liferay.talend.BasePropertiesTestCase;
import com.liferay.talend.properties.resource.Operation;

import org.junit.Assert;
import org.junit.Test;

import org.talend.components.api.component.Connector;
import org.talend.components.api.component.PropertyPathConnector;
import org.talend.components.api.properties.ComponentProperties;
import org.talend.components.common.SchemaProperties;
import org.talend.daikon.NamedThing;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.property.EnumProperty;
import org.talend.daikon.properties.property.Property;

/**
 * @author Igor Beslic
 */
public class TLiferayOutputPropertiesTest extends BasePropertiesTestCase {

	@Test
	public void testComponentConnectors() throws Exception {
		TLiferayOutputDefinition tLiferayOutputDefinition =
			new TLiferayOutputDefinition();

		Class<? extends ComponentProperties> propertyClass =
			tLiferayOutputDefinition.getPropertyClass();

		Assert.assertEquals(
			"Component properties implementation class",
			TLiferayOutputProperties.class, propertyClass);

		ComponentProperties componentProperties =
			getDefaultInitializedComponentPropertiesInstance(propertyClass);

		for (Connector outputConnector :
				componentProperties.getPossibleConnectors(true)) {

			PropertyPathConnector propertyPathConnector =
				(PropertyPathConnector)outputConnector;

			assertEquals(
				componentProperties, propertyPathConnector.getPropertyPath(),
				SchemaProperties.EMPTY_SCHEMA);
		}

		for (Connector inputConnector :
				componentProperties.getPossibleConnectors(false)) {

			PropertyPathConnector propertyPathConnector =
				(PropertyPathConnector)inputConnector;

			assertEquals(
				componentProperties, propertyPathConnector.getPropertyPath(),
				SchemaProperties.EMPTY_SCHEMA);
		}
	}

	@Test
	public void testInit() {
		TLiferayOutputProperties tLiferayOutputProperties =
			new TLiferayOutputProperties("root");

		tLiferayOutputProperties.init();

		NamedThing dieOnErrorNamedThing = getNamedThing(
			"dieOnError", tLiferayOutputProperties.getForm(Form.ADVANCED));

		Assert.assertEquals(
			"Die on error widget display component implementation",
			Property.class, dieOnErrorNamedThing.getClass());

		NamedThing resourceNamedThing = getNamedThing(
			"resource", tLiferayOutputProperties.getForm(Form.MAIN));

		Assert.assertEquals(
			"Resource widget display component implementation", Form.class,
			resourceNamedThing.getClass());

		NamedThing operationsNamedThing = getNamedThing(
			"operations", (Form)resourceNamedThing);

		Assert.assertEquals(
			"Resource widget display component implementation",
			EnumProperty.class, operationsNamedThing.getClass());

		Property<Operation> operationProperty =
			(EnumProperty)operationsNamedThing;

		Assert.assertTrue(
			"Field operation is required", operationProperty.isRequired());
		Assert.assertFalse(
			"Field operation is nullable", operationProperty.isNullable());
	}

}