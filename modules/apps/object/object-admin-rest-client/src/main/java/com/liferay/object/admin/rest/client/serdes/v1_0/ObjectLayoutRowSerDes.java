/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.admin.rest.client.serdes.v1_0;

import com.liferay.object.admin.rest.client.dto.v1_0.ObjectLayoutColumn;
import com.liferay.object.admin.rest.client.dto.v1_0.ObjectLayoutRow;
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
public class ObjectLayoutRowSerDes {

	public static ObjectLayoutRow toDTO(String json) {
		ObjectLayoutRowJSONParser objectLayoutRowJSONParser =
			new ObjectLayoutRowJSONParser();

		return objectLayoutRowJSONParser.parseToDTO(json);
	}

	public static ObjectLayoutRow[] toDTOs(String json) {
		ObjectLayoutRowJSONParser objectLayoutRowJSONParser =
			new ObjectLayoutRowJSONParser();

		return objectLayoutRowJSONParser.parseToDTOs(json);
	}

	public static String toJSON(ObjectLayoutRow objectLayoutRow) {
		if (objectLayoutRow == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (objectLayoutRow.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(objectLayoutRow.getId());
		}

		if (objectLayoutRow.getObjectLayoutColumns() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"objectLayoutColumns\": ");

			sb.append("[");

			for (int i = 0; i < objectLayoutRow.getObjectLayoutColumns().length;
				 i++) {

				sb.append(
					String.valueOf(
						objectLayoutRow.getObjectLayoutColumns()[i]));

				if ((i + 1) < objectLayoutRow.getObjectLayoutColumns().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (objectLayoutRow.getPriority() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"priority\": ");

			sb.append(objectLayoutRow.getPriority());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		ObjectLayoutRowJSONParser objectLayoutRowJSONParser =
			new ObjectLayoutRowJSONParser();

		return objectLayoutRowJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(ObjectLayoutRow objectLayoutRow) {
		if (objectLayoutRow == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (objectLayoutRow.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(objectLayoutRow.getId()));
		}

		if (objectLayoutRow.getObjectLayoutColumns() == null) {
			map.put("objectLayoutColumns", null);
		}
		else {
			map.put(
				"objectLayoutColumns",
				String.valueOf(objectLayoutRow.getObjectLayoutColumns()));
		}

		if (objectLayoutRow.getPriority() == null) {
			map.put("priority", null);
		}
		else {
			map.put("priority", String.valueOf(objectLayoutRow.getPriority()));
		}

		return map;
	}

	public static class ObjectLayoutRowJSONParser
		extends BaseJSONParser<ObjectLayoutRow> {

		@Override
		protected ObjectLayoutRow createDTO() {
			return new ObjectLayoutRow();
		}

		@Override
		protected ObjectLayoutRow[] createDTOArray(int size) {
			return new ObjectLayoutRow[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "objectLayoutColumns")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "priority")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			ObjectLayoutRow objectLayoutRow, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					objectLayoutRow.setId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "objectLayoutColumns")) {

				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					ObjectLayoutColumn[] objectLayoutColumnsArray =
						new ObjectLayoutColumn[jsonParserFieldValues.length];

					for (int i = 0; i < objectLayoutColumnsArray.length; i++) {
						objectLayoutColumnsArray[i] =
							ObjectLayoutColumnSerDes.toDTO(
								(String)jsonParserFieldValues[i]);
					}

					objectLayoutRow.setObjectLayoutColumns(
						objectLayoutColumnsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "priority")) {
				if (jsonParserFieldValue != null) {
					objectLayoutRow.setPriority(
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