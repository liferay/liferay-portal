/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.serdes.v1_0;

import com.liferay.headless.admin.site.client.dto.v1_0.GridViewportDefinition;
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
public class GridViewportDefinitionSerDes {

	public static GridViewportDefinition toDTO(String json) {
		GridViewportDefinitionJSONParser gridViewportDefinitionJSONParser =
			new GridViewportDefinitionJSONParser();

		return gridViewportDefinitionJSONParser.parseToDTO(json);
	}

	public static GridViewportDefinition[] toDTOs(String json) {
		GridViewportDefinitionJSONParser gridViewportDefinitionJSONParser =
			new GridViewportDefinitionJSONParser();

		return gridViewportDefinitionJSONParser.parseToDTOs(json);
	}

	public static String toJSON(GridViewportDefinition gridViewportDefinition) {
		if (gridViewportDefinition == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (gridViewportDefinition.getModulesPerRow() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"modulesPerRow\": ");

			sb.append(gridViewportDefinition.getModulesPerRow());
		}

		if (gridViewportDefinition.getVerticalAlignment() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"verticalAlignment\": ");

			sb.append("\"");
			sb.append(gridViewportDefinition.getVerticalAlignment());
			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		GridViewportDefinitionJSONParser gridViewportDefinitionJSONParser =
			new GridViewportDefinitionJSONParser();

		return gridViewportDefinitionJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		GridViewportDefinition gridViewportDefinition) {

		if (gridViewportDefinition == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (gridViewportDefinition.getModulesPerRow() == null) {
			map.put("modulesPerRow", null);
		}
		else {
			map.put(
				"modulesPerRow",
				String.valueOf(gridViewportDefinition.getModulesPerRow()));
		}

		if (gridViewportDefinition.getVerticalAlignment() == null) {
			map.put("verticalAlignment", null);
		}
		else {
			map.put(
				"verticalAlignment",
				String.valueOf(gridViewportDefinition.getVerticalAlignment()));
		}

		return map;
	}

	public static class GridViewportDefinitionJSONParser
		extends BaseJSONParser<GridViewportDefinition> {

		@Override
		protected GridViewportDefinition createDTO() {
			return new GridViewportDefinition();
		}

		@Override
		protected GridViewportDefinition[] createDTOArray(int size) {
			return new GridViewportDefinition[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "modulesPerRow")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "verticalAlignment")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			GridViewportDefinition gridViewportDefinition,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "modulesPerRow")) {
				if (jsonParserFieldValue != null) {
					gridViewportDefinition.setModulesPerRow(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "verticalAlignment")) {
				if (jsonParserFieldValue != null) {
					gridViewportDefinition.setVerticalAlignment(
						GridViewportDefinition.VerticalAlignment.create(
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