/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.talend.common.json;

import com.liferay.talend.BaseTestCase;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Igor Beslic
 */
public class JsonFinderTest extends BaseTestCase {

	@Test
	public void testGetDescendantJsonArray() {
		JsonFinder jsonFinder = new JsonFinder();

		JsonObject jsonObject = readObject("openapi.json");

		JsonArray descendantJsonArray = jsonFinder.getDescendantJsonArray(
			"invalid>path>to>array", jsonObject);

		Assert.assertNotNull(
			"Expect non null json array object", descendantJsonArray);
		Assert.assertEquals(
			"Expect empty json array", JsonValue.EMPTY_JSON_ARRAY,
			descendantJsonArray);

		descendantJsonArray = jsonFinder.getDescendantJsonArray(
			"components>schemas>Option>properties>fieldType>enum", jsonObject);

		Assert.assertEquals(
			"Expected json array size", 6, descendantJsonArray.size());
	}

	@Test
	public void testGetDescendantJsonObject() {
		JsonFinder jsonFinder = new JsonFinder();

		JsonObject jsonObject = readObject("openapi.json");

		JsonObject descendantJsonObject = jsonFinder.getDescendantJsonObject(
			"invalid>path>to>object", jsonObject);

		Assert.assertNotNull(
			"Expect non null json object", descendantJsonObject);

		Assert.assertEquals(
			"Expect empty json object", JsonValue.EMPTY_JSON_OBJECT,
			descendantJsonObject);
	}

	@Test
	public void testHasJsonObject() {
		JsonFinder jsonFinder = new JsonFinder();

		JsonObject jsonObject = readObject("openapi.json");

		Assert.assertFalse(
			"Expect path \"invalid>path\" is invalid",
			jsonFinder.hasJsonObject("invalid>path", jsonObject));
		Assert.assertFalse(
			"Expect empty path is invalid",
			jsonFinder.hasJsonObject("", jsonObject));
		Assert.assertFalse(
			"Expect path \"invalid\" is invalid",
			jsonFinder.hasJsonObject("invalid", jsonObject));
		Assert.assertTrue(
			"Expect path \"info\" is valid",
			jsonFinder.hasJsonObject("info", jsonObject));
		Assert.assertTrue(
			"Expect path \"info>description\" is valid",
			jsonFinder.hasJsonObject("info>description", jsonObject));
	}

}