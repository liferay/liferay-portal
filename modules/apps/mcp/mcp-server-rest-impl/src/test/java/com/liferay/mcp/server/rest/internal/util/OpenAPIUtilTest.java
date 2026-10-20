/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mcp.server.rest.internal.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.liferay.mcp.server.rest.dto.v1_0.Tool;
import com.liferay.mcp.server.rest.dto.v1_0.ToolSummary;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.odata.filter.InvalidFilterException;
import com.liferay.portal.odata.sort.InvalidSortException;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.vulcan.http.VulcanRequestForwarder;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import java.nio.charset.StandardCharsets;

import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUpload;
import org.apache.commons.fileupload.UploadContext;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.skyscreamer.jsonassert.JSONAssert;

/**
 * @author Alejandro Tardín
 */
public class OpenAPIUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		_openAPIJSONObject = JSONFactoryUtil.createJSONObject(
			StringUtil.read(
				OpenAPIUtilTest.class.getResourceAsStream(
					"dependencies/openapi.json")));
	}

	@Test
	public void testGetLastPathParameter() {
		Assert.assertNull(OpenAPIUtil.getLastPathParameter(StringPool.BLANK));
		Assert.assertNull(OpenAPIUtil.getLastPathParameter("/sites"));
		Assert.assertEquals(
			"siteId",
			OpenAPIUtil.getLastPathParameter("/sites/{siteId}/blog-postings"));
		Assert.assertEquals(
			"blogPostingId",
			OpenAPIUtil.getLastPathParameter(
				"/sites/{siteId}/blog-postings/{blogPostingId}"));
	}

	@Test
	public void testGetPathParameter() {
		Assert.assertNull(OpenAPIUtil.getPathParameter(null));
		Assert.assertNull(OpenAPIUtil.getPathParameter("sites"));
		Assert.assertNull(OpenAPIUtil.getPathParameter("{}"));
		Assert.assertNull(OpenAPIUtil.getPathParameter("{siteId"));
		Assert.assertEquals("siteId", OpenAPIUtil.getPathParameter("{siteId}"));
	}

	@Test
	public void testGetPropertiesJSONObjects() {
		JSONObject openAPIJSONObject = JSONUtil.put(
			"components",
			JSONUtil.put(
				"schemas",
				JSONUtil.put(
					"Child",
					JSONUtil.put(
						"allOf",
						JSONUtil.putAll(
							JSONUtil.put("$ref", "#/components/schemas/Parent"),
							JSONUtil.put(
								"properties",
								JSONUtil.put(
									"childName",
									JSONUtil.put("type", "string"))))
					).put(
						"type", "object"
					)
				).put(
					"Parent",
					JSONUtil.put(
						"properties",
						JSONUtil.put(
							"parentName", JSONUtil.put("type", "string")
						).put(
							"parentPassword",
							JSONUtil.put(
								"type", "string"
							).put(
								"writeOnly", true
							)
						)
					).put(
						"type", "object"
					)
				)));

		Assert.assertTrue(
			MapUtil.isEmpty(
				OpenAPIUtil.getPropertiesJSONObjects(openAPIJSONObject, null)));

		Map<String, JSONObject> propertiesJSONObjects =
			OpenAPIUtil.getPropertiesJSONObjects(
				openAPIJSONObject,
				JSONUtil.put("$ref", "#/components/schemas/Child"));

		Assert.assertEquals(
			SetUtil.fromArray("childName", "parentName", "parentPassword"),
			propertiesJSONObjects.keySet());

		propertiesJSONObjects = OpenAPIUtil.getPropertiesJSONObjects(
			openAPIJSONObject,
			JSONUtil.put(
				"properties",
				JSONUtil.put(
					"items",
					JSONUtil.put(
						"items",
						JSONUtil.put("$ref", "#/components/schemas/Parent")
					).put(
						"type", "array"
					)
				).put(
					"pageSize", JSONUtil.put("type", "integer")
				)
			).put(
				"type", "object"
			));

		Assert.assertEquals(
			SetUtil.fromArray("parentName", "parentPassword"),
			propertiesJSONObjects.keySet());
	}

	@Test
	public void testGetRequest() throws Exception {
		_testGetRequest(
			null, null, "GET",
			"/v1.0/items/123?fields=name&restrictFields=actions",
			JSONUtil.put(
				"fields", "name"
			).put(
				"itemId", "123"
			),
			"getItem");
		_testGetRequest(
			null, null, "GET", "/v1.0/items/123?restrictFields=actions",
			JSONUtil.put("itemId", "123"), "getItem");
		_testGetRequest(
			null, null, "GET",
			"/v1.0/items?fields=name%2Cinteger&restrictFields=actions",
			JSONUtil.put("fields", JSONUtil.putAll("name", "integer")),
			"getItems");
		_testGetRequest(
			null, null, "GET",
			"/v1.0/items?filter=name+eq+%27John+Doe%27&restrictFields=actions",
			JSONUtil.put("filter", "name eq 'John Doe'"), "getItems");
		_testGetRequest(
			null, null, "GET",
			"/v1.0/items?filter=object1%2Fboolean+eq+true&restrictFields=" +
				"actions%2Cobject1.string",
			JSONUtil.put("filter", "object1/boolean eq true"), "object1.string",
			"getItems");
		_testGetRequest(
			null, null, "GET",
			"/v1.0/items?filter=string+eq+%27boolean%27%27s%27&" +
				"restrictFields=actions%2Cboolean",
			JSONUtil.put("filter", "string eq 'boolean''s'"), "boolean",
			"getItems");
		_testGetRequest(
			null, null, "GET",
			"/v1.0/items?filter=string+eq+%27phoneNumber%27&restrictFields=" +
				"actions%2CphoneNumber",
			JSONUtil.put("filter", "string eq 'phoneNumber'"), "phoneNumber",
			"getItems");
		_testGetRequest(
			null, null, "GET",
			"/v1.0/items?page=1&pageSize=20&fields=name&restrictFields=actions",
			JSONUtil.put(
				"fields", "name"
			).put(
				"page", "1"
			).put(
				"pageSize", "20"
			),
			"getItems");
		_testGetRequest(
			null, null, "GET", "/v1.0/items?restrictFields=actions",
			JSONFactoryUtil.createJSONObject(), "getItems");
		_testGetRequest(
			null, null, "GET", "/v1.0/items?restrictFields=actions",
			JSONFactoryUtil.createJSONObject(), StringPool.BLANK, "getItems");
		_testGetRequest(
			null, null, "GET", "/v1.0/items?restrictFields=actions",
			JSONUtil.put("fields", ""), "getItems");
		_testGetRequest(
			null, null, "GET", "/v1.0/items?restrictFields=actions",
			JSONUtil.put("restrictFields", "name"), "getItems");
		_testGetRequest(
			null, null, "GET",
			"/v1.0/items?restrictFields=actions%2Cname%2Cparent.name",
			JSONFactoryUtil.createJSONObject(), "name,parent.name", "getItems");
		_testGetRequest(
			null, null, "GET",
			"/v1.0/items?sort=boolean%3Aasc&restrictFields=actions%2Cstring",
			JSONUtil.put("sort", "boolean:asc"), "string", "getItems");
		_testGetRequest(
			null, null, "GET",
			"/v1.0/items?sort=string%3Aasc&restrictFields=actions",
			JSONUtil.put("sort", "string:asc"), "getItems");
		_testGetRequest(
			null, null, "GET",
			"/v1.0/localized?restrictFields=actions%2Cchild.name%2C" +
				"child.name_i18n",
			JSONFactoryUtil.createJSONObject(), "child.name", "getLocalized");
		_testGetRequest(
			null, null, "GET",
			"/v1.0/localized?restrictFields=actions%2Cname%2Cname_i18n",
			JSONFactoryUtil.createJSONObject(), "name", "getLocalized");
		_testGetRequest(
			null, null, "GET",
			"/v1.0/localized?restrictFields=actions%2Cname_i18n",
			JSONFactoryUtil.createJSONObject(), "name_i18n", "getLocalized");
		_testGetRequest(
			null, null, "GET",
			"/v1.0/localized?restrictFields=actions%2Ctags.name%2C" +
				"tags.name_i18n",
			JSONFactoryUtil.createJSONObject(), "tags.name", "getLocalized");
		_testGetRequest(
			null, null, "GET", "/v1.0/localized?restrictFields=actions%2Ctitle",
			JSONFactoryUtil.createJSONObject(), "title", "getLocalized");
		_testGetRequest(
			null, null, "GET",
			"/v1.0/localized-page?restrictFields=actions%2Cname%2Cname_i18n",
			JSONFactoryUtil.createJSONObject(), "name", "getLocalizedPage");
		_testGetRequest(
			JSONUtil.put(
				"name", "Test"
			).toString(),
			"application/json", "PATCH", "/v1.0/items/123",
			JSONUtil.put(
				"body", JSONUtil.put("name", "Test")
			).put(
				"itemId", "123"
			),
			"patchItem");
		_testGetRequest(
			JSONUtil.put(
				"name", "Test"
			).toString(),
			"application/json", "PATCH", "/v1.0/items/123?restrictFields=name",
			JSONUtil.put(
				"body", JSONUtil.put("name", "Test")
			).put(
				"itemId", "123"
			),
			"name", "patchItem");
		_testGetRequest(
			"{}", "application/json", "POST", "/v1.0/items",
			JSONUtil.put("body", JSONFactoryUtil.createJSONObject()),
			"postItem");
		_testGetRequest(
			"{}", "application/json", "POST", "/v1.0/items?restrictFields=name",
			JSONUtil.put("body", JSONFactoryUtil.createJSONObject()), "name",
			"postItem");
		_testGetRequest(
			JSONUtil.put(
				"name", "Test"
			).toString(),
			"application/json", "PUT", "/v1.0/items/123?restrictFields=name",
			JSONUtil.put(
				"body", JSONUtil.put("name", "Test")
			).put(
				"itemId", "123"
			),
			"name", "putItem");

		_testGetRequestFailure(
			InvalidFilterException.class,
			"Parameter \"filter\" references a restricted field",
			JSONUtil.put("filter", "object1.string eq 'Test'"),
			"object1.string", "getItems");
		_testGetRequestFailure(
			InvalidFilterException.class,
			"Parameter \"filter\" references a restricted field",
			JSONUtil.put("filter", "object1/object2/boolean eq true"),
			"object1", "getItems");
		_testGetRequestFailure(
			InvalidFilterException.class,
			"Parameter \"filter\" references a restricted field",
			JSONUtil.put("filter", "object1/string eq 'Test'"),
			"object1.string", "getItems");
		_testGetRequestFailure(
			InvalidFilterException.class,
			"Parameter \"filter\" references a restricted field",
			JSONUtil.put("filter", "string eq 'Test'"), "string", "getItems");
		_testGetRequestFailure(
			InvalidFilterException.class,
			"Parameter \"filter\" references a restricted field",
			JSONUtil.put("filter", "string eq 'Test' and boolean eq true"),
			"string,boolean", "getItems");
		_testGetRequestFailure(
			InvalidSortException.class,
			"Parameter \"sort\" references a restricted field",
			JSONUtil.put("sort", "boolean:asc, string :desc"), "string",
			"getItems");
		_testGetRequestFailure(
			InvalidSortException.class,
			"Parameter \"sort\" references a restricted field",
			JSONUtil.put("sort", "boolean:asc,string:desc"), "string",
			"getItems");
		_testGetRequestFailure(
			InvalidSortException.class,
			"Parameter \"sort\" references a restricted field",
			JSONUtil.put("sort", "object1/string"), "object1", "getItems");

		String fileContent = RandomTestUtil.randomString();
		String fileName = RandomTestUtil.randomString();
		String name = RandomTestUtil.randomString();

		VulcanRequestForwarder.Request request = OpenAPIUtil.getRequest(
			StringPool.BLANK, null,
			JSONUtil.put(
				"data",
				JSONUtil.put(
					"contentType", "text/plain"
				).put(
					"data",
					() -> {
						Base64.Encoder encoder = Base64.getEncoder();

						return encoder.encodeToString(fileContent.getBytes());
					}
				).put(
					"filename", fileName
				)
			).put(
				"name", name
			),
			_openAPIJSONObject, null, "postBinary", null);

		Assert.assertEquals("POST", request.getMethod());
		Assert.assertEquals("/v1.0/binaries", request.getPath());
		_assertMultipartContentType(request);

		List<FileItem> fileItems = _getFileItems(request);

		Assert.assertEquals(fileItems.toString(), 2, fileItems.size());

		FileItem fileItem = _getFileItem(fileItems, "data");

		Assert.assertFalse(fileItem.isFormField());
		Assert.assertEquals("text/plain", fileItem.getContentType());
		Assert.assertEquals(fileName, fileItem.getName());
		Assert.assertArrayEquals(fileContent.getBytes(), fileItem.get());

		fileItem = _getFileItem(fileItems, "name");

		Assert.assertTrue(fileItem.isFormField());
		Assert.assertEquals(name, fileItem.getString());

		request = OpenAPIUtil.getRequest(
			StringPool.BLANK, null,
			JSONUtil.put(
				"boolean", true
			).put(
				"integer", 1
			).put(
				"string", fileContent
			),
			_openAPIJSONObject, null, "postUpload", null);

		Assert.assertEquals("POST", request.getMethod());
		Assert.assertEquals("/v1.0/uploads", request.getPath());
		_assertMultipartContentType(request);

		fileItems = _getFileItems(request);

		Assert.assertEquals(fileItems.toString(), 3, fileItems.size());

		Assert.assertEquals("true", _getFileItemValue(fileItems, "boolean"));
		Assert.assertEquals("1", _getFileItemValue(fileItems, "integer"));
		Assert.assertEquals(
			fileContent, _getFileItemValue(fileItems, "string"));

		Map<String, String> headers = HashMapBuilder.put(
			RandomTestUtil.randomString(), RandomTestUtil.randomString()
		).build();

		request = OpenAPIUtil.getRequest(
			StringPool.BLANK, headers, JSONUtil.put("itemId", "123"),
			_openAPIJSONObject, null, "getItem", null);

		Assert.assertEquals(headers, request.getHeaders());

		AssertUtils.assertFailure(
			IllegalArgumentException.class,
			StringBundler.concat(
				"The \"postItem\" tool requires the request payload nested ",
				"under a \"body\" property. Pass any path or query parameters ",
				"as siblings of \"body\" rather than flattening the payload ",
				"into the input map."),
			() -> OpenAPIUtil.getRequest(
				StringPool.BLANK, null,
				JSONUtil.put(
					RandomTestUtil.randomString(),
					RandomTestUtil.randomString()),
				_openAPIJSONObject, null, "postItem", null));
	}

	@Test
	public void testGetRequestBodySchemaJSONObject() {
		JSONObject openAPIJSONObject = JSONUtil.put(
			"components",
			JSONUtil.put(
				"schemas",
				JSONUtil.put("Body", JSONUtil.put("type", "object"))));

		Assert.assertNull(
			OpenAPIUtil.getRequestBodySchemaJSONObject(
				openAPIJSONObject,
				JSONUtil.put("responses", JSONUtil.put("200", "ok"))));

		JSONObject schemaJSONObject =
			OpenAPIUtil.getRequestBodySchemaJSONObject(
				openAPIJSONObject,
				JSONUtil.put(
					"requestBody",
					JSONUtil.put(
						"content",
						JSONUtil.put(
							"application/json",
							JSONUtil.put(
								"schema",
								JSONUtil.put(
									"$ref", "#/components/schemas/Body"))))));

		Assert.assertEquals("object", schemaJSONObject.getString("type"));

		schemaJSONObject = OpenAPIUtil.getRequestBodySchemaJSONObject(
			openAPIJSONObject,
			JSONUtil.put(
				"requestBody",
				JSONUtil.put(
					"content",
					JSONUtil.put(
						"application/json",
						JSONUtil.put("schema", JSONUtil.put("type", "object"))
					).put(
						"multipart/form-data",
						JSONUtil.put("schema", JSONUtil.put("type", "string"))
					))));

		Assert.assertEquals("object", schemaJSONObject.getString("type"));

		schemaJSONObject = OpenAPIUtil.getRequestBodySchemaJSONObject(
			openAPIJSONObject,
			JSONUtil.put(
				"requestBody",
				JSONUtil.put(
					"content",
					JSONUtil.put(
						"multipart/form-data",
						JSONUtil.put(
							"schema", JSONUtil.put("type", "string"))))));

		Assert.assertEquals("string", schemaJSONObject.getString("type"));
	}

	@Test
	public void testGetRequiredPropertyNames() {
		JSONObject openAPIJSONObject = JSONUtil.put(
			"components",
			JSONUtil.put(
				"schemas",
				JSONUtil.put(
					"Parent",
					JSONUtil.put(
						"required", JSONUtil.putAll("parentName")
					).put(
						"type", "object"
					))));

		Assert.assertEquals(
			SetUtil.fromArray("childName", "parentName"),
			OpenAPIUtil.getRequiredPropertyNames(
				openAPIJSONObject,
				JSONUtil.put(
					"allOf",
					JSONUtil.putAll(
						JSONUtil.put("$ref", "#/components/schemas/Parent"),
						JSONUtil.put("required", JSONUtil.putAll("childName")))
				).put(
					"type", "object"
				)));
		Assert.assertEquals(
			SetUtil.fromArray("parentName"),
			OpenAPIUtil.getRequiredPropertyNames(
				openAPIJSONObject,
				JSONUtil.put("$ref", "#/components/schemas/Parent")));
	}

	@Test
	public void testGetResponseSchemaJSONObject() {
		Assert.assertNull(
			OpenAPIUtil.getResponseSchemaJSONObject(
				JSONUtil.put("description", "no responses")));
		Assert.assertNull(
			OpenAPIUtil.getResponseSchemaJSONObject(
				JSONUtil.put("responses", JSONUtil.put("200", "ok"))));
		Assert.assertNull(
			OpenAPIUtil.getResponseSchemaJSONObject(
				JSONUtil.put(
					"responses",
					JSONUtil.put("404", _getResponseJSONObject("not found")))));
		Assert.assertEquals(
			"accepted",
			_getResponseSchemaType(
				JSONUtil.put(
					"202", _getResponseJSONObject("accepted")
				).put(
					"500", _getResponseJSONObject("error")
				)));
		Assert.assertEquals(
			"default",
			_getResponseSchemaType(
				JSONUtil.put(
					"404", _getResponseJSONObject("not found")
				).put(
					"default", _getResponseJSONObject("default")
				)));
	}

	@Test
	public void testGetTool() throws Exception {
		AssertUtils.assertFailure(
			IllegalArgumentException.class,
			"OpenAPI document has no tool with name \"missing\"",
			() -> OpenAPIUtil.getTool(
				true, _openAPIJSONObject, false, null, "missing"));
		AssertUtils.assertFailure(
			IllegalArgumentException.class,
			"OpenAPI document has no \"paths\" object",
			() -> OpenAPIUtil.getTool(
				true, JSONFactoryUtil.createJSONObject(), false, null,
				RandomTestUtil.randomString()));
		AssertUtils.assertFailure(
			IllegalArgumentException.class, "Request body has no content",
			() -> _getInputSchema(_openAPIJSONObject, "postEmptyContent"));
		AssertUtils.assertFailure(
			IllegalArgumentException.class,
			"Request body content has no \"schema\"",
			() -> _getInputSchema(_openAPIJSONObject, "postNoSchema"));

		_testGetTool(
			"This is the description", "get_test_v1.0_items_itemId.json",
			"getItem");
		_testGetTool(
			"This is the summary. This is the description",
			"get_test_v1.0_items.json", "getItems");
		_testGetTool(
			"This is the summary. This is the description",
			"get_test_v1.0_items_no_inject.json", false, "getItems");
		_testGetTool("This is the summary", "get_c_test.json", "getItemsPage");
		_testGetTool(
			"PATCH /v1.0/items/{itemId}", "patch_test_v1.0_items_itemId.json",
			"patchItem");
		_testGetTool(
			"POST /v1.0/binaries", "post_test_v1.0_binaries.json",
			"postBinary");
		_testGetTool(
			"POST /v1.0/described", "post_test_v1.0_described.json",
			"postDescribed");
		_testGetTool(
			"POST /v1.0/items", "post_test_v1.0_items.json", "postItem");
		_testGetTool(
			"POST /v1.0/levels", "post_test_v1.0_levels.json", "postLevel");
		_testGetTool(
			"POST /v1.0/no-content", "post_test_v1.0_no-content.json",
			"postNoContent");
		_testGetTool(
			"POST /v1.0/parents", "post_test_v1.0_parents.json", "postParent");
		_testGetTool(
			"POST /v1.0/undescribed", "post_test_v1.0_undescribed.json",
			"postUndescribed");
		_testGetTool(
			"POST /v1.0/uploads", "post_test_v1.0_uploads.json", "postUpload");
		_testGetTool(
			"PUT /v1.0/items/{itemId}", "put_test_v1.0_items_itemId.json",
			"putItem");

		Tool tool = OpenAPIUtil.getTool(
			true, _openAPIJSONObject, false, "boolean,object1.name",
			"getItems");

		Map<String, ?> inputSchemaMap = tool.getInputSchema();

		Map<String, ?> propertiesMap = (Map<String, ?>)inputSchemaMap.get(
			"properties");

		Map<String, ?> fieldsMap = (Map<String, ?>)propertiesMap.get("fields");

		Map<String, ?> itemsMap = (Map<String, ?>)fieldsMap.get("items");

		List<String> enumValues = (List<String>)itemsMap.get("enum");

		Assert.assertFalse(enumValues.contains("boolean"));
		Assert.assertTrue(enumValues.contains("object1"));

		tool = OpenAPIUtil.getTool(
			true, _openAPIJSONObject, false, "name,title", "getLocalized");

		inputSchemaMap = tool.getInputSchema();

		propertiesMap = (Map<String, ?>)inputSchemaMap.get("properties");

		fieldsMap = (Map<String, ?>)propertiesMap.get("fields");

		itemsMap = (Map<String, ?>)fieldsMap.get("items");

		enumValues = (List<String>)itemsMap.get("enum");

		Assert.assertTrue(enumValues.contains("child"));
		Assert.assertFalse(enumValues.contains("name"));
		Assert.assertFalse(enumValues.contains("name_i18n"));
		Assert.assertTrue(enumValues.contains("tags"));
		Assert.assertFalse(enumValues.contains("title"));

		JSONObject itemJSONObject = JSONFactoryUtil.createJSONObject(
			_read("get_test_v1.0_items_itemId_output.json"));

		_testGetToolOutputSchema(itemJSONObject, "getItem");
		_testGetToolOutputSchema(
			JSONUtil.put(
				"items", itemJSONObject
			).put(
				"type", "array"
			),
			"getItems");
		_testGetToolOutputSchema(
			JSONUtil.put(
				"properties",
				JSONUtil.put(
					"items",
					JSONUtil.put(
						"items", itemJSONObject
					).put(
						"type", "array"
					)
				).put(
					"page", JSONUtil.put("type", "integer")
				).put(
					"totalCount", JSONUtil.put("type", "integer")
				)
			).put(
				"type", "object"
			),
			"getItemsPage");

		_testGetToolOutputSchema(
			JSONFactoryUtil.createJSONObject(
				_read("post_test_v1.0_levels_output.json")),
			"postLevel");

		Assert.assertNull(_getOutputSchema("patchItem"));
		Assert.assertNull(_getOutputSchema("postItem"));
		Assert.assertNull(_getOutputSchema("putItem"));
	}

	@Test
	public void testGetToolSummaries() {
		List<ToolSummary> toolSummaries = OpenAPIUtil.getToolSummaries(
			_openAPIJSONObject);

		Assert.assertEquals(toolSummaries.toString(), 17, toolSummaries.size());

		Map<String, String> descriptions = new HashMap<>();

		for (ToolSummary toolSummary : toolSummaries) {
			descriptions.put(
				toolSummary.getName(), toolSummary.getDescription());
		}

		Assert.assertEquals(
			"This is the description", descriptions.get("getItem"));
		Assert.assertEquals(
			"This is the summary. This is the description",
			descriptions.get("getItems"));
		Assert.assertEquals(
			"This is the summary", descriptions.get("getItemsPage"));
		Assert.assertEquals(
			"GET /v1.0/localized", descriptions.get("getLocalized"));
		Assert.assertEquals(
			"GET /v1.0/localized-page", descriptions.get("getLocalizedPage"));
		Assert.assertEquals(
			"PATCH /v1.0/items/{itemId}", descriptions.get("patchItem"));
		Assert.assertEquals(
			"POST /v1.0/binaries", descriptions.get("postBinary"));
		Assert.assertEquals(
			"POST /v1.0/described", descriptions.get("postDescribed"));
		Assert.assertEquals(
			"POST /v1.0/empty-content", descriptions.get("postEmptyContent"));
		Assert.assertEquals("POST /v1.0/items", descriptions.get("postItem"));
		Assert.assertEquals("POST /v1.0/levels", descriptions.get("postLevel"));
		Assert.assertEquals(
			"POST /v1.0/no-content", descriptions.get("postNoContent"));
		Assert.assertEquals(
			"POST /v1.0/no-schema", descriptions.get("postNoSchema"));
		Assert.assertEquals(
			"POST /v1.0/parents", descriptions.get("postParent"));
		Assert.assertEquals(
			"POST /v1.0/undescribed", descriptions.get("postUndescribed"));
		Assert.assertEquals(
			"POST /v1.0/uploads", descriptions.get("postUpload"));
		Assert.assertEquals(
			"PUT /v1.0/items/{itemId}", descriptions.get("putItem"));

		AssertUtils.assertFailure(
			IllegalArgumentException.class,
			"OpenAPI document has no \"paths\" object",
			() -> OpenAPIUtil.getToolSummaries(
				JSONFactoryUtil.createJSONObject()));
	}

	@Test
	public void testGetToolWithRequiredInputSchemaOnly() throws Exception {
		JSONObject inputSchemaJSONObject = JSONFactoryUtil.createJSONObject(
			_read("get_test_v1.0_items.json"));

		_assertRequiredInputSchema(
			JSONUtil.put(
				"properties",
				JSONUtil.put(
					"fields",
					JSONUtil.getValueAsJSONObject(
						inputSchemaJSONObject, "JSONObject/properties",
						"JSONObject/fields"))
			).put(
				"type", "object"
			),
			"getItems");

		_assertRequiredInputSchema(
			JSONUtil.put(
				"properties",
				JSONUtil.put(
					"body",
					JSONUtil.put(
						"properties",
						JSONUtil.put("string", JSONUtil.put("type", "string"))
					).put(
						"required", JSONUtil.put("string")
					).put(
						"type", "object"
					))
			).put(
				"required", JSONUtil.put("body")
			).put(
				"type", "object"
			),
			"postItem");

		_assertRequiredInputSchema(
			JSONUtil.put(
				"properties",
				JSONUtil.put(
					"body",
					JSONUtil.put(
						"properties",
						JSONUtil.put(
							"name", JSONUtil.put("type", "string")
						).put(
							"next", JSONUtil.put("type", "object")
						)
					).put(
						"type", "object"
					))
			).put(
				"required", JSONUtil.put("body")
			).put(
				"type", "object"
			),
			"postLevel");
	}

	@Test
	public void testIsCollectionSchema() {
		JSONObject openAPIJSONObject = JSONUtil.put(
			"components",
			JSONUtil.put(
				"schemas",
				JSONUtil.put(
					"Item", JSONUtil.put("type", "object")
				).put(
					"PageItem",
					JSONUtil.put(
						"properties",
						JSONUtil.put(
							"items",
							JSONUtil.put(
								"items",
								JSONUtil.put(
									"$ref", "#/components/schemas/Item")
							).put(
								"type", "array"
							)
						).put(
							"totalCount", JSONUtil.put("type", "integer")
						)
					).put(
						"type", "object"
					)
				)));

		Assert.assertFalse(
			OpenAPIUtil.isCollectionSchema(openAPIJSONObject, null));
		Assert.assertTrue(
			OpenAPIUtil.isCollectionSchema(
				openAPIJSONObject,
				JSONUtil.put("$ref", "#/components/schemas/PageItem")));
		Assert.assertTrue(
			OpenAPIUtil.isCollectionSchema(
				openAPIJSONObject,
				JSONUtil.put(
					"items", JSONUtil.put("$ref", "#/components/schemas/Item")
				).put(
					"type", "array"
				)));
		Assert.assertFalse(
			OpenAPIUtil.isCollectionSchema(
				openAPIJSONObject,
				JSONUtil.put("$ref", "#/components/schemas/Item")));
	}

	private void _assertMultipartContentType(
		VulcanRequestForwarder.Request request) {

		String contentType = request.getContentType();

		Assert.assertNotNull(contentType);
		Assert.assertTrue(
			contentType,
			contentType.startsWith("multipart/form-data; boundary="));
	}

	private void _assertRequiredInputSchema(
			JSONObject expectedInputSchemaJSONObject, String toolName)
		throws Exception {

		ObjectMapper objectMapper = new ObjectMapper();
		Tool tool = OpenAPIUtil.getTool(
			true, _openAPIJSONObject, true, null, toolName);

		JSONAssert.assertEquals(
			expectedInputSchemaJSONObject.toString(),
			objectMapper.writeValueAsString(tool.getInputSchema()), true);
	}

	private FileItem _getFileItem(List<FileItem> fileItems, String fieldName) {
		for (FileItem fileItem : fileItems) {
			if (Objects.equals(fileItem.getFieldName(), fieldName)) {
				return fileItem;
			}
		}

		throw new IllegalArgumentException(
			StringBundler.concat(
				"No part named \"", fieldName, "\" in ", fileItems));
	}

	private String _getFileItemValue(List<FileItem> fileItems, String name) {
		FileItem fileItem = _getFileItem(fileItems, name);

		return fileItem.getString();
	}

	private List<FileItem> _getFileItems(VulcanRequestForwarder.Request request)
		throws Exception {

		FileUpload fileUpload = new FileUpload(new DiskFileItemFactory());

		byte[] body = request.getBody();

		return fileUpload.parseRequest(
			new UploadContext() {

				@Override
				public long contentLength() {
					return body.length;
				}

				@Override
				public String getCharacterEncoding() {
					return StandardCharsets.UTF_8.name();
				}

				@Override
				public int getContentLength() {
					return body.length;
				}

				@Override
				public String getContentType() {
					return request.getContentType();
				}

				@Override
				public InputStream getInputStream() {
					return new ByteArrayInputStream(body);
				}

			});
	}

	private Map<String, ?> _getInputSchema(
		JSONObject openAPIJSONObject, String toolName) {

		Tool tool = OpenAPIUtil.getTool(
			true, openAPIJSONObject, false, null, toolName);

		return tool.getInputSchema();
	}

	private Map<String, ?> _getOutputSchema(String toolName) {
		return OpenAPIUtil.getOutputSchema(_openAPIJSONObject, toolName);
	}

	private JSONObject _getResponseJSONObject(String type) {
		return JSONUtil.put(
			"content",
			JSONUtil.put(
				"application/json",
				JSONUtil.put("schema", JSONUtil.put("type", type))));
	}

	private String _getResponseSchemaType(JSONObject responsesJSONObject) {
		JSONObject schemaJSONObject = OpenAPIUtil.getResponseSchemaJSONObject(
			JSONUtil.put("responses", responsesJSONObject));

		return schemaJSONObject.getString("type");
	}

	private String _read(String fileName) throws Exception {
		return StringUtil.read(
			getClass().getResourceAsStream("dependencies/" + fileName));
	}

	private void _testGetRequest(
			String expectedBody, String expectedContentType,
			String expectedMethod, String expectedPathWithQuery,
			JSONObject inputJSONObject, String toolName)
		throws Exception {

		_testGetRequest(
			expectedBody, expectedContentType, expectedMethod,
			expectedPathWithQuery, inputJSONObject, null, toolName);
	}

	private void _testGetRequest(
			String expectedBody, String expectedContentType,
			String expectedMethod, String expectedPathWithQuery,
			JSONObject inputJSONObject, String restrictFields, String toolName)
		throws Exception {

		VulcanRequestForwarder.Request request = OpenAPIUtil.getRequest(
			StringPool.BLANK, null, inputJSONObject, _openAPIJSONObject,
			restrictFields, toolName, null);

		if (expectedBody == null) {
			Assert.assertNull(request.getBody());
			Assert.assertNull(request.getContentType());
		}
		else {
			Assert.assertEquals(
				expectedBody,
				new String(request.getBody(), StandardCharsets.UTF_8));
			Assert.assertEquals(expectedContentType, request.getContentType());
		}

		Assert.assertEquals(expectedMethod, request.getMethod());
		Assert.assertEquals(expectedPathWithQuery, request.getPath());
	}

	private void _testGetRequestFailure(
		Class<? extends Exception> expectedExceptionClass,
		String expectedMessage, JSONObject inputJSONObject,
		String restrictFields, String toolName) {

		AssertUtils.assertFailure(
			expectedExceptionClass, expectedMessage,
			() -> OpenAPIUtil.getRequest(
				StringPool.BLANK, null, inputJSONObject, _openAPIJSONObject,
				restrictFields, toolName, null));
	}

	private void _testGetTool(
			String expectedDescription, String expectedSchemaFileName,
			boolean injectVulcanParameters, String toolName)
		throws Exception {

		Tool tool = OpenAPIUtil.getTool(
			injectVulcanParameters, _openAPIJSONObject, false, null, toolName);

		Assert.assertEquals(expectedDescription, tool.getDescription());
		Assert.assertEquals(toolName, tool.getName());

		JSONAssert.assertEquals(
			_read(expectedSchemaFileName),
			new ObjectMapper(
			).writeValueAsString(
				tool.getInputSchema()
			),
			true);
	}

	private void _testGetTool(
			String expectedDescription, String expectedSchemaFileName,
			String toolName)
		throws Exception {

		_testGetTool(
			expectedDescription, expectedSchemaFileName, true, toolName);
	}

	private void _testGetToolOutputSchema(
			JSONObject expectedJSONObject, String toolName)
		throws Exception {

		JSONAssert.assertEquals(
			expectedJSONObject.toString(),
			new ObjectMapper(
			).writeValueAsString(
				_getOutputSchema(toolName)
			),
			true);
	}

	private JSONObject _openAPIJSONObject;

}