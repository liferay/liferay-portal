/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.rest.internal.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.headless.admin.taxonomy.client.dto.v1_0.TaxonomyCategory;
import com.liferay.object.constants.ObjectFieldSettingConstants;
import com.liferay.object.constants.ObjectRelationshipConstants;
import com.liferay.object.field.setting.util.ObjectFieldSettingUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.rest.test.util.ObjectEntryTestUtil;
import com.liferay.object.rest.test.util.UserAccountTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.test.randomizerbumpers.UniqueStringRandomizerBumper;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.HTTPTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.URLCodec;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.io.Serializable;

import java.util.Arrays;
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
public class ObjectEntryResourceFilterByRelatedObjectEntriesTest
	extends BaseObjectEntryResourceTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testFilterByComparisonOperatorsObjectEntriesByRelatedObjectEntriesFields()
		throws Exception {

		ObjectEntry objectEntry1 = ObjectEntryTestUtil.addObjectEntry(
			getObjectDefinition1(), OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1);
		ObjectEntry objectEntry2 = ObjectEntryTestUtil.addObjectEntry(
			getObjectDefinition2(), OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2);

		_testFilterByComparisonOperatorsObjectEntriesByRelatedObjectEntriesFields(
			objectEntry1, objectEntry2,
			ObjectRelationshipConstants.TYPE_MANY_TO_MANY);
		_testFilterByComparisonOperatorsObjectEntriesByRelatedObjectEntriesFields(
			objectEntry1, objectEntry2,
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);
	}

	@Test
	public void testFilterByComparisonOperatorsObjectEntriesByRelatedObjectEntriesFieldsThroughMultipleRelationships()
		throws Exception {

		ObjectEntry objectEntry1 = ObjectEntryTestUtil.addObjectEntry(
			getObjectDefinition1(), OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1);
		ObjectEntry objectEntry2 = ObjectEntryTestUtil.addObjectEntry(
			getObjectDefinition2(), OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2);
		ObjectEntry objectEntry3 = ObjectEntryTestUtil.addObjectEntry(
			getObjectDefinition3(), OBJECT_FIELD_NAME_3, OBJECT_FIELD_VALUE_3);
		ObjectEntry objectEntry4 = ObjectEntryTestUtil.addObjectEntry(
			getObjectDefinition4(), OBJECT_FIELD_NAME_TEXT,
			OBJECT_FIELD_VALUE_4);

		ObjectRelationship objectRelationship1 =
			addObjectRelationshipAndRelateObjectEntries(
				getObjectDefinition1(), getObjectDefinition2(),
				objectEntry1.getPrimaryKey(), objectEntry2.getPrimaryKey(),
				ObjectRelationshipConstants.TYPE_MANY_TO_MANY);
		ObjectRelationship objectRelationship2 =
			addObjectRelationshipAndRelateObjectEntries(
				getObjectDefinition2(), getObjectDefinition3(),
				objectEntry2.getPrimaryKey(), objectEntry3.getPrimaryKey(),
				ObjectRelationshipConstants.TYPE_MANY_TO_MANY);

		_testFilterByComparisonOperatorsObjectEntriesByRelatedObjectEntriesFieldsThroughMultipleRelationships(
			objectEntry1, objectEntry3, objectRelationship1,
			objectRelationship2);

		objectRelationship1 = addObjectRelationshipAndRelateObjectEntries(
			getObjectDefinition1(), getObjectDefinition2(),
			objectEntry1.getPrimaryKey(), objectEntry2.getPrimaryKey(),
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);
		objectRelationship2 = addObjectRelationshipAndRelateObjectEntries(
			getObjectDefinition2(), getObjectDefinition3(),
			objectEntry2.getPrimaryKey(), objectEntry3.getPrimaryKey(),
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);

		_testFilterByComparisonOperatorsObjectEntriesByRelatedObjectEntriesFieldsThroughMultipleRelationships(
			objectEntry1, objectEntry3, objectRelationship1,
			objectRelationship2);

		objectRelationshipLocalService.deleteObjectRelationship(
			objectRelationship1);
		objectRelationshipLocalService.deleteObjectRelationship(
			objectRelationship2);

		// One to many relationships with two paths to the same destination

		objectRelationship1 = addObjectRelationshipAndRelateObjectEntries(
			getObjectDefinition1(), getObjectDefinition2(),
			objectEntry1.getPrimaryKey(), objectEntry2.getPrimaryKey(),
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);
		objectRelationship2 = addObjectRelationshipAndRelateObjectEntries(
			getObjectDefinition2(), getObjectDefinition3(),
			objectEntry2.getPrimaryKey(), objectEntry3.getPrimaryKey(),
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);
		ObjectRelationship objectRelationship3 =
			addObjectRelationshipAndRelateObjectEntries(
				getObjectDefinition3(), getObjectDefinition4(),
				objectEntry3.getPrimaryKey(), objectEntry4.getPrimaryKey(),
				ObjectRelationshipConstants.TYPE_ONE_TO_MANY);
		ObjectRelationship objectRelationship4 =
			addObjectRelationshipAndRelateObjectEntries(
				getObjectDefinition2(), getObjectDefinition3(),
				objectEntry2.getPrimaryKey(), objectEntry3.getPrimaryKey(),
				ObjectRelationshipConstants.TYPE_ONE_TO_MANY);

		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"%s/%s/%s/%s eq '%s'", objectRelationship1.getName(),
				objectRelationship2.getName(), objectRelationship3.getName(),
				OBJECT_FIELD_NAME_TEXT, OBJECT_FIELD_VALUE_4),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"%s/%s/%s/%s eq '%s'", objectRelationship1.getName(),
				objectRelationship4.getName(), objectRelationship3.getName(),
				OBJECT_FIELD_NAME_TEXT, OBJECT_FIELD_VALUE_4),
			getObjectDefinition1());
	}

	@Test
	public void testFilterByGroupingOperatorsObjectEntriesByRelatedObjectEntriesFields()
		throws Exception {

		ObjectEntry objectEntry1 = ObjectEntryTestUtil.addObjectEntry(
			getObjectDefinition1(), OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1);
		ObjectEntry objectEntry2 = ObjectEntryTestUtil.addObjectEntry(
			getObjectDefinition2(), OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2);

		_testFilterByGroupingOperatorsObjectEntriesByRelatedObjectEntriesFields(
			objectEntry1, objectEntry2,
			ObjectRelationshipConstants.TYPE_MANY_TO_MANY);
		_testFilterByGroupingOperatorsObjectEntriesByRelatedObjectEntriesFields(
			objectEntry1, objectEntry2,
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);
	}

	@Test
	public void testFilterByGroupingOperatorsObjectEntriesByRelatedObjectEntriesFieldsThroughMultipleRelationships()
		throws Exception {

		ObjectEntry objectEntry1 = ObjectEntryTestUtil.addObjectEntry(
			getObjectDefinition1(), OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1);
		ObjectEntry objectEntry2 = ObjectEntryTestUtil.addObjectEntry(
			getObjectDefinition2(), OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2);
		ObjectEntry objectEntry3 = ObjectEntryTestUtil.addObjectEntry(
			getObjectDefinition3(), OBJECT_FIELD_NAME_3, OBJECT_FIELD_VALUE_3);

		_testFilterByGroupingOperatorsObjectEntriesByRelatedObjectEntriesFieldsThroughMultipleRelationships(
			objectEntry1, objectEntry2, objectEntry3,
			ObjectRelationshipConstants.TYPE_MANY_TO_MANY);
		_testFilterByGroupingOperatorsObjectEntriesByRelatedObjectEntriesFieldsThroughMultipleRelationships(
			objectEntry1, objectEntry2, objectEntry3,
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);
	}

	@Test
	public void testFilterByLambdaOperatorsObjectEntriesByRelatedObjectEntriesFields()
		throws Exception {

		ObjectEntry objectEntry1 = ObjectEntryTestUtil.addObjectEntry(
			getObjectDefinition1(),
			HashMapBuilder.<String, Serializable>put(
				OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1
			).put(
				OBJECT_FIELD_NAME_MULTISELECT_PICKLIST,
				(Serializable)Arrays.asList(
					LIST_TYPE_ENTRY_KEY_1, LIST_TYPE_ENTRY_KEY_2)
			).build(),
			TAG_1);
		ObjectEntry objectEntry2 = ObjectEntryTestUtil.addObjectEntry(
			getObjectDefinition2(),
			HashMapBuilder.<String, Serializable>put(
				OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2
			).put(
				OBJECT_FIELD_NAME_MULTISELECT_PICKLIST,
				(Serializable)Arrays.asList(
					LIST_TYPE_ENTRY_KEY_1, LIST_TYPE_ENTRY_KEY_2)
			).build(),
			_TAG_2);

		_testFilterByLambdaOperatorsObjectEntriesByRelatedObjectEntriesFields(
			objectEntry1, objectEntry2,
			ObjectRelationshipConstants.TYPE_MANY_TO_MANY);
		_testFilterByLambdaOperatorsObjectEntriesByRelatedObjectEntriesFields(
			objectEntry1, objectEntry2,
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);
	}

	@Test
	public void testFilterByLambdaOperatorsObjectEntriesByRelatedObjectEntriesFieldsThroughMultipleRelationships()
		throws Exception {

		ObjectEntry objectEntry1 = ObjectEntryTestUtil.addObjectEntry(
			getObjectDefinition1(),
			HashMapBuilder.<String, Serializable>put(
				OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1
			).put(
				OBJECT_FIELD_NAME_MULTISELECT_PICKLIST, LIST_TYPE_ENTRY_KEY_1
			).build(),
			TAG_1);
		ObjectEntry objectEntry2 = ObjectEntryTestUtil.addObjectEntry(
			getObjectDefinition2(),
			HashMapBuilder.<String, Serializable>put(
				OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2
			).put(
				OBJECT_FIELD_NAME_MULTISELECT_PICKLIST, LIST_TYPE_ENTRY_KEY_2
			).build(),
			_TAG_2);
		ObjectEntry objectEntry3 = ObjectEntryTestUtil.addObjectEntry(
			getObjectDefinition3(),
			HashMapBuilder.<String, Serializable>put(
				OBJECT_FIELD_NAME_3, OBJECT_FIELD_VALUE_3
			).put(
				OBJECT_FIELD_NAME_MULTISELECT_PICKLIST, LIST_TYPE_ENTRY_KEY_3
			).build(),
			_TAG_3);

		_testFilterByLambdaOperatorsObjectEntriesByRelatedObjectEntriesFieldsThroughMultipleRelationships(
			objectEntry1, objectEntry2, objectEntry3,
			ObjectRelationshipConstants.TYPE_MANY_TO_MANY);
		_testFilterByLambdaOperatorsObjectEntriesByRelatedObjectEntriesFieldsThroughMultipleRelationships(
			objectEntry1, objectEntry2, objectEntry3,
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);
	}

	@Test
	public void testFilterByListOperatorsObjectEntriesByRelatedObjectEntriesFields()
		throws Exception {

		ObjectEntry objectEntry1 = ObjectEntryTestUtil.addObjectEntry(
			getObjectDefinition1(), OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1);
		ObjectEntry objectEntry2 = ObjectEntryTestUtil.addObjectEntry(
			getObjectDefinition2(), OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2);

		_testFilterByListOperatorsObjectEntriesByRelatedObjectEntriesFields(
			objectEntry1, objectEntry2,
			ObjectRelationshipConstants.TYPE_MANY_TO_MANY);
		_testFilterByListOperatorsObjectEntriesByRelatedObjectEntriesFields(
			objectEntry1, objectEntry2,
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);
	}

	@Test
	public void testFilterByLogicalOperatorsObjectEntriesByRelatedObjectEntriesFields()
		throws Exception {

		ObjectEntry objectEntry1 = ObjectEntryTestUtil.addObjectEntry(
			getObjectDefinition1(), OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1);
		ObjectEntry objectEntry2 = ObjectEntryTestUtil.addObjectEntry(
			getObjectDefinition2(), OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2);

		_testFilterByLogicalOperatorsObjectEntriesByRelatedObjectEntriesFields(
			objectEntry1, objectEntry2,
			ObjectRelationshipConstants.TYPE_MANY_TO_MANY);
		_testFilterByLogicalOperatorsObjectEntriesByRelatedObjectEntriesFields(
			objectEntry1, objectEntry2,
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);
	}

	@Test
	public void testFilterByLogicalOperatorsObjectEntriesByRelatedObjectEntriesFieldsThroughMultipleRelationships()
		throws Exception {

		ObjectEntry objectEntry1 = ObjectEntryTestUtil.addObjectEntry(
			getObjectDefinition1(), OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1);
		ObjectEntry objectEntry2 = ObjectEntryTestUtil.addObjectEntry(
			getObjectDefinition2(), OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2);
		ObjectEntry objectEntry3 = ObjectEntryTestUtil.addObjectEntry(
			getObjectDefinition3(), OBJECT_FIELD_NAME_3, OBJECT_FIELD_VALUE_3);

		_testFilterByLogicalOperatorsObjectEntriesByRelatedObjectEntriesFieldsThroughMultipleRelationships(
			objectEntry1, objectEntry2, objectEntry3,
			ObjectRelationshipConstants.TYPE_MANY_TO_MANY);
		_testFilterByLogicalOperatorsObjectEntriesByRelatedObjectEntriesFieldsThroughMultipleRelationships(
			objectEntry1, objectEntry2, objectEntry3,
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);
	}

	@Test
	public void testFilterByObjectRelationshipERCObjectFieldNameInOneToManyRelationship()
		throws Exception {

		ObjectDefinition objectDefinition1 = getObjectDefinition1();

		ObjectEntry objectEntry1 = ObjectEntryTestUtil.addObjectEntry(
			objectDefinition1,
			HashMapBuilder.<String, Serializable>put(
				OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1
			).put(
				"externalReferenceCode",
				RandomTestUtil.randomString(4) + "a" +
					RandomTestUtil.randomString(3)
			).build());

		ObjectDefinition objectDefinition2 = getObjectDefinition2();

		ObjectEntry objectEntry2 = ObjectEntryTestUtil.addObjectEntry(
			objectDefinition2, OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2);

		ObjectRelationship objectRelationship =
			addObjectRelationshipAndRelateObjectEntries(
				objectDefinition1, objectDefinition2,
				objectEntry1.getPrimaryKey(), objectEntry2.getPrimaryKey(),
				ObjectRelationshipConstants.TYPE_ONE_TO_MANY);

		String objectRelationshipERCObjectFieldName =
			ObjectFieldSettingUtil.getValue(
				ObjectFieldSettingConstants.
					NAME_OBJECT_RELATIONSHIP_ERC_OBJECT_FIELD_NAME,
				objectFieldLocalService.getObjectField(
					objectRelationship.getObjectFieldId2()));

		Assert.assertEquals(
			StringBundler.concat(
				"r_", objectRelationship.getName(), "_",
				StringUtil.replaceLast(
					objectDefinition1.getPKObjectFieldName(), "Id", "ERC")),
			objectRelationshipERCObjectFieldName);

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			null, objectDefinition2.getRESTContextPath(), Http.Method.GET);

		JSONArray itemsJSONArray = jsonObject.getJSONArray("items");

		Assert.assertEquals(1, itemsJSONArray.length());

		JSONObject itemJSONObject = itemsJSONArray.getJSONObject(0);

		String objectEntry1ExternalReferenceCode =
			objectEntry1.getExternalReferenceCode();

		Assert.assertEquals(
			objectEntry1ExternalReferenceCode,
			itemJSONObject.getString(objectRelationshipERCObjectFieldName));

		// Comparison operators

		String substring = objectEntry1ExternalReferenceCode.substring(0, 4);

		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"%s/%s eq '%s'", objectRelationship.getName(),
				objectRelationshipERCObjectFieldName,
				objectEntry1ExternalReferenceCode),
			objectDefinition1);
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"%s/%s ge '%s'", objectRelationship.getName(),
				objectRelationshipERCObjectFieldName,
				objectEntry1ExternalReferenceCode),
			objectDefinition1);

		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"%s/%s gt '%s'", objectRelationship.getName(),
				objectRelationshipERCObjectFieldName, substring + "0000"),
			objectDefinition1);
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"%s/%s le '%s'", objectRelationship.getName(),
				objectRelationshipERCObjectFieldName,
				objectEntry1ExternalReferenceCode),
			objectDefinition1);
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"%s/%s lt '%s'", objectRelationship.getName(),
				objectRelationshipERCObjectFieldName, substring + "zzzz"),
			objectDefinition1);
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"%s/%s ne '%s'", objectRelationship.getName(),
				objectRelationshipERCObjectFieldName,
				RandomTestUtil.randomInt()),
			objectDefinition1);

		// List operators

		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"%s/%s in ('%s', '%s')", objectRelationship.getName(),
				objectRelationshipERCObjectFieldName,
				objectEntry1ExternalReferenceCode, RandomTestUtil.randomInt()),
			objectDefinition1);

		// String operators

		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"contains(%s/%s,'%s')", objectRelationship.getName(),
				objectRelationshipERCObjectFieldName, substring),
			objectDefinition1);
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"startswith(%s/%s,'%s')", objectRelationship.getName(),
				objectRelationshipERCObjectFieldName, substring),
			objectDefinition1);
	}

	@Test
	public void testFilterByStringOperatorsObjectEntriesByRelatedObjectEntriesFields()
		throws Exception {

		ObjectEntry objectEntry1 = ObjectEntryTestUtil.addObjectEntry(
			getObjectDefinition1(), OBJECT_FIELD_NAME_1,
			String.valueOf(OBJECT_FIELD_VALUE_1));
		ObjectEntry objectEntry2 = ObjectEntryTestUtil.addObjectEntry(
			getObjectDefinition2(), OBJECT_FIELD_NAME_2,
			String.valueOf(OBJECT_FIELD_VALUE_2));

		_testFilterByStringOperatorsObjectEntriesByRelatedObjectEntriesFields(
			objectEntry1, objectEntry2,
			ObjectRelationshipConstants.TYPE_MANY_TO_MANY);
		_testFilterByStringOperatorsObjectEntriesByRelatedObjectEntriesFields(
			objectEntry1, objectEntry2,
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);
	}

	@Test
	public void testFilterByStringOperatorsObjectEntriesByRelatedObjectEntriesFieldsThroughMultipleRelationships()
		throws Exception {

		ObjectEntry objectEntry1 = ObjectEntryTestUtil.addObjectEntry(
			getObjectDefinition1(), OBJECT_FIELD_NAME_1,
			String.valueOf(OBJECT_FIELD_VALUE_1));
		ObjectEntry objectEntry2 = ObjectEntryTestUtil.addObjectEntry(
			getObjectDefinition2(), OBJECT_FIELD_NAME_2,
			String.valueOf(OBJECT_FIELD_VALUE_2));
		ObjectEntry objectEntry3 = ObjectEntryTestUtil.addObjectEntry(
			getObjectDefinition3(), OBJECT_FIELD_NAME_3,
			String.valueOf(OBJECT_FIELD_VALUE_3));

		_testFilterByStringOperatorsObjectEntriesByRelatedObjectEntriesFieldsThroughMultipleRelationships(
			objectEntry1, objectEntry2, objectEntry3,
			ObjectRelationshipConstants.TYPE_MANY_TO_MANY);
		_testFilterByStringOperatorsObjectEntriesByRelatedObjectEntriesFieldsThroughMultipleRelationships(
			objectEntry1, objectEntry2, objectEntry3,
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);
	}

	@Test
	public void testFilterObjectEntriesByRelatedLocalizedObjectEntries()
		throws Exception {

		ObjectDefinition localizedObjectDefinition1 =
			publishLocalizedObjectDefinition(OBJECT_FIELD_NAME_1);

		ObjectEntry localizedObjectEntry1 = _addLocalizedObjectEntry(
			localizedObjectDefinition1, OBJECT_FIELD_NAME_1,
			new String[] {
				String.valueOf(OBJECT_FIELD_VALUE_1),
				String.valueOf(OBJECT_FIELD_VALUE_2)
			});

		ObjectDefinition localizedObjectDefinition2 =
			publishLocalizedObjectDefinition(OBJECT_FIELD_NAME_2);

		ObjectEntry localizedObjectEntry2 = _addLocalizedObjectEntry(
			localizedObjectDefinition2, OBJECT_FIELD_NAME_2,
			new String[] {
				String.valueOf(OBJECT_FIELD_VALUE_3),
				String.valueOf(OBJECT_FIELD_VALUE_4)
			});

		ObjectRelationship objectRelationship =
			addObjectRelationshipAndRelateObjectEntries(
				localizedObjectDefinition1, localizedObjectDefinition2,
				localizedObjectEntry1.getPrimaryKey(),
				localizedObjectEntry2.getPrimaryKey(),
				ObjectRelationshipConstants.TYPE_ONE_TO_MANY);

		try {

			// Many to one relationship

			_testFilterObjectEntriesByRelatedLocalizedObjectEntries(
				String.format(
					"%s/%s eq '%s'", objectRelationship.getName(),
					OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1),
				localizedObjectDefinition2, localizedObjectEntry2, "en-US");
			_testFilterObjectEntriesByRelatedLocalizedObjectEntries(
				String.format(
					"%s/%s eq '%s'", objectRelationship.getName(),
					OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_2),
				localizedObjectDefinition2, localizedObjectEntry2, "es-ES");

			// One to many relationship

			_testFilterObjectEntriesByRelatedLocalizedObjectEntries(
				String.format(
					"%s/%s eq '%s'", objectRelationship.getName(),
					OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_3),
				localizedObjectDefinition1, localizedObjectEntry1, "en-US");
			_testFilterObjectEntriesByRelatedLocalizedObjectEntries(
				String.format(
					"%s/%s eq '%s'", objectRelationship.getName(),
					OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_4),
				localizedObjectDefinition1, localizedObjectEntry1, "es-ES");
		}
		finally {
			objectRelationshipLocalService.deleteObjectRelationship(
				objectRelationship);

			objectDefinitionLocalService.deleteObjectDefinition(
				localizedObjectDefinition1);
			objectDefinitionLocalService.deleteObjectDefinition(
				localizedObjectDefinition2);
		}
	}

	@Test
	public void testFilterObjectEntriesByRelatedSystemObjectEntriesFields()
		throws Exception {

		ObjectEntry objectEntry = ObjectEntryTestUtil.addObjectEntry(
			getObjectDefinition1(), OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1);

		JSONObject userAccountJSONObject =
			UserAccountTestUtil.addUserAccountJSONObject(
				getUserSystemObjectDefinitionManager(),
				HashMapBuilder.<String, Serializable>put(
					OBJECT_FIELD_NAME_2, String.valueOf(OBJECT_FIELD_VALUE_2)
				).build());

		// Many to many relationship

		ObjectRelationship objectRelationship =
			addObjectRelationshipAndRelateObjectEntries(
				getObjectDefinition1(), getUserSystemObjectDefinition(),
				objectEntry.getPrimaryKey(),
				userAccountJSONObject.getLong("id"),
				ObjectRelationshipConstants.TYPE_MANY_TO_MANY);

		_testFilterObjectEntriesByRelatedSystemObjectEntriesFields(
			String.format(
				"%s/%s eq '%s'", objectRelationship.getName(),
				OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2));

		objectRelationshipLocalService.deleteObjectRelationship(
			objectRelationship);

		// One to many relationship

		objectRelationship = addObjectRelationshipAndRelateObjectEntries(
			getUserSystemObjectDefinition(), getObjectDefinition1(),
			userAccountJSONObject.getLong("id"), objectEntry.getPrimaryKey(),
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);

		_testFilterObjectEntriesByRelatedSystemObjectEntriesFields(
			String.format(
				"%s/%s eq '%s'", objectRelationship.getName(),
				OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2));

		String objectRelationshipERCObjectFieldName =
			ObjectFieldSettingUtil.getValue(
				ObjectFieldSettingConstants.
					NAME_OBJECT_RELATIONSHIP_ERC_OBJECT_FIELD_NAME,
				objectFieldLocalService.getObjectField(
					objectRelationship.getObjectFieldId2()));

		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"%s eq '%s'", objectRelationshipERCObjectFieldName,
				userAccountJSONObject.getString("externalReferenceCode")),
			getObjectDefinition1());
	}

	@Test
	public void testFilterObjectEntriesByRelatedSystemObjectEntriesTaxonomyCategoryIds()
		throws Exception {

		ObjectEntry objectEntry = ObjectEntryTestUtil.addObjectEntry(
			getObjectDefinition1(), OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1);

		JSONObject userAccountJSONObject =
			UserAccountTestUtil.addUserAccountJSONObject(
				getUserSystemObjectDefinitionManager(), Collections.emptyMap());

		// Many to many relationship

		ObjectRelationship objectRelationship =
			addObjectRelationshipAndRelateObjectEntries(
				getObjectDefinition1(), getUserSystemObjectDefinition(),
				objectEntry.getPrimaryKey(),
				userAccountJSONObject.getLong("id"),
				ObjectRelationshipConstants.TYPE_MANY_TO_MANY);

		TaxonomyCategory taxonomyCategory =
			postTaxonomyVocabularyTaxonomyCategory();

		_testFilterObjectEntriesByRelatedSystemObjectEntriesFields(
			String.format(
				"%s/taxonomyCategoryIds/any(t:t eq %s)",
				objectRelationship.getName(), taxonomyCategory.getId()));

		objectRelationshipLocalService.deleteObjectRelationship(
			objectRelationship);

		// One to many relationship

		objectRelationship = addObjectRelationshipAndRelateObjectEntries(
			getUserSystemObjectDefinition(), getObjectDefinition1(),
			userAccountJSONObject.getLong("id"), objectEntry.getPrimaryKey(),
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);

		_testFilterObjectEntriesByRelatedSystemObjectEntriesFields(
			String.format(
				"%s/taxonomyCategoryIds/any(t:t eq %s)",
				objectRelationship.getName(), taxonomyCategory.getId()));
	}

	@Test
	public void testGetObjectEntriesPageWithFilterOnManyToManyRelatedLocalizedObjectField()
		throws Exception {

		ObjectDefinition localizedObjectDefinition1 =
			publishLocalizedObjectDefinition(OBJECT_FIELD_NAME_TEXT);
		String objectFieldValue1 = RandomTestUtil.randomString();

		ObjectEntry localizedObjectEntry1 = ObjectEntryTestUtil.addObjectEntry(
			localizedObjectDefinition1,
			HashMapBuilder.<String, Serializable>put(
				OBJECT_FIELD_NAME_TEXT, objectFieldValue1
			).put(
				OBJECT_FIELD_NAME_TEXT + "_i18n",
				HashMapBuilder.<String, Serializable>put(
					"en_US", objectFieldValue1
				).build()
			).build());

		ObjectDefinition localizedObjectDefinition2 =
			publishLocalizedObjectDefinition(OBJECT_FIELD_NAME_TEXT);
		String objectFieldValue2 = RandomTestUtil.randomString();

		ObjectEntry localizedObjectEntry2 = ObjectEntryTestUtil.addObjectEntry(
			localizedObjectDefinition2,
			HashMapBuilder.<String, Serializable>put(
				OBJECT_FIELD_NAME_TEXT, objectFieldValue2
			).put(
				OBJECT_FIELD_NAME_TEXT + "_i18n",
				HashMapBuilder.<String, Serializable>put(
					"en_US", objectFieldValue2
				).build()
			).build());

		ObjectRelationship objectRelationship =
			addObjectRelationshipAndRelateObjectEntries(
				localizedObjectDefinition1, localizedObjectDefinition2,
				localizedObjectEntry1.getPrimaryKey(),
				localizedObjectEntry2.getPrimaryKey(),
				ObjectRelationshipConstants.TYPE_MANY_TO_MANY);

		_assertObjectEntriesPageWithFilterOnRelatedLocalizedObjectField(
			objectFieldValue1, localizedObjectDefinition1, objectRelationship,
			objectFieldValue2);
		_assertObjectEntriesPageWithFilterOnRelatedLocalizedObjectField(
			objectFieldValue2, localizedObjectDefinition2, objectRelationship,
			objectFieldValue1);

		objectRelationshipLocalService.deleteObjectRelationship(
			objectRelationship);

		objectDefinitionLocalService.deleteObjectDefinition(
			localizedObjectDefinition1);
		objectDefinitionLocalService.deleteObjectDefinition(
			localizedObjectDefinition2);
	}

	private ObjectEntry _addLocalizedObjectEntry(
			ObjectDefinition objectDefinition, String objectFieldName,
			String[] objectFieldValues)
		throws Exception {

		return ObjectEntryTestUtil.addObjectEntry(
			objectDefinition,
			HashMapBuilder.<String, Serializable>put(
				objectFieldName + "_i18n",
				HashMapBuilder.<String, Serializable>put(
					"en_US", objectFieldValues[0]
				).put(
					"es_ES", objectFieldValues[1]
				).build()
			).build());
	}

	private void
			_assertObjectEntriesPageWithFilterOnRelatedLocalizedObjectField(
				String expectedObjectFieldValue,
				ObjectDefinition objectDefinition,
				ObjectRelationship objectRelationship,
				String relatedObjectFieldValue)
		throws Exception {

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			StringBundler.concat(
				objectDefinition.getRESTContextPath(), "?filter=",
				URLCodec.encodeURL(
					String.format(
						"%s/%s eq '%s'", objectRelationship.getName(),
						OBJECT_FIELD_NAME_TEXT, relatedObjectFieldValue)),
				"&nestedFields=", objectRelationship.getName()),
			Http.Method.GET);

		JSONArray itemsJSONArray = jsonObject.getJSONArray("items");

		Assert.assertEquals(
			itemsJSONArray.toString(), 1, itemsJSONArray.length());

		JSONObject itemJSONObject = itemsJSONArray.getJSONObject(0);

		Assert.assertEquals(
			relatedObjectFieldValue,
			JSONUtil.getValueAsString(
				itemJSONObject, "JSONArray/" + objectRelationship.getName(),
				"Object/0", "Object/" + OBJECT_FIELD_NAME_TEXT));
		Assert.assertEquals(
			expectedObjectFieldValue,
			itemJSONObject.getString(OBJECT_FIELD_NAME_TEXT));
	}

	private void
			_testFilterByComparisonOperatorsObjectEntriesByRelatedObjectEntriesFields(
				ObjectEntry objectEntry1, ObjectEntry objectEntry2, String type)
		throws Exception {

		ObjectDefinition objectDefinition1 = getObjectDefinition1();

		ObjectRelationship objectRelationship =
			addObjectRelationshipAndRelateObjectEntries(
				objectDefinition1, getObjectDefinition2(),
				objectEntry1.getPrimaryKey(), objectEntry2.getPrimaryKey(),
				type);

		// Custom object field

		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"%s/%s eq '%s'", objectRelationship.getName(),
				OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2),
			objectDefinition1);
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"%s/%s ge '%s'", objectRelationship.getName(),
				OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2),
			objectDefinition1);
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"%s/%s gt '%s'", objectRelationship.getName(),
				OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2 - 1),
			objectDefinition1);
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"%s/%s le '%s'", objectRelationship.getName(),
				OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2),
			objectDefinition1);
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"%s/%s lt '%s'", objectRelationship.getName(),
				OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2 + 1),
			objectDefinition1);
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"%s/%s ne '%s'", objectRelationship.getName(),
				OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2 - 1),
			objectDefinition1);

		if (StringUtil.equals(
				type, ObjectRelationshipConstants.TYPE_ONE_TO_MANY)) {

			assertFilterString(
				OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
				String.format(
					"%s/%s eq '%s'", objectRelationship.getName(),
					StringUtil.removeFirst(
						objectDefinition1.getPKObjectFieldName(), "c_"),
					objectEntry1.getPrimaryKey()),
				objectDefinition1);
		}

		// Other side, custom object field

		assertFilterString(
			OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2,
			String.format(
				"%s/%s eq '%s'", objectRelationship.getName(),
				OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1),
			getObjectDefinition2());
		assertFilterString(
			OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2,
			String.format(
				"%s/%s ge '%s'", objectRelationship.getName(),
				OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1),
			getObjectDefinition2());
		assertFilterString(
			OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2,
			String.format(
				"%s/%s gt '%s'", objectRelationship.getName(),
				OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1 - 1),
			getObjectDefinition2());
		assertFilterString(
			OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2,
			String.format(
				"%s/%s le '%s'", objectRelationship.getName(),
				OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1),
			getObjectDefinition2());
		assertFilterString(
			OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2,
			String.format(
				"%s/%s lt '%s'", objectRelationship.getName(),
				OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1 + 1),
			getObjectDefinition2());
		assertFilterString(
			OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2,
			String.format(
				"%s/%s ne '%s'", objectRelationship.getName(),
				OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1 - 1),
			getObjectDefinition2());

		// Other side, system object field

		assertFilterString(
			OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2,
			String.format(
				"%s/id eq '%s'", objectRelationship.getName(),
				objectEntry1.getObjectEntryId()),
			getObjectDefinition2());
		assertFilterString(
			OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2,
			String.format(
				"%s/id ge '%s'", objectRelationship.getName(),
				objectEntry1.getObjectEntryId()),
			getObjectDefinition2());
		assertFilterString(
			OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2,
			String.format(
				"%s/id gt '%s'", objectRelationship.getName(),
				objectEntry1.getObjectEntryId() - 1),
			getObjectDefinition2());
		assertFilterString(
			OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2,
			String.format(
				"%s/id le '%s'", objectRelationship.getName(),
				objectEntry1.getObjectEntryId()),
			getObjectDefinition2());
		assertFilterString(
			OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2,
			String.format(
				"%s/id lt '%s'", objectRelationship.getName(),
				objectEntry1.getObjectEntryId() + 1),
			getObjectDefinition2());
		assertFilterString(
			OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2,
			String.format(
				"%s/id ne '%s'", objectRelationship.getName(),
				objectEntry1.getObjectEntryId() - 1),
			getObjectDefinition2());

		// System object field

		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"%s/id eq '%s'", objectRelationship.getName(),
				objectEntry2.getObjectEntryId()),
			objectDefinition1);
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"%s/id ge '%s'", objectRelationship.getName(),
				objectEntry2.getObjectEntryId()),
			objectDefinition1);
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"%s/id gt '%s'", objectRelationship.getName(),
				objectEntry2.getObjectEntryId() - 1),
			objectDefinition1);
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"%s/id le '%s'", objectRelationship.getName(),
				objectEntry2.getObjectEntryId()),
			objectDefinition1);
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"%s/id lt '%s'", objectRelationship.getName(),
				objectEntry2.getObjectEntryId() + 1),
			objectDefinition1);
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"%s/id ne '%s'", objectRelationship.getName(),
				objectEntry2.getObjectEntryId() - 1),
			objectDefinition1);

		objectRelationshipLocalService.deleteObjectRelationship(
			objectRelationship);

		// Self relationship, custom object field

		objectRelationship = addObjectRelationshipAndRelateObjectEntries(
			objectDefinition1, objectDefinition1, objectEntry1.getPrimaryKey(),
			objectEntry1.getPrimaryKey(), type);

		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"%s/%s eq '%s'", objectRelationship.getName(),
				OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1),
			objectDefinition1);
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"%s/%s ge '%s'", objectRelationship.getName(),
				OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1),
			objectDefinition1);
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"%s/%s gt '%s'", objectRelationship.getName(),
				OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1 - 1),
			objectDefinition1);
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"%s/%s le '%s'", objectRelationship.getName(),
				OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1),
			objectDefinition1);
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"%s/%s lt '%s'", objectRelationship.getName(),
				OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1 + 1),
			objectDefinition1);
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"%s/%s ne '%s'", objectRelationship.getName(),
				OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1 - 1),
			objectDefinition1);
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"%s/%s/%s eq '%s'", objectRelationship.getName(),
				objectRelationship.getName(), OBJECT_FIELD_NAME_1,
				OBJECT_FIELD_VALUE_1),
			objectDefinition1);
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"%s/%s/%s ge '%s'", objectRelationship.getName(),
				objectRelationship.getName(), OBJECT_FIELD_NAME_1,
				OBJECT_FIELD_VALUE_1),
			objectDefinition1);
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"%s/%s/%s gt '%s'", objectRelationship.getName(),
				objectRelationship.getName(), OBJECT_FIELD_NAME_1,
				OBJECT_FIELD_VALUE_1 - 1),
			objectDefinition1);
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"%s/%s/%s le '%s'", objectRelationship.getName(),
				objectRelationship.getName(), OBJECT_FIELD_NAME_1,
				OBJECT_FIELD_VALUE_1),
			objectDefinition1);
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"%s/%s/%s lt '%s'", objectRelationship.getName(),
				objectRelationship.getName(), OBJECT_FIELD_NAME_1,
				OBJECT_FIELD_VALUE_1 + 1),
			objectDefinition1);
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"%s/%s/%s ne '%s'", objectRelationship.getName(),
				objectRelationship.getName(), OBJECT_FIELD_NAME_1,
				OBJECT_FIELD_VALUE_1 - 1),
			objectDefinition1);
	}

	private void
			_testFilterByComparisonOperatorsObjectEntriesByRelatedObjectEntriesFieldsThroughMultipleRelationships(
				ObjectEntry objectEntry1, ObjectEntry objectEntry3,
				ObjectRelationship objectRelationship1,
				ObjectRelationship objectRelationship2)
		throws Exception {

		// Custom object field

		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"%s/%s/%s eq '%s'", objectRelationship1.getName(),
				objectRelationship2.getName(), OBJECT_FIELD_NAME_3,
				OBJECT_FIELD_VALUE_3),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"%s/%s/%s ge '%s'", objectRelationship1.getName(),
				objectRelationship2.getName(), OBJECT_FIELD_NAME_3,
				OBJECT_FIELD_VALUE_3),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"%s/%s/%s gt '%s'", objectRelationship1.getName(),
				objectRelationship2.getName(), OBJECT_FIELD_NAME_3,
				OBJECT_FIELD_VALUE_3 - 1),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"%s/%s/%s le '%s'", objectRelationship1.getName(),
				objectRelationship2.getName(), OBJECT_FIELD_NAME_3,
				OBJECT_FIELD_VALUE_3),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"%s/%s/%s lt '%s'", objectRelationship1.getName(),
				objectRelationship2.getName(), OBJECT_FIELD_NAME_3,
				OBJECT_FIELD_VALUE_3 + 1),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"%s/%s/%s ne '%s'", objectRelationship1.getName(),
				objectRelationship2.getName(), OBJECT_FIELD_NAME_3,
				OBJECT_FIELD_VALUE_3 - 1),
			getObjectDefinition1());

		// Other side, custom object field

		assertFilterString(
			OBJECT_FIELD_NAME_3, OBJECT_FIELD_VALUE_3,
			String.format(
				"%s/%s/%s eq '%s'", objectRelationship2.getName(),
				objectRelationship1.getName(), OBJECT_FIELD_NAME_1,
				OBJECT_FIELD_VALUE_1),
			getObjectDefinition3());
		assertFilterString(
			OBJECT_FIELD_NAME_3, OBJECT_FIELD_VALUE_3,
			String.format(
				"%s/%s/%s ge '%s'", objectRelationship2.getName(),
				objectRelationship1.getName(), OBJECT_FIELD_NAME_1,
				OBJECT_FIELD_VALUE_1),
			getObjectDefinition3());
		assertFilterString(
			OBJECT_FIELD_NAME_3, OBJECT_FIELD_VALUE_3,
			String.format(
				"%s/%s/%s gt '%s'", objectRelationship2.getName(),
				objectRelationship1.getName(), OBJECT_FIELD_NAME_1,
				OBJECT_FIELD_VALUE_1 - 1),
			getObjectDefinition3());
		assertFilterString(
			OBJECT_FIELD_NAME_3, OBJECT_FIELD_VALUE_3,
			String.format(
				"%s/%s/%s le '%s'", objectRelationship2.getName(),
				objectRelationship1.getName(), OBJECT_FIELD_NAME_1,
				OBJECT_FIELD_VALUE_1),
			getObjectDefinition3());
		assertFilterString(
			OBJECT_FIELD_NAME_3, OBJECT_FIELD_VALUE_3,
			String.format(
				"%s/%s/%s lt '%s'", objectRelationship2.getName(),
				objectRelationship1.getName(), OBJECT_FIELD_NAME_1,
				OBJECT_FIELD_VALUE_1 + 1),
			getObjectDefinition3());
		assertFilterString(
			OBJECT_FIELD_NAME_3, OBJECT_FIELD_VALUE_3,
			String.format(
				"%s/%s/%s ne '%s'", objectRelationship2.getName(),
				objectRelationship1.getName(), OBJECT_FIELD_NAME_1,
				OBJECT_FIELD_VALUE_1 - 1),
			getObjectDefinition3());

		// Other side, system object field

		assertFilterString(
			OBJECT_FIELD_NAME_3, OBJECT_FIELD_VALUE_3,
			String.format(
				"%s/%s/id eq '%s'", objectRelationship2.getName(),
				objectRelationship1.getName(), objectEntry1.getObjectEntryId()),
			getObjectDefinition3());
		assertFilterString(
			OBJECT_FIELD_NAME_3, OBJECT_FIELD_VALUE_3,
			String.format(
				"%s/%s/id ge '%s'", objectRelationship2.getName(),
				objectRelationship1.getName(), objectEntry1.getObjectEntryId()),
			getObjectDefinition3());
		assertFilterString(
			OBJECT_FIELD_NAME_3, OBJECT_FIELD_VALUE_3,
			String.format(
				"%s/%s/id gt '%s'", objectRelationship2.getName(),
				objectRelationship1.getName(),
				objectEntry1.getObjectEntryId() - 1),
			getObjectDefinition3());
		assertFilterString(
			OBJECT_FIELD_NAME_3, OBJECT_FIELD_VALUE_3,
			String.format(
				"%s/%s/id le '%s'", objectRelationship2.getName(),
				objectRelationship1.getName(), objectEntry1.getObjectEntryId()),
			getObjectDefinition3());
		assertFilterString(
			OBJECT_FIELD_NAME_3, OBJECT_FIELD_VALUE_3,
			String.format(
				"%s/%s/id lt '%s'", objectRelationship2.getName(),
				objectRelationship1.getName(),
				objectEntry1.getObjectEntryId() + 1),
			getObjectDefinition3());
		assertFilterString(
			OBJECT_FIELD_NAME_3, OBJECT_FIELD_VALUE_3,
			String.format(
				"%s/%s/id ne '%s'", objectRelationship2.getName(),
				objectRelationship1.getName(),
				objectEntry1.getObjectEntryId() - 1),
			getObjectDefinition3());

		// System object field

		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"%s/%s/id eq '%s'", objectRelationship1.getName(),
				objectRelationship2.getName(), objectEntry3.getObjectEntryId()),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"%s/%s/id ge '%s'", objectRelationship1.getName(),
				objectRelationship2.getName(), objectEntry3.getObjectEntryId()),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"%s/%s/id gt '%s'", objectRelationship1.getName(),
				objectRelationship2.getName(),
				objectEntry3.getObjectEntryId() - 1),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"%s/%s/id le '%s'", objectRelationship1.getName(),
				objectRelationship2.getName(), objectEntry3.getObjectEntryId()),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"%s/%s/id lt '%s'", objectRelationship1.getName(),
				objectRelationship2.getName(),
				objectEntry3.getObjectEntryId() + 1),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"%s/%s/id ne '%s'", objectRelationship1.getName(),
				objectRelationship2.getName(),
				objectEntry3.getObjectEntryId() - 1),
			getObjectDefinition1());
	}

	private void
			_testFilterByGroupingOperatorsObjectEntriesByRelatedObjectEntriesFields(
				ObjectEntry objectEntry1, ObjectEntry objectEntry2, String type)
		throws Exception {

		ObjectRelationship objectRelationship =
			addObjectRelationshipAndRelateObjectEntries(
				getObjectDefinition1(), getObjectDefinition2(),
				objectEntry1.getPrimaryKey(), objectEntry2.getPrimaryKey(),
				type);

		// Custom object field

		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"(%s/%s le '%s') and (%s/%s gt '%s')",
				objectRelationship.getName(), OBJECT_FIELD_NAME_2,
				OBJECT_FIELD_VALUE_2, objectRelationship.getName(),
				OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2 - 1),
			getObjectDefinition1());

		// Other side, custom object field

		assertFilterString(
			OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2,
			String.format(
				"(%s/%s le '%s') and (%s/%s gt '%s')",
				objectRelationship.getName(), OBJECT_FIELD_NAME_1,
				OBJECT_FIELD_VALUE_1, objectRelationship.getName(),
				OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1 - 1),
			getObjectDefinition2());

		// Other side, system object field

		assertFilterString(
			OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2,
			String.format(
				"(%s/id le '%s') and (%s/id gt '%s')",
				objectRelationship.getName(), objectEntry1.getObjectEntryId(),
				objectRelationship.getName(),
				objectEntry1.getObjectEntryId() - 1),
			getObjectDefinition2());

		// System object field

		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"(%s/id le '%s') and (%s/id gt '%s')",
				objectRelationship.getName(), objectEntry2.getObjectEntryId(),
				objectRelationship.getName(),
				objectEntry2.getObjectEntryId() - 1),
			getObjectDefinition1());

		objectRelationshipLocalService.deleteObjectRelationship(
			objectRelationship);

		// Self relationship, custom object field

		objectRelationship = addObjectRelationshipAndRelateObjectEntries(
			getObjectDefinition1(), getObjectDefinition1(),
			objectEntry1.getPrimaryKey(), objectEntry1.getPrimaryKey(), type);

		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"(%s/%s le '%s') and (%s/%s gt '%s')",
				objectRelationship.getName(), OBJECT_FIELD_NAME_1,
				OBJECT_FIELD_VALUE_1, objectRelationship.getName(),
				OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1 - 1),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"(%s/%s/%s le '%s') and (%s/%s/%s gt '%s')",
				objectRelationship.getName(), objectRelationship.getName(),
				OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
				objectRelationship.getName(), objectRelationship.getName(),
				OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1 - 1),
			getObjectDefinition1());
	}

	private void
			_testFilterByGroupingOperatorsObjectEntriesByRelatedObjectEntriesFieldsThroughMultipleRelationships(
				ObjectEntry objectEntry1, ObjectEntry objectEntry2,
				ObjectEntry objectEntry3, String type)
		throws Exception {

		ObjectRelationship objectRelationship1 =
			addObjectRelationshipAndRelateObjectEntries(
				getObjectDefinition1(), getObjectDefinition2(),
				objectEntry1.getPrimaryKey(), objectEntry2.getPrimaryKey(),
				type);
		ObjectRelationship objectRelationship2 =
			addObjectRelationshipAndRelateObjectEntries(
				getObjectDefinition2(), getObjectDefinition3(),
				objectEntry2.getPrimaryKey(), objectEntry3.getPrimaryKey(),
				type);

		// Custom object field

		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"(%s/%s/%s le '%s') and (%s/%s/%s gt '%s')",
				objectRelationship1.getName(), objectRelationship2.getName(),
				OBJECT_FIELD_NAME_3, OBJECT_FIELD_VALUE_3,
				objectRelationship1.getName(), objectRelationship2.getName(),
				OBJECT_FIELD_NAME_3, OBJECT_FIELD_VALUE_3 - 1),
			getObjectDefinition1());

		// Other side, custom object field

		assertFilterString(
			OBJECT_FIELD_NAME_3, OBJECT_FIELD_VALUE_3,
			String.format(
				"(%s/%s/%s le '%s') and (%s/%s/%s gt '%s')",
				objectRelationship2.getName(), objectRelationship1.getName(),
				OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
				objectRelationship2.getName(), objectRelationship1.getName(),
				OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1 - 1),
			getObjectDefinition3());

		// Other side, system object field

		assertFilterString(
			OBJECT_FIELD_NAME_3, OBJECT_FIELD_VALUE_3,
			String.format(
				"(%s/%s/id le '%s') and (%s/%s/id gt '%s')",
				objectRelationship2.getName(), objectRelationship1.getName(),
				objectEntry1.getObjectEntryId(), objectRelationship2.getName(),
				objectRelationship1.getName(),
				objectEntry1.getObjectEntryId() - 1),
			getObjectDefinition3());

		// System object field

		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"(%s/%s/id le '%s') and (%s/%s/id gt '%s')",
				objectRelationship1.getName(), objectRelationship2.getName(),
				objectEntry3.getObjectEntryId(), objectRelationship1.getName(),
				objectRelationship2.getName(),
				objectEntry3.getObjectEntryId() - 1),
			getObjectDefinition1());
	}

	private void
			_testFilterByLambdaOperatorsObjectEntriesByRelatedObjectEntriesFields(
				ObjectEntry objectEntry1, ObjectEntry objectEntry2, String type)
		throws Exception {

		ObjectRelationship objectRelationship =
			addObjectRelationshipAndRelateObjectEntries(
				getObjectDefinition1(), getObjectDefinition2(),
				objectEntry1.getPrimaryKey(), objectEntry2.getPrimaryKey(),
				type);

		// Custom object field

		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"%s/%s/any(k:contains(k,'%s'))", objectRelationship.getName(),
				OBJECT_FIELD_NAME_MULTISELECT_PICKLIST, LIST_TYPE_ENTRY_KEY_1),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"%s/%s/any(k:k eq '%s')", objectRelationship.getName(),
				OBJECT_FIELD_NAME_MULTISELECT_PICKLIST, LIST_TYPE_ENTRY_KEY_1),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"%s/%s/any(k:k in ('%s', '%s'))", objectRelationship.getName(),
				OBJECT_FIELD_NAME_MULTISELECT_PICKLIST, LIST_TYPE_ENTRY_KEY_1,
				RandomTestUtil.randomString()),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"%s/%s/any(k:k ne '%s')", objectRelationship.getName(),
				OBJECT_FIELD_NAME_MULTISELECT_PICKLIST, LIST_TYPE_ENTRY_KEY_1),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"%s/%s/any(k:startswith(k,'%s'))", objectRelationship.getName(),
				OBJECT_FIELD_NAME_MULTISELECT_PICKLIST, LIST_TYPE_ENTRY_KEY_1),
			getObjectDefinition1());

		// Other side, custom object field

		assertFilterString(
			OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2,
			String.format(
				"%s/%s/any(k:contains(k,'%s'))", objectRelationship.getName(),
				OBJECT_FIELD_NAME_MULTISELECT_PICKLIST, LIST_TYPE_ENTRY_KEY_1),
			getObjectDefinition2());
		assertFilterString(
			OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2,
			String.format(
				"%s/%s/any(k:k eq '%s')", objectRelationship.getName(),
				OBJECT_FIELD_NAME_MULTISELECT_PICKLIST, LIST_TYPE_ENTRY_KEY_1),
			getObjectDefinition2());
		assertFilterString(
			OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2,
			String.format(
				"%s/%s/any(k:k in ('%s', '%s'))", objectRelationship.getName(),
				OBJECT_FIELD_NAME_MULTISELECT_PICKLIST, LIST_TYPE_ENTRY_KEY_1,
				RandomTestUtil.randomString()),
			getObjectDefinition2());
		assertFilterString(
			OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2,
			String.format(
				"%s/%s/any(k:k ne '%s')", objectRelationship.getName(),
				OBJECT_FIELD_NAME_MULTISELECT_PICKLIST, LIST_TYPE_ENTRY_KEY_1),
			getObjectDefinition2());
		assertFilterString(
			OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2,
			String.format(
				"%s/%s/any(k:startswith(k,'%s'))", objectRelationship.getName(),
				OBJECT_FIELD_NAME_MULTISELECT_PICKLIST, LIST_TYPE_ENTRY_KEY_1),
			getObjectDefinition2());

		// Other side, system object field

		assertFilterString(
			OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2,
			String.format(
				"%s/keywords/any(k:contains(k,'%s'))",
				objectRelationship.getName(), TAG_1.substring(1)),
			getObjectDefinition2());
		assertFilterString(
			OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2,
			String.format(
				"%s/keywords/any(k:k eq '%s')", objectRelationship.getName(),
				TAG_1),
			getObjectDefinition2());
		assertFilterString(
			OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2,
			String.format(
				"%s/keywords/any(k:k in ('%s', '%s'))",
				objectRelationship.getName(), TAG_1,
				RandomTestUtil.randomString()),
			getObjectDefinition2());
		assertFilterString(
			OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2,
			String.format(
				"%s/keywords/any(k:k ne '%s')", objectRelationship.getName(),
				_TAG_2),
			getObjectDefinition2());
		assertFilterString(
			OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2,
			String.format(
				"%s/keywords/any(k:startswith(k,'%s'))",
				objectRelationship.getName(), TAG_1.substring(0, 2)),
			getObjectDefinition2());

		// System object field

		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"%s/keywords/any(k:contains(k,'%s'))",
				objectRelationship.getName(), _TAG_2.substring(1)),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"%s/keywords/any(k:k eq '%s')", objectRelationship.getName(),
				_TAG_2),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"%s/keywords/any(k:k in ('%s', '%s'))",
				objectRelationship.getName(), _TAG_2,
				RandomTestUtil.randomString()),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"%s/keywords/any(k:k ne '%s')", objectRelationship.getName(),
				TAG_1),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"%s/keywords/any(k:startswith(k,'%s'))",
				objectRelationship.getName(), _TAG_2.substring(0, 2)),
			getObjectDefinition1());

		objectRelationshipLocalService.deleteObjectRelationship(
			objectRelationship);

		// Self relationship, custom object field

		objectRelationship = addObjectRelationshipAndRelateObjectEntries(
			getObjectDefinition1(), getObjectDefinition1(),
			objectEntry1.getPrimaryKey(), objectEntry1.getPrimaryKey(), type);

		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"%s/%s/%s/any(k:contains(k,'%s'))",
				objectRelationship.getName(), objectRelationship.getName(),
				OBJECT_FIELD_NAME_MULTISELECT_PICKLIST, LIST_TYPE_ENTRY_KEY_1),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"%s/%s/%s/any(k:k eq '%s')", objectRelationship.getName(),
				objectRelationship.getName(),
				OBJECT_FIELD_NAME_MULTISELECT_PICKLIST, LIST_TYPE_ENTRY_KEY_1),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"%s/%s/%s/any(k:k in ('%s', '%s'))",
				objectRelationship.getName(), objectRelationship.getName(),
				OBJECT_FIELD_NAME_MULTISELECT_PICKLIST, LIST_TYPE_ENTRY_KEY_1,
				RandomTestUtil.randomString()),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"%s/%s/%s/any(k:k ne '%s')", objectRelationship.getName(),
				objectRelationship.getName(),
				OBJECT_FIELD_NAME_MULTISELECT_PICKLIST, LIST_TYPE_ENTRY_KEY_1),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"%s/%s/%s/any(k:startswith(k,'%s'))",
				objectRelationship.getName(), objectRelationship.getName(),
				OBJECT_FIELD_NAME_MULTISELECT_PICKLIST, LIST_TYPE_ENTRY_KEY_1),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"%s/%s/any(k:contains(k,'%s'))", objectRelationship.getName(),
				OBJECT_FIELD_NAME_MULTISELECT_PICKLIST, LIST_TYPE_ENTRY_KEY_1),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"%s/%s/any(k:k eq '%s')", objectRelationship.getName(),
				OBJECT_FIELD_NAME_MULTISELECT_PICKLIST, LIST_TYPE_ENTRY_KEY_1),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"%s/%s/any(k:k in ('%s', '%s'))", objectRelationship.getName(),
				OBJECT_FIELD_NAME_MULTISELECT_PICKLIST, LIST_TYPE_ENTRY_KEY_1,
				RandomTestUtil.randomString()),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"%s/%s/any(k:k ne '%s')", objectRelationship.getName(),
				OBJECT_FIELD_NAME_MULTISELECT_PICKLIST, LIST_TYPE_ENTRY_KEY_1),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"%s/%s/any(k:startswith(k,'%s'))", objectRelationship.getName(),
				OBJECT_FIELD_NAME_MULTISELECT_PICKLIST, LIST_TYPE_ENTRY_KEY_1),
			getObjectDefinition1());
	}

	private void
			_testFilterByLambdaOperatorsObjectEntriesByRelatedObjectEntriesFieldsThroughMultipleRelationships(
				ObjectEntry objectEntry1, ObjectEntry objectEntry2,
				ObjectEntry objectEntry3, String type)
		throws Exception {

		ObjectRelationship objectRelationship1 =
			addObjectRelationshipAndRelateObjectEntries(
				getObjectDefinition1(), getObjectDefinition2(),
				objectEntry1.getPrimaryKey(), objectEntry2.getPrimaryKey(),
				type);
		ObjectRelationship objectRelationship2 =
			addObjectRelationshipAndRelateObjectEntries(
				getObjectDefinition2(), getObjectDefinition3(),
				objectEntry2.getPrimaryKey(), objectEntry3.getPrimaryKey(),
				type);

		// Custom object field

		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"%s/%s/%s/any(k:contains(k,'%s'))",
				objectRelationship1.getName(), objectRelationship2.getName(),
				OBJECT_FIELD_NAME_MULTISELECT_PICKLIST, LIST_TYPE_ENTRY_KEY_3),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"%s/%s/%s/any(k:k eq '%s')", objectRelationship1.getName(),
				objectRelationship2.getName(),
				OBJECT_FIELD_NAME_MULTISELECT_PICKLIST, LIST_TYPE_ENTRY_KEY_3),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"%s/%s/%s/any(k:k in ('%s', '%s'))",
				objectRelationship1.getName(), objectRelationship2.getName(),
				OBJECT_FIELD_NAME_MULTISELECT_PICKLIST, LIST_TYPE_ENTRY_KEY_3,
				RandomTestUtil.randomString()),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"%s/%s/%s/any(k:k ne '%s')", objectRelationship1.getName(),
				objectRelationship2.getName(),
				OBJECT_FIELD_NAME_MULTISELECT_PICKLIST, LIST_TYPE_ENTRY_KEY_1),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"%s/%s/%s/any(k:k ne '%s')", objectRelationship1.getName(),
				objectRelationship2.getName(),
				OBJECT_FIELD_NAME_MULTISELECT_PICKLIST, LIST_TYPE_ENTRY_KEY_2),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"%s/%s/%s/any(k:startswith(k,'%s'))",
				objectRelationship1.getName(), objectRelationship2.getName(),
				OBJECT_FIELD_NAME_MULTISELECT_PICKLIST, LIST_TYPE_ENTRY_KEY_3),
			getObjectDefinition1());

		// Other side, custom object field

		assertFilterString(
			OBJECT_FIELD_NAME_3, OBJECT_FIELD_VALUE_3,
			String.format(
				"%s/%s/%s/any(k:contains(k,'%s'))",
				objectRelationship2.getName(), objectRelationship1.getName(),
				OBJECT_FIELD_NAME_MULTISELECT_PICKLIST, LIST_TYPE_ENTRY_KEY_1),
			getObjectDefinition3());
		assertFilterString(
			OBJECT_FIELD_NAME_3, OBJECT_FIELD_VALUE_3,
			String.format(
				"%s/%s/%s/any(k:k eq '%s')", objectRelationship2.getName(),
				objectRelationship1.getName(),
				OBJECT_FIELD_NAME_MULTISELECT_PICKLIST, LIST_TYPE_ENTRY_KEY_1),
			getObjectDefinition3());
		assertFilterString(
			OBJECT_FIELD_NAME_3, OBJECT_FIELD_VALUE_3,
			String.format(
				"%s/%s/%s/any(k:k in ('%s', '%s'))",
				objectRelationship2.getName(), objectRelationship1.getName(),
				OBJECT_FIELD_NAME_MULTISELECT_PICKLIST, LIST_TYPE_ENTRY_KEY_1,
				RandomTestUtil.randomString()),
			getObjectDefinition3());
		assertFilterString(
			OBJECT_FIELD_NAME_3, OBJECT_FIELD_VALUE_3,
			String.format(
				"%s/%s/%s/any(k:k ne '%s')", objectRelationship2.getName(),
				objectRelationship1.getName(),
				OBJECT_FIELD_NAME_MULTISELECT_PICKLIST, LIST_TYPE_ENTRY_KEY_3),
			getObjectDefinition3());
		assertFilterString(
			OBJECT_FIELD_NAME_3, OBJECT_FIELD_VALUE_3,
			String.format(
				"%s/%s/%s/any(k:startswith(k,'%s'))",
				objectRelationship2.getName(), objectRelationship1.getName(),
				OBJECT_FIELD_NAME_MULTISELECT_PICKLIST, LIST_TYPE_ENTRY_KEY_1),
			getObjectDefinition3());

		// Other side, system object field

		assertFilterString(
			OBJECT_FIELD_NAME_3, OBJECT_FIELD_VALUE_3,
			String.format(
				"%s/%s/keywords/any(k:contains(k,'%s'))",
				objectRelationship2.getName(), objectRelationship1.getName(),
				TAG_1.substring(1)),
			getObjectDefinition3());
		assertFilterString(
			OBJECT_FIELD_NAME_3, OBJECT_FIELD_VALUE_3,
			String.format(
				"%s/%s/keywords/any(k:k eq '%s')",
				objectRelationship2.getName(), objectRelationship1.getName(),
				TAG_1),
			getObjectDefinition3());
		assertFilterString(
			OBJECT_FIELD_NAME_3, OBJECT_FIELD_VALUE_3,
			String.format(
				"%s/%s/keywords/any(k:k in ('%s', '%s'))",
				objectRelationship2.getName(), objectRelationship1.getName(),
				TAG_1, RandomTestUtil.randomString()),
			getObjectDefinition3());
		assertFilterString(
			OBJECT_FIELD_NAME_3, OBJECT_FIELD_VALUE_3,
			String.format(
				"%s/%s/keywords/any(k:k ne '%s')",
				objectRelationship2.getName(), objectRelationship1.getName(),
				_TAG_3),
			getObjectDefinition3());
		assertFilterString(
			OBJECT_FIELD_NAME_3, OBJECT_FIELD_VALUE_3,
			String.format(
				"%s/%s/keywords/any(k:startswith(k,'%s'))",
				objectRelationship2.getName(), objectRelationship1.getName(),
				TAG_1.substring(0, 2)),
			getObjectDefinition3());

		// System object field

		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"%s/%s/keywords/any(k:contains(k,'%s'))",
				objectRelationship1.getName(), objectRelationship2.getName(),
				_TAG_3.substring(1)),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"%s/%s/keywords/any(k:k eq '%s')",
				objectRelationship1.getName(), objectRelationship2.getName(),
				_TAG_3),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"%s/%s/keywords/any(k:k in ('%s', '%s'))",
				objectRelationship1.getName(), objectRelationship2.getName(),
				_TAG_3, RandomTestUtil.randomString()),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"%s/%s/keywords/any(k:k ne '%s')",
				objectRelationship1.getName(), objectRelationship2.getName(),
				TAG_1),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"%s/%s/keywords/any(k:startswith(k,'%s'))",
				objectRelationship1.getName(), objectRelationship2.getName(),
				_TAG_3.substring(0, 2)),
			getObjectDefinition1());
	}

	private void
			_testFilterByListOperatorsObjectEntriesByRelatedObjectEntriesFields(
				ObjectEntry objectEntry1, ObjectEntry objectEntry2, String type)
		throws Exception {

		ObjectRelationship objectRelationship =
			addObjectRelationshipAndRelateObjectEntries(
				getObjectDefinition1(), getObjectDefinition2(),
				objectEntry1.getPrimaryKey(), objectEntry2.getPrimaryKey(),
				type);

		// Custom object field

		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"%s/%s in ('%s', '%s')", objectRelationship.getName(),
				OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2,
				RandomTestUtil.randomInt()),
			getObjectDefinition1());

		// Other side, custom object field

		assertFilterString(
			OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2,
			String.format(
				"%s/%s in ('%s', '%s')", objectRelationship.getName(),
				OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
				RandomTestUtil.randomInt()),
			getObjectDefinition2());

		// Other side, system object field

		assertFilterString(
			OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2,
			String.format(
				"%s/id in ('%s', '%s')", objectRelationship.getName(),
				objectEntry1.getObjectEntryId(), RandomTestUtil.randomInt()),
			getObjectDefinition2());

		// System object field

		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"%s/id in ('%s', '%s')", objectRelationship.getName(),
				objectEntry2.getObjectEntryId(), RandomTestUtil.randomInt()),
			getObjectDefinition1());

		objectRelationshipLocalService.deleteObjectRelationship(
			objectRelationship);

		// Self relationship, custom object field

		objectRelationship = addObjectRelationshipAndRelateObjectEntries(
			getObjectDefinition1(), getObjectDefinition1(),
			objectEntry1.getPrimaryKey(), objectEntry1.getPrimaryKey(), type);

		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"%s/%s in ('%s', '%s')", objectRelationship.getName(),
				OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
				RandomTestUtil.randomInt()),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"%s/%s/%s in ('%s', '%s')", objectRelationship.getName(),
				objectRelationship.getName(), OBJECT_FIELD_NAME_1,
				OBJECT_FIELD_VALUE_1, RandomTestUtil.randomInt()),
			getObjectDefinition1());
	}

	private void
			_testFilterByLogicalOperatorsObjectEntriesByRelatedObjectEntriesFields(
				ObjectEntry objectEntry1, ObjectEntry objectEntry2, String type)
		throws Exception {

		ObjectRelationship objectRelationship =
			addObjectRelationshipAndRelateObjectEntries(
				getObjectDefinition1(), getObjectDefinition2(),
				objectEntry1.getPrimaryKey(), objectEntry2.getPrimaryKey(),
				type);

		// Custom object field

		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"%s/%s le '%s' and %s/%s gt '%s'", objectRelationship.getName(),
				OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2,
				objectRelationship.getName(), OBJECT_FIELD_NAME_2,
				OBJECT_FIELD_VALUE_2 - 1),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"%s/%s le '%s' or %s/%s gt '%s'", objectRelationship.getName(),
				OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2,
				objectRelationship.getName(), OBJECT_FIELD_NAME_2,
				OBJECT_FIELD_VALUE_2 - 1),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"not (%s/%s ge '%s')", objectRelationship.getName(),
				OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2 + 1),
			getObjectDefinition1());

		// Other side, custom object field

		assertFilterString(
			OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2,
			String.format(
				"%s/%s le '%s' and %s/%s gt '%s'", objectRelationship.getName(),
				OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
				objectRelationship.getName(), OBJECT_FIELD_NAME_1,
				OBJECT_FIELD_VALUE_1 - 1),
			getObjectDefinition2());
		assertFilterString(
			OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2,
			String.format(
				"%s/%s le '%s' or %s/%s gt '%s'", objectRelationship.getName(),
				OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
				objectRelationship.getName(), OBJECT_FIELD_NAME_1,
				OBJECT_FIELD_VALUE_1 - 1),
			getObjectDefinition2());
		assertFilterString(
			OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2,
			String.format(
				"not (%s/%s ge '%s')", objectRelationship.getName(),
				OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1 + 1),
			getObjectDefinition2());

		// Other side, system object field

		assertFilterString(
			OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2,
			String.format(
				"%s/id le '%s' and %s/id gt '%s'", objectRelationship.getName(),
				objectEntry1.getObjectEntryId(), objectRelationship.getName(),
				objectEntry1.getObjectEntryId() - 1),
			getObjectDefinition2());
		assertFilterString(
			OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2,
			String.format(
				"%s/id le '%s' or %s/id gt '%s'", objectRelationship.getName(),
				objectEntry1.getObjectEntryId(), objectRelationship.getName(),
				objectEntry1.getObjectEntryId() - 1),
			getObjectDefinition2());
		assertFilterString(
			OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2,
			String.format(
				"not (%s/id ge '%s')", objectRelationship.getName(),
				objectEntry1.getObjectEntryId() + 1),
			getObjectDefinition2());

		// System object field

		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"%s/id le '%s' and %s/id gt '%s'", objectRelationship.getName(),
				objectEntry2.getObjectEntryId(), objectRelationship.getName(),
				objectEntry2.getObjectEntryId() - 1),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"%s/id le '%s' or %s/id gt '%s'", objectRelationship.getName(),
				objectEntry2.getObjectEntryId(), objectRelationship.getName(),
				objectEntry2.getObjectEntryId() - 1),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"not (%s/id ge '%s')", objectRelationship.getName(),
				objectEntry2.getObjectEntryId() + 1),
			getObjectDefinition1());

		objectRelationshipLocalService.deleteObjectRelationship(
			objectRelationship);

		// Self relationship, custom object field

		objectRelationship = addObjectRelationshipAndRelateObjectEntries(
			getObjectDefinition1(), getObjectDefinition1(),
			objectEntry1.getPrimaryKey(), objectEntry1.getPrimaryKey(), type);

		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"%s/%s le '%s' and %s/%s gt '%s'", objectRelationship.getName(),
				OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
				objectRelationship.getName(), OBJECT_FIELD_NAME_1,
				OBJECT_FIELD_VALUE_1 - 1),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"%s/%s le '%s' or %s/%s gt '%s'", objectRelationship.getName(),
				OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
				objectRelationship.getName(), OBJECT_FIELD_NAME_1,
				OBJECT_FIELD_VALUE_1 - 1),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"%s/%s/%s le '%s' and %s/%s/%s gt '%s'",
				objectRelationship.getName(), objectRelationship.getName(),
				OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
				objectRelationship.getName(), objectRelationship.getName(),
				OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1 - 1),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"%s/%s/%s le '%s' or %s/%s/%s gt '%s'",
				objectRelationship.getName(), objectRelationship.getName(),
				OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
				objectRelationship.getName(), objectRelationship.getName(),
				OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1 - 1),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"not (%s/%s ge '%s')", objectRelationship.getName(),
				OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1 + 1),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"not (%s/%s/%s ge '%s')", objectRelationship.getName(),
				objectRelationship.getName(), OBJECT_FIELD_NAME_1,
				OBJECT_FIELD_VALUE_1 + 1),
			getObjectDefinition1());
	}

	private void
			_testFilterByLogicalOperatorsObjectEntriesByRelatedObjectEntriesFieldsThroughMultipleRelationships(
				ObjectEntry objectEntry1, ObjectEntry objectEntry2,
				ObjectEntry objectEntry3, String type)
		throws Exception {

		ObjectRelationship objectRelationship1 =
			addObjectRelationshipAndRelateObjectEntries(
				getObjectDefinition1(), getObjectDefinition2(),
				objectEntry1.getPrimaryKey(), objectEntry2.getPrimaryKey(),
				type);
		ObjectRelationship objectRelationship2 =
			addObjectRelationshipAndRelateObjectEntries(
				getObjectDefinition2(), getObjectDefinition3(),
				objectEntry2.getPrimaryKey(), objectEntry3.getPrimaryKey(),
				type);

		// Custom object field

		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"%s/%s/%s le '%s' and %s/%s/%s gt '%s'",
				objectRelationship1.getName(), objectRelationship2.getName(),
				OBJECT_FIELD_NAME_3, OBJECT_FIELD_VALUE_3,
				objectRelationship1.getName(), objectRelationship2.getName(),
				OBJECT_FIELD_NAME_3, OBJECT_FIELD_VALUE_3 - 1),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"%s/%s/%s le '%s' or %s/%s/%s gt '%s'",
				objectRelationship1.getName(), objectRelationship2.getName(),
				OBJECT_FIELD_NAME_3, OBJECT_FIELD_VALUE_3,
				objectRelationship1.getName(), objectRelationship2.getName(),
				OBJECT_FIELD_NAME_3, OBJECT_FIELD_VALUE_3 - 1),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"not (%s/%s/%s ge '%s')", objectRelationship1.getName(),
				objectRelationship2.getName(), OBJECT_FIELD_NAME_3,
				OBJECT_FIELD_VALUE_3 + 1),
			getObjectDefinition1());

		// Other side, custom object field

		assertFilterString(
			OBJECT_FIELD_NAME_3, OBJECT_FIELD_VALUE_3,
			String.format(
				"%s/%s/%s le '%s' and %s/%s/%s gt '%s'",
				objectRelationship2.getName(), objectRelationship1.getName(),
				OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
				objectRelationship2.getName(), objectRelationship1.getName(),
				OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1 - 1),
			getObjectDefinition3());
		assertFilterString(
			OBJECT_FIELD_NAME_3, OBJECT_FIELD_VALUE_3,
			String.format(
				"%s/%s/%s le '%s' or %s/%s/%s gt '%s'",
				objectRelationship2.getName(), objectRelationship1.getName(),
				OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
				objectRelationship2.getName(), objectRelationship1.getName(),
				OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1 - 1),
			getObjectDefinition3());
		assertFilterString(
			OBJECT_FIELD_NAME_3, OBJECT_FIELD_VALUE_3,
			String.format(
				"not (%s/%s/%s ge '%s')", objectRelationship2.getName(),
				objectRelationship1.getName(), OBJECT_FIELD_NAME_1,
				OBJECT_FIELD_VALUE_1 + 1),
			getObjectDefinition3());

		// Other side, system object field

		assertFilterString(
			OBJECT_FIELD_NAME_3, OBJECT_FIELD_VALUE_3,
			String.format(
				"%s/%s/id le '%s' and %s/%s/id gt '%s'",
				objectRelationship2.getName(), objectRelationship1.getName(),
				objectEntry1.getObjectEntryId(), objectRelationship2.getName(),
				objectRelationship1.getName(),
				objectEntry1.getObjectEntryId() - 1),
			getObjectDefinition3());
		assertFilterString(
			OBJECT_FIELD_NAME_3, OBJECT_FIELD_VALUE_3,
			String.format(
				"%s/%s/id le '%s' or %s/%s/id gt '%s'",
				objectRelationship2.getName(), objectRelationship1.getName(),
				objectEntry1.getObjectEntryId(), objectRelationship2.getName(),
				objectRelationship1.getName(),
				objectEntry1.getObjectEntryId() - 1),
			getObjectDefinition3());
		assertFilterString(
			OBJECT_FIELD_NAME_3, OBJECT_FIELD_VALUE_3,
			String.format(
				"not (%s/%s/id ge '%s')", objectRelationship2.getName(),
				objectRelationship1.getName(),
				objectEntry1.getObjectEntryId() + 1),
			getObjectDefinition3());

		// System object field

		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"%s/%s/id le '%s' and %s/%s/id gt '%s'",
				objectRelationship1.getName(), objectRelationship2.getName(),
				objectEntry3.getObjectEntryId(), objectRelationship1.getName(),
				objectRelationship2.getName(),
				objectEntry3.getObjectEntryId() - 1),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"%s/%s/id le '%s' or %s/%s/id gt '%s'",
				objectRelationship1.getName(), objectRelationship2.getName(),
				objectEntry3.getObjectEntryId(), objectRelationship1.getName(),
				objectRelationship2.getName(),
				objectEntry3.getObjectEntryId() - 1),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"not (%s/%s/id ge '%s')", objectRelationship1.getName(),
				objectRelationship2.getName(),
				objectEntry3.getObjectEntryId() + 1),
			getObjectDefinition1());
	}

	private void
			_testFilterByStringOperatorsObjectEntriesByRelatedObjectEntriesFields(
				ObjectEntry objectEntry1, ObjectEntry objectEntry2, String type)
		throws Exception {

		ObjectRelationship objectRelationship =
			addObjectRelationshipAndRelateObjectEntries(
				getObjectDefinition1(), getObjectDefinition2(),
				objectEntry1.getPrimaryKey(), objectEntry2.getPrimaryKey(),
				type);

		// Custom object field

		String objectFieldValue2 = String.valueOf(OBJECT_FIELD_VALUE_2);

		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"contains(%s/%s,'%s')", objectRelationship.getName(),
				OBJECT_FIELD_NAME_2, objectFieldValue2.substring(1)),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"startswith(%s/%s,'%s')", objectRelationship.getName(),
				OBJECT_FIELD_NAME_2, objectFieldValue2.substring(0, 2)),
			getObjectDefinition1());

		// Other side, custom object field

		String objectFieldValue1 = String.valueOf(OBJECT_FIELD_VALUE_1);

		assertFilterString(
			OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2,
			String.format(
				"contains(%s/%s,'%s')", objectRelationship.getName(),
				OBJECT_FIELD_NAME_1, objectFieldValue1.substring(1)),
			getObjectDefinition2());
		assertFilterString(
			OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2,
			String.format(
				"startswith(%s/%s,'%s')", objectRelationship.getName(),
				OBJECT_FIELD_NAME_1, objectFieldValue1.substring(0, 2)),
			getObjectDefinition2());

		// Other side, system object field

		String objectEntry1ExternalReferenceCode =
			objectEntry1.getExternalReferenceCode();

		assertFilterString(
			OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2,
			String.format(
				"contains(%s/externalReferenceCode,'%s')",
				objectRelationship.getName(),
				objectEntry1ExternalReferenceCode.substring(1)),
			getObjectDefinition2());
		assertFilterString(
			OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2,
			String.format(
				"startswith(%s/externalReferenceCode,'%s')",
				objectRelationship.getName(),
				objectEntry1ExternalReferenceCode.substring(0, 2)),
			getObjectDefinition2());

		// System object field

		String objectEntry2ExternalReferenceCode =
			objectEntry2.getExternalReferenceCode();

		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"contains(%s/externalReferenceCode,'%s')",
				objectRelationship.getName(),
				objectEntry2ExternalReferenceCode.substring(1)),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"startswith(%s/externalReferenceCode,'%s')",
				objectRelationship.getName(),
				objectEntry2ExternalReferenceCode.substring(0, 2)),
			getObjectDefinition1());

		objectRelationshipLocalService.deleteObjectRelationship(
			objectRelationship);

		// Self relationship, custom object field

		objectRelationship = addObjectRelationshipAndRelateObjectEntries(
			getObjectDefinition1(), getObjectDefinition1(),
			objectEntry1.getPrimaryKey(), objectEntry1.getPrimaryKey(), type);

		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"contains(%s/%s,'%s')", objectRelationship.getName(),
				OBJECT_FIELD_NAME_1, objectFieldValue1.substring(1)),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"contains(%s/%s/%s,'%s')", objectRelationship.getName(),
				objectRelationship.getName(), OBJECT_FIELD_NAME_1,
				objectFieldValue1.substring(1)),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"startswith(%s/%s,'%s')", objectRelationship.getName(),
				OBJECT_FIELD_NAME_1, objectFieldValue1.substring(0, 2)),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"startswith(%s/%s/%s,'%s')", objectRelationship.getName(),
				objectRelationship.getName(), OBJECT_FIELD_NAME_1,
				objectFieldValue1.substring(0, 2)),
			getObjectDefinition1());
	}

	private void
			_testFilterByStringOperatorsObjectEntriesByRelatedObjectEntriesFieldsThroughMultipleRelationships(
				ObjectEntry objectEntry1, ObjectEntry objectEntry2,
				ObjectEntry objectEntry3, String type)
		throws Exception {

		ObjectRelationship objectRelationship1 =
			addObjectRelationshipAndRelateObjectEntries(
				getObjectDefinition1(), getObjectDefinition2(),
				objectEntry1.getPrimaryKey(), objectEntry2.getPrimaryKey(),
				type);
		ObjectRelationship objectRelationship2 =
			addObjectRelationshipAndRelateObjectEntries(
				getObjectDefinition2(), getObjectDefinition3(),
				objectEntry2.getPrimaryKey(), objectEntry3.getPrimaryKey(),
				type);

		// Custom object field

		String objectFieldValue3 = String.valueOf(OBJECT_FIELD_VALUE_3);

		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"contains(%s/%s/%s,'%s')", objectRelationship1.getName(),
				objectRelationship2.getName(), OBJECT_FIELD_NAME_3,
				objectFieldValue3.substring(1)),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"startswith(%s/%s/%s,'%s')", objectRelationship1.getName(),
				objectRelationship2.getName(), OBJECT_FIELD_NAME_3,
				objectFieldValue3.substring(0, 2)),
			getObjectDefinition1());

		// Other side, custom object field

		String objectFieldValue1 = String.valueOf(OBJECT_FIELD_VALUE_1);

		assertFilterString(
			OBJECT_FIELD_NAME_3, OBJECT_FIELD_VALUE_3,
			String.format(
				"contains(%s/%s/%s,'%s')", objectRelationship2.getName(),
				objectRelationship1.getName(), OBJECT_FIELD_NAME_1,
				objectFieldValue1.substring(1)),
			getObjectDefinition3());
		assertFilterString(
			OBJECT_FIELD_NAME_3, OBJECT_FIELD_VALUE_3,
			String.format(
				"startswith(%s/%s/%s,'%s')", objectRelationship2.getName(),
				objectRelationship1.getName(), OBJECT_FIELD_NAME_1,
				objectFieldValue1.substring(0, 2)),
			getObjectDefinition3());

		// Other side, system object field

		String objectEntry1ExternalReferenceCode =
			objectEntry1.getExternalReferenceCode();

		assertFilterString(
			OBJECT_FIELD_NAME_3, OBJECT_FIELD_VALUE_3,
			String.format(
				"contains(%s/%s/externalReferenceCode,'%s')",
				objectRelationship2.getName(), objectRelationship1.getName(),
				objectEntry1ExternalReferenceCode.substring(1)),
			getObjectDefinition3());
		assertFilterString(
			OBJECT_FIELD_NAME_3, OBJECT_FIELD_VALUE_3,
			String.format(
				"startswith(%s/%s/externalReferenceCode,'%s')",
				objectRelationship2.getName(), objectRelationship1.getName(),
				objectEntry1ExternalReferenceCode.substring(0, 2)),
			getObjectDefinition3());

		// System object field

		String objectEntry3ExternalReferenceCode =
			objectEntry3.getExternalReferenceCode();

		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"contains(%s/%s/externalReferenceCode,'%s')",
				objectRelationship1.getName(), objectRelationship2.getName(),
				objectEntry3ExternalReferenceCode.substring(1)),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"startswith(%s/%s/externalReferenceCode,'%s')",
				objectRelationship1.getName(), objectRelationship2.getName(),
				objectEntry3ExternalReferenceCode.substring(0, 2)),
			getObjectDefinition1());
	}

	private void _testFilterObjectEntriesByRelatedLocalizedObjectEntries(
			String filterString, ObjectDefinition objectDefinition,
			ObjectEntry objectEntry, String w3cLanguageId)
		throws Exception {

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			objectDefinition.getRESTContextPath() + "?filter=" +
				URLCodec.encodeURL(filterString),
			HashMapBuilder.put(
				"Accept-Language", w3cLanguageId
			).build(),
			Http.Method.GET);

		JSONArray itemsJSONArray = jsonObject.getJSONArray("items");

		Assert.assertEquals(1, itemsJSONArray.length());

		JSONObject itemJSONObject = itemsJSONArray.getJSONObject(0);

		Assert.assertEquals(
			objectEntry.getObjectEntryId(), itemJSONObject.getLong("id"));
	}

	private void _testFilterObjectEntriesByRelatedSystemObjectEntriesFields(
			String filterString)
		throws Exception {

		ObjectDefinition objectDefinition = getObjectDefinition1();

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			objectDefinition.getRESTContextPath() + "?filter=" +
				URLCodec.encodeURL(filterString),
			Http.Method.GET);

		Assert.assertEquals("BAD_REQUEST", jsonObject.getString("status"));
		Assert.assertEquals(
			"Filtering is not supported for system objects",
			jsonObject.getString("title"));
	}

	private static final String _TAG_2 = StringUtil.toLowerCase(
		RandomTestUtil.randomString(UniqueStringRandomizerBumper.INSTANCE));

	private static final String _TAG_3 = StringUtil.toLowerCase(
		RandomTestUtil.randomString(UniqueStringRandomizerBumper.INSTANCE));

}