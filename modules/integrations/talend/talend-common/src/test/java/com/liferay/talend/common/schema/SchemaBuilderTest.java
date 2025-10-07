/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.talend.common.schema;

import com.liferay.talend.BaseTestCase;
import com.liferay.talend.common.oas.OASException;
import com.liferay.talend.common.oas.constants.OASConstants;

import java.util.List;

import javax.json.JsonObject;

import org.apache.avro.Schema;

import org.hamcrest.CoreMatchers;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.talend.daikon.avro.AvroUtils;

/**
 * @author Zoltán Takács
 * @author Igor Beslic
 */
public class SchemaBuilderTest extends BaseTestCase {

	@Before
	public void setUp() {
		if (_oasJsonObject != null) {
			return;
		}

		_oasJsonObject = readObject("openapi.json");
	}

	@Test
	public void testBooleanSchemaFieldsForProducts() {
		String endpoint = "/v1.0/catalogs/{siteId}/product";

		_assertValidProductSchema(
			_getSchema(endpoint, OASConstants.OPERATION_POST));
	}

	@Test
	public void testGetEntitySchema() {
		Schema productSchema = _schemaBuilder.getEntitySchema(
			"Product", _oasJsonObject);

		_assertValidProductSchema(productSchema);
	}

	@Test
	public void testInferSchemaFieldsCycleReference() {
		String endpoint = "/v1.0/cycle_reference";

		Schema schema = _getSchema(
			endpoint, OASConstants.OPERATION_GET,
			readObject("openapi_data_types.json"));

		List<Schema.Field> fields = schema.getFields();

		Assert.assertThat(fields.size(), CoreMatchers.equalTo(2));

		Schema.Field field = schema.getField("parentBranch_id");

		Schema fieldSchema = AvroUtils.unwrapIfNullable(field.schema());

		Assert.assertTrue(
			"OAS integer in nested object maps to AVRO long",
			AvroUtils.isSameType(fieldSchema, AvroUtils._long()));
	}

	@Test
	public void testInferSchemaFieldsMultipleNesting() {
		String endpoint = "/v1.0/organization";

		Schema schema = _getSchema(
			endpoint, OASConstants.OPERATION_GET,
			readObject("openapi_data_types.json"));

		List<Schema.Field> fields = schema.getFields();

		Assert.assertThat(fields.size(), CoreMatchers.equalTo(6));

		Schema.Field field = schema.getField("location_address_street");

		Schema fieldSchema = AvroUtils.unwrapIfNullable(field.schema());

		Assert.assertTrue(
			"OAS integer in nested object maps to AVRO integer",
			AvroUtils.isSameType(fieldSchema, AvroUtils._string()));
	}

	@Test
	public void testInferSchemaForDeleteOperation() {
		String endpoint =
			"/v1.0/products/by-externalReferenceCode/{externalReferenceCode}";

		Assert.assertFalse(
			AvroUtils.isSchemaEmpty(
				_getSchema(endpoint, OASConstants.OPERATION_DELETE)));
	}

	@Test
	public void testInferSchemaForGetOperation() {
		String endpoint = "/v1.0/catalogs/{siteId}/products";

		Schema schema = _getSchema(endpoint, OASConstants.OPERATION_GET);

		Assert.assertFalse(AvroUtils.isSchemaEmpty(schema));

		_assertValidProductSchema(schema);
	}

	@Test
	public void testInferSchemaForInsertOperation() {
		String endpoint = "/v1.0/catalogs/{siteId}/product";

		Assert.assertFalse(
			AvroUtils.isSchemaEmpty(
				_getSchema(endpoint, OASConstants.OPERATION_POST)));
	}

	@Test
	public void testInferSchemaForUpdateOperation() {
		String endpoint =
			"/v1.0/products/by-externalReferenceCode/{externalReferenceCode}";

		Assert.assertFalse(
			AvroUtils.isSchemaEmpty(
				_getSchema(endpoint, OASConstants.OPERATION_PATCH)));
	}

	@Test(expected = OASException.class)
	public void testInferSchemaInvalidRequestContentBody() {
		String endpoint = "/v1.0/organization";

		_getSchema(
			endpoint, OASConstants.OPERATION_POST,
			readObject("openapi_data_types.json"));
	}

	@Test
	public void testInferSchemaIterableRequestContentBody() {
		String endpoint = "/v1.0/organization";

		Schema schema = _getSchema(
			endpoint, OASConstants.OPERATION_PATCH,
			readObject("openapi_data_types.json"));

		List<Schema.Field> fields = schema.getFields();

		Boolean iterable = (Boolean)schema.getObjectProp("iterable");

		Assert.assertNotNull(iterable);

		Assert.assertThat(fields.size(), CoreMatchers.equalTo(6));

		Schema.Field field = schema.getField("id");

		Schema fieldSchema = AvroUtils.unwrapIfNullable(field.schema());

		Assert.assertTrue(
			"OAS integer in iterable request body maps to AVRO long",
			AvroUtils.isSameType(fieldSchema, AvroUtils._long()));
	}

