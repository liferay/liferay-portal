/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mcp.server.rest.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.mcp.server.rest.client.dto.v1_0.Tool;
import com.liferay.mcp.server.rest.client.http.HttpInvoker;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.util.Base64;
import java.util.Collections;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.skyscreamer.jsonassert.JSONAssert;

/**
 * @author Alejandro Tardín
 */
@FeatureFlag("LPD-63311")
@RunWith(Arquillian.class)
public class ToolResourceTest extends BaseToolResourceTestCase {

	@Override
	@Test
	public void testGetToolSetToolSetNameTool() throws Exception {

		// Tool from a REST application

		Tool tool = toolResource.getToolSetToolSetNameTool(
			"mcp-server-v1.0", "getToolSetsPage");

		Assert.assertEquals("getToolSetsPage", tool.getName());
		Assert.assertNotNull(tool.getInputSchema());

		// Object field added after the tool was requested

		ObjectDefinition objectDefinition =
			ObjectDefinitionTestUtil.publishObjectDefinition();

		_getTool(objectDefinition);

		String name = "a" + RandomTestUtil.randomString(8);

		_objectFieldLocalService.addCustomObjectField(
			null, TestPropsValues.getUserId(), 0,
			objectDefinition.getObjectDefinitionId(),
			ObjectFieldConstants.BUSINESS_TYPE_TEXT,
			ObjectFieldConstants.DB_TYPE_STRING, false, false, null,
			LocalizedMapUtil.getLocalizedMap(name), false, name, null, null,
			false, false, Collections.emptyList());

		JSONAssert.assertEquals(
			JSONUtil.put(
				name, JSONUtil.put("type", "string")
			).toString(),
			JSONUtil.getValueAsString(
				JSONFactoryUtil.createJSONObject(
					String.valueOf(_getTool(objectDefinition))),
				"JSONObject/inputSchema", "JSONObject/properties",
				"JSONObject/body", "JSONObject/properties"),
			false);

	}

	@Override
	@Test
	public void testPostToolSetToolSetNameToolInvoke() throws Exception {
		byte[] bytes = RandomTestUtil.randomBytes();
		Base64.Encoder encoder = Base64.getEncoder();
		String fileName =
			"mcp-upload-" + RandomTestUtil.randomString() + ".txt";

		HttpInvoker.HttpResponse httpResponse =
			toolResource.postToolSetToolSetNameToolInvokeHttpResponse(
				"headless-delivery-v1.0", "postSiteDocument",
				JSONUtil.put(
					"file",
					JSONUtil.put(
						"contentType", "text/plain"
					).put(
						"data", encoder.encodeToString(bytes)
					).put(
						"filename", fileName
					)
				).put(
					"siteId", testGroup.getGroupId()
				).toString());

		Assert.assertEquals(
			httpResponse.getContent(), 200, httpResponse.getStatusCode());

		JSONObject documentJSONObject = JSONFactoryUtil.createJSONObject(
			httpResponse.getContent());

		Assert.assertTrue(documentJSONObject.getLong("id") > 0);
		Assert.assertEquals(
			bytes.length, documentJSONObject.getInt("sizeInBytes"));
		Assert.assertEquals(fileName, documentJSONObject.getString("title"));

		httpResponse =
			toolResource.postToolSetToolSetNameToolInvokeHttpResponse(
				"mcp-server-v1.0", "getToolSetToolSetNameToolSummariesPage",
				JSONUtil.put(
					"toolSetName", "mcp-server-v1.0"
				).toString());

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
			httpResponse.getContent());

		JSONArray jsonArray = jsonObject.getJSONArray("items");

		Assert.assertFalse(
			jsonArray.getJSONObject(
				0
			).has(
				"xClassName"
			));

		httpResponse =
			toolResource.postToolSetToolSetNameToolInvokeHttpResponse(
				"headless-delivery-v1.0", "getSiteDocumentsPage",
				JSONUtil.put(
					"fields", "id"
				).put(
					"siteId", testGroup.getGroupId()
				).toString());

		jsonObject = JSONFactoryUtil.createJSONObject(
			httpResponse.getContent());

		jsonArray = jsonObject.getJSONArray("items");

		JSONObject itemJSONObject = jsonArray.getJSONObject(0);

		Assert.assertTrue(itemJSONObject.has("id"));
		Assert.assertFalse(itemJSONObject.has("title"));
	}

	private Tool _getTool(ObjectDefinition objectDefinition) throws Exception {
		return toolResource.getToolSetToolSetNameTool(
			_getToolSetName(objectDefinition),
			"post" + objectDefinition.getShortName());
	}

	private String _getToolSetName(ObjectDefinition objectDefinition) {
		String restContextPath = objectDefinition.getRESTContextPath();

		return StringUtil.replace(
			restContextPath.substring(1), CharPool.SLASH, CharPool.DASH);
	}

	@Inject
	private ObjectFieldLocalService _objectFieldLocalService;

}