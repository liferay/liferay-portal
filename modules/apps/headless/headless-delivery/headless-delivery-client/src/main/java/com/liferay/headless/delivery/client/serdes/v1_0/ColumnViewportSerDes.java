/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.client.serdes.v1_0;

import com.liferay.headless.delivery.client.dto.v1_0.ColumnViewport;
import com.liferay.headless.delivery.client.json.BaseJSONParser;

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
public class ColumnViewportSerDes {

	public static ColumnViewport toDTO(String json) {
		ColumnViewportJSONParser columnViewportJSONParser =
			new ColumnViewportJSONParser();

		return columnViewportJSONParser.parseToDTO(json);
	}

	public static ColumnViewport[] toDTOs(String json) {
		ColumnViewportJSONParser columnViewportJSONParser =
			new ColumnViewportJSONParser();

		return columnViewportJSONParser.parseToDTOs(json);
	}

	public static String toJSON(ColumnViewport columnViewport) {
		if (columnViewport == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (columnViewport.getColumnViewportDefinition() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"columnViewportDefinition\": ");

			sb.append(
				String.valueOf(columnViewport.getColumnViewportDefinition()));
		}

		if (columnViewport.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append("\"");

			sb.append(_escape(columnViewport.getId()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		ColumnViewportJSONParser columnViewportJSONParser =
			new ColumnViewportJSONParser();

		return columnViewportJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(ColumnViewport columnViewport) {
		if (columnViewport == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (columnViewport.getColumnViewportDefinition() == null) {
			map.put("columnViewportDefinition", null);
		}
		else {
			map.put(
				"columnViewportDefinition",
				String.valueOf(columnViewport.getColumnViewportDefinition()));
		}

		if (columnViewport.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(columnViewport.getId()));
		}

		return map;
	}

	public static class ColumnViewportJSONParser
		extends BaseJSONParser<ColumnViewport> {

		@Override
		protected ColumnViewport createDTO() {
			return new ColumnViewport();
		}

		@Override
		protected ColumnViewport[] createDTOArray(int size) {
			return new ColumnViewport[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(
					jsonParserFieldName, "columnViewportDefinition")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			ColumnViewport columnViewport, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(
					jsonParserFieldName, "columnViewportDefinition")) {

				if (jsonParserFieldValue != null) {
					columnViewport.setColumnViewportDefinition(
						ColumnViewportDefinitionSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					columnViewport.setId((String)jsonParserFieldValue);
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