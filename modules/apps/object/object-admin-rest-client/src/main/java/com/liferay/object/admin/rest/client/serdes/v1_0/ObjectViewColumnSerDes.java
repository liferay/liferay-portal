/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.admin.rest.client.serdes.v1_0;

import com.liferay.object.admin.rest.client.dto.v1_0.ObjectViewColumn;
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
public class ObjectViewColumnSerDes {

	public static ObjectViewColumn toDTO(String json) {
		ObjectViewColumnJSONParser objectViewColumnJSONParser =
			new ObjectViewColumnJSONParser();

		return objectViewColumnJSONParser.parseToDTO(json);
	}

	public static ObjectViewColumn[] toDTOs(String json) {
		ObjectViewColumnJSONParser objectViewColumnJSONParser =
			new ObjectViewColumnJSONParser();

		return objectViewColumnJSONParser.parseToDTOs(json);
	}

	public static String toJSON(ObjectViewColumn objectViewColumn) {
		if (objectViewColumn == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (objectViewColumn.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(objectViewColumn.getId());
		}

		if (objectViewColumn.getLabel() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"label\": ");

			sb.append(_toJSON(objectViewColumn.getLabel()));
		}

		if (objectViewColumn.getObjectFieldName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"objectFieldName\": ");

			sb.append("\"");

			sb.append(_escape(objectViewColumn.getObjectFieldName()));

			sb.append("\"");
		}

		if (objectViewColumn.getPriority() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"priority\": ");

			sb.append(objectViewColumn.getPriority());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		ObjectViewColumnJSONParser objectViewColumnJSONParser =
			new ObjectViewColumnJSONParser();

		return objectViewColumnJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(ObjectViewColumn objectViewColumn) {
		if (objectViewColumn == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (objectViewColumn.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(objectViewColumn.getId()));
		}

		if (objectViewColumn.getLabel() == null) {
			map.put("label", null);
		}
		else {
			map.put("label", String.valueOf(objectViewColumn.getLabel()));
		}

		if (objectViewColumn.getObjectFieldName() == null) {
			map.put("objectFieldName", null);
		}
		else {
			map.put(
				"objectFieldName",
				String.valueOf(objectViewColumn.getObjectFieldName()));
		}

		if (objectViewColumn.getPriority() == null) {
			map.put("priority", null);
		}
		else {
			map.put("priority", String.valueOf(objectViewColumn.getPriority()));
		}

		return map;
	}

	public static class ObjectViewColumnJSONParser
		extends BaseJSONParser<ObjectViewColumn> {

		@Override
		protected ObjectViewColumn createDTO() {
			return new ObjectViewColumn();
		}

		@Override
		protected ObjectViewColumn[] createDTOArray(int size) {
			return new ObjectViewColumn[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "label")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "objectFieldName")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "priority")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			ObjectViewColumn objectViewColumn, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					objectViewColumn.setId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "label")) {
				if (jsonParserFieldValue != null) {
					objectViewColumn.setLabel(
						(Map<String, String>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "objectFieldName")) {
				if (jsonParserFieldValue != null) {
					objectViewColumn.setObjectFieldName(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "priority")) {
				if (jsonParserFieldValue != null) {
					objectViewColumn.setPriority(
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