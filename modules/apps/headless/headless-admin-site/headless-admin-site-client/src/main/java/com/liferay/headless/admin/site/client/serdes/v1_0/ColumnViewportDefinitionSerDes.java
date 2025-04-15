/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.serdes.v1_0;

import com.liferay.headless.admin.site.client.dto.v1_0.ColumnViewportDefinition;
import com.liferay.headless.admin.site.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
public class ColumnViewportDefinitionSerDes {

	public static ColumnViewportDefinition toDTO(String json) {
		ColumnViewportDefinitionJSONParser columnViewportDefinitionJSONParser =
			new ColumnViewportDefinitionJSONParser();

		return columnViewportDefinitionJSONParser.parseToDTO(json);
	}

	public static ColumnViewportDefinition[] toDTOs(String json) {
		ColumnViewportDefinitionJSONParser columnViewportDefinitionJSONParser =
			new ColumnViewportDefinitionJSONParser();

		return columnViewportDefinitionJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		ColumnViewportDefinition columnViewportDefinition) {

		if (columnViewportDefinition == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (columnViewportDefinition.getSize() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"size\": ");

			sb.append(columnViewportDefinition.getSize());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		ColumnViewportDefinitionJSONParser columnViewportDefinitionJSONParser =
			new ColumnViewportDefinitionJSONParser();

		return columnViewportDefinitionJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		ColumnViewportDefinition columnViewportDefinition) {

		if (columnViewportDefinition == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (columnViewportDefinition.getSize() == null) {
			map.put("size", null);
		}
		else {
			map.put("size", String.valueOf(columnViewportDefinition.getSize()));
		}

		return map;
	}

	public static class ColumnViewportDefinitionJSONParser
		extends BaseJSONParser<ColumnViewportDefinition> {

		@Override
		protected ColumnViewportDefinition createDTO() {
			return new ColumnViewportDefinition();
		}

		@Override
		protected ColumnViewportDefinition[] createDTOArray(int size) {
			return new ColumnViewportDefinition[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "size")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			ColumnViewportDefinition columnViewportDefinition,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "size")) {
				if (jsonParserFieldValue != null) {
					columnViewportDefinition.setSize(
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