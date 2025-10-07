/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.talend.runtime.writer;

import com.liferay.talend.BaseTestCase;
import com.liferay.talend.avro.JsonObjectIndexedRecordConverter;
import com.liferay.talend.common.schema.SchemaUtils;
import com.liferay.talend.properties.output.LiferayOutputProperties;
import com.liferay.talend.properties.parameters.RequestParameterProperties;
import com.liferay.talend.properties.resource.LiferayResourceProperties;
import com.liferay.talend.properties.resource.Operation;
import com.liferay.talend.runtime.LiferayRequestContentAggregatorSink;
import com.liferay.talend.runtime.LiferaySink;

import java.io.IOException;

import java.math.BigDecimal;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonValue;

import org.apache.avro.Schema;
import org.apache.avro.generic.IndexedRecord;

import org.junit.Assert;
import org.junit.Test;

import org.talend.daikon.properties.property.Property;

/**
 * @author Igor Beslic
 */
public class LiferayWriterTest extends BaseTestCase {

	@Test
	public void testWrite() throws Exception {
		String openApiModule = "/headless-openapi-module/v1.0";
		String endpoint = "/catalogs/{siteId}/product";

		LiferayRequestContentAggregatorSink
			liferayRequestContentAggregatorSink =
				new LiferayRequestContentAggregatorSink();

		LiferayOutputProperties testLiferayOutputProperties =
			_getLiferayOutputProperties(
				Operation.Insert, openApiModule, _OAS_URL, endpoint, "siteId");

		JsonObject oasJsonObject = readObject("openapi.json");

		Schema postContentSchema = getSchema(
			"/v1.0/catalogs/{siteId}/product", "POST", oasJsonObject);

		testLiferayOutputProperties.resource.inboundSchemaProperties.schema.
			setValue(postContentSchema);
		testLiferayOutputProperties.resource.outboundSchemaProperties.schema.
			setValue(postContentSchema);

		LiferayWriter liferayWriter = new LiferayWriter(
			new LiferayWriteOperation(
				liferayRequestContentAggregatorSink,
				testLiferayOutputProperties),
			testLiferayOutputProperties);

		liferayWriter.open("aaaa-bbbb-cccc-dddd");

		liferayWriter.write(
			_createIndexedRecordFromFile(
				"product_content.json", postContentSchema));

		JsonValue outputJsonValue =
			liferayRequestContentAggregatorSink.getOutputJsonValue();

		Assert.assertTrue(outputJsonValue instanceof JsonObject);

		JsonObject outputJsonObject = outputJsonValue.asJsonObject();

		Assert.assertTrue(outputJsonObject.containsKey("name"));

		JsonObject nameJsonObject = outputJsonObject.getJsonObject("name");

		Assert.assertNotNull(nameJsonObject);
		Assert.assertTrue(nameJsonObject.containsKey("hr_HR"));
	}

	@Test
	public void testWriteArray() throws Exception {
		String openApiModule = "/headless-commerce-admin-catalog/v1.0";
		String endpoint = "/v1.0/products/{id}/categories";

		LiferayRequestContentAggregatorSink
			liferayRequestContentAggregatorSink =
				new LiferayRequestContentAggregatorSink();

		LiferayOutputProperties testLiferayOutputProperties =
			_getLiferayOutputProperties(
				Operation.Update, openApiModule, _OAS_URL, endpoint, "id");

		JsonObject oasJsonObject = readObject(
			"headless-commerce-admin-catalog-openapi.json");

		Schema patchContentSchema = getSchema(
			endpoint, Operation.Update.getHttpMethod(), oasJsonObject);

		Object iterableObjectProp = patchContentSchema.getObjectProp(
			"iterable");

		Assert.assertNotNull(iterableObjectProp);
		Assert.assertTrue(iterableObjectProp instanceof Boolean);
		Assert.assertTrue((Boolean)iterableObjectProp);

		testLiferayOutputProperties.resource.inboundSchemaProperties.schema.
			setValue(patchContentSchema);
		testLiferayOutputProperties.resource.outboundSchemaProperties.schema.
			setValue(patchContentSchema);

		LiferayWriter liferayWriter = new LiferayWriter(
			new LiferayWriteOperation(
				liferayRequestContentAggregatorSink,
				testLiferayOutputProperties),
			testLiferayOutputProperties);

		liferayWriter.open("aaaa-bbbb-cccc-dddd");

		liferayWriter.write(
			_createIndexedRecordFromFile(
				"category_content.json", patchContentSchema));

		JsonValue outputJsonValue =
			liferayRequestContentAggregatorSink.getOutputJsonValue();

		JsonArray jsonArray = outputJsonValue.asJsonArray();

		Assert.assertNotNull(jsonArray);
		Assert.assertEquals(1, jsonArray.size());

		Iterable<IndexedRecord> successfulWrites =
			liferayWriter.getSuccessfulWrites();

		Iterator<IndexedRecord> iterator = successfulWrites.iterator();

		Assert.assertNotNull(iterator.next());
		Assert.assertFalse(iterator.hasNext());
	}

