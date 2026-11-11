/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
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
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
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
 * @author Magdalena Jedraszak
 * @author Daniel Raposo
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

		Group testGroup = groupLocalService.fetchGroup(testGroupId);

		_testDeleteScopeScopeKeyByExternalReferenceCode(
			testGroup.getExternalReferenceCode());
		_testDeleteScopeScopeKeyByExternalReferenceCode(testGroup.getGroupId());
		_testDeleteScopeScopeKeyByExternalReferenceCode(
			testGroup.getGroupKey());
	}

	@Test
	public void testGetScopeScopeKeyByExternalReferenceCode() throws Exception {
		Group testGroup = groupLocalService.fetchGroup(testGroupId);

		_testGetScopeScopeKeyByExternalReferenceCode(
			testGroup.getExternalReferenceCode());
		_testGetScopeScopeKeyByExternalReferenceCode(testGroup.getGroupId());
		_testGetScopeScopeKeyByExternalReferenceCode(testGroup.getGroupKey());
	}

	@Test
	public void testGetScopeScopeKeyObjectEntriesPage() throws Exception {

		// Depot scope group ID

		ObjectDefinition depotScopedObjectDefinition =
			publishDepotScopedObjectDefinition();

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

		ObjectEntryTestUtil.addObjectEntry(
			getSiteScopedObjectDefinition1(), OBJECT_FIELD_NAME_1,
			OBJECT_FIELD_VALUE_1);

		Group testGroup = groupLocalService.fetchGroup(testGroupId);

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			getEndpoint(
				getSiteScopedObjectDefinition1(),
				testGroup.getExternalReferenceCode()),
			Http.Method.GET);

		assertItem(0, jsonObject, OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1);

		// Site scope group ID

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			getEndpoint(
				getSiteScopedObjectDefinition1(), testGroup.getGroupId()),
			Http.Method.GET);

		assertItem(0, jsonObject, OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1);

		// Site scope group key

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			getEndpoint(
				getSiteScopedObjectDefinition1(), testGroup.getGroupKey()),
			Http.Method.GET);

		assertItem(0, jsonObject, OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1);
	}

	@Test
	@TestInfo("LPD-53245")
	public void testPatchScopeScopeKeyByExternalReferenceCode()
		throws Exception {

		Group testGroup = groupLocalService.fetchGroup(testGroupId);

		_testPatchScopeScopeKeyByExternalReferenceCode(
			testGroup.getExternalReferenceCode());
		_testPatchScopeScopeKeyByExternalReferenceCode(testGroup.getGroupId());
		_testPatchScopeScopeKeyByExternalReferenceCode(testGroup.getGroupKey());
	}

	@Test
	public void testPostScopeScopeKey() throws Exception {
		Group testGroup = groupLocalService.fetchGroup(testGroupId);

		_testPostScopeScopeKey(
			testGroup.getExternalReferenceCode(),
			group.getExternalReferenceCode());
		_testPostScopeScopeKey(testGroup.getGroupId(), group.getGroupId());
		_testPostScopeScopeKey(testGroup.getGroupKey(), group.getGroupKey());
	}

	@Test
	public void testPostScopeScopeKeyWithUserGroup() throws Exception {
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
		Group testGroup = groupLocalService.fetchGroup(testGroupId);

		_testPutScopeScopeKeyByExternalReferenceCode(
			testGroup.getExternalReferenceCode());
		_testPutScopeScopeKeyByExternalReferenceCode(testGroup.getGroupId());
		_testPutScopeScopeKeyByExternalReferenceCode(testGroup.getGroupKey());
	}

	private void _testDeleteScopeScopeKeyByExternalReferenceCode(
			Object scopeKey)
		throws Exception {

		ObjectEntry objectEntry = ObjectEntryTestUtil.addObjectEntry(
			getSiteScopedObjectDefinition1(), OBJECT_FIELD_NAME_1,
			RandomTestUtil.randomString());

		HTTPTestUtil.invokeToJSONObject(
			null,
			StringBundler.concat(
				getEndpoint(getSiteScopedObjectDefinition1(), scopeKey),
				"/by-external-reference-code/",
				objectEntry.getExternalReferenceCode()),
			Http.Method.DELETE);

		assertNotFound(
			getObjectEntryJSONObject(
				getSiteScopedObjectDefinition1(),
				String.valueOf(objectEntry.getObjectEntryId())));
	}

	private void _testGetScopeScopeKeyByExternalReferenceCode(Object scopeKey)
		throws Exception {

		String objectFieldValue = RandomTestUtil.randomString();

		ObjectEntry objectEntry = ObjectEntryTestUtil.addObjectEntry(
			getSiteScopedObjectDefinition1(), OBJECT_FIELD_NAME_1,
			objectFieldValue);

		Assert.assertEquals(
			objectFieldValue,
			JSONUtil.getValueAsString(
				HTTPTestUtil.invokeToJSONObject(
					null,
					StringBundler.concat(
						getEndpoint(getSiteScopedObjectDefinition1(), scopeKey),
						"/by-external-reference-code/",
						objectEntry.getExternalReferenceCode()),
					Http.Method.GET),
				"Object/" + OBJECT_FIELD_NAME_1));
	}

	private void _testPatchScopeScopeKeyByExternalReferenceCode(Object scopeKey)
		throws Exception {

		assertPatchCustomObjectEntry(
			jsonObject ->
				getEndpoint(getSiteScopedObjectDefinition1(), scopeKey) +
					"/by-external-reference-code/" +
						jsonObject.getString("externalReferenceCode"),
			getSiteScopedObjectDefinition1(), getSiteScopedObjectDefinition2(),
			scopeKey);
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

	private void _testPutScopeScopeKeyByExternalReferenceCode(Object scopeKey)
		throws Exception {

		assertPutCustomObjectEntry(
			jsonObject ->
				getEndpoint(getSiteScopedObjectDefinition1(), scopeKey) +
					"/by-external-reference-code/" +
						jsonObject.getString("externalReferenceCode"),
			getSiteScopedObjectDefinition1(), getSiteScopedObjectDefinition2(),
			scopeKey);
	}

}