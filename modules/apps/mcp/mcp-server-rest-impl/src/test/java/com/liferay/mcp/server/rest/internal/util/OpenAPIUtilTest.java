/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mcp.server.rest.internal.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.liferay.mcp.server.rest.dto.v1_0.Tool;
import com.liferay.mcp.server.rest.dto.v1_0.ToolSummary;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Base64;
import java.util.List;
import java.util.Map;

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
	public void testGetOptions() {
		_testGetOptions(
			null, null, Http.Method.GET,
			"http://localhost/test/v1.0/items?restrictFields=actions",
			JSONFactoryUtil.createJSONObject(), "getItems");
		_testGetOptions(
			null, null, Http.Method.GET,
			"http://localhost/test/v1.0/items?page=1&pageSize=20" +
				"&restrictFields=actions",
			JSONUtil.put(
				"page", "1"
			).put(
				"pageSize", "20"
			),
			"getItems");
		_testGetOptions(
			null, null, Http.Method.GET,
			"http://localhost/test/v1.0/items?filter=name+eq+%27John+Doe%27" +
				"&restrictFields=actions",
			JSONUtil.put("filter", "name eq 'John Doe'"), "getItems");
		_testGetOptions(
			null, null, Http.Method.GET,
			"http://localhost/test/v1.0/items?restrictFields=actions",
			JSONUtil.put("fields", "name"), "getItems");
		_testGetOptions(
			null, null, Http.Method.GET,
			"http://localhost/test/v1.0/items?restrictFields=actions",
			JSONUtil.put("restrictFields", "name"), "getItems");
		_testGetOptions(
			null, null, Http.Method.GET,
			"http://localhost/test/v1.0/items/123?restrictFields=actions",
			JSONUtil.put("itemId", "123"), "getItem");
		_testGetOptions(
			JSONUtil.put(
				"name", "Test"
			).toString(),
			"application/json", Http.Method.PATCH,
			"http://localhost/test/v1.0/items/123",
			JSONUtil.put(
				"body", JSONUtil.put("name", "Test")
			).put(
				"itemId", "123"
			),
			"patchItem");

		String fileContent = RandomTestUtil.randomString();
		String fileName = RandomTestUtil.randomString();
		String name = RandomTestUtil.randomString();

		Http.Options options = OpenAPIUtil.getOptions(
			"http://localhost/test",
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
			_openAPIJSONObject, "postBinary");

		Assert.assertNull(options.getBody());
		_assertMultipartContentType(options);
		Assert.assertEquals(Http.Method.POST, options.getMethod());
		Assert.assertEquals(
			"http://localhost/test/v1.0/binaries", options.getLocation());

		List<Http.FilePart> fileParts = options.getFileParts();

		Assert.assertEquals(fileParts.toString(), 1, fileParts.size());

		Http.FilePart filePart = fileParts.get(0);

		Assert.assertEquals("text/plain", filePart.getContentType());
		Assert.assertEquals(fileName, filePart.getFileName());
		Assert.assertEquals("data", filePart.getName());
		Assert.assertArrayEquals(fileContent.getBytes(), filePart.getValue());

		Map<String, String> parts = options.getParts();

		Assert.assertEquals(parts.toString(), 1, parts.size());
		Assert.assertEquals(name, parts.get("name"));

		options = OpenAPIUtil.getOptions(
			"http://localhost/test",
			JSONUtil.put(
				"boolean", true
			).put(
				"integer", 1
			).put(
				"string", fileContent
			),
			_openAPIJSONObject, "postUpload");

		Assert.assertNull(options.getBody());
		_assertMultipartContentType(options);
		Assert.assertNull(options.getFileParts());
		Assert.assertEquals(Http.Method.POST, options.getMethod());
		Assert.assertEquals(
			"http://localhost/test/v1.0/uploads", options.getLocation());

		parts = options.getParts();

		Assert.assertEquals(parts.toString(), 3, parts.size());
		Assert.assertEquals("true", parts.get("boolean"));
		Assert.assertEquals("1", parts.get("integer"));
		Assert.assertEquals(fileContent, parts.get("string"));
	}

	@Test
	public void testGetTool() throws Exception {
		AssertUtils.assertFailure(
			IllegalArgumentException.class,
			"OpenAPI document has no tool with name \"missing\"",
			() -> OpenAPIUtil.getTool(_openAPIJSONObject, "missing"));
		AssertUtils.assertFailure(
			IllegalArgumentException.class,
			"OpenAPI document has no \"paths\" object",
			() -> OpenAPIUtil.getTool(
				JSONFactoryUtil.createJSONObject(),
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
		_testGetTool("This is the summary", "get_c_test.json", "getItemsPage");
		_testGetTool(
			"PATCH /v1.0/items/{itemId}", "patch_test_v1.0_items_itemId.json",
			"patchItem");
		_testGetTool(
			"POST /v1.0/binaries", "post_test_v1.0_binaries.json",
			"postBinary");
		_testGetTool(
			"POST /v1.0/items", "post_test_v1.0_items.json", "postItem");
		_testGetTool(
			"POST /v1.0/parents", "post_test_v1.0_parents.json", "postParent");
		_testGetTool(
			"POST /v1.0/uploads", "post_test_v1.0_uploads.json", "postUpload");
		_testGetTool(
			"PUT /v1.0/items/{itemId}", "put_test_v1.0_items_itemId.json",
			"putItem");
	}

	@Test
	public void testGetToolMergesRequestBodyAndSchemaDescriptions() {
		JSONObject openAPIJSONObject = JSONUtil.put(
			"paths",
			JSONUtil.put(
				"/things",
				JSONUtil.put(
					"post",
					JSONUtil.put(
						"operationId", "postThing"
					).put(
						"requestBody",
						JSONUtil.put(
							"content",
							JSONUtil.put(
								"application/json",
								JSONUtil.put(
									"schema",
									JSONUtil.put(
										"description", "A Thing resource."
									).put(
										"type", "object"
									)))
						).put(
							"description", "The thing to create."
						)
					))));

		Tool tool = OpenAPIUtil.getTool(openAPIJSONObject, "postThing");

		Map<String, ?> inputSchema = tool.getInputSchema();

		Map<?, ?> properties = (Map<?, ?>)inputSchema.get("properties");

		Map<?, ?> bodySchema = (Map<?, ?>)properties.get("body");

		Assert.assertEquals(
			"The thing to create. A Thing resource.",
			bodySchema.get("description"));
	}

	@Test
	public void testGetToolRequestBodyDescriptionIsSurfaced() {
		JSONObject openAPIJSONObject = JSONUtil.put(
			"paths",
			JSONUtil.put(
				"/things",
				JSONUtil.put(
					"post",
					JSONUtil.put(
						"operationId", "postThing"
					).put(
						"requestBody",
						JSONUtil.put(
							"content",
							JSONUtil.put(
								"application/json",
								JSONUtil.put(
									"schema", JSONUtil.put("type", "object")))
						).put(
							"description", "The thing to create."
						)
					))));

		Tool tool = OpenAPIUtil.getTool(openAPIJSONObject, "postThing");

		Map<String, ?> inputSchema = tool.getInputSchema();

		Map<?, ?> properties = (Map<?, ?>)inputSchema.get("properties");

		Map<?, ?> bodySchema = (Map<?, ?>)properties.get("body");

		Assert.assertEquals(
			"The thing to create.", bodySchema.get("description"));
	}

	@Test
	public void testGetToolRequestBodyWithoutContentDoesNotFail() {
		JSONObject openAPIJSONObject = JSONUtil.put(
			"paths",
			JSONUtil.put(
				"/things",
				JSONUtil.put(
					"post",
					JSONUtil.put(
						"operationId", "postThing"
					).put(
						"requestBody",
						JSONUtil.put("description", "The thing to create.")
					))));

		Tool tool = OpenAPIUtil.getTool(openAPIJSONObject, "postThing");

		Map<String, ?> inputSchema = tool.getInputSchema();

		Map<?, ?> properties = (Map<?, ?>)inputSchema.get("properties");

		Map<?, ?> bodySchema = (Map<?, ?>)properties.get("body");

		Assert.assertEquals("object", bodySchema.get("type"));
		Assert.assertEquals(
			"The thing to create.", bodySchema.get("description"));
	}

	@Test
	public void testGetToolSummaries() {
		List<ToolSummary> toolSummaries = OpenAPIUtil.getToolSummaries(
			_openAPIJSONObject);

		Assert.assertEquals(toolSummaries.toString(), 12, toolSummaries.size());
		_assertToolSummary(
			toolSummaries.get(0), "This is the description", "getItem");
		_assertToolSummary(
			toolSummaries.get(1), "PATCH /v1.0/items/{itemId}", "patchItem");
		_assertToolSummary(
			toolSummaries.get(2), "PUT /v1.0/items/{itemId}", "putItem");
		_assertToolSummary(
			toolSummaries.get(3), "POST /v1.0/binaries", "postBinary");
		_assertToolSummary(
			toolSummaries.get(4), "POST /v1.0/empty-content",
			"postEmptyContent");
		_assertToolSummary(
			toolSummaries.get(5), "POST /v1.0/no-content", "postNoContent");
		_assertToolSummary(
			toolSummaries.get(6), "POST /v1.0/parents", "postParent");
		_assertToolSummary(
			toolSummaries.get(7), "POST /v1.0/uploads", "postUpload");
		_assertToolSummary(
			toolSummaries.get(8),
			"This is the summary. This is the description", "getItems");
		_assertToolSummary(
			toolSummaries.get(9), "POST /v1.0/items", "postItem");
		_assertToolSummary(
			toolSummaries.get(10), "POST /v1.0/no-schema", "postNoSchema");
		_assertToolSummary(
			toolSummaries.get(11), "This is the summary", "getItemsPage");

		AssertUtils.assertFailure(
			IllegalArgumentException.class,
			"OpenAPI document has no \"paths\" object",
			() -> OpenAPIUtil.getToolSummaries(
				JSONFactoryUtil.createJSONObject()));
	}

	private void _assertMultipartContentType(Http.Options options) {
		String contentTypeHeader = options.getHeader("Content-Type");

		Assert.assertNotNull(contentTypeHeader);
		Assert.assertTrue(
			contentTypeHeader,
			contentTypeHeader.startsWith("multipart/form-data; boundary="));
	}

	private void _assertToolSummary(
		ToolSummary toolSummary, String expectedDescription,
		String expectedName) {

		Assert.assertEquals(expectedDescription, toolSummary.getDescription());
		Assert.assertEquals(expectedName, toolSummary.getName());
	}

	private Map<String, ?> _getInputSchema(
		JSONObject openAPIJSONObject, String toolName) {

		Tool tool = OpenAPIUtil.getTool(openAPIJSONObject, toolName);

		return tool.getInputSchema();
	}

	private String _read(String fileName) throws Exception {
		return StringUtil.read(
			getClass().getResourceAsStream("dependencies/" + fileName));
	}

	private void _testGetOptions(
		String expectedBody, String expectedContentType,
		Http.Method expectedMethod, String expectedURL,
		JSONObject inputJSONObject, String toolName) {

		Http.Options options = OpenAPIUtil.getOptions(
			"http://localhost/test", inputJSONObject, _openAPIJSONObject,
			toolName);

		Http.Body body = options.getBody();

		if (expectedBody == null) {
			Assert.assertNull(body);
		}
		else {
			Assert.assertEquals(expectedBody, body.getContent());
			Assert.assertEquals(expectedContentType, body.getContentType());
		}

		Assert.assertNull(options.getFileParts());
		Assert.assertEquals(expectedURL, options.getLocation());
		Assert.assertEquals(expectedMethod, options.getMethod());
		Assert.assertNull(options.getParts());
	}

	private void _testGetTool(
			String expectedDescription, String expectedSchemaFileName,
			String toolName)
		throws Exception {

		Tool tool = OpenAPIUtil.getTool(_openAPIJSONObject, toolName);

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

	private JSONObject _openAPIJSONObject;

}