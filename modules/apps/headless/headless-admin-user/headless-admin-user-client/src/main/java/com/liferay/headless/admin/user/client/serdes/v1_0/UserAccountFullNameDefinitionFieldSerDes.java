/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.user.client.serdes.v1_0;

import com.liferay.headless.admin.user.client.dto.v1_0.UserAccountFullNameDefinitionField;
import com.liferay.headless.admin.user.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

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
public class UserAccountFullNameDefinitionFieldSerDes {

	public static UserAccountFullNameDefinitionField toDTO(String json) {
		UserAccountFullNameDefinitionFieldJSONParser
			userAccountFullNameDefinitionFieldJSONParser =
				new UserAccountFullNameDefinitionFieldJSONParser();

		return userAccountFullNameDefinitionFieldJSONParser.parseToDTO(json);
	}

	public static UserAccountFullNameDefinitionField[] toDTOs(String json) {
		UserAccountFullNameDefinitionFieldJSONParser
			userAccountFullNameDefinitionFieldJSONParser =
				new UserAccountFullNameDefinitionFieldJSONParser();

		return userAccountFullNameDefinitionFieldJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		UserAccountFullNameDefinitionField userAccountFullNameDefinitionField) {

		if (userAccountFullNameDefinitionField == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (userAccountFullNameDefinitionField.getKey() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"key\": ");

			sb.append("\"");

			sb.append(_escape(userAccountFullNameDefinitionField.getKey()));

			sb.append("\"");
		}

		if (userAccountFullNameDefinitionField.getRequired() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"required\": ");

			sb.append(userAccountFullNameDefinitionField.getRequired());
		}

		if (userAccountFullNameDefinitionField.getValues() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"values\": ");

			sb.append("[");

			for (int i = 0;
				 i < userAccountFullNameDefinitionField.getValues().length;
				 i++) {

				sb.append(
					_toJSON(userAccountFullNameDefinitionField.getValues()[i]));

				if ((i + 1) <
						userAccountFullNameDefinitionField.getValues().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		UserAccountFullNameDefinitionFieldJSONParser
			userAccountFullNameDefinitionFieldJSONParser =
				new UserAccountFullNameDefinitionFieldJSONParser();

		return userAccountFullNameDefinitionFieldJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		UserAccountFullNameDefinitionField userAccountFullNameDefinitionField) {

		if (userAccountFullNameDefinitionField == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (userAccountFullNameDefinitionField.getKey() == null) {
			map.put("key", null);
		}
		else {
			map.put(
				"key",
				String.valueOf(userAccountFullNameDefinitionField.getKey()));
		}

		if (userAccountFullNameDefinitionField.getRequired() == null) {
			map.put("required", null);
		}
		else {
			map.put(
				"required",
				String.valueOf(
					userAccountFullNameDefinitionField.getRequired()));
		}

		if (userAccountFullNameDefinitionField.getValues() == null) {
			map.put("values", null);
		}
		else {
			map.put(
				"values",
				String.valueOf(userAccountFullNameDefinitionField.getValues()));
		}

		return map;
	}

	public static class UserAccountFullNameDefinitionFieldJSONParser
		extends BaseJSONParser<UserAccountFullNameDefinitionField> {

		@Override
		protected UserAccountFullNameDefinitionField createDTO() {
			return new UserAccountFullNameDefinitionField();
		}

		@Override
		protected UserAccountFullNameDefinitionField[] createDTOArray(
			int size) {

			return new UserAccountFullNameDefinitionField[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "key")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "required")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "values")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			UserAccountFullNameDefinitionField
				userAccountFullNameDefinitionField,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "key")) {
				if (jsonParserFieldValue != null) {
					userAccountFullNameDefinitionField.setKey(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "required")) {
				if (jsonParserFieldValue != null) {
					userAccountFullNameDefinitionField.setRequired(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "values")) {
				if (jsonParserFieldValue != null) {
					userAccountFullNameDefinitionField.setValues(
						toStrings((Object[])jsonParserFieldValue));
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