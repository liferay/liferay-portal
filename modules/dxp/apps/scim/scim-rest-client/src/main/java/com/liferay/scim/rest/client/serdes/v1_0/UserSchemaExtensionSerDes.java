/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.scim.rest.client.serdes.v1_0;

import com.liferay.scim.rest.client.dto.v1_0.UserSchemaExtension;
import com.liferay.scim.rest.client.json.BaseJSONParser;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import jakarta.annotation.Generated;

/**
 * @author Olivér Kecskeméty
 * @generated
 */
@Generated("")
public class UserSchemaExtensionSerDes {

	public static UserSchemaExtension toDTO(String json) {
		UserSchemaExtensionJSONParser userSchemaExtensionJSONParser =
			new UserSchemaExtensionJSONParser();

		return userSchemaExtensionJSONParser.parseToDTO(json);
	}

	public static UserSchemaExtension[] toDTOs(String json) {
		UserSchemaExtensionJSONParser userSchemaExtensionJSONParser =
			new UserSchemaExtensionJSONParser();

		return userSchemaExtensionJSONParser.parseToDTOs(json);
	}

	public static String toJSON(UserSchemaExtension userSchemaExtension) {
		if (userSchemaExtension == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (userSchemaExtension.getBirthday() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"birthday\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					userSchemaExtension.getBirthday()));

			sb.append("\"");
		}

		if (userSchemaExtension.getMale() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"male\": ");

			sb.append(userSchemaExtension.getMale());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		UserSchemaExtensionJSONParser userSchemaExtensionJSONParser =
			new UserSchemaExtensionJSONParser();

		return userSchemaExtensionJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		UserSchemaExtension userSchemaExtension) {

		if (userSchemaExtension == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (userSchemaExtension.getBirthday() == null) {
			map.put("birthday", null);
		}
		else {
			map.put(
				"birthday",
				liferayToJSONDateFormat.format(
					userSchemaExtension.getBirthday()));
		}

		if (userSchemaExtension.getMale() == null) {
			map.put("male", null);
		}
		else {
			map.put("male", String.valueOf(userSchemaExtension.getMale()));
		}

		return map;
	}

	public static class UserSchemaExtensionJSONParser
		extends BaseJSONParser<UserSchemaExtension> {

		@Override
		protected UserSchemaExtension createDTO() {
			return new UserSchemaExtension();
		}

		@Override
		protected UserSchemaExtension[] createDTOArray(int size) {
			return new UserSchemaExtension[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "birthday")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "male")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			UserSchemaExtension userSchemaExtension, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "birthday")) {
				if (jsonParserFieldValue != null) {
					userSchemaExtension.setBirthday(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "male")) {
				if (jsonParserFieldValue != null) {
					userSchemaExtension.setMale((Boolean)jsonParserFieldValue);
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