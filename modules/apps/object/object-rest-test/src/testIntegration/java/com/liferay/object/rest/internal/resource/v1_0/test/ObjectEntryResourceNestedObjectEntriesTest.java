/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.rest.internal.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.constants.ObjectRelationshipConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.rest.test.util.ObjectEntryTestUtil;
import com.liferay.object.rest.test.util.ObjectRelationshipTestUtil;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.HTTPTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

/**
 * @author Luis Miguel Barcos
 * @author Magdalena Jedraszak
 * @author Daniel Raposo
 */
@FeatureFlags(featureFlags = @FeatureFlag(value = "LPS-164801"))
@RunWith(Arquillian.class)
public class ObjectEntryResourceNestedObjectEntriesTest
	extends BaseObjectEntryResourceTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	@TestInfo("LPD-53919")
	public void testPatchCustomObjectEntryUnlinkNestedCustomObjectEntries()
		throws Exception {

		// Many to many

		_testPatchPutCustomObjectEntryUnlinkXToManyNestedCustomObjectEntries(
			jsonObject ->
				getObjectDefinition1().getRESTContextPath() + "/" +
					jsonObject.getLong("id"),
			Http.Method.PATCH, getObjectDefinition1(), getObjectDefinition2(),
			ObjectRelationshipConstants.TYPE_MANY_TO_MANY);

		_testPatchPutCustomObjectEntryUnlinkXToManyNestedCustomObjectEntries(
			jsonObject ->
				getSiteScopedObjectDefinition1().getRESTContextPath() + "/" +
					jsonObject.getLong("id"),
			Http.Method.PATCH, getSiteScopedObjectDefinition1(),
			getSiteScopedObjectDefinition2(),
			ObjectRelationshipConstants.TYPE_MANY_TO_MANY);

		// Many to one

		_testPatchPutCustomObjectEntryUnlinkManyToOneNestedCustomObjectEntries(
			jsonObject ->
				getObjectDefinition1().getRESTContextPath() + "/" +
					jsonObject.getLong("id"),
			Http.Method.PATCH, getObjectDefinition1(), getObjectDefinition2());
		_testPatchPutCustomObjectEntryUnlinkManyToOneNestedCustomObjectEntries(
			jsonObject ->
				getSiteScopedObjectDefinition1().getRESTContextPath() + "/" +
					jsonObject.getLong("id"),
			Http.Method.PATCH, getSiteScopedObjectDefinition1(),
			getSiteScopedObjectDefinition2());

		// One to many

		_testPatchPutCustomObjectEntryUnlinkXToManyNestedCustomObjectEntries(
			jsonObject ->
				getObjectDefinition1().getRESTContextPath() + "/" +
					jsonObject.getLong("id"),
			Http.Method.PATCH, getObjectDefinition1(), getObjectDefinition2(),
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);
		_testPatchPutCustomObjectEntryUnlinkXToManyNestedCustomObjectEntries(
			jsonObject ->
				getSiteScopedObjectDefinition1().getRESTContextPath() + "/" +
					jsonObject.getLong("id"),
			Http.Method.PATCH, getSiteScopedObjectDefinition1(),
			getSiteScopedObjectDefinition2(),
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);
	}

	@Test
	@TestInfo("LPD-53919")
	public void testPatchCustomObjectEntryUnlinkNestedCustomObjectEntriesByExternalReferenceCode()
		throws Exception {

		// Many to many

		_testPatchPutCustomObjectEntryUnlinkXToManyNestedCustomObjectEntries(
			jsonObject ->
				getObjectDefinition1().getRESTContextPath() +
					"/by-external-reference-code/" +
						jsonObject.getString("externalReferenceCode"),
			Http.Method.PATCH, getObjectDefinition1(), getObjectDefinition2(),
			ObjectRelationshipConstants.TYPE_MANY_TO_MANY);

		String endpoint = getEndpoint(
			getSiteScopedObjectDefinition1(), testGroupId);

		_testPatchPutCustomObjectEntryUnlinkXToManyNestedCustomObjectEntries(
			jsonObject ->
				endpoint + "/by-external-reference-code/" +
					jsonObject.getString("externalReferenceCode"),
			Http.Method.PATCH, getSiteScopedObjectDefinition1(),
			getSiteScopedObjectDefinition2(),
			ObjectRelationshipConstants.TYPE_MANY_TO_MANY);

		// Many to one

		_testPatchPutCustomObjectEntryUnlinkManyToOneNestedCustomObjectEntries(
			jsonObject ->
				getObjectDefinition1().getRESTContextPath() +
					"/by-external-reference-code/" +
						jsonObject.getString("externalReferenceCode"),
			Http.Method.PATCH, getObjectDefinition1(), getObjectDefinition2());
		_testPatchPutCustomObjectEntryUnlinkManyToOneNestedCustomObjectEntries(
			jsonObject ->
				endpoint + "/by-external-reference-code/" +
					jsonObject.getString("externalReferenceCode"),
			Http.Method.PATCH, getSiteScopedObjectDefinition1(),
			getSiteScopedObjectDefinition2());

		// One to many

		_testPatchPutCustomObjectEntryUnlinkXToManyNestedCustomObjectEntries(
			jsonObject ->
				getObjectDefinition1().getRESTContextPath() +
					"/by-external-reference-code/" +
						jsonObject.getString("externalReferenceCode"),
			Http.Method.PATCH, getObjectDefinition1(), getObjectDefinition2(),
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);
		_testPatchPutCustomObjectEntryUnlinkXToManyNestedCustomObjectEntries(
			jsonObject ->
				endpoint + "/by-external-reference-code/" +
					jsonObject.getString("externalReferenceCode"),
			Http.Method.PATCH, getSiteScopedObjectDefinition1(),
			getSiteScopedObjectDefinition2(),
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);
	}

	@Test
	public void testPostCustomObjectEntryWithEmptyNestedCustomObjectEntriesInOneToManyRelationship()
		throws Exception {

		objectRelationship1 = ObjectRelationshipTestUtil.addObjectRelationship(
			getObjectDefinition1(), getObjectDefinition2(),
			TestPropsValues.getUserId(),
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);

		JSONObject objectEntryJSONObject = JSONUtil.put(
			objectRelationship1.getName(), JSONFactoryUtil.createJSONArray());

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			objectEntryJSONObject.toString(),
			getObjectDefinition1().getRESTContextPath(), Http.Method.POST);

		Assert.assertEquals(
			0,
			jsonObject.getJSONObject(
				"status"
			).get(
				"code"
			));

		JSONArray jsonArray = jsonObject.getJSONArray(
			objectRelationship1.getName());

		Assert.assertEquals(0, jsonArray.length());
	}

	@Test
	public void testPostCustomObjectEntryWithInvalidNestedCustomObjectEntries()
		throws Exception {

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.portal.vulcan.internal.jaxrs.exception.mapper." +
					"WebApplicationExceptionMapper",
				LoggerTestUtil.WARN)) {

			objectRelationship1 =
				ObjectRelationshipTestUtil.addObjectRelationship(
					getObjectDefinition1(), getObjectDefinition2(),
					TestPropsValues.getUserId(),
					ObjectRelationshipConstants.TYPE_MANY_TO_MANY);

			_testPostCustomObjectEntryWithInvalidNestedCustomObjectEntriesInManyToManyRelationship(
				getObjectDefinition1().getRESTContextPath(),
				objectRelationship1);

			objectRelationship1 =
				ObjectRelationshipTestUtil.addObjectRelationship(
					getObjectDefinition1(), getObjectDefinition2(),
					TestPropsValues.getUserId(),
					ObjectRelationshipConstants.TYPE_ONE_TO_MANY);

			_testPostCustomObjectEntryWithInvalidNestedCustomObjectEntriesInManyToOneRelationship(
				getObjectDefinition2().getRESTContextPath(),
				objectRelationship1);

			_testPostCustomObjectEntryWithInvalidNestedCustomObjectEntriesInOneToManyRelationship(
				getObjectDefinition1().getRESTContextPath(),
				objectRelationship1);
		}
	}

	@Test
	@TestInfo("LPD-55622")
	public void testPostCustomObjectEntryWithNestedCustomObjectEntriesInManyToManyRelationship()
		throws Exception {

		// Company scope

		objectRelationship1 = ObjectRelationshipTestUtil.addObjectRelationship(
			getObjectDefinition1(), getObjectDefinition2(),
			TestPropsValues.getUserId(),
			ObjectRelationshipConstants.TYPE_MANY_TO_MANY);

		JSONObject objectEntryJSONObject = JSONUtil.put(
			objectRelationship1.getName(),
			createObjectEntriesJSONArray(
				new String[] {ERC_VALUE_1, ERC_VALUE_2}, OBJECT_FIELD_NAME_2,
				new String[] {
					NEW_OBJECT_FIELD_VALUE_1, NEW_OBJECT_FIELD_VALUE_2
				}));

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			objectEntryJSONObject.toString(),
			getObjectDefinition1().getRESTContextPath(), Http.Method.POST);

		Assert.assertEquals(
			0,
			jsonObject.getJSONObject(
				"status"
			).get(
				"code"
			));

		JSONArray nestedObjectEntriesJSONArray = jsonObject.getJSONArray(
			objectRelationship1.getName());

		Assert.assertEquals(2, nestedObjectEntriesJSONArray.length());

		_assertEquals(nestedObjectEntriesJSONArray);

		String objectEntryId = jsonObject.getString("id");

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			StringBundler.concat(
				getObjectDefinition1().getRESTContextPath(), StringPool.SLASH,
				objectEntryId, "?nestedFields=", objectRelationship1.getName()),
			Http.Method.GET);

		nestedObjectEntriesJSONArray = jsonObject.getJSONArray(
			objectRelationship1.getName());

		Assert.assertEquals(2, nestedObjectEntriesJSONArray.length());

		_assertEquals(nestedObjectEntriesJSONArray);

		// Company-Site scope: with 'scopeKey'

		objectRelationship2 = ObjectRelationshipTestUtil.addObjectRelationship(
			getObjectDefinition1(), getSiteScopedObjectDefinition2(),
			TestPropsValues.getUserId(),
			ObjectRelationshipConstants.TYPE_MANY_TO_MANY);

		objectEntryJSONObject = JSONUtil.put(
			objectRelationship2.getName(),
			_putScopeKey(
				createObjectEntriesJSONArray(
					new String[] {ERC_VALUE_1, ERC_VALUE_2},
					OBJECT_FIELD_NAME_2,
					new String[] {
						NEW_OBJECT_FIELD_VALUE_1, NEW_OBJECT_FIELD_VALUE_2
					}),
				"Guest"));

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			objectEntryJSONObject.toString(),
			getObjectDefinition1().getRESTContextPath(), Http.Method.POST);

		Assert.assertEquals(
			0,
			jsonObject.getJSONObject(
				"status"
			).get(
				"code"
			));

		nestedObjectEntriesJSONArray = jsonObject.getJSONArray(
			objectRelationship2.getName());

		Assert.assertEquals(2, nestedObjectEntriesJSONArray.length());

		_assertEquals(nestedObjectEntriesJSONArray);

		objectEntryId = jsonObject.getString("id");

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			StringBundler.concat(
				getObjectDefinition1().getRESTContextPath(), StringPool.SLASH,
				objectEntryId, "?nestedFields=", objectRelationship2.getName()),
			Http.Method.GET);

		nestedObjectEntriesJSONArray = jsonObject.getJSONArray(
			objectRelationship2.getName());

		Assert.assertEquals(2, nestedObjectEntriesJSONArray.length());

		_assertEquals(nestedObjectEntriesJSONArray);

		// Company-Site scope: with empty 'scopeKey'

		objectEntryJSONObject = JSONUtil.put(
			objectRelationship2.getName(),
			_putScopeKey(
				createObjectEntriesJSONArray(
					new String[] {ERC_VALUE_1, ERC_VALUE_2},
					OBJECT_FIELD_NAME_2,
					new String[] {
						NEW_OBJECT_FIELD_VALUE_1, NEW_OBJECT_FIELD_VALUE_2
					}),
				StringPool.BLANK));

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			objectEntryJSONObject.toString(),
			getObjectDefinition1().getRESTContextPath(), Http.Method.POST);

		JSONAssert.assertEquals(
			JSONUtil.put(
				"status", "BAD_REQUEST"
			).put(
				"title", "Group ID 0 is not valid for scope \"site\""
			).toString(),
			jsonObject.toString(), JSONCompareMode.LENIENT);

		// Company-Site scope: with nonexistent 'scopeKey'

		objectEntryJSONObject = JSONUtil.put(
			objectRelationship2.getName(),
			_putScopeKey(
				createObjectEntriesJSONArray(
					new String[] {ERC_VALUE_1, ERC_VALUE_2},
					OBJECT_FIELD_NAME_2,
					new String[] {
						NEW_OBJECT_FIELD_VALUE_1, NEW_OBJECT_FIELD_VALUE_2
					}),
				RandomTestUtil.randomString()));

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			objectEntryJSONObject.toString(),
			getObjectDefinition1().getRESTContextPath(), Http.Method.POST);

		JSONAssert.assertEquals(
			JSONUtil.put(
				"status", "BAD_REQUEST"
			).put(
				"title", "Group ID 0 is not valid for scope \"site\""
			).toString(),
			jsonObject.toString(), JSONCompareMode.LENIENT);

		// Company-Site scope: without 'scopeKey'

		objectEntryJSONObject = JSONUtil.put(
			objectRelationship2.getName(),
			createObjectEntriesJSONArray(
				new String[] {ERC_VALUE_1, ERC_VALUE_2}, OBJECT_FIELD_NAME_2,
				new String[] {
					NEW_OBJECT_FIELD_VALUE_1, NEW_OBJECT_FIELD_VALUE_2
				}));

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			objectEntryJSONObject.toString(),
			getObjectDefinition1().getRESTContextPath(), Http.Method.POST);

		JSONAssert.assertEquals(
			JSONUtil.put(
				"status", "BAD_REQUEST"
			).put(
				"title",
				StringBundler.concat(
					"No scope key was provided for the \"",
					getSiteScopedObjectDefinition2().getName(), "\" entry.")
			).toString(),
			jsonObject.toString(), JSONCompareMode.LENIENT);

		// Site scope

		objectRelationship3 = ObjectRelationshipTestUtil.addObjectRelationship(
			getSiteScopedObjectDefinition1(), getSiteScopedObjectDefinition2(),
			TestPropsValues.getUserId(),
			ObjectRelationshipConstants.TYPE_MANY_TO_MANY);

		objectEntryJSONObject = JSONUtil.put(
			objectRelationship3.getName(),
			createObjectEntriesJSONArray(
				new String[] {ERC_VALUE_1, ERC_VALUE_2}, OBJECT_FIELD_NAME_2,
				new String[] {
					NEW_OBJECT_FIELD_VALUE_1, NEW_OBJECT_FIELD_VALUE_2
				}));

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			objectEntryJSONObject.toString(),
			getEndpoint(getSiteScopedObjectDefinition1(), testGroupId),
			Http.Method.POST);

		Assert.assertEquals(
			0,
			jsonObject.getJSONObject(
				"status"
			).get(
				"code"
			));

		nestedObjectEntriesJSONArray = jsonObject.getJSONArray(
			objectRelationship3.getName());

		Assert.assertEquals(2, nestedObjectEntriesJSONArray.length());

		_assertEquals(nestedObjectEntriesJSONArray);

		objectEntryId = jsonObject.getString("id");

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			StringBundler.concat(
				getSiteScopedObjectDefinition1().getRESTContextPath(),
				StringPool.SLASH, objectEntryId, "?nestedFields=",
				objectRelationship3.getName()),
			Http.Method.GET);

		nestedObjectEntriesJSONArray = jsonObject.getJSONArray(
			objectRelationship3.getName());

		Assert.assertEquals(2, nestedObjectEntriesJSONArray.length());

		_assertEquals(nestedObjectEntriesJSONArray);
	}

	@Test
	public void testPostCustomObjectEntryWithNestedCustomObjectEntriesInManyToOneRelationship()
		throws Exception {

		// Company scope

		objectRelationship1 = ObjectRelationshipTestUtil.addObjectRelationship(
			getObjectDefinition1(), getObjectDefinition2(),
			TestPropsValues.getUserId(),
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);

		JSONObject objectEntryJSONObject = JSONUtil.put(
			objectRelationship1.getName(),
			JSONFactoryUtil.createJSONObject(
				JSONUtil.put(
					OBJECT_FIELD_NAME_1, NEW_OBJECT_FIELD_VALUE_1
				).put(
					"externalReferenceCode", ERC_VALUE_1
				).toString()));

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			objectEntryJSONObject.toString(),
			getObjectDefinition2().getRESTContextPath(), Http.Method.POST);

		Assert.assertEquals(
			0,
			jsonObject.getJSONObject(
				"status"
			).get(
				"code"
			));

		_assertObjectEntryField(
			jsonObject.getJSONObject(
				StringBundler.concat(
					"r_", objectRelationship1.getName(), "_",
					StringUtil.removeLast(
						getObjectDefinition1().getPKObjectFieldName(), "Id"))),
			OBJECT_FIELD_NAME_1, NEW_OBJECT_FIELD_VALUE_1);

		String objectEntryId = jsonObject.getString("id");

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			StringBundler.concat(
				getObjectDefinition2().getRESTContextPath(), StringPool.SLASH,
				objectEntryId, "?nestedFields=",
				StringBundler.concat(
					"r_", objectRelationship1.getName(), "_",
					StringUtil.removeLast(
						getObjectDefinition1().getPKObjectFieldName(), "Id"))),
			Http.Method.GET);

		_assertObjectEntryField(
			jsonObject.getJSONObject(
				StringBundler.concat(
					"r_", objectRelationship1.getName(), "_",
					StringUtil.removeLast(
						getObjectDefinition1().getPKObjectFieldName(), "Id"))),
			OBJECT_FIELD_NAME_1, NEW_OBJECT_FIELD_VALUE_1);

		// Site scope

		objectRelationship6 = ObjectRelationshipTestUtil.addObjectRelationship(
			getSiteScopedObjectDefinition1(), getSiteScopedObjectDefinition2(),
			TestPropsValues.getUserId(),
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);

		objectEntryJSONObject = JSONUtil.put(
			objectRelationship6.getName(),
			JSONFactoryUtil.createJSONObject(
				JSONUtil.put(
					OBJECT_FIELD_NAME_1, NEW_OBJECT_FIELD_VALUE_1
				).put(
					"externalReferenceCode", ERC_VALUE_1
				).toString()));

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			objectEntryJSONObject.toString(),
			getEndpoint(getSiteScopedObjectDefinition2(), testGroupId),
			Http.Method.POST);

		Assert.assertEquals(
			0,
			jsonObject.getJSONObject(
				"status"
			).get(
				"code"
			));

		_assertObjectEntryField(
			jsonObject.getJSONObject(
				StringBundler.concat(
					"r_", objectRelationship6.getName(), "_",
					StringUtil.removeLast(
						getSiteScopedObjectDefinition1().getPKObjectFieldName(),
						"Id"))),
			OBJECT_FIELD_NAME_1, NEW_OBJECT_FIELD_VALUE_1);

		objectEntryId = jsonObject.getString("id");

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			StringBundler.concat(
				getSiteScopedObjectDefinition2().getRESTContextPath(),
				StringPool.SLASH, objectEntryId, "?nestedFields=",
				StringBundler.concat(
					"r_", objectRelationship6.getName(), "_",
					StringUtil.removeLast(
						getSiteScopedObjectDefinition1().getPKObjectFieldName(),
						"Id"))),
			Http.Method.GET);

		_assertObjectEntryField(
			jsonObject.getJSONObject(
				StringBundler.concat(
					"r_", objectRelationship6.getName(), "_",
					StringUtil.removeLast(
						getSiteScopedObjectDefinition1().getPKObjectFieldName(),
						"Id"))),
			OBJECT_FIELD_NAME_1, NEW_OBJECT_FIELD_VALUE_1);
	}

	@Test
	public void testPostCustomObjectEntryWithNestedCustomObjectEntriesInOneToManyRelationship()
		throws Exception {

		// Company scope

		objectRelationship1 = ObjectRelationshipTestUtil.addObjectRelationship(
			getObjectDefinition1(), getObjectDefinition2(),
			TestPropsValues.getUserId(),
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);

		JSONObject objectEntryJSONObject = JSONUtil.put(
			objectRelationship1.getName(),
			createObjectEntriesJSONArray(
				new String[] {ERC_VALUE_1, ERC_VALUE_2}, OBJECT_FIELD_NAME_2,
				new String[] {
					NEW_OBJECT_FIELD_VALUE_1, NEW_OBJECT_FIELD_VALUE_2
				}));

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			objectEntryJSONObject.toString(),
			getObjectDefinition1().getRESTContextPath(), Http.Method.POST);

		Assert.assertEquals(
			0,
			jsonObject.getJSONObject(
				"status"
			).get(
				"code"
			));

		JSONArray nestedObjectEntriesJSONArray = jsonObject.getJSONArray(
			objectRelationship1.getName());

		Assert.assertEquals(2, nestedObjectEntriesJSONArray.length());

		_assertEquals(nestedObjectEntriesJSONArray);

		String objectEntryId = jsonObject.getString("id");

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			StringBundler.concat(
				getObjectDefinition1().getRESTContextPath(), StringPool.SLASH,
				objectEntryId, "?nestedFields=", objectRelationship1.getName()),
			Http.Method.GET);

		nestedObjectEntriesJSONArray = jsonObject.getJSONArray(
			objectRelationship1.getName());

		Assert.assertEquals(2, nestedObjectEntriesJSONArray.length());

		_assertEquals(nestedObjectEntriesJSONArray);

		// Site scope

		objectRelationship6 = ObjectRelationshipTestUtil.addObjectRelationship(
			getSiteScopedObjectDefinition1(), getSiteScopedObjectDefinition2(),
			TestPropsValues.getUserId(),
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);

		objectEntryJSONObject = JSONUtil.put(
			objectRelationship6.getName(),
			createObjectEntriesJSONArray(
				new String[] {ERC_VALUE_1, ERC_VALUE_2}, OBJECT_FIELD_NAME_2,
				new String[] {
					NEW_OBJECT_FIELD_VALUE_1, NEW_OBJECT_FIELD_VALUE_2
				}));

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			objectEntryJSONObject.toString(),
			getEndpoint(getSiteScopedObjectDefinition1(), testGroupId),
			Http.Method.POST);

		Assert.assertEquals(
			0,
			jsonObject.getJSONObject(
				"status"
			).get(
				"code"
			));

		nestedObjectEntriesJSONArray = jsonObject.getJSONArray(
			objectRelationship6.getName());

		Assert.assertEquals(2, nestedObjectEntriesJSONArray.length());

		_assertEquals(nestedObjectEntriesJSONArray);

		objectEntryId = jsonObject.getString("id");

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			StringBundler.concat(
				getSiteScopedObjectDefinition1().getRESTContextPath(),
				StringPool.SLASH, objectEntryId, "?nestedFields=",
				objectRelationship6.getName()),
			Http.Method.GET);

		nestedObjectEntriesJSONArray = jsonObject.getJSONArray(
			objectRelationship6.getName());

		Assert.assertEquals(2, nestedObjectEntriesJSONArray.length());

		_assertEquals(nestedObjectEntriesJSONArray);
	}

	@Test
	@TestInfo("LPD-53919")
	public void testPutCustomObjectEntryUnlinkNestedCustomObjectEntries()
		throws Exception {

		// Many to many

		_testPatchPutCustomObjectEntryUnlinkXToManyNestedCustomObjectEntries(
			jsonObject ->
				getObjectDefinition1().getRESTContextPath() + "/" +
					jsonObject.getLong("id"),
			Http.Method.PUT, getObjectDefinition1(), getObjectDefinition2(),
			ObjectRelationshipConstants.TYPE_MANY_TO_MANY);

		_testPatchPutCustomObjectEntryUnlinkXToManyNestedCustomObjectEntries(
			jsonObject ->
				getSiteScopedObjectDefinition1().getRESTContextPath() + "/" +
					jsonObject.getLong("id"),
			Http.Method.PUT, getSiteScopedObjectDefinition1(),
			getSiteScopedObjectDefinition2(),
			ObjectRelationshipConstants.TYPE_MANY_TO_MANY);

		// Many to one

		_testPatchPutCustomObjectEntryUnlinkManyToOneNestedCustomObjectEntries(
			jsonObject ->
				getObjectDefinition1().getRESTContextPath() + "/" +
					jsonObject.getLong("id"),
			Http.Method.PUT, getObjectDefinition1(), getObjectDefinition2());
		_testPatchPutCustomObjectEntryUnlinkManyToOneNestedCustomObjectEntries(
			jsonObject ->
				getSiteScopedObjectDefinition1().getRESTContextPath() + "/" +
					jsonObject.getLong("id"),
			Http.Method.PUT, getSiteScopedObjectDefinition1(),
			getSiteScopedObjectDefinition2());

		// One to many

		_testPatchPutCustomObjectEntryUnlinkXToManyNestedCustomObjectEntries(
			jsonObject ->
				getObjectDefinition1().getRESTContextPath() + "/" +
					jsonObject.getLong("id"),
			Http.Method.PUT, getObjectDefinition1(), getObjectDefinition2(),
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);
		_testPatchPutCustomObjectEntryUnlinkXToManyNestedCustomObjectEntries(
			jsonObject ->
				getSiteScopedObjectDefinition1().getRESTContextPath() + "/" +
					jsonObject.getLong("id"),
			Http.Method.PUT, getSiteScopedObjectDefinition1(),
			getSiteScopedObjectDefinition2(),
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);
	}

	@Test
	@TestInfo("LPD-53919")
	public void testPutCustomObjectEntryUnlinkNestedCustomObjectEntriesByExternalReferenceCode()
		throws Exception {

		// Many to many

		_testPatchPutCustomObjectEntryUnlinkXToManyNestedCustomObjectEntries(
			jsonObject ->
				getObjectDefinition1().getRESTContextPath() +
					"/by-external-reference-code/" +
						jsonObject.getString("externalReferenceCode"),
			Http.Method.PUT, getObjectDefinition1(), getObjectDefinition2(),
			ObjectRelationshipConstants.TYPE_MANY_TO_MANY);

		String endpoint = getEndpoint(
			getSiteScopedObjectDefinition1(), testGroupId);

		_testPatchPutCustomObjectEntryUnlinkXToManyNestedCustomObjectEntries(
			jsonObject ->
				endpoint + "/by-external-reference-code/" +
					jsonObject.getString("externalReferenceCode"),
			Http.Method.PUT, getSiteScopedObjectDefinition1(),
			getSiteScopedObjectDefinition2(),
			ObjectRelationshipConstants.TYPE_MANY_TO_MANY);

		// Many to one

		_testPatchPutCustomObjectEntryUnlinkManyToOneNestedCustomObjectEntries(
			jsonObject ->
				getObjectDefinition1().getRESTContextPath() +
					"/by-external-reference-code/" +
						jsonObject.getString("externalReferenceCode"),
			Http.Method.PUT, getObjectDefinition1(), getObjectDefinition2());
		_testPatchPutCustomObjectEntryUnlinkManyToOneNestedCustomObjectEntries(
			jsonObject ->
				endpoint + "/by-external-reference-code/" +
					jsonObject.getString("externalReferenceCode"),
			Http.Method.PUT, getSiteScopedObjectDefinition1(),
			getSiteScopedObjectDefinition2());

		// One to many

		_testPatchPutCustomObjectEntryUnlinkXToManyNestedCustomObjectEntries(
			jsonObject ->
				getObjectDefinition1().getRESTContextPath() +
					"/by-external-reference-code/" +
						jsonObject.getString("externalReferenceCode"),
			Http.Method.PUT, getObjectDefinition1(), getObjectDefinition2(),
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);
		_testPatchPutCustomObjectEntryUnlinkXToManyNestedCustomObjectEntries(
			jsonObject ->
				endpoint + "/by-external-reference-code/" +
					jsonObject.getString("externalReferenceCode"),
			Http.Method.PUT, getSiteScopedObjectDefinition1(),
			getSiteScopedObjectDefinition2(),
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);
	}

	@Test
	public void testPutCustomObjectEntryWithNestedCustomObjectEntriesByExternalReferenceCode()
		throws Exception {

		// Many to many

		objectRelationship1 = ObjectRelationshipTestUtil.addObjectRelationship(
			getObjectDefinition1(), getObjectDefinition2(),
			TestPropsValues.getUserId(),
			ObjectRelationshipConstants.TYPE_MANY_TO_MANY);

		_testPutCustomObjectEntryWithNestedCustomObjectEntriesByExternalReferenceCode(
			false);

		objectRelationshipLocalService.deleteObjectRelationship(
			objectRelationship1);

		// Many to one

		objectRelationship1 = ObjectRelationshipTestUtil.addObjectRelationship(
			getObjectDefinition2(), getObjectDefinition1(),
			TestPropsValues.getUserId(),
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);

		_testPutCustomObjectEntryWithNestedCustomObjectEntriesByExternalReferenceCode(
			true);

		objectRelationshipLocalService.deleteObjectRelationship(
			objectRelationship1);

		// One to many

		objectRelationship1 = ObjectRelationshipTestUtil.addObjectRelationship(
			getObjectDefinition1(), getObjectDefinition2(),
			TestPropsValues.getUserId(),
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);

		_testPutCustomObjectEntryWithNestedCustomObjectEntriesByExternalReferenceCode(
			false);
	}

	@Test
	public void testPutCustomObjectEntryWithNestedCustomObjectEntriesInManyToManyRelationship()
		throws Exception {

		objectEntry1 = ObjectEntryTestUtil.addObjectEntry(
			getObjectDefinition1(), OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1);

		objectRelationship1 = ObjectRelationshipTestUtil.addObjectRelationship(
			getObjectDefinition1(), getObjectDefinition2(),
			TestPropsValues.getUserId(),
			ObjectRelationshipConstants.TYPE_MANY_TO_MANY);

		JSONObject objectEntryJSONObject = JSONUtil.put(
			objectRelationship1.getName(),
			createObjectEntriesJSONArray(
				new String[] {ERC_VALUE_1, ERC_VALUE_2}, OBJECT_FIELD_NAME_2,
				new String[] {
					RandomTestUtil.randomString(), RandomTestUtil.randomString()
				}));

		HTTPTestUtil.invokeToJSONObject(
			objectEntryJSONObject.toString(),
			getObjectDefinition1().getRESTContextPath(), Http.Method.POST);

		JSONObject newObjectEntryJSONObject = JSONUtil.put(
			objectRelationship1.getName(),
			createObjectEntriesJSONArray(
				new String[] {ERC_VALUE_1}, OBJECT_FIELD_NAME_2,
				new String[] {NEW_OBJECT_FIELD_VALUE_1}));

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			newObjectEntryJSONObject.toString(),
			StringBundler.concat(
				getObjectDefinition1().getRESTContextPath(), StringPool.SLASH,
				objectEntry1.getPrimaryKey()),
			Http.Method.PUT);

		Assert.assertEquals(
			0,
			jsonObject.getJSONObject(
				"status"
			).get(
				"code"
			));

		JSONArray nestedObjectEntriesJSONArray = jsonObject.getJSONArray(
			objectRelationship1.getName());

		Assert.assertEquals(1, nestedObjectEntriesJSONArray.length());

		_assertObjectEntryField(
			nestedObjectEntriesJSONArray.getJSONObject(0), OBJECT_FIELD_NAME_2,
			NEW_OBJECT_FIELD_VALUE_1);

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			StringBundler.concat(
				getObjectDefinition1().getRESTContextPath(), StringPool.SLASH,
				objectEntry1.getPrimaryKey(), "?nestedFields=",
				objectRelationship1.getName()),
			Http.Method.GET);

		nestedObjectEntriesJSONArray = jsonObject.getJSONArray(
			objectRelationship1.getName());

		Assert.assertEquals(1, nestedObjectEntriesJSONArray.length());

		_assertObjectEntryField(
			nestedObjectEntriesJSONArray.getJSONObject(0), OBJECT_FIELD_NAME_2,
			NEW_OBJECT_FIELD_VALUE_1);
	}

	@Test
	public void testPutCustomObjectEntryWithNestedCustomObjectEntriesInManyToOneRelationship()
		throws Exception {

		objectRelationship1 = ObjectRelationshipTestUtil.addObjectRelationship(
			getObjectDefinition1(), getObjectDefinition2(),
			TestPropsValues.getUserId(),
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);

		JSONObject objectEntryJSONObject = JSONUtil.put(
			objectRelationship1.getName(),
			JSONFactoryUtil.createJSONObject(
				JSONUtil.put(
					OBJECT_FIELD_NAME_1, RandomTestUtil.randomString()
				).put(
					"externalReferenceCode", ERC_VALUE_1
				).toString()));

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			objectEntryJSONObject.toString(),
			getObjectDefinition2().getRESTContextPath(), Http.Method.POST);

		String objectEntryId = jsonObject.getString("id");

		JSONObject newObjectEntryJSONObject = JSONUtil.put(
			objectRelationship1.getName(),
			JSONFactoryUtil.createJSONObject(
				JSONUtil.put(
					OBJECT_FIELD_NAME_1, NEW_OBJECT_FIELD_VALUE_1
				).put(
					"externalReferenceCode", ERC_VALUE_1
				).toString()));

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			newObjectEntryJSONObject.toString(),
			StringBundler.concat(
				getObjectDefinition2().getRESTContextPath(), StringPool.SLASH,
				objectEntryId),
			Http.Method.PUT);

		Assert.assertEquals(
			0,
			jsonObject.getJSONObject(
				"status"
			).get(
				"code"
			));

		_assertObjectEntryField(
			jsonObject.getJSONObject(
				StringBundler.concat(
					"r_", objectRelationship1.getName(), "_",
					StringUtil.removeLast(
						getObjectDefinition1().getPKObjectFieldName(), "Id"))),
			OBJECT_FIELD_NAME_1, NEW_OBJECT_FIELD_VALUE_1);

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			StringBundler.concat(
				getObjectDefinition2().getRESTContextPath(), StringPool.SLASH,
				objectEntryId, "?nestedFields=",
				StringBundler.concat(
					"r_", objectRelationship1.getName(), "_",
					StringUtil.removeLast(
						getObjectDefinition1().getPKObjectFieldName(), "Id"))),
			Http.Method.GET);

		_assertObjectEntryField(
			jsonObject.getJSONObject(
				StringBundler.concat(
					"r_", objectRelationship1.getName(), "_",
					StringUtil.removeLast(
						getObjectDefinition1().getPKObjectFieldName(), "Id"))),
			OBJECT_FIELD_NAME_1, NEW_OBJECT_FIELD_VALUE_1);
	}

	@Test
	public void testPutCustomObjectEntryWithNestedCustomObjectEntriesInOneToManyRelationship()
		throws Exception {

		objectEntry1 = ObjectEntryTestUtil.addObjectEntry(
			getObjectDefinition1(), OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1);

		objectRelationship1 = ObjectRelationshipTestUtil.addObjectRelationship(
			getObjectDefinition1(), getObjectDefinition2(),
			TestPropsValues.getUserId(),
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);

		JSONObject objectEntryJSONObject = JSONUtil.put(
			objectRelationship1.getName(),
			createObjectEntriesJSONArray(
				new String[] {ERC_VALUE_1, ERC_VALUE_2}, OBJECT_FIELD_NAME_2,
				new String[] {
					RandomTestUtil.randomString(), RandomTestUtil.randomString()
				}));

		HTTPTestUtil.invokeToJSONObject(
			objectEntryJSONObject.toString(),
			getObjectDefinition1().getRESTContextPath(), Http.Method.POST);

		JSONObject newObjectEntryJSONObject = JSONUtil.put(
			objectRelationship1.getName(),
			createObjectEntriesJSONArray(
				new String[] {ERC_VALUE_1}, OBJECT_FIELD_NAME_2,
				new String[] {NEW_OBJECT_FIELD_VALUE_1}));

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			newObjectEntryJSONObject.toString(),
			StringBundler.concat(
				getObjectDefinition1().getRESTContextPath(), StringPool.SLASH,
				objectEntry1.getPrimaryKey()),
			Http.Method.PUT);

		Assert.assertEquals(
			0,
			jsonObject.getJSONObject(
				"status"
			).get(
				"code"
			));

		JSONArray nestedObjectEntriesJSONArray = jsonObject.getJSONArray(
			objectRelationship1.getName());

		Assert.assertEquals(1, nestedObjectEntriesJSONArray.length());

		_assertObjectEntryField(
			nestedObjectEntriesJSONArray.getJSONObject(0), OBJECT_FIELD_NAME_2,
			NEW_OBJECT_FIELD_VALUE_1);

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			StringBundler.concat(
				getObjectDefinition1().getRESTContextPath(), StringPool.SLASH,
				objectEntry1.getPrimaryKey(), "?nestedFields=",
				objectRelationship1.getName()),
			Http.Method.GET);

		nestedObjectEntriesJSONArray = jsonObject.getJSONArray(
			objectRelationship1.getName());

		Assert.assertEquals(1, nestedObjectEntriesJSONArray.length());

		_assertObjectEntryField(
			nestedObjectEntriesJSONArray.getJSONObject(0), OBJECT_FIELD_NAME_2,
			NEW_OBJECT_FIELD_VALUE_1);
	}

	@Test
	public void testPutSiteScopedCustomObjectEntryWithNestedCustomObjectEntries()
		throws Exception {

		objectRelationship1 = ObjectRelationshipTestUtil.addObjectRelationship(
			getSiteScopedObjectDefinition1(), getSiteScopedObjectDefinition2(),
			TestPropsValues.getUserId(),
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);

		JSONObject postJSONObject = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				objectRelationship1.getName(),
				createObjectEntriesJSONArray(
					new String[] {ERC_VALUE_1, ERC_VALUE_2},
					OBJECT_FIELD_NAME_1,
					new String[] {
						NEW_OBJECT_FIELD_VALUE_1, NEW_OBJECT_FIELD_VALUE_2
					})
			).toString(),
			getEndpoint(getSiteScopedObjectDefinition1(), testGroupId),
			Http.Method.POST);

		JSONObject putJSONObject = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1
			).put(
				objectRelationship1.getName(),
				createObjectEntriesJSONArray(
					new String[] {ERC_VALUE_3}, OBJECT_FIELD_NAME_2,
					new String[] {NEW_OBJECT_FIELD_VALUE_2})
			).toString(),
			getSiteScopedObjectDefinition1().getRESTContextPath() + "/" +
				postJSONObject.getLong("id"),
			Http.Method.PUT);

		Assert.assertEquals(
			ERC_VALUE_3,
			putJSONObject.getJSONArray(
				objectRelationship1.getName()
			).getJSONObject(
				0
			).getString(
				"externalReferenceCode"
			));
	}

	private void _assertEquals(JSONArray nestedObjectEntriesJSONArray)
		throws Exception {

		JSONAssert.assertEquals(
			JSONUtil.putAll(
				JSONUtil.put(
					OBJECT_FIELD_NAME_2, NEW_OBJECT_FIELD_VALUE_1
				).put(
					"externalReferenceCode", ERC_VALUE_1
				),
				JSONUtil.put(
					OBJECT_FIELD_NAME_2, NEW_OBJECT_FIELD_VALUE_2
				).put(
					"externalReferenceCode", ERC_VALUE_2
				)
			).toString(),
			nestedObjectEntriesJSONArray.toString(), JSONCompareMode.LENIENT);
	}

	private void _assertObjectEntryField(
		JSONObject objectEntryJSONObject, String objectFieldName,
		String objectFieldValue) {

		int objectEntryId = objectEntryJSONObject.getInt("id");

		ObjectEntry objectEntry = objectEntryLocalService.fetchObjectEntry(
			objectEntryId);

		Assert.assertEquals(
			"_assertObjectEntryField",
			MapUtil.getString(objectEntry.getValues(), objectFieldName),
			objectFieldValue);
	}

	private JSONArray _putScopeKey(JSONArray jsonArray, String scopeKey) {
		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);

			jsonObject.put("scopeKey", scopeKey);
		}

		return jsonArray;
	}

	private void
			_testPatchPutCustomObjectEntryUnlinkManyToOneNestedCustomObjectEntries(
				UnsafeFunction<JSONObject, String, Exception>
					endpointUnsafeFunction,
				Http.Method method, ObjectDefinition objectDefinition,
				ObjectDefinition relatedObjectDefinition)
		throws Exception {

		ObjectRelationship emptyObjectEntryObjectRelationship =
			ObjectRelationshipTestUtil.addObjectRelationship(
				relatedObjectDefinition, objectDefinition,
				TestPropsValues.getUserId(),
				ObjectRelationshipConstants.TYPE_ONE_TO_MANY);
		ObjectRelationship nullObjectEntryObjectRelationship =
			ObjectRelationshipTestUtil.addObjectRelationship(
				relatedObjectDefinition, objectDefinition,
				TestPropsValues.getUserId(),
				ObjectRelationshipConstants.TYPE_ONE_TO_MANY);
		ObjectRelationship zeroIdObjectRelationship =
			ObjectRelationshipTestUtil.addObjectRelationship(
				relatedObjectDefinition, objectDefinition,
				TestPropsValues.getUserId(),
				ObjectRelationshipConstants.TYPE_ONE_TO_MANY);
		ObjectRelationship nullIdObjectRelationship =
			ObjectRelationshipTestUtil.addObjectRelationship(
				relatedObjectDefinition, objectDefinition,
				TestPropsValues.getUserId(),
				ObjectRelationshipConstants.TYPE_ONE_TO_MANY);
		ObjectRelationship blankERCObjectRelationship =
			ObjectRelationshipTestUtil.addObjectRelationship(
				relatedObjectDefinition, objectDefinition,
				TestPropsValues.getUserId(),
				ObjectRelationshipConstants.TYPE_ONE_TO_MANY);
		ObjectRelationship nullERCObjectRelationship =
			ObjectRelationshipTestUtil.addObjectRelationship(
				relatedObjectDefinition, objectDefinition,
				TestPropsValues.getUserId(),
				ObjectRelationshipConstants.TYPE_ONE_TO_MANY);

		try {
			JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
				JSONUtil.put(
					emptyObjectEntryObjectRelationship.getName(),
					JSONFactoryUtil.createJSONObject(
						JSONUtil.put(
							OBJECT_FIELD_NAME_2, RandomTestUtil.randomString()
						).toString())
				).put(
					nullObjectEntryObjectRelationship.getName(),
					JSONFactoryUtil.createJSONObject(
						JSONUtil.put(
							OBJECT_FIELD_NAME_2, RandomTestUtil.randomString()
						).toString())
				).put(
					zeroIdObjectRelationship.getName(),
					JSONFactoryUtil.createJSONObject(
						JSONUtil.put(
							OBJECT_FIELD_NAME_2, RandomTestUtil.randomString()
						).toString())
				).put(
					nullIdObjectRelationship.getName(),
					JSONFactoryUtil.createJSONObject(
						JSONUtil.put(
							OBJECT_FIELD_NAME_2, RandomTestUtil.randomString()
						).toString())
				).put(
					blankERCObjectRelationship.getName(),
					JSONFactoryUtil.createJSONObject(
						JSONUtil.put(
							OBJECT_FIELD_NAME_2, RandomTestUtil.randomString()
						).toString())
				).put(
					nullERCObjectRelationship.getName(),
					JSONFactoryUtil.createJSONObject(
						JSONUtil.put(
							OBJECT_FIELD_NAME_2, RandomTestUtil.randomString()
						).toString())
				).toString(),
				getEndpoint(objectDefinition, testGroupId), Http.Method.POST);

			jsonObject = HTTPTestUtil.invokeToJSONObject(
				JSONUtil.put(
					emptyObjectEntryObjectRelationship.getName(),
					JSONFactoryUtil.createJSONObject()
				).put(
					nullObjectEntryObjectRelationship.getName(),
					org.json.JSONObject.NULL
				).put(
					StringBundler.concat(
						"r_", zeroIdObjectRelationship.getName(), "_",
						relatedObjectDefinition.getPKObjectFieldName()),
					0
				).put(
					StringBundler.concat(
						"r_", nullIdObjectRelationship.getName(), "_",
						relatedObjectDefinition.getPKObjectFieldName()),
					org.json.JSONObject.NULL
				).put(
					StringBundler.concat(
						"r_", blankERCObjectRelationship.getName(), "_",
						StringUtil.replaceLast(
							relatedObjectDefinition.getPKObjectFieldName(),
							"Id", "ERC")),
					""
				).put(
					StringBundler.concat(
						"r_", nullERCObjectRelationship.getName(), "_",
						StringUtil.replaceLast(
							relatedObjectDefinition.getPKObjectFieldName(),
							"Id", "ERC")),
					org.json.JSONObject.NULL
				).toString(),
				endpointUnsafeFunction.apply(jsonObject), method);

			Assert.assertEquals(
				0,
				jsonObject.getJSONObject(
					"status"
				).get(
					"code"
				));
			Assert.assertNull(
				jsonObject.getJSONObject(
					emptyObjectEntryObjectRelationship.getName()));
			Assert.assertNull(
				jsonObject.getJSONObject(
					nullObjectEntryObjectRelationship.getName()));
			Assert.assertNull(
				jsonObject.getJSONObject(zeroIdObjectRelationship.getName()));
			Assert.assertNull(
				jsonObject.getJSONObject(nullIdObjectRelationship.getName()));
			Assert.assertNull(
				jsonObject.getJSONObject(blankERCObjectRelationship.getName()));
			Assert.assertNull(
				jsonObject.getJSONObject(nullERCObjectRelationship.getName()));

			JSONAssert.assertEquals(
				JSONUtil.put(
					StringBundler.concat(
						"r_", emptyObjectEntryObjectRelationship.getName(), "_",
						relatedObjectDefinition.getPKObjectFieldName()),
					0
				).put(
					StringBundler.concat(
						"r_", emptyObjectEntryObjectRelationship.getName(), "_",
						StringUtil.replaceLast(
							relatedObjectDefinition.getPKObjectFieldName(),
							"Id", "ERC")),
					""
				).put(
					StringBundler.concat(
						"r_", nullObjectEntryObjectRelationship.getName(), "_",
						relatedObjectDefinition.getPKObjectFieldName()),
					0
				).put(
					StringBundler.concat(
						"r_", nullObjectEntryObjectRelationship.getName(), "_",
						StringUtil.replaceLast(
							relatedObjectDefinition.getPKObjectFieldName(),
							"Id", "ERC")),
					""
				).put(
					StringBundler.concat(
						"r_", zeroIdObjectRelationship.getName(), "_",
						relatedObjectDefinition.getPKObjectFieldName()),
					0
				).put(
					StringBundler.concat(
						"r_", zeroIdObjectRelationship.getName(), "_",
						StringUtil.replaceLast(
							relatedObjectDefinition.getPKObjectFieldName(),
							"Id", "ERC")),
					""
				).put(
					StringBundler.concat(
						"r_", nullIdObjectRelationship.getName(), "_",
						relatedObjectDefinition.getPKObjectFieldName()),
					0
				).put(
					StringBundler.concat(
						"r_", nullIdObjectRelationship.getName(), "_",
						StringUtil.replaceLast(
							relatedObjectDefinition.getPKObjectFieldName(),
							"Id", "ERC")),
					""
				).put(
					StringBundler.concat(
						"r_", blankERCObjectRelationship.getName(), "_",
						relatedObjectDefinition.getPKObjectFieldName()),
					0
				).put(
					StringBundler.concat(
						"r_", blankERCObjectRelationship.getName(), "_",
						StringUtil.replaceLast(
							relatedObjectDefinition.getPKObjectFieldName(),
							"Id", "ERC")),
					""
				).put(
					StringBundler.concat(
						"r_", nullERCObjectRelationship.getName(), "_",
						relatedObjectDefinition.getPKObjectFieldName()),
					0
				).put(
					StringBundler.concat(
						"r_", nullERCObjectRelationship.getName(), "_",
						StringUtil.replaceLast(
							relatedObjectDefinition.getPKObjectFieldName(),
							"Id", "ERC")),
					""
				).toString(),
				jsonObject.toString(), JSONCompareMode.LENIENT);
		}
		finally {
			objectRelationshipLocalService.deleteObjectRelationship(
				emptyObjectEntryObjectRelationship);
			objectRelationshipLocalService.deleteObjectRelationship(
				nullObjectEntryObjectRelationship);
			objectRelationshipLocalService.deleteObjectRelationship(
				zeroIdObjectRelationship);
			objectRelationshipLocalService.deleteObjectRelationship(
				nullIdObjectRelationship);
			objectRelationshipLocalService.deleteObjectRelationship(
				blankERCObjectRelationship);
			objectRelationshipLocalService.deleteObjectRelationship(
				nullERCObjectRelationship);
		}
	}

	private void
			_testPatchPutCustomObjectEntryUnlinkXToManyNestedCustomObjectEntries(
				UnsafeFunction<JSONObject, String, Exception>
					endpointUnsafeFunction,
				Http.Method method, ObjectDefinition objectDefinition,
				ObjectDefinition relatedObjectDefinition, String type)
		throws Exception {

		ObjectRelationship emptyObjectEntriesObjectRelationship =
			ObjectRelationshipTestUtil.addObjectRelationship(
				objectDefinition, relatedObjectDefinition,
				TestPropsValues.getUserId(), type);
		ObjectRelationship nullObjectEntriesObjectRelationship =
			ObjectRelationshipTestUtil.addObjectRelationship(
				objectDefinition, relatedObjectDefinition,
				TestPropsValues.getUserId(), type);

		try {
			JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
				JSONUtil.put(
					emptyObjectEntriesObjectRelationship.getName(),
					createObjectEntriesJSONArray(
						new String[] {ERC_VALUE_1, ERC_VALUE_2},
						OBJECT_FIELD_NAME_2,
						new String[] {
							RandomTestUtil.randomString(),
							RandomTestUtil.randomString()
						})
				).put(
					nullObjectEntriesObjectRelationship.getName(),
					createObjectEntriesJSONArray(
						new String[] {ERC_VALUE_1, ERC_VALUE_2},
						OBJECT_FIELD_NAME_2,
						new String[] {
							RandomTestUtil.randomString(),
							RandomTestUtil.randomString()
						})
				).toString(),
				getEndpoint(objectDefinition, testGroupId), Http.Method.POST);

			jsonObject = HTTPTestUtil.invokeToJSONObject(
				JSONUtil.put(
					emptyObjectEntriesObjectRelationship.getName(),
					JSONFactoryUtil.createJSONArray()
				).put(
					nullObjectEntriesObjectRelationship.getName(),
					org.json.JSONObject.NULL
				).toString(),
				endpointUnsafeFunction.apply(jsonObject), method);

			Assert.assertEquals(
				0,
				jsonObject.getJSONObject(
					"status"
				).get(
					"code"
				));

			JSONArray nestedObjectEntriesJSONArray1 = jsonObject.getJSONArray(
				emptyObjectEntriesObjectRelationship.getName());

			Assert.assertEquals(0, nestedObjectEntriesJSONArray1.length());

			JSONArray nestedObjectEntriesJSONArray2 = jsonObject.getJSONArray(
				nullObjectEntriesObjectRelationship.getName());

			Assert.assertEquals(0, nestedObjectEntriesJSONArray2.length());
		}
		finally {
			objectRelationshipLocalService.deleteObjectRelationship(
				emptyObjectEntriesObjectRelationship);
			objectRelationshipLocalService.deleteObjectRelationship(
				nullObjectEntriesObjectRelationship);
		}
	}

	private void
			_testPostCustomObjectEntryWithInvalidNestedCustomObjectEntriesInManyToManyRelationship(
				String objectDefinitionRESTContextPath,
				ObjectRelationship objectRelationship)
		throws Exception {

		JSONObject objectEntryJSONObject = JSONUtil.put(
			objectRelationship.getName(),
			JSONFactoryUtil.createJSONObject(
				JSONUtil.put(
					OBJECT_FIELD_NAME_2, NEW_OBJECT_FIELD_VALUE_2
				).put(
					"externalReferenceCode", ERC_VALUE_2
				).toString()));

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			objectEntryJSONObject.toString(), objectDefinitionRESTContextPath,
			Http.Method.POST);

		Assert.assertEquals("BAD_REQUEST", jsonObject.get("status"));
	}

	private void
			_testPostCustomObjectEntryWithInvalidNestedCustomObjectEntriesInManyToOneRelationship(
				String objectDefinitionRESTContextPath,
				ObjectRelationship objectRelationship)
		throws Exception {

		JSONObject objectEntryJSONObject = JSONUtil.put(
			objectRelationship.getName(),
			createObjectEntriesJSONArray(
				new String[] {ERC_VALUE_1, ERC_VALUE_2}, OBJECT_FIELD_NAME_1,
				new String[] {
					RandomTestUtil.randomString(), RandomTestUtil.randomString()
				}));

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			objectEntryJSONObject.toString(), objectDefinitionRESTContextPath,
			Http.Method.POST);

		Assert.assertEquals("BAD_REQUEST", jsonObject.get("status"));
	}

	private void
			_testPostCustomObjectEntryWithInvalidNestedCustomObjectEntriesInOneToManyRelationship(
				String objectDefinitionRESTContextPath,
				ObjectRelationship objectRelationship)
		throws Exception {

		JSONObject objectEntryJSONObject = JSONUtil.put(
			objectRelationship.getName(),
			JSONFactoryUtil.createJSONObject(
				JSONUtil.put(
					OBJECT_FIELD_NAME_2, NEW_OBJECT_FIELD_VALUE_2
				).put(
					"externalReferenceCode", ERC_VALUE_2
				).toString()));

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			objectEntryJSONObject.toString(), objectDefinitionRESTContextPath,
			Http.Method.POST);

		Assert.assertEquals("BAD_REQUEST", jsonObject.get("status"));
	}

	private void
			_testPutCustomObjectEntryWithNestedCustomObjectEntriesByExternalReferenceCode(
				boolean manyToOne)
		throws Exception {

		JSONObject objectEntryJSONObject = JSONUtil.put(
			objectRelationship1.getName(),
			() -> {
				if (manyToOne) {
					return JSONFactoryUtil.createJSONObject(
						JSONUtil.put(
							OBJECT_FIELD_NAME_2, RandomTestUtil.randomString()
						).put(
							"externalReferenceCode", ERC_VALUE_1
						).toString());
				}

				return createObjectEntriesJSONArray(
					new String[] {ERC_VALUE_1, ERC_VALUE_2},
					OBJECT_FIELD_NAME_2,
					new String[] {
						RandomTestUtil.randomString(),
						RandomTestUtil.randomString()
					});
			});

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			objectEntryJSONObject.toString(),
			getObjectDefinition1().getRESTContextPath(), Http.Method.POST);

		JSONObject newObjectEntryJSONObject = JSONUtil.put(
			objectRelationship1.getName(),
			() -> {
				if (manyToOne) {
					return JSONFactoryUtil.createJSONObject(
						JSONUtil.put(
							OBJECT_FIELD_NAME_2, NEW_OBJECT_FIELD_VALUE_1
						).put(
							"externalReferenceCode", ERC_VALUE_1
						).toString());
				}

				return createObjectEntriesJSONArray(
					new String[] {ERC_VALUE_1}, OBJECT_FIELD_NAME_2,
					new String[] {NEW_OBJECT_FIELD_VALUE_1});
			});

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			newObjectEntryJSONObject.toString(),
			StringBundler.concat(
				getObjectDefinition1().getRESTContextPath(),
				"/by-external-reference-code/",
				jsonObject.getString("externalReferenceCode")),
			Http.Method.PUT);

		Assert.assertEquals(
			0,
			jsonObject.getJSONObject(
				"status"
			).get(
				"code"
			));

		if (manyToOne) {
			_assertObjectEntryField(
				jsonObject.getJSONObject(
					StringBundler.concat(
						"r_", objectRelationship1.getName(), "_",
						StringUtil.removeLast(
							getObjectDefinition2().getPKObjectFieldName(),
							"Id"))),
				OBJECT_FIELD_NAME_2, NEW_OBJECT_FIELD_VALUE_1);
		}
		else {
			JSONArray nestedObjectEntriesJSONArray = jsonObject.getJSONArray(
				objectRelationship1.getName());

			Assert.assertEquals(1, nestedObjectEntriesJSONArray.length());

			_assertObjectEntryField(
				nestedObjectEntriesJSONArray.getJSONObject(0),
				OBJECT_FIELD_NAME_2, NEW_OBJECT_FIELD_VALUE_1);
		}
	}

}