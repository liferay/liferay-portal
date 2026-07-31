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
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.HTTPTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.PropsValues;
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
		Tool tool = toolResource.getToolSetToolSetNameTool(
			"mcp-server-v1.0", "getToolSetsPage");

		Assert.assertEquals("getToolSetsPage", tool.getName());
		Assert.assertNotNull(tool.getInputSchema());

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

		String objectDefinitionName = ObjectDefinitionTestUtil.getRandomName();
		String objectFieldName = "a" + RandomTestUtil.randomString(8);

		ObjectDefinition companyObjectDefinition = _publishObjectDefinition(
			objectDefinitionName, objectFieldName, TestPropsValues.getUserId());

		Company company = CompanyTestUtil.addCompany();

		User user = UserTestUtil.getAdminUser(company.getCompanyId());

		String otherObjectFieldName = "a" + RandomTestUtil.randomString(8);

		_publishObjectDefinition(
			objectDefinitionName, otherObjectFieldName, user.getUserId());

		JSONAssert.assertEquals(
			JSONUtil.put(
				objectFieldName, JSONUtil.put("type", "string")
			).toString(),
			JSONUtil.getValueAsString(
				JSONFactoryUtil.createJSONObject(
					String.valueOf(_getTool(companyObjectDefinition))),
				"JSONObject/inputSchema", "JSONObject/properties",
				"JSONObject/body", "JSONObject/properties"),
			false);

		HTTPTestUtil.customize(
		).withCredentials(
			user.getEmailAddress(), PropsValues.DEFAULT_ADMIN_PASSWORD
		).apply(
			() -> JSONAssert.assertEquals(
				JSONUtil.put(
					otherObjectFieldName, JSONUtil.put("type", "string")
				).toString(),
				JSONUtil.getValueAsString(
					JSONFactoryUtil.createJSONObject(
						HTTPTestUtil.invokeToString(
							null,
							StringBundler.concat(
								"mcp-server/v1.0/tool-sets/",
								_getToolSetName(companyObjectDefinition),
								"/tools/post",
								companyObjectDefinition.getShortName()),
							HashMapBuilder.put(
								"Host", company.getVirtualHostname()
							).build(),
							Http.Method.GET)),
					"JSONObject/inputSchema", "JSONObject/properties",
					"JSONObject/body", "JSONObject/properties"),
				false)
		);
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

	private ObjectDefinition _publishObjectDefinition(
			String name, String objectFieldName, long userId)
		throws Exception {

		ObjectDefinition objectDefinition =
			ObjectDefinitionTestUtil.addCustomObjectDefinition(name, userId);

		_objectFieldLocalService.addCustomObjectField(
			null, userId, 0, objectDefinition.getObjectDefinitionId(),
			ObjectFieldConstants.BUSINESS_TYPE_TEXT,
			ObjectFieldConstants.DB_TYPE_STRING, false, false, null,
			LocalizedMapUtil.getLocalizedMap(objectFieldName), false,
			objectFieldName, null, null, false, false, Collections.emptyList());

		return _objectDefinitionLocalService.publishCustomObjectDefinition(
			userId, objectDefinition.getObjectDefinitionId());
	}

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectFieldLocalService _objectFieldLocalService;

}