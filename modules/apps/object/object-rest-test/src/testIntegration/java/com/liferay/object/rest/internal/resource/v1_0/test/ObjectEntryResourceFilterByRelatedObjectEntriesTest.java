/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.rest.internal.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
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

		// Many to many relationship, custom object field

		objectEntry1 = ObjectEntryTestUtil.addObjectEntry(
			getObjectDefinition1(), OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1);
		objectEntry2 = ObjectEntryTestUtil.addObjectEntry(
			getObjectDefinition2(), OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2);

		objectRelationship1 = addObjectRelationshipAndRelateObjectEntries(
			ObjectRelationshipConstants.TYPE_MANY_TO_MANY);

		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s eq '%s'", objectRelationship1.getName(),
					OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2)),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s ge '%s'", objectRelationship1.getName(),
					OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2)),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s gt '%s'", objectRelationship1.getName(),
					OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2 - 1)),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s le '%s'", objectRelationship1.getName(),
					OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2)),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s lt '%s'", objectRelationship1.getName(),
					OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2 + 1)),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s ne '%s'", objectRelationship1.getName(),
					OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2 - 1)),
			getObjectDefinition1());

		// Many to many relationship, system object field

		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/id eq '%s'", objectRelationship1.getName(),
					objectEntry2.getObjectEntryId())),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/id ge '%s'", objectRelationship1.getName(),
					objectEntry2.getObjectEntryId())),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/id gt '%s'", objectRelationship1.getName(),
					objectEntry2.getObjectEntryId() - 1)),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/id le '%s'", objectRelationship1.getName(),
					objectEntry2.getObjectEntryId())),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/id lt '%s'", objectRelationship1.getName(),
					objectEntry2.getObjectEntryId() + 1)),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/id ne '%s'", objectRelationship1.getName(),
					objectEntry2.getObjectEntryId() - 1)),
			getObjectDefinition1());

		// Many to many relationship (other side), custom object field

		assertFilterString(
			OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2,
			escape(
				String.format(
					"%s/%s eq '%s'", objectRelationship1.getName(),
					OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1)),
			getObjectDefinition2());
		assertFilterString(
			OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2,
			escape(
				String.format(
					"%s/%s ge '%s'", objectRelationship1.getName(),
					OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1)),
			getObjectDefinition2());
		assertFilterString(
			OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2,
			escape(
				String.format(
					"%s/%s gt '%s'", objectRelationship1.getName(),
					OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1 - 1)),
			getObjectDefinition2());
		assertFilterString(
			OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2,
			escape(
				String.format(
					"%s/%s le '%s'", objectRelationship1.getName(),
					OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1)),
			getObjectDefinition2());
		assertFilterString(
			OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2,
			escape(
				String.format(
					"%s/%s lt '%s'", objectRelationship1.getName(),
					OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1 + 1)),
			getObjectDefinition2());
		assertFilterString(
			OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2,
			escape(
				String.format(
					"%s/%s ne '%s'", objectRelationship1.getName(),
					OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1 - 1)),
			getObjectDefinition2());

		// Many to many relationship (other side), system object field

		assertFilterString(
			OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2,
			escape(
				String.format(
					"%s/id eq '%s'", objectRelationship1.getName(),
					objectEntry1.getObjectEntryId())),
			getObjectDefinition2());
		assertFilterString(
			OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2,
			escape(
				String.format(
					"%s/id ge '%s'", objectRelationship1.getName(),
					objectEntry1.getObjectEntryId())),
			getObjectDefinition2());
		assertFilterString(
			OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2,
			escape(
				String.format(
					"%s/id gt '%s'", objectRelationship1.getName(),
					objectEntry1.getObjectEntryId() - 1)),
			getObjectDefinition2());
		assertFilterString(
			OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2,
			escape(
				String.format(
					"%s/id le '%s'", objectRelationship1.getName(),
					objectEntry1.getObjectEntryId())),
			getObjectDefinition2());
		assertFilterString(
			OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2,
			escape(
				String.format(
					"%s/id lt '%s'", objectRelationship1.getName(),
					objectEntry1.getObjectEntryId() + 1)),
			getObjectDefinition2());
		assertFilterString(
			OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2,
			escape(
				String.format(
					"%s/id ne '%s'", objectRelationship1.getName(),
					objectEntry1.getObjectEntryId() - 1)),
			getObjectDefinition2());

		objectRelationshipLocalService.deleteObjectRelationship(
			objectRelationship1);

		// Many to many self relationship, custom object field

		objectRelationship1 = addObjectRelationshipAndRelateObjectEntries(
			getObjectDefinition1(), getObjectDefinition1(),
			objectEntry1.getPrimaryKey(), objectEntry1.getPrimaryKey(),
			ObjectRelationshipConstants.TYPE_MANY_TO_MANY);

		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s eq '%s'", objectRelationship1.getName(),
					OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1)),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s/%s eq '%s'", objectRelationship1.getName(),
					objectRelationship1.getName(), OBJECT_FIELD_NAME_1,
					OBJECT_FIELD_VALUE_1)),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s ge '%s'", objectRelationship1.getName(),
					OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1)),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s/%s ge '%s'", objectRelationship1.getName(),
					objectRelationship1.getName(), OBJECT_FIELD_NAME_1,
					OBJECT_FIELD_VALUE_1)),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s gt '%s'", objectRelationship1.getName(),
					OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1 - 1)),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s/%s gt '%s'", objectRelationship1.getName(),
					objectRelationship1.getName(), OBJECT_FIELD_NAME_1,
					OBJECT_FIELD_VALUE_1 - 1)),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s le '%s'", objectRelationship1.getName(),
					OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1)),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s/%s le '%s'", objectRelationship1.getName(),
					objectRelationship1.getName(), OBJECT_FIELD_NAME_1,
					OBJECT_FIELD_VALUE_1)),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s lt '%s'", objectRelationship1.getName(),
					OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1 + 1)),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s/%s lt '%s'", objectRelationship1.getName(),
					objectRelationship1.getName(), OBJECT_FIELD_NAME_1,
					OBJECT_FIELD_VALUE_1 + 1)),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s ne '%s'", objectRelationship1.getName(),
					OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1 - 1)),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s/%s ne '%s'", objectRelationship1.getName(),
					objectRelationship1.getName(), OBJECT_FIELD_NAME_1,
					OBJECT_FIELD_VALUE_1 - 1)),
			getObjectDefinition1());

		objectRelationshipLocalService.deleteObjectRelationship(
			objectRelationship1);

		// One to many relationship, custom object field

		objectRelationship1 = addObjectRelationshipAndRelateObjectEntries(
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);

		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s eq '%s'", objectRelationship1.getName(),
					StringUtil.removeFirst(
						getObjectDefinition1().getPKObjectFieldName(), "c_"),
					objectEntry1.getPrimaryKey())),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s eq '%s'", objectRelationship1.getName(),
					OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2)),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s ge '%s'", objectRelationship1.getName(),
					OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2)),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s gt '%s'", objectRelationship1.getName(),
					OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2 - 1)),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s le '%s'", objectRelationship1.getName(),
					OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2)),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s lt '%s'", objectRelationship1.getName(),
					OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2 + 1)),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s ne '%s'", objectRelationship1.getName(),
					OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2 - 1)),
			getObjectDefinition1());

		// One to many relationship, system object field

		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/id eq '%s'", objectRelationship1.getName(),
					objectEntry2.getObjectEntryId())),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/id ge '%s'", objectRelationship1.getName(),
					objectEntry2.getObjectEntryId())),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/id gt '%s'", objectRelationship1.getName(),
					objectEntry2.getObjectEntryId() - 1)),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/id le '%s'", objectRelationship1.getName(),
					objectEntry2.getObjectEntryId())),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/id lt '%s'", objectRelationship1.getName(),
					objectEntry2.getObjectEntryId() + 1)),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/id ne '%s'", objectRelationship1.getName(),
					objectEntry2.getObjectEntryId() - 1)),
			getObjectDefinition1());

		// One to many relationship (other side), custom object field

		assertFilterString(
			OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2,
			escape(
				String.format(
					"%s/%s eq '%s'", objectRelationship1.getName(),
					OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1)),
			getObjectDefinition2());
		assertFilterString(
			OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2,
			escape(
				String.format(
					"%s/%s ge '%s'", objectRelationship1.getName(),
					OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1)),
			getObjectDefinition2());
		assertFilterString(
			OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2,
			escape(
				String.format(
					"%s/%s gt '%s'", objectRelationship1.getName(),
					OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1 - 1)),
			getObjectDefinition2());
		assertFilterString(
			OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2,
			escape(
				String.format(
					"%s/%s le '%s'", objectRelationship1.getName(),
					OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1)),
			getObjectDefinition2());
		assertFilterString(
			OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2,
			escape(
				String.format(
					"%s/%s lt '%s'", objectRelationship1.getName(),
					OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1 + 1)),
			getObjectDefinition2());
		assertFilterString(
			OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2,
			escape(
				String.format(
					"%s/%s ne '%s'", objectRelationship1.getName(),
					OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1 - 1)),
			getObjectDefinition2());

		// One to many relationship (other side), system object field

		assertFilterString(
			OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2,
			escape(
				String.format(
					"%s/id eq '%s'", objectRelationship1.getName(),
					objectEntry1.getObjectEntryId())),
			getObjectDefinition2());
		assertFilterString(
			OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2,
			escape(
				String.format(
					"%s/id ge '%s'", objectRelationship1.getName(),
					objectEntry1.getObjectEntryId())),
			getObjectDefinition2());
		assertFilterString(
			OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2,
			escape(
				String.format(
					"%s/id gt '%s'", objectRelationship1.getName(),
					objectEntry1.getObjectEntryId() - 1)),
			getObjectDefinition2());
		assertFilterString(
			OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2,
			escape(
				String.format(
					"%s/id le '%s'", objectRelationship1.getName(),
					objectEntry1.getObjectEntryId())),
			getObjectDefinition2());
		assertFilterString(
			OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2,
			escape(
				String.format(
					"%s/id lt '%s'", objectRelationship1.getName(),
					objectEntry1.getObjectEntryId() + 1)),
			getObjectDefinition2());
		assertFilterString(
			OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2,
			escape(
				String.format(
					"%s/id ne '%s'", objectRelationship1.getName(),
					objectEntry1.getObjectEntryId() - 1)),
			getObjectDefinition2());

		objectRelationshipLocalService.deleteObjectRelationship(
			objectRelationship1);

		// One to many self relationship, custom object field

		objectRelationship1 = addObjectRelationshipAndRelateObjectEntries(
			getObjectDefinition1(), getObjectDefinition1(),
			objectEntry1.getPrimaryKey(), objectEntry1.getPrimaryKey(),
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);

		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s eq '%s'", objectRelationship1.getName(),
					OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1)),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s/%s eq '%s'", objectRelationship1.getName(),
					objectRelationship1.getName(), OBJECT_FIELD_NAME_1,
					OBJECT_FIELD_VALUE_1)),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s ge '%s'", objectRelationship1.getName(),
					OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1)),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s/%s ge '%s'", objectRelationship1.getName(),
					objectRelationship1.getName(), OBJECT_FIELD_NAME_1,
					OBJECT_FIELD_VALUE_1)),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s gt '%s'", objectRelationship1.getName(),
					OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1 - 1)),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s/%s gt '%s'", objectRelationship1.getName(),
					objectRelationship1.getName(), OBJECT_FIELD_NAME_1,
					OBJECT_FIELD_VALUE_1 - 1)),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s le '%s'", objectRelationship1.getName(),
					OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1)),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s/%s le '%s'", objectRelationship1.getName(),
					objectRelationship1.getName(), OBJECT_FIELD_NAME_1,
					OBJECT_FIELD_VALUE_1)),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s lt '%s'", objectRelationship1.getName(),
					OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1 + 1)),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s/%s lt '%s'", objectRelationship1.getName(),
					objectRelationship1.getName(), OBJECT_FIELD_NAME_1,
					OBJECT_FIELD_VALUE_1 + 1)),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s ne '%s'", objectRelationship1.getName(),
					OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1 - 1)),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s/%s ne '%s'", objectRelationship1.getName(),
					objectRelationship1.getName(), OBJECT_FIELD_NAME_1,
					OBJECT_FIELD_VALUE_1 - 1)),
			getObjectDefinition1());
	}

	@Test
	public void testFilterByComparisonOperatorsObjectEntriesByRelatedObjectEntriesFieldsThroughMultipleRelationships()
		throws Exception {

		// Many to many relationship, custom object field

		objectEntry1 = ObjectEntryTestUtil.addObjectEntry(
			getObjectDefinition1(), OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1);
		objectEntry2 = ObjectEntryTestUtil.addObjectEntry(
			getObjectDefinition2(), OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2);
		objectEntry3 = ObjectEntryTestUtil.addObjectEntry(
			getObjectDefinition3(), OBJECT_FIELD_NAME_3, OBJECT_FIELD_VALUE_3);
		objectEntry4 = ObjectEntryTestUtil.addObjectEntry(
			getObjectDefinition4(), OBJECT_FIELD_NAME_TEXT,
			OBJECT_FIELD_VALUE_4);

		objectRelationship1 = addObjectRelationshipAndRelateObjectEntries(
			getObjectDefinition1(), getObjectDefinition2(),
			objectEntry1.getPrimaryKey(), objectEntry2.getPrimaryKey(),
			ObjectRelationshipConstants.TYPE_MANY_TO_MANY);
		objectRelationship2 = addObjectRelationshipAndRelateObjectEntries(
			getObjectDefinition2(), getObjectDefinition3(),
			objectEntry2.getPrimaryKey(), objectEntry3.getPrimaryKey(),
			ObjectRelationshipConstants.TYPE_MANY_TO_MANY);

		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s/%s eq '%s'", objectRelationship1.getName(),
					objectRelationship2.getName(), OBJECT_FIELD_NAME_3,
					OBJECT_FIELD_VALUE_3)),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s/%s ge '%s'", objectRelationship1.getName(),
					objectRelationship2.getName(), OBJECT_FIELD_NAME_3,
					OBJECT_FIELD_VALUE_3)),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s/%s gt '%s'", objectRelationship1.getName(),
					objectRelationship2.getName(), OBJECT_FIELD_NAME_3,
					OBJECT_FIELD_VALUE_3 - 1)),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s/%s le '%s'", objectRelationship1.getName(),
					objectRelationship2.getName(), OBJECT_FIELD_NAME_3,
					OBJECT_FIELD_VALUE_3)),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s/%s lt '%s'", objectRelationship1.getName(),
					objectRelationship2.getName(), OBJECT_FIELD_NAME_3,
					OBJECT_FIELD_VALUE_3 + 1)),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s/%s ne '%s'", objectRelationship1.getName(),
					objectRelationship2.getName(), OBJECT_FIELD_NAME_3,
					OBJECT_FIELD_VALUE_3 - 1)),
			getObjectDefinition1());

		// Many to many relationship, system object field

		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s/id eq '%s'", objectRelationship1.getName(),
					objectRelationship2.getName(),
					objectEntry3.getObjectEntryId())),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s/id ge '%s'", objectRelationship1.getName(),
					objectRelationship2.getName(),
					objectEntry3.getObjectEntryId())),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s/id gt '%s'", objectRelationship1.getName(),
					objectRelationship2.getName(),
					objectEntry3.getObjectEntryId() - 1)),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s/id le '%s'", objectRelationship1.getName(),
					objectRelationship2.getName(),
					objectEntry3.getObjectEntryId())),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s/id lt '%s'", objectRelationship1.getName(),
					objectRelationship2.getName(),
					objectEntry3.getObjectEntryId() + 1)),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s/id ne '%s'", objectRelationship1.getName(),
					objectRelationship2.getName(),
					objectEntry3.getObjectEntryId() - 1)),
			getObjectDefinition1());

		// Many to many relationship (other side), custom object field

		assertFilterString(
			OBJECT_FIELD_NAME_3, OBJECT_FIELD_VALUE_3,
			escape(
				String.format(
					"%s/%s/%s eq '%s'", objectRelationship2.getName(),
					objectRelationship1.getName(), OBJECT_FIELD_NAME_1,
					OBJECT_FIELD_VALUE_1)),
			getObjectDefinition3());
		assertFilterString(
			OBJECT_FIELD_NAME_3, OBJECT_FIELD_VALUE_3,
			escape(
				String.format(
					"%s/%s/%s ge '%s'", objectRelationship2.getName(),
					objectRelationship1.getName(), OBJECT_FIELD_NAME_1,
					OBJECT_FIELD_VALUE_1)),
			getObjectDefinition3());
		assertFilterString(
			OBJECT_FIELD_NAME_3, OBJECT_FIELD_VALUE_3,
			escape(
				String.format(
					"%s/%s/%s gt '%s'", objectRelationship2.getName(),
					objectRelationship1.getName(), OBJECT_FIELD_NAME_1,
					OBJECT_FIELD_VALUE_1 - 1)),
			getObjectDefinition3());
		assertFilterString(
			OBJECT_FIELD_NAME_3, OBJECT_FIELD_VALUE_3,
			escape(
				String.format(
					"%s/%s/%s le '%s'", objectRelationship2.getName(),
					objectRelationship1.getName(), OBJECT_FIELD_NAME_1,
					OBJECT_FIELD_VALUE_1)),
			getObjectDefinition3());
		assertFilterString(
			OBJECT_FIELD_NAME_3, OBJECT_FIELD_VALUE_3,
			escape(
				String.format(
					"%s/%s/%s lt '%s'", objectRelationship2.getName(),
					objectRelationship1.getName(), OBJECT_FIELD_NAME_1,
					OBJECT_FIELD_VALUE_1 + 1)),
			getObjectDefinition3());
		assertFilterString(
			OBJECT_FIELD_NAME_3, OBJECT_FIELD_VALUE_3,
			escape(
				String.format(
					"%s/%s/%s ne '%s'", objectRelationship2.getName(),
					objectRelationship1.getName(), OBJECT_FIELD_NAME_1,
					OBJECT_FIELD_VALUE_1 - 1)),
			getObjectDefinition3());

		// Many to many relationship (other side), system object field

		assertFilterString(
			OBJECT_FIELD_NAME_3, OBJECT_FIELD_VALUE_3,
			escape(
				String.format(
					"%s/%s/id eq '%s'", objectRelationship2.getName(),
					objectRelationship1.getName(),
					objectEntry1.getObjectEntryId())),
			getObjectDefinition3());
		assertFilterString(
			OBJECT_FIELD_NAME_3, OBJECT_FIELD_VALUE_3,
			escape(
				String.format(
					"%s/%s/id ge '%s'", objectRelationship2.getName(),
					objectRelationship1.getName(),
					objectEntry1.getObjectEntryId())),
			getObjectDefinition3());
		assertFilterString(
			OBJECT_FIELD_NAME_3, OBJECT_FIELD_VALUE_3,
			escape(
				String.format(
					"%s/%s/id gt '%s'", objectRelationship2.getName(),
					objectRelationship1.getName(),
					objectEntry1.getObjectEntryId() - 1)),
			getObjectDefinition3());
		assertFilterString(
			OBJECT_FIELD_NAME_3, OBJECT_FIELD_VALUE_3,
			escape(
				String.format(
					"%s/%s/id le '%s'", objectRelationship2.getName(),
					objectRelationship1.getName(),
					objectEntry1.getObjectEntryId())),
			getObjectDefinition3());
		assertFilterString(
			OBJECT_FIELD_NAME_3, OBJECT_FIELD_VALUE_3,
			escape(
				String.format(
					"%s/%s/id lt '%s'", objectRelationship2.getName(),
					objectRelationship1.getName(),
					objectEntry1.getObjectEntryId() + 1)),
			getObjectDefinition3());
		assertFilterString(
			OBJECT_FIELD_NAME_3, OBJECT_FIELD_VALUE_3,
			escape(
				String.format(
					"%s/%s/id ne '%s'", objectRelationship2.getName(),
					objectRelationship1.getName(),
					objectEntry1.getObjectEntryId() - 1)),
			getObjectDefinition3());

		objectRelationshipLocalService.deleteObjectRelationship(
			objectRelationship1);
		objectRelationshipLocalService.deleteObjectRelationship(
			objectRelationship2);

		// One to many relationship, custom object field

		objectRelationship1 = addObjectRelationshipAndRelateObjectEntries(
			getObjectDefinition1(), getObjectDefinition2(),
			objectEntry1.getPrimaryKey(), objectEntry2.getPrimaryKey(),
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);

		objectRelationship2 = addObjectRelationshipAndRelateObjectEntries(
			getObjectDefinition2(), getObjectDefinition3(),
			objectEntry2.getPrimaryKey(), objectEntry3.getPrimaryKey(),
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);

		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s/%s eq '%s'", objectRelationship1.getName(),
					objectRelationship2.getName(), OBJECT_FIELD_NAME_3,
					OBJECT_FIELD_VALUE_3)),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s/%s ge '%s'", objectRelationship1.getName(),
					objectRelationship2.getName(), OBJECT_FIELD_NAME_3,
					OBJECT_FIELD_VALUE_3)),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s/%s gt '%s'", objectRelationship1.getName(),
					objectRelationship2.getName(), OBJECT_FIELD_NAME_3,
					OBJECT_FIELD_VALUE_3 - 1)),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s/%s le '%s'", objectRelationship1.getName(),
					objectRelationship2.getName(), OBJECT_FIELD_NAME_3,
					OBJECT_FIELD_VALUE_3)),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s/%s lt '%s'", objectRelationship1.getName(),
					objectRelationship2.getName(), OBJECT_FIELD_NAME_3,
					OBJECT_FIELD_VALUE_3 + 1)),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s/%s ne '%s'", objectRelationship1.getName(),
					objectRelationship2.getName(), OBJECT_FIELD_NAME_3,
					OBJECT_FIELD_VALUE_3 - 1)),
			getObjectDefinition1());

		// One to many relationship, system object field

		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s/id eq '%s'", objectRelationship1.getName(),
					objectRelationship2.getName(),
					objectEntry3.getObjectEntryId())),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s/id ge '%s'", objectRelationship1.getName(),
					objectRelationship2.getName(),
					objectEntry3.getObjectEntryId())),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s/id gt '%s'", objectRelationship1.getName(),
					objectRelationship2.getName(),
					objectEntry3.getObjectEntryId() - 1)),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s/id le '%s'", objectRelationship1.getName(),
					objectRelationship2.getName(),
					objectEntry3.getObjectEntryId())),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s/id lt '%s'", objectRelationship1.getName(),
					objectRelationship2.getName(),
					objectEntry3.getObjectEntryId() + 1)),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s/id ne '%s'", objectRelationship1.getName(),
					objectRelationship2.getName(),
					objectEntry3.getObjectEntryId() - 1)),
			getObjectDefinition1());

		// One to many relationship (other side), custom object field

		assertFilterString(
			OBJECT_FIELD_NAME_3, OBJECT_FIELD_VALUE_3,
			escape(
				String.format(
					"%s/%s/%s eq '%s'", objectRelationship2.getName(),
					objectRelationship1.getName(), OBJECT_FIELD_NAME_1,
					OBJECT_FIELD_VALUE_1)),
			getObjectDefinition3());
		assertFilterString(
			OBJECT_FIELD_NAME_3, OBJECT_FIELD_VALUE_3,
			escape(
				String.format(
					"%s/%s/%s ge '%s'", objectRelationship2.getName(),
					objectRelationship1.getName(), OBJECT_FIELD_NAME_1,
					OBJECT_FIELD_VALUE_1)),
			getObjectDefinition3());
		assertFilterString(
			OBJECT_FIELD_NAME_3, OBJECT_FIELD_VALUE_3,
			escape(
				String.format(
					"%s/%s/%s gt '%s'", objectRelationship2.getName(),
					objectRelationship1.getName(), OBJECT_FIELD_NAME_1,
					OBJECT_FIELD_VALUE_1 - 1)),
			getObjectDefinition3());
		assertFilterString(
			OBJECT_FIELD_NAME_3, OBJECT_FIELD_VALUE_3,
			escape(
				String.format(
					"%s/%s/%s le '%s'", objectRelationship2.getName(),
					objectRelationship1.getName(), OBJECT_FIELD_NAME_1,
					OBJECT_FIELD_VALUE_1)),
			getObjectDefinition3());
		assertFilterString(
			OBJECT_FIELD_NAME_3, OBJECT_FIELD_VALUE_3,
			escape(
				String.format(
					"%s/%s/%s lt '%s'", objectRelationship2.getName(),
					objectRelationship1.getName(), OBJECT_FIELD_NAME_1,
					OBJECT_FIELD_VALUE_1 + 1)),
			getObjectDefinition3());
		assertFilterString(
			OBJECT_FIELD_NAME_3, OBJECT_FIELD_VALUE_3,
			escape(
				String.format(
					"%s/%s/%s ne '%s'", objectRelationship2.getName(),
					objectRelationship1.getName(), OBJECT_FIELD_NAME_1,
					OBJECT_FIELD_VALUE_1 - 1)),
			getObjectDefinition3());

		// One to many relationship (other side), system object field

		assertFilterString(
			OBJECT_FIELD_NAME_3, OBJECT_FIELD_VALUE_3,
			escape(
				String.format(
					"%s/%s/id eq '%s'", objectRelationship2.getName(),
					objectRelationship1.getName(),
					objectEntry1.getObjectEntryId())),
			getObjectDefinition3());
		assertFilterString(
			OBJECT_FIELD_NAME_3, OBJECT_FIELD_VALUE_3,
			escape(
				String.format(
					"%s/%s/id ge '%s'", objectRelationship2.getName(),
					objectRelationship1.getName(),
					objectEntry1.getObjectEntryId())),
			getObjectDefinition3());
		assertFilterString(
			OBJECT_FIELD_NAME_3, OBJECT_FIELD_VALUE_3,
			escape(
				String.format(
					"%s/%s/id gt '%s'", objectRelationship2.getName(),
					objectRelationship1.getName(),
					objectEntry1.getObjectEntryId() - 1)),
			getObjectDefinition3());
		assertFilterString(
			OBJECT_FIELD_NAME_3, OBJECT_FIELD_VALUE_3,
			escape(
				String.format(
					"%s/%s/id le '%s'", objectRelationship2.getName(),
					objectRelationship1.getName(),
					objectEntry1.getObjectEntryId())),
			getObjectDefinition3());
		assertFilterString(
			OBJECT_FIELD_NAME_3, OBJECT_FIELD_VALUE_3,
			escape(
				String.format(
					"%s/%s/id lt '%s'", objectRelationship2.getName(),
					objectRelationship1.getName(),
					objectEntry1.getObjectEntryId() + 1)),
			getObjectDefinition3());
		assertFilterString(
			OBJECT_FIELD_NAME_3, OBJECT_FIELD_VALUE_3,
			escape(
				String.format(
					"%s/%s/id ne '%s'", objectRelationship2.getName(),
					objectRelationship1.getName(),
					objectEntry1.getObjectEntryId() - 1)),
			getObjectDefinition3());

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

		objectRelationship3 = addObjectRelationshipAndRelateObjectEntries(
			getObjectDefinition3(), getObjectDefinition4(),
			objectEntry3.getPrimaryKey(), objectEntry4.getPrimaryKey(),
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);

		objectRelationship4 = addObjectRelationshipAndRelateObjectEntries(
			getObjectDefinition2(), getObjectDefinition3(),
			objectEntry2.getPrimaryKey(), objectEntry3.getPrimaryKey(),
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);

		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s/%s/%s eq '%s'", objectRelationship1.getName(),
					objectRelationship2.getName(),
					objectRelationship3.getName(), OBJECT_FIELD_NAME_TEXT,
					OBJECT_FIELD_VALUE_4)),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s/%s/%s eq '%s'", objectRelationship1.getName(),
					objectRelationship4.getName(),
					objectRelationship3.getName(), OBJECT_FIELD_NAME_TEXT,
					OBJECT_FIELD_VALUE_4)),
			getObjectDefinition1());
	}

	@Test
	public void testFilterByGroupingOperatorsObjectEntriesByRelatedObjectEntriesFields()
		throws Exception {

		// Many to many relationship, custom object field

		objectEntry1 = ObjectEntryTestUtil.addObjectEntry(
			getObjectDefinition1(), OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1);
		objectEntry2 = ObjectEntryTestUtil.addObjectEntry(
			getObjectDefinition2(), OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2);

		objectRelationship1 = addObjectRelationshipAndRelateObjectEntries(
			ObjectRelationshipConstants.TYPE_MANY_TO_MANY);

		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"(%s/%s le '%s') and (%s/%s gt '%s')",
					objectRelationship1.getName(), OBJECT_FIELD_NAME_2,
					OBJECT_FIELD_VALUE_2, objectRelationship1.getName(),
					OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2 - 1)),
			getObjectDefinition1());

		// Many to many relationship, system object field

		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"(%s/id le '%s') and (%s/id gt '%s')",
					objectRelationship1.getName(),
					objectEntry2.getObjectEntryId(),
					objectRelationship1.getName(),
					objectEntry2.getObjectEntryId() - 1)),
			getObjectDefinition1());

		// Many to many relationship (other side), custom object field

		assertFilterString(
			OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2,
			escape(
				String.format(
					"(%s/%s le '%s') and (%s/%s gt '%s')",
					objectRelationship1.getName(), OBJECT_FIELD_NAME_1,
					OBJECT_FIELD_VALUE_1, objectRelationship1.getName(),
					OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1 - 1)),
			getObjectDefinition2());

		// Many to many relationship (other side), system object field

		assertFilterString(
			OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2,
			escape(
				String.format(
					"(%s/id le '%s') and (%s/id gt '%s')",
					objectRelationship1.getName(),
					objectEntry1.getObjectEntryId(),
					objectRelationship1.getName(),
					objectEntry1.getObjectEntryId() - 1)),
			getObjectDefinition2());

		objectRelationshipLocalService.deleteObjectRelationship(
			objectRelationship1);

		// Many to many self relationship, custom object field

		objectRelationship1 = addObjectRelationshipAndRelateObjectEntries(
			getObjectDefinition1(), getObjectDefinition1(),
			objectEntry1.getPrimaryKey(), objectEntry1.getPrimaryKey(),
			ObjectRelationshipConstants.TYPE_MANY_TO_MANY);

		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"(%s/%s le '%s') and (%s/%s gt '%s')",
					objectRelationship1.getName(), OBJECT_FIELD_NAME_1,
					OBJECT_FIELD_VALUE_1, objectRelationship1.getName(),
					OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1 - 1)),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"(%s/%s/%s le '%s') and (%s/%s/%s gt '%s')",
					objectRelationship1.getName(),
					objectRelationship1.getName(), OBJECT_FIELD_NAME_1,
					OBJECT_FIELD_VALUE_1, objectRelationship1.getName(),
					objectRelationship1.getName(), OBJECT_FIELD_NAME_1,
					OBJECT_FIELD_VALUE_1 - 1)),
			getObjectDefinition1());

		// One to many relationship, custom object field

		objectRelationship1 = addObjectRelationshipAndRelateObjectEntries(
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);

		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"(%s/%s le '%s') and (%s/%s gt '%s')",
					objectRelationship1.getName(), OBJECT_FIELD_NAME_2,
					OBJECT_FIELD_VALUE_2, objectRelationship1.getName(),
					OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2 - 1)),
			getObjectDefinition1());

		// One to many relationship, system object field

		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"(%s/id le '%s') and (%s/id gt '%s')",
					objectRelationship1.getName(),
					objectEntry2.getObjectEntryId(),
					objectRelationship1.getName(),
					objectEntry2.getObjectEntryId() - 1)),
			getObjectDefinition1());

		// One to many relationship (other side), custom object field

		assertFilterString(
			OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2,
			escape(
				String.format(
					"(%s/%s le '%s') and (%s/%s gt '%s')",
					objectRelationship1.getName(), OBJECT_FIELD_NAME_1,
					OBJECT_FIELD_VALUE_1, objectRelationship1.getName(),
					OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1 - 1)),
			getObjectDefinition2());

		// One to many relationship (other side), system object field

		assertFilterString(
			OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2,
			escape(
				String.format(
					"(%s/id le '%s') and (%s/id gt '%s')",
					objectRelationship1.getName(),
					objectEntry1.getObjectEntryId(),
					objectRelationship1.getName(),
					objectEntry1.getObjectEntryId() - 1)),
			getObjectDefinition2());

		objectRelationshipLocalService.deleteObjectRelationship(
			objectRelationship1);

		// One to many self relationship, custom object field

		objectRelationship1 = addObjectRelationshipAndRelateObjectEntries(
			getObjectDefinition1(), getObjectDefinition1(),
			objectEntry1.getPrimaryKey(), objectEntry1.getPrimaryKey(),
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);

		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"(%s/%s le '%s') and (%s/%s gt '%s')",
					objectRelationship1.getName(), OBJECT_FIELD_NAME_1,
					OBJECT_FIELD_VALUE_1, objectRelationship1.getName(),
					OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1 - 1)),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"(%s/%s/%s le '%s') and (%s/%s/%s gt '%s')",
					objectRelationship1.getName(),
					objectRelationship1.getName(), OBJECT_FIELD_NAME_1,
					OBJECT_FIELD_VALUE_1, objectRelationship1.getName(),
					objectRelationship1.getName(), OBJECT_FIELD_NAME_1,
					OBJECT_FIELD_VALUE_1 - 1)),
			getObjectDefinition1());
	}

	@Test
	public void testFilterByGroupingOperatorsObjectEntriesByRelatedObjectEntriesFieldsThroughMultipleRelationships()
		throws Exception {

		// Many to many relationship, custom object field

		objectEntry1 = ObjectEntryTestUtil.addObjectEntry(
			getObjectDefinition1(), OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1);
		objectEntry2 = ObjectEntryTestUtil.addObjectEntry(
			getObjectDefinition2(), OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2);
		objectEntry3 = ObjectEntryTestUtil.addObjectEntry(
			getObjectDefinition3(), OBJECT_FIELD_NAME_3, OBJECT_FIELD_VALUE_3);

		objectRelationship1 = addObjectRelationshipAndRelateObjectEntries(
			getObjectDefinition1(), getObjectDefinition2(),
			objectEntry1.getPrimaryKey(), objectEntry2.getPrimaryKey(),
			ObjectRelationshipConstants.TYPE_MANY_TO_MANY);
		objectRelationship2 = addObjectRelationshipAndRelateObjectEntries(
			getObjectDefinition2(), getObjectDefinition3(),
			objectEntry2.getPrimaryKey(), objectEntry3.getPrimaryKey(),
			ObjectRelationshipConstants.TYPE_MANY_TO_MANY);

		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"(%s/%s/%s le '%s') and (%s/%s/%s gt '%s')",
					objectRelationship1.getName(),
					objectRelationship2.getName(), OBJECT_FIELD_NAME_3,
					OBJECT_FIELD_VALUE_3, objectRelationship1.getName(),
					objectRelationship2.getName(), OBJECT_FIELD_NAME_3,
					OBJECT_FIELD_VALUE_3 - 1)),
			getObjectDefinition1());

		// Many to many relationship, system object field

		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"(%s/%s/id le '%s') and (%s/%s/id gt '%s')",
					objectRelationship1.getName(),
					objectRelationship2.getName(),
					objectEntry3.getObjectEntryId(),
					objectRelationship1.getName(),
					objectRelationship2.getName(),
					objectEntry3.getObjectEntryId() - 1)),
			getObjectDefinition1());

		// Many to many relationship (other side), custom object field

		assertFilterString(
			OBJECT_FIELD_NAME_3, OBJECT_FIELD_VALUE_3,
			escape(
				String.format(
					"(%s/%s/%s le '%s') and (%s/%s/%s gt '%s')",
					objectRelationship2.getName(),
					objectRelationship1.getName(), OBJECT_FIELD_NAME_1,
					OBJECT_FIELD_VALUE_1, objectRelationship2.getName(),
					objectRelationship1.getName(), OBJECT_FIELD_NAME_1,
					OBJECT_FIELD_VALUE_1 - 1)),
			getObjectDefinition3());

		// Many to many relationship (other side), system object field

		assertFilterString(
			OBJECT_FIELD_NAME_3, OBJECT_FIELD_VALUE_3,
			escape(
				String.format(
					"(%s/%s/id le '%s') and (%s/%s/id gt '%s')",
					objectRelationship2.getName(),
					objectRelationship1.getName(),
					objectEntry1.getObjectEntryId(),
					objectRelationship2.getName(),
					objectRelationship1.getName(),
					objectEntry1.getObjectEntryId() - 1)),
			getObjectDefinition3());

		objectRelationshipLocalService.deleteObjectRelationship(
			objectRelationship1);
		objectRelationshipLocalService.deleteObjectRelationship(
			objectRelationship2);

		// One to many relationship, custom object field

		objectRelationship1 = addObjectRelationshipAndRelateObjectEntries(
			getObjectDefinition1(), getObjectDefinition2(),
			objectEntry1.getPrimaryKey(), objectEntry2.getPrimaryKey(),
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);

		objectRelationship2 = addObjectRelationshipAndRelateObjectEntries(
			getObjectDefinition2(), getObjectDefinition3(),
			objectEntry2.getPrimaryKey(), objectEntry3.getPrimaryKey(),
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);

		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"(%s/%s/%s le '%s') and (%s/%s/%s gt '%s')",
					objectRelationship1.getName(),
					objectRelationship2.getName(), OBJECT_FIELD_NAME_3,
					OBJECT_FIELD_VALUE_3, objectRelationship1.getName(),
					objectRelationship2.getName(), OBJECT_FIELD_NAME_3,
					OBJECT_FIELD_VALUE_3 - 1)),
			getObjectDefinition1());

		// One to many relationship, system object field

		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"(%s/%s/id le '%s') and (%s/%s/id gt '%s')",
					objectRelationship1.getName(),
					objectRelationship2.getName(),
					objectEntry3.getObjectEntryId(),
					objectRelationship1.getName(),
					objectRelationship2.getName(),
					objectEntry3.getObjectEntryId() - 1)),
			getObjectDefinition1());

		// One to many relationship (other side), custom object field

		assertFilterString(
			OBJECT_FIELD_NAME_3, OBJECT_FIELD_VALUE_3,
			escape(
				String.format(
					"(%s/%s/%s le '%s') and (%s/%s/%s gt '%s')",
					objectRelationship2.getName(),
					objectRelationship1.getName(), OBJECT_FIELD_NAME_1,
					OBJECT_FIELD_VALUE_1, objectRelationship2.getName(),
					objectRelationship1.getName(), OBJECT_FIELD_NAME_1,
					OBJECT_FIELD_VALUE_1 - 1)),
			getObjectDefinition3());

		// One to many relationship (other side), system object field

		assertFilterString(
			OBJECT_FIELD_NAME_3, OBJECT_FIELD_VALUE_3,
			escape(
				String.format(
					"(%s/%s/id le '%s') and (%s/%s/id gt '%s')",
					objectRelationship2.getName(),
					objectRelationship1.getName(),
					objectEntry1.getObjectEntryId(),
					objectRelationship2.getName(),
					objectRelationship1.getName(),
					objectEntry1.getObjectEntryId() - 1)),
			getObjectDefinition3());
	}

	@Test
	public void testFilterByLambdaOperatorsObjectEntriesByRelatedObjectEntriesFields()
		throws Exception {

		objectEntry1 = ObjectEntryTestUtil.addObjectEntry(
			getObjectDefinition1(),
			HashMapBuilder.<String, Serializable>put(
				OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1
			).put(
				OBJECT_FIELD_NAME_MULTISELECT_PICKLIST,
				(Serializable)Arrays.asList(
					LIST_TYPE_ENTRY_KEY_1, LIST_TYPE_ENTRY_KEY_2)
			).build(),
			TAG_1);
		objectEntry2 = ObjectEntryTestUtil.addObjectEntry(
			getObjectDefinition2(),
			HashMapBuilder.<String, Serializable>put(
				OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2
			).put(
				OBJECT_FIELD_NAME_MULTISELECT_PICKLIST,
				(Serializable)Arrays.asList(
					LIST_TYPE_ENTRY_KEY_1, LIST_TYPE_ENTRY_KEY_2)
			).build(),
			_TAG_2);

		// Many to many relationship, custom object field

		objectRelationship1 = addObjectRelationshipAndRelateObjectEntries(
			getObjectDefinition1(), getObjectDefinition2(),
			objectEntry1.getPrimaryKey(), objectEntry2.getPrimaryKey(),
			ObjectRelationshipConstants.TYPE_MANY_TO_MANY);

		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s/any(k:contains(k,'%s'))",
					objectRelationship1.getName(),
					OBJECT_FIELD_NAME_MULTISELECT_PICKLIST,
					LIST_TYPE_ENTRY_KEY_1)),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s/any(k:k eq '%s')", objectRelationship1.getName(),
					OBJECT_FIELD_NAME_MULTISELECT_PICKLIST,
					LIST_TYPE_ENTRY_KEY_1)),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s/any(k:k in ('%s', '%s'))",
					objectRelationship1.getName(),
					OBJECT_FIELD_NAME_MULTISELECT_PICKLIST,
					LIST_TYPE_ENTRY_KEY_1, RandomTestUtil.randomString())),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s/any(k:k ne '%s')", objectRelationship1.getName(),
					OBJECT_FIELD_NAME_MULTISELECT_PICKLIST,
					LIST_TYPE_ENTRY_KEY_1)),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s/any(k:startswith(k,'%s'))",
					objectRelationship1.getName(),
					OBJECT_FIELD_NAME_MULTISELECT_PICKLIST,
					LIST_TYPE_ENTRY_KEY_1)),
			getObjectDefinition1());

		// Many to many relationship, system object field

		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/keywords/any(k:contains(k,'%s'))",
					objectRelationship1.getName(), _TAG_2.substring(1))),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/keywords/any(k:k eq '%s')",
					objectRelationship1.getName(), _TAG_2)),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/keywords/any(k:k in ('%s', '%s'))",
					objectRelationship1.getName(), _TAG_2,
					RandomTestUtil.randomString())),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/keywords/any(k:k ne '%s')",
					objectRelationship1.getName(), TAG_1)),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/keywords/any(k:startswith(k,'%s'))",
					objectRelationship1.getName(), _TAG_2.substring(0, 2))),
			getObjectDefinition1());

		// Many to many relationship (other side), custom object field

		assertFilterString(
			OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2,
			escape(
				String.format(
					"%s/%s/any(k:contains(k,'%s'))",
					objectRelationship1.getName(),
					OBJECT_FIELD_NAME_MULTISELECT_PICKLIST,
					LIST_TYPE_ENTRY_KEY_1)),
			getObjectDefinition2());
		assertFilterString(
			OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2,
			escape(
				String.format(
					"%s/%s/any(k:k eq '%s')", objectRelationship1.getName(),
					OBJECT_FIELD_NAME_MULTISELECT_PICKLIST,
					LIST_TYPE_ENTRY_KEY_1)),
			getObjectDefinition2());
		assertFilterString(
			OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2,
			escape(
				String.format(
					"%s/%s/any(k:k in ('%s', '%s'))",
					objectRelationship1.getName(),
					OBJECT_FIELD_NAME_MULTISELECT_PICKLIST,
					LIST_TYPE_ENTRY_KEY_1, RandomTestUtil.randomString())),
			getObjectDefinition2());
		assertFilterString(
			OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2,
			escape(
				String.format(
					"%s/%s/any(k:k ne '%s')", objectRelationship1.getName(),
					OBJECT_FIELD_NAME_MULTISELECT_PICKLIST,
					LIST_TYPE_ENTRY_KEY_1)),
			getObjectDefinition2());
		assertFilterString(
			OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2,
			escape(
				String.format(
					"%s/%s/any(k:startswith(k,'%s'))",
					objectRelationship1.getName(),
					OBJECT_FIELD_NAME_MULTISELECT_PICKLIST,
					LIST_TYPE_ENTRY_KEY_1)),
			getObjectDefinition2());

		// Many to many relationship (other side), system object field

		assertFilterString(
			OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2,
			escape(
				String.format(
					"%s/keywords/any(k:contains(k,'%s'))",
					objectRelationship1.getName(), TAG_1.substring(1))),
			getObjectDefinition2());
		assertFilterString(
			OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2,
			escape(
				String.format(
					"%s/keywords/any(k:k eq '%s')",
					objectRelationship1.getName(), TAG_1)),
			getObjectDefinition2());
		assertFilterString(
			OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2,
			escape(
				String.format(
					"%s/keywords/any(k:k in ('%s', '%s'))",
					objectRelationship1.getName(), TAG_1,
					RandomTestUtil.randomString())),
			getObjectDefinition2());
		assertFilterString(
			OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2,
			escape(
				String.format(
					"%s/keywords/any(k:k ne '%s')",
					objectRelationship1.getName(), _TAG_2)),
			getObjectDefinition2());
		assertFilterString(
			OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2,
			escape(
				String.format(
					"%s/keywords/any(k:startswith(k,'%s'))",
					objectRelationship1.getName(), TAG_1.substring(0, 2))),
			getObjectDefinition2());

		objectRelationshipLocalService.deleteObjectRelationship(
			objectRelationship1);

		// Many to many self relationship, custom object field

		objectRelationship1 = addObjectRelationshipAndRelateObjectEntries(
			getObjectDefinition1(), getObjectDefinition1(),
			objectEntry1.getPrimaryKey(), objectEntry1.getPrimaryKey(),
			ObjectRelationshipConstants.TYPE_MANY_TO_MANY);

		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s/any(k:contains(k,'%s'))",
					objectRelationship1.getName(),
					OBJECT_FIELD_NAME_MULTISELECT_PICKLIST,
					LIST_TYPE_ENTRY_KEY_1)),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s/%s/any(k:contains(k,'%s'))",
					objectRelationship1.getName(),
					objectRelationship1.getName(),
					OBJECT_FIELD_NAME_MULTISELECT_PICKLIST,
					LIST_TYPE_ENTRY_KEY_1)),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s/any(k:k eq '%s')", objectRelationship1.getName(),
					OBJECT_FIELD_NAME_MULTISELECT_PICKLIST,
					LIST_TYPE_ENTRY_KEY_1)),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s/%s/any(k:k eq '%s')", objectRelationship1.getName(),
					objectRelationship1.getName(),
					OBJECT_FIELD_NAME_MULTISELECT_PICKLIST,
					LIST_TYPE_ENTRY_KEY_1)),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s/any(k:k in ('%s', '%s'))",
					objectRelationship1.getName(),
					OBJECT_FIELD_NAME_MULTISELECT_PICKLIST,
					LIST_TYPE_ENTRY_KEY_1, RandomTestUtil.randomString())),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s/%s/any(k:k in ('%s', '%s'))",
					objectRelationship1.getName(),
					objectRelationship1.getName(),
					OBJECT_FIELD_NAME_MULTISELECT_PICKLIST,
					LIST_TYPE_ENTRY_KEY_1, RandomTestUtil.randomString())),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s/any(k:k ne '%s')", objectRelationship1.getName(),
					OBJECT_FIELD_NAME_MULTISELECT_PICKLIST,
					LIST_TYPE_ENTRY_KEY_1)),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s/%s/any(k:k ne '%s')", objectRelationship1.getName(),
					objectRelationship1.getName(),
					OBJECT_FIELD_NAME_MULTISELECT_PICKLIST,
					LIST_TYPE_ENTRY_KEY_1)),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s/any(k:startswith(k,'%s'))",
					objectRelationship1.getName(),
					OBJECT_FIELD_NAME_MULTISELECT_PICKLIST,
					LIST_TYPE_ENTRY_KEY_1)),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s/%s/any(k:startswith(k,'%s'))",
					objectRelationship1.getName(),
					objectRelationship1.getName(),
					OBJECT_FIELD_NAME_MULTISELECT_PICKLIST,
					LIST_TYPE_ENTRY_KEY_1)),
			getObjectDefinition1());

		objectRelationshipLocalService.deleteObjectRelationship(
			objectRelationship1);

		// One to many relationship, custom object field

		objectRelationship1 = addObjectRelationshipAndRelateObjectEntries(
			getObjectDefinition1(), getObjectDefinition2(),
			objectEntry1.getPrimaryKey(), objectEntry2.getPrimaryKey(),
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);

		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s/any(k:contains(k,'%s'))",
					objectRelationship1.getName(),
					OBJECT_FIELD_NAME_MULTISELECT_PICKLIST,
					LIST_TYPE_ENTRY_KEY_1)),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s/any(k:k eq '%s')", objectRelationship1.getName(),
					OBJECT_FIELD_NAME_MULTISELECT_PICKLIST,
					LIST_TYPE_ENTRY_KEY_1)),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s/any(k:k in ('%s', '%s'))",
					objectRelationship1.getName(),
					OBJECT_FIELD_NAME_MULTISELECT_PICKLIST,
					LIST_TYPE_ENTRY_KEY_1, RandomTestUtil.randomString())),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s/any(k:k ne '%s')", objectRelationship1.getName(),
					OBJECT_FIELD_NAME_MULTISELECT_PICKLIST,
					LIST_TYPE_ENTRY_KEY_1)),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s/any(k:startswith(k,'%s'))",
					objectRelationship1.getName(),
					OBJECT_FIELD_NAME_MULTISELECT_PICKLIST,
					LIST_TYPE_ENTRY_KEY_1)),
			getObjectDefinition1());

		// One to many relationship, system object field

		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/keywords/any(k:contains(k,'%s'))",
					objectRelationship1.getName(), _TAG_2.substring(1))),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/keywords/any(k:k eq '%s')",
					objectRelationship1.getName(), _TAG_2)),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/keywords/any(k:k in ('%s', '%s'))",
					objectRelationship1.getName(), _TAG_2,
					RandomTestUtil.randomString())),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/keywords/any(k:k ne '%s')",
					objectRelationship1.getName(), TAG_1)),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/keywords/any(k:startswith(k,'%s'))",
					objectRelationship1.getName(), _TAG_2.substring(0, 2))),
			getObjectDefinition1());

		// One to many relationship (other side), custom object field

		assertFilterString(
			OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2,
			escape(
				String.format(
					"%s/%s/any(k:contains(k,'%s'))",
					objectRelationship1.getName(),
					OBJECT_FIELD_NAME_MULTISELECT_PICKLIST,
					LIST_TYPE_ENTRY_KEY_1)),
			getObjectDefinition2());
		assertFilterString(
			OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2,
			escape(
				String.format(
					"%s/%s/any(k:k eq '%s')", objectRelationship1.getName(),
					OBJECT_FIELD_NAME_MULTISELECT_PICKLIST,
					LIST_TYPE_ENTRY_KEY_1)),
			getObjectDefinition2());
		assertFilterString(
			OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2,
			escape(
				String.format(
					"%s/%s/any(k:k in ('%s', '%s'))",
					objectRelationship1.getName(),
					OBJECT_FIELD_NAME_MULTISELECT_PICKLIST,
					LIST_TYPE_ENTRY_KEY_1, RandomTestUtil.randomString())),
			getObjectDefinition2());
		assertFilterString(
			OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2,
			escape(
				String.format(
					"%s/%s/any(k:k ne '%s')", objectRelationship1.getName(),
					OBJECT_FIELD_NAME_MULTISELECT_PICKLIST,
					LIST_TYPE_ENTRY_KEY_1)),
			getObjectDefinition2());
		assertFilterString(
			OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2,
			escape(
				String.format(
					"%s/%s/any(k:startswith(k,'%s'))",
					objectRelationship1.getName(),
					OBJECT_FIELD_NAME_MULTISELECT_PICKLIST,
					LIST_TYPE_ENTRY_KEY_1)),
			getObjectDefinition2());

		// One to many relationship (other side), system object field

		assertFilterString(
			OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2,
			escape(
				String.format(
					"%s/keywords/any(k:contains(k,'%s'))",
					objectRelationship1.getName(), TAG_1.substring(1))),
			getObjectDefinition2());
		assertFilterString(
			OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2,
			escape(
				String.format(
					"%s/keywords/any(k:k eq '%s')",
					objectRelationship1.getName(), TAG_1)),
			getObjectDefinition2());
		assertFilterString(
			OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2,
			escape(
				String.format(
					"%s/keywords/any(k:k in ('%s', '%s'))",
					objectRelationship1.getName(), TAG_1,
					RandomTestUtil.randomString())),
			getObjectDefinition2());
		assertFilterString(
			OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2,
			escape(
				String.format(
					"%s/keywords/any(k:k ne '%s')",
					objectRelationship1.getName(), _TAG_2)),
			getObjectDefinition2());
		assertFilterString(
			OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2,
			escape(
				String.format(
					"%s/keywords/any(k:startswith(k,'%s'))",
					objectRelationship1.getName(), TAG_1.substring(0, 2))),
			getObjectDefinition2());

		objectRelationshipLocalService.deleteObjectRelationship(
			objectRelationship1);

		// One to many self relationship, custom object field

		objectRelationship1 = addObjectRelationshipAndRelateObjectEntries(
			getObjectDefinition1(), getObjectDefinition1(),
			objectEntry1.getPrimaryKey(), objectEntry1.getPrimaryKey(),
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);

		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s/any(k:contains(k,'%s'))",
					objectRelationship1.getName(),
					OBJECT_FIELD_NAME_MULTISELECT_PICKLIST,
					LIST_TYPE_ENTRY_KEY_1)),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s/%s/any(k:contains(k,'%s'))",
					objectRelationship1.getName(),
					objectRelationship1.getName(),
					OBJECT_FIELD_NAME_MULTISELECT_PICKLIST,
					LIST_TYPE_ENTRY_KEY_1)),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s/any(k:k eq '%s')", objectRelationship1.getName(),
					OBJECT_FIELD_NAME_MULTISELECT_PICKLIST,
					LIST_TYPE_ENTRY_KEY_1)),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s/%s/any(k:k eq '%s')", objectRelationship1.getName(),
					objectRelationship1.getName(),
					OBJECT_FIELD_NAME_MULTISELECT_PICKLIST,
					LIST_TYPE_ENTRY_KEY_1)),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s/any(k:k in ('%s', '%s'))",
					objectRelationship1.getName(),
					OBJECT_FIELD_NAME_MULTISELECT_PICKLIST,
					LIST_TYPE_ENTRY_KEY_1, RandomTestUtil.randomString())),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s/%s/any(k:k in ('%s', '%s'))",
					objectRelationship1.getName(),
					objectRelationship1.getName(),
					OBJECT_FIELD_NAME_MULTISELECT_PICKLIST,
					LIST_TYPE_ENTRY_KEY_1, RandomTestUtil.randomString())),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s/any(k:k ne '%s')", objectRelationship1.getName(),
					OBJECT_FIELD_NAME_MULTISELECT_PICKLIST,
					LIST_TYPE_ENTRY_KEY_1)),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s/%s/any(k:k ne '%s')", objectRelationship1.getName(),
					objectRelationship1.getName(),
					OBJECT_FIELD_NAME_MULTISELECT_PICKLIST,
					LIST_TYPE_ENTRY_KEY_1)),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s/any(k:startswith(k,'%s'))",
					objectRelationship1.getName(),
					OBJECT_FIELD_NAME_MULTISELECT_PICKLIST,
					LIST_TYPE_ENTRY_KEY_1)),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s/%s/any(k:startswith(k,'%s'))",
					objectRelationship1.getName(),
					objectRelationship1.getName(),
					OBJECT_FIELD_NAME_MULTISELECT_PICKLIST,
					LIST_TYPE_ENTRY_KEY_1)),
			getObjectDefinition1());
	}

	@Test
	public void testFilterByLambdaOperatorsObjectEntriesByRelatedObjectEntriesFieldsThroughMultipleRelationships()
		throws Exception {

		objectEntry1 = ObjectEntryTestUtil.addObjectEntry(
			getObjectDefinition1(),
			HashMapBuilder.<String, Serializable>put(
				OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1
			).put(
				OBJECT_FIELD_NAME_MULTISELECT_PICKLIST, LIST_TYPE_ENTRY_KEY_1
			).build(),
			TAG_1);
		objectEntry2 = ObjectEntryTestUtil.addObjectEntry(
			getObjectDefinition2(),
			HashMapBuilder.<String, Serializable>put(
				OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2
			).put(
				OBJECT_FIELD_NAME_MULTISELECT_PICKLIST, LIST_TYPE_ENTRY_KEY_2
			).build(),
			_TAG_2);
		objectEntry3 = ObjectEntryTestUtil.addObjectEntry(
			getObjectDefinition3(),
			HashMapBuilder.<String, Serializable>put(
				OBJECT_FIELD_NAME_3, OBJECT_FIELD_VALUE_3
			).put(
				OBJECT_FIELD_NAME_MULTISELECT_PICKLIST, LIST_TYPE_ENTRY_KEY_3
			).build(),
			_TAG_3);

		// Many to many relationship, custom object field

		objectRelationship1 = addObjectRelationshipAndRelateObjectEntries(
			getObjectDefinition1(), getObjectDefinition2(),
			objectEntry1.getPrimaryKey(), objectEntry2.getPrimaryKey(),
			ObjectRelationshipConstants.TYPE_MANY_TO_MANY);
		objectRelationship2 = addObjectRelationshipAndRelateObjectEntries(
			getObjectDefinition2(), getObjectDefinition3(),
			objectEntry2.getPrimaryKey(), objectEntry3.getPrimaryKey(),
			ObjectRelationshipConstants.TYPE_MANY_TO_MANY);

		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s/%s/any(k:contains(k,'%s'))",
					objectRelationship1.getName(),
					objectRelationship2.getName(),
					OBJECT_FIELD_NAME_MULTISELECT_PICKLIST,
					LIST_TYPE_ENTRY_KEY_3)),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s/%s/any(k:k eq '%s')", objectRelationship1.getName(),
					objectRelationship2.getName(),
					OBJECT_FIELD_NAME_MULTISELECT_PICKLIST,
					LIST_TYPE_ENTRY_KEY_3)),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s/%s/any(k:k in ('%s', '%s'))",
					objectRelationship1.getName(),
					objectRelationship2.getName(),
					OBJECT_FIELD_NAME_MULTISELECT_PICKLIST,
					LIST_TYPE_ENTRY_KEY_3, RandomTestUtil.randomString())),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s/%s/any(k:k ne '%s')", objectRelationship1.getName(),
					objectRelationship2.getName(),
					OBJECT_FIELD_NAME_MULTISELECT_PICKLIST,
					LIST_TYPE_ENTRY_KEY_2)),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s/%s/any(k:startswith(k,'%s'))",
					objectRelationship1.getName(),
					objectRelationship2.getName(),
					OBJECT_FIELD_NAME_MULTISELECT_PICKLIST,
					LIST_TYPE_ENTRY_KEY_3)),
			getObjectDefinition1());

		// Many to many relationship, system object field

		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s/keywords/any(k:contains(k,'%s'))",
					objectRelationship1.getName(),
					objectRelationship2.getName(), _TAG_3.substring(1))),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s/keywords/any(k:k eq '%s')",
					objectRelationship1.getName(),
					objectRelationship2.getName(), _TAG_3)),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s/keywords/any(k:k in ('%s', '%s'))",
					objectRelationship1.getName(),
					objectRelationship2.getName(), _TAG_3,
					RandomTestUtil.randomString())),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s/keywords/any(k:k ne '%s')",
					objectRelationship1.getName(),
					objectRelationship2.getName(), TAG_1)),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s/keywords/any(k:startswith(k,'%s'))",
					objectRelationship1.getName(),
					objectRelationship2.getName(), _TAG_3.substring(0, 2))),
			getObjectDefinition1());

		// Many to many relationship (other side), custom object field

		assertFilterString(
			OBJECT_FIELD_NAME_3, OBJECT_FIELD_VALUE_3,
			escape(
				String.format(
					"%s/%s/%s/any(k:contains(k,'%s'))",
					objectRelationship2.getName(),
					objectRelationship1.getName(),
					OBJECT_FIELD_NAME_MULTISELECT_PICKLIST,
					LIST_TYPE_ENTRY_KEY_1)),
			getObjectDefinition3());
		assertFilterString(
			OBJECT_FIELD_NAME_3, OBJECT_FIELD_VALUE_3,
			escape(
				String.format(
					"%s/%s/%s/any(k:k eq '%s')", objectRelationship2.getName(),
					objectRelationship1.getName(),
					OBJECT_FIELD_NAME_MULTISELECT_PICKLIST,
					LIST_TYPE_ENTRY_KEY_1)),
			getObjectDefinition3());
		assertFilterString(
			OBJECT_FIELD_NAME_3, OBJECT_FIELD_VALUE_3,
			escape(
				String.format(
					"%s/%s/%s/any(k:k in ('%s', '%s'))",
					objectRelationship2.getName(),
					objectRelationship1.getName(),
					OBJECT_FIELD_NAME_MULTISELECT_PICKLIST,
					LIST_TYPE_ENTRY_KEY_1, RandomTestUtil.randomString())),
			getObjectDefinition3());
		assertFilterString(
			OBJECT_FIELD_NAME_3, OBJECT_FIELD_VALUE_3,
			escape(
				String.format(
					"%s/%s/%s/any(k:k ne '%s')", objectRelationship2.getName(),
					objectRelationship1.getName(),
					OBJECT_FIELD_NAME_MULTISELECT_PICKLIST,
					LIST_TYPE_ENTRY_KEY_3)),
			getObjectDefinition3());
		assertFilterString(
			OBJECT_FIELD_NAME_3, OBJECT_FIELD_VALUE_3,
			escape(
				String.format(
					"%s/%s/%s/any(k:startswith(k,'%s'))",
					objectRelationship2.getName(),
					objectRelationship1.getName(),
					OBJECT_FIELD_NAME_MULTISELECT_PICKLIST,
					LIST_TYPE_ENTRY_KEY_1)),
			getObjectDefinition3());

		// Many to many relationship (other side), system object field

		assertFilterString(
			OBJECT_FIELD_NAME_3, OBJECT_FIELD_VALUE_3,
			escape(
				String.format(
					"%s/%s/keywords/any(k:contains(k,'%s'))",
					objectRelationship2.getName(),
					objectRelationship1.getName(), TAG_1.substring(1))),
			getObjectDefinition3());
		assertFilterString(
			OBJECT_FIELD_NAME_3, OBJECT_FIELD_VALUE_3,
			escape(
				String.format(
					"%s/%s/keywords/any(k:k eq '%s')",
					objectRelationship2.getName(),
					objectRelationship1.getName(), TAG_1)),
			getObjectDefinition3());
		assertFilterString(
			OBJECT_FIELD_NAME_3, OBJECT_FIELD_VALUE_3,
			escape(
				String.format(
					"%s/%s/keywords/any(k:k in ('%s', '%s'))",
					objectRelationship2.getName(),
					objectRelationship1.getName(), TAG_1,
					RandomTestUtil.randomString())),
			getObjectDefinition3());
		assertFilterString(
			OBJECT_FIELD_NAME_3, OBJECT_FIELD_VALUE_3,
			escape(
				String.format(
					"%s/%s/keywords/any(k:k ne '%s')",
					objectRelationship2.getName(),
					objectRelationship1.getName(), _TAG_3)),
			getObjectDefinition3());
		assertFilterString(
			OBJECT_FIELD_NAME_3, OBJECT_FIELD_VALUE_3,
			escape(
				String.format(
					"%s/%s/keywords/any(k:startswith(k,'%s'))",
					objectRelationship2.getName(),
					objectRelationship1.getName(), TAG_1.substring(0, 2))),
			getObjectDefinition3());

		objectRelationshipLocalService.deleteObjectRelationship(
			objectRelationship1);
		objectRelationshipLocalService.deleteObjectRelationship(
			objectRelationship2);

		// One to many relationship, custom object field

		objectRelationship1 = addObjectRelationshipAndRelateObjectEntries(
			getObjectDefinition1(), getObjectDefinition2(),
			objectEntry1.getPrimaryKey(), objectEntry2.getPrimaryKey(),
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);
		objectRelationship2 = addObjectRelationshipAndRelateObjectEntries(
			getObjectDefinition2(), getObjectDefinition3(),
			objectEntry2.getPrimaryKey(), objectEntry3.getPrimaryKey(),
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);

		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s/%s/any(k:contains(k,'%s'))",
					objectRelationship1.getName(),
					objectRelationship2.getName(),
					OBJECT_FIELD_NAME_MULTISELECT_PICKLIST,
					LIST_TYPE_ENTRY_KEY_3)),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s/%s/any(k:k eq '%s')", objectRelationship1.getName(),
					objectRelationship2.getName(),
					OBJECT_FIELD_NAME_MULTISELECT_PICKLIST,
					LIST_TYPE_ENTRY_KEY_3)),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s/%s/any(k:k in ('%s', '%s'))",
					objectRelationship1.getName(),
					objectRelationship2.getName(),
					OBJECT_FIELD_NAME_MULTISELECT_PICKLIST,
					LIST_TYPE_ENTRY_KEY_3, RandomTestUtil.randomString())),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s/%s/any(k:k ne '%s')", objectRelationship1.getName(),
					objectRelationship2.getName(),
					OBJECT_FIELD_NAME_MULTISELECT_PICKLIST,
					LIST_TYPE_ENTRY_KEY_1)),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s/%s/any(k:startswith(k,'%s'))",
					objectRelationship1.getName(),
					objectRelationship2.getName(),
					OBJECT_FIELD_NAME_MULTISELECT_PICKLIST,
					LIST_TYPE_ENTRY_KEY_3)),
			getObjectDefinition1());

		// One to many relationship, system object field

		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s/keywords/any(k:contains(k,'%s'))",
					objectRelationship1.getName(),
					objectRelationship2.getName(), _TAG_3.substring(1))),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s/keywords/any(k:k eq '%s')",
					objectRelationship1.getName(),
					objectRelationship2.getName(), _TAG_3)),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s/keywords/any(k:k in ('%s', '%s'))",
					objectRelationship1.getName(),
					objectRelationship2.getName(), _TAG_3,
					RandomTestUtil.randomString())),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s/keywords/any(k:k ne '%s')",
					objectRelationship1.getName(),
					objectRelationship2.getName(), TAG_1)),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s/keywords/any(k:startswith(k,'%s'))",
					objectRelationship1.getName(),
					objectRelationship2.getName(), _TAG_3.substring(0, 2))),
			getObjectDefinition1());

		// One to many relationship (other side), custom object field

		assertFilterString(
			OBJECT_FIELD_NAME_3, OBJECT_FIELD_VALUE_3,
			escape(
				String.format(
					"%s/%s/%s/any(k:contains(k,'%s'))",
					objectRelationship2.getName(),
					objectRelationship1.getName(),
					OBJECT_FIELD_NAME_MULTISELECT_PICKLIST,
					LIST_TYPE_ENTRY_KEY_1)),
			getObjectDefinition3());
		assertFilterString(
			OBJECT_FIELD_NAME_3, OBJECT_FIELD_VALUE_3,
			escape(
				String.format(
					"%s/%s/%s/any(k:k eq '%s')", objectRelationship2.getName(),
					objectRelationship1.getName(),
					OBJECT_FIELD_NAME_MULTISELECT_PICKLIST,
					LIST_TYPE_ENTRY_KEY_1)),
			getObjectDefinition3());
		assertFilterString(
			OBJECT_FIELD_NAME_3, OBJECT_FIELD_VALUE_3,
			escape(
				String.format(
					"%s/%s/%s/any(k:k in ('%s', '%s'))",
					objectRelationship2.getName(),
					objectRelationship1.getName(),
					OBJECT_FIELD_NAME_MULTISELECT_PICKLIST,
					LIST_TYPE_ENTRY_KEY_1, RandomTestUtil.randomString())),
			getObjectDefinition3());
		assertFilterString(
			OBJECT_FIELD_NAME_3, OBJECT_FIELD_VALUE_3,
			escape(
				String.format(
					"%s/%s/%s/any(k:k ne '%s')", objectRelationship2.getName(),
					objectRelationship1.getName(),
					OBJECT_FIELD_NAME_MULTISELECT_PICKLIST,
					LIST_TYPE_ENTRY_KEY_3)),
			getObjectDefinition3());
		assertFilterString(
			OBJECT_FIELD_NAME_3, OBJECT_FIELD_VALUE_3,
			escape(
				String.format(
					"%s/%s/%s/any(k:startswith(k,'%s'))",
					objectRelationship2.getName(),
					objectRelationship1.getName(),
					OBJECT_FIELD_NAME_MULTISELECT_PICKLIST,
					LIST_TYPE_ENTRY_KEY_1)),
			getObjectDefinition3());

		// One to many relationship (other side), system object field

		assertFilterString(
			OBJECT_FIELD_NAME_3, OBJECT_FIELD_VALUE_3,
			escape(
				String.format(
					"%s/%s/keywords/any(k:contains(k,'%s'))",
					objectRelationship2.getName(),
					objectRelationship1.getName(), TAG_1.substring(1))),
			getObjectDefinition3());
		assertFilterString(
			OBJECT_FIELD_NAME_3, OBJECT_FIELD_VALUE_3,
			escape(
				String.format(
					"%s/%s/keywords/any(k:k eq '%s')",
					objectRelationship2.getName(),
					objectRelationship1.getName(), TAG_1)),
			getObjectDefinition3());
		assertFilterString(
			OBJECT_FIELD_NAME_3, OBJECT_FIELD_VALUE_3,
			escape(
				String.format(
					"%s/%s/keywords/any(k:k in ('%s', '%s'))",
					objectRelationship2.getName(),
					objectRelationship1.getName(), TAG_1,
					RandomTestUtil.randomString())),
			getObjectDefinition3());
		assertFilterString(
			OBJECT_FIELD_NAME_3, OBJECT_FIELD_VALUE_3,
			escape(
				String.format(
					"%s/%s/keywords/any(k:k ne '%s')",
					objectRelationship2.getName(),
					objectRelationship1.getName(), _TAG_3)),
			getObjectDefinition3());
		assertFilterString(
			OBJECT_FIELD_NAME_3, OBJECT_FIELD_VALUE_3,
			escape(
				String.format(
					"%s/%s/keywords/any(k:startswith(k,'%s'))",
					objectRelationship2.getName(),
					objectRelationship1.getName(), TAG_1.substring(0, 2))),
			getObjectDefinition3());
	}

	@Test
	public void testFilterByListOperatorsObjectEntriesByRelatedObjectEntriesFields()
		throws Exception {

		// Many to many relationship, custom object field

		objectEntry1 = ObjectEntryTestUtil.addObjectEntry(
			getObjectDefinition1(), OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1);
		objectEntry2 = ObjectEntryTestUtil.addObjectEntry(
			getObjectDefinition2(), OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2);

		objectRelationship1 = addObjectRelationshipAndRelateObjectEntries(
			ObjectRelationshipConstants.TYPE_MANY_TO_MANY);

		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s in ('%s', '%s')", objectRelationship1.getName(),
					OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2,
					RandomTestUtil.randomInt())),
			getObjectDefinition1());

		// Many to many relationship, system object field

		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/id in ('%s', '%s')", objectRelationship1.getName(),
					objectEntry2.getObjectEntryId(),
					RandomTestUtil.randomInt())),
			getObjectDefinition1());

		// Many to many relationship (other side), custom object field

		assertFilterString(
			OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2,
			escape(
				String.format(
					"%s/%s in ('%s', '%s')", objectRelationship1.getName(),
					OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
					RandomTestUtil.randomInt())),
			getObjectDefinition2());

		// Many to many relationship (other side), system object field

		assertFilterString(
			OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2,
			escape(
				String.format(
					"%s/id in ('%s', '%s')", objectRelationship1.getName(),
					objectEntry1.getObjectEntryId(),
					RandomTestUtil.randomInt())),
			getObjectDefinition2());

		objectRelationshipLocalService.deleteObjectRelationship(
			objectRelationship1);

		// Many to many self relationship, custom object field

		objectRelationship1 = addObjectRelationshipAndRelateObjectEntries(
			getObjectDefinition1(), getObjectDefinition1(),
			objectEntry1.getPrimaryKey(), objectEntry1.getPrimaryKey(),
			ObjectRelationshipConstants.TYPE_MANY_TO_MANY);

		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s in ('%s', '%s')", objectRelationship1.getName(),
					OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
					RandomTestUtil.randomInt())),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s/%s in ('%s', '%s')", objectRelationship1.getName(),
					objectRelationship1.getName(), OBJECT_FIELD_NAME_1,
					OBJECT_FIELD_VALUE_1, RandomTestUtil.randomInt())),
			getObjectDefinition1());

		objectRelationshipLocalService.deleteObjectRelationship(
			objectRelationship1);

		// One to many relationship, custom object field

		objectRelationship1 = addObjectRelationshipAndRelateObjectEntries(
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);

		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s in ('%s', '%s')", objectRelationship1.getName(),
					OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2,
					RandomTestUtil.randomInt())),
			getObjectDefinition1());

		// One to many relationship, system object field

		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/id in ('%s', '%s')", objectRelationship1.getName(),
					objectEntry2.getObjectEntryId(),
					RandomTestUtil.randomInt())),
			getObjectDefinition1());

		// One to many relationship (other side), custom object field

		assertFilterString(
			OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2,
			escape(
				String.format(
					"%s/%s in ('%s', '%s')", objectRelationship1.getName(),
					OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
					RandomTestUtil.randomInt())),
			getObjectDefinition2());

		// One to many relationship (other side), system object field

		assertFilterString(
			OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2,
			escape(
				String.format(
					"%s/id in ('%s', '%s')", objectRelationship1.getName(),
					objectEntry1.getObjectEntryId(),
					RandomTestUtil.randomInt())),
			getObjectDefinition2());

		objectRelationshipLocalService.deleteObjectRelationship(
			objectRelationship1);

		// One to many self relationship, custom object field

		objectRelationship1 = addObjectRelationshipAndRelateObjectEntries(
			getObjectDefinition1(), getObjectDefinition1(),
			objectEntry1.getPrimaryKey(), objectEntry1.getPrimaryKey(),
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);

		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s in ('%s', '%s')", objectRelationship1.getName(),
					OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
					RandomTestUtil.randomInt())),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s/%s in ('%s', '%s')", objectRelationship1.getName(),
					objectRelationship1.getName(), OBJECT_FIELD_NAME_1,
					OBJECT_FIELD_VALUE_1, RandomTestUtil.randomInt())),
			getObjectDefinition1());
	}

	@Test
	public void testFilterByLogicalOperatorsObjectEntriesByRelatedObjectEntriesFields()
		throws Exception {

		// Many to many relationship, custom object field

		objectEntry1 = ObjectEntryTestUtil.addObjectEntry(
			getObjectDefinition1(), OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1);
		objectEntry2 = ObjectEntryTestUtil.addObjectEntry(
			getObjectDefinition2(), OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2);

		objectRelationship1 = addObjectRelationshipAndRelateObjectEntries(
			ObjectRelationshipConstants.TYPE_MANY_TO_MANY);

		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s le '%s' and %s/%s gt '%s'",
					objectRelationship1.getName(), OBJECT_FIELD_NAME_2,
					OBJECT_FIELD_VALUE_2, objectRelationship1.getName(),
					OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2 - 1)),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s le '%s' or %s/%s gt '%s'",
					objectRelationship1.getName(), OBJECT_FIELD_NAME_2,
					OBJECT_FIELD_VALUE_2, objectRelationship1.getName(),
					OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2 - 1)),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"not (%s/%s ge '%s')", objectRelationship1.getName(),
					OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2 + 1)),
			getObjectDefinition1());

		// Many to many relationship, system object field

		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/id le '%s' and %s/id gt '%s'",
					objectRelationship1.getName(),
					objectEntry2.getObjectEntryId(),
					objectRelationship1.getName(),
					objectEntry2.getObjectEntryId() - 1)),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/id le '%s' or %s/id gt '%s'",
					objectRelationship1.getName(), OBJECT_FIELD_VALUE_2,
					objectRelationship1.getName(),
					objectEntry2.getObjectEntryId() - 1)),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"not (%s/id ge '%s')", objectRelationship1.getName(),
					objectEntry2.getObjectEntryId() + 1)),
			getObjectDefinition1());

		// Many to many relationship (other side), custom object field

		assertFilterString(
			OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2,
			escape(
				String.format(
					"%s/%s le '%s' and %s/%s gt '%s'",
					objectRelationship1.getName(), OBJECT_FIELD_NAME_1,
					OBJECT_FIELD_VALUE_1, objectRelationship1.getName(),
					OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1 - 1)),
			getObjectDefinition2());
		assertFilterString(
			OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2,
			escape(
				String.format(
					"%s/%s le '%s' or %s/%s gt '%s'",
					objectRelationship1.getName(), OBJECT_FIELD_NAME_1,
					OBJECT_FIELD_VALUE_1, objectRelationship1.getName(),
					OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1 - 1)),
			getObjectDefinition2());
		assertFilterString(
			OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2,
			escape(
				String.format(
					"not (%s/%s ge '%s')", objectRelationship1.getName(),
					OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1 + 1)),
			getObjectDefinition2());

		// Many to many relationship (other side), system object field

		assertFilterString(
			OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2,
			escape(
				String.format(
					"%s/id le '%s' and %s/id gt '%s'",
					objectRelationship1.getName(),
					objectEntry1.getObjectEntryId(),
					objectRelationship1.getName(),
					objectEntry1.getObjectEntryId() - 1)),
			getObjectDefinition2());
		assertFilterString(
			OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2,
			escape(
				String.format(
					"%s/id le '%s' or %s/id gt '%s'",
					objectRelationship1.getName(), OBJECT_FIELD_VALUE_1,
					objectRelationship1.getName(),
					objectEntry1.getObjectEntryId() - 1)),
			getObjectDefinition2());
		assertFilterString(
			OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2,
			escape(
				String.format(
					"not (%s/id ge '%s')", objectRelationship1.getName(),
					objectEntry1.getObjectEntryId() + 1)),
			getObjectDefinition2());

		objectRelationshipLocalService.deleteObjectRelationship(
			objectRelationship1);

		// Many to many self relationship, custom object field

		objectRelationship1 = addObjectRelationshipAndRelateObjectEntries(
			getObjectDefinition1(), getObjectDefinition1(),
			objectEntry1.getPrimaryKey(), objectEntry1.getPrimaryKey(),
			ObjectRelationshipConstants.TYPE_MANY_TO_MANY);

		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s le '%s' and %s/%s gt '%s'",
					objectRelationship1.getName(), OBJECT_FIELD_NAME_1,
					OBJECT_FIELD_VALUE_1, objectRelationship1.getName(),
					OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1 - 1)),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s/%s le '%s' and %s/%s/%s gt '%s'",
					objectRelationship1.getName(),
					objectRelationship1.getName(), OBJECT_FIELD_NAME_1,
					OBJECT_FIELD_VALUE_1, objectRelationship1.getName(),
					objectRelationship1.getName(), OBJECT_FIELD_NAME_1,
					OBJECT_FIELD_VALUE_1 - 1)),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s le '%s' or %s/%s gt '%s'",
					objectRelationship1.getName(), OBJECT_FIELD_NAME_1,
					OBJECT_FIELD_VALUE_1, objectRelationship1.getName(),
					OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1 - 1)),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s/%s le '%s' or %s/%s/%s gt '%s'",
					objectRelationship1.getName(),
					objectRelationship1.getName(), OBJECT_FIELD_NAME_1,
					OBJECT_FIELD_VALUE_1, objectRelationship1.getName(),
					objectRelationship1.getName(), OBJECT_FIELD_NAME_1,
					OBJECT_FIELD_VALUE_1 - 1)),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"not (%s/%s ge '%s')", objectRelationship1.getName(),
					OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1 + 1)),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"not (%s/%s/%s ge '%s')", objectRelationship1.getName(),
					objectRelationship1.getName(), OBJECT_FIELD_NAME_1,
					OBJECT_FIELD_VALUE_1 + 1)),
			getObjectDefinition1());

		objectRelationshipLocalService.deleteObjectRelationship(
			objectRelationship1);

		// One to many relationship, custom object field

		objectRelationship1 = addObjectRelationshipAndRelateObjectEntries(
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);

		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s le '%s' and %s/%s gt '%s'",
					objectRelationship1.getName(), OBJECT_FIELD_NAME_2,
					OBJECT_FIELD_VALUE_2, objectRelationship1.getName(),
					OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2 - 1)),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s le '%s' or %s/%s gt '%s'",
					objectRelationship1.getName(), OBJECT_FIELD_NAME_2,
					OBJECT_FIELD_VALUE_2, objectRelationship1.getName(),
					OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2 - 1)),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"not (%s/%s ge '%s')", objectRelationship1.getName(),
					OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2 + 1)),
			getObjectDefinition1());

		// One to many relationship, system object field

		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/id le '%s' and %s/id gt '%s'",
					objectRelationship1.getName(),
					objectEntry2.getObjectEntryId(),
					objectRelationship1.getName(),
					objectEntry2.getObjectEntryId() - 1)),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/id le '%s' or %s/id gt '%s'",
					objectRelationship1.getName(), OBJECT_FIELD_VALUE_2,
					objectRelationship1.getName(),
					objectEntry2.getObjectEntryId() - 1)),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"not (%s/id ge '%s')", objectRelationship1.getName(),
					objectEntry2.getObjectEntryId() + 1)),
			getObjectDefinition1());

		// One to many relationship (other side), custom object field

		assertFilterString(
			OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2,
			escape(
				String.format(
					"%s/%s le '%s' and %s/%s gt '%s'",
					objectRelationship1.getName(), OBJECT_FIELD_NAME_1,
					OBJECT_FIELD_VALUE_1, objectRelationship1.getName(),
					OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1 - 1)),
			getObjectDefinition2());
		assertFilterString(
			OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2,
			escape(
				String.format(
					"%s/%s le '%s' or %s/%s gt '%s'",
					objectRelationship1.getName(), OBJECT_FIELD_NAME_1,
					OBJECT_FIELD_VALUE_1, objectRelationship1.getName(),
					OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1 - 1)),
			getObjectDefinition2());
		assertFilterString(
			OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2,
			escape(
				String.format(
					"not (%s/%s ge '%s')", objectRelationship1.getName(),
					OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1 + 1)),
			getObjectDefinition2());

		// One to many relationship (other side), system object field

		assertFilterString(
			OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2,
			escape(
				String.format(
					"%s/id le '%s' and %s/id gt '%s'",
					objectRelationship1.getName(),
					objectEntry1.getObjectEntryId(),
					objectRelationship1.getName(),
					objectEntry1.getObjectEntryId() - 1)),
			getObjectDefinition2());
		assertFilterString(
			OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2,
			escape(
				String.format(
					"%s/id le '%s' or %s/id gt '%s'",
					objectRelationship1.getName(), OBJECT_FIELD_VALUE_1,
					objectRelationship1.getName(),
					objectEntry1.getObjectEntryId() - 1)),
			getObjectDefinition2());
		assertFilterString(
			OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2,
			escape(
				String.format(
					"not (%s/id ge '%s')", objectRelationship1.getName(),
					objectEntry1.getObjectEntryId() + 1)),
			getObjectDefinition2());

		objectRelationshipLocalService.deleteObjectRelationship(
			objectRelationship1);

		// One to many self relationship, custom object field

		objectRelationship1 = addObjectRelationshipAndRelateObjectEntries(
			getObjectDefinition1(), getObjectDefinition1(),
			objectEntry1.getPrimaryKey(), objectEntry1.getPrimaryKey(),
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);

		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s le '%s' and %s/%s gt '%s'",
					objectRelationship1.getName(), OBJECT_FIELD_NAME_1,
					OBJECT_FIELD_VALUE_1, objectRelationship1.getName(),
					OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1 - 1)),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s/%s le '%s' and %s/%s/%s gt '%s'",
					objectRelationship1.getName(),
					objectRelationship1.getName(), OBJECT_FIELD_NAME_1,
					OBJECT_FIELD_VALUE_1, objectRelationship1.getName(),
					objectRelationship1.getName(), OBJECT_FIELD_NAME_1,
					OBJECT_FIELD_VALUE_1 - 1)),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s le '%s' or %s/%s gt '%s'",
					objectRelationship1.getName(), OBJECT_FIELD_NAME_1,
					OBJECT_FIELD_VALUE_1, objectRelationship1.getName(),
					OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1 - 1)),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s/%s le '%s' or %s/%s/%s gt '%s'",
					objectRelationship1.getName(),
					objectRelationship1.getName(), OBJECT_FIELD_NAME_1,
					OBJECT_FIELD_VALUE_1, objectRelationship1.getName(),
					objectRelationship1.getName(), OBJECT_FIELD_NAME_1,
					OBJECT_FIELD_VALUE_1 - 1)),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"not (%s/%s ge '%s')", objectRelationship1.getName(),
					OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1 + 1)),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"not (%s/%s/%s ge '%s')", objectRelationship1.getName(),
					objectRelationship1.getName(), OBJECT_FIELD_NAME_1,
					OBJECT_FIELD_VALUE_1 + 1)),
			getObjectDefinition1());
	}

	@Test
	public void testFilterByLogicalOperatorsObjectEntriesByRelatedObjectEntriesFieldsThroughMultipleRelationships()
		throws Exception {

		// Many to many relationship, custom object field

		objectEntry1 = ObjectEntryTestUtil.addObjectEntry(
			getObjectDefinition1(), OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1);
		objectEntry2 = ObjectEntryTestUtil.addObjectEntry(
			getObjectDefinition2(), OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2);
		objectEntry3 = ObjectEntryTestUtil.addObjectEntry(
			getObjectDefinition3(), OBJECT_FIELD_NAME_3, OBJECT_FIELD_VALUE_3);

		objectRelationship1 = addObjectRelationshipAndRelateObjectEntries(
			getObjectDefinition1(), getObjectDefinition2(),
			objectEntry1.getPrimaryKey(), objectEntry2.getPrimaryKey(),
			ObjectRelationshipConstants.TYPE_MANY_TO_MANY);
		objectRelationship2 = addObjectRelationshipAndRelateObjectEntries(
			getObjectDefinition2(), getObjectDefinition3(),
			objectEntry2.getPrimaryKey(), objectEntry3.getPrimaryKey(),
			ObjectRelationshipConstants.TYPE_MANY_TO_MANY);

		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s/%s le '%s' and %s/%s/%s gt '%s'",
					objectRelationship1.getName(),
					objectRelationship2.getName(), OBJECT_FIELD_NAME_3,
					OBJECT_FIELD_VALUE_3, objectRelationship1.getName(),
					objectRelationship2.getName(), OBJECT_FIELD_NAME_3,
					OBJECT_FIELD_VALUE_3 - 1)),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s/%s le '%s' or %s/%s/%s gt '%s'",
					objectRelationship1.getName(),
					objectRelationship2.getName(), OBJECT_FIELD_NAME_3,
					OBJECT_FIELD_VALUE_3, objectRelationship1.getName(),
					objectRelationship2.getName(), OBJECT_FIELD_NAME_3,
					OBJECT_FIELD_VALUE_3 - 1)),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"not (%s/%s/%s ge '%s')", objectRelationship1.getName(),
					objectRelationship2.getName(), OBJECT_FIELD_NAME_3,
					OBJECT_FIELD_VALUE_3 + 1)),
			getObjectDefinition1());

		// Many to many relationship, system object field

		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s/id le '%s' and %s/%s/id gt '%s'",
					objectRelationship1.getName(),
					objectRelationship2.getName(),
					objectEntry3.getObjectEntryId(),
					objectRelationship1.getName(),
					objectRelationship2.getName(),
					objectEntry3.getObjectEntryId() - 1)),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s/id le '%s' or %s/%s/id gt '%s'",
					objectRelationship1.getName(),
					objectRelationship2.getName(),
					objectEntry3.getObjectEntryId(),
					objectRelationship1.getName(),
					objectRelationship2.getName(),
					objectEntry3.getObjectEntryId() - 1)),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"not (%s/%s/id ge '%s')", objectRelationship1.getName(),
					objectRelationship2.getName(),
					objectEntry3.getObjectEntryId() + 1)),
			getObjectDefinition1());

		// Many to many relationship (other side), custom object field

		assertFilterString(
			OBJECT_FIELD_NAME_3, OBJECT_FIELD_VALUE_3,
			escape(
				String.format(
					"%s/%s/%s le '%s' and %s/%s/%s gt '%s'",
					objectRelationship2.getName(),
					objectRelationship1.getName(), OBJECT_FIELD_NAME_1,
					OBJECT_FIELD_VALUE_1, objectRelationship2.getName(),
					objectRelationship1.getName(), OBJECT_FIELD_NAME_1,
					OBJECT_FIELD_VALUE_1 - 1)),
			getObjectDefinition3());
		assertFilterString(
			OBJECT_FIELD_NAME_3, OBJECT_FIELD_VALUE_3,
			escape(
				String.format(
					"%s/%s/%s le '%s' or %s/%s/%s gt '%s'",
					objectRelationship2.getName(),
					objectRelationship1.getName(), OBJECT_FIELD_NAME_1,
					OBJECT_FIELD_VALUE_1, objectRelationship2.getName(),
					objectRelationship1.getName(), OBJECT_FIELD_NAME_1,
					OBJECT_FIELD_VALUE_1 - 1)),
			getObjectDefinition3());
		assertFilterString(
			OBJECT_FIELD_NAME_3, OBJECT_FIELD_VALUE_3,
			escape(
				String.format(
					"not (%s/%s/%s ge '%s')", objectRelationship2.getName(),
					objectRelationship1.getName(), OBJECT_FIELD_NAME_1,
					OBJECT_FIELD_VALUE_1 + 1)),
			getObjectDefinition3());

		// Many to many relationship (other side), system object field

		assertFilterString(
			OBJECT_FIELD_NAME_3, OBJECT_FIELD_VALUE_3,
			escape(
				String.format(
					"%s/%s/id le '%s' and %s/%s/id gt '%s'",
					objectRelationship2.getName(),
					objectRelationship1.getName(),
					objectEntry1.getObjectEntryId(),
					objectRelationship2.getName(),
					objectRelationship1.getName(),
					objectEntry1.getObjectEntryId() - 1)),
			getObjectDefinition3());
		assertFilterString(
			OBJECT_FIELD_NAME_3, OBJECT_FIELD_VALUE_3,
			escape(
				String.format(
					"%s/%s/id le '%s' or %s/%s/id gt '%s'",
					objectRelationship2.getName(),
					objectRelationship1.getName(),
					objectEntry1.getObjectEntryId(),
					objectRelationship2.getName(),
					objectRelationship1.getName(),
					objectEntry1.getObjectEntryId() - 1)),
			getObjectDefinition3());
		assertFilterString(
			OBJECT_FIELD_NAME_3, OBJECT_FIELD_VALUE_3,
			escape(
				String.format(
					"not (%s/%s/id ge '%s')", objectRelationship2.getName(),
					objectRelationship1.getName(),
					objectEntry1.getObjectEntryId() + 1)),
			getObjectDefinition3());

		objectRelationshipLocalService.deleteObjectRelationship(
			objectRelationship1);
		objectRelationshipLocalService.deleteObjectRelationship(
			objectRelationship2);

		// One to many relationship, custom object field

		objectRelationship1 = addObjectRelationshipAndRelateObjectEntries(
			getObjectDefinition1(), getObjectDefinition2(),
			objectEntry1.getPrimaryKey(), objectEntry2.getPrimaryKey(),
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);
		objectRelationship2 = addObjectRelationshipAndRelateObjectEntries(
			getObjectDefinition2(), getObjectDefinition3(),
			objectEntry2.getPrimaryKey(), objectEntry3.getPrimaryKey(),
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);

		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s/%s le '%s' and %s/%s/%s gt '%s'",
					objectRelationship1.getName(),
					objectRelationship2.getName(), OBJECT_FIELD_NAME_3,
					OBJECT_FIELD_VALUE_3, objectRelationship1.getName(),
					objectRelationship2.getName(), OBJECT_FIELD_NAME_3,
					OBJECT_FIELD_VALUE_3 - 1)),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s/%s le '%s' or %s/%s/%s gt '%s'",
					objectRelationship1.getName(),
					objectRelationship2.getName(), OBJECT_FIELD_NAME_3,
					OBJECT_FIELD_VALUE_3, objectRelationship1.getName(),
					objectRelationship2.getName(), OBJECT_FIELD_NAME_3,
					OBJECT_FIELD_VALUE_3 - 1)),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"not (%s/%s/%s ge '%s')", objectRelationship1.getName(),
					objectRelationship2.getName(), OBJECT_FIELD_NAME_3,
					OBJECT_FIELD_VALUE_3 + 1)),
			getObjectDefinition1());

		// One to many relationship, system object field

		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s/id le '%s' and %s/%s/id gt '%s'",
					objectRelationship1.getName(),
					objectRelationship2.getName(),
					objectEntry3.getObjectEntryId(),
					objectRelationship1.getName(),
					objectRelationship2.getName(),
					objectEntry3.getObjectEntryId() - 1)),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s/id le '%s' or %s/%s/id gt '%s'",
					objectRelationship1.getName(),
					objectRelationship2.getName(),
					objectEntry3.getObjectEntryId(),
					objectRelationship1.getName(),
					objectRelationship2.getName(),
					objectEntry3.getObjectEntryId() - 1)),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"not (%s/%s/id ge '%s')", objectRelationship1.getName(),
					objectRelationship2.getName(),
					objectEntry3.getObjectEntryId() + 1)),
			getObjectDefinition1());

		// One to many relationship (other side), custom object field

		assertFilterString(
			OBJECT_FIELD_NAME_3, OBJECT_FIELD_VALUE_3,
			escape(
				String.format(
					"%s/%s/%s le '%s' and %s/%s/%s gt '%s'",
					objectRelationship2.getName(),
					objectRelationship1.getName(), OBJECT_FIELD_NAME_1,
					OBJECT_FIELD_VALUE_1, objectRelationship2.getName(),
					objectRelationship1.getName(), OBJECT_FIELD_NAME_1,
					OBJECT_FIELD_VALUE_1 - 1)),
			getObjectDefinition3());
		assertFilterString(
			OBJECT_FIELD_NAME_3, OBJECT_FIELD_VALUE_3,
			escape(
				String.format(
					"%s/%s/%s le '%s' or %s/%s/%s gt '%s'",
					objectRelationship2.getName(),
					objectRelationship1.getName(), OBJECT_FIELD_NAME_1,
					OBJECT_FIELD_VALUE_1, objectRelationship2.getName(),
					objectRelationship1.getName(), OBJECT_FIELD_NAME_1,
					OBJECT_FIELD_VALUE_1 - 1)),
			getObjectDefinition3());
		assertFilterString(
			OBJECT_FIELD_NAME_3, OBJECT_FIELD_VALUE_3,
			escape(
				String.format(
					"not (%s/%s/%s ge '%s')", objectRelationship2.getName(),
					objectRelationship1.getName(), OBJECT_FIELD_NAME_1,
					OBJECT_FIELD_VALUE_1 + 1)),
			getObjectDefinition3());

		// One to many relationship (other side), system object field

		assertFilterString(
			OBJECT_FIELD_NAME_3, OBJECT_FIELD_VALUE_3,
			escape(
				String.format(
					"%s/%s/id le '%s' and %s/%s/id gt '%s'",
					objectRelationship2.getName(),
					objectRelationship1.getName(),
					objectEntry1.getObjectEntryId(),
					objectRelationship2.getName(),
					objectRelationship1.getName(),
					objectEntry1.getObjectEntryId() - 1)),
			getObjectDefinition3());
		assertFilterString(
			OBJECT_FIELD_NAME_3, OBJECT_FIELD_VALUE_3,
			escape(
				String.format(
					"%s/%s/id le '%s' or %s/%s/id gt '%s'",
					objectRelationship2.getName(),
					objectRelationship1.getName(),
					objectEntry1.getObjectEntryId(),
					objectRelationship2.getName(),
					objectRelationship1.getName(),
					objectEntry1.getObjectEntryId() - 1)),
			getObjectDefinition3());
		assertFilterString(
			OBJECT_FIELD_NAME_3, OBJECT_FIELD_VALUE_3,
			escape(
				String.format(
					"not (%s/%s/id ge '%s')", objectRelationship2.getName(),
					objectRelationship1.getName(),
					objectEntry1.getObjectEntryId() + 1)),
			getObjectDefinition3());
	}

	@Test
	public void testFilterByObjectRelationshipERCObjectFieldNameInOneToManyRelationship()
		throws Exception {

		objectEntry1 = ObjectEntryTestUtil.addObjectEntry(
			getObjectDefinition1(),
			HashMapBuilder.<String, Serializable>put(
				OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1
			).put(
				"externalReferenceCode",
				() -> {
					StringBuilder sb = new StringBuilder(
						RandomTestUtil.randomString());

					sb.setCharAt(4, 'a');

					return sb.toString();
				}
			).build());

		objectEntry2 = ObjectEntryTestUtil.addObjectEntry(
			getObjectDefinition2(), OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2);

		objectRelationship1 = addObjectRelationshipAndRelateObjectEntries(
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);

		String objectRelationshipERCObjectFieldName =
			ObjectFieldSettingUtil.getValue(
				ObjectFieldSettingConstants.
					NAME_OBJECT_RELATIONSHIP_ERC_OBJECT_FIELD_NAME,
				objectFieldLocalService.getObjectField(
					objectRelationship1.getObjectFieldId2()));

		Assert.assertEquals(
			StringBundler.concat(
				"r_", objectRelationship1.getName(), "_",
				StringUtil.replaceLast(
					getObjectDefinition1().getPKObjectFieldName(), "Id",
					"ERC")),
			objectRelationshipERCObjectFieldName);

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			null, getObjectDefinition2().getRESTContextPath(), Http.Method.GET);

		JSONArray itemsJSONArray = jsonObject.getJSONArray("items");

		Assert.assertEquals(1, itemsJSONArray.length());

		JSONObject itemJSONObject = itemsJSONArray.getJSONObject(0);

		String objectEntry1ExternalReferenceCode =
			objectEntry1.getExternalReferenceCode();

		Assert.assertEquals(
			itemJSONObject.getString(objectRelationshipERCObjectFieldName),
			objectEntry1ExternalReferenceCode);

		// Comparison operators

		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s eq '%s'", objectRelationship1.getName(),
					objectRelationshipERCObjectFieldName,
					objectEntry1ExternalReferenceCode)),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s ge '%s'", objectRelationship1.getName(),
					objectRelationshipERCObjectFieldName,
					objectEntry1ExternalReferenceCode)),
			getObjectDefinition1());

		String substring = objectEntry1ExternalReferenceCode.substring(0, 4);

		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s gt '%s'", objectRelationship1.getName(),
					objectRelationshipERCObjectFieldName, substring + "0000")),
			getObjectDefinition1());

		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s le '%s'", objectRelationship1.getName(),
					objectRelationshipERCObjectFieldName,
					objectEntry1ExternalReferenceCode)),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s lt '%s'", objectRelationship1.getName(),
					objectRelationshipERCObjectFieldName, substring + "zzzz")),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s ne '%s'", objectRelationship1.getName(),
					objectRelationshipERCObjectFieldName,
					RandomTestUtil.randomInt())),
			getObjectDefinition1());

		// List operators

		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s/%s in ('%s', '%s')", objectRelationship1.getName(),
					objectRelationshipERCObjectFieldName,
					objectEntry1ExternalReferenceCode,
					RandomTestUtil.randomInt())),
			getObjectDefinition1());

		// String operators

		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"contains(%s/%s,'%s')", objectRelationship1.getName(),
				objectRelationshipERCObjectFieldName, substring),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"startswith(%s/%s,'%s')", objectRelationship1.getName(),
				objectRelationshipERCObjectFieldName, substring),
			getObjectDefinition1());
	}

	@Test
	public void testFilterByStringOperatorsObjectEntriesByRelatedObjectEntriesFields()
		throws Exception {

		String objectFieldValue1 = String.valueOf(OBJECT_FIELD_VALUE_1);

		objectEntry1 = ObjectEntryTestUtil.addObjectEntry(
			getObjectDefinition1(), OBJECT_FIELD_NAME_1, objectFieldValue1);

		String objectFieldValue2 = String.valueOf(OBJECT_FIELD_VALUE_2);

		objectEntry2 = ObjectEntryTestUtil.addObjectEntry(
			getObjectDefinition2(), OBJECT_FIELD_NAME_2, objectFieldValue2);

		// Many to many relationship, custom object field

		objectRelationship1 = addObjectRelationshipAndRelateObjectEntries(
			ObjectRelationshipConstants.TYPE_MANY_TO_MANY);

		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"contains(%s/%s,'%s')", objectRelationship1.getName(),
				OBJECT_FIELD_NAME_2, objectFieldValue2.substring(1)),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"startswith(%s/%s,'%s')", objectRelationship1.getName(),
				OBJECT_FIELD_NAME_2, objectFieldValue2.substring(0, 2)),
			getObjectDefinition1());

		// Many to many relationship, system object field

		String objectEntry2ExternalReferenceCode =
			objectEntry2.getExternalReferenceCode();

		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"contains(%s/externalReferenceCode,'%s')",
				objectRelationship1.getName(),
				objectEntry2ExternalReferenceCode.substring(1)),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"startswith(%s/externalReferenceCode,'%s')",
				objectRelationship1.getName(),
				objectEntry2ExternalReferenceCode.substring(0, 2)),
			getObjectDefinition1());

		// Many to many relationship (other side), custom object field

		assertFilterString(
			OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2,
			String.format(
				"contains(%s/%s,'%s')", objectRelationship1.getName(),
				OBJECT_FIELD_NAME_1, objectFieldValue1.substring(1)),
			getObjectDefinition2());
		assertFilterString(
			OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2,
			String.format(
				"startswith(%s/%s,'%s')", objectRelationship1.getName(),
				OBJECT_FIELD_NAME_1, objectFieldValue1.substring(0, 2)),
			getObjectDefinition2());

		// Many to many relationship (other side), system object field

		String objectEntry1ExternalReferenceCode =
			objectEntry1.getExternalReferenceCode();

		assertFilterString(
			OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2,
			String.format(
				"contains(%s/externalReferenceCode,'%s')",
				objectRelationship1.getName(),
				objectEntry1ExternalReferenceCode.substring(1)),
			getObjectDefinition2());
		assertFilterString(
			OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2,
			String.format(
				"startswith(%s/externalReferenceCode,'%s')",
				objectRelationship1.getName(),
				objectEntry1ExternalReferenceCode.substring(0, 2)),
			getObjectDefinition2());

		objectRelationshipLocalService.deleteObjectRelationship(
			objectRelationship1);

		// Many to many self relationship, custom object field

		objectRelationship1 = addObjectRelationshipAndRelateObjectEntries(
			getObjectDefinition1(), getObjectDefinition1(),
			objectEntry1.getPrimaryKey(), objectEntry1.getPrimaryKey(),
			ObjectRelationshipConstants.TYPE_MANY_TO_MANY);

		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"contains(%s/%s,'%s')", objectRelationship1.getName(),
				OBJECT_FIELD_NAME_1, objectFieldValue1.substring(1)),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"contains(%s/%s/%s,'%s')", objectRelationship1.getName(),
				objectRelationship1.getName(), OBJECT_FIELD_NAME_1,
				objectFieldValue1.substring(1)),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"startswith(%s/%s,'%s')", objectRelationship1.getName(),
				OBJECT_FIELD_NAME_1, objectFieldValue1.substring(0, 2)),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"startswith(%s/%s/%s,'%s')", objectRelationship1.getName(),
				objectRelationship1.getName(), OBJECT_FIELD_NAME_1,
				objectFieldValue1.substring(0, 2)),
			getObjectDefinition1());

		objectRelationshipLocalService.deleteObjectRelationship(
			objectRelationship1);

		// One to many relationship, custom object field

		objectRelationship1 = addObjectRelationshipAndRelateObjectEntries(
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);

		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"contains(%s/%s,'%s')", objectRelationship1.getName(),
				OBJECT_FIELD_NAME_2, objectFieldValue2.substring(1)),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"startswith(%s/%s,'%s')", objectRelationship1.getName(),
				OBJECT_FIELD_NAME_2, objectFieldValue2.substring(0, 2)),
			getObjectDefinition1());

		// One to many relationship, system object field

		objectEntry2ExternalReferenceCode =
			objectEntry2.getExternalReferenceCode();

		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"contains(%s/externalReferenceCode,'%s')",
				objectRelationship1.getName(),
				objectEntry2ExternalReferenceCode.substring(1)),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"startswith(%s/externalReferenceCode,'%s')",
				objectRelationship1.getName(),
				objectEntry2ExternalReferenceCode.substring(0, 2)),
			getObjectDefinition1());

		// One to many relationship (other side), custom object field

		assertFilterString(
			OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2,
			String.format(
				"contains(%s/%s,'%s')", objectRelationship1.getName(),
				OBJECT_FIELD_NAME_1, objectFieldValue1.substring(1)),
			getObjectDefinition2());
		assertFilterString(
			OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2,
			String.format(
				"startswith(%s/%s,'%s')", objectRelationship1.getName(),
				OBJECT_FIELD_NAME_1, objectFieldValue1.substring(0, 2)),
			getObjectDefinition2());

		// One to many relationship (other side), system object field

		objectEntry1ExternalReferenceCode =
			objectEntry1.getExternalReferenceCode();

		assertFilterString(
			OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2,
			String.format(
				"contains(%s/externalReferenceCode,'%s')",
				objectRelationship1.getName(),
				objectEntry1ExternalReferenceCode.substring(1)),
			getObjectDefinition2());
		assertFilterString(
			OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2,
			String.format(
				"startswith(%s/externalReferenceCode,'%s')",
				objectRelationship1.getName(),
				objectEntry1ExternalReferenceCode.substring(0, 2)),
			getObjectDefinition2());

		objectRelationshipLocalService.deleteObjectRelationship(
			objectRelationship1);

		// One to many self relationship, custom object field

		objectRelationship1 = addObjectRelationshipAndRelateObjectEntries(
			getObjectDefinition1(), getObjectDefinition1(),
			objectEntry1.getPrimaryKey(), objectEntry1.getPrimaryKey(),
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);

		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"contains(%s/%s,'%s')", objectRelationship1.getName(),
				OBJECT_FIELD_NAME_1, objectFieldValue1.substring(1)),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"contains(%s/%s/%s,'%s')", objectRelationship1.getName(),
				objectRelationship1.getName(), OBJECT_FIELD_NAME_1,
				objectFieldValue1.substring(1)),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"startswith(%s/%s,'%s')", objectRelationship1.getName(),
				OBJECT_FIELD_NAME_1, objectFieldValue1.substring(0, 2)),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			String.format(
				"startswith(%s/%s/%s,'%s')", objectRelationship1.getName(),
				objectRelationship1.getName(), OBJECT_FIELD_NAME_1,
				objectFieldValue1.substring(0, 2)),
			getObjectDefinition1());
	}

	@Test
	public void testFilterByStringOperatorsObjectEntriesByRelatedObjectEntriesFieldsThroughMultipleRelationships()
		throws Exception {

		String objectFieldValue1 = String.valueOf(OBJECT_FIELD_VALUE_1);
		String objectFieldValue2 = String.valueOf(OBJECT_FIELD_VALUE_2);
		String objectFieldValue3 = String.valueOf(OBJECT_FIELD_VALUE_3);

		objectEntry1 = ObjectEntryTestUtil.addObjectEntry(
			getObjectDefinition1(), OBJECT_FIELD_NAME_1, objectFieldValue1);
		objectEntry2 = ObjectEntryTestUtil.addObjectEntry(
			getObjectDefinition2(), OBJECT_FIELD_NAME_2, objectFieldValue2);
		objectEntry3 = ObjectEntryTestUtil.addObjectEntry(
			getObjectDefinition3(), OBJECT_FIELD_NAME_3, objectFieldValue3);

		// Many to many relationship, custom object field

		objectRelationship1 = addObjectRelationshipAndRelateObjectEntries(
			getObjectDefinition1(), getObjectDefinition2(),
			objectEntry1.getPrimaryKey(), objectEntry2.getPrimaryKey(),
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);
		objectRelationship2 = addObjectRelationshipAndRelateObjectEntries(
			getObjectDefinition2(), getObjectDefinition3(),
			objectEntry2.getPrimaryKey(), objectEntry3.getPrimaryKey(),
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);

		assertFilterString(
			OBJECT_FIELD_NAME_1, objectFieldValue1,
			escape(
				String.format(
					"contains(%s/%s/%s,'%s')", objectRelationship1.getName(),
					objectRelationship2.getName(), OBJECT_FIELD_NAME_3,
					objectFieldValue3.substring(1))),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, objectFieldValue1,
			escape(
				String.format(
					"startswith(%s/%s/%s,'%s')", objectRelationship1.getName(),
					objectRelationship2.getName(), OBJECT_FIELD_NAME_3,
					objectFieldValue3.substring(0, 2))),
			getObjectDefinition1());

		// Many to many relationship, system object field

		String objectEntry3ExternalReferenceCode =
			objectEntry3.getExternalReferenceCode();

		assertFilterString(
			OBJECT_FIELD_NAME_1, objectFieldValue1,
			escape(
				String.format(
					"contains(%s/%s/externalReferenceCode,'%s')",
					objectRelationship1.getName(),
					objectRelationship2.getName(),
					objectEntry3ExternalReferenceCode.substring(1))),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, objectFieldValue1,
			escape(
				String.format(
					"startswith(%s/%s/externalReferenceCode,'%s')",
					objectRelationship1.getName(),
					objectRelationship2.getName(),
					objectEntry3ExternalReferenceCode.substring(0, 2))),
			getObjectDefinition1());

		// Many to many relationship (other side), custom object field

		assertFilterString(
			OBJECT_FIELD_NAME_3, objectFieldValue3,
			escape(
				String.format(
					"contains(%s/%s/%s,'%s')", objectRelationship2.getName(),
					objectRelationship1.getName(), OBJECT_FIELD_NAME_1,
					objectFieldValue1.substring(1))),
			getObjectDefinition3());
		assertFilterString(
			OBJECT_FIELD_NAME_3, objectFieldValue3,
			escape(
				String.format(
					"startswith(%s/%s/%s,'%s')", objectRelationship2.getName(),
					objectRelationship1.getName(), OBJECT_FIELD_NAME_1,
					objectFieldValue1.substring(0, 2))),
			getObjectDefinition3());

		// Many to many relationship (other side), system object field

		String objectEntry1ExternalReferenceCode =
			objectEntry1.getExternalReferenceCode();

		assertFilterString(
			OBJECT_FIELD_NAME_3, objectFieldValue3,
			escape(
				String.format(
					"contains(%s/%s/externalReferenceCode,'%s')",
					objectRelationship2.getName(),
					objectRelationship1.getName(),
					objectEntry1ExternalReferenceCode.substring(1))),
			getObjectDefinition3());
		assertFilterString(
			OBJECT_FIELD_NAME_3, objectFieldValue3,
			escape(
				String.format(
					"startswith(%s/%s/externalReferenceCode,'%s')",
					objectRelationship2.getName(),
					objectRelationship1.getName(),
					objectEntry1ExternalReferenceCode.substring(0, 2))),
			getObjectDefinition3());

		objectRelationshipLocalService.deleteObjectRelationship(
			objectRelationship1);
		objectRelationshipLocalService.deleteObjectRelationship(
			objectRelationship2);

		// One to many relationship, custom object field

		objectRelationship1 = addObjectRelationshipAndRelateObjectEntries(
			getObjectDefinition1(), getObjectDefinition2(),
			objectEntry1.getPrimaryKey(), objectEntry2.getPrimaryKey(),
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);
		objectRelationship2 = addObjectRelationshipAndRelateObjectEntries(
			getObjectDefinition2(), getObjectDefinition3(),
			objectEntry2.getPrimaryKey(), objectEntry3.getPrimaryKey(),
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);

		assertFilterString(
			OBJECT_FIELD_NAME_1, objectFieldValue1,
			escape(
				String.format(
					"contains(%s/%s/%s,'%s')", objectRelationship1.getName(),
					objectRelationship2.getName(), OBJECT_FIELD_NAME_3,
					objectFieldValue3.substring(1))),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, objectFieldValue1,
			escape(
				String.format(
					"startswith(%s/%s/%s,'%s')", objectRelationship1.getName(),
					objectRelationship2.getName(), OBJECT_FIELD_NAME_3,
					objectFieldValue3.substring(0, 2))),
			getObjectDefinition1());

		// One to many relationship, system object field

		objectEntry3ExternalReferenceCode =
			objectEntry3.getExternalReferenceCode();

		assertFilterString(
			OBJECT_FIELD_NAME_1, objectFieldValue1,
			escape(
				String.format(
					"contains(%s/%s/externalReferenceCode,'%s')",
					objectRelationship1.getName(),
					objectRelationship2.getName(),
					objectEntry3ExternalReferenceCode.substring(1))),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, objectFieldValue1,
			escape(
				String.format(
					"startswith(%s/%s/externalReferenceCode,'%s')",
					objectRelationship1.getName(),
					objectRelationship2.getName(),
					objectEntry3ExternalReferenceCode.substring(0, 2))),
			getObjectDefinition1());

		// One to many relationship (other side), custom object field

		assertFilterString(
			OBJECT_FIELD_NAME_3, objectFieldValue3,
			escape(
				String.format(
					"contains(%s/%s/%s,'%s')", objectRelationship2.getName(),
					objectRelationship1.getName(), OBJECT_FIELD_NAME_1,
					objectFieldValue1.substring(1))),
			getObjectDefinition3());
		assertFilterString(
			OBJECT_FIELD_NAME_3, objectFieldValue3,
			escape(
				String.format(
					"startswith(%s/%s/%s,'%s')", objectRelationship2.getName(),
					objectRelationship1.getName(), OBJECT_FIELD_NAME_1,
					objectFieldValue1.substring(0, 2))),
			getObjectDefinition3());

		// One to many relationship (other side), system object field

		objectEntry1ExternalReferenceCode =
			objectEntry1.getExternalReferenceCode();

		assertFilterString(
			OBJECT_FIELD_NAME_3, objectFieldValue3,
			escape(
				String.format(
					"contains(%s/%s/externalReferenceCode,'%s')",
					objectRelationship2.getName(),
					objectRelationship1.getName(),
					objectEntry1ExternalReferenceCode.substring(1))),
			getObjectDefinition3());
		assertFilterString(
			OBJECT_FIELD_NAME_3, objectFieldValue3,
			escape(
				String.format(
					"startswith(%s/%s/externalReferenceCode,'%s')",
					objectRelationship2.getName(),
					objectRelationship1.getName(),
					objectEntry1ExternalReferenceCode.substring(0, 2))),
			getObjectDefinition3());
	}

	@Test
	public void testFilterObjectEntriesByRelatedLocalizedObjectEntries()
		throws Exception {

		ObjectDefinition objectDefinition1 = publishLocalizedObjectDefinition(
			OBJECT_FIELD_NAME_1);

		ObjectEntry objectEntry1 = _addLocalizedObjectEntry(
			objectDefinition1, OBJECT_FIELD_NAME_1,
			new String[] {
				String.valueOf(OBJECT_FIELD_VALUE_1),
				String.valueOf(OBJECT_FIELD_VALUE_2)
			});

		ObjectDefinition objectDefinition2 = publishLocalizedObjectDefinition(
			OBJECT_FIELD_NAME_2);

		ObjectEntry objectEntry2 = _addLocalizedObjectEntry(
			objectDefinition2, OBJECT_FIELD_NAME_2,
			new String[] {
				String.valueOf(OBJECT_FIELD_VALUE_3),
				String.valueOf(OBJECT_FIELD_VALUE_4)
			});

		ObjectRelationship objectRelationship =
			addObjectRelationshipAndRelateObjectEntries(
				objectDefinition1, objectDefinition2,
				objectEntry1.getPrimaryKey(), objectEntry2.getPrimaryKey(),
				ObjectRelationshipConstants.TYPE_ONE_TO_MANY);

		try {

			// Many to one relationship

			_testFilterObjectEntriesByRelatedLocalizedObjectEntries(
				String.format(
					"%s/%s eq '%s'", objectRelationship.getName(),
					OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1),
				"en-US", objectDefinition2, objectEntry2);

			_testFilterObjectEntriesByRelatedLocalizedObjectEntries(
				String.format(
					"%s/%s eq '%s'", objectRelationship.getName(),
					OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_2),
				"es-ES", objectDefinition2, objectEntry2);

			// One to many relationship

			_testFilterObjectEntriesByRelatedLocalizedObjectEntries(
				String.format(
					"%s/%s eq '%s'", objectRelationship.getName(),
					OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_3),
				"en-US", objectDefinition1, objectEntry1);

			_testFilterObjectEntriesByRelatedLocalizedObjectEntries(
				String.format(
					"%s/%s eq '%s'", objectRelationship.getName(),
					OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_4),
				"es-ES", objectDefinition1, objectEntry1);
		}
		finally {
			objectRelationshipLocalService.deleteObjectRelationship(
				objectRelationship);

			objectDefinitionLocalService.deleteObjectDefinition(
				objectDefinition1);
			objectDefinitionLocalService.deleteObjectDefinition(
				objectDefinition2);
		}
	}

	@Test
	public void testFilterObjectEntriesByRelatedSystemObjectEntriesFields()
		throws Exception {

		// Many to many relationship

		objectEntry1 = ObjectEntryTestUtil.addObjectEntry(
			getObjectDefinition1(), OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1);

		userAccountJSONObject = UserAccountTestUtil.addUserAccountJSONObject(
			systemObjectDefinitionManager,
			HashMapBuilder.<String, Serializable>put(
				OBJECT_FIELD_NAME_2, String.valueOf(OBJECT_FIELD_VALUE_2)
			).build());

		objectRelationship1 = addObjectRelationshipAndRelateObjectEntries(
			getObjectDefinition1(), userSystemObjectDefinition,
			objectEntry1.getPrimaryKey(), userAccountJSONObject.getLong("id"),
			ObjectRelationshipConstants.TYPE_MANY_TO_MANY);

		_testFilterObjectEntriesByRelatedSystemObjectEntriesFields(
			escape(
				String.format(
					"%s/%s eq '%s'", objectRelationship1.getName(),
					OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2)),
			getObjectDefinition1());

		objectRelationshipLocalService.deleteObjectRelationship(
			objectRelationship1);

		// One to many relationship

		objectRelationship1 = addObjectRelationshipAndRelateObjectEntries(
			userSystemObjectDefinition, getObjectDefinition1(),
			userAccountJSONObject.getLong("id"), objectEntry1.getPrimaryKey(),
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);

		_testFilterObjectEntriesByRelatedSystemObjectEntriesFields(
			escape(
				String.format(
					"%s/%s eq '%s'", objectRelationship1.getName(),
					OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2)),
			getObjectDefinition1());

		String objectRelationshipERCObjectFieldName =
			ObjectFieldSettingUtil.getValue(
				ObjectFieldSettingConstants.
					NAME_OBJECT_RELATIONSHIP_ERC_OBJECT_FIELD_NAME,
				objectFieldLocalService.getObjectField(
					objectRelationship1.getObjectFieldId2()));

		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			escape(
				String.format(
					"%s eq '%s'", objectRelationshipERCObjectFieldName,
					userAccountJSONObject.getString("externalReferenceCode"))),
			getObjectDefinition1());
	}

	@Test
	public void testGetObjectEntriesPageWithFilterOnManyToManyRelatedLocalizedObjectField()
		throws Exception {

		ObjectDefinition objectDefinition1 = publishLocalizedObjectDefinition(
			OBJECT_FIELD_NAME_TEXT);
		String objectEntryValue1 = RandomTestUtil.randomString();

		ObjectEntry objectEntry1 = ObjectEntryTestUtil.addObjectEntry(
			objectDefinition1,
			HashMapBuilder.<String, Serializable>put(
				OBJECT_FIELD_NAME_TEXT, objectEntryValue1
			).put(
				OBJECT_FIELD_NAME_TEXT + "_i18n",
				HashMapBuilder.<String, Serializable>put(
					"en_US", objectEntryValue1
				).build()
			).build());

		ObjectDefinition objectDefinition2 = publishLocalizedObjectDefinition(
			OBJECT_FIELD_NAME_TEXT);
		String objectEntryValue2 = RandomTestUtil.randomString();

		ObjectEntry objectEntry2 = ObjectEntryTestUtil.addObjectEntry(
			objectDefinition2,
			HashMapBuilder.<String, Serializable>put(
				OBJECT_FIELD_NAME_TEXT, objectEntryValue2
			).put(
				OBJECT_FIELD_NAME_TEXT + "_i18n",
				HashMapBuilder.<String, Serializable>put(
					"en_US", objectEntryValue2
				).build()
			).build());

		ObjectRelationship objectRelationship =
			addObjectRelationshipAndRelateObjectEntries(
				objectDefinition1, objectDefinition2,
				objectEntry1.getObjectEntryId(),
				objectEntry2.getObjectEntryId(),
				ObjectRelationshipConstants.TYPE_MANY_TO_MANY);

		_assertObjectEntriesPageWithFilterOnRelatedLocalizedObjectField(
			objectEntryValue1, objectDefinition1, objectRelationship,
			objectEntryValue2);
		_assertObjectEntriesPageWithFilterOnRelatedLocalizedObjectField(
			objectEntryValue2, objectDefinition2, objectRelationship,
			objectEntryValue1);

		objectRelationshipLocalService.deleteObjectRelationship(
			objectRelationship);

		objectDefinitionLocalService.deleteObjectDefinition(objectDefinition1);
		objectDefinitionLocalService.deleteObjectDefinition(objectDefinition2);
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

		String filterString = StringBundler.concat(
			objectRelationship.getName(), "/", OBJECT_FIELD_NAME_TEXT, " eq '",
			relatedObjectFieldValue, "'");

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			StringBundler.concat(
				objectDefinition.getRESTContextPath(), "?filter=",
				URLCodec.encodeURL(filterString), "&nestedFields=",
				objectRelationship.getName()),
			Http.Method.GET);

		JSONArray itemsJSONArray = jsonObject.getJSONArray("items");

		Assert.assertEquals(
			itemsJSONArray.toString(), 1, itemsJSONArray.length());

		JSONObject itemJSONObject = itemsJSONArray.getJSONObject(0);

		Assert.assertEquals(
			expectedObjectFieldValue,
			itemJSONObject.getString(OBJECT_FIELD_NAME_TEXT));
		Assert.assertEquals(
			relatedObjectFieldValue,
			JSONUtil.getValueAsString(
				itemJSONObject, "JSONArray/" + objectRelationship.getName(),
				"Object/0", "Object/" + OBJECT_FIELD_NAME_TEXT));
	}

	private void _testFilterObjectEntriesByRelatedLocalizedObjectEntries(
			String filterString, String languageId,
			ObjectDefinition objectDefinition, ObjectEntry objectEntry)
		throws Exception {

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			objectDefinition.getRESTContextPath() + "?filter=" +
				escape(filterString),
			HashMapBuilder.put(
				"Accept-Language", languageId
			).build(),
			Http.Method.GET);

		JSONArray itemsJSONArray = jsonObject.getJSONArray("items");

		Assert.assertEquals(1, itemsJSONArray.length());

		JSONObject itemJSONObject = itemsJSONArray.getJSONObject(0);

		Assert.assertEquals(
			itemJSONObject.getLong("id"), objectEntry.getObjectEntryId());
	}

	private void _testFilterObjectEntriesByRelatedSystemObjectEntriesFields(
			String filterString, ObjectDefinition objectDefinition)
		throws Exception {

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			objectDefinition.getRESTContextPath() + "?filter=" + filterString,
			Http.Method.GET);

		Assert.assertEquals(
			"Filtering is not supported for system objects",
			jsonObject.getString("title"));
		Assert.assertEquals("BAD_REQUEST", jsonObject.getString("status"));
	}

	private static final String _TAG_2 = StringUtil.toLowerCase(
		RandomTestUtil.randomString());

	private static final String _TAG_3 = StringUtil.toLowerCase(
		RandomTestUtil.randomString());

}