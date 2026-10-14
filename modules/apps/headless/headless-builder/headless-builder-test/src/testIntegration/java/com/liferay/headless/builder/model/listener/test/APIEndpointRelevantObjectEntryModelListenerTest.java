/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.builder.model.listener.test;

import com.liferay.headless.builder.application.APIApplication;
import com.liferay.headless.builder.constants.HeadlessBuilderConstants;
import com.liferay.headless.builder.test.BaseTestCase;
import com.liferay.headless.builder.test.util.ObjectDefinitionTestUtil;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.rest.test.util.ObjectEntryTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.HTTPTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.FeatureFlag;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;

import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

/**
 * @author Sergio Jiménez del Coso
 */
@FeatureFlag("LPS-178642")
public class APIEndpointRelevantObjectEntryModelListenerTest
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
		_siteScopedObjectDefinition =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				Collections.singletonList(
					ObjectFieldUtil.createObjectField(
						"Text", "String", true, true, null,
						RandomTestUtil.randomString(),
						"x" + RandomTestUtil.randomString(), false)),
				ObjectDefinitionConstants.SCOPE_SITE);
		_objectEntry = ObjectEntryTestUtil.addObjectEntry(
			_objectDefinition, "x" + RandomTestUtil.randomString(),
			RandomTestUtil.randomString());
	}

	@Test
	public void testCreateGetAPIEndpoint() throws Exception {
		JSONAssert.assertEquals(
			JSONUtil.put(
				"status", "BAD_REQUEST"
			).put(
				"title",
				"An API endpoint must be related to an API application."
			).toString(),
			HTTPTestUtil.invokeToJSONObject(
				JSONUtil.put(
					"httpMethod", "get"
				).put(
					"name", RandomTestUtil.randomString()
				).put(
					"path",
					StringPool.FORWARD_SLASH +
						StringUtil.toLowerCase(RandomTestUtil.randomString())
				).put(
					"retrieveType",
					APIApplication.Endpoint.RetrieveType.COLLECTION.getValue()
				).put(
					"scope", APIApplication.Endpoint.Scope.COMPANY.getValue()
				).toString(),
				"headless-builder/endpoints", Http.Method.POST
			).toString(),
			JSONCompareMode.STRICT);

		JSONObject apiApplicationJSONObject1 = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				"applicationStatus", "published"
			).put(
				"baseURL", StringUtil.toLowerCase(RandomTestUtil.randomString())
			).put(
				"title", RandomTestUtil.randomString()
			).toString(),
			"headless-builder/applications", Http.Method.POST);

		JSONAssert.assertEquals(
			JSONUtil.put(
				"status", "BAD_REQUEST"
			).put(
				"title",
				"Path can have a maximum of 255 alphanumeric characters."
			).toString(),
			HTTPTestUtil.invokeToJSONObject(
				JSONUtil.put(
					"httpMethod", "get"
				).put(
					"name", RandomTestUtil.randomString()
				).put(
					"path",
					StringBundler.concat(
						StringPool.FORWARD_SLASH,
						StringUtil.toLowerCase(RandomTestUtil.randomString()),
						StringPool.FORWARD_SLASH,
						StringUtil.toLowerCase(RandomTestUtil.randomString()),
						StringPool.COMMA)
				).put(
					"r_apiApplicationToAPIEndpoints_l_apiApplicationId",
					apiApplicationJSONObject1.getLong("id")
				).put(
					"retrieveType",
					APIApplication.Endpoint.RetrieveType.COLLECTION.getValue()
				).put(
					"scope", APIApplication.Endpoint.Scope.COMPANY.getValue()
				).toString(),
				"headless-builder/endpoints", Http.Method.POST
			).toString(),
			JSONCompareMode.STRICT);

		JSONObject apiSchemaJSONObject1 = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				"mainObjectDefinitionERC",
				_objectDefinition.getExternalReferenceCode()
			).put(
				"name", RandomTestUtil.randomString()
			).put(
				"r_apiApplicationToAPISchemas_l_apiApplicationId",
				apiApplicationJSONObject1.getLong("id")
			).toString(),
			"headless-builder/schemas", Http.Method.POST);

		JSONAssert.assertEquals(
			JSONUtil.put(
				"status", "BAD_REQUEST"
			).put(
				"title",
				"Path must contain a path parameter between curly braces."
			).toString(),
			HTTPTestUtil.invokeToJSONObject(
				JSONUtil.put(
					"httpMethod", "get"
				).put(
					"name", RandomTestUtil.randomString()
				).put(
					"path",
					StringBundler.concat(
						StringPool.FORWARD_SLASH,
						StringUtil.toLowerCase(RandomTestUtil.randomString()),
						StringPool.FORWARD_SLASH, StringPool.OPEN_CURLY_BRACE)
				).put(
					"pathParameter", HeadlessBuilderConstants.PATH_PARAMETER_ERC
				).put(
					"r_apiApplicationToAPIEndpoints_l_apiApplicationId",
					apiApplicationJSONObject1.getLong("id")
				).put(
					"r_responseAPISchemaToAPIEndpoints_l_apiSchemaId",
					apiSchemaJSONObject1.getLong("id")
				).put(
					"retrieveType",
					APIApplication.Endpoint.RetrieveType.SINGLE_ELEMENT.
						getValue()
				).put(
					"scope", APIApplication.Endpoint.Scope.COMPANY.getValue()
				).toString(),
				"headless-builder/endpoints", Http.Method.POST
			).toString(),
			JSONCompareMode.STRICT);
		JSONAssert.assertEquals(
			JSONUtil.put(
				"status", "BAD_REQUEST"
			).put(
				"title", "Path must contain only lower case characters."
			).toString(),
			HTTPTestUtil.invokeToJSONObject(
				JSONUtil.put(
					"httpMethod", "get"
				).put(
					"name", RandomTestUtil.randomString()
				).put(
					"path",
					StringBundler.concat(
						StringPool.FORWARD_SLASH,
						StringUtil.toUpperCase(RandomTestUtil.randomString()),
						StringPool.FORWARD_SLASH, StringPool.OPEN_CURLY_BRACE,
						RandomTestUtil.randomString(),
						StringPool.CLOSE_CURLY_BRACE)
				).put(
					"pathParameter", HeadlessBuilderConstants.PATH_PARAMETER_ERC
				).put(
					"r_apiApplicationToAPIEndpoints_l_apiApplicationId",
					apiApplicationJSONObject1.getLong("id")
				).put(
					"r_responseAPISchemaToAPIEndpoints_l_apiSchemaId",
					apiSchemaJSONObject1.getLong("id")
				).put(
					"retrieveType",
					APIApplication.Endpoint.RetrieveType.SINGLE_ELEMENT.
						getValue()
				).put(
					"scope", APIApplication.Endpoint.Scope.COMPANY.getValue()
				).toString(),
				"headless-builder/endpoints", Http.Method.POST
			).toString(),
			JSONCompareMode.STRICT);

		JSONAssert.assertEquals(
			JSONUtil.put(
				"status", "BAD_REQUEST"
			).put(
				"title", "Path must start with the \"/\" character."
			).toString(),
			HTTPTestUtil.invokeToJSONObject(
				JSONUtil.put(
					"httpMethod", "get"
				).put(
					"name", RandomTestUtil.randomString()
				).put(
					"path",
					StringBundler.concat(
						StringUtil.toLowerCase(RandomTestUtil.randomString()),
						StringPool.FORWARD_SLASH, StringPool.COMMA)
				).put(
					"r_apiApplicationToAPIEndpoints_l_apiApplicationId",
					apiApplicationJSONObject1.getLong("id")
				).put(
					"retrieveType",
					APIApplication.Endpoint.RetrieveType.COLLECTION.getValue()
				).put(
					"scope", APIApplication.Endpoint.Scope.COMPANY.getValue()
				).toString(),
				"headless-builder/endpoints", Http.Method.POST
			).toString(),
			JSONCompareMode.STRICT);
		JSONAssert.assertEquals(
			JSONUtil.put(
				"status", "BAD_REQUEST"
			).put(
				"title",
				"Object entry value exceeds the maximum length of 280 " +
					"characters for object field \"description\""
			).toString(),
			HTTPTestUtil.invokeToJSONObject(
				JSONUtil.put(
					"description", RandomTestUtil.randomString(281)
				).put(
					"httpMethod", "get"
				).put(
					"name", RandomTestUtil.randomString()
				).put(
					"path",
					StringPool.FORWARD_SLASH +
						StringUtil.toLowerCase(RandomTestUtil.randomString())
				).put(
					"r_apiApplicationToAPIEndpoints_l_apiApplicationId",
					apiApplicationJSONObject1.getLong("id")
				).put(
					"retrieveType",
					APIApplication.Endpoint.RetrieveType.COLLECTION.getValue()
				).put(
					"scope", APIApplication.Endpoint.Scope.COMPANY.getValue()
				).toString(),
				"headless-builder/endpoints", Http.Method.POST
			).toString(),
			JSONCompareMode.STRICT);
		JSONAssert.assertEquals(
			JSONUtil.put(
				"status", "BAD_REQUEST"
			).put(
				"title",
				"Path parameter cannot be set without a response schema."
			).toString(),
			HTTPTestUtil.invokeToJSONObject(
				JSONUtil.put(
					"httpMethod", "get"
				).put(
					"name", RandomTestUtil.randomString()
				).put(
					"path",
					StringBundler.concat(
						StringPool.FORWARD_SLASH,
						StringUtil.toLowerCase(RandomTestUtil.randomString()),
						StringPool.FORWARD_SLASH, StringPool.OPEN_CURLY_BRACE,
						RandomTestUtil.randomString(),
						StringPool.CLOSE_CURLY_BRACE)
				).put(
					"pathParameter", RandomTestUtil.randomString()
				).put(
					"r_apiApplicationToAPIEndpoints_l_apiApplicationId",
					apiApplicationJSONObject1.getLong("id")
				).put(
					"retrieveType",
					APIApplication.Endpoint.RetrieveType.SINGLE_ELEMENT.
						getValue()
				).put(
					"scope", APIApplication.Endpoint.Scope.COMPANY.getValue()
				).toString(),
				"headless-builder/endpoints", Http.Method.POST
			).toString(),
			JSONCompareMode.STRICT);
		JSONAssert.assertEquals(
			JSONUtil.put(
				"status", "BAD_REQUEST"
			).put(
				"title",
				"Path parameter description cannot be set with empty path " +
					"parameter property."
			).toString(),
			HTTPTestUtil.invokeToJSONObject(
				JSONUtil.put(
					"httpMethod", "get"
				).put(
					"name", RandomTestUtil.randomString()
				).put(
					"path",
					StringBundler.concat(
						StringPool.FORWARD_SLASH,
						StringUtil.toLowerCase(RandomTestUtil.randomString()),
						StringPool.FORWARD_SLASH, StringPool.OPEN_CURLY_BRACE,
						RandomTestUtil.randomString(),
						StringPool.CLOSE_CURLY_BRACE)
				).put(
					"pathParameterDescription", RandomTestUtil.randomString()
				).put(
					"r_apiApplicationToAPIEndpoints_l_apiApplicationId",
					apiApplicationJSONObject1.getLong("id")
				).put(
					"retrieveType",
					APIApplication.Endpoint.RetrieveType.SINGLE_ELEMENT.
						getValue()
				).put(
					"scope", APIApplication.Endpoint.Scope.COMPANY.getValue()
				).toString(),
				"headless-builder/endpoints", Http.Method.POST
			).toString(),
			JSONCompareMode.STRICT);
		JSONAssert.assertEquals(
			JSONUtil.put(
				"status", "BAD_REQUEST"
			).put(
				"title",
				"Path parameter description cannot be set with empty path " +
					"parameter property."
			).toString(),
			HTTPTestUtil.invokeToJSONObject(
				JSONUtil.put(
					"httpMethod", "get"
				).put(
					"name", RandomTestUtil.randomString()
				).put(
					"path",
					StringPool.FORWARD_SLASH +
						StringUtil.toLowerCase(RandomTestUtil.randomString())
				).put(
					"pathParameterDescription", RandomTestUtil.randomString()
				).put(
					"r_apiApplicationToAPIEndpoints_l_apiApplicationId",
					apiApplicationJSONObject1.getLong("id")
				).put(
					"retrieveType",
					APIApplication.Endpoint.RetrieveType.COLLECTION.getValue()
				).put(
					"scope", APIApplication.Endpoint.Scope.COMPANY.getValue()
				).toString(),
				"headless-builder/endpoints", Http.Method.POST
			).toString(),
			JSONCompareMode.STRICT);
		JSONAssert.assertEquals(
			JSONUtil.put(
				"status", "BAD_REQUEST"
			).put(
				"title",
				"Path parameter must be an external reference code, ID, or " +
					"unique field."
			).toString(),
			HTTPTestUtil.invokeToJSONObject(
				JSONUtil.put(
					"httpMethod", "get"
				).put(
					"name", RandomTestUtil.randomString()
				).put(
					"path",
					StringBundler.concat(
						StringPool.FORWARD_SLASH,
						StringUtil.toLowerCase(RandomTestUtil.randomString()),
						StringPool.FORWARD_SLASH, StringPool.OPEN_CURLY_BRACE,
						RandomTestUtil.randomString(),
						StringPool.CLOSE_CURLY_BRACE)
				).put(
					"pathParameter", RandomTestUtil.randomString()
				).put(
					"r_apiApplicationToAPIEndpoints_l_apiApplicationId",
					apiApplicationJSONObject1.getLong("id")
				).put(
					"r_responseAPISchemaToAPIEndpoints_l_apiSchemaId",
					apiSchemaJSONObject1.getLong("id")
				).put(
					"retrieveType",
					APIApplication.Endpoint.RetrieveType.SINGLE_ELEMENT.
						getValue()
				).put(
					"scope", APIApplication.Endpoint.Scope.COMPANY.getValue()
				).toString(),
				"headless-builder/endpoints", Http.Method.POST
			).toString(),
			JSONCompareMode.STRICT);
		JSONAssert.assertEquals(
			JSONUtil.put(
				"status", "BAD_REQUEST"
			).put(
				"title",
				"Path parameters are not supported by GET API endpoints with " +
					"the \"collection\" retrieve type."
			).toString(),
			HTTPTestUtil.invokeToJSONObject(
				JSONUtil.put(
					"httpMethod", "get"
				).put(
					"name", RandomTestUtil.randomString()
				).put(
					"path",
					StringPool.FORWARD_SLASH +
						StringUtil.toUpperCase(RandomTestUtil.randomString())
				).put(
					"pathParameter", HeadlessBuilderConstants.PATH_PARAMETER_ERC
				).put(
					"r_apiApplicationToAPIEndpoints_l_apiApplicationId",
					apiApplicationJSONObject1.getLong("id")
				).put(
					"r_responseAPISchemaToAPIEndpoints_l_apiSchemaId",
					apiSchemaJSONObject1.getLong("id")
				).put(
					"retrieveType",
					APIApplication.Endpoint.RetrieveType.COLLECTION.getValue()
				).put(
					"scope", APIApplication.Endpoint.Scope.COMPANY.getValue()
				).toString(),
				"headless-builder/endpoints", Http.Method.POST
			).toString(),
			JSONCompareMode.STRICT);

		JSONObject apiSchemaJSONObject2 = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				"mainObjectDefinitionERC",
				_siteScopedObjectDefinition.getExternalReferenceCode()
			).put(
				"name", RandomTestUtil.randomString()
			).put(
				"r_apiApplicationToAPISchemas_l_apiApplicationId",
				apiApplicationJSONObject1.getLong("id")
			).toString(),
			"headless-builder/schemas", Http.Method.POST);

		JSONAssert.assertEquals(
			JSONUtil.put(
				"status", "BAD_REQUEST"
			).put(
				"title", "Single element ID endpoint cannot be scoped by site."
			).toString(),
			HTTPTestUtil.invokeToJSONObject(
				JSONUtil.put(
					"httpMethod", "get"
				).put(
					"name", RandomTestUtil.randomString()
				).put(
					"path",
					StringBundler.concat(
						StringPool.FORWARD_SLASH,
						StringUtil.toLowerCase(RandomTestUtil.randomString()),
						StringPool.FORWARD_SLASH, StringPool.OPEN_CURLY_BRACE,
						RandomTestUtil.randomString(),
						StringPool.CLOSE_CURLY_BRACE)
				).put(
					"pathParameter", HeadlessBuilderConstants.PATH_PARAMETER_ID
				).put(
					"r_apiApplicationToAPIEndpoints_l_apiApplicationId",
					apiApplicationJSONObject1.getLong("id")
				).put(
					"r_requestAPISchemaToAPIEndpoints_l_apiSchemaId",
					apiSchemaJSONObject2.getLong("id")
				).put(
					"r_responseAPISchemaToAPIEndpoints_l_apiSchemaId",
					apiSchemaJSONObject2.getLong("id")
				).put(
					"retrieveType",
					APIApplication.Endpoint.RetrieveType.SINGLE_ELEMENT.
						getValue()
				).put(
					"scope", APIApplication.Endpoint.Scope.SITE.getValue()
				).toString(),
				"headless-builder/endpoints", Http.Method.POST
			).toString(),
			JSONCompareMode.LENIENT);

		JSONAssert.assertEquals(
			JSONUtil.put(
				"r_apiApplicationToAPIEndpoints_l_apiApplicationId",
				apiApplicationJSONObject1.get("id")
			).put(
				"status", JSONUtil.put("code", 0)
			).toString(),
			HTTPTestUtil.invokeToJSONObject(
				JSONUtil.put(
					"httpMethod", "get"
				).put(
					"name", RandomTestUtil.randomString()
				).put(
					"path",
					StringPool.FORWARD_SLASH +
						StringUtil.toLowerCase(RandomTestUtil.randomString())
				).put(
					"r_apiApplicationToAPIEndpoints_l_apiApplicationId",
					apiApplicationJSONObject1.getLong("id")
				).put(
					"r_requestAPISchemaToAPIEndpoints_l_apiSchemaId",
					apiSchemaJSONObject1.getLong("id")
				).put(
					"r_responseAPISchemaToAPIEndpoints_l_apiSchemaId",
					apiSchemaJSONObject1.getLong("id")
				).put(
					"retrieveType",
					APIApplication.Endpoint.RetrieveType.COLLECTION.getValue()
				).put(
					"scope", APIApplication.Endpoint.Scope.COMPANY.getValue()
				).toString(),
				"headless-builder/endpoints", Http.Method.POST
			).toString(),
			JSONCompareMode.LENIENT);
		JSONAssert.assertEquals(
			JSONUtil.put(
				"r_apiApplicationToAPIEndpoints_l_apiApplicationId",
				apiApplicationJSONObject1.get("id")
			).put(
				"status", JSONUtil.put("code", 0)
			).toString(),
			HTTPTestUtil.invokeToJSONObject(
				JSONUtil.put(
					"httpMethod", "get"
				).put(
					"name", RandomTestUtil.randomString()
				).put(
					"path",
					StringBundler.concat(
						StringPool.FORWARD_SLASH,
						StringUtil.toLowerCase(RandomTestUtil.randomString()),
						StringPool.FORWARD_SLASH, StringPool.OPEN_CURLY_BRACE,
						RandomTestUtil.randomString(),
						StringPool.CLOSE_CURLY_BRACE)
				).put(
					"pathParameter", HeadlessBuilderConstants.PATH_PARAMETER_ID
				).put(
					"r_apiApplicationToAPIEndpoints_l_apiApplicationId",
					apiApplicationJSONObject1.getLong("id")
				).put(
					"r_requestAPISchemaToAPIEndpoints_l_apiSchemaId",
					apiSchemaJSONObject1.getLong("id")
				).put(
					"r_responseAPISchemaToAPIEndpoints_l_apiSchemaId",
					apiSchemaJSONObject1.getLong("id")
				).put(
					"retrieveType",
					APIApplication.Endpoint.RetrieveType.SINGLE_ELEMENT.
						getValue()
				).put(
					"scope", APIApplication.Endpoint.Scope.COMPANY.getValue()
				).toString(),
				"headless-builder/endpoints", Http.Method.POST
			).toString(),
			JSONCompareMode.LENIENT);

		String path =
			StringPool.FORWARD_SLASH +
				StringUtil.toLowerCase(RandomTestUtil.randomString());

		JSONObject apiEndpointJSONObject = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				"httpMethod", "get"
			).put(
				"name", RandomTestUtil.randomString()
			).put(
				"path", path
			).put(
				"r_apiApplicationToAPIEndpoints_l_apiApplicationId",
				apiApplicationJSONObject1.getLong("id")
			).put(
				"retrieveType",
				APIApplication.Endpoint.RetrieveType.COLLECTION.getValue()
			).put(
				"scope", APIApplication.Endpoint.Scope.COMPANY.getValue()
			).toString(),
			"headless-builder/endpoints", Http.Method.POST);

		JSONObject apiApplicationJSONObject2 = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				"applicationStatus", "published"
			).put(
				"baseURL", StringUtil.toLowerCase(RandomTestUtil.randomString())
			).put(
				"title", RandomTestUtil.randomString()
			).toString(),
			"headless-builder/applications", Http.Method.POST);

		JSONObject apiSchemaJSONObject3 = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				"mainObjectDefinitionERC",
				_objectDefinition.getExternalReferenceCode()
			).put(
				"name", RandomTestUtil.randomString()
			).put(
				"r_apiApplicationToAPISchemas_l_apiApplicationId",
				apiApplicationJSONObject2.getLong("id")
			).toString(),
			"headless-builder/schemas", Http.Method.POST);

		JSONAssert.assertEquals(
			JSONUtil.put(
				"status", "BAD_REQUEST"
			).put(
				"title",
				"There is an API endpoint with the same HTTP method and path."
			).toString(),
			HTTPTestUtil.invokeToJSONObject(
				JSONUtil.put(
					"httpMethod", "get"
				).put(
					"name", RandomTestUtil.randomString()
				).put(
					"path", path
				).put(
					"r_apiApplicationToAPIEndpoints_l_apiApplicationId",
					apiApplicationJSONObject1.getLong("id")
				).put(
					"retrieveType",
					APIApplication.Endpoint.RetrieveType.COLLECTION.getValue()
				).put(
					"scope", APIApplication.Endpoint.Scope.COMPANY.getValue()
				).toString(),
				"headless-builder/endpoints", Http.Method.POST
			).toString(),
			JSONCompareMode.STRICT);

		JSONAssert.assertEquals(
			JSONUtil.put(
				"status", "BAD_REQUEST"
			).put(
				"title",
				"The API endpoint and the API schema must be related to the " +
					"same API application."
			).toString(),
			HTTPTestUtil.invokeToJSONObject(
				null,
				StringBundler.concat(
					"headless-builder/schemas/",
					apiSchemaJSONObject3.getLong("id"),
					"/responseAPISchemaToAPIEndpoints/",
					apiEndpointJSONObject.getLong("id")),
				Http.Method.PUT
			).toString(),
			JSONCompareMode.STRICT);
		JSONAssert.assertEquals(
			JSONUtil.put(
				"status", "BAD_REQUEST"
			).put(
				"title",
				"The API endpoint and the API schema must be related to the " +
					"same API application."
			).toString(),
			HTTPTestUtil.invokeToJSONObject(
				JSONUtil.put(
					"httpMethod", "get"
				).put(
					"name", RandomTestUtil.randomString()
				).put(
					"path",
					StringPool.FORWARD_SLASH +
						StringUtil.toLowerCase(RandomTestUtil.randomString())
				).put(
					"r_apiApplicationToAPIEndpoints_l_apiApplicationId",
					apiApplicationJSONObject1.getLong("id")
				).put(
					"r_requestAPISchemaToAPIEndpoints_l_apiSchemaId",
					apiSchemaJSONObject3.getLong("id")
				).put(
					"retrieveType",
					APIApplication.Endpoint.RetrieveType.COLLECTION.getValue()
				).put(
					"scope", APIApplication.Endpoint.Scope.COMPANY.getValue()
				).toString(),
				"headless-builder/endpoints", Http.Method.POST
			).toString(),
			JSONCompareMode.STRICT);

		JSONAssert.assertEquals(
			JSONUtil.put(
				"status", "BAD_REQUEST"
			).put(
				"title",
				"The API endpoint and the API schema must have the same scope."
			).toString(),
			HTTPTestUtil.invokeToJSONObject(
				JSONUtil.put(
					"httpMethod", "get"
				).put(
					"name", RandomTestUtil.randomString()
				).put(
					"path", path
				).put(
					"r_apiApplicationToAPIEndpoints_l_apiApplicationId",
					apiApplicationJSONObject1.getLong("id")
				).put(
					"r_responseAPISchemaToAPIEndpoints_l_apiSchemaId",
					apiSchemaJSONObject2.getLong("id")
				).put(
					"retrieveType",
					APIApplication.Endpoint.RetrieveType.COLLECTION.getValue()
				).put(
					"scope", APIApplication.Endpoint.Scope.COMPANY.getValue()
				).toString(),
				"headless-builder/endpoints", Http.Method.POST
			).toString(),
			JSONCompareMode.STRICT);

		JSONAssert.assertEquals(
			JSONUtil.put(
				"r_apiApplicationToAPIEndpoints_l_apiApplicationId",
				apiApplicationJSONObject1.get("id")
			).toString(),
			apiEndpointJSONObject.toString(), JSONCompareMode.LENIENT);

		JSONAssert.assertEquals(
			JSONUtil.put(
				"status", "BAD_REQUEST"
			).put(
				"title",
				"The value is invalid for object field " +
					"\"r_apiApplicationToAPIEndpoints_l_apiApplicationId\""
			).toString(),
			HTTPTestUtil.invokeToJSONObject(
				JSONUtil.put(
					"httpMethod", "get"
				).put(
					"name", RandomTestUtil.randomString()
				).put(
					"path",
					StringPool.FORWARD_SLASH +
						StringUtil.toLowerCase(RandomTestUtil.randomString())
				).put(
					"r_apiApplicationToAPIEndpoints_l_apiApplicationId",
					_objectEntry.getObjectEntryId()
				).put(
					"retrieveType",
					APIApplication.Endpoint.RetrieveType.COLLECTION.getValue()
				).put(
					"scope", APIApplication.Endpoint.Scope.COMPANY.getValue()
				).toString(),
				"headless-builder/endpoints", Http.Method.POST
			).toString(),
			JSONCompareMode.STRICT);

		JSONAssert.assertEquals(
			JSONUtil.put(
				"status", "BAD_REQUEST"
			).put(
				"title",
				"The value is invalid for object field " +
					"\"r_requestAPISchemaToAPIEndpoints_l_apiSchemaId\""
			).toString(),
			HTTPTestUtil.invokeToJSONObject(
				JSONUtil.put(
					"httpMethod", "get"
				).put(
					"name", RandomTestUtil.randomString()
				).put(
					"path",
					StringPool.FORWARD_SLASH +
						StringUtil.toLowerCase(RandomTestUtil.randomString())
				).put(
					"r_apiApplicationToAPIEndpoints_l_apiApplicationId",
					apiApplicationJSONObject1.getLong("id")
				).put(
					"r_requestAPISchemaToAPIEndpoints_l_apiSchemaId",
					_objectEntry.getObjectEntryId()
				).put(
					"r_responseAPISchemaToAPIEndpoints_l_apiSchemaId",
					_objectEntry.getObjectEntryId()
				).put(
					"retrieveType",
					APIApplication.Endpoint.RetrieveType.COLLECTION.getValue()
				).put(
					"scope", APIApplication.Endpoint.Scope.COMPANY.getValue()
				).toString(),
				"headless-builder/endpoints", Http.Method.POST
			).toString(),
			JSONCompareMode.STRICT);
	}

	@Test
	public void testCreatePostAPIEndpoint() throws Exception {
		JSONObject apiApplicationJSONObject = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				"applicationStatus", "published"
			).put(
				"baseURL", StringUtil.toLowerCase(RandomTestUtil.randomString())
			).put(
				"title", RandomTestUtil.randomString()
			).toString(),
			"headless-builder/applications", Http.Method.POST);

		JSONObject apiSchemaJSONObject = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				"mainObjectDefinitionERC",
				_objectDefinition.getExternalReferenceCode()
			).put(
				"name", RandomTestUtil.randomString()
			).put(
				"r_apiApplicationToAPISchemas_l_apiApplicationId",
				apiApplicationJSONObject.getLong("id")
			).toString(),
			"headless-builder/schemas", Http.Method.POST);

		JSONAssert.assertEquals(
			JSONUtil.put(
				"status", "BAD_REQUEST"
			).put(
				"title",
				"Path can have a maximum of 255 alphanumeric characters."
			).toString(),
			HTTPTestUtil.invokeToJSONObject(
				JSONUtil.put(
					"httpMethod", "post"
				).put(
					"name", RandomTestUtil.randomString()
				).put(
					"path",
					StringBundler.concat(
						StringPool.FORWARD_SLASH,
						StringUtil.toLowerCase(RandomTestUtil.randomString()),
						StringPool.FORWARD_SLASH,
						StringUtil.toLowerCase(RandomTestUtil.randomString()),
						StringPool.COMMA)
				).put(
					"r_apiApplicationToAPIEndpoints_l_apiApplicationId",
					apiApplicationJSONObject.getLong("id")
				).put(
					"retrieveType",
					APIApplication.Endpoint.RetrieveType.SINGLE_ELEMENT.
						getValue()
				).put(
					"scope", APIApplication.Endpoint.Scope.COMPANY.getValue()
				).toString(),
				"headless-builder/endpoints", Http.Method.POST
			).toString(),
			JSONCompareMode.STRICT);
		JSONAssert.assertEquals(
			JSONUtil.put(
				"status", "BAD_REQUEST"
			).put(
				"title",
				"Path can have a maximum of 255 alphanumeric characters."
			).toString(),
			HTTPTestUtil.invokeToJSONObject(
				JSONUtil.put(
					"httpMethod", "post"
				).put(
					"name", RandomTestUtil.randomString()
				).put(
					"path",
					StringBundler.concat(
						StringPool.FORWARD_SLASH,
						StringUtil.toLowerCase(RandomTestUtil.randomString()),
						StringPool.FORWARD_SLASH, StringPool.OPEN_CURLY_BRACE,
						StringUtil.toLowerCase(RandomTestUtil.randomString()),
						StringPool.CLOSE_CURLY_BRACE)
				).put(
					"r_apiApplicationToAPIEndpoints_l_apiApplicationId",
					apiApplicationJSONObject.getLong("id")
				).put(
					"r_requestAPISchemaToAPIEndpoints_l_apiSchemaId",
					apiSchemaJSONObject.getLong("id")
				).put(
					"r_responseAPISchemaToAPIEndpoints_l_apiSchemaId",
					apiSchemaJSONObject.getLong("id")
				).put(
					"retrieveType",
					APIApplication.Endpoint.RetrieveType.SINGLE_ELEMENT.
						getValue()
				).put(
					"scope", APIApplication.Endpoint.Scope.COMPANY.getValue()
				).toString(),
				"headless-builder/endpoints", Http.Method.POST
			).toString(),
			JSONCompareMode.LENIENT);
		JSONAssert.assertEquals(
			JSONUtil.put(
				"status", "BAD_REQUEST"
			).put(
				"title", "Path must start with the \"/\" character."
			).toString(),
			HTTPTestUtil.invokeToJSONObject(
				JSONUtil.put(
					"httpMethod", "post"
				).put(
					"name", RandomTestUtil.randomString()
				).put(
					"path",
					StringUtil.toLowerCase(RandomTestUtil.randomString())
				).put(
					"r_apiApplicationToAPIEndpoints_l_apiApplicationId",
					apiApplicationJSONObject.getLong("id")
				).put(
					"retrieveType",
					APIApplication.Endpoint.RetrieveType.SINGLE_ELEMENT.
						getValue()
				).put(
					"scope", APIApplication.Endpoint.Scope.COMPANY.getValue()
				).toString(),
				"headless-builder/endpoints", Http.Method.POST
			).toString(),
			JSONCompareMode.STRICT);
		JSONAssert.assertEquals(
			JSONUtil.put(
				"status", "BAD_REQUEST"
			).put(
				"title",
				"Path parameters are not supported by POST API endpoints."
			).toString(),
			HTTPTestUtil.invokeToJSONObject(
				JSONUtil.put(
					"httpMethod", "post"
				).put(
					"name", RandomTestUtil.randomString()
				).put(
					"path",
					StringPool.FORWARD_SLASH +
						StringUtil.toLowerCase(RandomTestUtil.randomString())
				).put(
					"pathParameter", HeadlessBuilderConstants.PATH_PARAMETER_ID
				).put(
					"r_apiApplicationToAPIEndpoints_l_apiApplicationId",
					apiApplicationJSONObject.getLong("id")
				).put(
					"r_requestAPISchemaToAPIEndpoints_l_apiSchemaId",
					apiSchemaJSONObject.getLong("id")
				).put(
					"r_responseAPISchemaToAPIEndpoints_l_apiSchemaId",
					apiSchemaJSONObject.getLong("id")
				).put(
					"retrieveType",
					APIApplication.Endpoint.RetrieveType.SINGLE_ELEMENT.
						getValue()
				).put(
					"scope", APIApplication.Endpoint.Scope.COMPANY.getValue()
				).toString(),
				"headless-builder/endpoints", Http.Method.POST
			).toString(),
			JSONCompareMode.LENIENT);
		JSONAssert.assertEquals(
			JSONUtil.put(
				"status", "BAD_REQUEST"
			).put(
				"title",
				"POST API endpoints retrieve type must be \"singleElement.\""
			).toString(),
			HTTPTestUtil.invokeToJSONObject(
				JSONUtil.put(
					"httpMethod", "post"
				).put(
					"name", RandomTestUtil.randomString()
				).put(
					"path",
					StringPool.FORWARD_SLASH +
						StringUtil.toLowerCase(RandomTestUtil.randomString())
				).put(
					"r_apiApplicationToAPIEndpoints_l_apiApplicationId",
					apiApplicationJSONObject.getLong("id")
				).put(
					"r_requestAPISchemaToAPIEndpoints_l_apiSchemaId",
					apiSchemaJSONObject.getLong("id")
				).put(
					"r_responseAPISchemaToAPIEndpoints_l_apiSchemaId",
					apiSchemaJSONObject.getLong("id")
				).put(
					"retrieveType",
					APIApplication.Endpoint.RetrieveType.COLLECTION.getValue()
				).put(
					"scope", APIApplication.Endpoint.Scope.COMPANY.getValue()
				).toString(),
				"headless-builder/endpoints", Http.Method.POST
			).toString(),
			JSONCompareMode.LENIENT);
		JSONAssert.assertEquals(
			JSONUtil.put(
				"r_apiApplicationToAPIEndpoints_l_apiApplicationId",
				apiApplicationJSONObject.get("id")
			).put(
				"status", JSONUtil.put("code", 0)
			).toString(),
			HTTPTestUtil.invokeToJSONObject(
				JSONUtil.put(
					"httpMethod", "post"
				).put(
					"name", RandomTestUtil.randomString()
				).put(
					"path",
					StringPool.FORWARD_SLASH +
						StringUtil.toLowerCase(RandomTestUtil.randomString())
				).put(
					"r_apiApplicationToAPIEndpoints_l_apiApplicationId",
					apiApplicationJSONObject.getLong("id")
				).put(
					"r_requestAPISchemaToAPIEndpoints_l_apiSchemaId",
					apiSchemaJSONObject.getLong("id")
				).put(
					"r_responseAPISchemaToAPIEndpoints_l_apiSchemaId",
					apiSchemaJSONObject.getLong("id")
				).put(
					"retrieveType",
					APIApplication.Endpoint.RetrieveType.SINGLE_ELEMENT.
						getValue()
				).put(
					"scope", APIApplication.Endpoint.Scope.COMPANY.getValue()
				).toString(),
				"headless-builder/endpoints", Http.Method.POST
			).toString(),
			JSONCompareMode.LENIENT);
	}

	@DeleteAfterTestRun
	private ObjectDefinition _objectDefinition;

	private ObjectEntry _objectEntry;

	@DeleteAfterTestRun
	private ObjectDefinition _siteScopedObjectDefinition;

}