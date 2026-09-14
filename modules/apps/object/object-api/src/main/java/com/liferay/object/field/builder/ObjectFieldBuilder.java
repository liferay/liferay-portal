/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.field.builder;

import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectFieldSetting;
import com.liferay.object.service.ObjectFieldLocalServiceUtil;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author Feliphe Marinho
 */
public class ObjectFieldBuilder {

	public ObjectFieldBuilder() {
		objectField.setReadOnly(ObjectFieldConstants.READ_ONLY_FALSE);
		objectField.setObjectFieldSettings(Collections.emptyList());
	}

	public ObjectField build() {
		return objectField;
	}

	public ObjectFieldBuilder businessType(String businessType) {
		objectField.setBusinessType(businessType);

		return this;
	}

	public ObjectFieldBuilder dbColumnName(String dbColumnName) {
		objectField.setDBColumnName(dbColumnName);

		return this;
	}

	public ObjectFieldBuilder dbTableName(String dbTableName) {
		objectField.setDBTableName(dbTableName);

		return this;
	}

	public ObjectFieldBuilder dbType(String dbType) {
		objectField.setDBType(dbType);

		return this;
	}

	public ObjectFieldBuilder descriptionMap(
		Map<Locale, String> descriptionMap) {

		objectField.setDescriptionMap(descriptionMap);

		return this;
	}

	public ObjectFieldBuilder externalReferenceCode(
		String externalReferenceCode) {

		objectField.setExternalReferenceCode(externalReferenceCode);

		return this;
	}

	public ObjectFieldBuilder indexed(boolean indexed) {
		objectField.setIndexed(indexed);

		return this;
	}

	public ObjectFieldBuilder indexedAsKeyword(boolean indexedAsKeyword) {
		objectField.setIndexedAsKeyword(indexedAsKeyword);

		return this;
	}

	public ObjectFieldBuilder indexedLanguageId(String indexedLanguageId) {
		objectField.setIndexedLanguageId(indexedLanguageId);

		return this;
	}

	public ObjectFieldBuilder labelMap(Map<Locale, String> labelMap) {
		objectField.setLabelMap(labelMap);

		return this;
	}

	public ObjectFieldBuilder listTypeDefinitionId(long listTypeDefinitionId) {
		objectField.setListTypeDefinitionId(listTypeDefinitionId);

		return this;
	}

	public ObjectFieldBuilder localized(boolean localized) {
		objectField.setLocalized(localized);

		return this;
	}

	public ObjectFieldBuilder name(String name) {
		objectField.setName(name);

		return this;
	}

	public ObjectFieldBuilder objectDefinitionId(long objectDefinitionId) {
		objectField.setObjectDefinitionId(objectDefinitionId);

		return this;
	}

	public ObjectFieldBuilder objectFieldId(long objectFieldId) {
		objectField.setObjectFieldId(objectFieldId);

		return this;
	}

	public ObjectFieldBuilder objectFieldSettings(
		List<ObjectFieldSetting> objectFieldSettings) {

		objectField.setObjectFieldSettings(objectFieldSettings);

		return this;
	}

	public ObjectFieldBuilder readOnly(String readOnly) {
		objectField.setReadOnly(readOnly);

		return this;
	}

	public ObjectFieldBuilder readOnlyConditionExpression(
		String readOnlyConditionExpression) {

		objectField.setReadOnlyConditionExpression(readOnlyConditionExpression);

		return this;
	}

	public ObjectFieldBuilder relationshipType(String relationshipType) {
		objectField.setRelationshipType(relationshipType);

		return this;
	}

	public ObjectFieldBuilder required(boolean required) {
		objectField.setRequired(required);

		return this;
	}

	public ObjectFieldBuilder state(boolean state) {
		objectField.setState(state);

		return this;
	}

	public ObjectFieldBuilder system(boolean system) {
		objectField.setSystem(system);

		return this;
	}

	public ObjectFieldBuilder userId(long userId) {
		objectField.setUserId(userId);

		return this;
	}

	protected final ObjectField objectField =
		ObjectFieldLocalServiceUtil.createObjectField(0);

}