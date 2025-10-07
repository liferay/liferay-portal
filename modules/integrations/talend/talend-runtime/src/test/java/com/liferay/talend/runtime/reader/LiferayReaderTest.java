/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.talend.runtime.reader;

import com.liferay.talend.BaseTestCase;
import com.liferay.talend.properties.input.LiferayInputProperties;
import com.liferay.talend.properties.resource.Operation;
import com.liferay.talend.runtime.LiferayFixedResponseContentSource;

import java.util.Arrays;
import java.util.NoSuchElementException;

import javax.json.JsonObject;
import javax.json.JsonValue;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Igor Beslic
 */
public class LiferayReaderTest extends BaseTestCase {

	@Test(expected = NoSuchElementException.class)
	public void testNoSuchElementException() throws Exception {
		String openAPIModule = "/headless-commerce-admin-catalog/v1.0";
		String endpoint = "/taxonomy-vocabularies/{id}/taxonomy-categories";

		LiferayReader liferayReader = new LiferayReader(
			new LiferayFixedResponseContentSource(
				readObject("page_content.json")),
			_getTLiferayInputProperties(
				Operation.Unavailable, openAPIModule, _OAS_URL, endpoint));

		liferayReader.start();

		liferayReader.advance();

		liferayReader.getCurrentJsonValue();
	}

	@Test
	public void testPaging() throws Exception {
		String openAPIModule = "/headless-commerce-admin-catalog/v1.0";
		String endpoint = "/page-content/{id}/taxonomy-categories";

		LiferayReader liferayReader = new LiferayReader(
			_getLiferayFixedResponseContentSource(),
			_getTLiferayInputProperties(
				Operation.Unavailable, openAPIModule, _OAS_URL, endpoint));

		liferayReader.start();

		Assert.assertTrue(
			"Liferay input reader advanced to next page",
			liferayReader.advance());

		JsonValue currentJsonValue = liferayReader.getCurrentJsonValue();

		Assert.assertNotNull("Current JSON value", currentJsonValue);

		JsonObject jsonObject = currentJsonValue.asJsonObject();

		Assert.assertEquals("Item id value", 80350, jsonObject.getInt("id"));

		Assert.assertFalse(
			"Liferay input reader advanced to next page",
			liferayReader.advance());
	}

	@Test
	public void testStartIfEmptyPageReturned() throws Exception {
		String openAPIModule = "/headless-commerce-admin-catalog/v1.0";
		String endpoint = "/taxonomy-vocabularies/{id}/taxonomy-categories";

		LiferayReader liferayReader = new LiferayReader(
			_getLiferayFixedResponseContentSource(
				readObject("page_no_content.json")),
			_getTLiferayInputProperties(
				Operation.Unavailable, openAPIModule, _OAS_URL, endpoint));

		Assert.assertFalse(
			"Liferay input reader must not be initialized and advanced",
			liferayReader.start());
	}

	@Test
	public void testStartIfPageReturned() throws Exception {
		String openAPIModule = "/headless-commerce-admin-catalog/v1.0";
		String endpoint = "/taxonomy-vocabularies/{id}/taxonomy-categories";

		LiferayReader liferayReader = new LiferayReader(
			_getLiferayFixedResponseContentSource(
				readObject("page_content.json")),
			_getTLiferayInputProperties(
				Operation.Unavailable, openAPIModule, _OAS_URL, endpoint));

		Assert.assertTrue(
			"Liferay input reader must be initialized and advanced",
			liferayReader.start());

		JsonValue currentJsonValue = liferayReader.getCurrentJsonValue();

		Assert.assertNotNull("Current JSON value", currentJsonValue);

		Assert.assertEquals(
			"Subsequent reads with no advancing must return same JSON value",
			currentJsonValue, liferayReader.getCurrentJsonValue());

		Assert.assertFalse(
			"Advance must not advance reader to next record",
			liferayReader.advance());
	}

	private LiferayFixedResponseContentSource
		_getLiferayFixedResponseContentSource() {

		return _getLiferayFixedResponseContentSource(null);
	}

	private LiferayFixedResponseContentSource
		_getLiferayFixedResponseContentSource(JsonObject jsonObject) {

		LiferayFixedResponseContentSource liferayFixedResponseContentSource =
			new LiferayFixedResponseContentSource(jsonObject);

		liferayFixedResponseContentSource.setBaseTestCase(this);

		return liferayFixedResponseContentSource;
	}

	private LiferayInputProperties _getTLiferayInputProperties(
		Operation operation, String openAPIModule, String apiSpecURL,
		String endpoint) {

		return new LiferayInputProperties(
			"testLiferayInputProperties", operation, openAPIModule, apiSpecURL,
			endpoint, Arrays.asList("id"), Arrays.asList("path"),
			Arrays.asList("1234"));
	}

	private static final String _OAS_URL =
		"http://localhost:8080/o/headless-commerce-admin-catalog/v1.0" +
			"/openapi.json";

}