/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
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
import com.liferay.object.model.ObjectRelationship;
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
import com.liferay.portal.kernel.test.randomizerbumpers.UniqueStringRandomizerBumper;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.HTTPTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
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
 * @author Magdalena Jedraszak
 * @author Daniel Raposo
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

		ObjectRelationship objectRelationship =
			ObjectRelationshipTestUtil.addObjectRelationship(
				getObjectDefinition2(), getObjectDefinition1(),
				TestPropsValues.getUserId(),
				ObjectRelationshipConstants.TYPE_ONE_TO_MANY);

		ObjectField objectField = objectFieldLocalService.getObjectField(
			objectRelationship.getObjectFieldId2());

		BigDecimal randomBigDecimal = new BigDecimal(
			RandomTestUtil.randomDouble());
		Date randomDate1 = RandomTestUtil.nextDate();
		Date randomDate2 = RandomTestUtil.nextDate();
		float randomFloat = RandomTestUtil.randomFloat();
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
				dateTimeFormat.format(randomDate2)
			).put(
				OBJECT_FIELD_NAME_DECIMAL, randomFloat
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
				dateFormat.format(
					new Date(randomDate1.getTime() + (24 * 3600 * 1000)))
			).put(
				OBJECT_FIELD_NAME_DATE_TIME_INPUT,
				dateTimeFormat.format(
					new Date(
						randomDate2.getTime() +
							(RandomTestUtil.randomInt(1, 60) * 1000)))
			).put(
				OBJECT_FIELD_NAME_DECIMAL, randomFloat + 1
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

			_testSortByFieldNames(
				endpoint, jsonObject1, jsonObject2,
				StringUtil.extractLast(objectFieldName, StringPool.UNDERLINE));
			_testSortByFieldNames(
				endpoint, jsonObject1, jsonObject2,
				StringUtil.replaceLast(objectFieldName, "Id", "ERC"));

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
			_deleteObjectEntries(jsonObject1, jsonObject2);
		}
	}

	@Test
	public void testSortByManyToOneAndOneToManyRelationshipsCustomObjectFields()
		throws Exception {

		ObjectRelationship objectRelationship1 =
			ObjectRelationshipTestUtil.addObjectRelationship(
				getObjectDefinition2(), getObjectDefinition1(),
				TestPropsValues.getUserId(),
				ObjectRelationshipConstants.TYPE_ONE_TO_MANY);
		ObjectRelationship objectRelationship2 =
			ObjectRelationshipTestUtil.addObjectRelationship(
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
		float randomFloat = RandomTestUtil.randomFloat();
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
				dateTimeFormat.format(randomDate2)
			).put(
				OBJECT_FIELD_NAME_DECIMAL, randomFloat
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
				dateFormat.format(
					new Date(randomDate1.getTime() + (2 * 24 * 3600 * 1000)))
			).put(
				OBJECT_FIELD_NAME_DATE_TIME_INPUT,
				dateTimeFormat.format(new Date(randomDate2.getTime() + 2000))
			).put(
				OBJECT_FIELD_NAME_DECIMAL, randomFloat + 2
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
				dateTimeFormat.format(randomDate2)
			).put(
				OBJECT_FIELD_NAME_DECIMAL, randomFloat
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
				dateFormat.format(
					new Date(randomDate1.getTime() + (2 * 24 * 3600 * 1000)))
			).put(
				OBJECT_FIELD_NAME_DATE_TIME_INPUT,
				dateTimeFormat.format(new Date(randomDate2.getTime() + 2000))
			).put(
				OBJECT_FIELD_NAME_DECIMAL, randomFloat + 2
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
				dateFormat.format(
					new Date(randomDate1.getTime() + (24 * 3600 * 1000)))
			).put(
				OBJECT_FIELD_NAME_DATE_TIME_INPUT,
				dateTimeFormat.format(new Date(randomDate2.getTime() + 1000))
			).put(
				OBJECT_FIELD_NAME_DECIMAL, randomFloat + 1
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
				dateFormat.format(
					new Date(randomDate1.getTime() + (3 * 24 * 3600 * 1000)))
			).put(
				OBJECT_FIELD_NAME_DATE_TIME_INPUT,
				dateTimeFormat.format(new Date(randomDate2.getTime() + 3000))
			).put(
				OBJECT_FIELD_NAME_DECIMAL, randomFloat + 3
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

		_relateObjectEntries(
			endpoint2, depth1JSONObject1, objectRelationship1, jsonObject1);
		_relateObjectEntries(
			endpoint2, depth1JSONObject1, objectRelationship1, jsonObject2);
		_relateObjectEntries(
			endpoint2, depth1JSONObject2, objectRelationship1, jsonObject3);
		_relateObjectEntries(
			endpoint2, depth1JSONObject2, objectRelationship1, jsonObject4);

		_relateObjectEntries(
			endpoint2, depth1JSONObject1, objectRelationship2,
			depth2JSONObject1);
		_relateObjectEntries(
			endpoint2, depth1JSONObject1, objectRelationship2,
			depth2JSONObject2);
		_relateObjectEntries(
			endpoint2, depth1JSONObject2, objectRelationship2,
			depth2JSONObject3);
		_relateObjectEntries(
			endpoint2, depth1JSONObject2, objectRelationship2,
			depth2JSONObject4);

		try {
			_testSortByManyToOneAndOneToManyRelationshipsCustomObjectFields(
				endpoint1, jsonObject1, jsonObject2, jsonObject3, jsonObject4,
				endpoint3, depth2JSONObject1, depth2JSONObject2,
				depth2JSONObject3, depth2JSONObject4,
				String.format(
					"%s/%s/%s", objectRelationship1.getName(),
					objectRelationship2.getName(), OBJECT_FIELD_NAME_BOOLEAN));
			_testSortByManyToOneAndOneToManyRelationshipsCustomObjectFields(
				endpoint1, jsonObject1, jsonObject2, jsonObject3, jsonObject4,
				endpoint3, depth2JSONObject1, depth2JSONObject2,
				depth2JSONObject3, depth2JSONObject4,
				String.format(
					"%s/%s/%s", objectRelationship1.getName(),
					objectRelationship2.getName(), OBJECT_FIELD_NAME_DATE));
			_testSortByManyToOneAndOneToManyRelationshipsCustomObjectFields(
				endpoint1, jsonObject1, jsonObject2, jsonObject3, jsonObject4,
				endpoint3, depth2JSONObject1, depth2JSONObject2,
				depth2JSONObject3, depth2JSONObject4,
				String.format(
					"%s/%s/%s", objectRelationship1.getName(),
					objectRelationship2.getName(),
					OBJECT_FIELD_NAME_DATE_TIME_INPUT));
			_testSortByManyToOneAndOneToManyRelationshipsCustomObjectFields(
				endpoint1, jsonObject1, jsonObject2, jsonObject3, jsonObject4,
				endpoint3, depth2JSONObject1, depth2JSONObject2,
				depth2JSONObject3, depth2JSONObject4,
				String.format(
					"%s/%s/%s", objectRelationship1.getName(),
					objectRelationship2.getName(), OBJECT_FIELD_NAME_DECIMAL));
			_testSortByManyToOneAndOneToManyRelationshipsCustomObjectFields(
				endpoint1, jsonObject1, jsonObject2, jsonObject3, jsonObject4,
				endpoint3, depth2JSONObject1, depth2JSONObject2,
				depth2JSONObject3, depth2JSONObject4,
				String.format(
					"%s/%s/%s", objectRelationship1.getName(),
					objectRelationship2.getName(), OBJECT_FIELD_NAME_INTEGER));
			_testSortByManyToOneAndOneToManyRelationshipsCustomObjectFields(
				endpoint1, jsonObject1, jsonObject2, jsonObject3, jsonObject4,
				endpoint3, depth2JSONObject1, depth2JSONObject2,
				depth2JSONObject3, depth2JSONObject4,
				String.format(
					"%s/%s/%s", objectRelationship1.getName(),
					objectRelationship2.getName(),
					OBJECT_FIELD_NAME_LONG_INTEGER));
			_testSortByManyToOneAndOneToManyRelationshipsCustomObjectFields(
				endpoint1, jsonObject1, jsonObject2, jsonObject3, jsonObject4,
				endpoint3, depth2JSONObject1, depth2JSONObject2,
				depth2JSONObject3, depth2JSONObject4,
				String.format(
					"%s/%s/%s", objectRelationship1.getName(),
					objectRelationship2.getName(),
					OBJECT_FIELD_NAME_LONG_TEXT));
			_testSortByManyToOneAndOneToManyRelationshipsCustomObjectFields(
				endpoint1, jsonObject1, jsonObject2, jsonObject3, jsonObject4,
				endpoint3, depth2JSONObject1, depth2JSONObject2,
				depth2JSONObject3, depth2JSONObject4,
				String.format(
					"%s/%s/%s", objectRelationship1.getName(),
					objectRelationship2.getName(),
					OBJECT_FIELD_NAME_MULTISELECT_PICKLIST));
			_testSortByManyToOneAndOneToManyRelationshipsCustomObjectFields(
				endpoint1, jsonObject1, jsonObject2, jsonObject3, jsonObject4,
				endpoint3, depth2JSONObject1, depth2JSONObject2,
				depth2JSONObject3, depth2JSONObject4,
				String.format(
					"%s/%s/%s", objectRelationship1.getName(),
					objectRelationship2.getName(), OBJECT_FIELD_NAME_PICKLIST));
			_testSortByManyToOneAndOneToManyRelationshipsCustomObjectFields(
				endpoint1, jsonObject1, jsonObject2, jsonObject3, jsonObject4,
				endpoint3, depth2JSONObject1, depth2JSONObject2,
				depth2JSONObject3, depth2JSONObject4,
				String.format(
					"%s/%s/%s", objectRelationship1.getName(),
					objectRelationship2.getName(),
					OBJECT_FIELD_NAME_PRECISION_DECIMAL));
			_testSortByManyToOneAndOneToManyRelationshipsCustomObjectFields(
				endpoint1, jsonObject1, jsonObject2, jsonObject3, jsonObject4,
				endpoint3, depth2JSONObject1, depth2JSONObject2,
				depth2JSONObject3, depth2JSONObject4,
				String.format(
					"%s/%s/%s", objectRelationship1.getName(),
					objectRelationship2.getName(), OBJECT_FIELD_NAME_TEXT));

			// Sort by several fields

			_testSortByManyToOneAndOneToManyRelationshipsCustomObjectFields(
				endpoint1, jsonObject1, jsonObject2, jsonObject3, jsonObject4,
				endpoint3, depth2JSONObject1, depth2JSONObject2,
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
			_deleteObjectEntries(
				jsonObject1, jsonObject2, jsonObject3, jsonObject4,
				depth2JSONObject1, depth2JSONObject2, depth2JSONObject3,
				depth2JSONObject4, depth1JSONObject1, depth1JSONObject2);
		}
	}

	@Test
	public void testSortByManyToOneRelationshipCustomObjectFields()
		throws Exception {

		ObjectRelationship objectRelationship1 =
			ObjectRelationshipTestUtil.addObjectRelationship(
				getObjectDefinition2(), getObjectDefinition1(),
				TestPropsValues.getUserId(),
				ObjectRelationshipConstants.TYPE_ONE_TO_MANY);
		ObjectRelationship objectRelationship2 =
			ObjectRelationshipTestUtil.addObjectRelationship(
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
		float randomFloat = RandomTestUtil.randomFloat();
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
				dateTimeFormat.format(randomDate2)
			).put(
				OBJECT_FIELD_NAME_DECIMAL, randomFloat
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
				dateFormat.format(
					new Date(randomDate1.getTime() + (2 * 24 * 3600 * 1000)))
			).put(
				OBJECT_FIELD_NAME_DATE_TIME_INPUT,
				dateTimeFormat.format(new Date(randomDate2.getTime() + 2000))
			).put(
				OBJECT_FIELD_NAME_DECIMAL, randomFloat + 2
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
				dateTimeFormat.format(randomDate2)
			).put(
				OBJECT_FIELD_NAME_DECIMAL, randomFloat
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
				dateFormat.format(
					new Date(randomDate1.getTime() + (2 * 24 * 3600 * 1000)))
			).put(
				OBJECT_FIELD_NAME_DATE_TIME_INPUT,
				dateTimeFormat.format(new Date(randomDate2.getTime() + 2000))
			).put(
				OBJECT_FIELD_NAME_DECIMAL, randomFloat + 2
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

		_relateObjectEntries(
			endpoint2, depth1JSONObject1, objectRelationship1, jsonObject1);
		_relateObjectEntries(
			endpoint2, depth1JSONObject1, objectRelationship1, jsonObject2);
		_relateObjectEntries(
			endpoint2, depth1JSONObject2, objectRelationship1, jsonObject3);
		_relateObjectEntries(
			endpoint2, depth1JSONObject2, objectRelationship1, jsonObject4);

		_relateObjectEntries(
			endpoint3, depth2JSONObject1, objectRelationship2,
			depth1JSONObject1);
		_relateObjectEntries(
			endpoint3, depth2JSONObject2, objectRelationship2,
			depth1JSONObject2);

		try {

			// Depth 1

			_testSortByManyToOneRelationshipCustomObjectFields(
				endpoint1, jsonObject1, jsonObject2, jsonObject3, jsonObject4,
				endpoint2, depth1JSONObject1, depth1JSONObject2,
				String.format(
					"%s/%s", objectRelationship1.getName(),
					OBJECT_FIELD_NAME_BOOLEAN));
			_testSortByManyToOneRelationshipCustomObjectFields(
				endpoint1, jsonObject1, jsonObject2, jsonObject3, jsonObject4,
				endpoint2, depth1JSONObject1, depth1JSONObject2,
				String.format(
					"%s/%s", objectRelationship1.getName(),
					OBJECT_FIELD_NAME_DATE));
			_testSortByManyToOneRelationshipCustomObjectFields(
				endpoint1, jsonObject1, jsonObject2, jsonObject3, jsonObject4,
				endpoint2, depth1JSONObject1, depth1JSONObject2,
				String.format(
					"%s/%s", objectRelationship1.getName(),
					OBJECT_FIELD_NAME_DATE_TIME_INPUT));
			_testSortByManyToOneRelationshipCustomObjectFields(
				endpoint1, jsonObject1, jsonObject2, jsonObject3, jsonObject4,
				endpoint2, depth1JSONObject1, depth1JSONObject2,
				String.format(
					"%s/%s", objectRelationship1.getName(),
					OBJECT_FIELD_NAME_DECIMAL));
			_testSortByManyToOneRelationshipCustomObjectFields(
				endpoint1, jsonObject1, jsonObject2, jsonObject3, jsonObject4,
				endpoint2, depth1JSONObject1, depth1JSONObject2,
				String.format(
					"%s/%s", objectRelationship1.getName(),
					OBJECT_FIELD_NAME_INTEGER));
			_testSortByManyToOneRelationshipCustomObjectFields(
				endpoint1, jsonObject1, jsonObject2, jsonObject3, jsonObject4,
				endpoint2, depth1JSONObject1, depth1JSONObject2,
				String.format(
					"%s/%s", objectRelationship1.getName(),
					OBJECT_FIELD_NAME_LONG_INTEGER));
			_testSortByManyToOneRelationshipCustomObjectFields(
				endpoint1, jsonObject1, jsonObject2, jsonObject3, jsonObject4,
				endpoint2, depth1JSONObject1, depth1JSONObject2,
				String.format(
					"%s/%s", objectRelationship1.getName(),
					OBJECT_FIELD_NAME_LONG_TEXT));
			_testSortByManyToOneRelationshipCustomObjectFields(
				endpoint1, jsonObject1, jsonObject2, jsonObject3, jsonObject4,
				endpoint2, depth1JSONObject1, depth1JSONObject2,
				String.format(
					"%s/%s", objectRelationship1.getName(),
					OBJECT_FIELD_NAME_MULTISELECT_PICKLIST));
			_testSortByManyToOneRelationshipCustomObjectFields(
				endpoint1, jsonObject1, jsonObject2, jsonObject3, jsonObject4,
				endpoint2, depth1JSONObject1, depth1JSONObject2,
				String.format(
					"%s/%s", objectRelationship1.getName(),
					OBJECT_FIELD_NAME_PICKLIST));
			_testSortByManyToOneRelationshipCustomObjectFields(
				endpoint1, jsonObject1, jsonObject2, jsonObject3, jsonObject4,
				endpoint2, depth1JSONObject1, depth1JSONObject2,
				String.format(
					"%s/%s", objectRelationship1.getName(),
					OBJECT_FIELD_NAME_PRECISION_DECIMAL));
			_testSortByManyToOneRelationshipCustomObjectFields(
				endpoint1, jsonObject1, jsonObject2, jsonObject3, jsonObject4,
				endpoint2, depth1JSONObject1, depth1JSONObject2,
				String.format(
					"%s/%s", objectRelationship1.getName(),
					OBJECT_FIELD_NAME_TEXT));

			// Depth 2

			_testSortByManyToOneRelationshipCustomObjectFields(
				endpoint1, jsonObject1, jsonObject2, jsonObject3, jsonObject4,
				endpoint3, depth2JSONObject1, depth2JSONObject2,
				String.format(
					"%s/%s/%s", objectRelationship1.getName(),
					objectRelationship2.getName(), OBJECT_FIELD_NAME_BOOLEAN));
			_testSortByManyToOneRelationshipCustomObjectFields(
				endpoint1, jsonObject1, jsonObject2, jsonObject3, jsonObject4,
				endpoint3, depth2JSONObject1, depth2JSONObject2,
				String.format(
					"%s/%s/%s", objectRelationship1.getName(),
					objectRelationship2.getName(), OBJECT_FIELD_NAME_DATE));
			_testSortByManyToOneRelationshipCustomObjectFields(
				endpoint1, jsonObject1, jsonObject2, jsonObject3, jsonObject4,
				endpoint3, depth2JSONObject1, depth2JSONObject2,
				String.format(
					"%s/%s/%s", objectRelationship1.getName(),
					objectRelationship2.getName(),
					OBJECT_FIELD_NAME_DATE_TIME_INPUT));
			_testSortByManyToOneRelationshipCustomObjectFields(
				endpoint1, jsonObject1, jsonObject2, jsonObject3, jsonObject4,
				endpoint3, depth2JSONObject1, depth2JSONObject2,
				String.format(
					"%s/%s/%s", objectRelationship1.getName(),
					objectRelationship2.getName(), OBJECT_FIELD_NAME_DECIMAL));
			_testSortByManyToOneRelationshipCustomObjectFields(
				endpoint1, jsonObject1, jsonObject2, jsonObject3, jsonObject4,
				endpoint3, depth2JSONObject1, depth2JSONObject2,
				String.format(
					"%s/%s/%s", objectRelationship1.getName(),
					objectRelationship2.getName(), OBJECT_FIELD_NAME_INTEGER));
			_testSortByManyToOneRelationshipCustomObjectFields(
				endpoint1, jsonObject1, jsonObject2, jsonObject3, jsonObject4,
				endpoint3, depth2JSONObject1, depth2JSONObject2,
				String.format(
					"%s/%s/%s", objectRelationship1.getName(),
					objectRelationship2.getName(),
					OBJECT_FIELD_NAME_LONG_INTEGER));
			_testSortByManyToOneRelationshipCustomObjectFields(
				endpoint1, jsonObject1, jsonObject2, jsonObject3, jsonObject4,
				endpoint3, depth2JSONObject1, depth2JSONObject2,
				String.format(
					"%s/%s/%s", objectRelationship1.getName(),
					objectRelationship2.getName(),
					OBJECT_FIELD_NAME_LONG_TEXT));
			_testSortByManyToOneRelationshipCustomObjectFields(
				endpoint1, jsonObject1, jsonObject2, jsonObject3, jsonObject4,
				endpoint3, depth2JSONObject1, depth2JSONObject2,
				String.format(
					"%s/%s/%s", objectRelationship1.getName(),
					objectRelationship2.getName(),
					OBJECT_FIELD_NAME_MULTISELECT_PICKLIST));
			_testSortByManyToOneRelationshipCustomObjectFields(
				endpoint1, jsonObject1, jsonObject2, jsonObject3, jsonObject4,
				endpoint3, depth2JSONObject1, depth2JSONObject2,
				String.format(
					"%s/%s/%s", objectRelationship1.getName(),
					objectRelationship2.getName(), OBJECT_FIELD_NAME_PICKLIST));
			_testSortByManyToOneRelationshipCustomObjectFields(
				endpoint1, jsonObject1, jsonObject2, jsonObject3, jsonObject4,
				endpoint3, depth2JSONObject1, depth2JSONObject2,
				String.format(
					"%s/%s/%s", objectRelationship1.getName(),
					objectRelationship2.getName(),
					OBJECT_FIELD_NAME_PRECISION_DECIMAL));
			_testSortByManyToOneRelationshipCustomObjectFields(
				endpoint1, jsonObject1, jsonObject2, jsonObject3, jsonObject4,
				endpoint3, depth2JSONObject1, depth2JSONObject2,
				String.format(
					"%s/%s/%s", objectRelationship1.getName(),
					objectRelationship2.getName(), OBJECT_FIELD_NAME_TEXT));

			// Sort by several fields

			_testSortByManyToOneRelationshipCustomObjectFields(
				endpoint1, jsonObject1, jsonObject2, jsonObject3, jsonObject4,
				endpoint2, depth1JSONObject1, depth1JSONObject2,
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
				endpoint1, jsonObject1, jsonObject2, jsonObject3, jsonObject4,
				endpoint3, depth2JSONObject1, depth2JSONObject2,
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
			_deleteObjectEntries(
				jsonObject1, jsonObject2, jsonObject3, jsonObject4,
				depth1JSONObject1, depth1JSONObject2, depth2JSONObject1,
				depth2JSONObject2);
		}
	}

	@Test
	public void testSortByManyToOneRelationshipSystemObjectFields()
		throws Exception {

		ObjectDefinition objectDefinition1 = getObjectDefinition1();
		ObjectDefinition objectDefinition2 = getObjectDefinition2();

		ObjectRelationship objectRelationship1 =
			ObjectRelationshipTestUtil.addObjectRelationship(
				objectDefinition2, objectDefinition1,
				TestPropsValues.getUserId(),
				ObjectRelationshipConstants.TYPE_ONE_TO_MANY);

		ObjectDefinition objectDefinition3 = getObjectDefinition3();

		ObjectRelationship objectRelationship2 =
			ObjectRelationshipTestUtil.addObjectRelationship(
				objectDefinition3, objectDefinition2,
				TestPropsValues.getUserId(),
				ObjectRelationshipConstants.TYPE_ONE_TO_MANY);

		Role role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		addResourcePermission(
			ObjectActionKeys.ADD_OBJECT_ENTRY,
			objectDefinition2.getResourceName(), role);
		addResourcePermission(
			ObjectActionKeys.ADD_OBJECT_ENTRY,
			objectDefinition3.getResourceName(), role);
		addResourcePermission(
			ActionKeys.VIEW, objectDefinition2.getClassName(), role);
		addResourcePermission(
			ActionKeys.VIEW, objectDefinition3.getClassName(), role);

		String password = RandomTestUtil.randomString();
		String randomString = RandomTestUtil.randomString(
			UniqueStringRandomizerBumper.INSTANCE);

		User user1 = addUser("test1" + randomString, password);
		User user2 = addUser("test2" + randomString, password);
		User user3 = addUser("test3" + randomString, password);

		roleLocalService.addUserRole(user1.getUserId(), role.getRoleId());
		roleLocalService.addUserRole(user2.getUserId(), role.getRoleId());
		roleLocalService.addUserRole(user3.getUserId(), role.getRoleId());

		objectDefinition1.setEnableObjectEntryDraft(true);

		objectDefinition2.setEnableObjectEntryDraft(true);

		objectDefinition3.setEnableObjectEntryDraft(true);

		objectDefinition1 = objectDefinitionLocalService.updateObjectDefinition(
			objectDefinition1);
		objectDefinition2 = objectDefinitionLocalService.updateObjectDefinition(
			objectDefinition2);
		objectDefinition3 = objectDefinitionLocalService.updateObjectDefinition(
			objectDefinition3);

		String endpoint1 = getEndpoint(objectDefinition1, testGroupId);
		String endpoint2 = getEndpoint(objectDefinition2, testGroupId);
		String endpoint3 = getEndpoint(objectDefinition3, testGroupId);

		JSONObject[] depth1JSONObjects = new JSONObject[2];

		HTTPTestUtil.customize(
		).withCredentials(
			user3.getEmailAddress(), password
		).apply(
			() ->
				depth1JSONObjects[0] = HTTPTestUtil.invokeToJSONObject(
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
			user2.getEmailAddress(), password
		).apply(
			() ->
				depth1JSONObjects[1] = HTTPTestUtil.invokeToJSONObject(
					JSONUtil.put(
						"externalReferenceCode", "ERC1_1"
					).toString(),
					endpoint2, Http.Method.POST)
		);

		JSONObject[] depth2JSONObjects = new JSONObject[2];

		HTTPTestUtil.customize(
		).withCredentials(
			user2.getEmailAddress(), password
		).apply(
			() ->
				depth2JSONObjects[0] = HTTPTestUtil.invokeToJSONObject(
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
			user1.getEmailAddress(), password
		).apply(
			() ->
				depth2JSONObjects[1] = HTTPTestUtil.invokeToJSONObject(
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

		_relateObjectEntries(
			endpoint2, depth1JSONObjects[0], objectRelationship1, jsonObject1);
		_relateObjectEntries(
			endpoint2, depth1JSONObjects[0], objectRelationship1, jsonObject2);
		_relateObjectEntries(
			endpoint2, depth1JSONObjects[1], objectRelationship1, jsonObject3);
		_relateObjectEntries(
			endpoint2, depth1JSONObjects[1], objectRelationship1, jsonObject4);

		_relateObjectEntries(
			endpoint3, depth2JSONObjects[0], objectRelationship2,
			depth1JSONObjects[0]);
		_relateObjectEntries(
			endpoint3, depth2JSONObjects[1], objectRelationship2,
			depth1JSONObjects[1]);

		HTTPTestUtil.customize(
		).withCredentials(
			user3.getEmailAddress(), password
		).apply(
			() ->
				depth1JSONObjects[0] = HTTPTestUtil.invokeToJSONObject(
					JSONUtil.put(
						OBJECT_FIELD_NAME_2, RandomTestUtil.randomString()
					).toString(),
					endpoint2 + "/by-external-reference-code/ERC2_1",
					Http.Method.PATCH)
		);

		HTTPTestUtil.customize(
		).withCredentials(
			user2.getEmailAddress(), password
		).apply(
			() ->
				depth1JSONObjects[1] = HTTPTestUtil.invokeToJSONObject(
					JSONUtil.put(
						OBJECT_FIELD_NAME_2, RandomTestUtil.randomString()
					).toString(),
					endpoint2 + "/by-external-reference-code/ERC1_1",
					Http.Method.PATCH)
		);

		HTTPTestUtil.customize(
		).withCredentials(
			user2.getEmailAddress(), password
		).apply(
			() ->
				depth2JSONObjects[0] = HTTPTestUtil.invokeToJSONObject(
					JSONUtil.put(
						OBJECT_FIELD_NAME_3, RandomTestUtil.randomString()
					).toString(),
					endpoint3 + "/by-external-reference-code/ERC2_2",
					Http.Method.PATCH)
		);

		HTTPTestUtil.customize(
		).withCredentials(
			user1.getEmailAddress(), password
		).apply(
			() ->
				depth2JSONObjects[1] = HTTPTestUtil.invokeToJSONObject(
					JSONUtil.put(
						OBJECT_FIELD_NAME_3, RandomTestUtil.randomString()
					).toString(),
					endpoint3 + "/by-external-reference-code/ERC1_2",
					Http.Method.PATCH)
		);

		try {

			// Depth 1

			_testSortByFieldNamesAndId(
				endpoint1, jsonObject1, jsonObject2, jsonObject3, jsonObject4,
				String.format("%s/dateCreated", objectRelationship1.getName()));
			_testSortByFieldNamesAndId(
				endpoint1, jsonObject1, jsonObject2, jsonObject3, jsonObject4,
				String.format(
					"%s/dateModified", objectRelationship1.getName()));
			_testSortByFieldNamesAndId(
				endpoint1, jsonObject1, jsonObject2, jsonObject3, jsonObject4,
				String.format("%s/id", objectRelationship1.getName()));
			_testSortByFieldNamesAndId(
				endpoint1, jsonObject3, jsonObject4, jsonObject1, jsonObject2,
				String.format("%s/creator", objectRelationship1.getName()));
			_testSortByFieldNamesAndId(
				endpoint1, jsonObject3, jsonObject4, jsonObject1, jsonObject2,
				String.format("%s/creatorId", objectRelationship1.getName()));
			_testSortByFieldNamesAndId(
				endpoint1, jsonObject3, jsonObject4, jsonObject1, jsonObject2,
				String.format(
					"%s/externalReferenceCode", objectRelationship1.getName()));
			_testSortByFieldNamesAndId(
				endpoint1, jsonObject3, jsonObject4, jsonObject1, jsonObject2,
				String.format("%s/status", objectRelationship1.getName()));
			_testSortByFieldNamesAndId(
				endpoint1, jsonObject3, jsonObject4, jsonObject1, jsonObject2,
				String.format("%s/userId", objectRelationship1.getName()));

			// Depth 2

			_testSortByFieldNamesAndId(
				endpoint1, jsonObject1, jsonObject2, jsonObject3, jsonObject4,
				String.format(
					"%s/%s/dateCreated", objectRelationship1.getName(),
					objectRelationship2.getName()));
			_testSortByFieldNamesAndId(
				endpoint1, jsonObject1, jsonObject2, jsonObject3, jsonObject4,
				String.format(
					"%s/%s/dateModified", objectRelationship1.getName(),
					objectRelationship2.getName()));
			_testSortByFieldNamesAndId(
				endpoint1, jsonObject1, jsonObject2, jsonObject3, jsonObject4,
				String.format(
					"%s/%s/id", objectRelationship1.getName(),
					objectRelationship2.getName()));
			_testSortByFieldNamesAndId(
				endpoint1, jsonObject3, jsonObject4, jsonObject1, jsonObject2,
				String.format(
					"%s/%s/creator", objectRelationship1.getName(),
					objectRelationship2.getName()));
			_testSortByFieldNamesAndId(
				endpoint1, jsonObject3, jsonObject4, jsonObject1, jsonObject2,
				String.format(
					"%s/%s/creatorId", objectRelationship1.getName(),
					objectRelationship2.getName()));
			_testSortByFieldNamesAndId(
				endpoint1, jsonObject3, jsonObject4, jsonObject1, jsonObject2,
				String.format(
					"%s/%s/externalReferenceCode",
					objectRelationship1.getName(),
					objectRelationship2.getName()));
			_testSortByFieldNamesAndId(
				endpoint1, jsonObject3, jsonObject4, jsonObject1, jsonObject2,
				String.format(
					"%s/%s/status", objectRelationship1.getName(),
					objectRelationship2.getName()));

			// Sort by several fields

			_testSortByFieldNamesAndId(
				endpoint1, jsonObject1, jsonObject2, jsonObject3, jsonObject4,
				String.format("%s/dateCreated", objectRelationship1.getName()),
				String.format("%s/id", objectRelationship1.getName()),
				String.format(
					"%s/%s/dateCreated", objectRelationship1.getName(),
					objectRelationship2.getName()),
				String.format(
					"%s/%s/id", objectRelationship1.getName(),
					objectRelationship2.getName()));
			_testSortByFieldNamesAndId(
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
			_deleteObjectEntries(
				jsonObject1, jsonObject2, jsonObject3, jsonObject4,
				depth1JSONObjects[0], depth1JSONObjects[1],
				depth2JSONObjects[0], depth2JSONObjects[1]);
		}
	}

	@Test
	public void testSortByOneToManyRelationshipCustomObjectFields()
		throws Exception {

		ObjectRelationship objectRelationship1 =
			ObjectRelationshipTestUtil.addObjectRelationship(
				getObjectDefinition1(), getObjectDefinition2(),
				TestPropsValues.getUserId(),
				ObjectRelationshipConstants.TYPE_ONE_TO_MANY);
		ObjectRelationship objectRelationship2 =
			ObjectRelationshipTestUtil.addObjectRelationship(
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
		float randomFloat = RandomTestUtil.randomFloat();
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
				dateTimeFormat.format(randomDate2)
			).put(
				OBJECT_FIELD_NAME_DECIMAL, randomFloat
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
				dateFormat.format(
					new Date(randomDate1.getTime() + (2 * 24 * 3600 * 1000)))
			).put(
				OBJECT_FIELD_NAME_DATE_TIME_INPUT,
				dateTimeFormat.format(new Date(randomDate2.getTime() + 2000))
			).put(
				OBJECT_FIELD_NAME_DECIMAL, randomFloat + 2
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
				dateFormat.format(
					new Date(randomDate1.getTime() + (24 * 3600 * 1000)))
			).put(
				OBJECT_FIELD_NAME_DATE_TIME_INPUT,
				dateTimeFormat.format(new Date(randomDate2.getTime() + 1000))
			).put(
				OBJECT_FIELD_NAME_DECIMAL, randomFloat + 1
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
				dateFormat.format(
					new Date(randomDate1.getTime() + (3 * 24 * 3600 * 1000)))
			).put(
				OBJECT_FIELD_NAME_DATE_TIME_INPUT,
				dateTimeFormat.format(new Date(randomDate2.getTime() + 3000))
			).put(
				OBJECT_FIELD_NAME_DECIMAL, randomFloat + 3
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
				dateTimeFormat.format(randomDate2)
			).put(
				OBJECT_FIELD_NAME_DECIMAL, randomFloat
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
				dateFormat.format(
					new Date(randomDate1.getTime() + (2 * 24 * 3600 * 1000)))
			).put(
				OBJECT_FIELD_NAME_DATE_TIME_INPUT,
				dateTimeFormat.format(new Date(randomDate2.getTime() + 2000))
			).put(
				OBJECT_FIELD_NAME_DECIMAL, randomFloat + 2
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
				dateFormat.format(
					new Date(randomDate1.getTime() + (24 * 3600 * 1000)))
			).put(
				OBJECT_FIELD_NAME_DATE_TIME_INPUT,
				dateTimeFormat.format(new Date(randomDate2.getTime() + 1000))
			).put(
				OBJECT_FIELD_NAME_DECIMAL, randomFloat + 1
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
				dateFormat.format(
					new Date(randomDate1.getTime() + (3 * 24 * 3600 * 1000)))
			).put(
				OBJECT_FIELD_NAME_DATE_TIME_INPUT,
				dateTimeFormat.format(new Date(randomDate2.getTime() + 3000))
			).put(
				OBJECT_FIELD_NAME_DECIMAL, randomFloat + 3
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

		_relateObjectEntries(
			endpoint1, jsonObject1, objectRelationship1, depth1JSONObject1);
		_relateObjectEntries(
			endpoint1, jsonObject1, objectRelationship1, depth1JSONObject2);
		_relateObjectEntries(
			endpoint1, jsonObject2, objectRelationship1, depth1JSONObject3);
		_relateObjectEntries(
			endpoint1, jsonObject2, objectRelationship1, depth1JSONObject4);

		_relateObjectEntries(
			endpoint2, depth1JSONObject1, objectRelationship2,
			depth2JSONObject1);
		_relateObjectEntries(
			endpoint2, depth1JSONObject2, objectRelationship2,
			depth2JSONObject2);
		_relateObjectEntries(
			endpoint2, depth1JSONObject3, objectRelationship2,
			depth2JSONObject3);
		_relateObjectEntries(
			endpoint2, depth1JSONObject4, objectRelationship2,
			depth2JSONObject4);

		try {

			// Depth 1

			_testSortByOneToManyRelationshipCustomObjectFields(
				endpoint1, jsonObject1, jsonObject2, endpoint2,
				depth1JSONObject1, depth1JSONObject2, depth1JSONObject3,
				depth1JSONObject4,
				String.format(
					"%s/%s", objectRelationship1.getName(),
					OBJECT_FIELD_NAME_BOOLEAN));
			_testSortByOneToManyRelationshipCustomObjectFields(
				endpoint1, jsonObject1, jsonObject2, endpoint2,
				depth1JSONObject1, depth1JSONObject2, depth1JSONObject3,
				depth1JSONObject4,
				String.format(
					"%s/%s", objectRelationship1.getName(),
					OBJECT_FIELD_NAME_DATE));
			_testSortByOneToManyRelationshipCustomObjectFields(
				endpoint1, jsonObject1, jsonObject2, endpoint2,
				depth1JSONObject1, depth1JSONObject2, depth1JSONObject3,
				depth1JSONObject4,
				String.format(
					"%s/%s", objectRelationship1.getName(),
					OBJECT_FIELD_NAME_DATE_TIME_INPUT));
			_testSortByOneToManyRelationshipCustomObjectFields(
				endpoint1, jsonObject1, jsonObject2, endpoint2,
				depth1JSONObject1, depth1JSONObject2, depth1JSONObject3,
				depth1JSONObject4,
				String.format(
					"%s/%s", objectRelationship1.getName(),
					OBJECT_FIELD_NAME_DECIMAL));
			_testSortByOneToManyRelationshipCustomObjectFields(
				endpoint1, jsonObject1, jsonObject2, endpoint2,
				depth1JSONObject1, depth1JSONObject2, depth1JSONObject3,
				depth1JSONObject4,
				String.format(
					"%s/%s", objectRelationship1.getName(),
					OBJECT_FIELD_NAME_INTEGER));
			_testSortByOneToManyRelationshipCustomObjectFields(
				endpoint1, jsonObject1, jsonObject2, endpoint2,
				depth1JSONObject1, depth1JSONObject2, depth1JSONObject3,
				depth1JSONObject4,
				String.format(
					"%s/%s", objectRelationship1.getName(),
					OBJECT_FIELD_NAME_LONG_INTEGER));
			_testSortByOneToManyRelationshipCustomObjectFields(
				endpoint1, jsonObject1, jsonObject2, endpoint2,
				depth1JSONObject1, depth1JSONObject2, depth1JSONObject3,
				depth1JSONObject4,
				String.format(
					"%s/%s", objectRelationship1.getName(),
					OBJECT_FIELD_NAME_LONG_TEXT));
			_testSortByOneToManyRelationshipCustomObjectFields(
				endpoint1, jsonObject1, jsonObject2, endpoint2,
				depth1JSONObject1, depth1JSONObject2, depth1JSONObject3,
				depth1JSONObject4,
				String.format(
					"%s/%s", objectRelationship1.getName(),
					OBJECT_FIELD_NAME_MULTISELECT_PICKLIST));
			_testSortByOneToManyRelationshipCustomObjectFields(
				endpoint1, jsonObject1, jsonObject2, endpoint2,
				depth1JSONObject1, depth1JSONObject2, depth1JSONObject3,
				depth1JSONObject4,
				String.format(
					"%s/%s", objectRelationship1.getName(),
					OBJECT_FIELD_NAME_PICKLIST));
			_testSortByOneToManyRelationshipCustomObjectFields(
				endpoint1, jsonObject1, jsonObject2, endpoint2,
				depth1JSONObject1, depth1JSONObject2, depth1JSONObject3,
				depth1JSONObject4,
				String.format(
					"%s/%s", objectRelationship1.getName(),
					OBJECT_FIELD_NAME_PRECISION_DECIMAL));
			_testSortByOneToManyRelationshipCustomObjectFields(
				endpoint1, jsonObject1, jsonObject2, endpoint2,
				depth1JSONObject1, depth1JSONObject2, depth1JSONObject3,
				depth1JSONObject4,
				String.format(
					"%s/%s", objectRelationship1.getName(),
					OBJECT_FIELD_NAME_TEXT));

			// Depth 2

			_testSortByOneToManyRelationshipCustomObjectFields(
				endpoint1, jsonObject1, jsonObject2, endpoint3,
				depth2JSONObject1, depth2JSONObject2, depth2JSONObject3,
				depth2JSONObject4,
				String.format(
					"%s/%s/%s", objectRelationship1.getName(),
					objectRelationship2.getName(), OBJECT_FIELD_NAME_BOOLEAN));
			_testSortByOneToManyRelationshipCustomObjectFields(
				endpoint1, jsonObject1, jsonObject2, endpoint3,
				depth2JSONObject1, depth2JSONObject2, depth2JSONObject3,
				depth2JSONObject4,
				String.format(
					"%s/%s/%s", objectRelationship1.getName(),
					objectRelationship2.getName(), OBJECT_FIELD_NAME_DATE));
			_testSortByOneToManyRelationshipCustomObjectFields(
				endpoint1, jsonObject1, jsonObject2, endpoint3,
				depth2JSONObject1, depth2JSONObject2, depth2JSONObject3,
				depth2JSONObject4,
				String.format(
					"%s/%s/%s", objectRelationship1.getName(),
					objectRelationship2.getName(),
					OBJECT_FIELD_NAME_DATE_TIME_INPUT));
			_testSortByOneToManyRelationshipCustomObjectFields(
				endpoint1, jsonObject1, jsonObject2, endpoint3,
				depth2JSONObject1, depth2JSONObject2, depth2JSONObject3,
				depth2JSONObject4,
				String.format(
					"%s/%s/%s", objectRelationship1.getName(),
					objectRelationship2.getName(), OBJECT_FIELD_NAME_DECIMAL));
			_testSortByOneToManyRelationshipCustomObjectFields(
				endpoint1, jsonObject1, jsonObject2, endpoint3,
				depth2JSONObject1, depth2JSONObject2, depth2JSONObject3,
				depth2JSONObject4,
				String.format(
					"%s/%s/%s", objectRelationship1.getName(),
					objectRelationship2.getName(), OBJECT_FIELD_NAME_INTEGER));
			_testSortByOneToManyRelationshipCustomObjectFields(
				endpoint1, jsonObject1, jsonObject2, endpoint3,
				depth2JSONObject1, depth2JSONObject2, depth2JSONObject3,
				depth2JSONObject4,
				String.format(
					"%s/%s/%s", objectRelationship1.getName(),
					objectRelationship2.getName(),
					OBJECT_FIELD_NAME_LONG_INTEGER));
			_testSortByOneToManyRelationshipCustomObjectFields(
				endpoint1, jsonObject1, jsonObject2, endpoint3,
				depth2JSONObject1, depth2JSONObject2, depth2JSONObject3,
				depth2JSONObject4,
				String.format(
					"%s/%s/%s", objectRelationship1.getName(),
					objectRelationship2.getName(),
					OBJECT_FIELD_NAME_LONG_TEXT));
			_testSortByOneToManyRelationshipCustomObjectFields(
				endpoint1, jsonObject1, jsonObject2, endpoint3,
				depth2JSONObject1, depth2JSONObject2, depth2JSONObject3,
				depth2JSONObject4,
				String.format(
					"%s/%s/%s", objectRelationship1.getName(),
					objectRelationship2.getName(),
					OBJECT_FIELD_NAME_MULTISELECT_PICKLIST));
			_testSortByOneToManyRelationshipCustomObjectFields(
				endpoint1, jsonObject1, jsonObject2, endpoint3,
				depth2JSONObject1, depth2JSONObject2, depth2JSONObject3,
				depth2JSONObject4,
				String.format(
					"%s/%s/%s", objectRelationship1.getName(),
					objectRelationship2.getName(), OBJECT_FIELD_NAME_PICKLIST));
			_testSortByOneToManyRelationshipCustomObjectFields(
				endpoint1, jsonObject1, jsonObject2, endpoint3,
				depth2JSONObject1, depth2JSONObject2, depth2JSONObject3,
				depth2JSONObject4,
				String.format(
					"%s/%s/%s", objectRelationship1.getName(),
					objectRelationship2.getName(),
					OBJECT_FIELD_NAME_PRECISION_DECIMAL));
			_testSortByOneToManyRelationshipCustomObjectFields(
				endpoint1, jsonObject1, jsonObject2, endpoint3,
				depth2JSONObject1, depth2JSONObject2, depth2JSONObject3,
				depth2JSONObject4,
				String.format(
					"%s/%s/%s", objectRelationship1.getName(),
					objectRelationship2.getName(), OBJECT_FIELD_NAME_TEXT));

			// Sort by several fields

			_testSortByOneToManyRelationshipCustomObjectFields(
				endpoint1, jsonObject1, jsonObject2, endpoint2,
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
				endpoint1, jsonObject1, jsonObject2, endpoint3,
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
			_deleteObjectEntries(
				depth2JSONObject1, depth2JSONObject2, depth2JSONObject3,
				depth2JSONObject4, depth1JSONObject1, depth1JSONObject2,
				depth1JSONObject3, depth1JSONObject4, jsonObject1, jsonObject2);
		}
	}

	@Test
	public void testSortByOneToManyRelationshipSystemObjectFields()
		throws Exception {

		ObjectDefinition objectDefinition1 = getObjectDefinition1();
		ObjectDefinition objectDefinition2 = getObjectDefinition2();

		ObjectRelationship objectRelationship1 =
			ObjectRelationshipTestUtil.addObjectRelationship(
				objectDefinition1, objectDefinition2,
				TestPropsValues.getUserId(),
				ObjectRelationshipConstants.TYPE_ONE_TO_MANY);

		ObjectDefinition objectDefinition3 = getObjectDefinition3();

		ObjectRelationship objectRelationship2 =
			ObjectRelationshipTestUtil.addObjectRelationship(
				objectDefinition2, objectDefinition3,
				TestPropsValues.getUserId(),
				ObjectRelationshipConstants.TYPE_ONE_TO_MANY);

		Role role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		addResourcePermission(
			ObjectActionKeys.ADD_OBJECT_ENTRY,
			objectDefinition2.getResourceName(), role);
		addResourcePermission(
			ObjectActionKeys.ADD_OBJECT_ENTRY,
			objectDefinition3.getResourceName(), role);
		addResourcePermission(
			ActionKeys.VIEW, objectDefinition1.getClassName(), role);

		String password = RandomTestUtil.randomString();
		String randomString = RandomTestUtil.randomString(
			UniqueStringRandomizerBumper.INSTANCE);

		User user1 = addUser("test1" + randomString, password);
		User user2 = addUser("test2" + randomString, password);
		User user3 = addUser("test3" + randomString, password);

		roleLocalService.addUserRole(user1.getUserId(), role.getRoleId());
		roleLocalService.addUserRole(user2.getUserId(), role.getRoleId());
		roleLocalService.addUserRole(user3.getUserId(), role.getRoleId());

		objectDefinition1.setEnableObjectEntryDraft(true);

		objectDefinition2.setEnableObjectEntryDraft(true);

		objectDefinition3.setEnableObjectEntryDraft(true);

		objectDefinition1 = objectDefinitionLocalService.updateObjectDefinition(
			objectDefinition1);
		objectDefinition2 = objectDefinitionLocalService.updateObjectDefinition(
			objectDefinition2);
		objectDefinition3 = objectDefinitionLocalService.updateObjectDefinition(
			objectDefinition3);

		String endpoint1 = getEndpoint(objectDefinition1, testGroupId);
		String endpoint2 = getEndpoint(objectDefinition2, testGroupId);
		String endpoint3 = getEndpoint(objectDefinition3, testGroupId);

		JSONObject[] depth1JSONObjects = new JSONObject[4];

		HTTPTestUtil.customize(
		).withCredentials(
			user3.getEmailAddress(), password
		).apply(
			() ->
				depth1JSONObjects[0] = HTTPTestUtil.invokeToJSONObject(
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
			user2.getEmailAddress(), password
		).apply(
			() ->
				depth1JSONObjects[1] = HTTPTestUtil.invokeToJSONObject(
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
			user2.getEmailAddress(), password
		).apply(
			() ->
				depth1JSONObjects[2] = HTTPTestUtil.invokeToJSONObject(
					JSONUtil.put(
						"externalReferenceCode", "ERC3"
					).toString(),
					endpoint2, Http.Method.POST)
		);

		HTTPTestUtil.customize(
		).withCredentials(
			user1.getEmailAddress(), password
		).apply(
			() ->
				depth1JSONObjects[3] = HTTPTestUtil.invokeToJSONObject(
					JSONUtil.put(
						"externalReferenceCode", "ERC1"
					).toString(),
					endpoint2, Http.Method.POST)
		);

		JSONObject[] depth2JSONObjects = new JSONObject[4];

		HTTPTestUtil.customize(
		).withCredentials(
			user3.getEmailAddress(), password
		).apply(
			() ->
				depth2JSONObjects[0] = HTTPTestUtil.invokeToJSONObject(
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
			user2.getEmailAddress(), password
		).apply(
			() ->
				depth2JSONObjects[1] = HTTPTestUtil.invokeToJSONObject(
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
			user2.getEmailAddress(), password
		).apply(
			() ->
				depth2JSONObjects[2] = HTTPTestUtil.invokeToJSONObject(
					JSONUtil.put(
						"externalReferenceCode", "ERC3_2"
					).toString(),
					endpoint3, Http.Method.POST)
		);

		HTTPTestUtil.customize(
		).withCredentials(
			user1.getEmailAddress(), password
		).apply(
			() ->
				depth2JSONObjects[3] = HTTPTestUtil.invokeToJSONObject(
					JSONUtil.put(
						"externalReferenceCode", "ERC1_2"
					).toString(),
					endpoint3, Http.Method.POST)
		);

		JSONObject jsonObject1 = HTTPTestUtil.invokeToJSONObject(
			JSONFactoryUtil.getNullJSON(), endpoint1, Http.Method.POST);

		JSONObject jsonObject2 = HTTPTestUtil.invokeToJSONObject(
			JSONFactoryUtil.getNullJSON(), endpoint1, Http.Method.POST);

		_relateObjectEntries(
			endpoint1, jsonObject1, objectRelationship1, depth1JSONObjects[0]);
		_relateObjectEntries(
			endpoint1, jsonObject1, objectRelationship1, depth1JSONObjects[1]);
		_relateObjectEntries(
			endpoint1, jsonObject2, objectRelationship1, depth1JSONObjects[2]);
		_relateObjectEntries(
			endpoint1, jsonObject2, objectRelationship1, depth1JSONObjects[3]);

		_relateObjectEntries(
			endpoint2, depth1JSONObjects[0], objectRelationship2,
			depth2JSONObjects[0]);
		_relateObjectEntries(
			endpoint2, depth1JSONObjects[1], objectRelationship2,
			depth2JSONObjects[1]);
		_relateObjectEntries(
			endpoint2, depth1JSONObjects[2], objectRelationship2,
			depth2JSONObjects[2]);
		_relateObjectEntries(
			endpoint2, depth1JSONObjects[3], objectRelationship2,
			depth2JSONObjects[3]);

		HTTPTestUtil.customize(
		).withCredentials(
			user3.getEmailAddress(), password
		).apply(
			() ->
				depth1JSONObjects[0] = HTTPTestUtil.invokeToJSONObject(
					JSONUtil.put(
						OBJECT_FIELD_NAME_2, RandomTestUtil.randomString()
					).toString(),
					endpoint2 + "/by-external-reference-code/ERC4",
					Http.Method.PATCH)
		);

		HTTPTestUtil.customize(
		).withCredentials(
			user2.getEmailAddress(), password
		).apply(
			() ->
				depth1JSONObjects[1] = HTTPTestUtil.invokeToJSONObject(
					JSONUtil.put(
						OBJECT_FIELD_NAME_2, RandomTestUtil.randomString()
					).toString(),
					endpoint2 + "/by-external-reference-code/ERC2",
					Http.Method.PATCH)
		);

		HTTPTestUtil.customize(
		).withCredentials(
			user3.getEmailAddress(), password
		).apply(
			() ->
				depth2JSONObjects[0] = HTTPTestUtil.invokeToJSONObject(
					JSONUtil.put(
						OBJECT_FIELD_NAME_3, RandomTestUtil.randomString()
					).toString(),
					endpoint3 + "/by-external-reference-code/ERC4_2",
					Http.Method.PATCH)
		);

		HTTPTestUtil.customize(
		).withCredentials(
			user2.getEmailAddress(), password
		).apply(
			() ->
				depth2JSONObjects[1] = HTTPTestUtil.invokeToJSONObject(
					JSONUtil.put(
						OBJECT_FIELD_NAME_3, RandomTestUtil.randomString()
					).toString(),
					endpoint3 + "/by-external-reference-code/ERC2_2",
					Http.Method.PATCH)
		);

		try {

			// Depth 1

			_testSortByFieldNames(
				endpoint1, jsonObject1, jsonObject2,
				String.format("%s/dateCreated", objectRelationship1.getName()));
			_testSortByFieldNames(
				endpoint1, jsonObject1, jsonObject2,
				String.format("%s/id", objectRelationship1.getName()));
			_testSortByFieldNames(
				endpoint1, jsonObject2, jsonObject1,
				String.format("%s/creator", objectRelationship1.getName()));
			_testSortByFieldNames(
				endpoint1, jsonObject2, jsonObject1,
				String.format("%s/creatorId", objectRelationship1.getName()));
			_testSortByFieldNames(
				endpoint1, jsonObject2, jsonObject1,
				String.format(
					"%s/dateModified", objectRelationship1.getName()));
			_testSortByFieldNames(
				endpoint1, jsonObject2, jsonObject1,
				String.format(
					"%s/externalReferenceCode", objectRelationship1.getName()));
			_testSortByFieldNames(
				endpoint1, jsonObject2, jsonObject1,
				String.format("%s/status", objectRelationship1.getName()));
			_testSortByFieldNames(
				endpoint1, jsonObject2, jsonObject1,
				String.format("%s/userId", objectRelationship1.getName()));

			// Depth 2

			_testSortByFieldNames(
				endpoint1, jsonObject1, jsonObject2,
				String.format(
					"%s/%s/dateCreated", objectRelationship1.getName(),
					objectRelationship2.getName()));
			_testSortByFieldNames(
				endpoint1, jsonObject1, jsonObject2,
				String.format(
					"%s/%s/id", objectRelationship1.getName(),
					objectRelationship2.getName()));
			_testSortByFieldNames(
				endpoint1, jsonObject2, jsonObject1,
				String.format(
					"%s/%s/creator", objectRelationship1.getName(),
					objectRelationship2.getName()));
			_testSortByFieldNames(
				endpoint1, jsonObject2, jsonObject1,
				String.format(
					"%s/%s/creatorId", objectRelationship1.getName(),
					objectRelationship2.getName()));
			_testSortByFieldNames(
				endpoint1, jsonObject2, jsonObject1,
				String.format(
					"%s/%s/dateModified", objectRelationship1.getName(),
					objectRelationship2.getName()));
			_testSortByFieldNames(
				endpoint1, jsonObject2, jsonObject1,
				String.format(
					"%s/%s/externalReferenceCode",
					objectRelationship1.getName(),
					objectRelationship2.getName()));
			_testSortByFieldNames(
				endpoint1, jsonObject2, jsonObject1,
				String.format(
					"%s/%s/status", objectRelationship1.getName(),
					objectRelationship2.getName()));

			// Sort by several fields

			_testSortByFieldNames(
				endpoint1, jsonObject1, jsonObject2,
				String.format("%s/dateCreated", objectRelationship1.getName()),
				String.format("%s/id", objectRelationship1.getName()),
				String.format(
					"%s/%s/dateCreated", objectRelationship1.getName(),
					objectRelationship2.getName()),
				String.format(
					"%s/%s/id", objectRelationship1.getName(),
					objectRelationship2.getName()));
			_testSortByFieldNames(
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
			_deleteObjectEntries(
				depth2JSONObjects[0], depth2JSONObjects[1],
				depth2JSONObjects[2], depth2JSONObjects[3],
				depth1JSONObjects[0], depth1JSONObjects[1],
				depth1JSONObjects[2], depth1JSONObjects[3], jsonObject1,
				jsonObject2);
		}
	}

	@Test
	public void testSortBySystemObjectField() throws Exception {
		Role role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		ObjectDefinition objectDefinition = getObjectDefinition1();

		addResourcePermission(
			ObjectActionKeys.ADD_OBJECT_ENTRY,
			objectDefinition.getResourceName(), role);

		String password = RandomTestUtil.randomString();
		String randomString = RandomTestUtil.randomString(
			UniqueStringRandomizerBumper.INSTANCE);

		User user1 = addUser("test1" + randomString, password);
		User user2 = addUser("test2" + randomString, password);

		roleLocalService.addUserRole(user1.getUserId(), role.getRoleId());
		roleLocalService.addUserRole(user2.getUserId(), role.getRoleId());

		objectDefinition.setEnableObjectEntryDraft(true);

		objectDefinition = objectDefinitionLocalService.updateObjectDefinition(
			objectDefinition);

		String endpoint = getEndpoint(objectDefinition, testGroupId);

		JSONObject[] jsonObjects = new JSONObject[2];

		HTTPTestUtil.customize(
		).withCredentials(
			user2.getEmailAddress(), password
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
			user1.getEmailAddress(), password
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
			user1.getEmailAddress(), password
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
			user2.getEmailAddress(), password
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
			_testSortByFieldNames(
				endpoint, jsonObjects[0], jsonObjects[1], "dateCreated");
			_testSortByFieldNames(
				endpoint, jsonObjects[0], jsonObjects[1], "id");
			_testSortByFieldNames(
				endpoint, jsonObjects[1], jsonObjects[0], "creator");
			_testSortByFieldNames(
				endpoint, jsonObjects[1], jsonObjects[0], "creatorId");
			_testSortByFieldNames(
				endpoint, jsonObjects[1], jsonObjects[0], "dateModified");
			_testSortByFieldNames(
				endpoint, jsonObjects[1], jsonObjects[0],
				"externalReferenceCode");
			_testSortByFieldNames(
				endpoint, jsonObjects[1], jsonObjects[0], "status");
			_testSortByFieldNames(
				endpoint, jsonObjects[1], jsonObjects[0], "userId");

			// Sort by several fields

			_testSortByFieldNames(
				endpoint, jsonObjects[0], jsonObjects[1], "dateCreated", "id");
			_testSortByFieldNames(
				endpoint, jsonObjects[1], jsonObjects[0], "creator",
				"dateModified", "externalReferenceCode");
		}
		finally {
			_deleteObjectEntries(jsonObjects);
		}
	}

	@Test
	public void testSortByUnsupportedObjectFields() throws Exception {
		ObjectRelationship objectRelationship =
			ObjectRelationshipTestUtil.addObjectRelationship(
				getObjectDefinition1(), getObjectDefinition2(),
				TestPropsValues.getUserId(),
				ObjectRelationshipConstants.TYPE_MANY_TO_MANY);

		_testSortByUnsupportedObjectField(
			"Unable to sort by a many to many related object field",
			getObjectDefinition1(),
			String.format(
				"%s/%s:asc", objectRelationship.getName(),
				OBJECT_FIELD_NAME_TEXT));

		_testSortByUnsupportedObjectField(
			"Unable to sort by property: objectDefinitionId",
			getObjectDefinition1(), "objectDefinitionId");
		_testSortByUnsupportedObjectField(
			"Unable to sort by property: siteId",
			getSiteScopedObjectDefinition1(), "siteId");
	}

	private void _deleteObjectEntries(JSONObject... jsonObjects)
		throws Exception {

		for (JSONObject jsonObject : jsonObjects) {
			objectEntryLocalService.deleteObjectEntry(jsonObject.getLong("id"));
		}
	}

	private void _relateObjectEntries(
			String endpoint, JSONObject jsonObject,
			ObjectRelationship objectRelationship, JSONObject relatedJSONObject)
		throws Exception {

		HTTPTestUtil.invokeToJSONObject(
			null,
			String.format(
				"%s/%d/%s/%d", endpoint, jsonObject.getLong("id"),
				objectRelationship.getName(), relatedJSONObject.getLong("id")),
			Http.Method.PUT);
	}

	private void _testSortByCustomObjectField(
			String endpoint, JSONObject expectedJSONObject1,
			JSONObject expectedJSONObject2, String... fieldNames)
		throws Exception {

		_testSortByFieldNames(
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
			HTTPTestUtil.invokeToJSONObject(
				valuesJSONObject2.toString(),
				endpoint + "/" + expectedJSONObject1.getLong("id"),
				Http.Method.PATCH);

			HTTPTestUtil.invokeToJSONObject(
				valuesJSONObject1.toString(),
				endpoint + "/" + expectedJSONObject2.getLong("id"),
				Http.Method.PATCH);

			_testSortByFieldNames(
				endpoint, expectedJSONObject2, expectedJSONObject1, fieldNames);
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

	private void _testSortByFieldNames(
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

	private void _testSortByFieldNamesAndId(
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

	private void
			_testSortByManyToOneAndOneToManyRelationshipsCustomObjectFields(
				String endpoint, JSONObject expectedJSONObject1,
				JSONObject expectedJSONObject2, JSONObject expectedJSONObject3,
				JSONObject expectedJSONObject4, String relatedEndpoint,
				JSONObject relatedJSONObject1, JSONObject relatedJSONObject2,
				JSONObject relatedJSONObject3, JSONObject relatedJSONObject4,
				String... fieldNames)
		throws Exception {

		_testSortByFieldNamesAndId(
			endpoint, expectedJSONObject1, expectedJSONObject2,
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
				relatedEndpoint + "/" + relatedJSONObject1.getLong("id"),
				Http.Method.PATCH);

			HTTPTestUtil.invokeToJSONObject(
				valuesJSONObject4.toString(),
				relatedEndpoint + "/" + relatedJSONObject2.getLong("id"),
				Http.Method.PATCH);

			HTTPTestUtil.invokeToJSONObject(
				valuesJSONObject1.toString(),
				relatedEndpoint + "/" + relatedJSONObject3.getLong("id"),
				Http.Method.PATCH);

			HTTPTestUtil.invokeToJSONObject(
				valuesJSONObject2.toString(),
				relatedEndpoint + "/" + relatedJSONObject4.getLong("id"),
				Http.Method.PATCH);

			_testSortByFieldNamesAndId(
				endpoint, expectedJSONObject3, expectedJSONObject4,
				expectedJSONObject1, expectedJSONObject2, fieldNames);
		}
		finally {
			HTTPTestUtil.invokeToJSONObject(
				valuesJSONObject1.toString(),
				relatedEndpoint + "/" + relatedJSONObject1.getLong("id"),
				Http.Method.PATCH);

			HTTPTestUtil.invokeToJSONObject(
				valuesJSONObject2.toString(),
				relatedEndpoint + "/" + relatedJSONObject2.getLong("id"),
				Http.Method.PATCH);

			HTTPTestUtil.invokeToJSONObject(
				valuesJSONObject3.toString(),
				relatedEndpoint + "/" + relatedJSONObject3.getLong("id"),
				Http.Method.PATCH);

			HTTPTestUtil.invokeToJSONObject(
				valuesJSONObject4.toString(),
				relatedEndpoint + "/" + relatedJSONObject4.getLong("id"),
				Http.Method.PATCH);
		}
	}

	private void _testSortByManyToOneRelationshipCustomObjectFields(
			String endpoint, JSONObject expectedJSONObject1,
			JSONObject expectedJSONObject2, JSONObject expectedJSONObject3,
			JSONObject expectedJSONObject4, String relatedEndpoint,
			JSONObject relatedJSONObject1, JSONObject relatedJSONObject2,
			String... fieldNames)
		throws Exception {

		_testSortByFieldNamesAndId(
			endpoint, expectedJSONObject1, expectedJSONObject2,
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
				relatedEndpoint + "/" + relatedJSONObject1.getLong("id"),
				Http.Method.PATCH);

			HTTPTestUtil.invokeToJSONObject(
				valuesJSONObject1.toString(),
				relatedEndpoint + "/" + relatedJSONObject2.getLong("id"),
				Http.Method.PATCH);

			_testSortByFieldNamesAndId(
				endpoint, expectedJSONObject3, expectedJSONObject4,
				expectedJSONObject1, expectedJSONObject2, fieldNames);
		}
		finally {
			HTTPTestUtil.invokeToJSONObject(
				valuesJSONObject1.toString(),
				relatedEndpoint + "/" + relatedJSONObject1.getLong("id"),
				Http.Method.PATCH);

			HTTPTestUtil.invokeToJSONObject(
				valuesJSONObject2.toString(),
				relatedEndpoint + "/" + relatedJSONObject2.getLong("id"),
				Http.Method.PATCH);
		}
	}

	private void _testSortByOneToManyRelationshipCustomObjectFields(
			String endpoint, JSONObject expectedJSONObject1,
			JSONObject expectedJSONObject2, String relatedEndpoint,
			JSONObject relatedJSONObject1, JSONObject relatedJSONObject2,
			JSONObject relatedJSONObject3, JSONObject relatedJSONObject4,
			String... fieldNames)
		throws Exception {

		_testSortByFieldNames(
			endpoint, expectedJSONObject1, expectedJSONObject2, fieldNames);

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
				relatedEndpoint + "/" + relatedJSONObject1.getLong("id"),
				Http.Method.PATCH);

			HTTPTestUtil.invokeToJSONObject(
				valuesJSONObject4.toString(),
				relatedEndpoint + "/" + relatedJSONObject2.getLong("id"),
				Http.Method.PATCH);

			HTTPTestUtil.invokeToJSONObject(
				valuesJSONObject1.toString(),
				relatedEndpoint + "/" + relatedJSONObject3.getLong("id"),
				Http.Method.PATCH);

			HTTPTestUtil.invokeToJSONObject(
				valuesJSONObject2.toString(),
				relatedEndpoint + "/" + relatedJSONObject4.getLong("id"),
				Http.Method.PATCH);

			_testSortByFieldNames(
				endpoint, expectedJSONObject2, expectedJSONObject1, fieldNames);
		}
		finally {
			HTTPTestUtil.invokeToJSONObject(
				valuesJSONObject1.toString(),
				relatedEndpoint + "/" + relatedJSONObject1.getLong("id"),
				Http.Method.PATCH);

			HTTPTestUtil.invokeToJSONObject(
				valuesJSONObject2.toString(),
				relatedEndpoint + "/" + relatedJSONObject2.getLong("id"),
				Http.Method.PATCH);

			HTTPTestUtil.invokeToJSONObject(
				valuesJSONObject3.toString(),
				relatedEndpoint + "/" + relatedJSONObject3.getLong("id"),
				Http.Method.PATCH);

			HTTPTestUtil.invokeToJSONObject(
				valuesJSONObject4.toString(),
				relatedEndpoint + "/" + relatedJSONObject4.getLong("id"),
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

			JSONAssert.assertEquals(
				JSONUtil.put(
					"status", "BAD_REQUEST"
				).put(
					"title", expectedTitle
				).toString(),
				HTTPTestUtil.invokeToString(
					null,
					getEndpoint(objectDefinition, testGroupId) + "?sort=" +
						URLCodec.encodeURL(sortString),
					Http.Method.GET),
				JSONCompareMode.STRICT);
		}
	}

}