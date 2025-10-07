/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mule.internal.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.NullNode;

import com.liferay.mule.internal.util.JsonNodeReader;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Matija Petanjek
 */
public class JsonNodeReaderTest {

	@Before
	public void setUp() throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();

		Class<?> clazz = getClass();

		ClassLoader classLoader = clazz.getClassLoader();

		InputStream inputStream = classLoader.getResourceAsStream(
			"com/liferay/mule/internal/json/example.json");

		jsonNode = objectMapper.readTree(inputStream);
	}

	@Test
	public void testFetchDescendantJsonNode() {
		JsonNodeReader jsonNodeReader = new JsonNodeReader();

		JsonNode jsonNode = jsonNodeReader.fetchDescendantJsonNode(
			this.jsonNode, "fieldName2>nestedFieldName1>nestedFieldNameWith/");

		Assert.assertEquals("nestedValue1", jsonNode.textValue());
	}

	@Test
	public void testFetchDescendantJsonNodeWithNonexistentField() {
		JsonNodeReader jsonNodeReader = new JsonNodeReader();

		JsonNode jsonNode = jsonNodeReader.fetchDescendantJsonNode(
			this.jsonNode, "fieldName3>nestedFieldName1>nestedFieldNameWith/");

		Assert.assertTrue(jsonNode instanceof NullNode);
	}

	@Test
	public void testFetchDescendantJsonNodeWithNonexistentTopLevelField() {
		JsonNodeReader jsonNodeReader = new JsonNodeReader();

		JsonNode jsonNode = jsonNodeReader.fetchDescendantJsonNode(
			this.jsonNode, "fieldName3");

		Assert.assertTrue(jsonNode instanceof NullNode);
	}

	@Test
	public void testFetchDescendantJsonNodeWithTopLevelField() {
		JsonNodeReader jsonNodeReader = new JsonNodeReader();

		JsonNode jsonNode = jsonNodeReader.fetchDescendantJsonNode(
			this.jsonNode, "fieldName1");

		Assert.assertEquals("value1", jsonNode.textValue());
	}

	@Test
	public void testGetDescendantJsonNode() {
		JsonNodeReader jsonNodeReader = new JsonNodeReader();

		JsonNode jsonNode = jsonNodeReader.getDescendantJsonNode(
			this.jsonNode, "fieldName2>nestedFieldName1>nestedFieldNameWith/");

		Assert.assertEquals("nestedValue1", jsonNode.textValue());
	}

	@Test(expected = NullPointerException.class)
	public void testGetDescendantJsonNodeWithNonexistentField() {
		JsonNodeReader jsonNodeReader = new JsonNodeReader();

		jsonNodeReader.getDescendantJsonNode(
			jsonNode, "fieldName3>nestedFieldName1");
	}

	@Test(expected = NullPointerException.class)
	public void testGetDescendantJsonNodeWithNonexistentTopLevelField() {
		JsonNodeReader jsonNodeReader = new JsonNodeReader();

		jsonNodeReader.getDescendantJsonNode(jsonNode, "fieldName3");
	}

	@Test
	public void testGetDescendantJsonNodeWithTopLevelField() {
		JsonNodeReader jsonNodeReader = new JsonNodeReader();

		JsonNode jsonNode = jsonNodeReader.getDescendantJsonNode(
			this.jsonNode, "fieldName1");

		Assert.assertEquals("value1", jsonNode.textValue());
	}

	@Test
	public void testHasPath() {
		JsonNodeReader jsonNodeReader = new JsonNodeReader();

		Assert.assertTrue(
			jsonNodeReader.hasPath(
				jsonNode, "fieldName2>nestedFieldName1>nestedFieldNameWith/"));
		Assert.assertFalse(
			jsonNodeReader.hasPath(
				jsonNode, "fieldName1>nestedFieldName1>nestedFieldNameWith/"));
	}

	private JsonNode jsonNode;

}