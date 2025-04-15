/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.admin.rest.client.serdes.v1_0;

import com.liferay.object.admin.rest.client.dto.v1_0.ObjectValidationRule;
import com.liferay.object.admin.rest.client.dto.v1_0.ObjectValidationRuleSetting;
import com.liferay.object.admin.rest.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class ObjectValidationRuleSerDes {

	public static ObjectValidationRule toDTO(String json) {
		ObjectValidationRuleJSONParser objectValidationRuleJSONParser =
			new ObjectValidationRuleJSONParser();

		return objectValidationRuleJSONParser.parseToDTO(json);
	}

	public static ObjectValidationRule[] toDTOs(String json) {
		ObjectValidationRuleJSONParser objectValidationRuleJSONParser =
			new ObjectValidationRuleJSONParser();

		return objectValidationRuleJSONParser.parseToDTOs(json);
	}

	public static String toJSON(ObjectValidationRule objectValidationRule) {
		if (objectValidationRule == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (objectValidationRule.getActions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"actions\": ");

			sb.append(_toJSON(objectValidationRule.getActions()));
		}

		if (objectValidationRule.getActive() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"active\": ");

			sb.append(objectValidationRule.getActive());
		}

		if (objectValidationRule.getDateCreated() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateCreated\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					objectValidationRule.getDateCreated()));

			sb.append("\"");
		}

		if (objectValidationRule.getDateModified() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateModified\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					objectValidationRule.getDateModified()));

			sb.append("\"");
		}

		if (objectValidationRule.getEngine() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"engine\": ");

			sb.append("\"");

			sb.append(_escape(objectValidationRule.getEngine()));

			sb.append("\"");
		}

		if (objectValidationRule.getEngineLabel() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"engineLabel\": ");

			sb.append("\"");

			sb.append(_escape(objectValidationRule.getEngineLabel()));

			sb.append("\"");
		}

		if (objectValidationRule.getErrorLabel() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"errorLabel\": ");

			sb.append(_toJSON(objectValidationRule.getErrorLabel()));
		}

		if (objectValidationRule.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(objectValidationRule.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (objectValidationRule.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(objectValidationRule.getId());
		}

		if (objectValidationRule.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append(_toJSON(objectValidationRule.getName()));
		}

		if (objectValidationRule.getObjectDefinitionExternalReferenceCode() !=
				null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"objectDefinitionExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(
					objectValidationRule.
						getObjectDefinitionExternalReferenceCode()));

			sb.append("\"");
		}

		if (objectValidationRule.getObjectDefinitionId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"objectDefinitionId\": ");

			sb.append(objectValidationRule.getObjectDefinitionId());
		}

		if (objectValidationRule.getObjectValidationRuleSettings() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"objectValidationRuleSettings\": ");

			sb.append("[");

			for (int i = 0;
				 i <
					 objectValidationRule.
						 getObjectValidationRuleSettings().length;
				 i++) {

				sb.append(
					String.valueOf(
						objectValidationRule.getObjectValidationRuleSettings()
							[i]));

				if ((i + 1) < objectValidationRule.
						getObjectValidationRuleSettings().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (objectValidationRule.getOutputType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"outputType\": ");

			sb.append("\"");

			sb.append(objectValidationRule.getOutputType());

			sb.append("\"");
		}

		if (objectValidationRule.getScript() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"script\": ");

			sb.append("\"");

			sb.append(_escape(objectValidationRule.getScript()));

			sb.append("\"");
		}

		if (objectValidationRule.getSystem() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"system\": ");

			sb.append(objectValidationRule.getSystem());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		ObjectValidationRuleJSONParser objectValidationRuleJSONParser =
			new ObjectValidationRuleJSONParser();

		return objectValidationRuleJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		ObjectValidationRule objectValidationRule) {

		if (objectValidationRule == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (objectValidationRule.getActions() == null) {
			map.put("actions", null);
		}
		else {
			map.put(
				"actions", String.valueOf(objectValidationRule.getActions()));
		}

		if (objectValidationRule.getActive() == null) {
			map.put("active", null);
		}
		else {
			map.put("active", String.valueOf(objectValidationRule.getActive()));
		}

		if (objectValidationRule.getDateCreated() == null) {
			map.put("dateCreated", null);
		}
		else {
			map.put(
				"dateCreated",
				liferayToJSONDateFormat.format(
					objectValidationRule.getDateCreated()));
		}

		if (objectValidationRule.getDateModified() == null) {
			map.put("dateModified", null);
		}
		else {
			map.put(
				"dateModified",
				liferayToJSONDateFormat.format(
					objectValidationRule.getDateModified()));
		}

		if (objectValidationRule.getEngine() == null) {
			map.put("engine", null);
		}
		else {
			map.put("engine", String.valueOf(objectValidationRule.getEngine()));
		}

		if (objectValidationRule.getEngineLabel() == null) {
			map.put("engineLabel", null);
		}
		else {
			map.put(
				"engineLabel",
				String.valueOf(objectValidationRule.getEngineLabel()));
		}

		if (objectValidationRule.getErrorLabel() == null) {
			map.put("errorLabel", null);
		}
		else {
			map.put(
				"errorLabel",
				String.valueOf(objectValidationRule.getErrorLabel()));
		}

		if (objectValidationRule.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(
					objectValidationRule.getExternalReferenceCode()));
		}

		if (objectValidationRule.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(objectValidationRule.getId()));
		}

		if (objectValidationRule.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(objectValidationRule.getName()));
		}

		if (objectValidationRule.getObjectDefinitionExternalReferenceCode() ==
				null) {

			map.put("objectDefinitionExternalReferenceCode", null);
		}
		else {
			map.put(
				"objectDefinitionExternalReferenceCode",
				String.valueOf(
					objectValidationRule.
						getObjectDefinitionExternalReferenceCode()));
		}

		if (objectValidationRule.getObjectDefinitionId() == null) {
			map.put("objectDefinitionId", null);
		}
		else {
			map.put(
				"objectDefinitionId",
				String.valueOf(objectValidationRule.getObjectDefinitionId()));
		}

		if (objectValidationRule.getObjectValidationRuleSettings() == null) {
			map.put("objectValidationRuleSettings", null);
		}
		else {
			map.put(
				"objectValidationRuleSettings",
				String.valueOf(
					objectValidationRule.getObjectValidationRuleSettings()));
		}

		if (objectValidationRule.getOutputType() == null) {
			map.put("outputType", null);
		}
		else {
			map.put(
				"outputType",
				String.valueOf(objectValidationRule.getOutputType()));
		}

		if (objectValidationRule.getScript() == null) {
			map.put("script", null);
		}
		else {
			map.put("script", String.valueOf(objectValidationRule.getScript()));
		}

		if (objectValidationRule.getSystem() == null) {
			map.put("system", null);
		}
		else {
			map.put("system", String.valueOf(objectValidationRule.getSystem()));
		}

		return map;
	}

	public static class ObjectValidationRuleJSONParser
		extends BaseJSONParser<ObjectValidationRule> {

		@Override
		protected ObjectValidationRule createDTO() {
			return new ObjectValidationRule();
		}

		@Override
		protected ObjectValidationRule[] createDTOArray(int size) {
			return new ObjectValidationRule[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "actions")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "active")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "dateCreated")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "dateModified")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "engine")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "engineLabel")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "errorLabel")) {
				return true;
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return true;
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"objectDefinitionExternalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "objectDefinitionId")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "objectValidationRuleSettings")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "outputType")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "script")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "system")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			ObjectValidationRule objectValidationRule,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "actions")) {
				if (jsonParserFieldValue != null) {
					objectValidationRule.setActions(
						(Map<String, Map<String, String>>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "active")) {
				if (jsonParserFieldValue != null) {
					objectValidationRule.setActive(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateCreated")) {
				if (jsonParserFieldValue != null) {
					objectValidationRule.setDateCreated(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateModified")) {
				if (jsonParserFieldValue != null) {
					objectValidationRule.setDateModified(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "engine")) {
				if (jsonParserFieldValue != null) {
					objectValidationRule.setEngine(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "engineLabel")) {
				if (jsonParserFieldValue != null) {
					objectValidationRule.setEngineLabel(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "errorLabel")) {
				if (jsonParserFieldValue != null) {
					objectValidationRule.setErrorLabel(
						(Map<String, String>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					objectValidationRule.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					objectValidationRule.setId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					objectValidationRule.setName(
						(Map<String, String>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"objectDefinitionExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					objectValidationRule.
						setObjectDefinitionExternalReferenceCode(
							(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "objectDefinitionId")) {

				if (jsonParserFieldValue != null) {
					objectValidationRule.setObjectDefinitionId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "objectValidationRuleSettings")) {

				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					ObjectValidationRuleSetting[]
						objectValidationRuleSettingsArray =
							new ObjectValidationRuleSetting
								[jsonParserFieldValues.length];

					for (int i = 0;
						 i < objectValidationRuleSettingsArray.length; i++) {

						objectValidationRuleSettingsArray[i] =
							ObjectValidationRuleSettingSerDes.toDTO(
								(String)jsonParserFieldValues[i]);
					}

					objectValidationRule.setObjectValidationRuleSettings(
						objectValidationRuleSettingsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "outputType")) {
				if (jsonParserFieldValue != null) {
					objectValidationRule.setOutputType(
						ObjectValidationRule.OutputType.create(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "script")) {
				if (jsonParserFieldValue != null) {
					objectValidationRule.setScript(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "system")) {
				if (jsonParserFieldValue != null) {
					objectValidationRule.setSystem(
						(Boolean)jsonParserFieldValue);
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