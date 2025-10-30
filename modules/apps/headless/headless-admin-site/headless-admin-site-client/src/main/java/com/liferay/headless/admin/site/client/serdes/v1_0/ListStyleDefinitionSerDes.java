/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.serdes.v1_0;

import com.liferay.headless.admin.site.client.dto.v1_0.ListStyleDefinition;
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
public class ListStyleDefinitionSerDes {

	public static ListStyleDefinition toDTO(String json) {
		ListStyleDefinitionJSONParser listStyleDefinitionJSONParser =
			new ListStyleDefinitionJSONParser();

		return listStyleDefinitionJSONParser.parseToDTO(json);
	}

	public static ListStyleDefinition[] toDTOs(String json) {
		ListStyleDefinitionJSONParser listStyleDefinitionJSONParser =
			new ListStyleDefinitionJSONParser();

		return listStyleDefinitionJSONParser.parseToDTOs(json);
	}

	public static String toJSON(ListStyleDefinition listStyleDefinition) {
		if (listStyleDefinition == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (listStyleDefinition.getAlign() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"align\": ");

			sb.append("\"");
			sb.append(listStyleDefinition.getAlign());
			sb.append("\"");
		}

		if (listStyleDefinition.getFlexWrap() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"flexWrap\": ");

			sb.append("\"");
			sb.append(listStyleDefinition.getFlexWrap());
			sb.append("\"");
		}

		if (listStyleDefinition.getGutters() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"gutters\": ");

			sb.append(listStyleDefinition.getGutters());
		}

		if (listStyleDefinition.getJustify() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"justify\": ");

			sb.append("\"");
			sb.append(listStyleDefinition.getJustify());
			sb.append("\"");
		}

		if (listStyleDefinition.getNumberOfColumns() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"numberOfColumns\": ");

			sb.append(listStyleDefinition.getNumberOfColumns());
		}

		if (listStyleDefinition.getVerticalAlignment() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"verticalAlignment\": ");

			sb.append("\"");
			sb.append(listStyleDefinition.getVerticalAlignment());
			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		ListStyleDefinitionJSONParser listStyleDefinitionJSONParser =
			new ListStyleDefinitionJSONParser();

		return listStyleDefinitionJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		ListStyleDefinition listStyleDefinition) {

		if (listStyleDefinition == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (listStyleDefinition.getAlign() == null) {
			map.put("align", null);
		}
		else {
			map.put("align", String.valueOf(listStyleDefinition.getAlign()));
		}

		if (listStyleDefinition.getFlexWrap() == null) {
			map.put("flexWrap", null);
		}
		else {
			map.put(
				"flexWrap", String.valueOf(listStyleDefinition.getFlexWrap()));
		}

		if (listStyleDefinition.getGutters() == null) {
			map.put("gutters", null);
		}
		else {
			map.put(
				"gutters", String.valueOf(listStyleDefinition.getGutters()));
		}

		if (listStyleDefinition.getJustify() == null) {
			map.put("justify", null);
		}
		else {
			map.put(
				"justify", String.valueOf(listStyleDefinition.getJustify()));
		}

		if (listStyleDefinition.getNumberOfColumns() == null) {
			map.put("numberOfColumns", null);
		}
		else {
			map.put(
				"numberOfColumns",
				String.valueOf(listStyleDefinition.getNumberOfColumns()));
		}

		if (listStyleDefinition.getVerticalAlignment() == null) {
			map.put("verticalAlignment", null);
		}
		else {
			map.put(
				"verticalAlignment",
				String.valueOf(listStyleDefinition.getVerticalAlignment()));
		}

		return map;
	}

	public static class ListStyleDefinitionJSONParser
		extends BaseJSONParser<ListStyleDefinition> {

		@Override
		protected ListStyleDefinition createDTO() {
			return new ListStyleDefinition();
		}

		@Override
		protected ListStyleDefinition[] createDTOArray(int size) {
			return new ListStyleDefinition[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "align")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "flexWrap")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "gutters")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "justify")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "numberOfColumns")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "verticalAlignment")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			ListStyleDefinition listStyleDefinition, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "align")) {
				if (jsonParserFieldValue != null) {
					listStyleDefinition.setAlign(
						ListStyleDefinition.Align.create(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "flexWrap")) {
				if (jsonParserFieldValue != null) {
					listStyleDefinition.setFlexWrap(
						ListStyleDefinition.FlexWrap.create(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "gutters")) {
				if (jsonParserFieldValue != null) {
					listStyleDefinition.setGutters(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "justify")) {
				if (jsonParserFieldValue != null) {
					listStyleDefinition.setJustify(
						ListStyleDefinition.Justify.create(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "numberOfColumns")) {
				if (jsonParserFieldValue != null) {
					listStyleDefinition.setNumberOfColumns(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "verticalAlignment")) {
				if (jsonParserFieldValue != null) {
					listStyleDefinition.setVerticalAlignment(
						ListStyleDefinition.VerticalAlignment.create(
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