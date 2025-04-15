/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.rest.builder.test.client.serdes.v1_0;

import com.liferay.portal.tools.rest.builder.test.client.dto.v1_0.JSONMapAttributeTestEntity;
import com.liferay.portal.tools.rest.builder.test.client.json.BaseJSONParser;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import jakarta.annotation.Generated;

/**
 * @author Alejandro Tardín
 * @generated
 */
@Generated("")
public class JSONMapAttributeTestEntitySerDes {

	public static JSONMapAttributeTestEntity toDTO(String json) {
		JSONMapAttributeTestEntityJSONParser
			jsonMapAttributeTestEntityJSONParser =
				new JSONMapAttributeTestEntityJSONParser();

		return jsonMapAttributeTestEntityJSONParser.parseToDTO(json);
	}

	public static JSONMapAttributeTestEntity[] toDTOs(String json) {
		JSONMapAttributeTestEntityJSONParser
			jsonMapAttributeTestEntityJSONParser =
				new JSONMapAttributeTestEntityJSONParser();

		return jsonMapAttributeTestEntityJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		JSONMapAttributeTestEntity jsonMapAttributeTestEntity) {

		if (jsonMapAttributeTestEntity == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (jsonMapAttributeTestEntity.getDescription() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"description\": ");

			sb.append("\"");

			sb.append(_escape(jsonMapAttributeTestEntity.getDescription()));

			sb.append("\"");
		}

		if (jsonMapAttributeTestEntity.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(jsonMapAttributeTestEntity.getName()));

			sb.append("\"");
		}

		if (jsonMapAttributeTestEntity.getProperties1() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"properties1\": ");

			sb.append(_toJSON(jsonMapAttributeTestEntity.getProperties1()));
		}

		if (jsonMapAttributeTestEntity.getProperties2() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"properties2\": ");

			sb.append(_toJSON(jsonMapAttributeTestEntity.getProperties2()));
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		JSONMapAttributeTestEntityJSONParser
			jsonMapAttributeTestEntityJSONParser =
				new JSONMapAttributeTestEntityJSONParser();

		return jsonMapAttributeTestEntityJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		JSONMapAttributeTestEntity jsonMapAttributeTestEntity) {

		if (jsonMapAttributeTestEntity == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (jsonMapAttributeTestEntity.getDescription() == null) {
			map.put("description", null);
		}
		else {
			map.put(
				"description",
				String.valueOf(jsonMapAttributeTestEntity.getDescription()));
		}

		if (jsonMapAttributeTestEntity.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put(
				"name", String.valueOf(jsonMapAttributeTestEntity.getName()));
		}

		if (jsonMapAttributeTestEntity.getProperties1() == null) {
			map.put("properties1", null);
		}
		else {
			map.put(
				"properties1",
				String.valueOf(jsonMapAttributeTestEntity.getProperties1()));
		}

		if (jsonMapAttributeTestEntity.getProperties2() == null) {
			map.put("properties2", null);
		}
		else {
			map.put(
				"properties2",
				String.valueOf(jsonMapAttributeTestEntity.getProperties2()));
		}

		return map;
	}

	public static class JSONMapAttributeTestEntityJSONParser
		extends BaseJSONParser<JSONMapAttributeTestEntity> {

		@Override
		protected JSONMapAttributeTestEntity createDTO() {
			return new JSONMapAttributeTestEntity();
		}

		@Override
		protected JSONMapAttributeTestEntity[] createDTOArray(int size) {
			return new JSONMapAttributeTestEntity[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "description")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "properties1")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "properties2")) {
				return true;
			}

			return false;
		}

		@Override
		protected void setField(
			JSONMapAttributeTestEntity jsonMapAttributeTestEntity,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "description")) {
				if (jsonParserFieldValue != null) {
					jsonMapAttributeTestEntity.setDescription(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					jsonMapAttributeTestEntity.setName(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "properties1")) {
				if (jsonParserFieldValue != null) {
					jsonMapAttributeTestEntity.setProperties1(
						(Map<String, Object>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "properties2")) {
				if (jsonParserFieldValue != null) {
					jsonMapAttributeTestEntity.setProperties2(
						(Map<String, Object>)jsonParserFieldValue);
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