	@Test
	public void testWriteBigDecimal() throws Exception {
		String openApiModule = "/headless-openapi-module/v1.0";
		String endpoint = "/bigdecimal/{id}";

		LiferayRequestContentAggregatorSink
			liferayRequestContentAggregatorSink =
				new LiferayRequestContentAggregatorSink();

		LiferayOutputProperties testLiferayOutputProperties =
			_getLiferayOutputProperties(
				Operation.Update, openApiModule, _OAS_URL, endpoint, "id");

		JsonObject oasJsonObject = readObject("openapi_data_types.json");

		Schema patchContentSchema = getSchema(
			"/v1.0/bigdecimal/{id}", "PATCH", oasJsonObject);

		testLiferayOutputProperties.resource.inboundSchemaProperties.schema.
			setValue(patchContentSchema);
		testLiferayOutputProperties.resource.outboundSchemaProperties.schema.
			setValue(patchContentSchema);

		LiferayWriter liferayWriter = new LiferayWriter(
			new LiferayWriteOperation(
				liferayRequestContentAggregatorSink,
				testLiferayOutputProperties),
			testLiferayOutputProperties);

		liferayWriter.open("aaaa-bbbb-cccc-dddd");

		liferayWriter.write(
			_createIndexedRecordFromFile(
				"bigdecimal_content.json", patchContentSchema));

		JsonValue outputJsonValue =
			liferayRequestContentAggregatorSink.getOutputJsonValue();

		JsonObject outputJsonObject = outputJsonValue.asJsonObject();

		Assert.assertTrue(
			"Output has bigDecimal1",
			outputJsonObject.containsKey("bigDecimal1"));

		JsonNumber bigDecimalNumber = outputJsonObject.getJsonNumber(
			"bigDecimal1");

		Assert.assertEquals(
			"Field bigDecimal1 value", new BigDecimal("1.97797"),
			bigDecimalNumber.bigDecimalValue());

		bigDecimalNumber = outputJsonObject.getJsonNumber("bigDecimal2");

		Assert.assertEquals(
			"Field bigDecimal2 value",
			new BigDecimal("0.0000000000000000197797"),
			bigDecimalNumber.bigDecimalValue());
	}

