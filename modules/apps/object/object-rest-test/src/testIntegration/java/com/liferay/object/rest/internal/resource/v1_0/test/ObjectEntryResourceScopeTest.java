/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.rest.internal.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.rest.test.util.ObjectEntryTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.HTTPTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserGroupTestUtil;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.Collections;

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
public class ObjectEntryResourceScopeTest
	extends BaseObjectEntryResourceTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testDeleteScopeScopeKeyByExternalReferenceCode()
		throws Exception {

		// Site scope external reference code

		Group group = groupLocalService.fetchGroup(testGroupId);

		_siteScopedObjectEntry1 = ObjectEntryTestUtil.addObjectEntry(
			getSiteScopedObjectDefinition1(), OBJECT_FIELD_NAME_1,
			OBJECT_FIELD_VALUE_1);

		HTTPTestUtil.invokeToJSONObject(
			null,
			StringBundler.concat(
				getEndpoint(
					getSiteScopedObjectDefinition1(),
					group.getExternalReferenceCode()),
				"/by-external-reference-code/",
				_siteScopedObjectEntry1.getExternalReferenceCode()),
			Http.Method.DELETE);

		assertNotFound(
			HTTPTestUtil.invokeToJSONObject(
				null,
				StringBundler.concat(
					getSiteScopedObjectDefinition1().getRESTContextPath(),
					StringPool.SLASH,
					_siteScopedObjectEntry1.getObjectEntryId()),
				Http.Method.GET));

		// Site scope group ID

		_siteScopedObjectEntry1 = ObjectEntryTestUtil.addObjectEntry(
			getSiteScopedObjectDefinition1(), OBJECT_FIELD_NAME_1,
			OBJECT_FIELD_VALUE_1);

		HTTPTestUtil.invokeToJSONObject(
			null,
			StringBundler.concat(
				getEndpoint(
					getSiteScopedObjectDefinition1(), group.getGroupId()),
				"/by-external-reference-code/",
				_siteScopedObjectEntry1.getExternalReferenceCode()),
			Http.Method.DELETE);

		assertNotFound(
			HTTPTestUtil.invokeToJSONObject(
				null,
				StringBundler.concat(
					getSiteScopedObjectDefinition1().getRESTContextPath(),
					StringPool.SLASH,
					_siteScopedObjectEntry1.getObjectEntryId()),
				Http.Method.GET));

		// Site scope group key

		_siteScopedObjectEntry1 = ObjectEntryTestUtil.addObjectEntry(
			getSiteScopedObjectDefinition1(), OBJECT_FIELD_NAME_1,
			OBJECT_FIELD_VALUE_1);

		HTTPTestUtil.invokeToJSONObject(
			null,
			StringBundler.concat(
				getEndpoint(
					getSiteScopedObjectDefinition1(), group.getGroupKey()),
				"/by-external-reference-code/",
				_siteScopedObjectEntry1.getExternalReferenceCode()),
			Http.Method.DELETE);

		assertNotFound(
			HTTPTestUtil.invokeToJSONObject(
				null,
				StringBundler.concat(
					getSiteScopedObjectDefinition1().getRESTContextPath(),
					StringPool.SLASH,
					_siteScopedObjectEntry1.getObjectEntryId()),
				Http.Method.GET));
	}

	@Test
	public void testGetScopeScopeKeyByExternalReferenceCode() throws Exception {

		// Site scope external reference code

		Group group = groupLocalService.fetchGroup(testGroupId);

		String objectFieldValue = RandomTestUtil.randomString();

		_siteScopedObjectEntry1 = ObjectEntryTestUtil.addObjectEntry(
			getSiteScopedObjectDefinition1(), OBJECT_FIELD_NAME_1,
			objectFieldValue);

		Assert.assertEquals(
			objectFieldValue,
			JSONUtil.getValueAsString(
				HTTPTestUtil.invokeToJSONObject(
					null,
					StringBundler.concat(
						getEndpoint(
							getSiteScopedObjectDefinition1(),
							group.getExternalReferenceCode()),
						"/by-external-reference-code/",
						_siteScopedObjectEntry1.getExternalReferenceCode()),
					Http.Method.GET),
				"Object/" + OBJECT_FIELD_NAME_1));

		// Site scope group ID

		Assert.assertEquals(
			objectFieldValue,
			JSONUtil.getValueAsString(
				HTTPTestUtil.invokeToJSONObject(
					null,
					StringBundler.concat(
						getEndpoint(
							getSiteScopedObjectDefinition1(),
							group.getGroupId()),
						"/by-external-reference-code/",
						_siteScopedObjectEntry1.getExternalReferenceCode()),
					Http.Method.GET),
				"Object/" + OBJECT_FIELD_NAME_1));

		// Site scope group key

		Assert.assertEquals(
			objectFieldValue,
			JSONUtil.getValueAsString(
				HTTPTestUtil.invokeToJSONObject(
					null,
					StringBundler.concat(
						getEndpoint(
							getSiteScopedObjectDefinition1(),
							group.getGroupKey()),
						"/by-external-reference-code/",
						_siteScopedObjectEntry1.getExternalReferenceCode()),
					Http.Method.GET),
				"Object/" + OBJECT_FIELD_NAME_1));
	}

	@Test
	public void testGetScopeScopeKeyObjectEntriesPage() throws Exception {

		// Depot scope group ID

		ObjectDefinition depotScopedObjectDefinition =
			addDepotScopedObjectDefinition();

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.portal.vulcan.internal.jaxrs.exception.mapper." +
					"WebApplicationExceptionMapper",
				LoggerTestUtil.ERROR)) {

			assertNotFound(
				HTTPTestUtil.invokeToJSONObject(
					null,
					getEndpoint(
						depotScopedObjectDefinition,
						RandomTestUtil.randomLong()),
					Http.Method.GET));
		}

		DepotEntry depotEntry = depotEntryLocalService.addDepotEntry(
			RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomLocaleStringMap(),
			DepotConstants.TYPE_ASSET_LIBRARY,
			ServiceContextTestUtil.getServiceContext());

		ObjectEntry depotScopedObjectEntry = ObjectEntryTestUtil.addObjectEntry(
			depotEntry.getGroupId(), depotScopedObjectDefinition,
			Collections.emptyMap());

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			getEndpoint(depotScopedObjectDefinition, depotEntry.getGroupId()),
			Http.Method.GET);

		assertItem(
			0, jsonObject, "id", depotScopedObjectEntry.getObjectEntryId());

		// Site scope external reference code

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.portal.vulcan.internal.jaxrs.exception.mapper." +
					"WebApplicationExceptionMapper",
				LoggerTestUtil.ERROR)) {

			assertNotFound(
				HTTPTestUtil.invokeToJSONObject(
					null,
					getEndpoint(
						getSiteScopedObjectDefinition1(),
						RandomTestUtil.randomLong()),
					Http.Method.GET));
		}

		_siteScopedObjectEntry1 = ObjectEntryTestUtil.addObjectEntry(
			getSiteScopedObjectDefinition1(), OBJECT_FIELD_NAME_1,
			OBJECT_FIELD_VALUE_1);

		Group group = groupLocalService.fetchGroup(testGroupId);

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			getEndpoint(
				getSiteScopedObjectDefinition1(),
				group.getExternalReferenceCode()),
			Http.Method.GET);

		assertItem(0, jsonObject, OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1);

		// Site scope group ID

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			getEndpoint(getSiteScopedObjectDefinition1(), group.getGroupId()),
			Http.Method.GET);

		assertItem(0, jsonObject, OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1);

		// Site scope group key

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			getEndpoint(getSiteScopedObjectDefinition1(), group.getGroupKey()),
			Http.Method.GET);

		assertItem(0, jsonObject, OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1);
	}

	@Test
	@TestInfo("LPD-53245")
	public void testPatchScopeScopeKeyByExternalReferenceCode()
		throws Exception {

		Group group = groupLocalService.fetchGroup(testGroupId);

		// Scope key: external reference code

		String externalReferenceCodeEndpoint = getEndpoint(
			getSiteScopedObjectDefinition1(), group.getExternalReferenceCode());

		assertPatchCustomObjectEntry(
			jsonObject ->
				externalReferenceCodeEndpoint + "/by-external-reference-code/" +
					jsonObject.getString("externalReferenceCode"),
			getSiteScopedObjectDefinition1(), getSiteScopedObjectDefinition2(),
			group.getExternalReferenceCode());

		// Scope key: group ID

		String groupIdEndpoint = getEndpoint(
			getSiteScopedObjectDefinition1(), testGroupId);

		assertPatchCustomObjectEntry(
			jsonObject ->
				groupIdEndpoint + "/by-external-reference-code/" +
					jsonObject.getString("externalReferenceCode"),
			getSiteScopedObjectDefinition1(), getSiteScopedObjectDefinition2(),
			testGroupId);

		// Scope key: group key

		String groupKeyEndpoint = getEndpoint(
			getSiteScopedObjectDefinition1(), group.getGroupKey());

		assertPatchCustomObjectEntry(
			jsonObject ->
				groupKeyEndpoint + "/by-external-reference-code/" +
					jsonObject.getString("externalReferenceCode"),
			getSiteScopedObjectDefinition1(), getSiteScopedObjectDefinition2(),
			group.getGroupKey());
	}

	@Test
	public void testPostScopeScopeKey() throws Exception {
		Group group1 = groupLocalService.fetchGroup(testGroupId);
		Group group2 = GroupTestUtil.addGroup();

		_testPostScopeScopeKey(
			group1.getExternalReferenceCode(),
			group2.getExternalReferenceCode());
		_testPostScopeScopeKey(group1.getGroupId(), group2.getGroupId());
		_testPostScopeScopeKey(group1.getGroupKey(), group2.getGroupKey());
	}

	@Test
	public void testPostSiteScopedCustomObjectEntryInUserGroup()
		throws Exception {

		UserGroup userGroup = UserGroupTestUtil.addUserGroup();

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			getEndpoint(
				getSiteScopedObjectDefinition1(), userGroup.getGroupId()),
			Http.Method.GET);

		JSONArray itemsJSONArray = jsonObject.getJSONArray("items");

		Assert.assertEquals(0, itemsJSONArray.length());

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1
			).toString(),
			getEndpoint(
				getSiteScopedObjectDefinition1(), userGroup.getGroupId()),
			Http.Method.POST);

		Assert.assertEquals(
			OBJECT_FIELD_VALUE_1, jsonObject.getInt(OBJECT_FIELD_NAME_1));
	}

	@Test
	@TestInfo("LPD-53245")
	public void testPutScopeScopeKeyByExternalReferenceCode() throws Exception {
		Group group = groupLocalService.fetchGroup(testGroupId);

		// Scope key: external reference code

		String externalReferenceCodeEndpoint = getEndpoint(
			getSiteScopedObjectDefinition1(), group.getExternalReferenceCode());

		assertPutCustomObjectEntry(
			jsonObject ->
				externalReferenceCodeEndpoint + "/by-external-reference-code/" +
					jsonObject.getString("externalReferenceCode"),
			getSiteScopedObjectDefinition1(), getSiteScopedObjectDefinition2(),
			group.getExternalReferenceCode());

		// Scope key: group ID

		String groupIdEndpoint = getEndpoint(
			getSiteScopedObjectDefinition1(), testGroupId);

		assertPutCustomObjectEntry(
			jsonObject ->
				groupIdEndpoint + "/by-external-reference-code/" +
					jsonObject.getString("externalReferenceCode"),
			getSiteScopedObjectDefinition1(), getSiteScopedObjectDefinition2(),
			testGroupId);

		// Scope key: group key

		String groupKeyEndpoint = getEndpoint(
			getSiteScopedObjectDefinition1(), group.getGroupKey());

		assertPutCustomObjectEntry(
			jsonObject ->
				groupKeyEndpoint + "/by-external-reference-code/" +
					jsonObject.getString("externalReferenceCode"),
			getSiteScopedObjectDefinition1(), getSiteScopedObjectDefinition2(),
			group.getGroupKey());
	}

	private void _testPostScopeScopeKey(Object scopeKey1, Object scopeKey2)
		throws Exception {

		String objectFieldValue = RandomTestUtil.randomString();

		Assert.assertEquals(
			objectFieldValue,
			JSONUtil.getValueAsString(
				HTTPTestUtil.invokeToJSONObject(
					JSONUtil.put(
						OBJECT_FIELD_NAME_1, objectFieldValue
					).toString(),
					getEndpoint(getSiteScopedObjectDefinition1(), scopeKey1),
					Http.Method.POST),
				"Object/" + OBJECT_FIELD_NAME_1));

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			null, getEndpoint(getSiteScopedObjectDefinition1(), scopeKey2),
			Http.Method.GET);

		JSONArray itemsJSONArray = jsonObject.getJSONArray("items");

		Assert.assertEquals(0, itemsJSONArray.length());
	}

	private ObjectEntry _siteScopedObjectEntry1;

}