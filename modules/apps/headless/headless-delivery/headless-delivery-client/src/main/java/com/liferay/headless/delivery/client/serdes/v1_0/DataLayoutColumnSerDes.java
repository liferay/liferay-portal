/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.client.serdes.v1_0;

import com.liferay.headless.delivery.client.dto.v1_0.DataLayoutColumn;
import com.liferay.headless.delivery.client.json.BaseJSONParser;

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
public class DataLayoutColumnSerDes {

	public static DataLayoutColumn toDTO(String json) {
		DataLayoutColumnJSONParser dataLayoutColumnJSONParser =
			new DataLayoutColumnJSONParser();

		return dataLayoutColumnJSONParser.parseToDTO(json);
	}

	public static DataLayoutColumn[] toDTOs(String json) {
		DataLayoutColumnJSONParser dataLayoutColumnJSONParser =
			new DataLayoutColumnJSONParser();

		return dataLayoutColumnJSONParser.parseToDTOs(json);
	}

	public static String toJSON(DataLayoutColumn dataLayoutColumn) {
		if (dataLayoutColumn == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (dataLayoutColumn.getColumnSize() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"columnSize\": ");

			sb.append(dataLayoutColumn.getColumnSize());
		}

		if (dataLayoutColumn.getFieldNames() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fieldNames\": ");

			sb.append("[");

			for (int i = 0; i < dataLayoutColumn.getFieldNames().length; i++) {
				sb.append(_toJSON(dataLayoutColumn.getFieldNames()[i]));

				if ((i + 1) < dataLayoutColumn.getFieldNames().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		DataLayoutColumnJSONParser dataLayoutColumnJSONParser =
			new DataLayoutColumnJSONParser();

		return dataLayoutColumnJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(DataLayoutColumn dataLayoutColumn) {
		if (dataLayoutColumn == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (dataLayoutColumn.getColumnSize() == null) {
			map.put("columnSize", null);
		}
		else {
			map.put(
				"columnSize", String.valueOf(dataLayoutColumn.getColumnSize()));
		}

		if (dataLayoutColumn.getFieldNames() == null) {
			map.put("fieldNames", null);
		}
		else {
			map.put(
				"fieldNames", String.valueOf(dataLayoutColumn.getFieldNames()));
		}

		return map;
	}

	public static class DataLayoutColumnJSONParser
		extends BaseJSONParser<DataLayoutColumn> {

		@Override
		protected DataLayoutColumn createDTO() {
			return new DataLayoutColumn();
		}

		@Override
		protected DataLayoutColumn[] createDTOArray(int size) {
			return new DataLayoutColumn[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "columnSize")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "fieldNames")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			DataLayoutColumn dataLayoutColumn, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "columnSize")) {
				if (jsonParserFieldValue != null) {
					dataLayoutColumn.setColumnSize(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "fieldNames")) {
				if (jsonParserFieldValue != null) {
					dataLayoutColumn.setFieldNames(
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