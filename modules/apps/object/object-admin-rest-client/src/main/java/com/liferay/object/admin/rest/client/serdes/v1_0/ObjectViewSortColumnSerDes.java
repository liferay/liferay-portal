/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.admin.rest.client.serdes.v1_0;

import com.liferay.object.admin.rest.client.dto.v1_0.ObjectViewSortColumn;
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
public class ObjectViewSortColumnSerDes {

	public static ObjectViewSortColumn toDTO(String json) {
		ObjectViewSortColumnJSONParser objectViewSortColumnJSONParser =
			new ObjectViewSortColumnJSONParser();

		return objectViewSortColumnJSONParser.parseToDTO(json);
	}

	public static ObjectViewSortColumn[] toDTOs(String json) {
		ObjectViewSortColumnJSONParser objectViewSortColumnJSONParser =
			new ObjectViewSortColumnJSONParser();

		return objectViewSortColumnJSONParser.parseToDTOs(json);
	}

	public static String toJSON(ObjectViewSortColumn objectViewSortColumn) {
		if (objectViewSortColumn == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (objectViewSortColumn.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(objectViewSortColumn.getId());
		}

		if (objectViewSortColumn.getObjectFieldName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"objectFieldName\": ");

			sb.append("\"");

			sb.append(_escape(objectViewSortColumn.getObjectFieldName()));

			sb.append("\"");
		}

		if (objectViewSortColumn.getPriority() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"priority\": ");

			sb.append(objectViewSortColumn.getPriority());
		}

		if (objectViewSortColumn.getSortOrder() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"sortOrder\": ");

			sb.append("\"");

			sb.append(objectViewSortColumn.getSortOrder());

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		ObjectViewSortColumnJSONParser objectViewSortColumnJSONParser =
			new ObjectViewSortColumnJSONParser();

		return objectViewSortColumnJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		ObjectViewSortColumn objectViewSortColumn) {

		if (objectViewSortColumn == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (objectViewSortColumn.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(objectViewSortColumn.getId()));
		}

		if (objectViewSortColumn.getObjectFieldName() == null) {
			map.put("objectFieldName", null);
		}
		else {
			map.put(
				"objectFieldName",
				String.valueOf(objectViewSortColumn.getObjectFieldName()));
		}

		if (objectViewSortColumn.getPriority() == null) {
			map.put("priority", null);
		}
		else {
			map.put(
				"priority", String.valueOf(objectViewSortColumn.getPriority()));
		}

		if (objectViewSortColumn.getSortOrder() == null) {
			map.put("sortOrder", null);
		}
		else {
			map.put(
				"sortOrder",
				String.valueOf(objectViewSortColumn.getSortOrder()));
		}

		return map;
	}

	public static class ObjectViewSortColumnJSONParser
		extends BaseJSONParser<ObjectViewSortColumn> {

		@Override
		protected ObjectViewSortColumn createDTO() {
			return new ObjectViewSortColumn();
		}

		@Override
		protected ObjectViewSortColumn[] createDTOArray(int size) {
			return new ObjectViewSortColumn[size];
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
			else if (Objects.equals(jsonParserFieldName, "sortOrder")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			ObjectViewSortColumn objectViewSortColumn,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					objectViewSortColumn.setId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "objectFieldName")) {
				if (jsonParserFieldValue != null) {
					objectViewSortColumn.setObjectFieldName(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "priority")) {
				if (jsonParserFieldValue != null) {
					objectViewSortColumn.setPriority(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "sortOrder")) {
				if (jsonParserFieldValue != null) {
					objectViewSortColumn.setSortOrder(
						ObjectViewSortColumn.SortOrder.create(
							(String)jsonParserFieldValue));
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