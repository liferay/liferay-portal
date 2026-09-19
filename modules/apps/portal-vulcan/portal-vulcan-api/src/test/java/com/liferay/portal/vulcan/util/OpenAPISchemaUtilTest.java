/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.util;

import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;

import java.util.Collections;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Nathaly Gomes
 */
public class OpenAPISchemaUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testGetReference() {
		Assert.assertEquals(
			_REFERENCE,
			OpenAPISchemaUtil.getReference(_getSchemaWithReference()));

		ArraySchema arraySchema = new ArraySchema();

		arraySchema.setItems(_getSchemaWithReference());

		Assert.assertEquals(
			_REFERENCE, OpenAPISchemaUtil.getReference(arraySchema));

		Assert.assertEquals(
			_REFERENCE,
			OpenAPISchemaUtil.getReference(
				OpenAPISchemaUtil.setDescription(
					_DESCRIPTION, _getSchemaWithReference())));
	}

	@Test
	public void testSetDescriptionWithNullDescription() {
		Schema schema = _getSchemaWithReference();

		Assert.assertSame(
			schema, OpenAPISchemaUtil.setDescription(null, schema));
		Assert.assertNull(schema.getAllOf());
		Assert.assertNull(schema.getDescription());
	}

	@Test
	public void testSetDescriptionWithReference() {
		Schema schema = _getSchemaWithReference();

		schema.setExtensions(
			Collections.singletonMap(
				RandomTestUtil.randomString(), RandomTestUtil.randomString()));

		Schema wrapperSchema = OpenAPISchemaUtil.setDescription(
			_DESCRIPTION, schema);

		Assert.assertNotSame(schema, wrapperSchema);
		Assert.assertNull(wrapperSchema.get$ref());
		Assert.assertEquals(
			Collections.singletonList(schema), wrapperSchema.getAllOf());
		Assert.assertEquals(_DESCRIPTION, wrapperSchema.getDescription());
		Assert.assertEquals(
			schema.getExtensions(), wrapperSchema.getExtensions());
	}

	@Test
	public void testSetDescriptionWithoutReference() {
		Schema schema = new ObjectSchema();

		Assert.assertSame(
			schema, OpenAPISchemaUtil.setDescription(_DESCRIPTION, schema));
		Assert.assertNull(schema.getAllOf());
		Assert.assertEquals(_DESCRIPTION, schema.getDescription());
	}

	@Test
	public void testSetReferenceWithDescription() {
		Schema schema = new ObjectSchema();

		schema.setDescription(_DESCRIPTION);

		OpenAPISchemaUtil.setReference(_REFERENCE, schema);

		Assert.assertNull(schema.get$ref());
		Assert.assertEquals(_DESCRIPTION, schema.getDescription());
		Assert.assertEquals(_REFERENCE, OpenAPISchemaUtil.getReference(schema));
	}

	@Test
	public void testSetReferenceWithNullDescription() {
		Schema schema = new ObjectSchema();

		OpenAPISchemaUtil.setReference(_REFERENCE, schema);

		Assert.assertEquals(_REFERENCE, schema.get$ref());
		Assert.assertNull(schema.getAllOf());
	}

	private Schema _getSchemaWithReference() {
		Schema schema = new ObjectSchema();

		schema.set$ref(_REFERENCE);

		return schema;
	}

	private static final String _DESCRIPTION = RandomTestUtil.randomString();

	private static final String _REFERENCE = "#/components/schemas/Test";

}