	@Test
	public void testWriteDynamicEndpointUrl() throws Exception {
		String openApiModule = "/headless-openapi-module/v1.0";
		String endpoint = "/catalogs/{siteId}/product";

		LiferayRequestContentAggregatorSink
			liferayRequestContentAggregatorSink =
				new LiferayRequestContentAggregatorSink();

		LiferayOutputProperties testLiferayOutputProperties =
			new LiferayOutputProperties(
				"testLiferayOutputProperties", Operation.Insert, openApiModule,
				_OAS_URL, endpoint, Arrays.asList("siteId"),
				Arrays.asList("path"), Collections.emptyList());

		JsonObject oasJsonObject = readObject("openapi.json");

		Schema postContentSchema = getSchema(
			"/v1.0/catalogs/{siteId}/product", "POST", oasJsonObject);

		testLiferayOutputProperties.resource.inboundSchemaProperties.schema.
			setValue(postContentSchema);
		testLiferayOutputProperties.resource.outboundSchemaProperties.schema.
			setValue(postContentSchema);
		testLiferayOutputProperties.resource.rejectSchemaProperties.schema.
			setValue(SchemaUtils.createRejectSchema(postContentSchema));

		LiferayWriter liferayWriter = new LiferayWriter(
			new LiferayWriteOperation(
				liferayRequestContentAggregatorSink,
				testLiferayOutputProperties),
			testLiferayOutputProperties);

		liferayWriter.open("aaaa-bbbb-cccc-dddd");

		IndexedRecord indexedRecord = _createIndexedRecordFromFile(
			"product_content.json", postContentSchema);

		liferayWriter.write(indexedRecord);

		Iterable<IndexedRecord> iterable = liferayWriter.getSuccessfulWrites();

		Iterator<IndexedRecord> iterator = iterable.iterator();

		Assert.assertFalse(iterator.hasNext());

		iterable = liferayWriter.getRejectedWrites();

		iterator = iterable.iterator();

		Assert.assertTrue(iterator.hasNext());

		IndexedRecord rejectedIndexedRecord = iterator.next();

		Assert.assertNotNull(rejectedIndexedRecord);

		Schema rejectSchema = rejectedIndexedRecord.getSchema();

		Schema.Field errorMessageField = rejectSchema.getField("errorMessage");

		String errorMessage = (String)rejectedIndexedRecord.get(
			errorMessageField.pos());

		Assert.assertEquals(
			"The template variable 'siteId' has no value", errorMessage);

		_simulateRuntimeParameterInjection(testLiferayOutputProperties);

		liferayWriter.write(indexedRecord);

		JsonValue outputJsonValue =
			liferayRequestContentAggregatorSink.getOutputJsonValue();

		JsonObject outputJsonObject = outputJsonValue.asJsonObject();

		Assert.assertTrue(
			"Output has name", outputJsonObject.containsKey("name"));
	}

	@Test(expected = IOException.class)
	public void testWriteNullIndexedRecord() throws Exception {
		String openApiModule = "/headless-openapi-module/v1.0";
		String endpoint = "/bigdecimal/{id}";

		LiferayOutputProperties testLiferayOutputProperties =
			_getLiferayOutputProperties(
				Operation.Update, openApiModule, _OAS_URL, endpoint, "id");

		testLiferayOutputProperties.setupProperties();

		LiferayWriter liferayWriter = new LiferayWriter(
			new LiferayWriteOperation(
				new LiferaySink(), testLiferayOutputProperties),
			testLiferayOutputProperties);

		liferayWriter.write(null);
	}

	private IndexedRecord _createIndexedRecordFromFile(
		String name, Schema schema) {

		JsonObjectIndexedRecordConverter jsonObjectIndexedRecordConverter =
			new JsonObjectIndexedRecordConverter(schema);

		return jsonObjectIndexedRecordConverter.toIndexedRecord(
			readObject(name));
	}

	private LiferayOutputProperties _getLiferayOutputProperties(
		Operation operation, String openAPIModule, String apiSpecURL,
		String endpoint, String parameterName) {

		return new LiferayOutputProperties(
			"testLiferayOutputProperties", operation, openAPIModule, apiSpecURL,
			endpoint, Arrays.asList(parameterName), Arrays.asList("path"),
			Arrays.asList("1977"));
	}

	private void _simulateRuntimeParameterInjection(
		LiferayOutputProperties liferayOutputProperties) {

		LiferayResourceProperties liferayResourceProperties =
			liferayOutputProperties.resource;

		RequestParameterProperties requestParameterProperties =
			liferayResourceProperties.parameters;

		Property<List<String>> parameterValueColumn =
			requestParameterProperties.parameterValueColumn;

		parameterValueColumn.setValue(Arrays.asList("1977"));
	}

	private static final String _OAS_URL =
		"http://localhost:8080/o/headless-commerce-admin-catalog/v1.0" +
			"/openapi.json";

}