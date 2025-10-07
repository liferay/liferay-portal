/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.talend.avro;

import com.liferay.talend.common.oas.constants.OASConstants;

import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonValue;

import org.apache.avro.generic.IndexedRecord;

import org.junit.Assert;
import org.junit.Test;

import org.talend.components.api.component.runtime.Result;

/**
 * @author Igor Beslic
 */
public class IndexedRecordJsonObjectConverterTest extends BaseConverterTest {

	@Test
	public void testToJsonObjectWithArrayProperties() throws Exception {
		JsonObjectIndexedRecordConverter jsonObjectIndexedRecordConverter =
			getJsonObjectIndexedRecordConverter(
				"/v1.0/catalogs/{siteId}/products", OASConstants.OPERATION_GET,
				readObject("openapi.json"));

		IndexedRecord indexedRecord =
			jsonObjectIndexedRecordConverter.toIndexedRecord(
				readObject("product_with_nested_images_content.json"));

		Object imagesArray = indexedRecord.get(18);

		Assert.assertEquals(
			"Images field type", String.class, imagesArray.getClass());

		IndexedRecordJsonObjectConverter indexedRecordJsonObjectConverter =
			new IndexedRecordJsonObjectConverter(
				true,
				getSchema(
					"/v1.0/catalogs/{siteId}/product",
					OASConstants.OPERATION_POST, readObject("openapi.json")),
				null, new Result());

		JsonObject jsonObject = _requireJsonObject(
			indexedRecordJsonObjectConverter.toJsonValue(indexedRecord));

		JsonArray imagesJsonArray = jsonObject.getJsonArray("images");

		Assert.assertNotNull(imagesJsonArray);

		JsonValue jsonValue = imagesJsonArray.get(0);

		Assert.assertTrue(
			"Array item is JSON object", jsonValue instanceof JsonObject);

		JsonObject itemJsonObject = jsonValue.asJsonObject();

		Assert.assertEquals(
			"attachment value", "U29tZXRleHQsIHRoaXMgaXMgbm90IGltYWdl",
			itemJsonObject.getString("attachment"));

		JsonNumber idJsonNumber = itemJsonObject.getJsonNumber("id");

		Assert.assertEquals("id value", 1977L, idJsonNumber.longValue());

		JsonValue titleJsonValue = itemJsonObject.get("title");

		Assert.assertTrue(
			"Title field  type is JsonObject",
			titleJsonValue instanceof JsonObject);

		JsonObject titleJsonObject = titleJsonValue.asJsonObject();

		Assert.assertEquals(
			"Title field value", "Image of me",
			titleJsonObject.getString("en_US"));
	}

	@Test
	public void testToJsonObjectWithDictionaryProperties() throws Exception {
		JsonObjectIndexedRecordConverter jsonObjectIndexedRecordConverter =
			getJsonObjectIndexedRecordConverter(
				"/v1.0/catalogs/{siteId}/products", OASConstants.OPERATION_GET,
				readObject("openapi.json"));

		IndexedRecord indexedRecord =
			jsonObjectIndexedRecordConverter.toIndexedRecord(
				readObject("product_content.json"));

		IndexedRecordJsonObjectConverter indexedRecordJsonObjectConverter =
			new IndexedRecordJsonObjectConverter(
				true,
				getSchema(
					"/v1.0/catalogs/{siteId}/product",
					OASConstants.OPERATION_POST, readObject("openapi.json")),
				null, new Result());

		JsonObject jsonObject = _requireJsonObject(
			indexedRecordJsonObjectConverter.toJsonValue(indexedRecord));

		JsonObject descriptionJsonObject = jsonObject.getJsonObject(
			"description");

		Assert.assertNotNull(descriptionJsonObject);

		Assert.assertEquals(
			"dictionary value", "Description of DXP Subscription",
			descriptionJsonObject.getString("en_US"));
	}

	@Test
	public void testToJsonObjectWithNestedObject() throws Exception {
		JsonObjectIndexedRecordConverter jsonObjectIndexedRecordConverter =
			getJsonObjectIndexedRecordConverter(
				"/v1.0/branch", OASConstants.OPERATION_POST,
				readObject("openapi_data_types.json"));

		IndexedRecord indexedRecord =
			jsonObjectIndexedRecordConverter.toIndexedRecord(
				readObject("branch_content.json"));

		IndexedRecordJsonObjectConverter indexedRecordJsonObjectConverter =
			new IndexedRecordJsonObjectConverter(
				true,
				getSchema(
					"/v1.0/branch", OASConstants.OPERATION_POST,
					readObject("openapi_data_types.json")),
				null, new Result());

		JsonObject jsonObject = _requireJsonObject(
			indexedRecordJsonObjectConverter.toJsonValue(indexedRecord));

		Assert.assertTrue(jsonObject.containsKey("parentBranch"));

		jsonObject = jsonObject.getJsonObject("parentBranch");

		Assert.assertEquals("parent branch id", 2006, jsonObject.getInt("id"));
	}

	@Test
	public void testToJsonObjectWithTimeProperties() throws Exception {
		JsonObjectIndexedRecordConverter jsonObjectIndexedRecordConverter =
			getJsonObjectIndexedRecordConverter(
				"/v1.0/timestamp/{code}", OASConstants.OPERATION_GET,
				readObject("openapi_data_types.json"));

		IndexedRecord indexedRecord =
			jsonObjectIndexedRecordConverter.toIndexedRecord(
				readObject("timestamp_content.json"));

		IndexedRecordJsonObjectConverter indexedRecordJsonObjectConverter =
			new IndexedRecordJsonObjectConverter(
				true,
				getSchema(
					"/v1.0/timestamp/{code}", OASConstants.OPERATION_PATCH,
					readObject("openapi_data_types.json")),
				null, new Result());

		JsonObject jsonObject = _requireJsonObject(
			indexedRecordJsonObjectConverter.toJsonValue(indexedRecord));

		Assert.assertEquals(
			"timestamp1 value", "2019-12-02T05:17:09Z",
			jsonObject.getString("timestamp1"));
		Assert.assertEquals(
			"timestamp2 value", "2019-12-02T05:17:09Z",
			jsonObject.getString("timestamp2"));
		Assert.assertEquals(
			"timestamp3 value", "2019-12-02T04:17:09Z",
			jsonObject.getString("timestamp3"));
	}

	private JsonObject _requireJsonObject(JsonValue jsonValue) {
		Assert.assertTrue(jsonValue instanceof JsonObject);

		return jsonValue.asJsonObject();
	}

}