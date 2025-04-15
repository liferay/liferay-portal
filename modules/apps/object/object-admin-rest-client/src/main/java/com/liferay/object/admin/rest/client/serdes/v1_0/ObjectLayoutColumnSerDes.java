/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.admin.rest.client.serdes.v1_0;

import com.liferay.object.admin.rest.client.dto.v1_0.ObjectLayoutColumn;
import com.liferay.object.admin.rest.client.json.BaseJSONParser;

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
public class ObjectLayoutColumnSerDes {

	public static ObjectLayoutColumn toDTO(String json) {
		ObjectLayoutColumnJSONParser objectLayoutColumnJSONParser =
			new ObjectLayoutColumnJSONParser();

		return objectLayoutColumnJSONParser.parseToDTO(json);
	}

	public static ObjectLayoutColumn[] toDTOs(String json) {
		ObjectLayoutColumnJSONParser objectLayoutColumnJSONParser =
			new ObjectLayoutColumnJSONParser();

		return objectLayoutColumnJSONParser.parseToDTOs(json);
	}

	public static String toJSON(ObjectLayoutColumn objectLayoutColumn) {
		if (objectLayoutColumn == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (objectLayoutColumn.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(objectLayoutColumn.getId());
		}

		if (objectLayoutColumn.getObjectFieldName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"objectFieldName\": ");

			sb.append("\"");

			sb.append(_escape(objectLayoutColumn.getObjectFieldName()));

			sb.append("\"");
		}

		if (objectLayoutColumn.getPriority() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"priority\": ");

			sb.append(objectLayoutColumn.getPriority());
		}

		if (objectLayoutColumn.getSize() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"size\": ");

			sb.append(objectLayoutColumn.getSize());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		ObjectLayoutColumnJSONParser objectLayoutColumnJSONParser =
			new ObjectLayoutColumnJSONParser();

		return objectLayoutColumnJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		ObjectLayoutColumn objectLayoutColumn) {

		if (objectLayoutColumn == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (objectLayoutColumn.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(objectLayoutColumn.getId()));
		}

		if (objectLayoutColumn.getObjectFieldName() == null) {
			map.put("objectFieldName", null);
		}
		else {
			map.put(
				"objectFieldName",
				String.valueOf(objectLayoutColumn.getObjectFieldName()));
		}

		if (objectLayoutColumn.getPriority() == null) {
			map.put("priority", null);
		}
		else {
			map.put(
				"priority", String.valueOf(objectLayoutColumn.getPriority()));
		}

		if (objectLayoutColumn.getSize() == null) {
			map.put("size", null);
		}
		else {
			map.put("size", String.valueOf(objectLayoutColumn.getSize()));
		}

		return map;
	}

	public static class ObjectLayoutColumnJSONParser
		extends BaseJSONParser<ObjectLayoutColumn> {

		@Override
		protected ObjectLayoutColumn createDTO() {
			return new ObjectLayoutColumn();
		}

		@Override
		protected ObjectLayoutColumn[] createDTOArray(int size) {
			return new ObjectLayoutColumn[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "objectFieldName")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "priority")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "size")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			ObjectLayoutColumn objectLayoutColumn, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					objectLayoutColumn.setId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "objectFieldName")) {
				if (jsonParserFieldValue != null) {
					objectLayoutColumn.setObjectFieldName(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "priority")) {
				if (jsonParserFieldValue != null) {
					objectLayoutColumn.setPriority(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "size")) {
				if (jsonParserFieldValue != null) {
					objectLayoutColumn.setSize(
						Integer.valueOf((String)jsonParserFieldValue));
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