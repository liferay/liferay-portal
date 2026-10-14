/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.builder.model.listener.test;

import com.liferay.headless.builder.test.BaseTestCase;
import com.liferay.headless.builder.test.util.ObjectDefinitionTestUtil;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.rest.test.util.ObjectEntryTestUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.HTTPTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.FeatureFlag;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Sergio Jiménez del Coso
 */
@FeatureFlag("LPS-178642")
public class APISchemaRelevantObjectEntryModelListenerTest
	extends BaseTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_objectDefinition = ObjectDefinitionTestUtil.publishObjectDefinition(
			Collections.singletonList(
				ObjectFieldUtil.createObjectField(
					"Text", "String", true, true, null,
					RandomTestUtil.randomString(),
					"x" + RandomTestUtil.randomString(), false)),
			ObjectDefinitionConstants.SCOPE_COMPANY);

		_objectEntry = ObjectEntryTestUtil.addObjectEntry(
			_objectDefinition, "x" + RandomTestUtil.randomString(),
			RandomTestUtil.randomString());
	}

	@Test
	public void test() throws Exception {
		JSONObject apiApplicationJSONObject = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				"applicationStatus", "unpublished"
			).put(
				"baseURL", StringUtil.toLowerCase(RandomTestUtil.randomString())
			).put(
				"title", RandomTestUtil.randomString()
			).toString(),
			"headless-builder/applications", Http.Method.POST);

		JSONObject jsonObject1 = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				"mainObjectDefinitionERC", "L_USER"
			).put(
				"name", RandomTestUtil.randomString()
			).put(
				"r_apiApplicationToAPISchemas_l_apiApplicationId",
				apiApplicationJSONObject.getLong("id")
			).toString(),
			"headless-builder/schemas", Http.Method.POST);

		Assert.assertEquals("BAD_REQUEST", jsonObject1.get("status"));
		Assert.assertEquals(
			"An API schema must be a modifiable object definition.",
			jsonObject1.get("title"));

		jsonObject1 = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				"mainObjectDefinitionERC", RandomTestUtil.randomString()
			).put(
				"name", RandomTestUtil.randomString()
			).put(
				"r_apiApplicationToAPISchemas_l_apiApplicationId",
				apiApplicationJSONObject.getLong("id")
			).toString(),
			"headless-builder/schemas", Http.Method.POST);

		Assert.assertEquals("BAD_REQUEST", jsonObject1.get("status"));
		Assert.assertEquals(
			"An API schema must be an existing object definition.",
			jsonObject1.get("title"));

		jsonObject1 = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				"mainObjectDefinitionERC",
				_objectDefinition.getExternalReferenceCode()
			).put(
				"name", RandomTestUtil.randomString()
			).toString(),
			"headless-builder/schemas", Http.Method.POST);

		Assert.assertEquals("BAD_REQUEST", jsonObject1.get("status"));
		Assert.assertEquals(
			"An API schema must be related to an API application.",
			jsonObject1.get("title"));

		jsonObject1 = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				"mainObjectDefinitionERC",
				_objectDefinition.getExternalReferenceCode()
			).put(
				"name", RandomTestUtil.randomString()
			).toString(),
			"headless-builder/schemas", Http.Method.POST);

		Assert.assertEquals("BAD_REQUEST", jsonObject1.get("status"));
		Assert.assertEquals(
			"An API schema must be related to an API application.",
			jsonObject1.get("title"));

		jsonObject1 = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				"mainObjectDefinitionERC",
				_objectDefinition.getExternalReferenceCode()
			).put(
				"name", RandomTestUtil.randomString()
			).put(
				"r_apiApplicationToAPISchemas_l_apiApplicationErc",
				_objectEntry.getExternalReferenceCode()
			).toString(),
			"headless-builder/schemas", Http.Method.POST);

		Assert.assertEquals("BAD_REQUEST", jsonObject1.get("status"));
		Assert.assertEquals(
			"An API schema must be related to an API application.",
			jsonObject1.get("title"));

		jsonObject1 = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				"description", RandomTestUtil.randomString(281)
			).put(
				"mainObjectDefinitionERC",
				_objectDefinition.getExternalReferenceCode()
			).put(
				"name", RandomTestUtil.randomString()
			).put(
				"r_apiApplicationToAPISchemas_l_apiApplicationId",
				apiApplicationJSONObject.getLong("id")
			).toString(),
			"headless-builder/schemas", Http.Method.POST);

		Assert.assertEquals("BAD_REQUEST", jsonObject1.get("status"));
		Assert.assertEquals(
			"Object entry value exceeds the maximum length of 280 characters " +
				"for object field \"description\"",
			jsonObject1.get("title"));

		JSONObject jsonObject2 = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				"mainObjectDefinitionERC",
				_objectDefinition.getExternalReferenceCode()
			).put(
				"name", _API_SCHEMA_NAME
			).put(
				"r_apiApplicationToAPISchemas_l_apiApplicationId",
				apiApplicationJSONObject.getLong("id")
			).toString(),
			"headless-builder/schemas", Http.Method.POST);

		Assert.assertEquals(
			0,
			jsonObject2.getJSONObject(
				"status"
			).get(
				"code"
			));

		jsonObject1 = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				"mainObjectDefinitionERC",
				_objectDefinition.getExternalReferenceCode()
			).put(
				"name", _API_SCHEMA_NAME
			).put(
				"r_apiApplicationToAPISchemas_l_apiApplicationId",
				apiApplicationJSONObject.getLong("id")
			).toString(),
			"headless-builder/schemas", Http.Method.POST);

		Assert.assertEquals("BAD_REQUEST", jsonObject1.get("status"));
		Assert.assertEquals(
			"There is an API schema with the same name in the API application.",
			jsonObject1.get("title"));

		jsonObject1 = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				"mainObjectDefinitionERC",
				_objectDefinition.getExternalReferenceCode()
			).put(
				"name", RandomTestUtil.randomString()
			).put(
				"r_apiApplicationToAPISchemas_l_apiApplicationId",
				jsonObject2.getLong("id")
			).toString(),
			"headless-builder/schemas", Http.Method.POST);

		Assert.assertEquals("BAD_REQUEST", jsonObject1.get("status"));
		Assert.assertEquals(
			"The value is invalid for object field " +
				"\"r_apiApplicationToAPISchemas_l_apiApplicationId\"",
			jsonObject1.get("title"));
	}

	private static final String _API_SCHEMA_NAME =
		RandomTestUtil.randomString();

	@DeleteAfterTestRun
	private ObjectDefinition _objectDefinition;

	private ObjectEntry _objectEntry;

}