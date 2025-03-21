/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.client.serdes.v1_0;

import com.liferay.headless.delivery.client.dto.v1_0.RowViewport;
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
public class RowViewportSerDes {

	public static RowViewport toDTO(String json) {
		RowViewportJSONParser rowViewportJSONParser =
			new RowViewportJSONParser();

		return rowViewportJSONParser.parseToDTO(json);
	}

	public static RowViewport[] toDTOs(String json) {
		RowViewportJSONParser rowViewportJSONParser =
			new RowViewportJSONParser();

		return rowViewportJSONParser.parseToDTOs(json);
	}

	public static String toJSON(RowViewport rowViewport) {
		if (rowViewport == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (rowViewport.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append("\"");

			sb.append(_escape(rowViewport.getId()));

			sb.append("\"");
		}

		if (rowViewport.getRowViewportDefinition() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"rowViewportDefinition\": ");

			sb.append(String.valueOf(rowViewport.getRowViewportDefinition()));
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		RowViewportJSONParser rowViewportJSONParser =
			new RowViewportJSONParser();

		return rowViewportJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(RowViewport rowViewport) {
		if (rowViewport == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (rowViewport.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(rowViewport.getId()));
		}

		if (rowViewport.getRowViewportDefinition() == null) {
			map.put("rowViewportDefinition", null);
		}
		else {
			map.put(
				"rowViewportDefinition",
				String.valueOf(rowViewport.getRowViewportDefinition()));
		}

		return map;
	}

	public static class RowViewportJSONParser
		extends BaseJSONParser<RowViewport> {

		@Override
		protected RowViewport createDTO() {
			return new RowViewport();
		}

		@Override
		protected RowViewport[] createDTOArray(int size) {
			return new RowViewport[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "rowViewportDefinition")) {

				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			RowViewport rowViewport, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					rowViewport.setId((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "rowViewportDefinition")) {

				if (jsonParserFieldValue != null) {
					rowViewport.setRowViewportDefinition(
						RowViewportDefinitionSerDes.toDTO(
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