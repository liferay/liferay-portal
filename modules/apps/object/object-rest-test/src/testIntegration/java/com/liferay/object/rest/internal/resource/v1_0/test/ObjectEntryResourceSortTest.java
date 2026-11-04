/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.rest.internal.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.constants.ObjectActionKeys;
import com.liferay.object.constants.ObjectFieldValidationConstants;
import com.liferay.object.constants.ObjectRelationshipConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectField;
import com.liferay.object.rest.test.util.ObjectEntryTestUtil;
import com.liferay.object.rest.test.util.ObjectRelationshipTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.HTTPTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.URLCodec;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.io.Serializable;

import java.math.BigDecimal;

import java.util.Date;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

/**
 * @author Luis Miguel Barcos
 */
@FeatureFlags(featureFlags = @FeatureFlag(value = "LPS-164801"))
@RunWith(Arquillian.class)
public class ObjectEntryResourceSortTest
	extends BaseObjectEntryResourceTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testSortByCustomObjectField() throws Exception {
		String endpoint = getEndpoint(getObjectDefinition1(), testGroupId);

		objectRelationship1 = ObjectRelationshipTestUtil.addObjectRelationship(
			getObjectDefinition2(), getObjectDefinition1(),
			TestPropsValues.getUserId(),
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);

		ObjectField objectField = objectFieldLocalService.getObjectField(
			objectRelationship1.getObjectFieldId2());

		BigDecimal randomBigDecimal = new BigDecimal(
			RandomTestUtil.randomDouble());
		Date randomDate1 = RandomTestUtil.nextDate();
		Date randomDate2 = RandomTestUtil.nextDate();
		float randomFloat1 = RandomTestUtil.randomFloat();
		int randomInt = RandomTestUtil.randomInt();
		long randomLong = RandomTestUtil.randomLong(
			ObjectFieldValidationConstants.BUSINESS_TYPE_LONG_VALUE_MIN,
			ObjectFieldValidationConstants.BUSINESS_TYPE_LONG_VALUE_MAX);
		String randomString1 = RandomTestUtil.randomString();
		String randomString2 = RandomTestUtil.randomString();
		String randomString3 = RandomTestUtil.randomString();

		JSONObject jsonObject1 = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_BOOLEAN, false
			).put(
				OBJECT_FIELD_NAME_DATE, dateFormat.format(randomDate1)
			).put(
				OBJECT_FIELD_NAME_DATE_TIME_INPUT,
				dateTimeDateFormat.format(randomDate2)
			).put(
				OBJECT_FIELD_NAME_DECIMAL, randomFloat1
			).put(
				OBJECT_FIELD_NAME_INTEGER, randomInt
			).put(
				OBJECT_FIELD_NAME_LONG_INTEGER, randomLong
			).put(
				OBJECT_FIELD_NAME_LONG_TEXT, "a" + randomString1
			).put(
				OBJECT_FIELD_NAME_MULTISELECT_PICKLIST,
				JSONUtil.putAll(LIST_TYPE_ENTRY_KEY_1, LIST_TYPE_ENTRY_KEY_2)
			).put(
				OBJECT_FIELD_NAME_PICKLIST, LIST_TYPE_ENTRY_KEY_1
			).put(
				OBJECT_FIELD_NAME_PRECISION_DECIMAL, randomBigDecimal
			).put(
				OBJECT_FIELD_NAME_TEXT, "a" + randomString2
			).put(
				"externalReferenceCode", ERC_VALUE_2
			).put(
				objectField.getName(),
				() -> {
					ObjectEntry relatedObjectEntry =
						ObjectEntryTestUtil.addObjectEntry(
							getObjectDefinition2(),
							HashMapBuilder.<String, Serializable>put(
								OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2
							).put(
								"externalReferenceCode", "a" + randomString3
							).build());

					return relatedObjectEntry.getObjectEntryId();
				}
			).toString(),
			endpoint, Http.Method.POST);

		JSONObject jsonObject2 = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_BOOLEAN, true
			).put(
				OBJECT_FIELD_NAME_DATE,
				() -> dateFormat.format(
					new Date(randomDate1.getTime() + (24 * 3600 * 1000)))
			).put(
				OBJECT_FIELD_NAME_DATE_TIME_INPUT,
				dateTimeDateFormat.format(
					new Date(
						randomDate2.getTime() +
							(RandomTestUtil.randomInt(1, 60) * 1000)))
			).put(
				OBJECT_FIELD_NAME_DECIMAL, randomFloat1 + 1
			).put(
				OBJECT_FIELD_NAME_INTEGER, randomInt + 1
			).put(
				OBJECT_FIELD_NAME_LONG_INTEGER, randomLong + 1
			).put(
				OBJECT_FIELD_NAME_LONG_TEXT, "b" + randomString1
			).put(
				OBJECT_FIELD_NAME_MULTISELECT_PICKLIST,
				JSONUtil.putAll(LIST_TYPE_ENTRY_KEY_2, LIST_TYPE_ENTRY_KEY_3)
			).put(
				OBJECT_FIELD_NAME_PICKLIST, LIST_TYPE_ENTRY_KEY_2
			).put(
				OBJECT_FIELD_NAME_PRECISION_DECIMAL,
				randomBigDecimal.add(BigDecimal.ONE)
			).put(
				OBJECT_FIELD_NAME_TEXT, "b" + randomString2
			).put(
				"externalReferenceCode", ERC_VALUE_1
			).put(
				objectField.getName(),
				() -> {
					ObjectEntry relatedObjectEntry =
						ObjectEntryTestUtil.addObjectEntry(
							getObjectDefinition2(),
							HashMapBuilder.<String, Serializable>put(
								OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2
							).put(
								"externalReferenceCode", "b" + randomString3
							).build());

					return relatedObjectEntry.getObjectEntryId();
				}
			).toString(),
			endpoint, Http.Method.POST);

		try {
			_testSortByCustomObjectField(
				endpoint, jsonObject1, jsonObject2, OBJECT_FIELD_NAME_BOOLEAN);
			_testSortByCustomObjectField(
				endpoint, jsonObject1, jsonObject2, OBJECT_FIELD_NAME_DATE);
			_testSortByCustomObjectField(
				endpoint, jsonObject1, jsonObject2,
				OBJECT_FIELD_NAME_DATE_TIME_INPUT);
			_testSortByCustomObjectField(
				endpoint, jsonObject1, jsonObject2, OBJECT_FIELD_NAME_DECIMAL);
			_testSortByCustomObjectField(
				endpoint, jsonObject1, jsonObject2, OBJECT_FIELD_NAME_INTEGER);
			_testSortByCustomObjectField(
				endpoint, jsonObject1, jsonObject2,
				OBJECT_FIELD_NAME_LONG_INTEGER);
			_testSortByCustomObjectField(
				endpoint, jsonObject1, jsonObject2,
				OBJECT_FIELD_NAME_LONG_TEXT);
			_testSortByCustomObjectField(
				endpoint, jsonObject1, jsonObject2,
				OBJECT_FIELD_NAME_MULTISELECT_PICKLIST);
			_testSortByCustomObjectField(
				endpoint, jsonObject1, jsonObject2, OBJECT_FIELD_NAME_PICKLIST);
			_testSortByCustomObjectField(
				endpoint, jsonObject1, jsonObject2,
				OBJECT_FIELD_NAME_PRECISION_DECIMAL);
			_testSortByCustomObjectField(
				endpoint, jsonObject1, jsonObject2, OBJECT_FIELD_NAME_TEXT);

			// Many to one relationship fields

			String objectFieldName = objectField.getName();

			_testSortByCustomObjectField(
				endpoint, jsonObject1, jsonObject2, objectFieldName);

			_testSortByFieldName(
				endpoint, jsonObject1, jsonObject2,
				StringUtil.replaceLast(objectField.getName(), "Id", "ERC"));
			_testSortByFieldName(
				endpoint, jsonObject1, jsonObject2,
				StringUtil.extractLast(
					objectField.getName(), StringPool.UNDERLINE));

			// Sort by several fields

			_testSortByCustomObjectField(
				endpoint, jsonObject1, jsonObject2, OBJECT_FIELD_NAME_BOOLEAN,
				OBJECT_FIELD_NAME_DATE, OBJECT_FIELD_NAME_DATE_TIME_INPUT,
				OBJECT_FIELD_NAME_DECIMAL, OBJECT_FIELD_NAME_INTEGER,
				OBJECT_FIELD_NAME_LONG_INTEGER, OBJECT_FIELD_NAME_LONG_TEXT,
				OBJECT_FIELD_NAME_MULTISELECT_PICKLIST,
				OBJECT_FIELD_NAME_PICKLIST, OBJECT_FIELD_NAME_PRECISION_DECIMAL,
				OBJECT_FIELD_NAME_TEXT, objectFieldName);
		}
		finally {
			if (jsonObject1 != null) {
				objectEntryLocalService.deleteObjectEntry(
					jsonObject1.getLong("id"));
			}

			if (jsonObject2 != null) {
				objectEntryLocalService.deleteObjectEntry(
					jsonObject2.getLong("id"));
			}
		}
	}

	@Test
	public void testSortByManyToOneAndOneToManyRelationshipsCustomObjectFields()
		throws Exception {

		objectRelationship1 = ObjectRelationshipTestUtil.addObjectRelationship(
			getObjectDefinition2(), getObjectDefinition1(),
			TestPropsValues.getUserId(),
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);
		objectRelationship2 = ObjectRelationshipTestUtil.addObjectRelationship(
			getObjectDefinition2(), getObjectDefinition3(),
			TestPropsValues.getUserId(),
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);

		String endpoint1 = getEndpoint(getObjectDefinition1(), testGroupId);
		String endpoint2 = getEndpoint(getObjectDefinition2(), testGroupId);
		String endpoint3 = getEndpoint(getObjectDefinition3(), testGroupId);

		BigDecimal randomBigDecimal = new BigDecimal(
			RandomTestUtil.randomDouble());
		Date randomDate1 = RandomTestUtil.nextDate();
		Date randomDate2 = RandomTestUtil.nextDate();
		float randomFloat1 = RandomTestUtil.randomFloat();
		int randomInt = RandomTestUtil.randomInt();
		long randomLong = RandomTestUtil.randomLong(
			ObjectFieldValidationConstants.BUSINESS_TYPE_LONG_VALUE_MIN,
			ObjectFieldValidationConstants.BUSINESS_TYPE_LONG_VALUE_MAX);
		String randomString1 = RandomTestUtil.randomString();
		String randomString2 = RandomTestUtil.randomString();

		JSONObject depth1JSONObject1 = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_BOOLEAN, false
			).put(
				OBJECT_FIELD_NAME_DATE, dateFormat.format(randomDate1)
			).put(
				OBJECT_FIELD_NAME_DATE_TIME_INPUT,
				dateTimeDateFormat.format(randomDate2)
			).put(
				OBJECT_FIELD_NAME_DECIMAL, randomFloat1
			).put(
				OBJECT_FIELD_NAME_INTEGER, randomInt
			).put(
				OBJECT_FIELD_NAME_LONG_INTEGER, randomLong
			).put(
				OBJECT_FIELD_NAME_LONG_TEXT, "a" + randomString1
			).put(
				OBJECT_FIELD_NAME_MULTISELECT_PICKLIST,
				JSONUtil.putAll(LIST_TYPE_ENTRY_KEY_1, LIST_TYPE_ENTRY_KEY_2)
			).put(
				OBJECT_FIELD_NAME_PICKLIST, LIST_TYPE_ENTRY_KEY_1
			).put(
				OBJECT_FIELD_NAME_PRECISION_DECIMAL, randomBigDecimal
			).put(
				OBJECT_FIELD_NAME_TEXT, "a" + randomString2
			).toString(),
			endpoint2, Http.Method.POST);

		JSONObject depth1JSONObject2 = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_BOOLEAN, true
			).put(
				OBJECT_FIELD_NAME_DATE,
				() -> dateFormat.format(
					new Date(randomDate1.getTime() + (2 * 24 * 3600 * 1000)))
			).put(
				OBJECT_FIELD_NAME_DATE_TIME_INPUT,
				dateTimeDateFormat.format(
					new Date(randomDate2.getTime() + 2000))
			).put(
				OBJECT_FIELD_NAME_DECIMAL, randomFloat1 + 2
			).put(
				OBJECT_FIELD_NAME_INTEGER, randomInt + 2
			).put(
				OBJECT_FIELD_NAME_LONG_INTEGER, randomLong + 2
			).put(
				OBJECT_FIELD_NAME_LONG_TEXT, "c" + randomString1
			).put(
				OBJECT_FIELD_NAME_MULTISELECT_PICKLIST,
				JSONUtil.putAll(LIST_TYPE_ENTRY_KEY_2, LIST_TYPE_ENTRY_KEY_3)
			).put(
				OBJECT_FIELD_NAME_PICKLIST, LIST_TYPE_ENTRY_KEY_2
			).put(
				OBJECT_FIELD_NAME_PRECISION_DECIMAL,
				randomBigDecimal.add(new BigDecimal(2))
			).put(
				OBJECT_FIELD_NAME_TEXT, "c" + randomString2
			).toString(),
			endpoint2, Http.Method.POST);

		JSONObject depth2JSONObject1 = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_BOOLEAN, false
			).put(
				OBJECT_FIELD_NAME_DATE, dateFormat.format(randomDate1)
			).put(
				OBJECT_FIELD_NAME_DATE_TIME_INPUT,
				dateTimeDateFormat.format(randomDate2)
			).put(
				OBJECT_FIELD_NAME_DECIMAL, randomFloat1
			).put(
				OBJECT_FIELD_NAME_INTEGER, randomInt
			).put(
				OBJECT_FIELD_NAME_LONG_INTEGER, randomLong
			).put(
				OBJECT_FIELD_NAME_LONG_TEXT, "a" + randomString1
			).put(
				OBJECT_FIELD_NAME_MULTISELECT_PICKLIST,
				JSONUtil.putAll(LIST_TYPE_ENTRY_KEY_1, LIST_TYPE_ENTRY_KEY_2)
			).put(
				OBJECT_FIELD_NAME_PICKLIST, LIST_TYPE_ENTRY_KEY_1
			).put(
				OBJECT_FIELD_NAME_PRECISION_DECIMAL, randomBigDecimal
			).put(
				OBJECT_FIELD_NAME_TEXT, "a" + randomString2
			).toString(),
			endpoint3, Http.Method.POST);

		JSONObject depth2JSONObject2 = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_BOOLEAN, false
			).put(
				OBJECT_FIELD_NAME_DATE,
				() -> dateFormat.format(
					new Date(randomDate1.getTime() + (2 * 24 * 3600 * 1000)))
			).put(
				OBJECT_FIELD_NAME_DATE_TIME_INPUT,
				dateTimeDateFormat.format(
					new Date(randomDate2.getTime() + 2000))
			).put(
				OBJECT_FIELD_NAME_DECIMAL, randomFloat1 + 2
			).put(
				OBJECT_FIELD_NAME_INTEGER, randomInt + 2
			).put(
				OBJECT_FIELD_NAME_LONG_INTEGER, randomLong + 2
			).put(
				OBJECT_FIELD_NAME_LONG_TEXT, "c" + randomString1
			).put(
				OBJECT_FIELD_NAME_MULTISELECT_PICKLIST,
				JSONUtil.putAll(LIST_TYPE_ENTRY_KEY_1, LIST_TYPE_ENTRY_KEY_2)
			).put(
				OBJECT_FIELD_NAME_PICKLIST, LIST_TYPE_ENTRY_KEY_2
			).put(
				OBJECT_FIELD_NAME_PRECISION_DECIMAL,
				randomBigDecimal.add(new BigDecimal(2))
			).put(
				OBJECT_FIELD_NAME_TEXT, "c" + randomString2
			).toString(),
			endpoint3, Http.Method.POST);

		JSONObject depth2JSONObject3 = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_BOOLEAN, true
			).put(
				OBJECT_FIELD_NAME_DATE,
				() -> dateFormat.format(
					new Date(randomDate1.getTime() + (24 * 3600 * 1000)))
			).put(
				OBJECT_FIELD_NAME_DATE_TIME_INPUT,
				dateTimeDateFormat.format(
					new Date(randomDate2.getTime() + 1000))
			).put(
				OBJECT_FIELD_NAME_DECIMAL, randomFloat1 + 1
			).put(
				OBJECT_FIELD_NAME_INTEGER, randomInt + 1
			).put(
				OBJECT_FIELD_NAME_LONG_INTEGER, randomLong + 1
			).put(
				OBJECT_FIELD_NAME_LONG_TEXT, "b" + randomString1
			).put(
				OBJECT_FIELD_NAME_MULTISELECT_PICKLIST,
				JSONUtil.putAll(LIST_TYPE_ENTRY_KEY_2, LIST_TYPE_ENTRY_KEY_3)
			).put(
				OBJECT_FIELD_NAME_PICKLIST, LIST_TYPE_ENTRY_KEY_2
			).put(
				OBJECT_FIELD_NAME_PRECISION_DECIMAL,
				randomBigDecimal.add(BigDecimal.ONE)
			).put(
				OBJECT_FIELD_NAME_TEXT, "b" + randomString2
			).toString(),
			endpoint3, Http.Method.POST);

		JSONObject depth2JSONObject4 = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_BOOLEAN, true
			).put(
				OBJECT_FIELD_NAME_DATE,
				() -> dateFormat.format(
					new Date(randomDate1.getTime() + (3 * 24 * 3600 * 1000)))
			).put(
				OBJECT_FIELD_NAME_DATE_TIME_INPUT,
				dateTimeDateFormat.format(
					new Date(randomDate2.getTime() + 3000))
			).put(
				OBJECT_FIELD_NAME_DECIMAL, randomFloat1 + 3
			).put(
				OBJECT_FIELD_NAME_INTEGER, randomInt + 3
			).put(
				OBJECT_FIELD_NAME_LONG_INTEGER, randomLong + 3
			).put(
				OBJECT_FIELD_NAME_LONG_TEXT, "d" + randomString1
			).put(
				OBJECT_FIELD_NAME_MULTISELECT_PICKLIST,
				JSONUtil.putAll(LIST_TYPE_ENTRY_KEY_2, LIST_TYPE_ENTRY_KEY_3)
			).put(
				OBJECT_FIELD_NAME_PICKLIST, LIST_TYPE_ENTRY_KEY_3
			).put(
				OBJECT_FIELD_NAME_PRECISION_DECIMAL,
				randomBigDecimal.add(new BigDecimal(3))
			).put(
				OBJECT_FIELD_NAME_TEXT, "d" + randomString2
			).toString(),
			endpoint3, Http.Method.POST);

		JSONObject jsonObject1 = HTTPTestUtil.invokeToJSONObject(
			JSONFactoryUtil.getNullJSON(), endpoint1, Http.Method.POST);

		JSONObject jsonObject2 = HTTPTestUtil.invokeToJSONObject(
			JSONFactoryUtil.getNullJSON(), endpoint1, Http.Method.POST);

		JSONObject jsonObject3 = HTTPTestUtil.invokeToJSONObject(
			JSONFactoryUtil.getNullJSON(), endpoint1, Http.Method.POST);

		JSONObject jsonObject4 = HTTPTestUtil.invokeToJSONObject(
			JSONFactoryUtil.getNullJSON(), endpoint1, Http.Method.POST);

		HTTPTestUtil.invokeToJSONObject(
			null,
			String.format(
				"%s/%d/%s/%d", endpoint2, depth1JSONObject1.getLong("id"),
				objectRelationship1.getName(), jsonObject1.getLong("id")),
			Http.Method.PUT);

		HTTPTestUtil.invokeToJSONObject(
			null,
			String.format(
				"%s/%d/%s/%d", endpoint2, depth1JSONObject1.getLong("id"),
				objectRelationship1.getName(), jsonObject2.getLong("id")),
			Http.Method.PUT);

		HTTPTestUtil.invokeToJSONObject(
			null,
			String.format(
				"%s/%d/%s/%d", endpoint2, depth1JSONObject2.getLong("id"),
				objectRelationship1.getName(), jsonObject3.getLong("id")),
			Http.Method.PUT);

		HTTPTestUtil.invokeToJSONObject(
			null,
			String.format(
				"%s/%d/%s/%d", endpoint2, depth1JSONObject2.getLong("id"),
				objectRelationship1.getName(), jsonObject4.getLong("id")),
			Http.Method.PUT);

		HTTPTestUtil.invokeToJSONObject(
			null,
			String.format(
				"%s/%d/%s/%d", endpoint2, depth1JSONObject1.getLong("id"),
				objectRelationship2.getName(), depth2JSONObject1.getLong("id")),
			Http.Method.PUT);

		HTTPTestUtil.invokeToJSONObject(
			null,
			String.format(
				"%s/%d/%s/%d", endpoint2, depth1JSONObject1.getLong("id"),
				objectRelationship2.getName(), depth2JSONObject2.getLong("id")),
			Http.Method.PUT);

		HTTPTestUtil.invokeToJSONObject(
			null,
			String.format(
				"%s/%d/%s/%d", endpoint2, depth1JSONObject2.getLong("id"),
				objectRelationship2.getName(), depth2JSONObject3.getLong("id")),
			Http.Method.PUT);

		HTTPTestUtil.invokeToJSONObject(
			null,
			String.format(
				"%s/%d/%s/%d", endpoint2, depth1JSONObject2.getLong("id"),
				objectRelationship2.getName(), depth2JSONObject4.getLong("id")),
			Http.Method.PUT);

		try {
			_testSortByManyToOneAndOneToManyRelationshipsCustomObjectFields(
				endpoint1, endpoint3, jsonObject1, jsonObject2, jsonObject3,
				jsonObject4, depth2JSONObject1, depth2JSONObject2,
				depth2JSONObject3, depth2JSONObject4,
				String.format(
					"%s/%s/%s", objectRelationship1.getName(),
					objectRelationship2.getName(), OBJECT_FIELD_NAME_BOOLEAN));
			_testSortByManyToOneAndOneToManyRelationshipsCustomObjectFields(
				endpoint1, endpoint3, jsonObject1, jsonObject2, jsonObject3,
				jsonObject4, depth2JSONObject1, depth2JSONObject2,
				depth2JSONObject3, depth2JSONObject4,
				String.format(
					"%s/%s/%s", objectRelationship1.getName(),
					objectRelationship2.getName(), OBJECT_FIELD_NAME_DATE));
			_testSortByManyToOneAndOneToManyRelationshipsCustomObjectFields(
				endpoint1, endpoint3, jsonObject1, jsonObject2, jsonObject3,
				jsonObject4, depth2JSONObject1, depth2JSONObject2,
				depth2JSONObject3, depth2JSONObject4,
				String.format(
					"%s/%s/%s", objectRelationship1.getName(),
					objectRelationship2.getName(),
					OBJECT_FIELD_NAME_DATE_TIME_INPUT));
			_testSortByManyToOneAndOneToManyRelationshipsCustomObjectFields(
				endpoint1, endpoint3, jsonObject1, jsonObject2, jsonObject3,
				jsonObject4, depth2JSONObject1, depth2JSONObject2,
				depth2JSONObject3, depth2JSONObject4,
				String.format(
					"%s/%s/%s", objectRelationship1.getName(),
					objectRelationship2.getName(), OBJECT_FIELD_NAME_DECIMAL));
			_testSortByManyToOneAndOneToManyRelationshipsCustomObjectFields(
				endpoint1, endpoint3, jsonObject1, jsonObject2, jsonObject3,
				jsonObject4, depth2JSONObject1, depth2JSONObject2,
				depth2JSONObject3, depth2JSONObject4,
				String.format(
					"%s/%s/%s", objectRelationship1.getName(),
					objectRelationship2.getName(), OBJECT_FIELD_NAME_INTEGER));
			_testSortByManyToOneAndOneToManyRelationshipsCustomObjectFields(
				endpoint1, endpoint3, jsonObject1, jsonObject2, jsonObject3,
				jsonObject4, depth2JSONObject1, depth2JSONObject2,
				depth2JSONObject3, depth2JSONObject4,
				String.format(
					"%s/%s/%s", objectRelationship1.getName(),
					objectRelationship2.getName(),
					OBJECT_FIELD_NAME_LONG_INTEGER));
			_testSortByManyToOneAndOneToManyRelationshipsCustomObjectFields(
				endpoint1, endpoint3, jsonObject1, jsonObject2, jsonObject3,
				jsonObject4, depth2JSONObject1, depth2JSONObject2,
				depth2JSONObject3, depth2JSONObject4,
				String.format(
					"%s/%s/%s", objectRelationship1.getName(),
					objectRelationship2.getName(),
					OBJECT_FIELD_NAME_LONG_TEXT));
			_testSortByManyToOneAndOneToManyRelationshipsCustomObjectFields(
				endpoint1, endpoint3, jsonObject1, jsonObject2, jsonObject3,
				jsonObject4, depth2JSONObject1, depth2JSONObject2,
				depth2JSONObject3, depth2JSONObject4,
				String.format(
					"%s/%s/%s", objectRelationship1.getName(),
					objectRelationship2.getName(),
					OBJECT_FIELD_NAME_MULTISELECT_PICKLIST));
			_testSortByManyToOneAndOneToManyRelationshipsCustomObjectFields(
				endpoint1, endpoint3, jsonObject1, jsonObject2, jsonObject3,
				jsonObject4, depth2JSONObject1, depth2JSONObject2,
				depth2JSONObject3, depth2JSONObject4,
				String.format(
					"%s/%s/%s", objectRelationship1.getName(),
					objectRelationship2.getName(), OBJECT_FIELD_NAME_PICKLIST));
			_testSortByManyToOneAndOneToManyRelationshipsCustomObjectFields(
				endpoint1, endpoint3, jsonObject1, jsonObject2, jsonObject3,
				jsonObject4, depth2JSONObject1, depth2JSONObject2,
				depth2JSONObject3, depth2JSONObject4,
				String.format(
					"%s/%s/%s", objectRelationship1.getName(),
					objectRelationship2.getName(),
					OBJECT_FIELD_NAME_PRECISION_DECIMAL));
			_testSortByManyToOneAndOneToManyRelationshipsCustomObjectFields(
				endpoint1, endpoint3, jsonObject1, jsonObject2, jsonObject3,
				jsonObject4, depth2JSONObject1, depth2JSONObject2,
				depth2JSONObject3, depth2JSONObject4,
				String.format(
					"%s/%s/%s", objectRelationship1.getName(),
					objectRelationship2.getName(), OBJECT_FIELD_NAME_TEXT));

			// Sort by several fields

			_testSortByManyToOneAndOneToManyRelationshipsCustomObjectFields(
				endpoint1, endpoint3, jsonObject1, jsonObject2, jsonObject3,
				jsonObject4, depth2JSONObject1, depth2JSONObject2,
				depth2JSONObject3, depth2JSONObject4,
				String.format(
					"%s/%s/%s", objectRelationship1.getName(),
					objectRelationship2.getName(), OBJECT_FIELD_NAME_BOOLEAN),
				String.format(
					"%s/%s/%s", objectRelationship1.getName(),
					objectRelationship2.getName(), OBJECT_FIELD_NAME_DATE),
				String.format(
					"%s/%s/%s", objectRelationship1.getName(),
					objectRelationship2.getName(),
					OBJECT_FIELD_NAME_DATE_TIME_INPUT),
				String.format(
					"%s/%s/%s", objectRelationship1.getName(),
					objectRelationship2.getName(), OBJECT_FIELD_NAME_DECIMAL),
				String.format(
					"%s/%s/%s", objectRelationship1.getName(),
					objectRelationship2.getName(), OBJECT_FIELD_NAME_INTEGER),
				String.format(
					"%s/%s/%s", objectRelationship1.getName(),
					objectRelationship2.getName(),
					OBJECT_FIELD_NAME_LONG_INTEGER),
				String.format(
					"%s/%s/%s", objectRelationship1.getName(),
					objectRelationship2.getName(), OBJECT_FIELD_NAME_LONG_TEXT),
				String.format(
					"%s/%s/%s", objectRelationship1.getName(),
					objectRelationship2.getName(),
					OBJECT_FIELD_NAME_MULTISELECT_PICKLIST),
				String.format(
					"%s/%s/%s", objectRelationship1.getName(),
					objectRelationship2.getName(), OBJECT_FIELD_NAME_PICKLIST),
				String.format(
					"%s/%s/%s", objectRelationship1.getName(),
					objectRelationship2.getName(),
					OBJECT_FIELD_NAME_PRECISION_DECIMAL),
				String.format(
					"%s/%s/%s", objectRelationship1.getName(),
					objectRelationship2.getName(), OBJECT_FIELD_NAME_TEXT));
		}
		finally {
			if (jsonObject1 != null) {
				objectEntryLocalService.deleteObjectEntry(
					jsonObject1.getLong("id"));
			}

			if (jsonObject2 != null) {
				objectEntryLocalService.deleteObjectEntry(
					jsonObject2.getLong("id"));
			}

			if (jsonObject3 != null) {
				objectEntryLocalService.deleteObjectEntry(
					jsonObject3.getLong("id"));
			}

			if (jsonObject4 != null) {
				objectEntryLocalService.deleteObjectEntry(
					jsonObject4.getLong("id"));
			}

			if (depth2JSONObject1 != null) {
				objectEntryLocalService.deleteObjectEntry(
					depth2JSONObject1.getLong("id"));
			}

			if (depth2JSONObject2 != null) {
				objectEntryLocalService.deleteObjectEntry(
					depth2JSONObject2.getLong("id"));
			}

			if (depth2JSONObject3 != null) {
				objectEntryLocalService.deleteObjectEntry(
					depth2JSONObject3.getLong("id"));
			}

			if (depth2JSONObject4 != null) {
				objectEntryLocalService.deleteObjectEntry(
					depth2JSONObject4.getLong("id"));
			}

			if (depth1JSONObject1 != null) {
				objectEntryLocalService.deleteObjectEntry(
					depth1JSONObject1.getLong("id"));
			}

			if (depth1JSONObject2 != null) {
				objectEntryLocalService.deleteObjectEntry(
					depth1JSONObject2.getLong("id"));
			}
		}
	}

	@Test
	public void testSortByManyToOneRelationshipCustomObjectFields()
		throws Exception {

		objectRelationship1 = ObjectRelationshipTestUtil.addObjectRelationship(
			getObjectDefinition2(), getObjectDefinition1(),
			TestPropsValues.getUserId(),
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);
		objectRelationship2 = ObjectRelationshipTestUtil.addObjectRelationship(
			getObjectDefinition3(), getObjectDefinition2(),
			TestPropsValues.getUserId(),
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);

		String endpoint1 = getEndpoint(getObjectDefinition1(), testGroupId);
		String endpoint2 = getEndpoint(getObjectDefinition2(), testGroupId);
		String endpoint3 = getEndpoint(getObjectDefinition3(), testGroupId);

		BigDecimal randomBigDecimal = new BigDecimal(
			RandomTestUtil.randomDouble());
		Date randomDate1 = RandomTestUtil.nextDate();
		Date randomDate2 = RandomTestUtil.nextDate();
		float randomFloat1 = RandomTestUtil.randomFloat();
		int randomInt = RandomTestUtil.randomInt();
		long randomLong = RandomTestUtil.randomLong(
			ObjectFieldValidationConstants.BUSINESS_TYPE_LONG_VALUE_MIN,
			ObjectFieldValidationConstants.BUSINESS_TYPE_LONG_VALUE_MAX);
		String randomString1 = RandomTestUtil.randomString();
		String randomString2 = RandomTestUtil.randomString();

		JSONObject depth1JSONObject1 = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_BOOLEAN, false
			).put(
				OBJECT_FIELD_NAME_DATE, dateFormat.format(randomDate1)
			).put(
				OBJECT_FIELD_NAME_DATE_TIME_INPUT,
				dateTimeDateFormat.format(randomDate2)
			).put(
				OBJECT_FIELD_NAME_DECIMAL, randomFloat1
			).put(
				OBJECT_FIELD_NAME_INTEGER, randomInt
			).put(
				OBJECT_FIELD_NAME_LONG_INTEGER, randomLong
			).put(
				OBJECT_FIELD_NAME_LONG_TEXT, "a" + randomString1
			).put(
				OBJECT_FIELD_NAME_MULTISELECT_PICKLIST,
				JSONUtil.putAll(LIST_TYPE_ENTRY_KEY_1, LIST_TYPE_ENTRY_KEY_2)
			).put(
				OBJECT_FIELD_NAME_PICKLIST, LIST_TYPE_ENTRY_KEY_1
			).put(
				OBJECT_FIELD_NAME_PRECISION_DECIMAL, randomBigDecimal
			).put(
				OBJECT_FIELD_NAME_TEXT, "a" + randomString2
			).toString(),
			endpoint2, Http.Method.POST);

		JSONObject depth1JSONObject2 = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_BOOLEAN, true
			).put(
				OBJECT_FIELD_NAME_DATE,
				() -> dateFormat.format(
					new Date(randomDate1.getTime() + (2 * 24 * 3600 * 1000)))
			).put(
				OBJECT_FIELD_NAME_DATE_TIME_INPUT,
				dateTimeDateFormat.format(
					new Date(randomDate2.getTime() + 2000))
			).put(
				OBJECT_FIELD_NAME_DECIMAL, randomFloat1 + 2
			).put(
				OBJECT_FIELD_NAME_INTEGER, randomInt + 2
			).put(
				OBJECT_FIELD_NAME_LONG_INTEGER, randomLong + 2
			).put(
				OBJECT_FIELD_NAME_LONG_TEXT, "c" + randomString1
			).put(
				OBJECT_FIELD_NAME_MULTISELECT_PICKLIST,
				JSONUtil.putAll(LIST_TYPE_ENTRY_KEY_2, LIST_TYPE_ENTRY_KEY_3)
			).put(
				OBJECT_FIELD_NAME_PICKLIST, LIST_TYPE_ENTRY_KEY_2
			).put(
				OBJECT_FIELD_NAME_PRECISION_DECIMAL,
				randomBigDecimal.add(new BigDecimal(2))
			).put(
				OBJECT_FIELD_NAME_TEXT, "c" + randomString2
			).toString(),
			endpoint2, Http.Method.POST);

		JSONObject depth2JSONObject1 = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_BOOLEAN, false
			).put(
				OBJECT_FIELD_NAME_DATE, dateFormat.format(randomDate1)
			).put(
				OBJECT_FIELD_NAME_DATE_TIME_INPUT,
				dateTimeDateFormat.format(randomDate2)
			).put(
				OBJECT_FIELD_NAME_DECIMAL, randomFloat1
			).put(
				OBJECT_FIELD_NAME_INTEGER, randomInt
			).put(
				OBJECT_FIELD_NAME_LONG_INTEGER, randomLong
			).put(
				OBJECT_FIELD_NAME_LONG_TEXT, "a" + randomString1
			).put(
				OBJECT_FIELD_NAME_MULTISELECT_PICKLIST,
				JSONUtil.putAll(LIST_TYPE_ENTRY_KEY_1, LIST_TYPE_ENTRY_KEY_2)
			).put(
				OBJECT_FIELD_NAME_PICKLIST, LIST_TYPE_ENTRY_KEY_1
			).put(
				OBJECT_FIELD_NAME_PRECISION_DECIMAL, randomBigDecimal
			).put(
				OBJECT_FIELD_NAME_TEXT, "a" + randomString2
			).toString(),
			endpoint3, Http.Method.POST);

		JSONObject depth2JSONObject2 = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_BOOLEAN, true
			).put(
				OBJECT_FIELD_NAME_DATE,
				() -> dateFormat.format(
					new Date(randomDate1.getTime() + (2 * 24 * 3600 * 1000)))
			).put(
				OBJECT_FIELD_NAME_DATE_TIME_INPUT,
				dateTimeDateFormat.format(
					new Date(randomDate2.getTime() + 2000))
			).put(
				OBJECT_FIELD_NAME_DECIMAL, randomFloat1 + 2
			).put(
				OBJECT_FIELD_NAME_INTEGER, randomInt + 2
			).put(
				OBJECT_FIELD_NAME_LONG_INTEGER, randomLong + 2
			).put(
				OBJECT_FIELD_NAME_LONG_TEXT, "c" + randomString1
			).put(
				OBJECT_FIELD_NAME_MULTISELECT_PICKLIST,
				JSONUtil.putAll(LIST_TYPE_ENTRY_KEY_2, LIST_TYPE_ENTRY_KEY_3)
			).put(
				OBJECT_FIELD_NAME_PICKLIST, LIST_TYPE_ENTRY_KEY_2
			).put(
				OBJECT_FIELD_NAME_PRECISION_DECIMAL,
				randomBigDecimal.add(new BigDecimal(2))
			).put(
				OBJECT_FIELD_NAME_TEXT, "c" + randomString2
			).toString(),
			endpoint3, Http.Method.POST);

		JSONObject jsonObject1 = HTTPTestUtil.invokeToJSONObject(
			JSONFactoryUtil.getNullJSON(), endpoint1, Http.Method.POST);

		JSONObject jsonObject2 = HTTPTestUtil.invokeToJSONObject(
			JSONFactoryUtil.getNullJSON(), endpoint1, Http.Method.POST);

		JSONObject jsonObject3 = HTTPTestUtil.invokeToJSONObject(
			JSONFactoryUtil.getNullJSON(), endpoint1, Http.Method.POST);

		JSONObject jsonObject4 = HTTPTestUtil.invokeToJSONObject(
			JSONFactoryUtil.getNullJSON(), endpoint1, Http.Method.POST);

		HTTPTestUtil.invokeToJSONObject(
			null,
			String.format(
				"%s/%d/%s/%d", endpoint2, depth1JSONObject1.getLong("id"),
				objectRelationship1.getName(), jsonObject1.getLong("id")),
			Http.Method.PUT);

		HTTPTestUtil.invokeToJSONObject(
			null,
			String.format(
				"%s/%d/%s/%d", endpoint2, depth1JSONObject1.getLong("id"),
				objectRelationship1.getName(), jsonObject2.getLong("id")),
			Http.Method.PUT);

		HTTPTestUtil.invokeToJSONObject(
			null,
			String.format(
				"%s/%d/%s/%d", endpoint2, depth1JSONObject2.getLong("id"),
				objectRelationship1.getName(), jsonObject3.getLong("id")),
			Http.Method.PUT);

		HTTPTestUtil.invokeToJSONObject(
			null,
			String.format(
				"%s/%d/%s/%d", endpoint2, depth1JSONObject2.getLong("id"),
				objectRelationship1.getName(), jsonObject4.getLong("id")),
			Http.Method.PUT);

		HTTPTestUtil.invokeToJSONObject(
			null,
			String.format(
				"%s/%d/%s/%d", endpoint3, depth2JSONObject1.getLong("id"),
				objectRelationship2.getName(), depth1JSONObject1.getLong("id")),
			Http.Method.PUT);

		HTTPTestUtil.invokeToJSONObject(
			null,
			String.format(
				"%s/%d/%s/%d", endpoint3, depth2JSONObject2.getLong("id"),
				objectRelationship2.getName(), depth1JSONObject2.getLong("id")),
			Http.Method.PUT);

		try {

			// Depth 1

			_testSortByManyToOneRelationshipCustomObjectFields(
				endpoint1, endpoint2, jsonObject1, jsonObject2, jsonObject3,
				jsonObject4, depth1JSONObject1, depth1JSONObject2,
				String.format(
					"%s/%s", objectRelationship1.getName(),
					OBJECT_FIELD_NAME_BOOLEAN));

			_testSortByManyToOneRelationshipCustomObjectFields(
				endpoint1, endpoint2, jsonObject1, jsonObject2, jsonObject3,
				jsonObject4, depth1JSONObject1, depth1JSONObject2,
				String.format(
					"%s/%s", objectRelationship1.getName(),
					OBJECT_FIELD_NAME_DATE));
			_testSortByManyToOneRelationshipCustomObjectFields(
				endpoint1, endpoint2, jsonObject1, jsonObject2, jsonObject3,
				jsonObject4, depth1JSONObject1, depth1JSONObject2,
				String.format(
					"%s/%s", objectRelationship1.getName(),
					OBJECT_FIELD_NAME_DATE_TIME_INPUT));
			_testSortByManyToOneRelationshipCustomObjectFields(
				endpoint1, endpoint2, jsonObject1, jsonObject2, jsonObject3,
				jsonObject4, depth1JSONObject1, depth1JSONObject2,
				String.format(
					"%s/%s", objectRelationship1.getName(),
					OBJECT_FIELD_NAME_DECIMAL));
			_testSortByManyToOneRelationshipCustomObjectFields(
				endpoint1, endpoint2, jsonObject1, jsonObject2, jsonObject3,
				jsonObject4, depth1JSONObject1, depth1JSONObject2,
				String.format(
					"%s/%s", objectRelationship1.getName(),
					OBJECT_FIELD_NAME_INTEGER));
			_testSortByManyToOneRelationshipCustomObjectFields(
				endpoint1, endpoint2, jsonObject1, jsonObject2, jsonObject3,
				jsonObject4, depth1JSONObject1, depth1JSONObject2,
				String.format(
					"%s/%s", objectRelationship1.getName(),
					OBJECT_FIELD_NAME_LONG_INTEGER));
			_testSortByManyToOneRelationshipCustomObjectFields(
				endpoint1, endpoint2, jsonObject1, jsonObject2, jsonObject3,
				jsonObject4, depth1JSONObject1, depth1JSONObject2,
				String.format(
					"%s/%s", objectRelationship1.getName(),
					OBJECT_FIELD_NAME_LONG_TEXT));
			_testSortByManyToOneRelationshipCustomObjectFields(
				endpoint1, endpoint2, jsonObject1, jsonObject2, jsonObject3,
				jsonObject4, depth1JSONObject1, depth1JSONObject2,
				String.format(
					"%s/%s", objectRelationship1.getName(),
					OBJECT_FIELD_NAME_MULTISELECT_PICKLIST));
			_testSortByManyToOneRelationshipCustomObjectFields(
				endpoint1, endpoint2, jsonObject1, jsonObject2, jsonObject3,
				jsonObject4, depth1JSONObject1, depth1JSONObject2,
				String.format(
					"%s/%s", objectRelationship1.getName(),
					OBJECT_FIELD_NAME_PICKLIST));
			_testSortByManyToOneRelationshipCustomObjectFields(
				endpoint1, endpoint2, jsonObject1, jsonObject2, jsonObject3,
				jsonObject4, depth1JSONObject1, depth1JSONObject2,
				String.format(
					"%s/%s", objectRelationship1.getName(),
					OBJECT_FIELD_NAME_PRECISION_DECIMAL));
			_testSortByManyToOneRelationshipCustomObjectFields(
				endpoint1, endpoint2, jsonObject1, jsonObject2, jsonObject3,
				jsonObject4, depth1JSONObject1, depth1JSONObject2,
				String.format(
					"%s/%s", objectRelationship1.getName(),
					OBJECT_FIELD_NAME_TEXT));

			// Depth 2

			_testSortByManyToOneRelationshipCustomObjectFields(
				endpoint1, endpoint3, jsonObject1, jsonObject2, jsonObject3,
				jsonObject4, depth2JSONObject1, depth2JSONObject2,
				String.format(
					"%s/%s/%s", objectRelationship1.getName(),
					objectRelationship2.getName(), OBJECT_FIELD_NAME_BOOLEAN));

			_testSortByManyToOneRelationshipCustomObjectFields(
				endpoint1, endpoint3, jsonObject1, jsonObject2, jsonObject3,
				jsonObject4, depth2JSONObject1, depth2JSONObject2,
				String.format(
					"%s/%s/%s", objectRelationship1.getName(),
					objectRelationship2.getName(), OBJECT_FIELD_NAME_DATE));
			_testSortByManyToOneRelationshipCustomObjectFields(
				endpoint1, endpoint3, jsonObject1, jsonObject2, jsonObject3,
				jsonObject4, depth2JSONObject1, depth2JSONObject2,
				String.format(
					"%s/%s/%s", objectRelationship1.getName(),
					objectRelationship2.getName(),
					OBJECT_FIELD_NAME_DATE_TIME_INPUT));
			_testSortByManyToOneRelationshipCustomObjectFields(
				endpoint1, endpoint3, jsonObject1, jsonObject2, jsonObject3,
				jsonObject4, depth2JSONObject1, depth2JSONObject2,
				String.format(
					"%s/%s/%s", objectRelationship1.getName(),
					objectRelationship2.getName(), OBJECT_FIELD_NAME_DECIMAL));
			_testSortByManyToOneRelationshipCustomObjectFields(
				endpoint1, endpoint3, jsonObject1, jsonObject2, jsonObject3,
				jsonObject4, depth2JSONObject1, depth2JSONObject2,
				String.format(
					"%s/%s/%s", objectRelationship1.getName(),
					objectRelationship2.getName(), OBJECT_FIELD_NAME_INTEGER));
			_testSortByManyToOneRelationshipCustomObjectFields(
				endpoint1, endpoint3, jsonObject1, jsonObject2, jsonObject3,
				jsonObject4, depth2JSONObject1, depth2JSONObject2,
				String.format(
					"%s/%s/%s", objectRelationship1.getName(),
					objectRelationship2.getName(),
					OBJECT_FIELD_NAME_LONG_INTEGER));
			_testSortByManyToOneRelationshipCustomObjectFields(
				endpoint1, endpoint3, jsonObject1, jsonObject2, jsonObject3,
				jsonObject4, depth2JSONObject1, depth2JSONObject2,
				String.format(
					"%s/%s/%s", objectRelationship1.getName(),
					objectRelationship2.getName(),
					OBJECT_FIELD_NAME_LONG_TEXT));
			_testSortByManyToOneRelationshipCustomObjectFields(
				endpoint1, endpoint3, jsonObject1, jsonObject2, jsonObject3,
				jsonObject4, depth2JSONObject1, depth2JSONObject2,
				String.format(
					"%s/%s/%s", objectRelationship1.getName(),
					objectRelationship2.getName(),
					OBJECT_FIELD_NAME_MULTISELECT_PICKLIST));
			_testSortByManyToOneRelationshipCustomObjectFields(
				endpoint1, endpoint3, jsonObject1, jsonObject2, jsonObject3,
				jsonObject4, depth2JSONObject1, depth2JSONObject2,
				String.format(
					"%s/%s/%s", objectRelationship1.getName(),
					objectRelationship2.getName(), OBJECT_FIELD_NAME_PICKLIST));
			_testSortByManyToOneRelationshipCustomObjectFields(
				endpoint1, endpoint3, jsonObject1, jsonObject2, jsonObject3,
				jsonObject4, depth2JSONObject1, depth2JSONObject2,
				String.format(
					"%s/%s/%s", objectRelationship1.getName(),
					objectRelationship2.getName(),
					OBJECT_FIELD_NAME_PRECISION_DECIMAL));
			_testSortByManyToOneRelationshipCustomObjectFields(
				endpoint1, endpoint3, jsonObject1, jsonObject2, jsonObject3,
				jsonObject4, depth2JSONObject1, depth2JSONObject2,
				String.format(
					"%s/%s/%s", objectRelationship1.getName(),
					objectRelationship2.getName(), OBJECT_FIELD_NAME_TEXT));

			// Sort by several fields

			_testSortByManyToOneRelationshipCustomObjectFields(
				endpoint1, endpoint2, jsonObject1, jsonObject2, jsonObject3,
				jsonObject4, depth1JSONObject1, depth1JSONObject2,
				String.format(
					"%s/%s", objectRelationship1.getName(),
					OBJECT_FIELD_NAME_BOOLEAN),
				String.format(
					"%s/%s", objectRelationship1.getName(),
					OBJECT_FIELD_NAME_DATE),
				String.format(
					"%s/%s", objectRelationship1.getName(),
					OBJECT_FIELD_NAME_DATE_TIME_INPUT),
				String.format(
					"%s/%s", objectRelationship1.getName(),
					OBJECT_FIELD_NAME_DECIMAL),
				String.format(
					"%s/%s", objectRelationship1.getName(),
					OBJECT_FIELD_NAME_INTEGER),
				String.format(
					"%s/%s", objectRelationship1.getName(),
					OBJECT_FIELD_NAME_LONG_INTEGER),
				String.format(
					"%s/%s", objectRelationship1.getName(),
					OBJECT_FIELD_NAME_LONG_TEXT),
				String.format(
					"%s/%s", objectRelationship1.getName(),
					OBJECT_FIELD_NAME_MULTISELECT_PICKLIST),
				String.format(
					"%s/%s", objectRelationship1.getName(),
					OBJECT_FIELD_NAME_PICKLIST),
				String.format(
					"%s/%s", objectRelationship1.getName(),
					OBJECT_FIELD_NAME_PRECISION_DECIMAL),
				String.format(
					"%s/%s", objectRelationship1.getName(),
					OBJECT_FIELD_NAME_TEXT));

			_testSortByManyToOneRelationshipCustomObjectFields(
				endpoint1, endpoint3, jsonObject1, jsonObject2, jsonObject3,
				jsonObject4, depth2JSONObject1, depth2JSONObject2,
				String.format(
					"%s/%s/%s", objectRelationship1.getName(),
					objectRelationship2.getName(), OBJECT_FIELD_NAME_BOOLEAN),
				String.format(
					"%s/%s/%s", objectRelationship1.getName(),
					objectRelationship2.getName(), OBJECT_FIELD_NAME_DATE),
				String.format(
					"%s/%s/%s", objectRelationship1.getName(),
					objectRelationship2.getName(),
					OBJECT_FIELD_NAME_DATE_TIME_INPUT),
				String.format(
					"%s/%s/%s", objectRelationship1.getName(),
					objectRelationship2.getName(), OBJECT_FIELD_NAME_DECIMAL),
				String.format(
					"%s/%s/%s", objectRelationship1.getName(),
					objectRelationship2.getName(), OBJECT_FIELD_NAME_INTEGER),
				String.format(
					"%s/%s/%s", objectRelationship1.getName(),
					objectRelationship2.getName(),
					OBJECT_FIELD_NAME_LONG_INTEGER),
				String.format(
					"%s/%s/%s", objectRelationship1.getName(),
					objectRelationship2.getName(), OBJECT_FIELD_NAME_LONG_TEXT),
				String.format(
					"%s/%s/%s", objectRelationship1.getName(),
					objectRelationship2.getName(),
					OBJECT_FIELD_NAME_MULTISELECT_PICKLIST),
				String.format(
					"%s/%s/%s", objectRelationship1.getName(),
					objectRelationship2.getName(), OBJECT_FIELD_NAME_PICKLIST),
				String.format(
					"%s/%s/%s", objectRelationship1.getName(),
					objectRelationship2.getName(),
					OBJECT_FIELD_NAME_PRECISION_DECIMAL),
				String.format(
					"%s/%s/%s", objectRelationship1.getName(),
					objectRelationship2.getName(), OBJECT_FIELD_NAME_TEXT));
		}
		finally {
			if (jsonObject1 != null) {
				objectEntryLocalService.deleteObjectEntry(
					jsonObject1.getLong("id"));
			}

			if (jsonObject2 != null) {
				objectEntryLocalService.deleteObjectEntry(
					jsonObject2.getLong("id"));
			}

			if (jsonObject3 != null) {
				objectEntryLocalService.deleteObjectEntry(
					jsonObject3.getLong("id"));
			}

			if (jsonObject4 != null) {
				objectEntryLocalService.deleteObjectEntry(
					jsonObject4.getLong("id"));
			}

			if (depth1JSONObject1 != null) {
				objectEntryLocalService.deleteObjectEntry(
					depth1JSONObject1.getLong("id"));
			}

			if (depth1JSONObject2 != null) {
				objectEntryLocalService.deleteObjectEntry(
					depth1JSONObject2.getLong("id"));
			}

			if (depth2JSONObject1 != null) {
				objectEntryLocalService.deleteObjectEntry(
					depth2JSONObject1.getLong("id"));
			}

			if (depth2JSONObject2 != null) {
				objectEntryLocalService.deleteObjectEntry(
					depth2JSONObject2.getLong("id"));
			}
		}
	}

	@Test
	public void testSortByManyToOneRelationshipSystemObjectFields()
		throws Exception {

		objectRelationship1 = ObjectRelationshipTestUtil.addObjectRelationship(
			getObjectDefinition2(), getObjectDefinition1(),
			TestPropsValues.getUserId(),
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);
		objectRelationship2 = ObjectRelationshipTestUtil.addObjectRelationship(
			getObjectDefinition3(), getObjectDefinition2(),
			TestPropsValues.getUserId(),
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);

		Role role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		addResourcePermission(
			ObjectActionKeys.ADD_OBJECT_ENTRY,
			getObjectDefinition2().getResourceName(), role);
		addResourcePermission(
			ObjectActionKeys.ADD_OBJECT_ENTRY,
			getObjectDefinition3().getResourceName(), role);
		addResourcePermission(
			ActionKeys.VIEW, getObjectDefinition2().getClassName(), role);
		addResourcePermission(
			ActionKeys.VIEW, getObjectDefinition3().getClassName(), role);

		User user1 = addUser("test1", "test1");
		User user2 = addUser("test2", "test2");
		User user3 = addUser("test3", "test3");

		roleLocalService.addUserRole(user1.getUserId(), role.getRoleId());
		roleLocalService.addUserRole(user2.getUserId(), role.getRoleId());
		roleLocalService.addUserRole(user3.getUserId(), role.getRoleId());

		getObjectDefinition1().setEnableObjectEntryDraft(true);

		getObjectDefinition2().setEnableObjectEntryDraft(true);

		getObjectDefinition3().setEnableObjectEntryDraft(true);

		objectDefinition1 = objectDefinitionLocalService.updateObjectDefinition(
			getObjectDefinition1());
		objectDefinition2 = objectDefinitionLocalService.updateObjectDefinition(
			getObjectDefinition2());
		objectDefinition3 = objectDefinitionLocalService.updateObjectDefinition(
			getObjectDefinition3());

		String endpoint1 = getEndpoint(getObjectDefinition1(), testGroupId);
		String endpoint2 = getEndpoint(getObjectDefinition2(), testGroupId);
		String endpoint3 = getEndpoint(getObjectDefinition3(), testGroupId);

		JSONObject[] manyToOneDepth1JSONObjects = new JSONObject[2];

		HTTPTestUtil.customize(
		).withCredentials(
			"test3@liferay.com", "test3"
		).apply(
			() ->
				manyToOneDepth1JSONObjects[0] = HTTPTestUtil.invokeToJSONObject(
					JSONUtil.put(
						"externalReferenceCode", "ERC2_1"
					).put(
						"status",
						JSONUtil.put("code", WorkflowConstants.STATUS_DRAFT)
					).toString(),
					endpoint2, Http.Method.POST)
		);

		HTTPTestUtil.customize(
		).withCredentials(
			"test2@liferay.com", "test2"
		).apply(
			() ->
				manyToOneDepth1JSONObjects[1] = HTTPTestUtil.invokeToJSONObject(
					JSONUtil.put(
						"externalReferenceCode", "ERC1_1"
					).toString(),
					endpoint2, Http.Method.POST)
		);

		JSONObject[] manyToOneDepth2JSONObjects = new JSONObject[2];

		HTTPTestUtil.customize(
		).withCredentials(
			"test2@liferay.com", "test2"
		).apply(
			() ->
				manyToOneDepth2JSONObjects[0] = HTTPTestUtil.invokeToJSONObject(
					JSONUtil.put(
						"externalReferenceCode", "ERC2_2"
					).put(
						"status",
						JSONUtil.put("code", WorkflowConstants.STATUS_DRAFT)
					).toString(),
					endpoint3, Http.Method.POST)
		);

		HTTPTestUtil.customize(
		).withCredentials(
			"test1@liferay.com", "test1"
		).apply(
			() ->
				manyToOneDepth2JSONObjects[1] = HTTPTestUtil.invokeToJSONObject(
					JSONUtil.put(
						"externalReferenceCode", "ERC1_2"
					).toString(),
					endpoint3, Http.Method.POST)
		);

		JSONObject jsonObject1 = HTTPTestUtil.invokeToJSONObject(
			JSONFactoryUtil.getNullJSON(), endpoint1, Http.Method.POST);

		JSONObject jsonObject2 = HTTPTestUtil.invokeToJSONObject(
			JSONFactoryUtil.getNullJSON(), endpoint1, Http.Method.POST);

		JSONObject jsonObject3 = HTTPTestUtil.invokeToJSONObject(
			JSONFactoryUtil.getNullJSON(), endpoint1, Http.Method.POST);

		JSONObject jsonObject4 = HTTPTestUtil.invokeToJSONObject(
			JSONFactoryUtil.getNullJSON(), endpoint1, Http.Method.POST);

		HTTPTestUtil.invokeToJSONObject(
			null,
			String.format(
				"%s/%d/%s/%d", endpoint2,
				manyToOneDepth1JSONObjects[0].getLong("id"),
				objectRelationship1.getName(), jsonObject1.getLong("id")),
			Http.Method.PUT);

		HTTPTestUtil.invokeToJSONObject(
			null,
			String.format(
				"%s/%d/%s/%d", endpoint2,
				manyToOneDepth1JSONObjects[0].getLong("id"),
				objectRelationship1.getName(), jsonObject2.getLong("id")),
			Http.Method.PUT);

		HTTPTestUtil.invokeToJSONObject(
			null,
			String.format(
				"%s/%d/%s/%d", endpoint2,
				manyToOneDepth1JSONObjects[1].getLong("id"),
				objectRelationship1.getName(), jsonObject3.getLong("id")),
			Http.Method.PUT);

		HTTPTestUtil.invokeToJSONObject(
			null,
			String.format(
				"%s/%d/%s/%d", endpoint2,
				manyToOneDepth1JSONObjects[1].getLong("id"),
				objectRelationship1.getName(), jsonObject4.getLong("id")),
			Http.Method.PUT);

		HTTPTestUtil.invokeToJSONObject(
			null,
			String.format(
				"%s/%d/%s/%d", endpoint3,
				manyToOneDepth2JSONObjects[0].getLong("id"),
				objectRelationship2.getName(),
				manyToOneDepth1JSONObjects[0].getLong("id")),
			Http.Method.PUT);

		HTTPTestUtil.invokeToJSONObject(
			null,
			String.format(
				"%s/%d/%s/%d", endpoint3,
				manyToOneDepth2JSONObjects[1].getLong("id"),
				objectRelationship2.getName(),
				manyToOneDepth1JSONObjects[1].getLong("id")),
			Http.Method.PUT);

		HTTPTestUtil.customize(
		).withCredentials(
			"test3@liferay.com", "test3"
		).apply(
			() ->
				manyToOneDepth1JSONObjects[0] = HTTPTestUtil.invokeToJSONObject(
					JSONUtil.put(
						OBJECT_FIELD_NAME_2, RandomTestUtil.randomString()
					).toString(),
					endpoint2 + "/by-external-reference-code/ERC2_1",
					Http.Method.PATCH)
		);

		HTTPTestUtil.customize(
		).withCredentials(
			"test2@liferay.com", "test2"
		).apply(
			() ->
				manyToOneDepth1JSONObjects[1] = HTTPTestUtil.invokeToJSONObject(
					JSONUtil.put(
						OBJECT_FIELD_NAME_2, RandomTestUtil.randomString()
					).toString(),
					endpoint2 + "/by-external-reference-code/ERC1_1",
					Http.Method.PATCH)
		);

		HTTPTestUtil.customize(
		).withCredentials(
			"test2@liferay.com", "test2"
		).apply(
			() ->
				manyToOneDepth2JSONObjects[0] = HTTPTestUtil.invokeToJSONObject(
					JSONUtil.put(
						OBJECT_FIELD_NAME_3, RandomTestUtil.randomString()
					).toString(),
					endpoint3 + "/by-external-reference-code/ERC2_2",
					Http.Method.PATCH)
		);

		HTTPTestUtil.customize(
		).withCredentials(
			"test1@liferay.com", "test1"
		).apply(
			() ->
				manyToOneDepth2JSONObjects[1] = HTTPTestUtil.invokeToJSONObject(
					JSONUtil.put(
						OBJECT_FIELD_NAME_3, RandomTestUtil.randomString()
					).toString(),
					endpoint3 + "/by-external-reference-code/ERC1_2",
					Http.Method.PATCH)
		);

		try {

			// Depth 1

			_testSortByFieldName(
				endpoint1, jsonObject3, jsonObject4, jsonObject1, jsonObject2,
				String.format("%s/creator", objectRelationship1.getName()));
			_testSortByFieldName(
				endpoint1, jsonObject3, jsonObject4, jsonObject1, jsonObject2,
				String.format("%s/creatorId", objectRelationship1.getName()));
			_testSortByFieldName(
				endpoint1, jsonObject1, jsonObject2, jsonObject3, jsonObject4,
				String.format("%s/dateCreated", objectRelationship1.getName()));
			_testSortByFieldName(
				endpoint1, jsonObject1, jsonObject2, jsonObject3, jsonObject4,
				String.format(
					"%s/dateModified", objectRelationship1.getName()));
			_testSortByFieldName(
				endpoint1, jsonObject3, jsonObject4, jsonObject1, jsonObject2,
				String.format(
					"%s/externalReferenceCode", objectRelationship1.getName()));
			_testSortByFieldName(
				endpoint1, jsonObject1, jsonObject2, jsonObject3, jsonObject4,
				String.format("%s/id", objectRelationship1.getName()));
			_testSortByFieldName(
				endpoint1, jsonObject3, jsonObject4, jsonObject1, jsonObject2,
				String.format("%s/status", objectRelationship1.getName()));

			// Depth 2

			_testSortByFieldName(
				endpoint1, jsonObject3, jsonObject4, jsonObject1, jsonObject2,
				String.format(
					"%s/%s/creator", objectRelationship1.getName(),
					objectRelationship2.getName()));
			_testSortByFieldName(
				endpoint1, jsonObject3, jsonObject4, jsonObject1, jsonObject2,
				String.format(
					"%s/%s/creatorId", objectRelationship1.getName(),
					objectRelationship2.getName()));
			_testSortByFieldName(
				endpoint1, jsonObject1, jsonObject2, jsonObject3, jsonObject4,
				String.format(
					"%s/%s/dateCreated", objectRelationship1.getName(),
					objectRelationship2.getName()));
			_testSortByFieldName(
				endpoint1, jsonObject1, jsonObject2, jsonObject3, jsonObject4,
				String.format(
					"%s/%s/dateModified", objectRelationship1.getName(),
					objectRelationship2.getName()));
			_testSortByFieldName(
				endpoint1, jsonObject3, jsonObject4, jsonObject1, jsonObject2,
				String.format(
					"%s/%s/externalReferenceCode",
					objectRelationship1.getName(),
					objectRelationship2.getName()));
			_testSortByFieldName(
				endpoint1, jsonObject1, jsonObject2, jsonObject3, jsonObject4,
				String.format(
					"%s/%s/id", objectRelationship1.getName(),
					objectRelationship2.getName()));
			_testSortByFieldName(
				endpoint1, jsonObject3, jsonObject4, jsonObject1, jsonObject2,
				String.format(
					"%s/%s/status", objectRelationship1.getName(),
					objectRelationship2.getName()));
			_testSortByFieldName(
				endpoint1, jsonObject3, jsonObject4, jsonObject1, jsonObject2,
				String.format("%s/userId", objectRelationship1.getName()));

			// Sort by several fields

			_testSortByFieldName(
				endpoint1, jsonObject1, jsonObject2, jsonObject3, jsonObject4,
				String.format("%s/dateCreated", objectRelationship1.getName()),
				String.format("%s/id", objectRelationship1.getName()),
				String.format(
					"%s/%s/dateCreated", objectRelationship1.getName(),
					objectRelationship2.getName()),
				String.format(
					"%s/%s/id", objectRelationship1.getName(),
					objectRelationship2.getName()));
			_testSortByFieldName(
				endpoint1, jsonObject3, jsonObject4, jsonObject1, jsonObject2,
				String.format("%s/creator", objectRelationship1.getName()),
				String.format("%s/creatorId", objectRelationship1.getName()),
				String.format("%s/dateModified", objectRelationship1.getName()),
				String.format(
					"%s/externalReferenceCode", objectRelationship1.getName()),
				String.format("%s/userId", objectRelationship1.getName()),
				String.format(
					"%s/%s/creator", objectRelationship1.getName(),
					objectRelationship2.getName()),
				String.format(
					"%s/%s/creatorId", objectRelationship1.getName(),
					objectRelationship2.getName()),
				String.format(
					"%s/%s/dateModified", objectRelationship1.getName(),
					objectRelationship2.getName()),
				String.format(
					"%s/%s/externalReferenceCode",
					objectRelationship1.getName(),
					objectRelationship2.getName()),
				String.format(
					"%s/%s/userId", objectRelationship1.getName(),
					objectRelationship2.getName()));
		}
		finally {
			if (jsonObject1 != null) {
				objectEntryLocalService.deleteObjectEntry(
					jsonObject1.getLong("id"));
			}

			if (jsonObject2 != null) {
				objectEntryLocalService.deleteObjectEntry(
					jsonObject2.getLong("id"));
			}

			if (jsonObject3 != null) {
				objectEntryLocalService.deleteObjectEntry(
					jsonObject3.getLong("id"));
			}

			if (jsonObject4 != null) {
				objectEntryLocalService.deleteObjectEntry(
					jsonObject4.getLong("id"));
			}

			for (JSONObject jsonObject :
					ArrayUtil.append(
						manyToOneDepth1JSONObjects,
						manyToOneDepth2JSONObjects)) {

				if (jsonObject == null) {
					continue;
				}

				objectEntryLocalService.deleteObjectEntry(
					jsonObject.getLong("id"));
			}
		}
	}

	@Test
	public void testSortByOneToManyRelationshipCustomObjectFields()
		throws Exception {

		objectRelationship1 = ObjectRelationshipTestUtil.addObjectRelationship(
			getObjectDefinition1(), getObjectDefinition2(),
			TestPropsValues.getUserId(),
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);
		objectRelationship2 = ObjectRelationshipTestUtil.addObjectRelationship(
			getObjectDefinition2(), getObjectDefinition3(),
			TestPropsValues.getUserId(),
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);

		String endpoint1 = getEndpoint(getObjectDefinition1(), testGroupId);
		String endpoint2 = getEndpoint(getObjectDefinition2(), testGroupId);
		String endpoint3 = getEndpoint(getObjectDefinition3(), testGroupId);

		BigDecimal randomBigDecimal = new BigDecimal(
			RandomTestUtil.randomDouble());
		Date randomDate1 = RandomTestUtil.nextDate();
		Date randomDate2 = RandomTestUtil.nextDate();
		float randomFloat1 = RandomTestUtil.randomFloat();
		int randomInt = RandomTestUtil.randomInt();
		long randomLong = RandomTestUtil.randomLong(
			ObjectFieldValidationConstants.BUSINESS_TYPE_LONG_VALUE_MIN,
			ObjectFieldValidationConstants.BUSINESS_TYPE_LONG_VALUE_MAX);
		String randomString1 = RandomTestUtil.randomString();
		String randomString2 = RandomTestUtil.randomString();

		JSONObject depth1JSONObject1 = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_BOOLEAN, false
			).put(
				OBJECT_FIELD_NAME_DATE, dateFormat.format(randomDate1)
			).put(
				OBJECT_FIELD_NAME_DATE_TIME_INPUT,
				dateTimeDateFormat.format(randomDate2)
			).put(
				OBJECT_FIELD_NAME_DECIMAL, randomFloat1
			).put(
				OBJECT_FIELD_NAME_INTEGER, randomInt
			).put(
				OBJECT_FIELD_NAME_LONG_INTEGER, randomLong
			).put(
				OBJECT_FIELD_NAME_LONG_TEXT, "a" + randomString1
			).put(
				OBJECT_FIELD_NAME_MULTISELECT_PICKLIST,
				JSONUtil.putAll(LIST_TYPE_ENTRY_KEY_1, LIST_TYPE_ENTRY_KEY_2)
			).put(
				OBJECT_FIELD_NAME_PICKLIST, LIST_TYPE_ENTRY_KEY_1
			).put(
				OBJECT_FIELD_NAME_PRECISION_DECIMAL, randomBigDecimal
			).put(
				OBJECT_FIELD_NAME_TEXT, "a" + randomString2
			).toString(),
			endpoint2, Http.Method.POST);

		JSONObject depth1JSONObject2 = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_BOOLEAN, false
			).put(
				OBJECT_FIELD_NAME_DATE,
				() -> dateFormat.format(
					new Date(randomDate1.getTime() + (2 * 24 * 3600 * 1000)))
			).put(
				OBJECT_FIELD_NAME_DATE_TIME_INPUT,
				dateTimeDateFormat.format(
					new Date(randomDate2.getTime() + 2000))
			).put(
				OBJECT_FIELD_NAME_DECIMAL, randomFloat1 + 2
			).put(
				OBJECT_FIELD_NAME_INTEGER, randomInt + 2
			).put(
				OBJECT_FIELD_NAME_LONG_INTEGER, randomLong + 2
			).put(
				OBJECT_FIELD_NAME_LONG_TEXT, "c" + randomString1
			).put(
				OBJECT_FIELD_NAME_MULTISELECT_PICKLIST,
				JSONUtil.putAll(LIST_TYPE_ENTRY_KEY_1, LIST_TYPE_ENTRY_KEY_2)
			).put(
				OBJECT_FIELD_NAME_PICKLIST, LIST_TYPE_ENTRY_KEY_2
			).put(
				OBJECT_FIELD_NAME_PRECISION_DECIMAL,
				randomBigDecimal.add(new BigDecimal(2))
			).put(
				OBJECT_FIELD_NAME_TEXT, "c" + randomString2
			).toString(),
			endpoint2, Http.Method.POST);

		JSONObject depth1JSONObject3 = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_BOOLEAN, true
			).put(
				OBJECT_FIELD_NAME_DATE,
				() -> dateFormat.format(
					new Date(randomDate1.getTime() + (24 * 3600 * 1000)))
			).put(
				OBJECT_FIELD_NAME_DATE_TIME_INPUT,
				dateTimeDateFormat.format(
					new Date(randomDate2.getTime() + 1000))
			).put(
				OBJECT_FIELD_NAME_DECIMAL, randomFloat1 + 1
			).put(
				OBJECT_FIELD_NAME_INTEGER, randomInt + 1
			).put(
				OBJECT_FIELD_NAME_LONG_INTEGER, randomLong + 1
			).put(
				OBJECT_FIELD_NAME_LONG_TEXT, "b" + randomString1
			).put(
				OBJECT_FIELD_NAME_MULTISELECT_PICKLIST,
				JSONUtil.putAll(LIST_TYPE_ENTRY_KEY_2, LIST_TYPE_ENTRY_KEY_3)
			).put(
				OBJECT_FIELD_NAME_PICKLIST, LIST_TYPE_ENTRY_KEY_2
			).put(
				OBJECT_FIELD_NAME_PRECISION_DECIMAL,
				randomBigDecimal.add(BigDecimal.ONE)
			).put(
				OBJECT_FIELD_NAME_TEXT, "b" + randomString2
			).toString(),
			endpoint2, Http.Method.POST);

		JSONObject depth1JSONObject4 = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_BOOLEAN, true
			).put(
				OBJECT_FIELD_NAME_DATE,
				() -> dateFormat.format(
					new Date(randomDate1.getTime() + (3 * 24 * 3600 * 1000)))
			).put(
				OBJECT_FIELD_NAME_DATE_TIME_INPUT,
				dateTimeDateFormat.format(
					new Date(randomDate2.getTime() + 3000))
			).put(
				OBJECT_FIELD_NAME_DECIMAL, randomFloat1 + 3
			).put(
				OBJECT_FIELD_NAME_INTEGER, randomInt + 3
			).put(
				OBJECT_FIELD_NAME_LONG_INTEGER, randomLong + 3
			).put(
				OBJECT_FIELD_NAME_LONG_TEXT, "d" + randomString1
			).put(
				OBJECT_FIELD_NAME_MULTISELECT_PICKLIST,
				JSONUtil.putAll(LIST_TYPE_ENTRY_KEY_2, LIST_TYPE_ENTRY_KEY_3)
			).put(
				OBJECT_FIELD_NAME_PICKLIST, LIST_TYPE_ENTRY_KEY_3
			).put(
				OBJECT_FIELD_NAME_PRECISION_DECIMAL,
				randomBigDecimal.add(new BigDecimal(3))
			).put(
				OBJECT_FIELD_NAME_TEXT, "d" + randomString2
			).toString(),
			endpoint2, Http.Method.POST);

		JSONObject depth2JSONObject1 = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_BOOLEAN, false
			).put(
				OBJECT_FIELD_NAME_DATE, dateFormat.format(randomDate1)
			).put(
				OBJECT_FIELD_NAME_DATE_TIME_INPUT,
				dateTimeDateFormat.format(randomDate2)
			).put(
				OBJECT_FIELD_NAME_DECIMAL, randomFloat1
			).put(
				OBJECT_FIELD_NAME_INTEGER, randomInt
			).put(
				OBJECT_FIELD_NAME_LONG_INTEGER, randomLong
			).put(
				OBJECT_FIELD_NAME_LONG_TEXT, "a" + randomString1
			).put(
				OBJECT_FIELD_NAME_MULTISELECT_PICKLIST,
				JSONUtil.putAll(LIST_TYPE_ENTRY_KEY_1, LIST_TYPE_ENTRY_KEY_2)
			).put(
				OBJECT_FIELD_NAME_PICKLIST, LIST_TYPE_ENTRY_KEY_1
			).put(
				OBJECT_FIELD_NAME_PRECISION_DECIMAL, randomBigDecimal
			).put(
				OBJECT_FIELD_NAME_TEXT, "a" + randomString2
			).toString(),
			endpoint3, Http.Method.POST);

		JSONObject depth2JSONObject2 = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_BOOLEAN, false
			).put(
				OBJECT_FIELD_NAME_DATE,
				() -> dateFormat.format(
					new Date(randomDate1.getTime() + (2 * 24 * 3600 * 1000)))
			).put(
				OBJECT_FIELD_NAME_DATE_TIME_INPUT,
				dateTimeDateFormat.format(
					new Date(randomDate2.getTime() + 2000))
			).put(
				OBJECT_FIELD_NAME_DECIMAL, randomFloat1 + 2
			).put(
				OBJECT_FIELD_NAME_INTEGER, randomInt + 2
			).put(
				OBJECT_FIELD_NAME_LONG_INTEGER, randomLong + 2
			).put(
				OBJECT_FIELD_NAME_LONG_TEXT, "c" + randomString1
			).put(
				OBJECT_FIELD_NAME_MULTISELECT_PICKLIST,
				JSONUtil.putAll(LIST_TYPE_ENTRY_KEY_1, LIST_TYPE_ENTRY_KEY_2)
			).put(
				OBJECT_FIELD_NAME_PICKLIST, LIST_TYPE_ENTRY_KEY_2
			).put(
				OBJECT_FIELD_NAME_PRECISION_DECIMAL,
				randomBigDecimal.add(new BigDecimal(2))
			).put(
				OBJECT_FIELD_NAME_TEXT, "c" + randomString2
			).toString(),
			endpoint3, Http.Method.POST);

		JSONObject depth2JSONObject3 = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_BOOLEAN, true
			).put(
				OBJECT_FIELD_NAME_DATE,
				() -> dateFormat.format(
					new Date(randomDate1.getTime() + (24 * 3600 * 1000)))
			).put(
				OBJECT_FIELD_NAME_DATE_TIME_INPUT,
				dateTimeDateFormat.format(
					new Date(randomDate2.getTime() + 1000))
			).put(
				OBJECT_FIELD_NAME_DECIMAL, randomFloat1 + 1
			).put(
				OBJECT_FIELD_NAME_INTEGER, randomInt + 1
			).put(
				OBJECT_FIELD_NAME_LONG_INTEGER, randomLong + 1
			).put(
				OBJECT_FIELD_NAME_LONG_TEXT, "b" + randomString1
			).put(
				OBJECT_FIELD_NAME_MULTISELECT_PICKLIST,
				JSONUtil.putAll(LIST_TYPE_ENTRY_KEY_2, LIST_TYPE_ENTRY_KEY_3)
			).put(
				OBJECT_FIELD_NAME_PICKLIST, LIST_TYPE_ENTRY_KEY_2
			).put(
				OBJECT_FIELD_NAME_PRECISION_DECIMAL,
				randomBigDecimal.add(BigDecimal.ONE)
			).put(
				OBJECT_FIELD_NAME_TEXT, "b" + randomString2
			).toString(),
			endpoint3, Http.Method.POST);

		JSONObject depth2JSONObject4 = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_BOOLEAN, true
			).put(
				OBJECT_FIELD_NAME_DATE,
				() -> dateFormat.format(
					new Date(randomDate1.getTime() + (3 * 24 * 3600 * 1000)))
			).put(
				OBJECT_FIELD_NAME_DATE_TIME_INPUT,
				dateTimeDateFormat.format(
					new Date(randomDate2.getTime() + 3000))
			).put(
				OBJECT_FIELD_NAME_DECIMAL, randomFloat1 + 3
			).put(
				OBJECT_FIELD_NAME_INTEGER, randomInt + 3
			).put(
				OBJECT_FIELD_NAME_LONG_INTEGER, randomLong + 3
			).put(
				OBJECT_FIELD_NAME_LONG_TEXT, "d" + randomString1
			).put(
				OBJECT_FIELD_NAME_MULTISELECT_PICKLIST,
				JSONUtil.putAll(LIST_TYPE_ENTRY_KEY_2, LIST_TYPE_ENTRY_KEY_3)
			).put(
				OBJECT_FIELD_NAME_PICKLIST, LIST_TYPE_ENTRY_KEY_3
			).put(
				OBJECT_FIELD_NAME_PRECISION_DECIMAL,
				randomBigDecimal.add(new BigDecimal(3))
			).put(
				OBJECT_FIELD_NAME_TEXT, "d" + randomString2
			).toString(),
			endpoint3, Http.Method.POST);

		JSONObject jsonObject1 = HTTPTestUtil.invokeToJSONObject(
			JSONFactoryUtil.getNullJSON(), endpoint1, Http.Method.POST);

		JSONObject jsonObject2 = HTTPTestUtil.invokeToJSONObject(
			JSONFactoryUtil.getNullJSON(), endpoint1, Http.Method.POST);

		HTTPTestUtil.invokeToJSONObject(
			null,
			String.format(
				"%s/%d/%s/%d", endpoint1, jsonObject1.getLong("id"),
				objectRelationship1.getName(), depth1JSONObject1.getLong("id")),
			Http.Method.PUT);

		HTTPTestUtil.invokeToJSONObject(
			null,
			String.format(
				"%s/%d/%s/%d", endpoint1, jsonObject1.getLong("id"),
				objectRelationship1.getName(), depth1JSONObject2.getLong("id")),
			Http.Method.PUT);

		HTTPTestUtil.invokeToJSONObject(
			null,
			String.format(
				"%s/%d/%s/%d", endpoint1, jsonObject2.getLong("id"),
				objectRelationship1.getName(), depth1JSONObject3.getLong("id")),
			Http.Method.PUT);

		HTTPTestUtil.invokeToJSONObject(
			null,
			String.format(
				"%s/%d/%s/%d", endpoint1, jsonObject2.getLong("id"),
				objectRelationship1.getName(), depth1JSONObject4.getLong("id")),
			Http.Method.PUT);

		HTTPTestUtil.invokeToJSONObject(
			null,
			String.format(
				"%s/%d/%s/%d", endpoint2, depth1JSONObject1.getLong("id"),
				objectRelationship2.getName(), depth2JSONObject1.getLong("id")),
			Http.Method.PUT);

		HTTPTestUtil.invokeToJSONObject(
			null,
			String.format(
				"%s/%d/%s/%d", endpoint2, depth1JSONObject2.getLong("id"),
				objectRelationship2.getName(), depth2JSONObject2.getLong("id")),
			Http.Method.PUT);

		HTTPTestUtil.invokeToJSONObject(
			null,
			String.format(
				"%s/%d/%s/%d", endpoint2, depth1JSONObject3.getLong("id"),
				objectRelationship2.getName(), depth2JSONObject3.getLong("id")),
			Http.Method.PUT);

		HTTPTestUtil.invokeToJSONObject(
			null,
			String.format(
				"%s/%d/%s/%d", endpoint2, depth1JSONObject4.getLong("id"),
				objectRelationship2.getName(), depth2JSONObject4.getLong("id")),
			Http.Method.PUT);

		try {

			// Depth 1

			_testSortByOneToManyRelationshipCustomObjectFields(
				endpoint1, endpoint2, jsonObject1, jsonObject2,
				depth1JSONObject1, depth1JSONObject2, depth1JSONObject3,
				depth1JSONObject4,
				String.format(
					"%s/%s", objectRelationship1.getName(),
					OBJECT_FIELD_NAME_BOOLEAN));
			_testSortByOneToManyRelationshipCustomObjectFields(
				endpoint1, endpoint2, jsonObject1, jsonObject2,
				depth1JSONObject1, depth1JSONObject2, depth1JSONObject3,
				depth1JSONObject4,
				String.format(
					"%s/%s", objectRelationship1.getName(),
					OBJECT_FIELD_NAME_DATE));
			_testSortByOneToManyRelationshipCustomObjectFields(
				endpoint1, endpoint2, jsonObject1, jsonObject2,
				depth1JSONObject1, depth1JSONObject2, depth1JSONObject3,
				depth1JSONObject4,
				String.format(
					"%s/%s", objectRelationship1.getName(),
					OBJECT_FIELD_NAME_DATE_TIME_INPUT));
			_testSortByOneToManyRelationshipCustomObjectFields(
				endpoint1, endpoint2, jsonObject1, jsonObject2,
				depth1JSONObject1, depth1JSONObject2, depth1JSONObject3,
				depth1JSONObject4,
				String.format(
					"%s/%s", objectRelationship1.getName(),
					OBJECT_FIELD_NAME_DECIMAL));
			_testSortByOneToManyRelationshipCustomObjectFields(
				endpoint1, endpoint2, jsonObject1, jsonObject2,
				depth1JSONObject1, depth1JSONObject2, depth1JSONObject3,
				depth1JSONObject4,
				String.format(
					"%s/%s", objectRelationship1.getName(),
					OBJECT_FIELD_NAME_INTEGER));
			_testSortByOneToManyRelationshipCustomObjectFields(
				endpoint1, endpoint2, jsonObject1, jsonObject2,
				depth1JSONObject1, depth1JSONObject2, depth1JSONObject3,
				depth1JSONObject4,
				String.format(
					"%s/%s", objectRelationship1.getName(),
					OBJECT_FIELD_NAME_LONG_INTEGER));
			_testSortByOneToManyRelationshipCustomObjectFields(
				endpoint1, endpoint2, jsonObject1, jsonObject2,
				depth1JSONObject1, depth1JSONObject2, depth1JSONObject3,
				depth1JSONObject4,
				String.format(
					"%s/%s", objectRelationship1.getName(),
					OBJECT_FIELD_NAME_LONG_TEXT));
			_testSortByOneToManyRelationshipCustomObjectFields(
				endpoint1, endpoint2, jsonObject1, jsonObject2,
				depth1JSONObject1, depth1JSONObject2, depth1JSONObject3,
				depth1JSONObject4,
				String.format(
					"%s/%s", objectRelationship1.getName(),
					OBJECT_FIELD_NAME_MULTISELECT_PICKLIST));
			_testSortByOneToManyRelationshipCustomObjectFields(
				endpoint1, endpoint2, jsonObject1, jsonObject2,
				depth1JSONObject1, depth1JSONObject2, depth1JSONObject3,
				depth1JSONObject4,
				String.format(
					"%s/%s", objectRelationship1.getName(),
					OBJECT_FIELD_NAME_PICKLIST));
			_testSortByOneToManyRelationshipCustomObjectFields(
				endpoint1, endpoint2, jsonObject1, jsonObject2,
				depth1JSONObject1, depth1JSONObject2, depth1JSONObject3,
				depth1JSONObject4,
				String.format(
					"%s/%s", objectRelationship1.getName(),
					OBJECT_FIELD_NAME_PRECISION_DECIMAL));
			_testSortByOneToManyRelationshipCustomObjectFields(
				endpoint1, endpoint2, jsonObject1, jsonObject2,
				depth1JSONObject1, depth1JSONObject2, depth1JSONObject3,
				depth1JSONObject4,
				String.format(
					"%s/%s", objectRelationship1.getName(),
					OBJECT_FIELD_NAME_TEXT));

			// Depth 2

			_testSortByOneToManyRelationshipCustomObjectFields(
				endpoint1, endpoint3, jsonObject1, jsonObject2,
				depth2JSONObject1, depth2JSONObject2, depth2JSONObject3,
				depth2JSONObject4,
				String.format(
					"%s/%s/%s", objectRelationship1.getName(),
					objectRelationship2.getName(), OBJECT_FIELD_NAME_BOOLEAN));
			_testSortByOneToManyRelationshipCustomObjectFields(
				endpoint1, endpoint3, jsonObject1, jsonObject2,
				depth2JSONObject1, depth2JSONObject2, depth2JSONObject3,
				depth2JSONObject4,
				String.format(
					"%s/%s/%s", objectRelationship1.getName(),
					objectRelationship2.getName(), OBJECT_FIELD_NAME_DATE));
			_testSortByOneToManyRelationshipCustomObjectFields(
				endpoint1, endpoint3, jsonObject1, jsonObject2,
				depth2JSONObject1, depth2JSONObject2, depth2JSONObject3,
				depth2JSONObject4,
				String.format(
					"%s/%s/%s", objectRelationship1.getName(),
					objectRelationship2.getName(),
					OBJECT_FIELD_NAME_DATE_TIME_INPUT));
			_testSortByOneToManyRelationshipCustomObjectFields(
				endpoint1, endpoint3, jsonObject1, jsonObject2,
				depth2JSONObject1, depth2JSONObject2, depth2JSONObject3,
				depth2JSONObject4,
				String.format(
					"%s/%s/%s", objectRelationship1.getName(),
					objectRelationship2.getName(), OBJECT_FIELD_NAME_DECIMAL));
			_testSortByOneToManyRelationshipCustomObjectFields(
				endpoint1, endpoint3, jsonObject1, jsonObject2,
				depth2JSONObject1, depth2JSONObject2, depth2JSONObject3,
				depth2JSONObject4,
				String.format(
					"%s/%s/%s", objectRelationship1.getName(),
					objectRelationship2.getName(), OBJECT_FIELD_NAME_INTEGER));
			_testSortByOneToManyRelationshipCustomObjectFields(
				endpoint1, endpoint3, jsonObject1, jsonObject2,
				depth2JSONObject1, depth2JSONObject2, depth2JSONObject3,
				depth2JSONObject4,
				String.format(
					"%s/%s/%s", objectRelationship1.getName(),
					objectRelationship2.getName(),
					OBJECT_FIELD_NAME_LONG_INTEGER));
			_testSortByOneToManyRelationshipCustomObjectFields(
				endpoint1, endpoint3, jsonObject1, jsonObject2,
				depth2JSONObject1, depth2JSONObject2, depth2JSONObject3,
				depth2JSONObject4,
				String.format(
					"%s/%s/%s", objectRelationship1.getName(),
					objectRelationship2.getName(),
					OBJECT_FIELD_NAME_LONG_TEXT));
			_testSortByOneToManyRelationshipCustomObjectFields(
				endpoint1, endpoint3, jsonObject1, jsonObject2,
				depth2JSONObject1, depth2JSONObject2, depth2JSONObject3,
				depth2JSONObject4,
				String.format(
					"%s/%s/%s", objectRelationship1.getName(),
					objectRelationship2.getName(),
					OBJECT_FIELD_NAME_MULTISELECT_PICKLIST));
			_testSortByOneToManyRelationshipCustomObjectFields(
				endpoint1, endpoint3, jsonObject1, jsonObject2,
				depth2JSONObject1, depth2JSONObject2, depth2JSONObject3,
				depth2JSONObject4,
				String.format(
					"%s/%s/%s", objectRelationship1.getName(),
					objectRelationship2.getName(), OBJECT_FIELD_NAME_PICKLIST));
			_testSortByOneToManyRelationshipCustomObjectFields(
				endpoint1, endpoint3, jsonObject1, jsonObject2,
				depth2JSONObject1, depth2JSONObject2, depth2JSONObject3,
				depth2JSONObject4,
				String.format(
					"%s/%s/%s", objectRelationship1.getName(),
					objectRelationship2.getName(),
					OBJECT_FIELD_NAME_PRECISION_DECIMAL));
			_testSortByOneToManyRelationshipCustomObjectFields(
				endpoint1, endpoint3, jsonObject1, jsonObject2,
				depth2JSONObject1, depth2JSONObject2, depth2JSONObject3,
				depth2JSONObject4,
				String.format(
					"%s/%s/%s", objectRelationship1.getName(),
					objectRelationship2.getName(), OBJECT_FIELD_NAME_TEXT));

			// Sort by several fields

			_testSortByOneToManyRelationshipCustomObjectFields(
				endpoint1, endpoint2, jsonObject1, jsonObject2,
				depth1JSONObject1, depth1JSONObject2, depth1JSONObject3,
				depth1JSONObject4,
				String.format(
					"%s/%s", objectRelationship1.getName(),
					OBJECT_FIELD_NAME_BOOLEAN),
				String.format(
					"%s/%s", objectRelationship1.getName(),
					OBJECT_FIELD_NAME_DATE),
				String.format(
					"%s/%s", objectRelationship1.getName(),
					OBJECT_FIELD_NAME_DATE_TIME_INPUT),
				String.format(
					"%s/%s", objectRelationship1.getName(),
					OBJECT_FIELD_NAME_DECIMAL),
				String.format(
					"%s/%s", objectRelationship1.getName(),
					OBJECT_FIELD_NAME_INTEGER),
				String.format(
					"%s/%s", objectRelationship1.getName(),
					OBJECT_FIELD_NAME_LONG_INTEGER),
				String.format(
					"%s/%s", objectRelationship1.getName(),
					OBJECT_FIELD_NAME_LONG_TEXT),
				String.format(
					"%s/%s", objectRelationship1.getName(),
					OBJECT_FIELD_NAME_MULTISELECT_PICKLIST),
				String.format(
					"%s/%s", objectRelationship1.getName(),
					OBJECT_FIELD_NAME_PICKLIST),
				String.format(
					"%s/%s", objectRelationship1.getName(),
					OBJECT_FIELD_NAME_PRECISION_DECIMAL),
				String.format(
					"%s/%s", objectRelationship1.getName(),
					OBJECT_FIELD_NAME_TEXT));

			_testSortByOneToManyRelationshipCustomObjectFields(
				endpoint1, endpoint3, jsonObject1, jsonObject2,
				depth2JSONObject1, depth2JSONObject2, depth2JSONObject3,
				depth2JSONObject4,
				String.format(
					"%s/%s/%s", objectRelationship1.getName(),
					objectRelationship2.getName(), OBJECT_FIELD_NAME_BOOLEAN),
				String.format(
					"%s/%s/%s", objectRelationship1.getName(),
					objectRelationship2.getName(), OBJECT_FIELD_NAME_DATE),
				String.format(
					"%s/%s/%s", objectRelationship1.getName(),
					objectRelationship2.getName(),
					OBJECT_FIELD_NAME_DATE_TIME_INPUT),
				String.format(
					"%s/%s/%s", objectRelationship1.getName(),
					objectRelationship2.getName(), OBJECT_FIELD_NAME_DECIMAL),
				String.format(
					"%s/%s/%s", objectRelationship1.getName(),
					objectRelationship2.getName(), OBJECT_FIELD_NAME_INTEGER),
				String.format(
					"%s/%s/%s", objectRelationship1.getName(),
					objectRelationship2.getName(),
					OBJECT_FIELD_NAME_LONG_INTEGER),
				String.format(
					"%s/%s/%s", objectRelationship1.getName(),
					objectRelationship2.getName(), OBJECT_FIELD_NAME_LONG_TEXT),
				String.format(
					"%s/%s/%s", objectRelationship1.getName(),
					objectRelationship2.getName(),
					OBJECT_FIELD_NAME_MULTISELECT_PICKLIST),
				String.format(
					"%s/%s/%s", objectRelationship1.getName(),
					objectRelationship2.getName(), OBJECT_FIELD_NAME_PICKLIST),
				String.format(
					"%s/%s/%s", objectRelationship1.getName(),
					objectRelationship2.getName(),
					OBJECT_FIELD_NAME_PRECISION_DECIMAL),
				String.format(
					"%s/%s/%s", objectRelationship1.getName(),
					objectRelationship2.getName(), OBJECT_FIELD_NAME_TEXT));
		}
		finally {
			if (depth2JSONObject1 != null) {
				objectEntryLocalService.deleteObjectEntry(
					depth2JSONObject1.getLong("id"));
			}

			if (depth2JSONObject2 != null) {
				objectEntryLocalService.deleteObjectEntry(
					depth2JSONObject2.getLong("id"));
			}

			if (depth2JSONObject3 != null) {
				objectEntryLocalService.deleteObjectEntry(
					depth2JSONObject3.getLong("id"));
			}

			if (depth2JSONObject4 != null) {
				objectEntryLocalService.deleteObjectEntry(
					depth2JSONObject4.getLong("id"));
			}

			if (depth1JSONObject1 != null) {
				objectEntryLocalService.deleteObjectEntry(
					depth1JSONObject1.getLong("id"));
			}

			if (depth1JSONObject2 != null) {
				objectEntryLocalService.deleteObjectEntry(
					depth1JSONObject2.getLong("id"));
			}

			if (depth1JSONObject3 != null) {
				objectEntryLocalService.deleteObjectEntry(
					depth1JSONObject3.getLong("id"));
			}

			if (depth1JSONObject4 != null) {
				objectEntryLocalService.deleteObjectEntry(
					depth1JSONObject4.getLong("id"));
			}

			if (jsonObject1 != null) {
				objectEntryLocalService.deleteObjectEntry(
					jsonObject1.getLong("id"));
			}

			if (jsonObject2 != null) {
				objectEntryLocalService.deleteObjectEntry(
					jsonObject2.getLong("id"));
			}
		}
	}

	@Test
	public void testSortByOneToManyRelationshipSystemObjectFields()
		throws Exception {

		objectRelationship1 = ObjectRelationshipTestUtil.addObjectRelationship(
			getObjectDefinition1(), getObjectDefinition2(),
			TestPropsValues.getUserId(),
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);
		objectRelationship2 = ObjectRelationshipTestUtil.addObjectRelationship(
			getObjectDefinition2(), getObjectDefinition3(),
			TestPropsValues.getUserId(),
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);

		Role role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		addResourcePermission(
			ObjectActionKeys.ADD_OBJECT_ENTRY,
			getObjectDefinition2().getResourceName(), role);
		addResourcePermission(
			ObjectActionKeys.ADD_OBJECT_ENTRY,
			getObjectDefinition3().getResourceName(), role);
		addResourcePermission(
			ActionKeys.VIEW, getObjectDefinition1().getClassName(), role);

		User user1 = addUser("test1", "test1");
		User user2 = addUser("test2", "test2");
		User user3 = addUser("test3", "test3");

		roleLocalService.addUserRole(user1.getUserId(), role.getRoleId());
		roleLocalService.addUserRole(user2.getUserId(), role.getRoleId());
		roleLocalService.addUserRole(user3.getUserId(), role.getRoleId());

		getObjectDefinition1().setEnableObjectEntryDraft(true);

		getObjectDefinition2().setEnableObjectEntryDraft(true);

		getObjectDefinition3().setEnableObjectEntryDraft(true);

		objectDefinition1 = objectDefinitionLocalService.updateObjectDefinition(
			getObjectDefinition1());
		objectDefinition2 = objectDefinitionLocalService.updateObjectDefinition(
			getObjectDefinition2());
		objectDefinition3 = objectDefinitionLocalService.updateObjectDefinition(
			getObjectDefinition3());

		String endpoint1 = getEndpoint(getObjectDefinition1(), testGroupId);
		String endpoint2 = getEndpoint(getObjectDefinition2(), testGroupId);
		String endpoint3 = getEndpoint(getObjectDefinition3(), testGroupId);

		JSONObject[] oneToManyDepth1JSONObjects = new JSONObject[4];

		HTTPTestUtil.customize(
		).withCredentials(
			"test3@liferay.com", "test3"
		).apply(
			() ->
				oneToManyDepth1JSONObjects[0] = HTTPTestUtil.invokeToJSONObject(
					JSONUtil.put(
						"externalReferenceCode", "ERC4"
					).put(
						"status",
						JSONUtil.put("code", WorkflowConstants.STATUS_DRAFT)
					).toString(),
					endpoint2, Http.Method.POST)
		);

		HTTPTestUtil.customize(
		).withCredentials(
			"test2@liferay.com", "test2"
		).apply(
			() ->
				oneToManyDepth1JSONObjects[1] = HTTPTestUtil.invokeToJSONObject(
					JSONUtil.put(
						"externalReferenceCode", "ERC2"
					).put(
						"status",
						JSONUtil.put("code", WorkflowConstants.STATUS_DRAFT)
					).toString(),
					endpoint2, Http.Method.POST)
		);

		HTTPTestUtil.customize(
		).withCredentials(
			"test2@liferay.com", "test2"
		).apply(
			() ->
				oneToManyDepth1JSONObjects[2] = HTTPTestUtil.invokeToJSONObject(
					JSONUtil.put(
						"externalReferenceCode", "ERC3"
					).toString(),
					endpoint2, Http.Method.POST)
		);

		HTTPTestUtil.customize(
		).withCredentials(
			"test1@liferay.com", "test1"
		).apply(
			() ->
				oneToManyDepth1JSONObjects[3] = HTTPTestUtil.invokeToJSONObject(
					JSONUtil.put(
						"externalReferenceCode", "ERC1"
					).toString(),
					endpoint2, Http.Method.POST)
		);

		JSONObject[] oneToManyDepth2JSONObjects = new JSONObject[4];

		HTTPTestUtil.customize(
		).withCredentials(
			"test3@liferay.com", "test3"
		).apply(
			() ->
				oneToManyDepth2JSONObjects[0] = HTTPTestUtil.invokeToJSONObject(
					JSONUtil.put(
						"externalReferenceCode", "ERC4_2"
					).put(
						"status",
						JSONUtil.put("code", WorkflowConstants.STATUS_DRAFT)
					).toString(),
					endpoint3, Http.Method.POST)
		);

		HTTPTestUtil.customize(
		).withCredentials(
			"test2@liferay.com", "test2"
		).apply(
			() ->
				oneToManyDepth2JSONObjects[1] = HTTPTestUtil.invokeToJSONObject(
					JSONUtil.put(
						"externalReferenceCode", "ERC2_2"
					).put(
						"status",
						JSONUtil.put("code", WorkflowConstants.STATUS_DRAFT)
					).toString(),
					endpoint3, Http.Method.POST)
		);

		HTTPTestUtil.customize(
		).withCredentials(
			"test2@liferay.com", "test2"
		).apply(
			() ->
				oneToManyDepth2JSONObjects[2] = HTTPTestUtil.invokeToJSONObject(
					JSONUtil.put(
						"externalReferenceCode", "ERC3_2"
					).toString(),
					endpoint3, Http.Method.POST)
		);

		HTTPTestUtil.customize(
		).withCredentials(
			"test1@liferay.com", "test1"
		).apply(
			() ->
				oneToManyDepth2JSONObjects[3] = HTTPTestUtil.invokeToJSONObject(
					JSONUtil.put(
						"externalReferenceCode", "ERC1_2"
					).toString(),
					endpoint3, Http.Method.POST)
		);

		JSONObject jsonObject1 = HTTPTestUtil.invokeToJSONObject(
			JSONFactoryUtil.getNullJSON(), endpoint1, Http.Method.POST);

		JSONObject jsonObject2 = HTTPTestUtil.invokeToJSONObject(
			JSONFactoryUtil.getNullJSON(), endpoint1, Http.Method.POST);

		HTTPTestUtil.invokeToJSONObject(
			null,
			String.format(
				"%s/%d/%s/%d", endpoint1, jsonObject1.getLong("id"),
				objectRelationship1.getName(),
				oneToManyDepth1JSONObjects[0].getLong("id")),
			Http.Method.PUT);

		HTTPTestUtil.invokeToJSONObject(
			null,
			String.format(
				"%s/%d/%s/%d", endpoint1, jsonObject1.getLong("id"),
				objectRelationship1.getName(),
				oneToManyDepth1JSONObjects[1].getLong("id")),
			Http.Method.PUT);

		HTTPTestUtil.invokeToJSONObject(
			null,
			String.format(
				"%s/%d/%s/%d", endpoint1, jsonObject2.getLong("id"),
				objectRelationship1.getName(),
				oneToManyDepth1JSONObjects[2].getLong("id")),
			Http.Method.PUT);

		HTTPTestUtil.invokeToJSONObject(
			null,
			String.format(
				"%s/%d/%s/%d", endpoint1, jsonObject2.getLong("id"),
				objectRelationship1.getName(),
				oneToManyDepth1JSONObjects[3].getLong("id")),
			Http.Method.PUT);

		HTTPTestUtil.invokeToJSONObject(
			null,
			String.format(
				"%s/%d/%s/%d", endpoint2,
				oneToManyDepth1JSONObjects[0].getLong("id"),
				objectRelationship2.getName(),
				oneToManyDepth2JSONObjects[0].getLong("id")),
			Http.Method.PUT);

		HTTPTestUtil.invokeToJSONObject(
			null,
			String.format(
				"%s/%d/%s/%d", endpoint2,
				oneToManyDepth1JSONObjects[1].getLong("id"),
				objectRelationship2.getName(),
				oneToManyDepth2JSONObjects[1].getLong("id")),
			Http.Method.PUT);

		HTTPTestUtil.invokeToJSONObject(
			null,
			String.format(
				"%s/%d/%s/%d", endpoint2,
				oneToManyDepth1JSONObjects[2].getLong("id"),
				objectRelationship2.getName(),
				oneToManyDepth2JSONObjects[2].getLong("id")),
			Http.Method.PUT);

		HTTPTestUtil.invokeToJSONObject(
			null,
			String.format(
				"%s/%d/%s/%d", endpoint2,
				oneToManyDepth1JSONObjects[3].getLong("id"),
				objectRelationship2.getName(),
				oneToManyDepth2JSONObjects[3].getLong("id")),
			Http.Method.PUT);

		HTTPTestUtil.customize(
		).withCredentials(
			"test3@liferay.com", "test3"
		).apply(
			() ->
				oneToManyDepth1JSONObjects[0] = HTTPTestUtil.invokeToJSONObject(
					JSONUtil.put(
						OBJECT_FIELD_NAME_2, RandomTestUtil.randomString()
					).toString(),
					endpoint2 + "/by-external-reference-code/ERC4",
					Http.Method.PATCH)
		);

		HTTPTestUtil.customize(
		).withCredentials(
			"test2@liferay.com", "test2"
		).apply(
			() ->
				oneToManyDepth1JSONObjects[1] = HTTPTestUtil.invokeToJSONObject(
					JSONUtil.put(
						OBJECT_FIELD_NAME_2, RandomTestUtil.randomString()
					).toString(),
					endpoint2 + "/by-external-reference-code/ERC2",
					Http.Method.PATCH)
		);

		HTTPTestUtil.customize(
		).withCredentials(
			"test3@liferay.com", "test3"
		).apply(
			() ->
				oneToManyDepth2JSONObjects[0] = HTTPTestUtil.invokeToJSONObject(
					JSONUtil.put(
						OBJECT_FIELD_NAME_3, RandomTestUtil.randomString()
					).toString(),
					endpoint3 + "/by-external-reference-code/ERC4_2",
					Http.Method.PATCH)
		);

		HTTPTestUtil.customize(
		).withCredentials(
			"test2@liferay.com", "test2"
		).apply(
			() ->
				oneToManyDepth2JSONObjects[1] = HTTPTestUtil.invokeToJSONObject(
					JSONUtil.put(
						OBJECT_FIELD_NAME_3, RandomTestUtil.randomString()
					).toString(),
					endpoint3 + "/by-external-reference-code/ERC2_2",
					Http.Method.PATCH)
		);

		try {

			// Depth 1

			_testSortByFieldName(
				endpoint1, jsonObject2, jsonObject1,
				String.format("%s/creator", objectRelationship1.getName()));
			_testSortByFieldName(
				endpoint1, jsonObject2, jsonObject1,
				String.format("%s/creatorId", objectRelationship1.getName()));
			_testSortByFieldName(
				endpoint1, jsonObject1, jsonObject2,
				String.format("%s/dateCreated", objectRelationship1.getName()));
			_testSortByFieldName(
				endpoint1, jsonObject2, jsonObject1,
				String.format(
					"%s/dateModified", objectRelationship1.getName()));
			_testSortByFieldName(
				endpoint1, jsonObject2, jsonObject1,
				String.format(
					"%s/externalReferenceCode", objectRelationship1.getName()));
			_testSortByFieldName(
				endpoint1, jsonObject1, jsonObject2,
				String.format("%s/id", objectRelationship1.getName()));
			_testSortByFieldName(
				endpoint1, jsonObject2, jsonObject1,
				String.format("%s/status", objectRelationship1.getName()));

			// Depth 2

			_testSortByFieldName(
				endpoint1, jsonObject2, jsonObject1,
				String.format(
					"%s/%s/creator", objectRelationship1.getName(),
					objectRelationship2.getName()));
			_testSortByFieldName(
				endpoint1, jsonObject2, jsonObject1,
				String.format(
					"%s/%s/creatorId", objectRelationship1.getName(),
					objectRelationship2.getName()));
			_testSortByFieldName(
				endpoint1, jsonObject1, jsonObject2,
				String.format(
					"%s/%s/dateCreated", objectRelationship1.getName(),
					objectRelationship2.getName()));
			_testSortByFieldName(
				endpoint1, jsonObject2, jsonObject1,
				String.format(
					"%s/%s/dateModified", objectRelationship1.getName(),
					objectRelationship2.getName()));
			_testSortByFieldName(
				endpoint1, jsonObject2, jsonObject1,
				String.format(
					"%s/%s/externalReferenceCode",
					objectRelationship1.getName(),
					objectRelationship2.getName()));
			_testSortByFieldName(
				endpoint1, jsonObject1, jsonObject2,
				String.format(
					"%s/%s/id", objectRelationship1.getName(),
					objectRelationship2.getName()));
			_testSortByFieldName(
				endpoint1, jsonObject2, jsonObject1,
				String.format(
					"%s/%s/status", objectRelationship1.getName(),
					objectRelationship2.getName()));
			_testSortByFieldName(
				endpoint1, jsonObject2, jsonObject1,
				String.format("%s/userId", objectRelationship1.getName()));

			// Sort by several fields

			_testSortByFieldName(
				endpoint1, jsonObject1, jsonObject2,
				String.format("%s/dateCreated", objectRelationship1.getName()),
				String.format("%s/id", objectRelationship1.getName()),
				String.format(
					"%s/%s/dateCreated", objectRelationship1.getName(),
					objectRelationship2.getName()),
				String.format(
					"%s/%s/id", objectRelationship1.getName(),
					objectRelationship2.getName()));
			_testSortByFieldName(
				endpoint1, jsonObject2, jsonObject1,
				String.format("%s/creator", objectRelationship1.getName()),
				String.format("%s/creatorId", objectRelationship1.getName()),
				String.format("%s/dateModified", objectRelationship1.getName()),
				String.format(
					"%s/externalReferenceCode", objectRelationship1.getName()),
				String.format("%s/userId", objectRelationship1.getName()),
				String.format(
					"%s/%s/creator", objectRelationship1.getName(),
					objectRelationship2.getName()),
				String.format(
					"%s/%s/creatorId", objectRelationship1.getName(),
					objectRelationship2.getName()),
				String.format(
					"%s/%s/dateModified", objectRelationship1.getName(),
					objectRelationship2.getName()),
				String.format(
					"%s/%s/externalReferenceCode",
					objectRelationship1.getName(),
					objectRelationship2.getName()),
				String.format(
					"%s/%s/userId", objectRelationship1.getName(),
					objectRelationship2.getName()));
		}
		finally {
			for (JSONObject jsonObject :
					ArrayUtil.append(
						oneToManyDepth2JSONObjects,
						oneToManyDepth1JSONObjects)) {

				if (jsonObject == null) {
					continue;
				}

				objectEntryLocalService.deleteObjectEntry(
					jsonObject.getLong("id"));
			}

			if (jsonObject1 != null) {
				objectEntryLocalService.deleteObjectEntry(
					jsonObject1.getLong("id"));
			}

			if (jsonObject2 != null) {
				objectEntryLocalService.deleteObjectEntry(
					jsonObject2.getLong("id"));
			}
		}
	}

	@Test
	public void testSortByRelationshipObjectFields() throws Exception {
		objectRelationship1 = ObjectRelationshipTestUtil.addObjectRelationship(
			getObjectDefinition1(), getObjectDefinition2(),
			TestPropsValues.getUserId(),
			ObjectRelationshipConstants.TYPE_MANY_TO_MANY);

		String endpoint = getEndpoint(getObjectDefinition1(), testGroupId);

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.portal.vulcan.internal.jaxrs.exception.mapper." +
					"WebApplicationExceptionMapper",
				LoggerTestUtil.ERROR)) {

			JSONAssert.assertEquals(
				JSONUtil.put(
					"status", "BAD_REQUEST"
				).put(
					"title",
					"Unable to sort by a many to many related object field"
				).toString(),
				HTTPTestUtil.invokeToString(
					null,
					StringBundler.concat(
						endpoint, "?sort=",
						URLCodec.encodeURL(
							String.format(
								"%s/%s:asc", objectRelationship1.getName(),
								OBJECT_FIELD_NAME_TEXT))),
					Http.Method.GET),
				JSONCompareMode.STRICT);
		}
	}

	@Test
	public void testSortBySystemObjectField() throws Exception {
		Role role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		addResourcePermission(
			ObjectActionKeys.ADD_OBJECT_ENTRY,
			getObjectDefinition1().getResourceName(), role);

		User user1 = addUser("test1", "test1");
		User user2 = addUser("test2", "test2");

		roleLocalService.addUserRole(user1.getUserId(), role.getRoleId());
		roleLocalService.addUserRole(user2.getUserId(), role.getRoleId());

		getObjectDefinition1().setEnableObjectEntryDraft(true);

		objectDefinition1 = objectDefinitionLocalService.updateObjectDefinition(
			getObjectDefinition1());

		String endpoint = getEndpoint(getObjectDefinition1(), testGroupId);

		JSONObject[] jsonObjects = new JSONObject[2];

		HTTPTestUtil.customize(
		).withCredentials(
			"test2@liferay.com", "test2"
		).apply(
			() ->
				jsonObjects[0] = HTTPTestUtil.invokeToJSONObject(
					JSONUtil.put(
						"externalReferenceCode", "ERC2"
					).put(
						"status",
						JSONUtil.put("code", WorkflowConstants.STATUS_DRAFT)
					).toString(),
					endpoint, Http.Method.POST)
		);

		HTTPTestUtil.customize(
		).withCredentials(
			"test1@liferay.com", "test1"
		).apply(
			() ->
				jsonObjects[1] = HTTPTestUtil.invokeToJSONObject(
					JSONUtil.put(
						"externalReferenceCode", "ERC1"
					).toString(),
					endpoint, Http.Method.POST)
		);

		HTTPTestUtil.customize(
		).withCredentials(
			"test1@liferay.com", "test1"
		).apply(
			() ->
				jsonObjects[1] = HTTPTestUtil.invokeToJSONObject(
					JSONUtil.put(
						OBJECT_FIELD_NAME_1, RandomTestUtil.randomString()
					).toString(),
					endpoint + "/by-external-reference-code/ERC1",
					Http.Method.PATCH)
		);

		HTTPTestUtil.customize(
		).withCredentials(
			"test2@liferay.com", "test2"
		).apply(
			() ->
				jsonObjects[0] = HTTPTestUtil.invokeToJSONObject(
					JSONUtil.put(
						OBJECT_FIELD_NAME_1, RandomTestUtil.randomString()
					).toString(),
					endpoint + "/by-external-reference-code/ERC2",
					Http.Method.PATCH)
		);

		try {
			_testSortByFieldName(
				endpoint, jsonObjects[1], jsonObjects[0], "creator");
			_testSortByFieldName(
				endpoint, jsonObjects[1], jsonObjects[0], "creatorId");
			_testSortByFieldName(
				endpoint, jsonObjects[0], jsonObjects[1], "dateCreated");
			_testSortByFieldName(
				endpoint, jsonObjects[1], jsonObjects[0], "dateModified");
			_testSortByFieldName(
				endpoint, jsonObjects[1], jsonObjects[0],
				"externalReferenceCode");
			_testSortByFieldName(
				endpoint, jsonObjects[0], jsonObjects[1], "id");
			_testSortByFieldName(
				endpoint, jsonObjects[1], jsonObjects[0], "status");
			_testSortByFieldName(
				endpoint, jsonObjects[1], jsonObjects[0], "userId");

			// Sort by several fields

			_testSortByFieldName(
				endpoint, jsonObjects[0], jsonObjects[1], "dateCreated", "id");
			_testSortByFieldName(
				endpoint, jsonObjects[1], jsonObjects[0], "creator",
				"dateModified", "externalReferenceCode");
		}
		finally {
			for (JSONObject jsonObject : jsonObjects) {
				if (jsonObject == null) {
					continue;
				}

				objectEntryLocalService.deleteObjectEntry(
					jsonObject.getLong("id"));
			}
		}
	}

	@Test
	public void testSortByUnsupportedObjectFields() throws Exception {
		objectRelationship1 = ObjectRelationshipTestUtil.addObjectRelationship(
			getObjectDefinition1(), getObjectDefinition2(),
			TestPropsValues.getUserId(),
			ObjectRelationshipConstants.TYPE_MANY_TO_MANY);

		_testSortByUnsupportedObjectField(
			"Unable to sort by a many to many related object field",
			getObjectDefinition1(),
			String.format(
				"%s/%s:asc", objectRelationship1.getName(),
				OBJECT_FIELD_NAME_TEXT));

		_testSortByUnsupportedObjectField(
			"Unable to sort by property: objectDefinitionId",
			getObjectDefinition1(), "objectDefinitionId");
		_testSortByUnsupportedObjectField(
			"Unable to sort by property: siteId",
			getSiteScopedObjectDefinition1(), "siteId");
	}

	private void _testSortByCustomObjectField(
			String endpoint, JSONObject expectedJSONObject1,
			JSONObject expectedJSONObject2, String... fieldNames)
		throws Exception {

		_testSortByFieldName(
			endpoint, expectedJSONObject1, expectedJSONObject2, fieldNames);

		JSONObject valuesJSONObject1 = JSONFactoryUtil.createJSONObject();
		JSONObject valuesJSONObject2 = JSONFactoryUtil.createJSONObject();

		for (String fieldName : fieldNames) {
			valuesJSONObject1.put(
				fieldName, expectedJSONObject1.get(fieldName));
			valuesJSONObject2.put(
				fieldName, expectedJSONObject2.get(fieldName));
		}

		try {
			JSONObject jsonObject1 = HTTPTestUtil.invokeToJSONObject(
				valuesJSONObject2.toString(),
				endpoint + "/" + expectedJSONObject1.getLong("id"),
				Http.Method.PATCH);

			JSONObject jsonObject2 = HTTPTestUtil.invokeToJSONObject(
				valuesJSONObject1.toString(),
				endpoint + "/" + expectedJSONObject2.getLong("id"),
				Http.Method.PATCH);

			_testSortByFieldName(
				endpoint, jsonObject2, jsonObject1, fieldNames);
		}
		finally {
			HTTPTestUtil.invokeToJSONObject(
				valuesJSONObject1.toString(),
				endpoint + "/" + expectedJSONObject1.getLong("id"),
				Http.Method.PATCH);

			HTTPTestUtil.invokeToJSONObject(
				valuesJSONObject2.toString(),
				endpoint + "/" + expectedJSONObject2.getLong("id"),
				Http.Method.PATCH);
		}
	}

	private void _testSortByFieldName(
			String endpoint, JSONObject expectedJSONObject1,
			JSONObject expectedJSONObject2, JSONObject expectedJSONObject3,
			JSONObject expectedJSONObject4, String... fieldNames)
		throws Exception {

		JSONObject pageJSONObject = HTTPTestUtil.invokeToJSONObject(
			null,
			StringBundler.concat(
				endpoint, "?sort=",
				URLCodec.encodeURL(
					StringUtil.merge(fieldNames, ":asc,") + ":asc,id:asc")),
			Http.Method.GET);

		assertItem(0, pageJSONObject, "id", expectedJSONObject1.getLong("id"));
		assertItem(1, pageJSONObject, "id", expectedJSONObject2.getLong("id"));
		assertItem(2, pageJSONObject, "id", expectedJSONObject3.getLong("id"));
		assertItem(3, pageJSONObject, "id", expectedJSONObject4.getLong("id"));

		pageJSONObject = HTTPTestUtil.invokeToJSONObject(
			null,
			StringBundler.concat(
				endpoint, "?sort=",
				URLCodec.encodeURL(
					StringUtil.merge(fieldNames, ":desc,") + ":desc,id:desc")),
			Http.Method.GET);

		assertItem(0, pageJSONObject, "id", expectedJSONObject4.getLong("id"));
		assertItem(1, pageJSONObject, "id", expectedJSONObject3.getLong("id"));
		assertItem(2, pageJSONObject, "id", expectedJSONObject2.getLong("id"));
		assertItem(3, pageJSONObject, "id", expectedJSONObject1.getLong("id"));
	}

	private void _testSortByFieldName(
			String endpoint, JSONObject expectedJSONObject1,
			JSONObject expectedJSONObject2, String... fieldNames)
		throws Exception {

		JSONObject pageJSONObject = HTTPTestUtil.invokeToJSONObject(
			null,
			StringBundler.concat(
				endpoint, "?sort=",
				URLCodec.encodeURL(
					StringUtil.merge(fieldNames, ":asc,") + ":asc")),
			Http.Method.GET);

		assertItem(0, pageJSONObject, "id", expectedJSONObject1.getLong("id"));
		assertItem(1, pageJSONObject, "id", expectedJSONObject2.getLong("id"));

		pageJSONObject = HTTPTestUtil.invokeToJSONObject(
			null,
			StringBundler.concat(
				endpoint, "?sort=",
				URLCodec.encodeURL(
					StringUtil.merge(fieldNames, ":desc,") + ":desc")),
			Http.Method.GET);

		assertItem(0, pageJSONObject, "id", expectedJSONObject2.getLong("id"));
		assertItem(1, pageJSONObject, "id", expectedJSONObject1.getLong("id"));
	}

	private void
			_testSortByManyToOneAndOneToManyRelationshipsCustomObjectFields(
				String endpoint1, String endpoint2,
				JSONObject expectedJSONObject1, JSONObject expectedJSONObject2,
				JSONObject expectedJSONObject3, JSONObject expectedJSONObject4,
				JSONObject relatedJSONObject1, JSONObject relatedJSONObject2,
				JSONObject relatedJSONObject3, JSONObject relatedJSONObject4,
				String... fieldNames)
		throws Exception {

		_testSortByFieldName(
			endpoint1, expectedJSONObject1, expectedJSONObject2,
			expectedJSONObject3, expectedJSONObject4, fieldNames);

		JSONObject valuesJSONObject1 = JSONFactoryUtil.createJSONObject();
		JSONObject valuesJSONObject2 = JSONFactoryUtil.createJSONObject();
		JSONObject valuesJSONObject3 = JSONFactoryUtil.createJSONObject();
		JSONObject valuesJSONObject4 = JSONFactoryUtil.createJSONObject();

		for (String fieldName : fieldNames) {
			String objectFieldName = StringUtil.extractLast(fieldName, "/");

			valuesJSONObject1.put(
				objectFieldName, relatedJSONObject1.get(objectFieldName));
			valuesJSONObject2.put(
				objectFieldName, relatedJSONObject2.get(objectFieldName));
			valuesJSONObject3.put(
				objectFieldName, relatedJSONObject3.get(objectFieldName));
			valuesJSONObject4.put(
				objectFieldName, relatedJSONObject4.get(objectFieldName));
		}

		try {
			HTTPTestUtil.invokeToJSONObject(
				valuesJSONObject3.toString(),
				endpoint2 + "/" + relatedJSONObject1.getLong("id"),
				Http.Method.PATCH);

			HTTPTestUtil.invokeToJSONObject(
				valuesJSONObject4.toString(),
				endpoint2 + "/" + relatedJSONObject2.getLong("id"),
				Http.Method.PATCH);

			HTTPTestUtil.invokeToJSONObject(
				valuesJSONObject1.toString(),
				endpoint2 + "/" + relatedJSONObject3.getLong("id"),
				Http.Method.PATCH);

			HTTPTestUtil.invokeToJSONObject(
				valuesJSONObject2.toString(),
				endpoint2 + "/" + relatedJSONObject4.getLong("id"),
				Http.Method.PATCH);

			_testSortByFieldName(
				endpoint1, expectedJSONObject3, expectedJSONObject4,
				expectedJSONObject1, expectedJSONObject2, fieldNames);
		}
		finally {
			HTTPTestUtil.invokeToJSONObject(
				valuesJSONObject1.toString(),
				endpoint2 + "/" + relatedJSONObject1.getLong("id"),
				Http.Method.PATCH);

			HTTPTestUtil.invokeToJSONObject(
				valuesJSONObject2.toString(),
				endpoint2 + "/" + relatedJSONObject2.getLong("id"),
				Http.Method.PATCH);

			HTTPTestUtil.invokeToJSONObject(
				valuesJSONObject3.toString(),
				endpoint2 + "/" + relatedJSONObject3.getLong("id"),
				Http.Method.PATCH);

			HTTPTestUtil.invokeToJSONObject(
				valuesJSONObject4.toString(),
				endpoint2 + "/" + relatedJSONObject4.getLong("id"),
				Http.Method.PATCH);
		}
	}

	private void _testSortByManyToOneRelationshipCustomObjectFields(
			String endpoint1, String endpoint2, JSONObject expectedJSONObject1,
			JSONObject expectedJSONObject2, JSONObject expectedJSONObject3,
			JSONObject expectedJSONObject4, JSONObject relatedJSONObject1,
			JSONObject relatedJSONObject2, String... fieldNames)
		throws Exception {

		_testSortByFieldName(
			endpoint1, expectedJSONObject1, expectedJSONObject2,
			expectedJSONObject3, expectedJSONObject4, fieldNames);

		JSONObject valuesJSONObject1 = JSONFactoryUtil.createJSONObject();
		JSONObject valuesJSONObject2 = JSONFactoryUtil.createJSONObject();

		for (String fieldName : fieldNames) {
			String objectFieldName = StringUtil.extractLast(fieldName, "/");

			valuesJSONObject1.put(
				objectFieldName, relatedJSONObject1.get(objectFieldName));
			valuesJSONObject2.put(
				objectFieldName, relatedJSONObject2.get(objectFieldName));
		}

		try {
			HTTPTestUtil.invokeToJSONObject(
				valuesJSONObject2.toString(),
				endpoint2 + "/" + relatedJSONObject1.getLong("id"),
				Http.Method.PATCH);

			HTTPTestUtil.invokeToJSONObject(
				valuesJSONObject1.toString(),
				endpoint2 + "/" + relatedJSONObject2.getLong("id"),
				Http.Method.PATCH);

			_testSortByFieldName(
				endpoint1, expectedJSONObject3, expectedJSONObject4,
				expectedJSONObject1, expectedJSONObject2, fieldNames);
		}
		finally {
			HTTPTestUtil.invokeToJSONObject(
				valuesJSONObject1.toString(),
				endpoint2 + "/" + relatedJSONObject1.getLong("id"),
				Http.Method.PATCH);

			HTTPTestUtil.invokeToJSONObject(
				valuesJSONObject2.toString(),
				endpoint2 + "/" + relatedJSONObject2.getLong("id"),
				Http.Method.PATCH);
		}
	}

	private void _testSortByOneToManyRelationshipCustomObjectFields(
			String endpoint1, String endpoint2, JSONObject expectedJSONObject1,
			JSONObject expectedJSONObject2, JSONObject relatedJSONObject1,
			JSONObject relatedJSONObject2, JSONObject relatedJSONObject3,
			JSONObject relatedJSONObject4, String... fieldNames)
		throws Exception {

		_testSortByFieldName(
			endpoint1, expectedJSONObject1, expectedJSONObject2, fieldNames);

		JSONObject valuesJSONObject1 = JSONFactoryUtil.createJSONObject();
		JSONObject valuesJSONObject2 = JSONFactoryUtil.createJSONObject();
		JSONObject valuesJSONObject3 = JSONFactoryUtil.createJSONObject();
		JSONObject valuesJSONObject4 = JSONFactoryUtil.createJSONObject();

		for (String fieldName : fieldNames) {
			String objectFieldName = StringUtil.extractLast(fieldName, "/");

			valuesJSONObject1.put(
				objectFieldName, relatedJSONObject1.get(objectFieldName));
			valuesJSONObject2.put(
				objectFieldName, relatedJSONObject2.get(objectFieldName));
			valuesJSONObject3.put(
				objectFieldName, relatedJSONObject3.get(objectFieldName));
			valuesJSONObject4.put(
				objectFieldName, relatedJSONObject4.get(objectFieldName));
		}

		try {
			HTTPTestUtil.invokeToJSONObject(
				valuesJSONObject3.toString(),
				endpoint2 + "/" + relatedJSONObject1.getLong("id"),
				Http.Method.PATCH);

			HTTPTestUtil.invokeToJSONObject(
				valuesJSONObject4.toString(),
				endpoint2 + "/" + relatedJSONObject2.getLong("id"),
				Http.Method.PATCH);

			HTTPTestUtil.invokeToJSONObject(
				valuesJSONObject1.toString(),
				endpoint2 + "/" + relatedJSONObject3.getLong("id"),
				Http.Method.PATCH);

			HTTPTestUtil.invokeToJSONObject(
				valuesJSONObject2.toString(),
				endpoint2 + "/" + relatedJSONObject4.getLong("id"),
				Http.Method.PATCH);

			_testSortByFieldName(
				endpoint1, expectedJSONObject2, expectedJSONObject1,
				fieldNames);
		}
		finally {
			HTTPTestUtil.invokeToJSONObject(
				valuesJSONObject1.toString(),
				endpoint2 + "/" + relatedJSONObject1.getLong("id"),
				Http.Method.PATCH);

			HTTPTestUtil.invokeToJSONObject(
				valuesJSONObject2.toString(),
				endpoint2 + "/" + relatedJSONObject2.getLong("id"),
				Http.Method.PATCH);

			HTTPTestUtil.invokeToJSONObject(
				valuesJSONObject3.toString(),
				endpoint2 + "/" + relatedJSONObject3.getLong("id"),
				Http.Method.PATCH);

			HTTPTestUtil.invokeToJSONObject(
				valuesJSONObject4.toString(),
				endpoint2 + "/" + relatedJSONObject4.getLong("id"),
				Http.Method.PATCH);
		}
	}

	private void _testSortByUnsupportedObjectField(
			String expectedTitle, ObjectDefinition objectDefinition,
			String sortString)
		throws Exception {

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.portal.vulcan.internal.jaxrs.exception.mapper." +
					"WebApplicationExceptionMapper",
				LoggerTestUtil.ERROR)) {

			String endpoint = getEndpoint(objectDefinition, testGroupId);

			JSONAssert.assertEquals(
				JSONUtil.put(
					"status", "BAD_REQUEST"
				).put(
					"title", expectedTitle
				).toString(),
				HTTPTestUtil.invokeToString(
					null, endpoint + "?sort=" + URLCodec.encodeURL(sortString),
					Http.Method.GET),
				JSONCompareMode.STRICT);
		}
	}

}