/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.admin.rest.client.serdes.v1_0;

import com.liferay.object.admin.rest.client.dto.v1_0.ObjectValidationRuleSetting;
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
public class ObjectValidationRuleSettingSerDes {

	public static ObjectValidationRuleSetting toDTO(String json) {
		ObjectValidationRuleSettingJSONParser
			objectValidationRuleSettingJSONParser =
				new ObjectValidationRuleSettingJSONParser();

		return objectValidationRuleSettingJSONParser.parseToDTO(json);
	}

	public static ObjectValidationRuleSetting[] toDTOs(String json) {
		ObjectValidationRuleSettingJSONParser
			objectValidationRuleSettingJSONParser =
				new ObjectValidationRuleSettingJSONParser();

		return objectValidationRuleSettingJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		ObjectValidationRuleSetting objectValidationRuleSetting) {

		if (objectValidationRuleSetting == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (objectValidationRuleSetting.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(objectValidationRuleSetting.getName()));

			sb.append("\"");
		}

		if (objectValidationRuleSetting.getValue() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"value\": ");

			if (objectValidationRuleSetting.getValue() instanceof String) {
				sb.append("\"");
				sb.append((String)objectValidationRuleSetting.getValue());
				sb.append("\"");
			}
			else {
				sb.append(objectValidationRuleSetting.getValue());
			}
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		ObjectValidationRuleSettingJSONParser
			objectValidationRuleSettingJSONParser =
				new ObjectValidationRuleSettingJSONParser();

		return objectValidationRuleSettingJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		ObjectValidationRuleSetting objectValidationRuleSetting) {

		if (objectValidationRuleSetting == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (objectValidationRuleSetting.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put(
				"name", String.valueOf(objectValidationRuleSetting.getName()));
		}

		if (objectValidationRuleSetting.getValue() == null) {
			map.put("value", null);
		}
		else {
			map.put(
				"value",
				String.valueOf(objectValidationRuleSetting.getValue()));
		}

		return map;
	}

	public static class ObjectValidationRuleSettingJSONParser
		extends BaseJSONParser<ObjectValidationRuleSetting> {

		@Override
		protected ObjectValidationRuleSetting createDTO() {
			return new ObjectValidationRuleSetting();
		}

		@Override
		protected ObjectValidationRuleSetting[] createDTOArray(int size) {
			return new ObjectValidationRuleSetting[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "name")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "value")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			ObjectValidationRuleSetting objectValidationRuleSetting,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					objectValidationRuleSetting.setName(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "value")) {
				if (jsonParserFieldValue != null) {
					objectValidationRuleSetting.setValue(
						(Object)jsonParserFieldValue);
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