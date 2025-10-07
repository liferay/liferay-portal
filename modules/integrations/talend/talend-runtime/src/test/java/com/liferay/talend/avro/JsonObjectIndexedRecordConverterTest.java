/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.talend.avro;

import com.liferay.talend.avro.exception.ConverterException;
import com.liferay.talend.common.oas.constants.OASConstants;

import java.math.BigDecimal;

import org.apache.avro.Schema;
import org.apache.avro.generic.IndexedRecord;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Igor Beslic
 */
public class JsonObjectIndexedRecordConverterTest extends BaseConverterTest {

	@Test
	public void testToIndexedRecordI18nProperty() {
		JsonObjectIndexedRecordConverter jsonObjectIndexedRecordConverter =
			getJsonObjectIndexedRecordConverter(
				"/v1.0/i18n/{id}", OASConstants.OPERATION_GET,
				readObject("openapi_data_types.json"));

		IndexedRecord indexedRecord =
			jsonObjectIndexedRecordConverter.toIndexedRecord(
				readObject("localized_content.json"));

		Object object = indexedRecord.get(1);

		Assert.assertEquals(
			"{\"en_US\":\"Application of i18n pattern\"," +
				"\"hr_HR\":\"Uporaba i18n predloska\"}",
			object);
	}

	@Test
	public void testToIndexedRecordI18nPropertyNested() {
		JsonObjectIndexedRecordConverter jsonObjectIndexedRecordConverter =
			getJsonObjectIndexedRecordConverter(
				"/v1.0/i18n_nested", OASConstants.OPERATION_GET,
				readObject("openapi_data_types.json"));

		IndexedRecord indexedRecord =
			jsonObjectIndexedRecordConverter.toIndexedRecord(
				readObject("localized_content.json"));

		Object object = indexedRecord.get(3);

		Assert.assertEquals(
			"{\"en_US\":\"i18n pattern within nested\"," +
				"\"hr_HR\":\"i18n predlozak u ugnjezdenom polju\"}",
			object);
	}

	@Test
	public void testToIndexedRecordIfBigDecimalPropertyPresent() {
		JsonObjectIndexedRecordConverter jsonObjectIndexedRecordConverter =
			getJsonObjectIndexedRecordConverter(
				"/v1.0/bigdecimal/{id}", OASConstants.OPERATION_GET,
				readObject("openapi_data_types.json"));

		IndexedRecord indexedRecord =
			jsonObjectIndexedRecordConverter.toIndexedRecord(
				readObject("bigdecimal_content.json"));

		Assert.assertNotNull(
			"Attachment is converted to indexed record", indexedRecord);

		Object bigDecimal1 = indexedRecord.get(
			_posOf("bigDecimal1", indexedRecord.getSchema()));

		Assert.assertEquals(
			"BigDecimal 1 field type", BigDecimal.class,
			bigDecimal1.getClass());

		Assert.assertEquals(
			"BigDecimal 1 field value", new BigDecimal("1.97797"), bigDecimal1);

		Object bigDecimal2 = indexedRecord.get(
			_posOf("bigDecimal2", indexedRecord.getSchema()));

		Assert.assertEquals(
			"BigDecimal 2 field type", BigDecimal.class,
			bigDecimal2.getClass());

		Assert.assertEquals(
			"BigDecimal 2 field value", new BigDecimal("1.97797E-17"),
			bigDecimal2);

		Object doubleObject = indexedRecord.get(
			_posOf("double", indexedRecord.getSchema()));

		Assert.assertEquals(
			"Double field type", Double.class, doubleObject.getClass());

		Assert.assertEquals(
			"Double field value", Double.valueOf("19779.7"), doubleObject);

		Object priceFloat = indexedRecord.get(
			_posOf("float", indexedRecord.getSchema()));

		Assert.assertEquals(
			"Float field type", Float.class, priceFloat.getClass());

		Assert.assertEquals(
			"Float field value", Float.valueOf("19.7797"), priceFloat);

		Object priceNumber = indexedRecord.get(
			_posOf("number", indexedRecord.getSchema()));

		Assert.assertEquals(
			"Number field type", BigDecimal.class, priceNumber.getClass());

		Assert.assertEquals(
			"Number field value", new BigDecimal("1977.97"), priceNumber);
	}

