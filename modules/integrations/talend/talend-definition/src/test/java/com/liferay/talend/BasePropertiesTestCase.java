/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.talend;

import java.lang.reflect.Constructor;

import org.apache.avro.Schema;

import org.junit.Assert;

import org.talend.components.api.properties.ComponentProperties;
import org.talend.components.common.SchemaProperties;
import org.talend.daikon.NamedThing;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.presentation.Widget;
import org.talend.daikon.properties.property.Property;

/**
 * @author Igor Beslic
 */
public abstract class BasePropertiesTestCase {

	protected void assertEquals(
		ComponentProperties componentProperties,
		String schemaPropertiesPropertyPath, Schema expected) {

		SchemaProperties schemaProperties =
			(SchemaProperties)componentProperties.getProperties(
				schemaPropertiesPropertyPath);

		Assert.assertNotNull(
			String.format(
				"Property path %s must identify not null schema properties " +
					"in %s",
				schemaPropertiesPropertyPath, componentProperties.getClass()),
			schemaProperties);

		Property<Schema> schemaProperty = schemaProperties.schema;

		Assert.assertEquals(
			String.format(
				"Schema in schema properties with property path %s in %s",
				schemaPropertiesPropertyPath, componentProperties.getClass()),
			expected, schemaProperty.getValue());
	}

	protected ComponentProperties
			getDefaultInitializedComponentPropertiesInstance(
				Class<? extends ComponentProperties> propertyClass)
		throws Exception {

		Constructor<? extends ComponentProperties> constructor =
			propertyClass.getConstructor(String.class);

		ComponentProperties componentProperties = constructor.newInstance(
			"Root");

		componentProperties.init();

		return componentProperties;
	}

	protected NamedThing getNamedThing(String name, Form form) {
		Widget formWidget = form.getWidget(name);

		return formWidget.getContent();
	}

}