	@Test(expected = OASException.class)
	public void testInferSchemaNonexistingSchema() {
		String endpoint = "/v1.0/schema_builder_breaker";

		_getSchema(endpoint, OASConstants.OPERATION_GET);
	}

	@Test
	public void testIntegerSchemaFieldsForProducts() {
		String endpoint = "/v1.0/catalogs/{siteId}/product";

		Schema schema = _getSchema(endpoint, OASConstants.OPERATION_POST);

		List<Schema.Field> fields = schema.getFields();

		Assert.assertThat(fields.size(), CoreMatchers.equalTo(48));

		Schema.Field field = schema.getField("configuration_minStockQuantity");

		Schema fieldSchema = AvroUtils.unwrapIfNullable(field.schema());

		Assert.assertTrue(
			"OAS integer in nested object maps to AVRO integer",
			AvroUtils.isSameType(fieldSchema, AvroUtils._int()));
	}

	@Test
	public void testLongSchemaFieldsForProducts() {
		String endpoint = "/v1.0/catalogs/{siteId}/product";

		Schema schema = _getSchema(endpoint, OASConstants.OPERATION_POST);

		List<Schema.Field> fields = schema.getFields();

		Assert.assertThat(fields.size(), CoreMatchers.equalTo(48));

		Schema.Field field = schema.getField("id");

		Schema fieldSchema = AvroUtils.unwrapIfNullable(field.schema());

		Assert.assertTrue(
			"OAS long maps to AVRO long",
			AvroUtils.isSameType(fieldSchema, AvroUtils._long()));

		field = schema.getField("subscriptionConfiguration_numberOfLength");

		fieldSchema = AvroUtils.unwrapIfNullable(field.schema());

		Assert.assertTrue(
			"OAS long in nested object maps to AVRO long",
			AvroUtils.isSameType(fieldSchema, AvroUtils._long()));
	}

	@Test
	public void testOpenAPISpecification() {
		Assert.assertTrue("Test", _oasJsonObject.containsKey("openapi"));
	}

	@Test
	public void testStringSchemaFieldsForProducts() {
		String endpoint = "/v1.0/catalogs/{siteId}/product";

		Schema schema = _getSchema(endpoint, OASConstants.OPERATION_POST);

		List<Schema.Field> fields = schema.getFields();

		Assert.assertThat(fields.size(), CoreMatchers.equalTo(48));

		Schema.Field field = schema.getField("productType");

		Schema fieldSchema = AvroUtils.unwrapIfNullable(field.schema());

		Assert.assertTrue(
			"OAS string maps to AVRO string",
			AvroUtils.isSameType(fieldSchema, AvroUtils._string()));

		field = schema.getField("taxConfiguration_taxCategory");

		fieldSchema = AvroUtils.unwrapIfNullable(field.schema());

		Assert.assertTrue(
			"OAS string in nested object maps to AVRO string",
			AvroUtils.isSameType(fieldSchema, AvroUtils._string()));

		field = schema.getField("description");

		fieldSchema = AvroUtils.unwrapIfNullable(field.schema());

		Assert.assertTrue(
			"OAS dictionary maps to AVRO string",
			AvroUtils.isSameType(fieldSchema, AvroUtils._string()));

		field = schema.getField("expando");

		fieldSchema = AvroUtils.unwrapIfNullable(field.schema());

		Assert.assertTrue(
			"OAS free form object maps to AVRO string",
			AvroUtils.isSameType(fieldSchema, AvroUtils._string()));

		field = schema.getField("categories");

		fieldSchema = AvroUtils.unwrapIfNullable(field.schema());

		Assert.assertTrue(
			"OAS string maps to AVRO string",
			AvroUtils.isSameType(fieldSchema, AvroUtils._string()));
	}

	private void _assertValidProductSchema(Schema schema) {
		List<Schema.Field> fields = schema.getFields();

		Assert.assertThat(fields.size(), CoreMatchers.equalTo(48));

		Schema.Field field = schema.getField("active");

		Schema fieldSchema = AvroUtils.unwrapIfNullable(field.schema());

		Assert.assertTrue(
			"OAS boolean maps to AVRO boolean",
			AvroUtils.isSameType(fieldSchema, AvroUtils._boolean()));

		field = schema.getField("subscriptionConfiguration_enable");

		fieldSchema = AvroUtils.unwrapIfNullable(field.schema());

		Assert.assertTrue(
			"OAS boolean in nested object maps to AVRO boolean",
			AvroUtils.isSameType(fieldSchema, AvroUtils._boolean()));
	}

	private Schema _getSchema(String endpoint, String operation) {
		return _getSchema(endpoint, operation, _oasJsonObject);
	}

	private Schema _getSchema(
		String endpoint, String operation, JsonObject oasJsonObject) {

		JsonObject endpointsJsonObject = oasJsonObject.getJsonObject(
			OASConstants.PATHS);

		Assert.assertTrue(endpointsJsonObject.containsKey(endpoint));

		return _schemaBuilder.inferSchema(endpoint, operation, oasJsonObject);
	}

	private JsonObject _oasJsonObject;
	private final SchemaBuilder _schemaBuilder = new SchemaBuilder();

}