	@Test
	public void testToIndexedRecordIfDateTimeStringPropertyPresent() {
		JsonObjectIndexedRecordConverter jsonObjectIndexedRecordConverter =
			getJsonObjectIndexedRecordConverter(
				"/v1.0/attachments/{id}", OASConstants.OPERATION_GET,
				readObject("openapi.json"));

		IndexedRecord indexedRecord =
			jsonObjectIndexedRecordConverter.toIndexedRecord(
				readObject("attachment_content.json"));

		Assert.assertNotNull(
			"Attachment is converted to indexed record", indexedRecord);

		Object displayDate = indexedRecord.get(1);

		Assert.assertEquals(
			"Display date field type", Long.class, displayDate.getClass());

		Assert.assertEquals(
			"Display date field value", 1320144300000L, displayDate);
	}

	@Test(expected = ConverterException.class)
	public void testToIndexedRecordInvalidI18nProperty1() {
		JsonObjectIndexedRecordConverter jsonObjectIndexedRecordConverter =
			getJsonObjectIndexedRecordConverter(
				"/v1.0/i18n_invalid_1/{id}", OASConstants.OPERATION_GET,
				readObject("openapi_data_types.json"));

		jsonObjectIndexedRecordConverter.toIndexedRecord(
			readObject("localized_content.json"));
	}

	@Test(expected = ConverterException.class)
	public void testToIndexedRecordInvalidI18nProperty2() {
		JsonObjectIndexedRecordConverter jsonObjectIndexedRecordConverter =
			getJsonObjectIndexedRecordConverter(
				"/v1.0/i18n_invalid_2/{id}", OASConstants.OPERATION_GET,
				readObject("openapi_data_types.json"));

		jsonObjectIndexedRecordConverter.toIndexedRecord(
			readObject("localized_content.json"));
	}

	@Test(expected = ConverterException.class)
	public void testToIndexedRecordWithInvalidDateProperty() {
		JsonObjectIndexedRecordConverter jsonObjectIndexedRecordConverter =
			getJsonObjectIndexedRecordConverter(
				"/v1.0/catalogs/{siteId}/products", OASConstants.OPERATION_GET,
				readObject("openapi.json"));

		jsonObjectIndexedRecordConverter.toIndexedRecord(
			readObject("attachment_content_invalid_date_property.json"));
	}

	@Test(expected = ConverterException.class)
	public void testToIndexedRecordWithMissingRequiredProperty() {
		JsonObjectIndexedRecordConverter jsonObjectIndexedRecordConverter =
			getJsonObjectIndexedRecordConverter(
				"/v1.0/catalogs/{siteId}/products", OASConstants.OPERATION_GET,
				readObject("openapi.json"));

		jsonObjectIndexedRecordConverter.toIndexedRecord(
			readObject("product_content_missing_required_property.json"));
	}

	@Test
	public void testToIndexedRecordWithSimpleTypeProperties() {
		JsonObjectIndexedRecordConverter jsonObjectIndexedRecordConverter =
			getJsonObjectIndexedRecordConverter(
				"/v1.0/catalogs/{siteId}/products", OASConstants.OPERATION_GET,
				readObject("openapi.json"));

		IndexedRecord indexedRecord =
			jsonObjectIndexedRecordConverter.toIndexedRecord(
				readObject("product_content.json"));

		Assert.assertNotNull(
			"Product is converted to indexed record", indexedRecord);

		Assert.assertEquals("SKU field value", "3 pcs", indexedRecord.get(13));

		Object id = indexedRecord.get(17);

		Assert.assertEquals("ID field type", Long.class, id.getClass());

		Assert.assertEquals("ID field value", Long.valueOf(19770709), id);

		Object productId = indexedRecord.get(24);

		Assert.assertEquals(
			"Product ID field type", Long.class, productId.getClass());

		Assert.assertEquals(
			"Product ID field value", Long.valueOf(20111101), productId);
	}

	private int _posOf(String fieldName, Schema fieldSchema) {
		Schema.Field field = fieldSchema.getField(fieldName);

		return field.pos();
	}

}