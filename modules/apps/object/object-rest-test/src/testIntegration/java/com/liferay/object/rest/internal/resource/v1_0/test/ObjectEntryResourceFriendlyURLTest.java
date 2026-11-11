/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.rest.internal.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.HTTPTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Luis Miguel Barcos
 * @author Magdalena Jedraszak
 * @author Daniel Raposo
 */
@FeatureFlags(featureFlags = @FeatureFlag(value = "LPS-164801"))
@RunWith(Arquillian.class)
public class ObjectEntryResourceFriendlyURLTest
	extends BaseObjectEntryResourceTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testGetObjectEntryWithFriendlyURL() throws Exception {
		ObjectDefinition objectDefinition = getObjectDefinition1();

		objectDefinition.setEnableFriendlyURLCustomization(true);

		objectDefinition = objectDefinitionLocalService.updateObjectDefinition(
			objectDefinition);

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_1, RandomTestUtil.randomInt()
			).toString(),
			objectDefinition.getRESTContextPath(), Http.Method.POST);

		jsonObject = getObjectEntryJSONObject(
			objectDefinition, jsonObject.getString("id"));

		Assert.assertEquals(
			jsonObject.getString("externalReferenceCode"),
			jsonObject.getString("friendlyUrlPath"));

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				"friendlyUrlPath", StringPool.SLASH
			).toString(),
			objectDefinition.getRESTContextPath(), Http.Method.POST);

		jsonObject = getObjectEntryJSONObject(
			objectDefinition, jsonObject.getString("id"));

		Assert.assertEquals(
			jsonObject.getString("externalReferenceCode"),
			jsonObject.getString("friendlyUrlPath"));

		_assertGetObjectEntryWithFriendlyURL("hello-world", "/hello-world");
		_assertGetObjectEntryWithFriendlyURL("parent/child", "parent//child");
		_assertGetObjectEntryWithFriendlyURL("test-url", "Test URL");
	}

	@Test
	public void testPatchObjectEntryWithFriendlyURLCustomizationEnabled()
		throws Exception {

		// With friendly URL

		ObjectDefinition objectDefinition = getObjectDefinition1();

		objectDefinition.setEnableFriendlyURLCustomization(true);

		objectDefinition = objectDefinitionLocalService.updateObjectDefinition(
			objectDefinition);

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_1, RandomTestUtil.randomString()
			).toString(),
			objectDefinition.getRESTContextPath(), Http.Method.POST);

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				"friendlyUrlPath_i18n",
				HashMapBuilder.put(
					"en_US", "Test URL"
				).build()
			).toString(),
			objectDefinition.getRESTContextPath() + StringPool.SLASH +
				jsonObject.getString("id"),
			Http.Method.PATCH);

		Assert.assertEquals(
			"test-url", jsonObject.getString("friendlyUrlPath"));

		// Without friendly URL

		String friendlyUrlPath = StringUtil.toLowerCase(
			RandomTestUtil.randomString());
		String objectFieldValue = RandomTestUtil.randomString();

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_1, objectFieldValue
			).put(
				"friendlyUrlPath_i18n",
				HashMapBuilder.put(
					"en_US", friendlyUrlPath
				).build()
			).toString(),
			objectDefinition.getRESTContextPath(), Http.Method.POST);

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_1, objectFieldValue
			).toString(),
			objectDefinition.getRESTContextPath() + StringPool.SLASH +
				jsonObject.getString("id"),
			Http.Method.PATCH);

		Assert.assertEquals(
			friendlyUrlPath, jsonObject.getString("friendlyUrlPath"));
	}

	private void _assertGetObjectEntryWithFriendlyURL(
			String expectedFriendlyUrlPath, String friendlyUrlPath)
		throws Exception {

		ObjectDefinition objectDefinition = getObjectDefinition1();

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				"friendlyUrlPath", friendlyUrlPath
			).toString(),
			objectDefinition.getRESTContextPath(), Http.Method.POST);

		jsonObject = getObjectEntryJSONObject(
			objectDefinition, jsonObject.getString("id"));

		Assert.assertEquals(
			expectedFriendlyUrlPath, jsonObject.getString("friendlyUrlPath"));
	}

}