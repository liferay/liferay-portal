/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.batch.planner.rest.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.batch.planner.rest.client.dto.v1_0.Field;
import com.liferay.batch.planner.rest.client.pagination.Page;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.constants.ObjectFieldSettingConstants;
import com.liferay.object.constants.ObjectRelationshipConstants;
import com.liferay.object.field.setting.util.ObjectFieldSettingUtil;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.rest.test.util.ObjectRelationshipTestUtil;
import com.liferay.object.service.ObjectFieldLocalServiceUtil;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.FeatureFlag;

import java.util.Collections;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Matija Petanjek
 */
@RunWith(Arquillian.class)
public class FieldResourceTest extends BaseFieldResourceTestCase {

	@FeatureFlag("LPD-17564")
	@Override
	@Test
	public void testGetPlanInternalClassNameKeyFieldsPage() throws Exception {
		String fieldName = "x" + RandomTestUtil.randomString();

		ObjectDefinition objectDefinition1 =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				ObjectDefinitionTestUtil.getRandomName(),
				Collections.singletonList(
					ObjectFieldUtil.createObjectField(
						ObjectFieldConstants.BUSINESS_TYPE_TEXT,
						ObjectFieldConstants.DB_TYPE_STRING, true, true, null,
						RandomTestUtil.randomString(), fieldName, false)),
				ObjectDefinitionConstants.SCOPE_COMPANY);

		ObjectDefinition objectDefinition2 =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				ObjectDefinitionTestUtil.getRandomName(),
				Collections.singletonList(
					ObjectFieldUtil.createObjectField(
						ObjectFieldConstants.BUSINESS_TYPE_TEXT,
						ObjectFieldConstants.DB_TYPE_STRING, true, true, null,
						RandomTestUtil.randomString(),
						"x" + RandomTestUtil.randomString(), false)),
				ObjectDefinitionConstants.SCOPE_COMPANY);

		ObjectRelationship manyToManyObjectRelationship =
			ObjectRelationshipTestUtil.addObjectRelationship(
				objectDefinition1, objectDefinition2,
				TestPropsValues.getUserId(),
				ObjectRelationshipConstants.TYPE_MANY_TO_MANY);
		ObjectRelationship manyToOneObjectRelationship1 =
			ObjectRelationshipTestUtil.addObjectRelationship(
				objectDefinition2, objectDefinition1,
				TestPropsValues.getUserId(),
				ObjectRelationshipConstants.TYPE_ONE_TO_MANY);
		ObjectRelationship manyToOneObjectRelationship2 =
			ObjectRelationshipTestUtil.addObjectRelationship(
				objectDefinition2, objectDefinition1,
				TestPropsValues.getUserId(),
				ObjectRelationshipConstants.TYPE_ONE_TO_MANY);
		ObjectRelationship oneToManyObjectRelationship =
			ObjectRelationshipTestUtil.addObjectRelationship(
				objectDefinition1, objectDefinition2,
				TestPropsValues.getUserId(),
				ObjectRelationshipConstants.TYPE_ONE_TO_MANY);

		Page<Field> page = fieldResource.getPlanInternalClassNameKeyFieldsPage(
			"com.liferay.object.rest.dto.v1_0.ObjectEntry%23" +
				objectDefinition1.getName(),
			null);

		String manyToOneObjectRelationship1Name =
			manyToOneObjectRelationship1.getName();

		String manyToOneObjectRelationship1PKObjectFieldName =
			StringBundler.concat(
				"r_", manyToOneObjectRelationship1Name, "_",
				objectDefinition2.getPKObjectFieldName());

		String manyToOneObjectRelationship1ERCObjectFieldName =
			ObjectFieldSettingUtil.getValue(
				ObjectFieldSettingConstants.
					NAME_OBJECT_RELATIONSHIP_ERC_OBJECT_FIELD_NAME,
				ObjectFieldLocalServiceUtil.getObjectField(
					objectDefinition1.getObjectDefinitionId(),
					manyToOneObjectRelationship1PKObjectFieldName));

		String manyToOneObjectRelationship2Name =
			manyToOneObjectRelationship2.getName();

		String manyToOneObjectRelationship2PKObjectFieldName =
			StringBundler.concat(
				"r_", manyToOneObjectRelationship2Name, "_",
				objectDefinition2.getPKObjectFieldName());

		String manyToOneObjectRelationship2ERCObjectFieldName =
			ObjectFieldSettingUtil.getValue(
				ObjectFieldSettingConstants.
					NAME_OBJECT_RELATIONSHIP_ERC_OBJECT_FIELD_NAME,
				ObjectFieldLocalServiceUtil.getObjectField(
					objectDefinition1.getObjectDefinitionId(),
					manyToOneObjectRelationship2PKObjectFieldName));

		assertEqualsIgnoringOrder(
			ListUtil.fromArray(
				_toField(null, "defaultLanguageId", false, "string", null),
				_toField(null, "displayDate", false, "string", null),
				_toField(null, "expirationDate", false, "string", null),
				_toField(null, "externalReferenceCode", false, "string", null),
				_toField(null, "friendlyUrlPath", false, "string", null),
				_toField(null, "friendlyUrlPath_i18n", false, "object", null),
				_toField(null, "keywords", false, "array", "CSV"),
				_toField(
					null, "objectEntryFolderExternalReferenceCode", false,
					"string", null),
				_toField(null, "objectEntryFolderId", false, "integer", null),
				_toField(null, "permissions", false, "array", null),
				_toField(null, "removedBy", false, null, null),
				_toField(null, "reviewDate", false, "string", null),
				_toField(null, "taxonomyCategoryIds", false, "array", "CSV"),
				_toField(null, fieldName, false, "string", null),
				_toField(
					null, manyToManyObjectRelationship.getName(), false,
					"array", null),
				_toField(
					null, oneToManyObjectRelationship.getName(), false, "array",
					null),
				_toField(
					manyToOneObjectRelationship1Name,
					manyToOneObjectRelationship1Name, false, "object", null),
				_toField(
					manyToOneObjectRelationship1Name,
					manyToOneObjectRelationship1PKObjectFieldName, false,
					"integer", null),
				_toField(
					manyToOneObjectRelationship1Name,
					manyToOneObjectRelationship1ERCObjectFieldName, false,
					"string", null),
				_toField(
					manyToOneObjectRelationship2Name,
					manyToOneObjectRelationship2Name, false, "object", null),
				_toField(
					manyToOneObjectRelationship2Name,
					manyToOneObjectRelationship2PKObjectFieldName, false,
					"integer", null),
				_toField(
					manyToOneObjectRelationship2Name,
					manyToOneObjectRelationship2ERCObjectFieldName, false,
					"string", null)),
			ListUtil.fromCollection(page.getItems()));
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {
			"anyOfGroup", "name", "required", "type", "unsupportedFormats"
		};
	}

	private Field _toField(
		String anyOfGroup, String name, boolean required, String type,
		String unsupportedFormats) {

		Field field = new Field();

		field.setAnyOfGroup(anyOfGroup);
		field.setName(name);
		field.setRequired(required);
		field.setType(type);

		if (unsupportedFormats != null) {
			field.setUnsupportedFormats(StringUtil.split(unsupportedFormats));
		}

		return field;
	}

}