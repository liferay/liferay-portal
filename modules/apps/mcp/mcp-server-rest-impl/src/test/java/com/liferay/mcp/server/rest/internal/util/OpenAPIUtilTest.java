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
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.vulcan.http.VulcanRequestForwarder;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import java.nio.charset.StandardCharsets;

import java.util.Base64;
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
			JSONUtil.put("fields", ""), "getItems");
		_testGetRequest(
			null, null, "GET", "/v1.0/items?restrictFields=actions",
			JSONUtil.put("restrictFields", "name"), "getItems");
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
			"{}", "application/json", "POST", "/v1.0/items",
			JSONUtil.put("body", JSONFactoryUtil.createJSONObject()),
			"postItem");

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
			_openAPIJSONObject, "postBinary", null);

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
			_openAPIJSONObject, "postUpload", null);

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
			_openAPIJSONObject, "getItem", null);

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
				_openAPIJSONObject, "postItem", null));
	}

	@Test
	public void testGetTool() throws Exception {
		AssertUtils.assertFailure(
			IllegalArgumentException.class,
			"OpenAPI document has no tool with name \"missing\"",
			() -> OpenAPIUtil.getTool(true, _openAPIJSONObject, "missing"));
		AssertUtils.assertFailure(
			IllegalArgumentException.class,
			"OpenAPI document has no \"paths\" object",
			() -> OpenAPIUtil.getTool(
				true, JSONFactoryUtil.createJSONObject(),
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
	}

	@Test
	public void testGetToolSummaries() {
		List<ToolSummary> toolSummaries = OpenAPIUtil.getToolSummaries(
			_openAPIJSONObject);

		Assert.assertEquals(toolSummaries.toString(), 15, toolSummaries.size());
		_assertToolSummary(
			"POST /v1.0/described", "postDescribed", toolSummaries.get(0));
		_assertToolSummary(
			"POST /v1.0/levels", "postLevel", toolSummaries.get(1));
		_assertToolSummary(
			"POST /v1.0/undescribed", "postUndescribed", toolSummaries.get(2));
		_assertToolSummary(
			"This is the description", "getItem", toolSummaries.get(3));
		_assertToolSummary(
			"PATCH /v1.0/items/{itemId}", "patchItem", toolSummaries.get(4));
		_assertToolSummary(
			"PUT /v1.0/items/{itemId}", "putItem", toolSummaries.get(5));
		_assertToolSummary(
			"POST /v1.0/binaries", "postBinary", toolSummaries.get(6));
		_assertToolSummary(
			"POST /v1.0/empty-content", "postEmptyContent",
			toolSummaries.get(7));
		_assertToolSummary(
			"POST /v1.0/no-content", "postNoContent", toolSummaries.get(8));
		_assertToolSummary(
			"POST /v1.0/parents", "postParent", toolSummaries.get(9));
		_assertToolSummary(
			"POST /v1.0/uploads", "postUpload", toolSummaries.get(10));
		_assertToolSummary(
			"This is the summary. This is the description", "getItems",
			toolSummaries.get(11));
		_assertToolSummary(
			"POST /v1.0/items", "postItem", toolSummaries.get(12));
		_assertToolSummary(
			"POST /v1.0/no-schema", "postNoSchema", toolSummaries.get(13));
		_assertToolSummary(
			"This is the summary", "getItemsPage", toolSummaries.get(14));

		AssertUtils.assertFailure(
			IllegalArgumentException.class,
			"OpenAPI document has no \"paths\" object",
			() -> OpenAPIUtil.getToolSummaries(
				JSONFactoryUtil.createJSONObject()));
	}

	private void _assertMultipartContentType(
		VulcanRequestForwarder.Request request) {

		String contentType = request.getContentType();

		Assert.assertNotNull(contentType);
		Assert.assertTrue(
			contentType,
			contentType.startsWith("multipart/form-data; boundary="));
	}

	private void _assertToolSummary(
		String expectedDescription, String expectedName,
		ToolSummary toolSummary) {

		Assert.assertEquals(expectedDescription, toolSummary.getDescription());
		Assert.assertEquals(expectedName, toolSummary.getName());
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

	private String _getFileItemValue(List<FileItem> fileItems, String name) {
		FileItem fileItem = _getFileItem(fileItems, name);

		return fileItem.getString();
	}

	private Map<String, ?> _getInputSchema(
		JSONObject openAPIJSONObject, String toolName) {

		Tool tool = OpenAPIUtil.getTool(true, openAPIJSONObject, toolName);

		return tool.getInputSchema();
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

		VulcanRequestForwarder.Request request = OpenAPIUtil.getRequest(
			StringPool.BLANK, null, inputJSONObject, _openAPIJSONObject,
			toolName, null);

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

	private void _testGetTool(
			String expectedDescription, String expectedSchemaFileName,
			boolean injectVulcanParameters, String toolName)
		throws Exception {

		Tool tool = OpenAPIUtil.getTool(
			injectVulcanParameters, _openAPIJSONObject, toolName);

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

	private JSONObject _openAPIJSONObject;

}