/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.admin.rest.client.serdes.v1_0;

import com.liferay.object.admin.rest.client.dto.v1_0.ObjectField;
import com.liferay.object.admin.rest.client.dto.v1_0.ObjectFieldSetting;
import com.liferay.object.admin.rest.client.json.BaseJSONParser;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import jakarta.annotation.Generated;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class ObjectFieldSerDes {

	public static ObjectField toDTO(String json) {
		ObjectFieldJSONParser objectFieldJSONParser =
			new ObjectFieldJSONParser();

		return objectFieldJSONParser.parseToDTO(json);
	}

	public static ObjectField[] toDTOs(String json) {
		ObjectFieldJSONParser objectFieldJSONParser =
			new ObjectFieldJSONParser();

		return objectFieldJSONParser.parseToDTOs(json);
	}

	public static String toJSON(ObjectField objectField) {
		if (objectField == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (objectField.getDBType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"DBType\": ");

			sb.append("\"");

			sb.append(objectField.getDBType());

			sb.append("\"");
		}

		if (objectField.getActions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"actions\": ");

			sb.append(_toJSON(objectField.getActions()));
		}

		if (objectField.getBusinessType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"businessType\": ");

			sb.append("\"");

			sb.append(objectField.getBusinessType());

			sb.append("\"");
		}

		if (objectField.getDefaultValue() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"defaultValue\": ");

			sb.append("\"");

			sb.append(_escape(objectField.getDefaultValue()));

			sb.append("\"");
		}

		if (objectField.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(objectField.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (objectField.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(objectField.getId());
		}

		if (objectField.getIndexed() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"indexed\": ");

			sb.append(objectField.getIndexed());
		}

		if (objectField.getIndexedAsKeyword() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"indexedAsKeyword\": ");

			sb.append(objectField.getIndexedAsKeyword());
		}

		if (objectField.getIndexedLanguageId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"indexedLanguageId\": ");

			sb.append("\"");

			sb.append(_escape(objectField.getIndexedLanguageId()));

			sb.append("\"");
		}

		if (objectField.getLabel() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"label\": ");

			sb.append(_toJSON(objectField.getLabel()));
		}

		if (objectField.getListTypeDefinitionExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"listTypeDefinitionExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(
					objectField.getListTypeDefinitionExternalReferenceCode()));

			sb.append("\"");
		}

		if (objectField.getListTypeDefinitionId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"listTypeDefinitionId\": ");

			sb.append(objectField.getListTypeDefinitionId());
		}

		if (objectField.getLocalized() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"localized\": ");

			sb.append(objectField.getLocalized());
		}

		if (objectField.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(objectField.getName()));

			sb.append("\"");
		}

		if (objectField.getObjectDefinitionExternalReferenceCode1() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"objectDefinitionExternalReferenceCode1\": ");

			sb.append("\"");

			sb.append(
				_escape(
					objectField.getObjectDefinitionExternalReferenceCode1()));

			sb.append("\"");
		}

		if (objectField.getObjectFieldSettings() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"objectFieldSettings\": ");

			sb.append("[");

			for (int i = 0; i < objectField.getObjectFieldSettings().length;
				 i++) {

				sb.append(
					String.valueOf(objectField.getObjectFieldSettings()[i]));

				if ((i + 1) < objectField.getObjectFieldSettings().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (objectField.getObjectRelationshipExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"objectRelationshipExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(
					objectField.getObjectRelationshipExternalReferenceCode()));

			sb.append("\"");
		}

		if (objectField.getReadOnly() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"readOnly\": ");

			sb.append("\"");

			sb.append(objectField.getReadOnly());

			sb.append("\"");
		}

		if (objectField.getReadOnlyConditionExpression() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"readOnlyConditionExpression\": ");

			sb.append("\"");

			sb.append(_escape(objectField.getReadOnlyConditionExpression()));

			sb.append("\"");
		}

		if (objectField.getRelationshipType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"relationshipType\": ");

			sb.append("\"");

			sb.append(objectField.getRelationshipType());

			sb.append("\"");
		}

		if (objectField.getRequired() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"required\": ");

			sb.append(objectField.getRequired());
		}

		if (objectField.getState() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"state\": ");

			sb.append(objectField.getState());
		}

		if (objectField.getSystem() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"system\": ");

			sb.append(objectField.getSystem());
		}

		if (objectField.getType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append("\"");

			sb.append(objectField.getType());

			sb.append("\"");
		}

		if (objectField.getUnique() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"unique\": ");

			sb.append(objectField.getUnique());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		ObjectFieldJSONParser objectFieldJSONParser =
			new ObjectFieldJSONParser();

		return objectFieldJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(ObjectField objectField) {
		if (objectField == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (objectField.getDBType() == null) {
			map.put("DBType", null);
		}
		else {
			map.put("DBType", String.valueOf(objectField.getDBType()));
		}

		if (objectField.getActions() == null) {
			map.put("actions", null);
		}
		else {
			map.put("actions", String.valueOf(objectField.getActions()));
		}

		if (objectField.getBusinessType() == null) {
			map.put("businessType", null);
		}
		else {
			map.put(
				"businessType", String.valueOf(objectField.getBusinessType()));
		}

		if (objectField.getDefaultValue() == null) {
			map.put("defaultValue", null);
		}
		else {
			map.put(
				"defaultValue", String.valueOf(objectField.getDefaultValue()));
		}

		if (objectField.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(objectField.getExternalReferenceCode()));
		}

		if (objectField.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(objectField.getId()));
		}

		if (objectField.getIndexed() == null) {
			map.put("indexed", null);
		}
		else {
			map.put("indexed", String.valueOf(objectField.getIndexed()));
		}

		if (objectField.getIndexedAsKeyword() == null) {
			map.put("indexedAsKeyword", null);
		}
		else {
			map.put(
				"indexedAsKeyword",
				String.valueOf(objectField.getIndexedAsKeyword()));
		}

		if (objectField.getIndexedLanguageId() == null) {
			map.put("indexedLanguageId", null);
		}
		else {
			map.put(
				"indexedLanguageId",
				String.valueOf(objectField.getIndexedLanguageId()));
		}

		if (objectField.getLabel() == null) {
			map.put("label", null);
		}
		else {
			map.put("label", String.valueOf(objectField.getLabel()));
		}

		if (objectField.getListTypeDefinitionExternalReferenceCode() == null) {
			map.put("listTypeDefinitionExternalReferenceCode", null);
		}
		else {
			map.put(
				"listTypeDefinitionExternalReferenceCode",
				String.valueOf(
					objectField.getListTypeDefinitionExternalReferenceCode()));
		}

		if (objectField.getListTypeDefinitionId() == null) {
			map.put("listTypeDefinitionId", null);
		}
		else {
			map.put(
				"listTypeDefinitionId",
				String.valueOf(objectField.getListTypeDefinitionId()));
		}

		if (objectField.getLocalized() == null) {
			map.put("localized", null);
		}
		else {
			map.put("localized", String.valueOf(objectField.getLocalized()));
		}

		if (objectField.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(objectField.getName()));
		}

		if (objectField.getObjectDefinitionExternalReferenceCode1() == null) {
			map.put("objectDefinitionExternalReferenceCode1", null);
		}
		else {
			map.put(
				"objectDefinitionExternalReferenceCode1",
				String.valueOf(
					objectField.getObjectDefinitionExternalReferenceCode1()));
		}

		if (objectField.getObjectFieldSettings() == null) {
			map.put("objectFieldSettings", null);
		}
		else {
			map.put(
				"objectFieldSettings",
				String.valueOf(objectField.getObjectFieldSettings()));
		}

		if (objectField.getObjectRelationshipExternalReferenceCode() == null) {
			map.put("objectRelationshipExternalReferenceCode", null);
		}
		else {
			map.put(
				"objectRelationshipExternalReferenceCode",
				String.valueOf(
					objectField.getObjectRelationshipExternalReferenceCode()));
		}

		if (objectField.getReadOnly() == null) {
			map.put("readOnly", null);
		}
		else {
			map.put("readOnly", String.valueOf(objectField.getReadOnly()));
		}

		if (objectField.getReadOnlyConditionExpression() == null) {
			map.put("readOnlyConditionExpression", null);
		}
		else {
			map.put(
				"readOnlyConditionExpression",
				String.valueOf(objectField.getReadOnlyConditionExpression()));
		}

		if (objectField.getRelationshipType() == null) {
			map.put("relationshipType", null);
		}
		else {
			map.put(
				"relationshipType",
				String.valueOf(objectField.getRelationshipType()));
		}

		if (objectField.getRequired() == null) {
			map.put("required", null);
		}
		else {
			map.put("required", String.valueOf(objectField.getRequired()));
		}

		if (objectField.getState() == null) {
			map.put("state", null);
		}
		else {
			map.put("state", String.valueOf(objectField.getState()));
		}

		if (objectField.getSystem() == null) {
			map.put("system", null);
		}
		else {
			map.put("system", String.valueOf(objectField.getSystem()));
		}

		if (objectField.getType() == null) {
			map.put("type", null);
		}
		else {
			map.put("type", String.valueOf(objectField.getType()));
		}

		if (objectField.getUnique() == null) {
			map.put("unique", null);
		}
		else {
			map.put("unique", String.valueOf(objectField.getUnique()));
		}

		return map;
	}

	public static class ObjectFieldJSONParser
		extends BaseJSONParser<ObjectField> {

		@Override
		protected ObjectField createDTO() {
			return new ObjectField();
		}

		@Override
		protected ObjectField[] createDTOArray(int size) {
			return new ObjectField[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "DBType")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "actions")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "businessType")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "defaultValue")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "indexed")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "indexedAsKeyword")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "indexedLanguageId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "label")) {
				return true;
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"listTypeDefinitionExternalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "listTypeDefinitionId")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "localized")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"objectDefinitionExternalReferenceCode1")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "objectFieldSettings")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"objectRelationshipExternalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "readOnly")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "readOnlyConditionExpression")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "relationshipType")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "required")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "state")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "system")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "unique")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			ObjectField objectField, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "DBType")) {
				if (jsonParserFieldValue != null) {
					objectField.setDBType(
						ObjectField.DBType.create(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "actions")) {
				if (jsonParserFieldValue != null) {
					objectField.setActions(
						(Map<String, Map<String, String>>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "businessType")) {
				if (jsonParserFieldValue != null) {
					objectField.setBusinessType(
						ObjectField.BusinessType.create(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "defaultValue")) {
				if (jsonParserFieldValue != null) {
					objectField.setDefaultValue((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					objectField.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					objectField.setId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "indexed")) {
				if (jsonParserFieldValue != null) {
					objectField.setIndexed((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "indexedAsKeyword")) {
				if (jsonParserFieldValue != null) {
					objectField.setIndexedAsKeyword(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "indexedLanguageId")) {
				if (jsonParserFieldValue != null) {
					objectField.setIndexedLanguageId(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "label")) {
				if (jsonParserFieldValue != null) {
					objectField.setLabel(
						(Map<String, String>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"listTypeDefinitionExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					objectField.setListTypeDefinitionExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "listTypeDefinitionId")) {

				if (jsonParserFieldValue != null) {
					objectField.setListTypeDefinitionId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "localized")) {
				if (jsonParserFieldValue != null) {
					objectField.setLocalized((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					objectField.setName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"objectDefinitionExternalReferenceCode1")) {

				if (jsonParserFieldValue != null) {
					objectField.setObjectDefinitionExternalReferenceCode1(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "objectFieldSettings")) {

				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					ObjectFieldSetting[] objectFieldSettingsArray =
						new ObjectFieldSetting[jsonParserFieldValues.length];

					for (int i = 0; i < objectFieldSettingsArray.length; i++) {
						objectFieldSettingsArray[i] =
							ObjectFieldSettingSerDes.toDTO(
								(String)jsonParserFieldValues[i]);
					}

					objectField.setObjectFieldSettings(
						objectFieldSettingsArray);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"objectRelationshipExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					objectField.setObjectRelationshipExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "readOnly")) {
				if (jsonParserFieldValue != null) {
					objectField.setReadOnly(
						ObjectField.ReadOnly.create(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "readOnlyConditionExpression")) {

				if (jsonParserFieldValue != null) {
					objectField.setReadOnlyConditionExpression(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "relationshipType")) {
				if (jsonParserFieldValue != null) {
					objectField.setRelationshipType(
						ObjectField.RelationshipType.create(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "required")) {
				if (jsonParserFieldValue != null) {
					objectField.setRequired((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "state")) {
				if (jsonParserFieldValue != null) {
					objectField.setState((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "system")) {
				if (jsonParserFieldValue != null) {
					objectField.setSystem((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				if (jsonParserFieldValue != null) {
					objectField.setType(
						ObjectField.Type.create((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "unique")) {
				if (jsonParserFieldValue != null) {
					objectField.setUnique((Boolean)jsonParserFieldValue);
				}
			}
		}

	}

	private static String _escape(Object object) {
		String string = String.valueOf(object);

		for (String[] strings : BaseJSONParser.JSON_ESCAPE_STRINGS) {
			string = string.replace(strings[0], strings[1]);
		}

		return string;
	}

	private static String _toJSON(Map<String, ?> map) {
		StringBuilder sb = new StringBuilder("{");

		@SuppressWarnings("unchecked")
		Set set = map.entrySet();

		@SuppressWarnings("unchecked")
		Iterator<Map.Entry<String, ?>> iterator = set.iterator();

		while (iterator.hasNext()) {
			Map.Entry<String, ?> entry = iterator.next();

			sb.append("\"");
			sb.append(entry.getKey());
			sb.append("\": ");

			Object value = entry.getValue();

			sb.append(_toJSON(value));

			if (iterator.hasNext()) {
				sb.append(", ");
			}
		}

		sb.append("}");

		return sb.toString();
	}

	private static String _toJSON(Object value) {
		if (value == null) {
			return "null";
		}

		if (value instanceof Map) {
			return _toJSON((Map)value);
		}

		Class<?> clazz = value.getClass();

		if (clazz.isArray()) {
			StringBuilder sb = new StringBuilder("[");

			Object[] values = (Object[])value;

			for (int i = 0; i < values.length; i++) {
				sb.append(_toJSON(values[i]));

				if ((i + 1) < values.length) {
					sb.append(", ");
				}
			}

			sb.append("]");

			return sb.toString();
		}

		if (value instanceof String) {
			return "\"" + _escape(value) + "\"";
		}

		return String.valueOf(value);
	}

}