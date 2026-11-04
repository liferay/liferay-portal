/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.rest.internal.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.rest.test.util.ObjectEntryTestUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.HTTPTestUtil;
import com.liferay.portal.kernel.util.Http;
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
 */
@FeatureFlags(featureFlags = @FeatureFlag(value = "LPS-164801"))
@RunWith(Arquillian.class)
public class ObjectEntryResourcePaginationTest
	extends BaseObjectEntryResourceTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testGetObjectEntriesWithPagination() throws Exception {
		ObjectEntryTestUtil.addObjectEntry(
			getObjectDefinition1(), OBJECT_FIELD_NAME_TEXT,
			NEW_OBJECT_FIELD_VALUE_1);
		ObjectEntryTestUtil.addObjectEntry(
			getObjectDefinition1(), OBJECT_FIELD_NAME_TEXT,
			NEW_OBJECT_FIELD_VALUE_1);
		ObjectEntryTestUtil.addObjectEntry(
			getObjectDefinition1(), OBJECT_FIELD_NAME_TEXT,
			NEW_OBJECT_FIELD_VALUE_1);
		ObjectEntryTestUtil.addObjectEntry(
			getObjectDefinition1(), OBJECT_FIELD_NAME_TEXT,
			NEW_OBJECT_FIELD_VALUE_1);
		ObjectEntryTestUtil.addObjectEntry(
			getObjectDefinition1(), OBJECT_FIELD_NAME_TEXT,
			NEW_OBJECT_FIELD_VALUE_1);
		ObjectEntryTestUtil.addObjectEntry(
			getObjectDefinition1(), OBJECT_FIELD_NAME_TEXT,
			NEW_OBJECT_FIELD_VALUE_1);
		ObjectEntryTestUtil.addObjectEntry(
			getObjectDefinition1(), OBJECT_FIELD_NAME_TEXT,
			NEW_OBJECT_FIELD_VALUE_1);

		_assertPagination(7, getObjectDefinition1());

		ObjectEntryTestUtil.addObjectEntry(
			getObjectDefinition1(), OBJECT_FIELD_NAME_TEXT,
			NEW_OBJECT_FIELD_VALUE_1);
		ObjectEntryTestUtil.addObjectEntry(
			getObjectDefinition1(), OBJECT_FIELD_NAME_TEXT,
			NEW_OBJECT_FIELD_VALUE_1);

		_assertPagination(9, getObjectDefinition1());

		ObjectEntryTestUtil.addObjectEntry(
			getObjectDefinition1(), OBJECT_FIELD_NAME_TEXT,
			NEW_OBJECT_FIELD_VALUE_1);
		ObjectEntryTestUtil.addObjectEntry(
			getObjectDefinition1(), OBJECT_FIELD_NAME_TEXT,
			NEW_OBJECT_FIELD_VALUE_1);

		_assertPagination(11, getObjectDefinition1());
	}

	private void _assertPagination(
			int expectedSize, ObjectDefinition objectDefinition)
		throws Exception {

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			null, objectDefinition.getRESTContextPath() + "?page=1&pageSize=5",
			Http.Method.GET);

		JSONArray itemsJSONArray = jsonObject.getJSONArray("items");

		Assert.assertEquals(5, itemsJSONArray.length());

		Assert.assertEquals(1, jsonObject.getLong("page"));
		Assert.assertEquals(5, jsonObject.getLong("pageSize"));
		Assert.assertEquals(expectedSize, jsonObject.getLong("totalCount"));

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			null, objectDefinition.getRESTContextPath() + "?pageSize=-1",
			Http.Method.GET);

		itemsJSONArray = jsonObject.getJSONArray("items");

		Assert.assertEquals(expectedSize, itemsJSONArray.length());

		Assert.assertEquals(expectedSize, jsonObject.getLong("totalCount"));
	}

}