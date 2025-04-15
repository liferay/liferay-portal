/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.batch.planner.rest.client.serdes.v1_0;

import com.liferay.batch.planner.rest.client.dto.v1_0.Field;
import com.liferay.batch.planner.rest.client.json.BaseJSONParser;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import jakarta.annotation.Generated;

/**
 * @author Matija Petanjek
 * @generated
 */
@Generated("")
public class FieldSerDes {

	public static Field toDTO(String json) {
		FieldJSONParser fieldJSONParser = new FieldJSONParser();

		return fieldJSONParser.parseToDTO(json);
	}

	public static Field[] toDTOs(String json) {
		FieldJSONParser fieldJSONParser = new FieldJSONParser();

		return fieldJSONParser.parseToDTOs(json);
	}

	public static String toJSON(Field field) {
		if (field == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (field.getAnyOfGroup() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"anyOfGroup\": ");

			sb.append("\"");

			sb.append(_escape(field.getAnyOfGroup()));

			sb.append("\"");
		}

		if (field.getDescription() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"description\": ");

			sb.append("\"");

			sb.append(_escape(field.getDescription()));

			sb.append("\"");
		}

		if (field.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(field.getName()));

			sb.append("\"");
		}

		if (field.getRequired() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"required\": ");

			sb.append(field.getRequired());
		}

		if (field.getType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append("\"");

			sb.append(_escape(field.getType()));

			sb.append("\"");
		}

		if (field.getUnsupportedFormats() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"unsupportedFormats\": ");

			sb.append("[");

			for (int i = 0; i < field.getUnsupportedFormats().length; i++) {
				sb.append(_toJSON(field.getUnsupportedFormats()[i]));

				if ((i + 1) < field.getUnsupportedFormats().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		FieldJSONParser fieldJSONParser = new FieldJSONParser();

		return fieldJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(Field field) {
		if (field == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (field.getAnyOfGroup() == null) {
			map.put("anyOfGroup", null);
		}
		else {
			map.put("anyOfGroup", String.valueOf(field.getAnyOfGroup()));
		}

		if (field.getDescription() == null) {
			map.put("description", null);
		}
		else {
			map.put("description", String.valueOf(field.getDescription()));
		}

		if (field.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(field.getName()));
		}

		if (field.getRequired() == null) {
			map.put("required", null);
		}
		else {
			map.put("required", String.valueOf(field.getRequired()));
		}

		if (field.getType() == null) {
			map.put("type", null);
		}
		else {
			map.put("type", String.valueOf(field.getType()));
		}

		if (field.getUnsupportedFormats() == null) {
			map.put("unsupportedFormats", null);
		}
		else {
			map.put(
				"unsupportedFormats",
				String.valueOf(field.getUnsupportedFormats()));
		}

		return map;
	}

	public static class FieldJSONParser extends BaseJSONParser<Field> {

		@Override
		protected Field createDTO() {
			return new Field();
		}

		@Override
		protected Field[] createDTOArray(int size) {
			return new Field[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "anyOfGroup")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "description")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "required")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "unsupportedFormats")) {

				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			Field field, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "anyOfGroup")) {
				if (jsonParserFieldValue != null) {
					field.setAnyOfGroup((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "description")) {
				if (jsonParserFieldValue != null) {
					field.setDescription((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					field.setName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "required")) {
				if (jsonParserFieldValue != null) {
					field.setRequired((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				if (jsonParserFieldValue != null) {
					field.setType((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "unsupportedFormats")) {

				if (jsonParserFieldValue != null) {
					field.setUnsupportedFormats(
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